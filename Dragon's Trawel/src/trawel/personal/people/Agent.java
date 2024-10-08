package trawel.personal.people;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import derg.ds.Chomp;
import trawel.core.Rand;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.classless.Skill;
import trawel.personal.item.Potion;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.behaviors.AbandonPostBehavior;
import trawel.personal.people.behaviors.WanderEndless;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.features.services.WitchHut;

/**
 * @author dragon
 * like Player, but for the ai. can be programmed with behaviors
 */
public class Agent extends SuperPerson{

	private static final long serialVersionUID = 1L;

	private Person person;
	
	//might include currencies from other worlds, and a home world
	
	private List<Behavior> behaviors;
	private Behavior current;
	
	private Set<AgentGoal> goals;
	
	private byte flags = 0b0;
	
	public enum AgentGoal {
		NONE, DEATHCHEAT, SPOOKY, OWN_SOMETHING, DELVE_HELP, 
		/**
		 * can only be assigned at char gen, dictates that the player can randomly encounter and kill them,
		 * at which point they lose this goal and enter the normal system (if the deathcheat)
		 */
		WORLD_ENCOUNTER
	}
	
	public enum AgentFlag{
		DEATHCHEATED_EVER
	}
	
	public Agent(Person p) {
		person = p;
		p.setSuper(this);
		behaviors = new ArrayList<Behavior>();
		current = new WanderEndless();
		//goals = EnumSet.of(AgentGoal.NONE);
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
		onlyGoal(AgentGoal.NONE);
		p.skillTriggers();
	}
	
	public Agent(Person p, AgentGoal goal) {
		person = p;
		p.setSuper(this);
		behaviors = new ArrayList<Behavior>();
		current = null;
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
		onlyGoal(goal);
		p.skillTriggers();
	}

	@Override
	public Person getPerson() {
		return person;
	}
	
	public void setFlag(AgentFlag flag, boolean bool) {
		flags = Chomp.setEnumByteFlag(flag.ordinal(), flags, bool);
	}
	
	public boolean getFlag(AgentFlag flag) {
		return Chomp.getEnumByteFlag(flag.ordinal(), flags);
	}

	public List<Behavior> getBehaviors() {
		return behaviors;
	}

	public void setBehaviors(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}
	
	public Behavior popBehave() {
		if (behaviors == null || behaviors.size() == 0) {
			return null;
		}
		Behavior b = behaviors.get(0);
		behaviors.remove(0);
		return b;
	}
	
	public Behavior getCurrent() {
		return current;
	}

	@Override
	public List<TimeEvent> passTime(double d, TimeContext calling) {
		if (current == null) {
			if (behaviors.isEmpty()) {
				return null;
			}else {
				current = popBehave();
			}
		}
		if (current.getTimeTo() == Double.NaN) {
			return null;//special value that means it can't advance
		}
		//will need to look at connections and features
		//a* pathing?
		List<TimeEvent> list = null;
		while(current.getTimeTo() < d) {
			d-=current.getTimeTo();
			List<TimeEvent> subList = current.action(this);
			if (subList != null) {
				if (list == null) {
					list = new ArrayList<TimeEvent>();
				}
				list.addAll(subList);
			}
			current = popBehave();
			if (current == null) {
				return list;
			}
			if (current.getTimeTo() == Double.NaN) {
				//new behavior can't pass time, return what we have thus far, it can return null on the next call
				return list;
			}
		}
		current.passTime(d, calling);
		return list;
	}

	public void enqueueBehavior(Behavior b) {
		behaviors.add(b);
	}

	@Override
	public void setGoal(AgentGoal goal) {
		if (goal == AgentGoal.DEATHCHEAT) {
			setFlag(AgentFlag.DEATHCHEATED_EVER,true);
		}
		goals.add(goal);
	}

	@Override
	public void onlyGoal(AgentGoal goal) {
		behaviors.clear();
		current = null;
		switch (goal) {
		case DEATHCHEAT:
			setFlag(AgentFlag.DEATHCHEATED_EVER,true);
			break;
		case OWN_SOMETHING://if we ONLY own something we should stop moving around
			break;
		case NONE:
			current = new WanderEndless();
			break;
		case DELVE_HELP:
			current = new AbandonPostBehavior();
			//one week and then potentially 2 more days
			current.setTimeTo((24*7)+(Rand.randFloat()*48));
			break;
		}
		goals = EnumSet.of(goal);
	}

	@Override
	public boolean removeGoal(AgentGoal goal) {
		return goals.remove(goal);
	}
	
	@Override
	public boolean hasGoal(AgentGoal goal) {
		if (goal == AgentGoal.NONE) {
			if (goals.size() > 1) {
				return false;
			}
			//fall through
		}
		return goals.contains(goal);
	}
	
	public boolean wantsRefill() {
		if (hasFlask()) {
			if (knowsPotion()) {//if they don't know what's in it, they always want a refill
				switch (peekFlask()) {
				case CURSE:
				case BEES:
						return false;
				}
			}
			return true;
		}
		return false;
	}

	public void refillWithPrice(int cost) {
		do {
			addFlaskUses((byte)3);
			buyMoneyAmount(cost);
		} while (getFlaskUses() < 6 && canBuyMoneyAmount(cost));
	}

	@Override
	public boolean everDeathCheated() {
		return getFlag(AgentFlag.DEATHCHEATED_EVER);
	}

	/**
	 * returns true if this did anything
	 */
	public boolean setActionTimeMin(double time) {
		if (current == null) {
			return false;
		}
		if (current.getTimeTo() == Double.NaN) {
			return false;
		}
		if (current.getTimeTo() < time) {
			current.setTimeTo(time);
			return true;
		}
		return false;
	}

	public boolean isCurrentBehaviorClass(Class<? extends Behavior> clazz) {
		return current != null && clazz.isInstance(current);
	}
	
	/**
	 * if setting location to a town, use addOccupant instead
	 * <br>
	 * this will remove any occupancy when used
	 */
	@Override
	public void setLocation(Town location) {
		Town old = getLocation();
		if (old != null && old != location) {
			old.removeOccupant(this);
		}
		super.setLocation(location);
	}

	public void skipCurrent() {
		current = null;
	}
	
}
