package trawel.towns.features.fight;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.FeatureData;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.World;
import trawel.towns.data.Calender;
import trawel.towns.features.Feature;

public class Arena extends Feature{
	
	//color goes through other function for non-tutorial text, so feature indicator text can be clean
	private static final String COLOR = TrawelColor.F_COMBAT;
	private static final String NAME = COLOR+"Arena"+TrawelColor.COLOR_RESET;
	private static final String NAMES = COLOR+"Arenas"+TrawelColor.COLOR_RESET;
	
	static {
		FeatureData.registerFeature(Arena.class,
				new FeatureData() {

					@Override
					public void tutorial() {
						Print.println(NAMES+" host fighting Tournaments, providing an endless stream of battles. NPC Tournament winners will hang around for you to fight or rematch. You can also loiter in "+NAMES+" to pass time for no cost.");
					}

					@Override
					public String name() {
						return NAME;
					}

					@Override
					public FeatureTutorialCategory category() {
						return FeatureTutorialCategory.ENCOUNTERS;
					}

					@Override
					public int priority() {
						return 10;
					}
				});
	}

	private static final long serialVersionUID = 1L;
	private int rounds;
	private double interval, timeLeft;
	private int timesDone;
	private List<Person> winners;
	private boolean playerActive;
	
	public Arena(String name,int tier,int rounds, double interval, double timeLeft,int timesDone, SuperPerson owner) {
		this.name = name;
		this.tier = tier;
		this.rounds = rounds;
		this.interval = interval;
		this.timeLeft = timeLeft;
		this.timesDone = timesDone;
		winners = new ArrayList<Person>();
		this.owner = owner;
	}
	
	@Override
	public String nameOfType() {
		return "Arena";
	}
	
	@Override
	public String nameOfFeature() {
		return "Arena";
	}
	
	@Override
	public Area getArea() {
		return Area.ARENA;
	}
	
	@Override
	public String getColor() {
		return COLOR;
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
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				
				if (getTimeLeft() < 32d) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.PRE_BATTLE+"Participate in the " + getRewardTitle() + " tournament in " + Print.format(getTimeLeft()) + " hours.";
						}

						@Override
						public boolean go() {
							playerActive = true;
							Player.addTime(getTimeLeft()+.1);
							TrawelTime.globalPassTime();
							Person winner = doTourny(Player.player.getPerson());
							if (winner == Player.player.getPerson()) {
								Print.println("You win the tournment!");
								Player.player.addGroupedAchieve(Arena.this, getName(), ""+timesDone);
							}
							playerActive = false;
							Player.addTime(1);
							TrawelTime.globalPassTime();
							getRematch();
							return false;
						}});
					
				}else {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "Upcoming " +getRewardTitle() + " tournament is "+Print.format(getTimeLeft()) + " hours away." ;
						}});
				}
				//this will be 2 or 1, so mashing 1 will start fights either way
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_FREE+"Hang around for a day.";
					}

					@Override
					public boolean go() {
						Player.addTime(23d+Rand.randRange(.5f,2.5f));
						TrawelTime.globalPassTime();
						Print.println("It is " +Calender.dateFull(town)+".");
						return false;
					}});
				//now also includes people who won when you weren't involved
				Person rematcher = getRematch();
				if (rematcher != null) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.PRE_BATTLE+"Fight with " + rematcher.getName() +", Level " +rematcher.getLevel()+".";
						}

						@Override
						public boolean go() {
							doRematch(rematcher);
							TrawelTime.globalPassTime();
							return false;
						}});
				}
				//will not display rematch if noone is there
				
				list.add(new MenuBack("Leave."));
				return list;
			}});
	}
	
	private void doRematch(Person p) {
		Player.addTime(.5);
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			winners.remove(p);
		}
	}

	/**
	 * returns the lowest level winner
	 */
	private Person getRematch() {
		Person p = null;
		for (Person s: winners) {
			if (p == null || s.getLevel() < p.getLevel()) {
				p = s;
			}
		}
		return p;
	}
	
	public Person popWinner() {
		if (!winners.isEmpty()) {
			return winners.get(0);
		}else {
			return null;
		}
	}
	
	private Person getOrGen(int tier) {
		if (winners.size() > 2) {
			Person p = winners.remove(Rand.getRand().nextInt(winners.size()));
			//only allow one tier higher
			if (p.getLevel() > tier+1) {
				town.addOccupant(p.setOrMakeAgentGoal(AgentGoal.NONE));
			}else {
				return p;
			}
		}
		return RaceFactory.getDueler(tier);
	}
	
	protected Person doTourny(Person added) {
		Person fore = added;
		if (fore == null) {
			fore = getOrGen(tier);
		}
		Person other;
		if (playerActive) {//if the player is fighting and it is the first round, force the fighter to be new to avoid higher levels
			other = RaceFactory.getDueler(tier);
		}else {
			other = getOrGen(tier);
		}
		World w = town.getIsland().getWorld();
		for (int i = 1;i <= rounds;i++) {
			if (fore.isPlayer() && i > 1) {
				Print.popPrintStack();//turned off below
			}
			Combat c = Combat.CombatTwo(fore, other,w);
			fore = c.getNonSummonSurvivors().get(0);
			if (i < rounds) {
				if (fore.isPlayer()) {
					Print.println("You move on to the next round of the tournament.");
					Print.offPrintStack();
				}
				//get the other branch on this part of the tree
				other = doBranch(i,w);
			}
		}
		if (!fore.isPlayer()) {
			winners.add(fore);
		}
		//used via time passing, if the player is fighting the player code will call this function instead of pass time
		timesDone++;
		//one currency per 8 hours times uneffective
		moneyEarned +=getUnEffectiveLevel()*(interval/8d);
		timeLeft = interval;
		return fore;
	}
	
	protected Person doBranch(int round,World w) {
		Person a, b;
		if (round != 1) {
			a = doBranch(round-1,w);
			b = doBranch(round-1,w);
		}else {
			a = getOrGen(tier);
			b = getOrGen(tier);
		}
		Combat c = Combat.CombatTwo(a,b,w);
		return c.getNonSummonSurvivors().get(0);
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
		//active set means the player is fighting
		if (!playerActive) {
			while (time > 0) {
				if (time < timeLeft) {
					timeLeft-=time;
					time = 0;
				}else {
					time -=timeLeft;
					Print.offPrintStack();
					doTourny(null);
					//look into freeing winners
					for (int i = winners.size()-1;i >= 0;i--) {
						Person p = winners.get(i);
						if (p.getLevel() > tier+1) {
							winners.remove(i);
							town.addOccupant(p.setOrMakeAgentGoal(AgentGoal.NONE));
						}else {
							//they train
							winners.get(i).addXp(tier/2);
						}
					}
					Print.popPrintStack();
				}
			}
		}
		return null;
	}
}
