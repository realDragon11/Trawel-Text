package trawel.towns.services;

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
import trawel.battle.BarkManager;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.BasicSideQuest;
import trawel.quests.QBMenuItem;
import trawel.quests.QRMenuItem;
import trawel.quests.Quest;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;
import trawel.towns.fight.ExploreFeature;

//sells booze which increases temp hp for a few fights,
//has a resident which changes with time
public class Inn extends Feature implements QuestBoardLocation{

	private static final long serialVersionUID = 1L;
	private byte resident;
	private double timePassed;
	private int wins = 0;
	private double nextReset;
	
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
		try {
			while (sideQuests.size() < 3) {
				generateSideQuest();
			}
			}catch (Exception e) {
				canQuest = false;
			}
	}
	
	private void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(extra.randList(sideQuests));
		}
		BasicSideQuest bsq = BasicSideQuest.getRandomSideQuest(this.getTown(),this);
		if (bsq != null) {
		sideQuests.add(bsq);
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
						return "beer ("+getTown().getIsland().getWorld().moneyString(beerCost)+")";
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "backroom";
					}

					@Override
					public boolean go() {
						backroom();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "bathe (" +getTown().getIsland().getWorld().moneyString(bathPrice())+")";
					}

					@Override
					public boolean go() {
						bathe();
						return false;
					}
				});
				if (town.getPersonableOccupants().limit(2).count() >=2){
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "watch duel (" + extra.format(nextReset-timePassed+1) + " hours)";
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
				mList.add(new MenuBack("leave"));
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
				for (QuestR qr: qrList) {
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
			if (!playerwatching) { extra.popPrintStack();}
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
		case 1: return "resident: A group of old fighters";
		case 2: return "resident: A group of dancers";
		case 3: return "resident: An oracle.";
		default:
			if (curAgent == null || !town.getPersonableOccupants().anyMatch(a -> a == curAgent)){
				if (town.getPersonableOccupants().count() == 0) {
					return "resident: Open Bar";
				}else {
					newCurAgent();
				}
			}
			return "resident: " + curAgent.getPerson().getName()+ " (" +curAgent.getPerson().getLevel() +")";	
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
						Combat c = Player.player.fightWith(agent.getPerson());
						if (c.playerWon() > 0) {
							town.removeOccupant(agent);
							newCurAgent();
						}
						return true;
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
		new Oracle("inn",tier).go();
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
	
	private int bathPrice() {
		return (int) Math.ceil(2*getUnEffectiveLevel());
	}
	
	private void bathe() {
		if (Player.player.getGold() >= (bathPrice())) {
			extra.println("Pay "+World.currentMoneyDisplay(bathPrice())+" for a bath?");
			if (extra.yesNo()) {
				Player.player.getPerson().washAll();
				Player.player.addGold(-bathPrice());
			}
			}else {
				extra.println("You can't afford that!");
			}
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
