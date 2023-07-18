package trawel.time;

import java.util.List;

public abstract class TContextOwner implements java.io.Serializable, CanPassTime, ReloadAble, HasTimeContext{

	protected transient TimeContext timeScope;
	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.GLOBAL,this);//subclasses will want to override this with their own contexts
		timeSetup();
	}
	
	/**
	 * unused but might be used later- call after you set up your timeScope in your reload function to futureproof
	 * call before you tell other things to reload
	 */
	public void timeSetup() {
		
	}
	
	@Override
	public List<TimeEvent> contextTime(double time, TimeContext calling) {
		return timeScope.call(calling, time).pop();
	}
	
	public List<TimeEvent> contextTime(double time, TimeContext calling, boolean ignorelazy) {
		return timeScope.call(calling, time,ignorelazy).pop();
	}
}
