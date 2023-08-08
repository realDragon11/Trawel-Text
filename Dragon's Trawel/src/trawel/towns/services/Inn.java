package trawel.towns.services;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.BarkManager;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent;
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

//sells booze which increases temp hp for a few fights,
//has a resident which changes with time
public class Inn extends Feature implements QuestBoardLocation{

	private static final long serialVersionUID = 1L;
	private byte resident;
	private double timePassed;
	private int wins = 0;
	private int nextReset;
	
	private int beerCount;
	private int beerCost;
	
	private boolean canQuest = true;
	
	public ArrayList<Quest> sideQuests = new ArrayList<Quest>();
	
	private transient Agent curAgent;

	
	private final static int RES_COUNT = 8;

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
		tutorialText = "Inns are a great place to buy beer and have various residents.";
		this.owner = owner;
		beerCount = extra.randRange(2,4);
		beerCost = tier +extra.randRange(0,2);
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
		Networking.setArea("inn");
		if (owner == Player.player && moneyEarned > 0) {
			extra.println("You take the " + moneyEarned + " in profits.");
			Player.player.addGold(moneyEarned);
			moneyEarned = 0;
		}
		Networking.sendStrong("Discord|imagesmall|inn|Inn|");
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "beer ("+getTown().getIsland().getWorld().moneyString(tier)+")";
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
						return "bathe (" +getTown().getIsland().getWorld().moneyString(tier*2)+")";
					}

					@Override
					public boolean go() {
						bathe();
						return false;
					}
				});
				if (town.getPersonableOccupants().count() >=2){
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "watch duel (" + extra.format(nextReset-timePassed+1) + " hours)";
					}

					@Override
					public boolean go() {
						occupantDuel(true);
						Player.addTime((nextReset-timePassed+1));
						mainGame.globalPassTime();
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
			moneyEarned +=tier*(nextReset/12);
			timePassed = 0;
			occupantDuel(false);
			resident = (byte)extra.randRange(1,RES_COUNT);
			nextReset = (extra.randRange(1,3)*12)+extra.randRange(0,5);
			if (canQuest) {this.generateSideQuest();}
		}
		return null;//TODO make inn time matter more
	}

	private void occupantDuel(boolean playerwatching) {
		List<SuperPerson> spList = new ArrayList<SuperPerson>();
		town.getPersonableOccupants().forEach(spList::add);
		if (spList.size() >= 2){
			SuperPerson sp1 = extra.randList(spList);
			spList.remove(sp1);
			SuperPerson sp2 = extra.randList(spList);
			if (!playerwatching) { extra.offPrintStack();}
			Combat c = mainGame.CombatTwo(sp1.getPerson(),sp2.getPerson(),town.getIsland().getWorld());
			town.removeAllKilled(c.killed);
			extra.changePrint(false);
			if (!playerwatching) { extra.popPrintStack();}
		}
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
				return "resident: " + curAgent.getPerson().getName()+ " (" +curAgent.getPerson().getLevel() +")";			
			}
		}
		return "ERROR";
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
						return extra.PRE_RED+"fight";
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
						return "chat";
					}

					@Override
					public boolean go() {
						if (extra.chanceIn(2,3)) {
							BarkManager.getBoast(Player.player.getPerson(),true);
						}else {
							BarkManager.getTaunt(Player.player.getPerson());
						}
						if (extra.chanceIn(2,3)) {
							BarkManager.getBoast(agent.getPerson(), true);	
						}else {
							BarkManager.getTaunt(agent.getPerson());
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
				Player.player.getPerson().addBeer(beerCount);
				moneyEarned +=beerCost;
				Player.player.addGold(beerCost);
			}
		}else {
			extra.println("You can't afford that! ("+World.currentMoneyDisplay(beerCost)+")");
		}
	}
	
	private void goOldFighter() {
		while (true) {
			extra.println("There's an old fighter here, at the inn.");
			extra.println("1 Leave");//DOLATER: fix menu
			extra.println("2 Chat with them");
			switch (extra.inInt(2)) {
			default: case 1: extra.println("You leave the fighter");return;
			case 2: extra.println("The old fighter turns and answers your greeting.");
			while (true) {
			extra.println("What would you like to ask about?");
			extra.println("1 tell them goodbye");
			extra.println("2 ask for a tip");
			extra.println("3 this inn");
			extra.println("4 "+extra.PRE_RED+" a duel");
			int in = extra.inInt(4);
			switch (in) {
				case 1: extra.println("They wish you well.") ;break;
				case 2: Oracle.tip("old");;break;
				case 3: extra.println("\"We are in " + this.getName() + ". It is pleasant here.\"");break;
				case 4: extra.println("You challenge the fighter!");
				Person p = RaceFactory.makeOld(tier+2);
				p.getBag().removeDrawBanes();
				mainGame.CombatTwo(Player.player.getPerson(),p);return;
			}
			if (in == 1) {
				break;
			}
			}
			}
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
		extra.println(extra.PRE_RED+"There is no resident, but there is room for a barfight... start one?");
		if (extra.yesNo()) {
			Combat c = Player.player.fightWith(RaceFactory.getDueler(tier));
			if (c.playerWon() > 0) {
				wins++;
			if (wins == 3) {
				Player.player.addTitle(this.getName() + " barfighter");
			}
			if (wins == 5) {
				Player.player.addTitle(this.getName() + " barbrewer");
			}
			if (wins == 10) {
				Player.player.addTitle(this.getName() + " barmaster");
			}
			
		}else {
			Person p = c.survivors.get(0);
			Agent a = new Agent(p);
			town.addOccupant(a);
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
	
	private void bathe() {
		if (Player.player.getGold() >= (tier)) {
			extra.println("Pay "+World.currentMoneyDisplay(tier)+" for a bath?");
			if (extra.yesNo()) {
				Player.player.getPerson().washAll();
				Player.player.addGold(-(tier));
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

}
