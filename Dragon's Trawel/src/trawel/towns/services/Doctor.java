package trawel.towns.services;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class Doctor extends Feature {

	private static final long serialVersionUID = 1L;

	public Doctor(String name,Town t) {
		this.name = name;
		town = t;
		tier = t.getTier();
		tutorialText = "Doctors can cure your ailments.";
	}
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}
	
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|doctor|Doctor|");
		String mstr = World.currentMoneyString();
		int dcost = tier*5;
		int cost = 50*tier+(tier*Math.min(3,Player.player.getPerson().effectsSize())*30);
		extra.println(mstr+": " +Player.player.getGold());
		extra.println("1 diagnosis (" + dcost+" "+mstr+")");
		extra.println("2 cure (" + cost+" "+mstr+")");
		extra.println("3 exit");
		switch (extra.inInt(3)) {
		case 1:
			if (Player.player.getGold() < dcost) {
				extra.println("Not enough "+mstr+"!");break;
			}
			Player.player.addGold(-dcost);
			Player.player.getPerson().displayEffects();
			break;
		case 2:
			if (Player.player.getGold() < cost) {
				extra.println("Not enough "+mstr+"!");break;
			}
			Player.player.addGold(-cost);
			Player.player.getPerson().cureEffects();
			break;
		case 3: return;
		}
		go();
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		return null;
	}

}
