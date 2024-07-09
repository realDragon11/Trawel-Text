package trawel.arc.misc;

import java.util.ArrayList;
import java.util.List;

import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.people.Player;

public class Deaths {

	public static void die(String deathMessage) {
		Networking.statAddUpload("deaths","total_deaths",1);
		//Networking.sendStrong("StatUp|deaths|1|");
		Networking.leaderboard("most_deaths",++Player.player.deaths);
		Player.player.getStory().onDeath();
		Print.println(deathMessage);
		Player.player.getStory().onDeathPart2();
		Input.endBackingSegment();
	}

	public static void dieMisc() {
		die(Rand.choose("You rise from death...","You return to life.","You walk again!","You rise from the grave!","Death releases its hold on you."));
	}
	
	public static void dieFight() {
		deathPenalty();
		if (Rand.chanceIn(1,3)) {
			dieMisc();
			return;
		}
		die(Rand.choose("You revive after the battle.","You right your repaired body.","You stand after your corpse pulls itself together."));
	}
	
	/**
	 * mostly used for death fights, other deaths tend to have their own penalties
	 */
	public static void deathPenalty() {
		//don't set burnout, for now, to see if it causes players to try to explore more?
		List<Effect> penaltyList = new ArrayList<Effect>();
		Person p = Player.player.getPerson();
		if (!p.hasEffect(Effect.CURSE)) {
			penaltyList.add(Effect.CURSE);
		}
		if (!p.hasEffect(Effect.DAMAGED)) {
			penaltyList.add(Effect.DAMAGED);
		}
		if (!p.hasEffect(Effect.WOUNDED)) {
			penaltyList.add(Effect.WOUNDED);
		}
		if (!p.hasEffect(Effect.TIRED)) {
			penaltyList.add(Effect.TIRED);
		}
		
		if (penaltyList.size() > 0) {
			Effect penalty = Rand.randList(penaltyList);
			switch (penalty) {
			case CURSE:
				Print.println(TrawelColor.RESULT_BAD+"The death cursed your flesh!");
				p.addEffect(Effect.CURSE);
				break;
			case DAMAGED:
				Print.println(TrawelColor.RESULT_BAD+"The death damaged your armor!");
				p.addEffect(Effect.DAMAGED);
				break;
			case WOUNDED:
				Print.println(TrawelColor.RESULT_BAD+"The death wounded your flesh!");
				p.addEffect(Effect.WOUNDED);
				break;
			case TIRED:
				Print.println(TrawelColor.RESULT_BAD+"The death tired you out!");
				p.addEffect(Effect.TIRED);
				break;
			default:
				throw new RuntimeException("Invalid death penalty effect:" + penalty);
			}
		}
	}

}
