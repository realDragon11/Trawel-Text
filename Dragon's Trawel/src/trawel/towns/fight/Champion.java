package trawel.towns.fight;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class Champion  extends Feature{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Person person;
	double timeElapsed;
	
	public Champion(int level){
		person = RaceFactory.getDueler(level);
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		tutorialText = "You should probably hold off on fighting champions until you're their level- explore the world and come back later.";
		timeElapsed=0;
	}
	
	public Champion(int level,int battleSize, Town t) {
		
		tutorialText = "Battleforged champions fought in a pit fight to survive.";
		timeElapsed=0;/*
		List<Person> people = new ArrayList<Person>();
		while(people.size() < battleSize) {
			people.add(RaceFactory.getDueler(level));
		}
		extra.disablePrintSubtle();
		while (people.size() > 1) {
			List<Person> people1 = new ArrayList<Person>();
			List<Person> people2 = new ArrayList<Person>();
			//Iterator<Person> iter = people.iterator();
			boolean sorter = false;
			for(Person p: people) {
				if (sorter) {
					people1.add(p);
				}else {
					people2.add(p);
				}
				sorter = !sorter;
			}
			people = mainGame.HugeBattle(t.getIsland().getWorld(),people1,people2);
		}*/
		//no longer teamfights
		extra.offPrintStack();
		List<List<Person>> people = new ArrayList<List<Person>>();
		while(people.size() < battleSize) {
			people.add(Collections.singletonList(RaceFactory.getDueler(level)));
		}
		
		person = mainGame.HugeBattle(t.getIsland().getWorld(),people).getNonSummonSurvivors().get(0);
		extra.popPrintStack();
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		
	}
	
	@Override
	public String getColor() {
		return extra.F_COMBAT;
	}
	
	@Override
	public void go() {
		Networking.setArea("champ");
		Networking.sendStrong("Discord|imagesmall|champion|Champion|");
		extra.println(extra.PRE_RED+"Challenge " + person.getName() + "?");
		if (extra.yesNo()) {
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),this.person);
			if (winner == Player.player.getPerson()) {
				extra.println("You defeat the champion!");
				Player.player.addTitle(person.getName() + " " + extra.choose("slayer","killer","champion-killer","champion"));
				person = null;
				Networking.unlockAchievement("beat_champion");
			}else {
				this.name = person.getName() + " (Level " + person.getLevel()+")" ;
				extra.println("You lose the bout. Perhaps you should explore other towns to level up before fighting them?");
				Networking.unlockAchievement("die_to_champion");
			}
		}
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (person == null) {
			timeElapsed+=time;
			if (timeElapsed > extra.randRange(24, 60)) {
				Agent delete = town.getPersonableOccupants()
						.filter(a -> a.getPerson().getLevel() == town.getTier())
						.findFirst().orElse(null);
					if (delete != null) {
						person = delete.getPerson();
						this.name = person.getName() + " (Level " + person.getLevel()+")" ;
						tutorialText = "New champions will emerge if a landed title is empty.";
						town.removeOccupant(delete);//MAYBELATER use events?
					}			
			}
		}
		return null;
	}
	
	@Override
	public boolean canShow() {
		return person != null;
	}

}
