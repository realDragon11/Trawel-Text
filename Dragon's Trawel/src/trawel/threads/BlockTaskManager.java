package trawel.threads;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import trawel.extra;

public class BlockTaskManager extends ThreadPoolExecutor {
	
	//https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
	private boolean isPaused;
	/**
	 * Whenever pauseLock is obtained, make sure that, if you are also syncing on handler, you
	 * do not give up pause lock until you are done also syncing on handler
	 * ie, any time you sync on handler it should not rely on other locks (like pause lock)
	 * and, if it does, should hold that lock before it gets handler's sync lock
	 */
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();
	private static BlockTaskManager handler;
	
	public int lastNewTasks = 0;
	public AtomicInteger completedTasks = new AtomicInteger();//also counts canceled tasks
	
	public boolean hasWarned = false;

	//this class manages doing work while the main thread is blocking for input
	
	//goal 1: generate weapons that can be scaled up to different levels
	//the main game will pick which ones it wants and occasionally not use some, our
	//job is just to make sure there are enough in the pool for when it wants more
	//this will help smooth over small increases in activity: the main game
	//can always do it on it's own if need be
	
	//goal 2: catch up on other world's passtime
	//to avoid linear growth of computation time, other world context's beside the current one do not emulate
	//until the player steps back in them. This will 'catch up' if the total time elapsed is greater than the time we have on record for them
	//each world is still single threaded as a whole
	//this might need careful integration with other goals
	
	/**
	 * all tasks given to BlockTaskManager should not particularly care if they don't get run for quite some time
	 * each start -> halt phase might not clear undone tasks, so tasks should partly figure out if they still need
	 * to do the work
	 * 
	 * this is because of the pause mechanism done by halt, in order to gradually stop
	 * 
	 * tasks should also be very brief to avoid noticeable lag
	 */
	public BlockTaskManager() {
		super(2, 24, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new TrawelTaskThreadFactory());
		
		handler = this;
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
	    try {
	    	//while (isPaused) unpaused.await();
	    	if (isPaused) {
	    		completedTasks.incrementAndGet();
	    		synchronized (handler){
	    			handler.notifyAll();
	    		}
	    		throw new InterruptedException();//TODO
	    	}
	    } catch (InterruptedException ie) {
	    	t.interrupt();
	    } finally {
	    	pauseLock.unlock();
	    }
	}

	@Override
	protected void afterExecute(Runnable r,Throwable t) {
		super.afterExecute(r,t);
		
		boolean newTask = false;
		pauseLock.lock();
		try {
			
			if (!isPaused) {
				if (t == null && r instanceof Future<?>) {
					try {
						Object result = ((Future<?>) r).get();
						if (result instanceof FollowUp) {
							Runnable next = ((FollowUp)result).nextTask();
							if (next != null) {
								handler.execute(r);
								newTask = true;
							}
						}
					} catch (CancellationException ce) {
						t = ce;
					} catch (ExecutionException ee) {
						t = ee.getCause();
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt(); // ignore/reset
					}
				}
				if (t != null)
					t.printStackTrace();
			}
	    } finally {
	    	if (!newTask) {
	    		completedTasks.incrementAndGet();
	    		synchronized (handler){
	    			handler.notifyAll();
	    		}
	    	}
	    	pauseLock.unlock();
	    }
	}
	
	public static void setup() {
		if (trawel.mainGame.permaNoThreads) {
			return;
		}
		trawel.mainGame.noThreads = false;
		
		handler = new BlockTaskManager();
		handler.prestartCoreThread();
	}
	
	/**
	 * start computing background tasks
	 * 
	 * should only ever be called on the main thread
	 */
	public static void start() {
		if (trawel.mainGame.noThreads) {
			return;
		}
		if (!trawel.mainGame.multiCanRun) {
			return;
		}
		
		//attempt to gain pause lock, if not, merely set that we have no new tasks
		//to avoid issues with rapid starting and halting
		try {
			if (handler.pauseLock.tryLock(20, TimeUnit.MILLISECONDS)) {
				synchronized (handler){
					handler.resume();
					//handler.notifyAll();//if any threads still exist we're screwed TODO
					handler.completedTasks.set(0);
					handler.lastNewTasks = trawel.WorldGen.plane.passiveTasks(handler);
					handler.pauseLock.unlock();
				}
			}else {
				synchronized (handler){
					//need to check if this causes issues, however it is monolock so probably not
					//still, might not be needed, or there might be a better way
					handler.lastNewTasks = 0;
					handler.completedTasks.set(0);
				}
			}
		} catch (InterruptedException e) {
			// unsure what to do here so just stack trace
			e.printStackTrace();
			throw new RuntimeException("Main thread couldn't get pause lock but was interrupted.");
		}
		
		
		//extra.println("new: " + handler.lastNewTasks);
	}
	
	public static final long HALT_TIMEOUT = 3_000;
	public static final long WARN_TIMEOUT = 300;//if we spend longer than 300ms
	
	/**
	 * note that tasks will never get canceled, they might have already taken timeDebt or some other resource
	 * from another place.
	 * However, expect not noticeable to humans delay on the average computer
	 * 
	 * should only ever be called on the main thread
	 */
	public static void halt() {
		if (trawel.mainGame.noThreads) {
			return;
		}
		if (!trawel.mainGame.multiCanRun) {
			return;
		}
		handler.pause();
		synchronized (handler){
			if (handler.lastNewTasks == 0) {
				return;//might bug out, TODO
			}
		try {
		
			long curtime = System.currentTimeMillis();
			long expiretime = System.currentTimeMillis()+HALT_TIMEOUT;
			while (handler.lastNewTasks > handler.completedTasks.get() && curtime < expiretime) {
				handler.wait(expiretime-curtime);//should never be 0
				curtime = System.currentTimeMillis();
			}
			if (handler.lastNewTasks > handler.completedTasks.get()) {
				extra.println("Task Mismatch: " + handler.lastNewTasks +" new; " + handler.completedTasks.get() + "done");
			}
			long timeSpan = (curtime-expiretime);//this is the time left before HALT_TIMEOUT, but negative;
			if (timeSpan > 0) {//if we timed out
				extra.println("Threads took >3 seconds to complete and timed out- you may encounter broken behavior and should treat this as an error. You can disable threads with the '-nothreads' argument if you keep encountering this.");
			}else {
				if (HALT_TIMEOUT+timeSpan > WARN_TIMEOUT) {//timeSpan is negative, for example 3000+(1000-3000) > 500 for 1 second going over .5 seconds of warn
					if (!handler.hasWarned) {
						handler.hasWarned = true;
						extra.println("Threadstop took "+(HALT_TIMEOUT+timeSpan)+ " milliseconds. You are experiencing at least minor multithreading issues. You can disable threads with the '-nothreads' argument if this is causing lag- this warning will only display once per game.");
					}
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();//should never happen because this should only be called from the main thread
		}
		}
		
	}
	
	private void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
		} finally {
			pauseLock.unlock();
		}
	}

	private void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}
}
