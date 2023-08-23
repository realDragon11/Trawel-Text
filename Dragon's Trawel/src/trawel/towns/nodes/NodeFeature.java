package trawel.towns.nodes;

import java.util.List;

import trawel.mainGame;
import trawel.personal.people.Player;
import trawel.quests.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public abstract class NodeFeature extends Feature {

	protected NodeConnector start;
	protected double findTime = 0;
	protected boolean spreadTime = false;
	
	public enum Shape{
		NONE, TOWER, ELEVATOR, RIGGED_DUNGEON;
	}
	protected Shape shape;
	
	public Shape getShape() {
		return shape;
	}
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (spreadTime) {
			start.spreadTime(time, calling);
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

	protected abstract void generate(int size);

	public void delayFind() {
		if (findTime > 3) {
			findTime = 3;
		}
		findTime -=1;
		
	}
	
	@Override
	public void reload() {
		super.reload();
		if (mainGame.debug) {
			System.out.println(start.getSize() +" size of " + this.getName());
		}
	}

	protected abstract byte bossType();

	public String sizeDesc() {
		return " S: " + start.getSize();
	}
	
	@Override
	public String getTitle() {
		return getName() + sizeDesc();
	}

}
