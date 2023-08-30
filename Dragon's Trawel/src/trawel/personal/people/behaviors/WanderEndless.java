package trawel.personal.people.behaviors;
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
			user.setLocation(connect.otherTown(user.getLocation()));
		}
		user.enqueueBehavior(new WanderEndless(destination(user)));
		return null;
	}
	
	public static Connection destination(Agent user) {
		Town current = user.getLocation();
		if (current.hasConnectFlow() && extra.chanceIn(2, 5)) {
			return current.getConnectFlow();
		}
		List<Connection> connects = current.getConnects();
		Connection c = connects.get(extra.randRange(0,connects.size()-1));
		Town other = c.otherTown(current);
		if (
				//if would be an interworld teleport
				other.getIsland().getWorld() != current.getIsland().getWorld()
				//and only 50% chance it not a road
				|| (c.getType() != ConnectType.ROAD && extra.chanceIn(1,2))) {
			return null;
		}
		return c;
	}

}
