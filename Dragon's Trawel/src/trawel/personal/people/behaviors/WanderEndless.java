package trawel.personal.people.behaviors;
import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.people.Agent;
import trawel.personal.people.Behavior;
import trawel.time.TimeEvent;
import trawel.towns.Connection;
import trawel.towns.Town;

public class WanderEndless extends Behavior{
		
	private Connection connect;
	
	public WanderEndless(Connection c) {
		if (c == null) {
			connect = null;
			setTimeTo(24);//wait a bit longer before retrying
		}else {
			connect = c;
			setTimeTo(c.getTime()+(extra.randFloat()*60));
		}
	}
	public WanderEndless() {
		connect = null;
		setTimeTo(extra.randFloat()*5);//random awakeness after start to prevent grouping
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
		if (current.hasConnectFlow() && extra.chanceIn(1, 5)) {//1/5th chance to follow natural flow
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
		total *= extra.randFloat();
		for (Connection c: connects) {
			total -= c.getAIWanderAppeal(current)+.2;
			if (total <= 0) {
				return c;
			}
		}
		throw new RuntimeException("ran of of weight for random wander " + user.getPerson().getName() + " in " + current.getName());
	}

}
