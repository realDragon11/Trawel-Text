import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class Champion  extends TravelingFeature{

	Person person;
	double timeElapsed;
	
	public Champion(int level){
		super(level);
		hasSomething = true;
		person = new Person(level);
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		tutorialText = "You should probably hold off on fighting champions until you're their level.";
		timeElapsed=0;
		color = Color.RED;
	}
	
	public Champion(int level,int battleSize, Town t) {
		super(level);
		hasSomething = true; 
		
		tutorialText = "Battleforged champions fought in a pit fight to survive.";
		timeElapsed=0;
		color = Color.RED;
		ArrayList<Person> people = new ArrayList<Person>();
		while(people.size() < battleSize) {
			people.add(new Person(level));
		}
		extra.disablePrintSubtle();
		while (people.size() > 1) {
			ArrayList<Person> people1 = new ArrayList<Person>();
			ArrayList<Person> people2 = new ArrayList<Person>();
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
		}
		extra.enablePrintSubtle();
		
		person = people.get(0);
		this.name = person.getName() + " (Level " + person.getLevel()+")" ;
		
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|icon|Champion|");
		Networking.sendColor(Color.RED);
		extra.println("Challenge " + person.getName() + "?");
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
	public void passTime(double time) {
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
				town.getOccupants().remove(delete);}
				
			}
		}
	}

}
