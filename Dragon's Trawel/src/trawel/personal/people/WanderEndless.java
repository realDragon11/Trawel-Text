package trawel.personal.people;
import java.util.List;

import trawel.extra;
import trawel.towns.Connection;
import trawel.towns.Town;
import trawel.towns.Connection.ConnectType;

public class WanderEndless extends Behavior implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Connection connect;
	
	public WanderEndless(Connection c) {
		connect = c;
		setTimeTo(c.getTime()+(extra.randFloat()*60));
	}
	public WanderEndless() {
		connect = null;
		setTimeTo(extra.randFloat()*5);
	}

	@Override
	public void action(Agent user) {
		if (connect != null) {
			user.setLocation(connect.otherTown(user.getLocation()));
		}
		user.enqueueBehavior(new WanderEndless(destination(user)));
	}
	
	public static Connection destination(Agent user) {
		List<Connection> connects = user.getLocation().getConnects();
		Connection c = connects.get(extra.randRange(0,connects.size()-1));
		Town other = c.otherTown(user.getLocation());
		if (
				//if would be an interworld teleport
				other.getIsland().getWorld() != user.getLocation().getIsland().getWorld()
				//and only 50% chance it not a road
				|| (c.getType() != ConnectType.ROAD && extra.chanceIn(1,2))) {
			return null;
		}
		return c;
	}

}
