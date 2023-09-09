package trawel.towns.fight;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class Champion  extends Feature{

	private static final long serialVersionUID = 1L;
	Person person;
	double timeElapsed;
	
	public Champion(int level){
		person = RaceFactory.getDueler(level);
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		tutorialText = "Champion";
		//tutorialText = "You should probably hold off on fighting champions until you're their level- explore the world and come back later.";
		timeElapsed=0;
		area_type = Area.CHAMPION;
	}
	
	public Champion(int level,int battleSize, Town t) {
		area_type = Area.CHAMPION;
		tutorialText = "Battleforged Champion";
		//tutorialText = "Battleforged champions fought in a pit fight to survive.";
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
		person.getBag().graphicalDisplay(1,person);
		extra.println(extra.PRE_BATTLE+"Challenge " + person.getName() + "?");
		if (extra.yesNo()) {
			Combat c = Player.player.fightWith(person);
			if (c.playerWon() > 0) {
				extra.println("You defeat the champion!");
				//we don't want to store the person for real, they can get deleted
				//we're also fine with this getting overwritten if two people share the same name
				Player.player.addGroupedAchieve(town.townHash()+"_champs","Champions of "+town.getName()+": ", person.getName() + " " + extra.choose("slayer","killer","champion-killer","champion"));
				person = null;
				Networking.unlockAchievement("beat_champion");
			}else {
				this.name = person.getName() + " (Level " + person.getLevel()+")" ;
				extra.println("You lose the bout. Perhaps you should explore other towns to level up before fighting them?");
				Networking.unlockAchievement("die_to_champion");
				if (tutorialText == "Champion {Taken Title}") {
					tutorialText = "Champion";
				}
			}
		}else {
			Networking.clearSide(1);
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
						delete.onlyGoal(AgentGoal.OWN_SOMETHING);
						this.name = person.getName() + " (Level " + person.getLevel()+")" ;
						//tutorialText = "New champions will emerge if a landed title is empty.";
						tutorialText = "Champion {Taken Title}";
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
