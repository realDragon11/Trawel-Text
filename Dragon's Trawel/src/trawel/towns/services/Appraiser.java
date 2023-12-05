package trawel.towns.services;
import java.util.List;

import trawel.Networking.Area;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Appraiser extends Feature {

	private static final long serialVersionUID = 1L;

	public Appraiser(String name) {
		this.name = name;
		tutorialText = "Appraiser";
		//TODO: needs better overhaul
		area_type = Area.MISC_SERVICE;
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}
	
	@Override
	public void go() {
		int in = 0;
		while (in != 7) {
		extra.println("1 head");
		extra.println("2 arms");
		extra.println("3 chest");
		extra.println("4 legs");
		extra.println("5 feet");
		extra.println("6 weapon");
		extra.println("7 exit");
		in = extra.inInt(7);
		if (in < 6) {
			Player.bag.getArmorSlot(in-1).display(2);
		}else {
			if (in == 6) {
				Player.bag.getHand().display(2);
			}
		}
		}
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		return null;
	}

}
