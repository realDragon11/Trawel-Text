package trawel.FeatureNodes;

import java.util.List;

import trawel.Feature;
import trawel.GroveNode;
import trawel.NodeConnector;
import trawel.Town;
import trawel.time.ContextType;
import trawel.time.ReloadAble;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public abstract class NodeFeature extends Feature implements ReloadAble {

	protected NodeConnector start;
	protected transient TimeContext timeScope;
	protected Town town;
	protected int size;
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timeScope.call(calling, time);
		start.timeFinish();
		return timeScope.pop();
	}

	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.UNBOUNDED,start);
	}

}
