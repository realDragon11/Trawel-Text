package trawel.threads;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

public class BlockTaskManager extends ThreadPoolExecutor {
	
	//https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
	private boolean isPaused;
	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();
	private static BlockTaskManager handler;

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
	
	public BlockTaskManager() {
		super(4, 24, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new TrawelTaskThreadFactory());
	}
	
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
	    try {
	    	while (isPaused) unpaused.await();
	    } catch (InterruptedException ie) {
	    	t.interrupt();
	    } finally {
	    	pauseLock.unlock();
	    }
	}
	
	public static void setup() {
		handler = new BlockTaskManager();
	}
	
	public static void start() {
		handler.resume();
	}
	
	public static void halt() {
		handler.pause();
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
