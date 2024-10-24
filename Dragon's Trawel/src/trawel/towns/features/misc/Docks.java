package trawel.towns.features.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import derg.menus.MenuBack;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.battle.Combat;
import trawel.battle.Combat.SkillCon;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.mainGame;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.personal.NPCMutator;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.Connection;
import trawel.towns.data.TownFlavorFactory;
import trawel.towns.data.WorldGen;
import trawel.towns.data.Connection.ConnectClass;
import trawel.towns.data.FeatureData;
import trawel.towns.features.Feature;

public class Docks extends Feature {
	
	static {
		FeatureData.registerFeature(Docks.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" act as a "+TrawelColor.PRE_SHIP+"port"+TrawelColor.COLOR_RESET+" that has extended reach to "+TrawelColor.SERVICE_CURRENCY+"travel"+TrawelColor.COLOR_RESET+" with."
						+" Traveling to a far-flung port will require you to be near that destination's level or in a higher level area, and is blocked if Drudger armies control the "+fancyName()+". Traveling this way carries no risk of being ambushed."
						+" Townspeople reward help with "+TrawelColor.SERVICE_COMBAT+"defending or reclaiming "+TrawelColor.COLOR_RESET+fancyNamePlural()+" from Drudger armies."
						+" It is possible to "+TrawelColor.SERVICE_EXPLORE+"wander"+TrawelColor.COLOR_RESET+" at sea and "+TrawelColor.SERVICE_FLAVOR+"admire"+TrawelColor.COLOR_RESET+" secured "+fancyNamePlural()+".");
			}
			
			@Override
			public int priority() {
				return 50;
			}
			
			@Override
			public String name() {
				return "Docks";
			}
			
			@Override
			public String namePlural() {
				return name();//is always plural
			}
			
			@Override
			public String color() {
				return TrawelColor.PRE_SHIP;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.ADVANCED_SERVICES;
			}
		});
	}
	
	/**
	 * if the townspeople currently own it
	 */
	public boolean townOwned = true;
	
	/**
	 * will be an npc or drudger
	 */
	public Agent leader;
	
	/**
	 * townspeople
	 */
	public List<Person> old_defenders;
	
	/**
	 * list of drudgers, which might currently own it
	 */
	public List<Person> old_attackers;
	
	public double fightCooldownTimer = 0d;
	
	public Docks(String name,Town t) {
		town = t;
		this.name = name;
		tier = Math.max(2,1+t.getTier());//min level of 2, but tries to be one higher
		old_defenders = new ArrayList<Person>();
		old_attackers = new ArrayList<Person>();
	}
	
	@Override
	public String nameOfType() {
		return "Docks";
	}
	
	@Override
	public Area getArea() {
		return Area.PORT;
	}
	
	@Override
	public boolean canShow() {
		return false;//displayed higher in the menu another way
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		fightCooldownTimer-=time;
		//defenders and attackers that get too high level should start wandering around instead
		//should probably also apply to town leaders, but not drudger leaders
		//both for flavor and because the player can't fight town leaders rn so they'll get stuck
		if (fightCooldownTimer < -24*6) {//if it's been 6 days, starts happening automatically
			battle(null,null);
			int leaderLevel = leader == null ? 0 :leader.getPerson().getLevel();
			for (int i = old_attackers.size()-1;i>=0;i--) {
				int mylevel = old_attackers.get(i).getLevel();
				if (mylevel >= tier+2) {//if more than two levels higher
					if (!townOwned) {//if drudgers (current loop) owns
						if (mylevel > leaderLevel) {//can take over
							//always attempt to now
							if (leader == null) {
								leaderLevel = mylevel;
								leader = old_attackers.remove(i).setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
								continue;
							}else {
								Person me = old_attackers.remove(i);
								Print.offPrintStack();
								Combat c = leader.fightWith(me);
								Print.popPrintStack();
								leader = c.getNonSummonSurvivors().get(0).setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
								//may the best drudger lead!
								Person p = leader.getPerson();
								//promote if was mook
								if (p.getFlag(PersonFlag.IS_MOOK)) {
									NPCMutator.mutateHonorStockDrudger(p);
								}
								
								continue;
							}
						}
						//fall through
					}
					Person me = old_attackers.remove(i);
					town.addOccupant(me.setOrMakeAgentGoal(AgentGoal.NONE));
				}
			}
			Person potentialLeader = null;
			if (townOwned) {
				if (leader != null) {
					potentialLeader = leader.getPerson();
					if (potentialLeader.getLevel() >= tier+2) {//less likely to leave than non-leader townies
						//we know this has a superperson, but we want to set the agent goal, and this wraps that fluently
						town.addOccupant(potentialLeader.setOrMakeAgentGoal(AgentGoal.NONE));
						potentialLeader = null;
						leader = null;
					}
				}
			}
			for (int i = old_defenders.size()-1;i>=0;i--) {
				int mylevel = old_defenders.get(i).getLevel();
				if (mylevel >= tier+1 && Rand.chanceIn(1,3)) {
					Person me = old_defenders.remove(i);
					town.addOccupant(me.setOrMakeAgentGoal(AgentGoal.NONE));
					continue;
				}
				if (potentialLeader == null || mylevel > potentialLeader.getLevel()) {
					potentialLeader = old_defenders.get(0);
				}
			}
			if (townOwned && potentialLeader != null) {
				if (leader == null || potentialLeader != leader.getPerson()) {
					if (leader != null) {
						leader.onlyGoal(AgentGoal.NONE);
						town.addOccupant(leader);
					}
					old_defenders.remove(potentialLeader);//remove if present
					leader = potentialLeader.setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
				}
			}
		}
		return null;
	}

	@Override
	public void go() {
		List<Connection> connects = new ArrayList<Connection>();
		town.getConnects().stream().filter(c -> c.getType().type == ConnectClass.SEA).forEach(connects::add);
		Input.menuGo(new ScrollMenuGenerator(connects.size(),"n/a","n/a") {

			@Override
			public List<MenuItem> header() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				
				
				if (leader != null) {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "Owner: " + leader.getPerson().getName() + " ("+leader.getPerson().getLevel()+")";
						}});
				}
				
				//variable defenses
				if (fightCooldownTimer > 0) {
					if (townOwned) {
						//no need to defend
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.SERVICE_FLAVOR+"Admire Port.";
							}

							@Override
							public boolean go() {
								Print.println("The Docks are currently clear of invaders, so you gaze out over the town.");
								TrawelTime.addPassTime(Rand.randRange(.1f,.3f));
								TownFlavorFactory.go(.1f,town);
								return false;
							}});
					}else {
						//can't defend yet
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.PRE_BATTLE
										+"Wait and then Take Back Port ("
										+Print.F_TWO_TRAILING.format(fightCooldownTimer+1)+" hours)";
							}

							@Override
							public boolean go() {
								Player.addTime(fightCooldownTimer+1);
								TrawelTime.globalPassTime();
								defend();
								if (!townOwned) {
									return true;//kick out, like nodes
								}
								return false;
							}});
					}
				}else {
					if (townOwned) {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.PRE_BATTLE+"Defend Port!";
							}

							@Override
							public boolean go() {
								defend();
								if (!townOwned) {
									return true;//kick out, like nodes
								}
								return false;
							}});
					}else {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.PRE_BATTLE+"Reclaim Port!";
							}

							@Override
							public boolean go() {
								defend();
								if (!townOwned) {
									return true;//kick out, like nodes
								}
								return false;
							}});
					}
				}
				
				ScrollMenuGenerator gen = farConnectsMenu();
				
				if (gen != null) {
					if (townOwned) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Travel to Far Flung Ports.";
						}

						@Override
						public boolean go() {
							Input.menuGo(gen);
							if (Player.player.getLocation() != town) {
								return true;//if we moved
							}
							return false;
						}});
					}else {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.RESULT_WARN+"Travel Blocked.";
							}

							@Override
							public boolean go() {
								Print.println("The docks are Blockaded, and you cannot take a long route.");
								return false;
							}});
					}//annoying that this will make the first option not far flung, but meh
				}
				return list;
			}

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				//used for nodes since this is a lot easier
				list.add(town.getConnectMenu(connects.get(i)));
				return list;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.PRE_MAYBE_BATTLE+"Sail Aimlessly";
					}

					@Override
					public boolean go() {
						TrawelTime.addPassTime(Rand.randRange(.3f,.7f));
						if (!town.dockWander(false)) {
							//attempt a flavor event
							if (!TownFlavorFactory.go(.2f,town)) {
								Print.println("Nothing interesting happens.");
							}
						}
						return false;
					}});
				list.add(new MenuBack("Leave."));
				return list;
			}});
	}
	
	public List<Town> farConnectsList(){
		List<Town> tList = new ArrayList<Town>();
		List<Town> openSet = new ArrayList<Town>();
		Set<Town> closedSet = new HashSet<Town>();
		
		town.getConnects().stream().filter(c -> c.getType().type == ConnectClass.SEA).forEachOrdered(c -> openSet.add(c.otherTown(town)));
		//tList.addAll(openSet);
		//we could add all adjacent towns to possible locations, but this makes seeing if we found any easier if
		//we decide that we won't show close-flung ports
		//openSet.add(town);//add this town to
		closedSet.add(town);//add our town to what we explored
		//int localSize = openSet.size();//this is why we don't just start from our town, we want to know if we actually
		//found any non-far-flung ports to bother with
		
		while (!openSet.isEmpty()) {
			Town cur = openSet.remove(0);
			for (Connection c: cur.getConnects()) {
				if (c.getType().type != ConnectClass.SEA) {
					continue;//not our type
				}
				Town other = c.otherTown(cur);
				if (!openSet.contains(other) && !closedSet.contains(other)) {
					tList.add(other);
					openSet.add(other);
				}
			}
			closedSet.add(cur);
		}
		//assert localSize <= closedSet.size();
		return tList;
	}
	
	/**
	 * lets the player skip along if they are high enough level to get somewhere
	 * <br>
	 * after leaving menu, always check to see if you're still in the same town
	 * <br>
	 * note that getting back might take more effort if the place doesn't have docks, only ports
	 */
	private ScrollMenuGenerator farConnectsMenu() {
		List<Town> tList = farConnectsList();
		if (tList.size() == 0) {
			return null;//no far flung ports
		}
		
		List<Double> timeList = new ArrayList<Double>();
		for (Town t: tList) {
			List<Connection> moves = WorldGen.aStarTown(town,t);
			double total = 0;
			for (Connection c: moves) {
				total += c.getTime();
			}
			timeList.add(total);
		}
			
		
		return new ScrollMenuGenerator(tList.size(),"previous <> ports","next <> ports") {
			
			@Override
			public List<MenuItem> header() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "You ponder far flung ports:";
					}});
				return list;
			}
			
			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				Town t = tList.get(i);
				//can go up to one level higher, and always go backwards from higher level areas to lower level ones
				final boolean outOfScoped = t.getTier() > Player.player.getPerson().getLevel()+1 && t.getTier() > getLevel();
				//cost is the uneffective level of the destination and this feature, minus one, then rounded to an int
				//minimum dock level is 2 which is 1.3x, minimum town is 1 which is 1.1x
				final int cost = Math.round((IEffectiveLevel.unclean(t.getTier())+getUnEffectiveLevel()-1.4f));
				final int playerGold = Player.player.getGold();
				final boolean canAfford = playerGold >= cost;
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return t.displayLine(Docks.this.town) +" [pay_time]fee: "+cost+" [clear]("+(outOfScoped ? ""+TrawelColor.TIMID_RED+"Out of Scope[clear]" : TrawelColor.SERVICE_TIME+Connection.displayTime(timeList.get(i))) + "[clear])";
					}

					@Override
					public boolean go() {
						// needs to be clickable even if can't go, for scroll reasons
						if (outOfScoped) {
							//not really a good excuse at this point
							Print.println("[r_error]The journey would be too dangerous!");
							return false;
						}
						if (!canAfford) {
							Print.println("[r_error]You can't afford that! (Have "+playerGold+", need "+cost+".)");
						}
						Player.player.addGold(-cost);
						//can either take naive time or use a*
						if (mainGame.displayTravelText) {
							Print.println("[r_pass]You pay "+World.currentMoneyDisplay(cost)+" and start the voyage to "+t.getName()+".");
						}
						Player.addTime(timeList.get(i));
						TrawelTime.globalPassTime();
						Player.player.setLocation(t);
						//town.dockWander(true);//no wander chance since that's the point of payment
						if (mainGame.displayTravelText) {
							Print.println("[r_pass]You arrive in "+t.getName()+".");
						}
						if (mainGame.displayFlavorText) {
							//attempt to display some flavor for the town you are arriving in
							TownFlavorFactory.go(.2f,t);
						}
						return true;
					}});
				return list;
			}
			
			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuBack("back"));
				return list;
			}
		};
	}

	private List<Person> popTownie() {
		if (old_defenders.size() > 0) {
			return old_defenders.remove(0).getSelfOrAllies();
		}
		Person p = RaceFactory.getDueler(tier-1);
		p.getBag().addLocalGoldIf(2);//so looting always has enough gold to divide up, must be 2 because of player
		return p.getSelfOrAllies();
	}
	
	private List<Person> popDrudger() {
		if (old_attackers.size() > 0) {
			return old_attackers.remove(0).getSelfOrAllies();
		}
		Person stock = RaceFactory.makeDrudgerStock(tier-1);
		stock.setFlag(PersonFlag.IS_MOOK,true);
		return stock.getSelfOrAllies();
	}
	
	private List<Person> makeDrudgerLeader() {
		if (old_attackers.size() > 0) {
			Person p = old_attackers.remove(0);
			if (p.getFlag(PersonFlag.IS_MOOK)) {
				NPCMutator.mutateHonorStockDrudger(p);
			}
			return p.getSelfOrAllies();
		}
		if (Rand.chanceIn(1,3)) {
			return RaceFactory.makeDrudgerMage(tier).getSelfOrAllies();
		}else {
			return RaceFactory.makeDrudgerTitan(tier).getSelfOrAllies();
		}
	}

	/**
	 * 
	 * @return true if battle occurred
	 */
	public boolean defend() {
		if (fightCooldownTimer > 0) {
			if (townOwned) {
				Print.println("The port isn't going to be attacked soon.");
			}else {
				Print.println("The port is too overrun right now to take back.");
			}
			return false;
		}
		if (Player.player.getPerson().getLevel() >= tier) {
			boolean oldOwned = townOwned;
			if (oldOwned) {
				Print.println("You help defend the port against the drudger onslaught.");
			}else {
				Print.println("You help defend try to take back the port from the drudger occupying army.");
			}
			Combat c = battle(Player.player.getAllies(),null);
			Player.addTime(3);//3 hour battle
			if (townOwned) {
				if (oldOwned == townOwned) {
					Print.println(TrawelColor.RESULT_NO_CHANGE_GOOD+"The docks are safe.");
				}else {
					Print.println(TrawelColor.RESULT_GOOD+"You took back the docks!");
				}
				if (c.playerWon() > 1) {//you must survive to get paid
					Networking.unlockAchievement("docks_survive");
					town.helpCommunity(1);
					int reward = (int) ((8*getUnEffectiveLevel())+Rand.randRange(0,4));
					Print.println("They pay you with "+World.currentMoneyDisplay(reward)+".");
					Player.player.addGold(reward);
				}
			}else {
				if (!oldOwned) {
					Print.println(TrawelColor.RESULT_NO_CHANGE_BAD+"The docks remain under drudger control...");
				}else {
					if (leader != null) {
						Print.println(TrawelColor.RESULT_BAD+leader.getPerson().getName() +"'s drudger army takes over the docks!");
					}else {
						Print.println(TrawelColor.RESULT_BAD+"The docks are overrun by the drudger force.");
					}
				}
			}
			return false;
		}else {
			Print.println(TrawelColor.RESULT_ERROR+"They don't think you capable of helping.");
			return false;
		}
	}
	
	public Combat battle(List<Person> addForTown, List<Person> addForDrudger) {
		boolean playerin = true;
		if (addForTown == null || !addForTown.contains(Player.player.getPerson())) {
			playerin = false;
			Print.offPrintStack();
		}
		int addSize = Rand.randRange(1,3);
		/*int addTownSize = addSize;
		int addSeaSize = addSize;*/
		List<Person> allyList = new ArrayList<Person>();
		List<Person> foeList = new ArrayList<Person>();
		if (addForTown != null) {
			allyList.addAll(addForTown);
		}
		if (addForDrudger != null) {
			foeList.addAll(addForDrudger);
		}
		
		boolean addOwnAttackLeader = true;
		if (leader != null) {
			if (townOwned) {
				//defense leader
				allyList.addAll(leader.getAllies());
			}else {
				foeList.addAll(leader.getAllies());//army leader
				addOwnAttackLeader = false;
			}
		}
		if (addOwnAttackLeader) {
			foeList.addAll(makeDrudgerLeader());
		}
		
		addSize = Math.max(addSize,3-Math.max(foeList.size(),allyList.size()));//minimum of 3 people on each side
		//minimum adds
		for (int i = addSize-1;i>=0;i--) {
			allyList.addAll(popTownie());
			foeList.addAll(popDrudger());
		}
		while (true) {
			int allySize = allyList.size();
			int foeSize = foeList.size();
			if (allySize > foeSize) {
				foeList.addAll(popDrudger());
			}else {
				if (foeSize > allySize) {
					allyList.addAll(popTownie());
				}else {
					break;//balanced
				}
			}
		}
		List<List<Person>> listlist = new ArrayList<List<Person>>();
		listlist.add(allyList);
		listlist.add(foeList);

		leader = null;//to prevent a leader from swapping sides or other oddness where they die
		
		List<List<SkillCon>> skillconlistlist = new ArrayList<List<SkillCon>>();
		Combat.numberSkillConLists(skillconlistlist);
		Combat c = Combat.HugeBattle(town.getIsland().getWorld(),town.getPassiveSkillCons(0),listlist,true);
		boolean townWon = c.getVictorySide() == 0;

		if (townWon) {
			c.getNonSummonSurvivors().stream().filter(p -> !p.isPlayer()).forEach(old_defenders::add);
			fightCooldownTimer = Rand.getRand().nextDouble(100,130);//over 4 days roughly
			
			Person highest = null;
			for (Person p: old_defenders) {
				if (highest == null || p.getLevel() > highest.getLevel()) {
					highest = p;
				}
			}
			if (highest != null) {
				leader = highest.getMakeAgent(AgentGoal.OWN_SOMETHING);
				old_defenders.remove(highest);
			}
			if (!townOwned) {//reclaimed
				townOwned = true;
			}//else just defended
			assert leader == null || leader.isPersonable();
		}else {//drudger victory
			List<Person> alive = c.getNonSummonSurvivors();
			Person wleader = alive.stream()
					.filter(p -> !p.getFlag(PersonFlag.IS_MOOK) && !p.getFlag(PersonFlag.IS_SUMMON))
					.findAny().orElse(null);
			if (wleader == null) {
				fightCooldownTimer = Rand.getRand().nextDouble(10,14);//12 hours roughly
				old_attackers.addAll(alive);
				if(townOwned){//taken over
					townOwned = false;
				}else {//held
					
				}
			}else {
				fightCooldownTimer = Rand.getRand().nextDouble(36,48);//1.5-2 days roughly
				leader = wleader.setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
				alive.remove(wleader);
				old_attackers.addAll(alive);
				if(townOwned){//taken over
					townOwned = false;
				}else {//held
					
				}
			}
			assert leader == null || !leader.isPersonable();
		}
		/*
		assert Combat.hasNonNullBag(old_defenders);
		assert Combat.hasNonNullBag(old_attackers);*/
		if (playerin == false) {
			Print.popPrintStack();
		}
		return c;
	}
}
