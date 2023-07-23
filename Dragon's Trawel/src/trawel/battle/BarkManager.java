package trawel.battle;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.Person;

public class BarkManager {

	
	public static void getTaunt(Person p) {
		if (extra.getPrint()) {
			return;
		}
		double hpPercent = ((double)p.getHp())/p.getMaxHp();
		switch (p.getPersonType()) {
		case COWARDLY:
			if (hpPercent > .4) {
				extra.println(genericTaunt(p));
			}else {
				extra.println(cowardTaunt(p));
			}
			break;
		case FEARLESS:
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
		case GRIZZLED:
			if (hpPercent > .3) {
				extra.println(genericTaunt(p));
			}else {
				if (extra.randRange(1,2) == 1) {
				extra.println(grizzleTaunt(p));
				}else {
					extra.println(genericTaunt(p));
				}
			}
			break;
		case DEATHCHEATED:
			if (hpPercent > .33) {
				extra.println(genericTaunt(p));
			}else {
				if (extra.randRange(1,2) == 1) {
				extra.println(deathCheaterTaunt(p));
				}else {
					extra.println(genericTaunt(p));
				}
			}
			break;
		case LIFEKEEPER:
			if (hpPercent > .33) {
				extra.println(lifeKeeperTaunt(p));
			}else {
				if (extra.randRange(1,2) == 1) {
					extra.println(lifeKeeperTaunt(p));
					}else {
						extra.println(lifeKeeperTauntLow(p));
					}
			}
			break;
		}
		
	}
	
	public static void getBoast(Person p, boolean opposed) {
		if (extra.getPrint()) {
			return;
		}
		double hpPercent = ((double)p.getHp())/p.getMaxHp();
		switch (p.getPersonType()) {
		case COWARDLY:
			if (hpPercent > .4) {
				extra.println(genericBoast(p,opposed));
			}else {
				return;
			}
			break;
		case FEARLESS: case GRIZZLED: case DEATHCHEATED:
			extra.println(genericBoast(p,opposed));
			break;
		case LIFEKEEPER:
			extra.println(lifeKeeperBoast(p));
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
	
	private static String lifeKeeperBoast(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("My task is not always clear, but that belies its importance.");//TODO: it's its it's i'ts
		list.add("Life itself pushes me to move forward!");
		list.add("The ebb and flow... wax and wane... it... I!");
		return p.getName() + " " + extra.choose("mutters","shouts","boasts","declares")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String resolveTaunt(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("Death draws near... but I am unafraid!");
		list.add("Can you kill me, or will you fall, like the others?");
		list.add("I greet death with courage!" + extra.choose(""," Can you say the same?"));
		return p.getName() + " " + extra.choose("screams","shouts","taunts")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String lifeKeeperTaunt(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("Primal forces urge me on!");
		list.add("Life itself demands that I do this!");
		list.add("The ebb and flow speaks to me!" + extra.choose(""," It whispers your weaknesses in my ear!"," It bolsters me with pure strength!"));
		return p.getName() + " " + extra.choose("mutters","shouts","taunts","declares")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String lifeKeeperTauntLow(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("Primal forces urge me on, and if I fall another shall rise to take my place!");
		list.add("If I fall, primality will reclaim me!");
		list.add("The ebb and flow speaks to me!" + extra.choose(""," Whispers of death and life are constant!"," I face oblivion."));
		return p.getName() + " " + extra.choose("mutters","shouts","taunts","declares")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String deathCheaterTaunt(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("Death draws near... "+extra.choose("but I am unafraid!","I care not, I fight on!","but I know it well!"));
		list.add("Can you kill me... for real?"+ extra.choose(""," I care not, I fight on!"));
		list.add("I greet death with courage!" + extra.choose(""," Can you say the same?"," It has proven weak!"));
		return p.getName() + " " + extra.choose("screams","shouts","taunts")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String grizzleTaunt(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("You are not the first... but you will not be the last!");
		list.add("Can you kill me, or will you fall, like the others?");
		list.add("Is it finally time?");
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
