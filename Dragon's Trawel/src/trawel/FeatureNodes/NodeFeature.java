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

public abstract class NodeFeature extends Feature {

	protected NodeConnector start;
	protected Town town;
	protected int size;
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		start.passTime(time, calling);
		start.timeFinish();
		return timeScope.pop();
	}

	protected abstract void generate();

}
