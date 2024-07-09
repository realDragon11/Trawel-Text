package trawel.arc.misc;

import trawel.core.Networking;
import trawel.core.mainGame;
import trawel.helper.methods.extra;
import trawel.personal.people.Player;

public class Deaths {

	public static void die(String deathMessage) {
		Networking.statAddUpload("deaths","total_deaths",1);
		//Networking.sendStrong("StatUp|deaths|1|");
		Networking.leaderboard("most_deaths",++Player.player.deaths);
		Player.player.getStory().onDeath();
		extra.println(deathMessage);
		Player.player.getStory().onDeathPart2();
		extra.endBackingSegment();
	}

	public static void dieMisc() {
		die(extra.choose("You rise from death...","You return to life.","You walk again!","You rise from the grave!","Death releases its hold on you."));
	}

	public static void dieFight() {
		if (extra.chanceIn(1,3)) {
			dieMisc();
			return;
		}
		die(extra.choose("You revive after the battle.","You right your repaired body.","You stand after your corpse pulls itself together."));
	}

}
