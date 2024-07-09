package trawel.time;

import trawel.Networking;
import trawel.WorldGen;
import trawel.mainGame;
import trawel.helper.methods.extra;
import trawel.personal.people.Player;

public class TrawelTime {

	/**
	 * note that some events, like the player generating gold, ignore normal restrictions
	 */
	public static void globalPassTime() {
		if (mainGame.showLargeTimePassing && Networking.connected()) {
			boolean largeTimePassing = false;
			if (Player.peekTime() > 24) {
				largeTimePassing = true;
				extra.println("Passing "+extra.F_WHOLE.format(Player.peekTime())+" hours.");
			}
			double passAmount = 1;
			while (Player.peekTime() > 0) {
				TrawelTime.globalPassTimeUpTo(passAmount);
				if (largeTimePassing) {
					passAmount+=.01;
				}
				/*
				if (Player.player.atFeature != null) {
					Networking.updateTime();
					Player.player.atFeature.sendBackVariant();
				}else {
					Player.player.getLocation().sendBackVariant();
				}*/
				Networking.updateTime();
				Networking.waitIfConnected(84);//1 second should be around 6 hours
			}
		}else {
			if (Player.peekTime() > 0) {
				TrawelTime.globalTimeCatchUp();
				Networking.updateTime();
			}
		}
	
	}

	public static void globalTimeCatchUp() {
		double time = Player.popTime();
		WorldGen.plane.advanceTime(time);
	}

	public static void globalPassTimeUpTo(double limit) {
		double time = Player.takeTime(limit);
		if (time > 0) {
			WorldGen.plane.advanceTime(time);
		}
	}

}
