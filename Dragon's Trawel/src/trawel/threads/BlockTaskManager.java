package trawel.threads;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

import trawel.extra;

public class BlockTaskManager extends ThreadPoolExecutor {
	
	//https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
	private boolean isPaused;
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();
	private static BlockTaskManager handler;
	
	public int lastNewTasks;
	public AtomicInteger completedTasks = new AtomicInteger();

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
		super(2, 24, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new TrawelTaskThreadFactory());
		
		handler = this;
	}
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
	    try {
	    	//while (isPaused) unpaused.await();
	    	if (isPaused) {
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
		
		completedTasks.incrementAndGet();
		
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
	
	public static void start() {
		if (trawel.mainGame.noThreads) {
			return;
		}
		
		synchronized (handler){
			handler.resume();
			//handler.notifyAll();//if any threads still exist we're screwed TODO
			handler.completedTasks.set(0);
			handler.lastNewTasks = trawel.WorldGen.plane.passiveTasks(handler);
		}
		extra.println("new: " + handler.lastNewTasks);
	}
	
	/**
	 * note that tasks will never get canceled, they might have already taken timeDebt or some other resource
	 * from another place.
	 * However, expect not noticeable to humans delay on the average computer
	 */
	public static void halt() {
		if (trawel.mainGame.noThreads) {
			return;
		}
		synchronized (handler){
			handler.pause();
		}
		if (handler.lastNewTasks == 0) {
			return;//might bug out, TODO TODO
		}
		try {
			long time1 = System.currentTimeMillis();//TODO might be able to remove if this works better than I expected

			
			int checks = 0;
			while (handler.lastNewTasks > handler.completedTasks.get() ) {//TODO might only need one of these//&& handler.getActiveCount() > 0
				if (checks >= 200) {
					extra.println("Threads took >3 second to complete- you may encounter broken behavior and should treat this as an error. You can disable threads with the '-nothreads' argument if you keep encountering this.");
					break;
				}
				Thread.sleep(15);
				checks++;
			}
			if (handler.lastNewTasks > handler.completedTasks.get()) {
				extra.println("Task Mismatch: " + handler.lastNewTasks +" new; " + handler.completedTasks.get() + "done");
			}
			
			/*if (handler.awaitTermination(10,TimeUnit.SECONDS)) {
				extra.println("Threads took >10 seconds to complete- you may encounter broken behavior and should treat this as an error. You can disable threads with the '-nothreads' argument if you keep encountering this.");
			}*/
			long timeSpan = (System.currentTimeMillis()-time1)/10;
			System.err.println("Threadstop 100ths: "+timeSpan);
			if (timeSpan > 90) {
				System.err.println("Threadstop took "+timeSpan+ " hundredth seconds. You are experiencing at least minor multithreading issues. You can disable threads with the '-nothreads' argument if you keep encountering this.");
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();//should never happen because this should only be called from the main thread
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
