package trawel.towns.features.fight;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.battle.Combat;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.FeatureData;
import trawel.helper.constants.TrawelColor;
import trawel.personal.NPCMutator;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.Town;
import trawel.towns.features.Feature;

public class Champion extends Feature{
	
	static {
		FeatureData.registerFeature(Champion.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" host a single fighter that can be battled.");
			}
			
			@Override
			public int priority() {
				return 100;
			}
			
			@Override
			public String name() {
				return "Champion";
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.ENCOUNTERS;
			}

			@Override
			public String color() {
				return TrawelColor.F_COMBAT;
			}
		});
	}

	private static final long serialVersionUID = 1L;
	Person person;
	double timeElapsed;
	
	public Champion(int level){
		tier = level;
		person = RaceFactory.getDueler(level);
		NPCMutator.mutateImproveGear(person,1);
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		//tutorialText = "You should probably hold off on fighting champions until you're their level- explore the world and come back later.";
		timeElapsed=0;
	}
	
	public Champion(int level,int battleSize, Town t) {
		tier = level;
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
		Print.offPrintStack();
		List<List<Person>> people = new ArrayList<List<Person>>();
		while(people.size() < battleSize) {
			people.add(Collections.singletonList(RaceFactory.getDueler(level)));
		}
		
		person = Combat.HugeBattle(t.getIsland().getWorld(),people).getNonSummonSurvivors().get(0);
		Print.popPrintStack();
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
	}
	
	@Override
	public String nameOfType() {
		return "Champion";
	}
	
	@Override
	public Area getArea() {
		return Area.CHAMPION;
	}
	
	@Override
	public void go() {
		if (person.reallyFight("Really Challenge")) {
			Combat c = Player.player.fightWith(person);
			if (c.playerWon() > 0) {
				Print.println("You defeat the champion!");
				//we don't want to store the person for real, they can get deleted
				//we're also fine with this getting overwritten if two people share the same name
				Player.player.addGroupedAchieve(town.townHash()+"_champs","Champions of "+town.getName()+": ", person.getName() + " " + Rand.choose("slayer","killer","champion-killer","champion"));
				person = null;
				Networking.unlockAchievement("beat_champion");
			}else {
				this.name = person.getName() + " (Level " + person.getLevel()+")" ;
				Print.println("You lose the bout. Perhaps you should explore other towns to level up before fighting them?");
				Networking.unlockAchievement("die_to_champion");
				/*if (tutorialText == "Champion {Taken Title}") {
					tutorialText = "Champion";
				}*/
			}
		}
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (person == null) {
			timeElapsed+=time;
			if (timeElapsed > Rand.randRange(24, 60)) {
				Agent delete = town.getPersonableOccupants()
						.filter(a -> a.getPerson().getLevel() == tier)
						.findFirst().orElse(null);
					if (delete != null) {
						person = delete.getPerson();
						delete.onlyGoal(AgentGoal.OWN_SOMETHING);
						this.name = person.getName() + " (Level " + person.getLevel()+")" ;
						//tutorialText = "New champions will emerge if a landed title is empty.";
						//tutorialText = "Champion {Taken Title}";
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
