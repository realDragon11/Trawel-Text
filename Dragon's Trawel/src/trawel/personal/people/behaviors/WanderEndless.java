package trawel.personal.people.behaviors;
import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.people.Agent;
import trawel.personal.people.Behavior;
import trawel.time.TimeEvent;
import trawel.towns.Connection;
import trawel.towns.Town;
import trawel.towns.Connection.ConnectType;

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
			c.isWorldConnection()
			//and only 50% chance it not a road
			|| (c.getType() != ConnectType.ROAD && extra.chanceIn(1,2))
			).forEach(connects::add);
		if (connects.size() == 0) {
			return null;
		}
		//MAYBELATER: if there's two connections to the same location (ie port AND roads), it is twice as likely
		float total = 0;
		//we add +.2 so there is a baseline chance of moving even if everywhere has it's needs filled
		for (Connection c: connects) {
			total += c.otherTown(current).occupantNeed()+.2;
		}
		total *= extra.randFloat();
		for (Connection c: connects) {
			total -= c.otherTown(current).occupantNeed()+.2;
			if (total <= 0) {
				return c;
			}
		}
		throw new RuntimeException("ran of of weight for random wander " + user.getPerson().getName() + " in " + current.getName());
	}

}
