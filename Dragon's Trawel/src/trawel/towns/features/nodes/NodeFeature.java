package trawel.towns.features.nodes;

import java.util.List;

import trawel.mainGame;
import trawel.personal.Person;
import trawel.personal.people.Player;
import trawel.quests.types.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.features.nodes.BossNode.BossType;

public abstract class NodeFeature extends Feature {

	protected NodeConnector start;
	protected double findTime = 0;
	//protected boolean spreadTime = false;
	
	public enum Shape{
		NONE, TOWER, ELEVATOR,
		/**
		 * requires a bosstype of 3 to work properly, and DungeonNode
		 */
		RIGGED_DUNGEON, RIGGED_TOWER,
		TREASURE_BEACH;
	}
	protected Shape shape;
	
	public Shape getShape() {
		return shape;
	}
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		//if (spreadTime) {
			start.spreadTime(time, calling);
		//}
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
			System.out.println(start.getSize() +" size; LvL "+start.lowestLevel+"-" +start.highestLevel+"; " + this.getName() + " ("+this.nameOfType()+") in "+getTown().getName());
		}
	}

	protected BossType bossType() {
		return BossType.NONE;
	}

	public String sizeDesc() {
		return " S: " + start.getSize() + (start.highestLevel != 0 ? " L: "+start.lowestLevel+"-"+start.highestLevel : "");
	}
	
	@Override
	public String getTitle() {
		return getName() + sizeDesc();
	}
	
	public List<Person> getHelpFighters(){
		throw new UnsupportedOperationException("This node feature " + getName() + " of " + this.getClass() + " does not have helpers!");
	}
	
	public void retainAliveFighters(List<Person> retain){
	
	}

}
