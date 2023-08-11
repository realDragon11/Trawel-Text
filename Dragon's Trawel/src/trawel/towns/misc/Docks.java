package trawel.towns.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.Person.PersonFlag;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Connection;
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
	public SuperPerson leader;
	
	/**
	 * townspeople
	 */
	public List<Person> old_defenders;
	
	/**
	 * list of drudgers, which might currently own it
	 */
	public List<Person> old_attackers;
	
	public double fightCooldownTimer = 0d;
	
	public Docks(Town t) {
		town = t;
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		fightCooldownTimer-=time;
		// TODO Auto-generated method stub
		//TODO: defenders and attackers that get too high level should start wandering around instead
		//should probably also apply to town leaders, but not drudger leaders
		//both for flavor and because the player can't fight town leaders rn so they'll get stuck
		if (fightCooldownTimer < -24*6) {//if it's been 6 days, starts happening automatically
			battle(null,null);
		}
		return null;
	}

	@Override
	public void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				// TODO Auto-generated method stub
				return null;
			}});
	}
	
	/**
	 * lets the player skip along if they are high enough level to get somewhere
	 * <br>
	 * after leaving menu, always check to see if you're still in the same town
	 */
	private ScrollMenuGenerator farConnectsMenu() {
		List<Town> tList = new ArrayList<Town>();
		List<Town> openSet = new ArrayList<Town>();
		Set<Town> closedSet = new HashSet<Town>();
		
		openSet.add(town);
		while (openSet.size() > 0) {
			Town cur = openSet.remove(0);
			for (Connection c: cur.getConnects()) {
				Town other = c.otherTown(cur);
				if (!closedSet.contains(other)) {
					tList.add(other);
					openSet.add(other);
				}
			}
		}
		final List<Town> finishedList = tList;
		return new ScrollMenuGenerator(finishedList.size(),"previous <> ports","next <> ports") {
			
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
				Town t = finishedList.get(i);
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return t.displayLine(Docks.this.town) + (t.getTier() > Player.player.getPerson().getLevel() ? " ("+extra.TIMID_RED+"Out of Scope)" : "");
					}

					@Override
					public boolean go() {
						// needs to be clickable even if can't go, for scroll reasons
						if (t.getTier() > Player.player.getPerson().getLevel()) {
							extra.println("Too high level!");
							return false;
						}
						Player.player.setLocation(t);
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

	@Override
	public String getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Person> popTownie() {
		if (old_defenders.size() > 0) {
			return old_defenders.remove(0).getSelfOrAllies();
		}
		return RaceFactory.getDueler(tier-1).getSelfOrAllies();
	}
	
	private List<Person> popDrudger() {
		if (old_defenders.size() > 0) {
			return old_defenders.remove(0).getSelfOrAllies();
		}
		Person stock = RaceFactory.makeDrudgerStock(tier-1);
		stock.setFlag(PersonFlag.IS_MOOK,true);
		return stock.getSelfOrAllies();
	}

	/**
	 * 
	 * @return true if battle occurred
	 */
	public boolean defend() {
		if (fightCooldownTimer > 0) {
			boolean oldOwned = townOwned;
			if (oldOwned) {
				extra.println("The port isn't going to be attacked soon.");
			}else {
				extra.println("The port is too overrun right now to take back.");
			}
			battle(Player.player.getAllies(),null);
			if (townOwned) {
				
			}else {
				if (oldOwned == townOwned) {
					
				}else {
					if (leader != null) {
						extra.println(leader.getPerson().getName() +"'s drudger army takes over the docks!");
					}else {
						extra.println("The docks are overrun by the drudger force.");
					}
				}
			}
			
			int reward = (10*tier)+extra.randRange(0,4);
			extra.println("You take back the docks. They pay you with "+World.currentMoneyDisplay(reward)+".");
			Player.player.addGold(reward);
			return false;
		}else {
			if (Player.player.getPerson().getLevel() >= tier) {
				extra.println("You help defend the port against the drudger onslaught.");
				
				Player.addTime(3);//3 hour battle
				return true;
			}else {
				extra.println("They don't think you capable of helping.");
				return false;
			}
		}
	}
	
	public void battle(List<Person> addForTown, List<Person> addForDrudger) {
		int addSize = extra.randRange(1,2);
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
			foeList.add(RaceFactory.makeDrudgerTitan(tier));
		}
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

		Combat c = mainGame.HugeBattle(town.getIsland().getWorld(), listlist);
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
			}
			if (!townOwned) {//reclaimed
				townOwned = true;
			}//else just defended
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
				fightCooldownTimer = extra.getRand().nextDouble(20,28);//2 days roughly
				leader = wleader.setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
				alive.remove(wleader);
				old_attackers.addAll(alive);
				if(townOwned){//taken over
					townOwned = false;
				}else {//held
					
				}
			}

		}
	}
}
