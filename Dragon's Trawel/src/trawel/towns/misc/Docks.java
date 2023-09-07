package trawel.towns.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import derg.menus.MenuBack;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.Networking.Area;
import trawel.WorldGen;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.battle.Combat.SkillCon;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Archetype;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Connection;
import trawel.towns.Connection.ConnectType;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class Docks extends Feature {
	
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
		tier = Math.min(2,1+t.getTier());//min level of 2, but tries to be one higher
		old_defenders = new ArrayList<Person>();
		old_attackers = new ArrayList<Person>();
		area_type = Area.PORT;
	}
	
	@Override
	public String getTutorialText() {
		return "Docks.";
		//return "Docks are large ports, able to transport you across water. You can also defend them or travel to far-flung ports.";
	}
	
	@Override
	public boolean canShow() {
		return false;//displayed higher in the menu another way
	}
	
	@Override
	public String getColor() {
		return extra.PRE_SHIP;
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
								leader = old_attackers.get(i).setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
								continue;
							}else {
								Person me = old_attackers.remove(i);
								extra.offPrintStack();
								Combat c = leader.fightWith(me);
								extra.popPrintStack();
								leader = c.getNonSummonSurvivors().get(0).setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
								//may the best drudger lead!
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
				if (mylevel >= tier+1 && extra.chanceIn(1,3)) {
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
		town.getConnects().stream().filter(c -> c.getType() == ConnectType.SHIP).forEach(connects::add);
		extra.menuGo(new ScrollMenuGenerator(connects.size(),"n/a","n/a") {

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
				
				//on top so it's consistent
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_MAYBE_BATTLE+"Sail Aimlessly";
					}

					@Override
					public boolean go() {
						if (!town.dockWander(false)) {
							extra.println("Nothing interesting happens.");
						}
						return false;
					}});
				
				ScrollMenuGenerator gen = farConnectsMenu();
				
				if (gen != null) {
					if (townOwned) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Travel to Far Flung Ports";
						}

						@Override
						public boolean go() {
							extra.menuGo(gen);
							if (Player.player.getLocation() != town) {
								return true;//if we moved
							}
							return false;
						}});
					}else {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "The docks are Blockaded, and you cannot take a long route.";
							}});
					}//annoying that this will make the first option not far flung, but meh
				}
				
				//variable defenses
				if (fightCooldownTimer > 0) {
					if (townOwned) {
						//no need to defend
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "The Docks are currently clear of invaders.";
							}});
					}else {
						//can't defend yet
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return extra.PRE_BATTLE
										+"Wait and then Take Back Port ("
										+extra.F_TWO_TRAILING.format(fightCooldownTimer+1)+" hours)";
							}

							@Override
							public boolean go() {
								Player.addTime(fightCooldownTimer+1);
								mainGame.globalPassTime();
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
								return extra.PRE_BATTLE+"Defend Port";
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
								return extra.PRE_BATTLE+"Reclaim Port";
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
				return Collections.singletonList(new MenuBack());
			}});
	}
	
	/**
	 * lets the player skip along if they are high enough level to get somewhere
	 * <br>
	 * after leaving menu, always check to see if you're still in the same town
	 * <br>
	 * note that getting back might take more effort if the place doesn't have docks, only ports
	 */
	private ScrollMenuGenerator farConnectsMenu() {
		List<Town> tList = new ArrayList<Town>();
		List<Town> openSet = new ArrayList<Town>();
		Set<Town> closedSet = new HashSet<Town>();
		
		town.getConnects().stream().forEachOrdered(c -> openSet.add(c.otherTown(town)));
		//tList.addAll(openSet);
		//we could add all adjacent towns to possible locations, but this makes seeing if we found any easier if
		//we decide that we won't show close-flung ports
		//openSet.add(town);//add this town to
		closedSet.add(town);//add our town to what we explored
		//int localSize = openSet.size();//this is why we don't just start from our town, we want to know if we actually
		//found any non-far-flung ports to bother with
		
		while (openSet.size() > 0) {
			Town cur = openSet.remove(0);
			for (Connection c: cur.getConnects()) {
				if (c.getType() != ConnectType.SHIP) {
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
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return t.displayLine(Docks.this.town) + " ("+(t.getTier() > Player.player.getPerson().getLevel() ? ""+extra.TIMID_RED+"Out of Scope" : Connection.displayTime(timeList.get(i))) + ")";
					}

					@Override
					public boolean go() {
						// needs to be clickable even if can't go, for scroll reasons
						if (t.getTier() > Player.player.getPerson().getLevel()) {
							extra.println("Too high level!");
							return false;
						}
						//can either take naive time or use a*
						
						Player.addTime(timeList.get(i));
						mainGame.globalPassTime();
						Player.player.setLocation(t);
						town.dockWander(true);
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
			p.setFlag(PersonFlag.IS_MOOK, false);
			p.setArch(Archetype.PROMOTED);
			return p.getSelfOrAllies();
		}
		if (extra.chanceIn(1,3)) {
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
				extra.println("The port isn't going to be attacked soon.");
			}else {
				extra.println("The port is too overrun right now to take back.");
			}
			return false;
		}
		if (Player.player.getPerson().getLevel() >= tier) {
			boolean oldOwned = townOwned;
			if (oldOwned) {
				extra.println("You help defend the port against the drudger onslaught.");
			}else {
				extra.println("You help defend try to take back the port from the drudger occupying army.");
			}
			Combat c = battle(Player.player.getAllies(),null);
			Player.addTime(3);//3 hour battle
			if (townOwned) {
				if (oldOwned == townOwned) {
					extra.println(extra.RESULT_NO_CHANGE_GOOD+"The docks are safe.");
				}else {
					extra.println(extra.RESULT_GOOD+"You took back the docks!");
				}
				if (c.playerWon() > 1) {//you must survive to get paid
					int reward = (int) ((8*getUnEffectiveLevel())+extra.randRange(0,4));
					extra.println("They pay you with "+World.currentMoneyDisplay(reward)+".");
					Player.player.addGold(reward);
				}
			}else {
				if (!oldOwned) {
					extra.println(extra.RESULT_NO_CHANGE_BAD+"The docks remain under drudger control...");
				}else {
					if (leader != null) {
						extra.println(extra.RESULT_BAD+leader.getPerson().getName() +"'s drudger army takes over the docks!");
					}else {
						extra.println(extra.RESULT_BAD+"The docks are overrun by the drudger force.");
					}
				}
			}
			return false;
		}else {
			extra.println("They don't think you capable of helping.");
			return false;
		}
	}
	
	public Combat battle(List<Person> addForTown, List<Person> addForDrudger) {
		boolean playerin = true;
		if (addForTown == null || !addForTown.contains(Player.player.getPerson())) {
			playerin = false;
			extra.offPrintStack();
		}
		int addSize = extra.randRange(1,3);
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
		Combat c = mainGame.HugeBattle(town.getIsland().getWorld(),town.getPassiveSkillCons(0),listlist,true);
		boolean townWon = c.getVictorySide() == 0;

		if (townWon) {
			c.getNonSummonSurvivors().stream().filter(p -> !p.isPlayer()).forEach(old_defenders::add);
			fightCooldownTimer = extra.getRand().nextDouble(100,130);//over 4 days roughly
			
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
				fightCooldownTimer = extra.getRand().nextDouble(10,14);//12 hours roughly
				old_attackers.addAll(alive);
				if(townOwned){//taken over
					townOwned = false;
				}else {//held
					
				}
			}else {
				fightCooldownTimer = extra.getRand().nextDouble(36,48);//1.5-2 days roughly
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
			extra.popPrintStack();
		}
		return c;
	}
}
