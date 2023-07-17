package trawel.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TrawelTaskThreadFactory implements ThreadFactory{

	@Override
	public Thread newThread(Runnable run) {
		Thread t = Executors.defaultThreadFactory().newThread(run);
        t.setDaemon(true);
        //t.setPriority(Thread.NORM_PRIORITY-1);
		return t;
	}
	
}
