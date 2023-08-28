package trawel.towns.fight;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Arena extends Feature{

	private static final long serialVersionUID = 1L;
	private int rounds;
	private double interval, timeLeft;
	private int timesDone;
	private ArrayList<Person> winners;
	private Person rematcher;
	
	public Arena(String name,int tier,int rounds, double interval, double timeLeft,int timesDone, SuperPerson owner) {
		this.name = name;
		this.tier = tier;
		this.rounds = rounds;
		this.interval = interval;
		this.timeLeft = timeLeft;
		this.timesDone = timesDone;
		winners = new ArrayList<Person>();
		tutorialText = "Arena.";
		this.owner = owner;
	}
	
	@Override
	public String getColor() {
		return extra.F_COMBAT;
	}
	
	public Arena(String name,int tier,int rounds, double interval, double timeLeft,int timesDone) {
		this(name,tier,rounds,interval,timeLeft,timesDone,null);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void go() {
		Networking.setArea("arena");
		if (owner == Player.player && moneyEarned > 0) {
			extra.println("You take the " + moneyEarned + " in profits.");
			Player.player.addGold(moneyEarned);
			moneyEarned = 0;
		}
		Networking.sendStrong("Discord|imagesmall|arena|Arena|");
		getRematch();
		extra.println("1 Participate in the " +  this.getRewardTitle() + " tournament in " + extra.format(this.getTimeLeft()) + " hours.");
		if (rematcher != null) {
		extra.println("2 rematch (" +rematcher.getName()+" level " +rematcher.getLevel() +")");}else {
			extra.println("2 No Rematch Open");
		}
		extra.println("3 leave");
		int input = extra.inInt(3);
		extra.linebreak();
		if (input == 3){
			return;
		}
		if (input == 2){
			if (rematcher != null) {
			doRematch();}else {
				extra.println("No Rematch Open");
			}
			return;
		}
		if (input == 1) {
			Player.addTime(this.getTimeLeft()+.1);
			mainGame.globalPassTime();
			this.doTournyPlayer();
			return;
		}
		mainGame.globalPassTime();
		go();
	}
	
	private void doRematch() {
		Player.addTime(.1);
		Combat c = Player.player.fightWith(rematcher);
		if (c.playerWon() > 0) {
			winners.remove(rematcher);
		}
	}

	private void getRematch() {
		Person p = null;
		int level = Integer.MAX_VALUE;
		for (Person s: winners) {
			if (s.getLevel() < level) {
				p = s;
			}
		}
		rematcher = p;
	}
	
	public Person popWinner() {
		if (!winners.isEmpty()) {
			return winners.get(0);
		}else {
			return null;
		}
	}

	public void doTournyPlayer() {
		for (int i = 1;i <= rounds;i++) {
			extra.println("It's round " + i + "...");
			Person fighter = RaceFactory.getDueler(tier);
			fighter.addXp(notFact(i));
			Combat c = Player.player.fightWith(fighter);
			if (c.playerWon() > 0) {
				if (i == rounds) {
					extra.println("You win the tournment!");
					Player.player.addGroupedAchieve(this, getName(), ""+timesDone);
				}else {
					extra.println("You move on to the next round of the tournament.");
				}
			}else {
				winners.add(fighter);
				extra.println("You lose the tourny.");break;
			}
		}
		Player.addTime(1);
	}
	
	public String getRewardTitle() {
		return timesDone + " " + name;
	}
	
	public int getTimesDone() {
		return timesDone;
	}

	public int notFact(int i) {
		int j = 0;
		for (int a = 1;a < i;a++) {
			j+=a;
		} 
		return j;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public double getInterval() {
		return interval;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

	public double getTimeLeft() {
		return timeLeft;
	}

	public void setTimeLeft(double timeLeft) {
		this.timeLeft = timeLeft;
	}

	public int getRounds() {
		return rounds;
	}

	public void setRounds(int rounds) {
		this.rounds = rounds;
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		
		while (time > 0) {
			if (time < timeLeft) {
				timeLeft-=time;
				time = 0;
			}else {
				time -=timeLeft;
				timesDone++;
				moneyEarned +=tier*2*(timeLeft/100);
				timeLeft = interval;
			}
		}//TODO: perhaps actual fights get recorded?
		return null;
	}
}
