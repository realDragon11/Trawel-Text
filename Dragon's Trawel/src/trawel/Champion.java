package trawel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class Champion  extends TravelingFeature{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Person person;
	double timeElapsed;
	
	public Champion(int level){
		super(level);
		hasSomething = true;
		person = RaceFactory.getDueler(level);
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		tutorialText = "You should probably hold off on fighting champions until you're their level- explore the world and come back later.";
		timeElapsed=0;
		color = Color.RED;
	}
	
	public Champion(int level,int battleSize, Town t) {
		super(level);
		hasSomething = true; 
		
		tutorialText = "Battleforged champions fought in a pit fight to survive.";
		timeElapsed=0;
		color = Color.RED;/*
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
		
		person = mainGame.HugeBattle(t.getIsland().getWorld(),people).get(0);
		extra.popPrintStack();
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		
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
				this.hasSomething = false;
				Networking.sendStrong("Achievement|beat_champion|");
			}else {
				this.name = person.getName() + " (Level " + person.getLevel()+")" ;
				extra.println("You lose the bout. Perhaps you should explore other towns to level up before fighting them?");
				Networking.sendStrong("Achievement|die_to_champion|");
			}
		}
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (hasSomething == false) {
			timeElapsed+=time;
			if (timeElapsed > extra.randRange(24, 60)) {
				SuperPerson delete = null;
				for (SuperPerson p: town.getOccupants()) {
					Agent a = (Agent)p;
					if (a.getPerson().getLevel() == this.tier) {
						person = a.getPerson();
						this.name = person.getName() + " (Level " + person.getLevel()+")" ;
						tutorialText = "You should probably hold off on fighting champions until you're their level.";
						hasSomething = true;
						//town.getOccupants().remove(p); // not sure if safe
						delete = p;
						break;
					}
				}
				if (delete != null) {
				town.getOccupants().remove(delete);}//TODO use events
				
			}
		}
		return null;
	}

}
