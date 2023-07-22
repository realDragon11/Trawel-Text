package trawel.time;

import java.util.List;

public interface HasTimeContext {
	public List<TimeEvent> contextTime(double time, TimeContext calling);
	
	public void consumeEvents(List<TimeEvent> list);
	
	public ContextLevel contextLevel();
}
