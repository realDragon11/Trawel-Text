package trawel;
import java.awt.Color;
import java.util.List;

import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class Doctor extends Feature {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Town town;

	public Doctor(String name,Town t) {
		this.name = name;
		town = t;
		color = Color.BLUE;
		tutorialText = "Doctors can cure your ailments.";
	}
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|doctor|Doctor|");
		int dcost = town.getTier()*5;
		int cost = 50*town.getTier()+(town.getTier()*Player.player.getPerson().effectsSize()*30);
		extra.println("gold: " +Player.bag.getGold());
		extra.println("1 diagnosis (" + dcost+" gold)");
		extra.println("2 cure (" + cost+" gold)");
		extra.println("3 exit");
		switch (extra.inInt(3)) {
		case 1:
			if (Player.bag.getGold() < dcost) {
				extra.println("Not enough gold!");break;
			}
			Player.bag.addGold(-dcost);
			Player.player.getPerson().displayEffects();
			break;
		case 2:
			if (Player.bag.getGold() < cost) {
				extra.println("Not enough gold!");break;
			}
			Player.bag.addGold(-cost);
			Player.player.getPerson().cureEffects();
			break;
		case 3: return;
		}
		go();
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {

	}

}
