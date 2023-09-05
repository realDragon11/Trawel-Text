package trawel.towns.services;
import java.util.List;

import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class Doctor extends Feature {

	private static final long serialVersionUID = 1L;

	private double timecounter;
	public Doctor(String name,Town t) {
		timecounter = extra.randRange(5,10);
		this.name = name;
		town = t;
		tier = t.getTier();
		area_type = Area.MISC_SERVICE;
	}
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}
	
	@Override
	public String getTutorialText() {
		return "Doctor.";
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|doctor|Doctor|");
		String mstr = World.currentMoneyString();
		int dcost = (int) (getUnEffectiveLevel());
		//includes effects that already wore off, which simulates a 'checkup' mechanic vaguely
		int effectGuess = extra.clamp(Player.player.getPerson().effectsSize(), 3, 6);
		int cost = 1+(int) (getUnEffectiveLevel()*(effectGuess*3));
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
		timecounter-=time;
		if (timecounter <= 0) {
			int price = (int) (2*getUnEffectiveLevel());
			//must have been afflicted by at least one effect since last doctor visit/creation
			town.getPersonableOccupants().filter(a -> a.getPerson().effectsSize() > 0 && a.canBuyMoneyAmount(price)).limit(3)
			.forEach(a -> a.getPerson().cureEffects());
			timecounter += extra.randRange(20,40);
		}
		return null;
	}

}
