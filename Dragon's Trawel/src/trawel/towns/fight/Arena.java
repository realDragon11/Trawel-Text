package trawel.towns.fight;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;

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
		tutorialText = "Arena";
		this.owner = owner;
		area_type = Area.ARENA;
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
		getRematch();
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				
				if (getTimeLeft() < 32d) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_BATTLE+"Participate in the " + getRewardTitle() + " tournament in " + extra.format(getTimeLeft()) + " hours.";
						}

						@Override
						public boolean go() {
							Player.addTime(getTimeLeft()+.1);
							mainGame.globalPassTime();
							Person winner = doTourny(Player.player.getPerson());
							if (winner == Player.player.getPerson()) {
								extra.println("You win the tournment!");
								Player.player.addGroupedAchieve(Arena.this, getName(), ""+timesDone);
							}
							Player.addTime(1);
							mainGame.globalPassTime();
							return false;
						}});
					
				}else {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "Upcoming " +getRewardTitle() + " tournament is "+extra.format(getTimeLeft()) + " hours away." ;
						}});
				}
				//this will be 2 or 1, so mashing 1 will start fights either way
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Hang around for 24 hours.";
					}

					@Override
					public boolean go() {
						Player.addTime(24d);
						mainGame.globalPassTime();
						return false;
					}});
				if (rematcher != null) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_BATTLE+"Rematch with " + rematcher.getName() +", Level " +rematcher.getLevel()+".";
						}

						@Override
						public boolean go() {
							doRematch();
							mainGame.globalPassTime();
							getRematch();
							return false;
						}});
				}
				//will not display rematch if noone is there
				
				list.add(new MenuBack("Leave"));
				return list;
			}});
		rematcher = null;//free up
	}
	
	private void doRematch() {
		Player.addTime(.5);
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
	
	protected Person doTourny(Person added) {
		Person fore = added;
		if (fore == null) {
			fore = RaceFactory.getDueler(tier);
		}
		Person other = RaceFactory.getDueler(tier);
		World w = town.getIsland().getWorld();
		for (int i = 1;i <= rounds;i++) {
			Combat c = mainGame.CombatTwo(fore, other,w);
			fore = c.getNonSummonSurvivors().get(0);
			if (i <= rounds) {
				if (fore.isPlayer()) {
					extra.println("You move on to the next round of the tournament.");
				}
				//get the other branch on this part of the tree
				other = RaceFactory.getDueler(tier);
				Person sub = RaceFactory.getDueler(tier);
				for (int j = 1; j <= i;j++) {
					Combat c2 = mainGame.CombatTwo(other, sub,w);
					other = c2.getNonSummonSurvivors().get(0);
				}
			}
		}
		if (!fore.isPlayer()) {
			winners.add(fore);
		}
		return fore;
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
				//one currency per 8 hours times uneffective
				moneyEarned +=getUnEffectiveLevel()*(interval/8d);
				timeLeft = interval;
				doTourny(null);
				//look into freeing winners
				for (int i = winners.size()-1;i >= 0;i--) {
					Person p = winners.get(i);
					if (extra.chanceIn(1,3)) {
						winners.remove(i);
						town.addOccupant(p.setOrMakeAgentGoal(AgentGoal.NONE));
					}
				}
			}
		}//TODO: perhaps actual fights get recorded?
		return null;
	}
}
