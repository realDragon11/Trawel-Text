package trawel.towns.services;
import java.awt.Color;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Appraiser extends Feature {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Appraiser(String name) {
		this.name = name;
		tutorialText = "Appraisers will tell you more about your items.";
		color = Color.BLUE;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|appraiser|Appraiser|");
		Networking.setArea("shop");
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
		// TODO Auto-generated method stub
		return null;
	}

}
