package trawel.time;

import java.util.List;

public interface HasTimeContext {
	public List<TimeEvent> contextTime(double time, TimeContext calling);
}
