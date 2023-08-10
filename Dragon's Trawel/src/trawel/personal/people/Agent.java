package trawel.personal.people;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import trawel.extra;
import trawel.personal.Person;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.World;

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
		NONE, DEATHCHEAT, SPOOKY
	}
	
	public enum AgentFlag{
		DEATHCHEATED_EVER
	}
	
	public Agent(Person p) {
		person = p;
		p.setSuper(this);
		behaviors = new ArrayList<Behavior>();
		current = new WanderEndless();
		goals = EnumSet.of(AgentGoal.NONE);
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
	}
	
	public Agent(Person p, AgentGoal goal) {
		person = p;
		p.setSuper(this);
		goals = EnumSet.of(goal);
		current = null;
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
		if (goal == AgentGoal.DEATHCHEAT) {
			setFlag(AgentFlag.DEATHCHEATED_EVER,true);
		}
	}

	@Override
	public Person getPerson() {
		return person;
	}
	
	public void setFlag(AgentFlag flag, boolean bool) {
		flags = extra.setEnumByteFlag(flag.ordinal(), flags, bool);
	}
	
	public boolean getFlag(AgentFlag flag) {
		return extra.getEnumByteFlag(flag.ordinal(), flags);
	}

	public List<Behavior> getBehaviors() {
		return behaviors;
	}

	public void setBehaviors(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}
	
	public Behavior popBehave() {
		if (behaviors == null) {
			return null;
		}
		Behavior b = behaviors.get(0);
		behaviors.remove(0);
		return b;
	}

	@Override
	public List<TimeEvent> passTime(double d, TimeContext calling) {
		//will need to look at connections and features
		//a* pathing?
		while(current.getTimeTo() < d) {
			d-=current.getTimeTo();
			current.action(this);
			current = popBehave();
		}
			current.passTime(d, calling);
			return null;
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
		if (goal == AgentGoal.DEATHCHEAT) {
			setFlag(AgentFlag.DEATHCHEATED_EVER,true);
		}
		goals = EnumSet.of(goal);
	}

	@Override
	public boolean removeGoal(AgentGoal goal) {
		return goals.remove(goal);
	}
	
	@Override
	public boolean hasGoal(AgentGoal goal) {
		return goals.contains(goal);
	}
	
	public boolean wantsRefill() {
		if (hasFlask()) {
			switch (peekFlask()) {
			case CURSE:
			case BEES:
					return false;
			}
			return true;
		}
		return false;
	}

	public void refillWithPrice(int cost) {
		do {
			addFlaskUses((byte)3);
			buyMoneyAmount(cost);
		} while (getFlashUses() < 6 && canBuyMoneyAmount(cost));
	}

	@Override
	public boolean everDeathCheated() {
		return getFlag(AgentFlag.DEATHCHEATED_EVER);
	}
	
}
