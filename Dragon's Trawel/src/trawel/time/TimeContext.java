package trawel.time;

import java.util.ArrayList;
import java.util.List;

public class TimeContext {

	public final ContextType owntype;
	private ContextType type;
	private List<TimeEvent> events = new ArrayList<TimeEvent>();
    //private final Lock lock = new ReentrantLock();
	public final CanPassTime scope;
	private TimeContext caller = null;//plane's will always be null
	
	public final boolean isLazy;
	private boolean didUpdate = false;
	private double trackedTime = 0;
	
	//TODO: for now, timecontext itself is not thread safe, but the rest of the program
	//is expected to only ever access it in a thread safe way
	//likely, this will be by only accessing global contexts from the main thread, and local contexts do not access each other
	//but are instead resolved by the global context they are in
	
	public TimeContext(ContextType type, CanPassTime scope) {
		this.owntype = type;
		this.scope = scope;
		isLazy = false;
	}
	
	public TimeContext(ContextType type, CanPassTime scope, boolean isLazy) {
		assert !(type == ContextType.UNBOUNDED && isLazy); 
		this.owntype = type;
		this.scope = scope;
		this.isLazy = isLazy;
		trackedTime=0;
		
	}
	
	/**
	 * calls with another forced type and lazyload
	 * most of the time you should use the call with only parent and calltime instead
	 * @param parent
	 * @param calltime
	 * @param type
	 * @param forced - ignore lazy updating
	 * @return
	 */
	public TimeContext call(TimeContext parent, double calltime,ContextType type, boolean forced) {
		this.type = type;
		caller = parent;
		
		double timeleft = calltime+trackedTime;
		if (timeleft != 0 && (!forced && isLazy && timeleft < type.time_span)) {
			//if we got forced with 0 but didn't have any timedebt, we can safely skip
			didUpdate = false;
			trackedTime+=calltime;
			return this;
		}
		trackedTime = 0;
		didUpdate = true;
		double time;
		while (timeleft > 0) {
			if (timeleft > type.time_span) {
				time = type.time_span;
				timeleft -=type.time_span;
			}else {
				time = timeleft;
				timeleft = 0;
			}
			addEvents(scope.passTime(time, this));
		}
		return this;
	}
	
	/**
	 * @param forced - ignore lazy updating
	 */
	public TimeContext call(TimeContext parent, double calltime,boolean forced) {
		return call(parent,calltime,owntype,forced);
	}
	
	public TimeContext call(TimeContext parent, double calltime) {
		return call(parent,calltime,owntype,false);
	}
	/**
	 * used internally to recursively pull together contexts, which then get processed, and unprocessed ones get passed on
	 * (sometimes they may get altered and then passed on as mere notifications)
	 * @param es
	 */
	private void addEvents(List<TimeEvent> es) {
		if (es == null) {
			return;
		}
		events.addAll(es);
	}
	
	/**
	 * call to process events without popping them
	 * @param caller 
	 */
	public void processEvents(HasTimeContext caller) {
		caller.consumeEvents(events);
	}

	/**
	 * used to add local events- for when a context contains recursive areas without their own contexts
	 * @param passTime
	 */
	public void localEvents(List<TimeEvent> es) {
		if (es == null) {
			return;
		}
		events.addAll(es);
	}
	
	/**
	 * give up all our events to another context
	 * @return
	 */
	public List<TimeEvent> pop(HasTimeContext caller){
		processEvents(caller);
		List<TimeEvent> ret = events;
		events = new ArrayList<TimeEvent>();
		return ret;
	}
	
	public TimeContext caller() {
		return caller;
	}
	
	/**
	 * will be false unless an actual update happened last time call was invoked.
	 * Typically caused by lazy contexts
	 * @return
	 */
	public boolean updated() {
		return didUpdate;
	}

	protected void load(double savedTime) {
		trackedTime = savedTime;
	}

	public double getDebt() {
		return trackedTime;
	}

	public void assumeDebt(double taken) {
		assert taken <= trackedTime;
		trackedTime-=taken;
	}

	public List<TimeEvent> forSaveEvents() {
		return events;
	}
}
