package trawel;
import java.awt.Color;
import java.util.ArrayList;

public class Arena extends Feature{
	private int tier, rounds;
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
		tutorialText = "Arenas are a great place to get started.";
		color = Color.RED;
		this.owner = owner;
	}
	
	public Arena(String name,int tier,int rounds, double interval, double timeLeft,int timesDone) {
		this(name,tier,rounds,interval,timeLeft,timesDone,null);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void go() {
		Networking.setArea("arena");
		if (owner == Player.player && moneyEarned > 0) {
			extra.println("You take the " + moneyEarned + " in profits.");
			Player.bag.addGold(moneyEarned);
			moneyEarned = 0;
		}
		Networking.sendStrong("Discord|imagesmall|arena|Arena|");
		getRematch();
		extra.println("1 Participate in the " +  this.getTitle() + " tournament in " + extra.format(this.getTimeLeft()) + " hours.");
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
			this.doTournyPlayer();
			return;
		}
		WorldGen.plane.passTime(Player.passTime);
		go();
	}
	
	private void doRematch() {
		Player.addTime(.1);
		Person winner = mainGame.CombatTwo(Player.player.getPerson(),rematcher);
		if (winner.isPlayer()) {
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
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),fighter);
			if (winner == Player.player.getPerson()) {
				if (i == rounds) {
					extra.println("You win the tournment!");
					Player.player.addTitle(this.getTitle());
					if (Player.player.animalQuest == 4 && this.name.equals("epino arena")) {
						extra.println("Well done hotshot! Here's the promised reward!");
						Player.bag.addGold(1000);
						extra.println("He gives you 1000 gold.");
						extra.println("Now, I heard you went up to the altar...");
						extra.println("And that you keep returning from the grave!");
						extra.println("I think you might be something special.");
						extra.println("I think you should go back to the altar and check it out again.");
						Player.player.animalQuest++;
					}
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
	
	public String getTitle() {
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

	public int getTier() {
		return tier;
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

	public void passTime(double time) {
		moneyEarned +=tier*2*time;
		while (time > 0) {
		if (time < timeLeft) {
			timeLeft-=time;
			time = 0;
		}else {
			time -=timeLeft;
			timesDone++;
			timeLeft = interval;
		}
		}
	}
}
