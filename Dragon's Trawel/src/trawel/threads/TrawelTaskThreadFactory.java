package trawel.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TrawelTaskThreadFactory implements ThreadFactory{

	//private ThreadGroup group = new ThreadGroup("trawelTask");
	private int threadCounter = 0;
	
	@Override
	public Thread newThread(Runnable run) {
		//Thread t = new PooledThread(group,null,group.getName()+"_"+ ++threadCounter);
		Thread t = Executors.defaultThreadFactory().newThread(run);
		t.setName("trawelTask"+"_"+ ++threadCounter);
        t.setDaemon(true);
        //t.setPriority(Thread.NORM_PRIORITY-1);
		return t;
	}
	
}
