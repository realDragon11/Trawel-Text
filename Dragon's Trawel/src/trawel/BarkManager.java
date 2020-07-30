package trawel;

import java.util.ArrayList;
import java.util.List;

public class BarkManager {

	
	public static String getTaunt(String pers,double hpPercent) {
		switch (pers) {
		case "cowardly":
			if (hpPercent > .4) {
				return genericTaunt();
			}else {
				return cowardTaunt();
			}
			
		}
		return "";
	}
	
	public static String getBoast(String pers,double hpPercent, boolean opposed) {
		switch (pers) {
		case "cowardly":
			if (hpPercent > .4) {
				return genericTaunt();
			}else {
				return cowardTaunt();
			}
			
		}
		return "";
	}

	private static String cowardTaunt() {
		List<String> tauntList = new ArrayList<String>();
		tauntList.add("Please! I don't want to die!");
		tauntList.add("Oh gods I'm going to die!");
		tauntList.add("AAAAAAAAA!");
		return extra.randList(tauntList);
	}

	private static String genericTaunt() {
		List<String> tauntList = new ArrayList<String>();
		tauntList.add("Your best is my worst!");
		tauntList.add("You'll be dead soon!");
		tauntList.add("Come on! Die!");
		tauntList.add("Time to put you six feet under!");
		tauntList.add("For glory!");
		tauntList.add(extra.choose("Run","Flee") +", "+extra.choose("run","flee")+"! Like the "+extra.choose("coward","craven")+" you are!");
		tauntList.add("Die by me!");
		tauntList.add("I shall overcome!");
		tauntList.add("All that fancy equipment will be mine soon!");
		tauntList.add("I will rip you to pieces!");
		tauntList.add("You're going DOWN!");
		tauntList.add("Come on, cry!");
		tauntList.add("Your resistance is futile!");
		tauntList.add("Go home to mommy!");
		tauntList.add("You're mincemeat!");
		tauntList.add("See you in hell!");
		tauntList.add("I'll show you!");
		tauntList.add("Prepare to die!");
		tauntList.add("Time for you to die!");
		tauntList.add("Now you're mine!");
		tauntList.add("Die already so I can take your stuff!");
		tauntList.add("Death comes for you!");
		tauntList.add("I've fought " + extra.choose("rats","crabs","worms") + " " + extra.choose("mightier","fiercer","stronger") + " than you!" );
		tauntList.add("You will now pay my taxes!");
		tauntList.add("I am NOT your equal!");
		tauntList.add("I fight better than you!");
		tauntList.add("I am your better!");
		tauntList.add("When are you gonna start fighting back?");
		tauntList.add("When are you going to start fighting for real?");
		tauntList.add("This is hardly a fight!");
		tauntList.add("No chance!");
		tauntList.add("You won't win!");
		tauntList.add("End yourself before I end you!");
		tauntList.add("Maybe the next fighter will prove a challenge!");
		return extra.randList(tauntList);
	}
}
