package trawel.towns.services;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.BarkManager;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.CleanseSideQuest;
import trawel.quests.CleanseSideQuest.CleanseType;
import trawel.quests.FetchSideQuest;
import trawel.quests.FetchSideQuest.FetchType;
import trawel.quests.KillSideQuest;
import trawel.quests.QBMenuItem;
import trawel.quests.QRMenuItem;
import trawel.quests.Quest;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Calender;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;
import trawel.towns.fight.ExploreFeature;

//sells booze which increases temp hp for a few fights,
//has a resident which changes with time
public class Inn extends Feature implements QuestBoardLocation{

	private static final long serialVersionUID = 1L;
	private byte resident;
	private int wins = 0;
	
	private double timePassed;
	private double nextReset;
	private double rentTime;
	
	private int beerCount;
	private int beerCost;
	
	private boolean canQuest = true;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private transient Agent curAgent;
	
	/**
	 * 0 = removed resident
	 * 1 = old fighters
	 * 2 = oracle
	 * 3 = dancers
	 * default and 0: town occupant
	 */
	private final static int RES_COUNT = 6;
	
	/**
	 * if the player is watching an inn duel, it will be here
	 */
	private static Inn playerWatching;

	@Override
	public QRType getQRType() {
		return QRType.INN;
	}
	
	public Inn(String n, int t,Town twn, SuperPerson owner) {
		name = n;
		tier = t;
		town = twn;
		timePassed = extra.randRange(1,30);
		resident = (byte) extra.randRange(1,RES_COUNT);
		nextReset = extra.randRange(4,30);
		rentTime = 0;
		tutorialText = "Inn";
		this.owner = owner;
		beerCount = extra.randRange(2,4);
		beerCost = (int) (getUnEffectiveLevel() +extra.randRange(0,2));
		area_type = Area.INN;
	}
	
	@Override
	public String nameOfType() {
		return "tavern";
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}
	
	@Override
	public void init() {

	}
	
	@Override
	public void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		switch (extra.randRange(1,3)) {
		case 1:
			if (extra.randFloat() > .85f) {//15% for crime
				sideQuests.add(FetchSideQuest.generate(this,FetchType.CRIME));
				break;
			}
			if (extra.randFloat() > .95f) {//5% for heroism
				sideQuests.add(FetchSideQuest.generate(this,FetchType.HERO));
				break;
			}
			if (extra.randFloat() > .65f) {//35% for merchant
				sideQuests.add(FetchSideQuest.generate(this,FetchType.MERCHANT));
				break;
			}
			//otherwise, community quest
			sideQuests.add(FetchSideQuest.generate(this,FetchType.COMMUNITY));
			break;
		case 2:
			sideQuests.add(CleanseSideQuest.generate(this,extra.choose(CleanseType.WOLF,CleanseType.BEAR,CleanseType.BANDIT,CleanseType.MONSTERS)));
			break;
		case 3:
			sideQuests.add(KillSideQuest.generate(this,extra.randFloat() > .8f));//20% chance to be a murder quest
			break;
		}
	}

	@Override
	public void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_CURRENCY+"Buy Beer ("+beerCount+" for "+getTown().getIsland().getWorld().moneyString(beerCost)+")";
					}

					@Override
					public boolean go() {
						buyBeer();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return getResidentName();
					}

					@Override
					public boolean go() {
						goResident();
						return false;
					}
				});
				/*
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "bard";
					}

					@Override
					public boolean go() {
						extra.println("Silence reigns.");
						return false;
					}
				});
				*/
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.FSERVICE_QUEST+"Backroom (Sidequests)";
					}

					@Override
					public boolean go() {
						backroom();
						return false;
					}
				});
				boolean playerOwns = owner == Player.player;
				if (rentTime > 0 || playerOwns){
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							if (playerOwns) {
								return extra.SERVICE_FREE+"Your Room (Owned Inn)";
							}
							//special payment is the time you already bought
							return extra.SERVICE_SPECIAL_PAYMENT+"Your Room ("+extra.F_TWO_TRAILING.format(rentTime)+" hours left)";
						}

						@Override
						public boolean go() {
							extra.menuGo(new MenuGenerator() {

								@Override
								public List<MenuItem> gen() {
									List<MenuItem> list = new ArrayList<MenuItem>();
									list.add(new MenuLine() {

										@Override
										public String title() {
											if (playerOwns) {
												return Calender.dateFull(town);
											}
											return Calender.dateFull(town)+": ("+extra.F_TWO_TRAILING.format(rentTime)+" hours left)";
										}});
									if (rentTime > 1 || playerOwns) {
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return (playerOwns ? extra.SERVICE_FREE : extra.SERVICE_SPECIAL_PAYMENT)+"Bathe (1 hour)";
											}

											@Override
											public boolean go() {
												Player.addTime(1);
												mainGame.globalPassTime();
												Player.player.getPerson().washAll();
												Player.player.getPerson().bathEffects();
												if (rentTime <= 0) {
													return true;
												}
												return false;
											}});
									}
									if (rentTime < 24 && !playerOwns) {
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return (playerOwns ? extra.SERVICE_FREE : extra.SERVICE_SPECIAL_PAYMENT)+"Wait " + extra.F_TWO_TRAILING.format(rentTime)+" hours.";
											}

											@Override
											public boolean go() {
												Player.addTime(rentTime+.1);
												mainGame.globalPassTime();
												Player.player.getPerson().restEffects();
												return true;
											}});
									}else {
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return (playerOwns ? extra.SERVICE_FREE : extra.SERVICE_SPECIAL_PAYMENT)+"Wait 24 hours.";
											}

											@Override
											public boolean go() {
												Player.addTime(24);
												mainGame.globalPassTime();
												Player.player.getPerson().restEffects();
												return false;
											}});
										if (rentTime > 72 || playerOwns) {
											list.add(new MenuSelect() {

												@Override
												public String title() {
													return (playerOwns ? extra.SERVICE_FREE : extra.SERVICE_SPECIAL_PAYMENT)+"Wait 3 days.";
												}

												@Override
												public boolean go() {
													Player.addTime(72);
													mainGame.globalPassTime();
													Player.player.getPerson().restEffects();
													return false;
												}});
										}
									}
									if (rentTime < 700 && !playerOwns) {//can rent for roughly two months tops
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return "Rent more time.";
											}

											@Override
											public boolean go() {
												rent();
												return false;
											}});
										
									}
									list.add(new MenuBack());
									return list;
								}});
							return false;
						}});
				}else {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.SERVICE_CURRENCY+"Rent a Room";
						}

						@Override
						public boolean go() {
							rent();
							return false;
						}
					});
				}
				if (town.getPersonableOccupants().limit(2).count() >=2){
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_FREE+"Watch duel (" + extra.format(nextReset-timePassed+1) + " hours)";
					}

					@Override
					public boolean go() {
						//occupantDuel(true);//now stored elsewhere
						playerWatching = Inn.this;
						Player.addTime((nextReset-timePassed+1));
						mainGame.globalPassTime();
						playerWatching = null;
						return false;
					}
				});
				}
				mList.add(new MenuBack("Leave"));
				return mList;
			}});
	}

	

	private void backroom() {
		Inn inn = this;
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new QBMenuItem(q,inn));
				}
				for (QuestR qr: Player.player.QRFor(Inn.this)) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timePassed += time;
		if (timePassed > nextReset) {
			occupantDuel(playerWatching == this);
			collectTimeStorage();
		}
		if (rentTime > 0) {
			rentTime-=time;
		}
		return null;
	}
	
	protected double nextDuelTime() {
		//limit of 20 acts as both our cap on reduction and also our sanity cap on iteration
		double mult = Math.max(5,town.getPersonableOccupants().limit(20).count())/5.0;
		//if <=5 people, occurs at normal rate, otherwise frequency increases, up to 4x
		//minimum of 4 hours, then 24-48 hours that does get hit by the mult, so final band of 10 to 52 between duels
		return 4 + ((24 +extra.getRand().nextDouble(24))/mult);
	}
	/**
	 * a duel requires that time pass
	 */
	private void occupantDuel(boolean playerwatching) {
		List<SuperPerson> spList = new ArrayList<SuperPerson>();
		town.getPersonableOccupants().forEach(spList::add);
		if (spList.size() >= 3){
			SuperPerson sp1 = extra.randList(spList);
			spList.remove(sp1);
			SuperPerson sp2 = extra.randList(spList);
			if (!playerwatching) { extra.offPrintStack();}
			//summons not allowed
			assert sp1 != null;
			assert sp2 != null;
			Combat c = mainGame.CombatTwo(sp1.getPerson(),sp2.getPerson(),town.getIsland().getWorld());
			town.removeAllKilled(c.killed);
			if (!playerwatching) {extra.popPrintStack();}
		}else {
			if (playerwatching) {
				extra.println("But no one came.");
			}
		}
	}
	
	private void collectTimeStorage() {
		//one currency per 6 hours times uneffective, better than arena
		moneyEarned +=getUnEffectiveLevel()*(timePassed/6d);
		resident = (byte)extra.randRange(1,RES_COUNT);
		if (canQuest) {this.generateSideQuest();}
		nextReset = nextDuelTime();
		timePassed = 0;
	}

	private void goResident() {
		switch (resident) {
		case 1: goOldFighter();break;
		case 2: goDancers();break;
		case 3: goOracle();break;
		default:
			if (town.getPersonableOccupants().count() == 0){
				barFight();
			}else {
				goAgent(curAgent);
			}
			;break;
		}
	}

	private String getResidentName() {
		switch(resident) {
		case 1: return "Resident: A group of old fighters";
		case 2: return "Resident: A group of dancers";
		case 3: return "Resident: An oracle.";
		default:
			if (curAgent == null || !town.getPersonableOccupants().anyMatch(a -> a == curAgent)){
				if (town.getPersonableOccupants().count() == 0) {
					return "Resident: Open Bar";
				}else {
					newCurAgent();
				}
			}
			return "Resident: " + curAgent.getPerson().getName()+ " (" +curAgent.getPerson().getLevel() +")";	
		}
	}
	
	private void goAgent(Agent agent) {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return agent.getPerson().getName() + " is resting in the inn.";
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Fight";
					}

					@Override
					public boolean go() {
						if (agent.getPerson().reallyFight("Really duel")) {
							Combat c = Player.player.fightWith(agent.getPerson());
							if (c.playerWon() > 0) {
								town.removeOccupant(agent);
								newCurAgent();
							}
							return true;
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Chat";
					}

					@Override
					public boolean go() {
						String str;
						//these boasts and taunts are not considered reduced flavor, because they are the reason to do this
						if (extra.chanceIn(2,3)) {
							str = (BarkManager.getBoast(Player.player.getPerson(),true));
						}else {
							str = (BarkManager.getTaunt(Player.player.getPerson(),agent.getPerson()));
						}
						if (str != null) {
							extra.println(str);
						}
						str = null;
						if (extra.chanceIn(2,3)) {
							str = (BarkManager.getBoast(agent.getPerson(), true));
						}else {
							str = (BarkManager.getTaunt(agent.getPerson(),Player.player.getPerson()));
						}
						if (str != null) {
							extra.println(str);
						}
						return false;
					}});
				list.add(new MenuBack("back"));
				return list;
			}}
				);
	}

	private void buyBeer() {
		if (Player.player.getGold() >= beerCost) {
			extra.println("Pay "+World.currentMoneyDisplay(beerCost)+" for "+beerCount+" beers?");
			if (extra.yesNo()) {
				Player.player.beer += beerCount;
				Player.player.addGold(beerCost);
				extra.println("You buy " +beerCount+" mugs worth. (New total "+Player.player.beer+")");
			}
		}else {
			extra.println("You can't afford that! ("+World.currentMoneyDisplay(beerCost)+")");
		}
	}
	
	private void goOldFighter() {
		int playerWon = ExploreFeature.oldFighter("a bench"," It is pleasant here.",this);
		if (playerWon > 0) {//only remove if they won
			extra.println("The old fighters leave.");
			resident = 0;//so they can't farm feat fragments
		}
	}
	
	private void goDancers() {
		extra.println("There are some dancers dancing excellently.");
		extra.println("They put on a good show.");
	}
	
	private void goOracle() {
		extra.println("There's an oracle staying at the inn.");
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Listen in on their ramblings.";
					}

					@Override
					public boolean go() {
						Player.addTime(1+extra.randFloat());
						mainGame.globalPassTime();
						Oracle.tip("");
						return true;
					}});
				list.add(new MenuBack());
				return list;
			}});
	}
	
	private void barFight() {
		if (Player.player.getPerson().getLevel()+1 < tier) {//to prevent higher up loot
			extra.println("You try to start a barfight, but get knocked out easily.");
			return;
		}
		extra.println(extra.PRE_BATTLE+"There is no resident, but there is room for a barfight... start one?");
		if (extra.yesNo()) {
			List<List<Person>> list = new ArrayList<List<Person>>();
			list.add(Player.player.getAllies());
			for (int i = 0; i < 3; i++) {
				list.add(RaceFactory.getDueler(tier).getSelfOrAllies());
			}
			Combat c = mainGame.HugeBattle(town.getIsland().getWorld(),list);
			if (c.playerWon() > 0) {
				wins++;
				if (wins == 1) {
					Player.player.addAchieve(this, this.getName() + " barfighter");
				}
				if (wins == 5) {
					Player.player.addAchieve(this, this.getName() + " barbrewer");
				}
				if (wins == 10) {
					Player.player.addAchieve(this, this.getName() + " barmaster");
				}
			}else {
				Person p = c.getNonSummonSurvivors().get(0);
				town.addOccupant(p.setOrMakeAgentGoal(AgentGoal.NONE));
			}
			
		}
	}

	@Override
	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
	
	private int roomPrice() {
		return (int) Math.ceil(2*getUnEffectiveLevel());
	}
	
	private void rent() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				int perDay = roomPrice();
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return Player.player.getGoldDisp();
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "One Day: "+World.currentMoneyDisplay(perDay);
					}

					@Override
					public boolean go() {
						if (Player.player.askBuyMoney(perDay, "one night")) {
							rentTime+=24;
							return true;
						}
						return false;
					}
				});
				list.add(new MenuSelect() {//7/5 = 1.4

					@Override
					public String title() {
						return "One Week: "+World.currentMoneyDisplay(perDay*5);
					}

					@Override
					public boolean go() {
						if (Player.player.askBuyMoney(perDay*5, "seven days")){
							rentTime+=168;
							return true;
						}
						return false;
					}
				});
				list.add(new MenuSelect() {//30/20 = 1.5

					@Override
					public String title() {
						return "One Month: "+World.currentMoneyDisplay(perDay*20);
					}

					@Override
					public boolean go() {
						if (Player.player.askBuyMoney(perDay*20, "30 days")) {
							rentTime+=720;
							return true;
						}
						return false;
					}
				});
				list.add(new MenuBack("Cancel."));
				return list;
			}});
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}
	
	public void newCurAgent() {
		curAgent = town.getRandPersonableOccupant();
	}
	
	@Override
	public float occupantDesire() {
		return 5f;
	}

}
