package trawel.time;

import java.util.ArrayList;
import java.util.List;

public abstract class TContextOwner implements java.io.Serializable, CanPassTime, ReloadAble, HasTimeContext{

	protected transient TimeContext timeScope;
	protected double savedTime = 0;
	
	protected List<TimeEvent> storedTimeEvents = new ArrayList<TimeEvent>();
	
	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.GLOBAL,this);//subclasses will want to override this with their own contexts
		timeSetup();
	}
	
	/**
	 * call after you set up your timeScope in your reload function
	 * call before you tell other things to reload
	 */
	public void timeSetup() {
		timeScope.load(savedTime);
	}
	
	public void prepareSave() {
		savedTime = timeScope.getDebt();
		storedTimeEvents = timeScope.forSaveEvents();
	}
	
	@Override
	public List<TimeEvent> contextTime(double time, TimeContext calling) {
		/*if (timeScope == null) {
			System.out.println(this.toString());
		}*/
		return timeScope.call(calling, time).pop(this);
	}
	
	public List<TimeEvent> contextTime(double time, TimeContext calling, boolean ignorelazy) {
		return timeScope.call(calling, time,ignorelazy).pop(this);
	}
	public List<TimeEvent> contextTime(double time, TimeContext calling, ContextType type, boolean ignorelazy) {
		return timeScope.call(calling, time,type, ignorelazy).pop(this);
	}
}
