package trawel.personal.people.behaviors;
import java.util.ArrayList;
import java.util.List;

import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.personal.people.Agent;
import trawel.personal.people.Behavior;
import trawel.time.TimeEvent;
import trawel.towns.contexts.Town;
import trawel.towns.data.Connection;

public class WanderEndless extends Behavior{
		
	private Connection connect;
	
	public WanderEndless(Connection c) {
		if (c == null) {
			connect = null;
			setTimeTo(24);//wait a bit longer before retrying
		}else {
			connect = c;
			setTimeTo(c.getTime()+(Rand.randFloat()*60));
		}
	}
	public WanderEndless() {
		connect = null;
		setTimeTo(Rand.randFloat()*5);//random awakeness after start to prevent grouping
	}

	@Override
	public List<TimeEvent> action(Agent user) {
		if (connect != null) {
			connect.otherTown(user.getLocation()).addOccupant(user);
		}	
		user.enqueueBehavior(new WanderEndless(destination(user)));
		return null;
	}
	
	public static Connection destination(Agent user) {
		Town current = user.getLocation();
		if (current == null) {
			if (mainGame.debug) {
				System.out.println("invalid location for wander endless on "+user.getPerson().getName());
			}
			return null;
		}
		if (current.hasConnectFlow() && Rand.chanceIn(1, 5)) {//1/5th chance to follow natural flow
			return current.getConnectFlow();
		}
		List<Connection> connects = new ArrayList<Connection>();
		current.getConnects().stream().filter(
			c -> 
			//if would be an interworld teleport
			!c.isWorldConnection()
			).forEach(connects::add);
		if (connects.size() == 0) {
			//System.err.println(user.getLocation().getName() + " has no valid connects");
			return null;
		}
		//dupe connects have their own system to reduce likelyhood, and also type plays a role
		float total = 0;
		//we add +.2 so there is a baseline chance of moving even if everywhere has it's needs filled
		for (Connection c: connects) {
			total += c.getAIWanderAppeal(current)+.2;
		}
		total *= Rand.randFloat();
		for (Connection c: connects) {
			total -= c.getAIWanderAppeal(current)+.2;
			if (total <= 0) {
				return c;
			}
		}
		//return last element to protect from rounding errors
		return connects.getLast();
	}

}
