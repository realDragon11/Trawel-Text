package trawel.battle;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.Person;
import trawel.personal.RaceFactory.RaceClass;

public class BarkManager {

	
	public static void getTaunt(Person p,Person target) {
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
		case DRUDGER_GENERIC:
			extra.println(drudgerTaunt(p,target));
			break;
		case FELL_MONSTER:
			extra.println(fellTaunt(p,target));
			break;
		case HARPY_GENERIC:
			if (hpPercent > .4 || extra.chanceIn(2,3)) {
				extra.println(harpyTaunt(p));
			}else {
				extra.println(cowardTaunt(p));
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
		case DRUDGER_GENERIC:
			extra.println(drudgerBoast(p));
			break;
		case FELL_MONSTER:
			extra.println(fellBoast(p));
			break;
		case HARPY_GENERIC:
			extra.println(harpyBoast(p));
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
		list.add("How many folk, have fallen before me!");
		return p.getName() + " " + extra.choose("screams","shouts","boasts")+  " \""+ extra.randList(list) +"\"";
	}
	
	private static String harpyBoast(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("So many shiny treasures today!");
		list.add("What an interesting series of events to ponder...");
		list.add("This isn't the best haul, but the best in a while!");
		list.add("Baubles and trinkets, all mine to keep!");
		return p.getName() + " " + extra.choose("shrills","screeches","ruminates","murmurs")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String harpyTaunt(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("Another corpse to feed the nest!");
		list.add("I hope you have some really shiny loot!");
		list.add("Your stuff better not be a waste of my time!");
		list.add("At least you look like you have some good gear to take!");
		return p.getName() + " " + extra.choose("shrills","screeches","wails","cries")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String fellBoast(Person p) {//can't really be used in personable things, but that is fine. For a fully personable fell creature make another list
		List<String> list = new ArrayList<String>();
		list.add(" looks around, and their vision falls on a corpse.");
		list.add(" scans the area menacingly, pondering their next move.");
		list.add(" lets loose a discordant howl!");
		list.add(" takes a trophy from the minds of the dead.");
		return p.getName() + extra.randList(list);
	}
	private static String fellTaunt(Person p,Person target) {
		List<String> list = new ArrayList<String>();
		if (extra.chanceIn(3,4)) {
			list.add(" gazes horribly.");
			list.add(" stares through your bones, your body, into your soul. You feel a chill...");
			list.add(" is looking at someone else... or are they? You don't quite understand how... that can't be!");
			list.add(" contorts and you feel an intense sense of wrongness.");
			list.add(" mocks your fragile sanity silently.");
			list.add(" knows too much!");
			list.add(" is something truly evil!");//woh reference, yes
			return p.getName() + extra.randList(list);
		}else {
			return "\""+target.getNameNoTitle()+"...\" Did "+p.getName() +" just say your name?!";
		}
		
	}
	
	private static String drudgerBoast(Person p) {
		List<String> list = new ArrayList<String>();
		list.add("The ocean prevails!");
		list.add("The sea sees all!");
		list.add("Our kingdom will not be limited to the sea alone!");
		list.add("Visions of briny depths spur me onwards in this accursed dry hell, they shall expand here!");
		list.add("From the depths to the peaks, the ocean owns all!");
		return p.getName() + " " + extra.choose("gurgles","babbles","bubbles","murmurs","hisses")+  " \""+ extra.randList(list) + "\"";
	}
	
	private static String drudgerTaunt(Person p, Person target) {
		List<String> list = new ArrayList<String>();
		list.add("The bottom-feeders will love your corpse!");
		list.add("Your bloated body will make a fine gift to those still at home!");
		
		if (target.getBag().getRace().raceClass != RaceClass.DRUDGER) {//for drudger on drudger rebel action in the future
			list.add("Landfolk are always so hardy- yet so much easier to kill.");
			list.add("From the the depths I come, to the depths you shall go!");
		}
		
		list.add("Ocean claim you!");
		list.add("Sea swallow you!");
		
		return p.getName() + " " + extra.choose("gurgles","babbles","bubbles","murmurs","hisses")+  " \""+ extra.randList(list) + "\"";
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
