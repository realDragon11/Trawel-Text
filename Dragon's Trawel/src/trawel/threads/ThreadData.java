package trawel.threads;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import trawel.personal.DummyPerson;
import trawel.personal.people.Player;
import trawel.towns.contexts.World;
import trawel.towns.data.WorldGen;

public final class ThreadData {
	public World world;
	
	//static methods from extra
	private static ReentrantLock mainThreadLock = new ReentrantLock();
	private static final ThreadLocal<ThreadData> threadLocalData = new ThreadLocal<ThreadData>() {
		@Override protected ThreadData initialValue() {
			return new ThreadData();
		}
	};
	private static final ThreadLocal<List<DummyPerson>> localDumInvs = new ThreadLocal<List<DummyPerson>>() {
		@Override protected List<DummyPerson> initialValue() {
			return WorldGen.initDummyInvs();
		}
	};

	public static final List<DummyPerson> getDumInvs() {
		return localDumInvs.get();
	}

	public static final boolean isMainThread() {
		return mainThreadLock.isHeldByCurrentThread();
	}

	public static final void setMainThread() {
		System.out.print("booting");
		mainThreadLock.lock();
		System.out.println("...");
	}

	/**
	 * since each thread will only ever be dealing with one world at a time
	 * this method lets you store that world to be accessed later
	 * 
	 * this is true because we made the assumption that threads will never trip over each other
	 * for the purposes of not needing to give everything in the game locks
	 * @return a container
	 */
	public static final ThreadData getThreadData() {
		//https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html
		return threadLocalData.get();
	}

	/**
	 * should be called after you update one of the following, in the main thread:
	 * <br>
	 * 1. the player's world
	 * @return getThreadData()
	 */
	public static final ThreadData mainThreadDataUpdate() {
		if (!isMainThread()) {
			throw new RuntimeException("trying to main update a non main thread");
		}
		ThreadData temp = getThreadData();
		temp.world = Player.player.getWorld();
		return temp;
	}
}