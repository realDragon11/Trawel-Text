package trawel;
import java.util.ArrayList;

public class WanderEndless extends Behavior implements java.io.Serializable{

	@Override
	public void action(Agent user) {
		ArrayList<Connection> connects = user.getLocation().getConnects();
		Boolean bool = false;
		Connection c;
		int i = 0;
		do {
		c = connects.get(extra.randRange(0,connects.size()-1));
		i++;
		if (i > 3) {
			Behavior b = new WanderEndless();
			b.setTimeTo(extra.randRange(6,200));
			user.enqueueBehavior(b);
			return;
		}
		if (c.getType().equals("teleport") || (c.getType().equals("ship") && extra.chanceIn(1,2))) {
			bool = true;
		}else {
			bool = false;
		}
		}while(bool);
		user.setLocation(c.otherTown(user.getLocation()));
		Behavior b = new WanderEndless();
		b.setTimeTo(extra.randRange(6,200));
		user.enqueueBehavior(b);
	}

}
