package trawel.towns.features.services;
import java.util.List;

import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.features.Feature;

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
		return TrawelColor.F_AUX_SERVICE;
	}
	
	@Override
	public void go() {
		int in = 0;
		while (in != 7) {
		Print.println("1 head");
		Print.println("2 arms");
		Print.println("3 chest");
		Print.println("4 legs");
		Print.println("5 feet");
		Print.println("6 weapon");
		Print.println("7 exit");
		in = Input.inInt(7);
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
