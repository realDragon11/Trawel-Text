package trawel.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import trawel.extra;
import trawel.extra.ThreadData;
import trawel.towns.World;

public class FollowUp {

	//note that followups don't take place if the BlockTaskManager is paused still
	//also used to create tasks in the first place, if they can be repeated
	
	public final Object data;
	public final FollowType type;
	
	private final static FollowUp stable = new FollowUp(null,null);
	
	public FollowUp(Object data, FollowType type) {
		this.data = data;
		this.type = type;
	}
	
	public enum FollowType{
		WORLD_TIME,//indicates that data is a world and that you should queue another task on it
		;
	}

	public Runnable nextTask() {
		return createTask(data,type);
	}
	
	public static Runnable createTask(Object data, FollowType type) {
		switch (type) {
		case WORLD_TIME:
			return stable.new WorldTime((World)data);
		}
		throw new RuntimeException("was not able to create new followup task");
	}
	
	private class WorldTime implements RunnableFuture<FollowUp>{
		private FollowUp myResult = null;
		private boolean completed = false;
		private boolean canceled = false;
		private boolean running = false;
		private final World world;
		
		public WorldTime(World world) {
			this.world = world;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			canceled = true;
			return completed || running;
		}

		@Override
		public FollowUp get() throws InterruptedException, ExecutionException {
			return myResult;
		}

		@Override
		public FollowUp get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return myResult;
		}

		@Override
		public boolean isCancelled() {
			return canceled;
		}

		@Override
		public boolean isDone() {
			return completed;
		}

		@Override
		public void run() {
			running = true;
			ThreadData d = extra.getThreadData();
			d.world = world;
			double debt = world.assumeDebt(1);//will take on up to 1 hour of debt
			if (debt <= 0) {//0 = no debt or not enough to bother, -1 = lock failure
				completed = false;
				if (debt == 0) {
					world.freeLock();
				}
				return;
			}
			//actually do the debt
			world.contextTime(debt, null, true);//events go nowhere
			if (world.hasDebt()) {
				myResult = new FollowUp(world,FollowType.WORLD_TIME);
			}
			world.freeLock();
			completed = true;
		}
		
	}
}
