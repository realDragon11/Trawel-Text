package trawel.personal.people;

import java.util.List;

import trawel.time.CanPassTime;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public abstract class Behavior implements CanPassTime, java.io.Serializable{
	private double timeTo;

	public double getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(double timeTo) {
		this.timeTo = timeTo;
	}
	
	
	public abstract List<TimeEvent> action(Agent user);

	@Override
	public List<TimeEvent> passTime(double d, TimeContext calling) {
		timeTo-=d;
		return null;
	}

}
