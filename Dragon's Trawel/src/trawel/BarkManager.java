package trawel;

import java.util.ArrayList;
import java.util.List;

public class BarkManager {

	
	public static void getTaunt(Person p) {
		double hpPercent = ((double)p.getHp())/p.getMaxHp();
		switch (p.getPersonType()) {
		case "cowardly":
			if (hpPercent > .4) {
				extra.println(genericTaunt(p));
			}else {
				extra.println(cowardTaunt(p));
			}
			break;
		case "fearless":
			if (hpPercent > .3) {
				extra.println(genericTaunt(p));
			}else {
				if (extra.randRange(1,2) == 1) {
				extra.println(resolveTaunt(p));
				}else {
					extra.println(genericTaunt(p));
				}
			}
			break;
		}
		
	}
	
	public static void getBoast(Person p, boolean opposed) {
		double hpPercent = ((double)p.getHp())/p.getMaxHp();
		switch (p.getPersonType()) {
		case "cowardly":
			if (hpPercent > .4) {
				extra.println(genericBoast(p,opposed));
			}else {
				return;
			}
			break;
		case "fearless":
			extra.println(genericBoast(p,opposed));
			break;
			
		}
	}

	private static String genericBoast(Person p, boolean opposed) {
		List<String> list = new ArrayList<String>();
		list.add("This is almost too easy!");
		list.add("I am the " +extra.choose("most powerful","strongest","mightiest","most amazing","greatest") +" "+extra.choose("warrior","fighter","combatant")+" in all the " + extra.choose("continent","kingdom","land","world","universe") +"!");
		list.add("I am the " +extra.choose("most powerful","strongest","mightiest","most amazing","greatest") +" "+extra.choose("warrior","fighter","combatant")+" in all the " + extra.choose("continent","kingdom","land","world","universe") +"!");
		list.add("I have slain many "+extra.choose("fearsome","dangerous","worthy")+" opponents!");
		list.add("Look out world, I'm coming!");
		list.add("All this equipment means that I'm the best!");
		list.add("I'm getting stronger everyday!");
		list.add("I am death!");
		list.add("I am become death!");
		list.add("I am the reaper of souls!");
		list.add("How many men, have fallen before me!");
		return p.getName() + " " + extra.choose("screams","shouts","boasts")+  " \""+ extra.randList(list) +"\"";
	}
	
	private static String resolveTaunt(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("Death draws near... but I am unafraid!");
		list.add("Can you kill me, or will you fall, like the others?");
		list.add("I greet death with courage!" + extra.choose(""," Can you say the same?"));
		return p.getName() + " " + extra.choose("screams","shouts","taunts")+  " \""+ extra.randList(list) + "\"";
	}


	private static String cowardTaunt(Person p) {
		List<String> tauntList = new ArrayList<String>();
		tauntList.add("\"Please! I don't want to die!\" " + p.getName() + " whimpers.");
		tauntList.add(p.getName() + " shouts \"Oh gods I'm going to die!\"");
		tauntList.add("\"AAAAAAAAA!\" " + p.getName() + " screams in terror.\"");
		return extra.randList(tauntList);
	}

	private static String genericTaunt(Person p) {
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
		return p.getName() + " " + extra.choose("screams","shouts","taunts")+  " \""+ extra.randList(tauntList) + "\"";
	}
}
