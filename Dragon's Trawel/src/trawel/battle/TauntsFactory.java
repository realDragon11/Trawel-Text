package trawel.battle;
import java.util.ArrayList;

import trawel.extra;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.item.body.Race;

public class TauntsFactory {
	public static ArrayList<String> tauntList = new ArrayList<String>();
	public static ArrayList<String> boastList = new ArrayList<String>();
	
	public static ArrayList<String> tauntListOrc = new ArrayList<String>();
	public static ArrayList<String> tauntListFugue = new ArrayList<String>();
	
	public TauntsFactory() {
		tauntList.add("Your best is my worst!");
		tauntList.add("You'll be dead soon!");
		tauntList.add("Come on! Die!");
		tauntList.add("Time to put you six feet under!");
		tauntList.add("For glory!");
		tauntList.add(extra.choose("Run","Flee") +", "+extra.choose("run","flee")+"! Like the "+extra.choose("coward","craven")+" you are!");
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
		//tauntList.add();
		
		boastList.add("This is almost too easy!");
		boastList.add("I am the " +extra.choose("most powerful","strongest","mightiest","most amazing","greatest") +" "+extra.choose("warrior","fighter","combatant")+" in all the " + extra.choose("continent","kingdom","land","world","universe") +"!");
		boastList.add("I am the " +extra.choose("most powerful","strongest","mightiest","most amazing","greatest") +" "+extra.choose("warrior","fighter","combatant")+" in all the " + extra.choose("continent","kingdom","land","world","universe") +"!");
		//darn, these will have to be load-spesfic with the chooses
		boastList.add("I have slain many "+extra.choose("fearsome","dangerous","worthy")+" opponents!");
		boastList.add("Look out world, I'm coming!");
		boastList.add("All this equipment means that I'm the best!");
		boastList.add("I'm getting stronger everyday!");
		boastList.add("I am death!");
		boastList.add("I am become death!");
		boastList.add("I am the reaper of souls!");
		boastList.add("How many men, have fallen before me!");
		//boastList.add();
		
		
		
		
		tauntListOrc.add("HRAUGH!");
		//tauntListOrc.add();
		
		tauntListFugue.add("You shall be reclaimed.");
		tauntListFugue.add("I shall reclaim you.");
		//tauntListFugue.add();
	}
	
	public static String randTaunt() {
		return tauntList.get(extra.randRange(0,tauntList.size()-1));
	}
	
	public static String randBoast() {
		return boastList.get(extra.randRange(0,boastList.size()-1));
	}
	
	public static String randTaunt(Race r) {
		if (extra.randRange(1,10) == 1) {
			if (r.raceID() == RaceID.ORC) {
				return tauntListOrc.get(extra.randRange(0,tauntListOrc.size()-1));
			}
			
			if (r.raceID() == RaceID.FUGUE) {
				return tauntListFugue.get(extra.randRange(0,tauntListFugue.size()-1));
			}
		}
		return tauntList.get(extra.randRange(0,tauntList.size()-1));
	}
	
	public static String randBoast(Race r) {
		
		return boastList.get(extra.randRange(0,boastList.size()-1));
	}
}
