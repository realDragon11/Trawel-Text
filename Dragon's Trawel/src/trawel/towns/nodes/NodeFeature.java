package trawel.towns.nodes;

import java.util.ArrayList;
import java.util.List;

import trawel.personal.people.Player;
import trawel.quests.Quest.TriggerType;
import trawel.time.ContextType;
import trawel.time.ReloadAble;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public abstract class NodeFeature extends Feature {

	protected NodeConnector start;
	protected int size;
	protected double findTime = 0;
	protected boolean spreadTime = false;
	
	//protected List<NodeType> typeList = new ArrayList<NodeType>();
	
	public enum Shape{
		NONE, TOWER, ELEVATOR;
	}
	protected Shape shape;
	
	public Shape getShape() {
		return shape;
	}
	
	public abstract NodeType numType(int i);
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (spreadTime) {
			start.spreadTime(time, calling);
			start.endPass();
		}
		findTime += time;
		return timeScope.pop(this);
	}
	
	public double getFindTime() {
		return findTime;
	}
	
	public void findCollect(String str, int amount) {
		Player.player.questTrigger(TriggerType.COLLECT, str, amount);
		findTime = 0;
	}

	protected abstract void generate();

	public void delayFind() {
		if (findTime > 3) {
			findTime = 3;
		}
		findTime -=1;
		
	}
	
	@Override
	public void reload() {
		super.reload();
		start.parentChain(this);
		start.endPass();
	}

}
