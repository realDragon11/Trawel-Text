package trawel.towns.misc;

import java.util.ArrayList;
import java.util.List;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void go() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	private Person popTownie() {
		if (old_defenders.size() > 0) {
			return old_defenders.remove(0);
		}
		return RaceFactory.getDueler(tier-1);
	}
	
	private Person popDrudger() {
		if (old_defenders.size() > 0) {
			return old_defenders.remove(0);
		}
		Person stock = RaceFactory.makeDrudgerStock(tier-1);
		stock.setFlag(PersonFlag.IS_MOOK,true);
		return stock;
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
		}else {
			if (Player.player.getPerson().getLevel() >= tier) {
				extra.println("You help defend the port against the drudger onslaught.");
				int addSize = extra.randRange(1,2);
				/*int addTownSize = addSize;
				int addSeaSize = addSize;*/
				List<Person> allyList = new ArrayList<Person>();
				List<Person> foeList = new ArrayList<Person>();
				allyList.add(Player.player.getPerson());
				boolean addOwnAttackLeader = true;
				if (leader != null) {
					if (townOwned) {
						//defense leader
						allyList.add(leader.getPerson());
					}else {
						foeList.add(leader.getPerson());//army leader
						addOwnAttackLeader = false;
					}
				}
				if (addOwnAttackLeader) {
					foeList.add(RaceFactory.makeDrudgerTitan(tier));
				}
				//minimum adds
				for (int i = addSize-1;i>=0;i--) {
					allyList.add(popTownie());
					foeList.add(popDrudger());
				}
				while (true) {
					int allySize = allyList.size();
					int foeSize = foeList.size();
					if (allySize > foeSize) {
						foeList.add(popDrudger());
					}else {
						if (foeSize > allySize) {
							allyList.add(popTownie());
						}else {
							break;//balanced
						}
					}
				}
				/*
				for (;addTownSize >=0;addTownSize--) {
					allyList.add(popTownie());
				}
				for (;addSeaSize >= 0;addSeaSize--) {
					foeList.add(popDrudger());
				}*/
				

				List<List<Person>> listlist = new ArrayList<List<Person>>();
				listlist.add(allyList);
				listlist.add(foeList);

				Combat c = mainGame.HugeBattle(town.getIsland().getWorld(), listlist);
				boolean pass = c.playerWon() > 0;

				if (pass) {
					c.getNonSummonSurvivors().stream().filter(p -> !p.isPlayer()).forEach(old_defenders::add);
					int reward = (10*tier)+extra.randRange(0,4);
					extra.println("You take back the docks. They pay you with "+World.currentMoneyDisplay(reward)+".");
					Player.player.addGold(reward);
					fightCooldownTimer = 24*4;//4 days
					
					Person highest = null;
					for (Person p: old_defenders) {
						if (highest == null || p.getLevel() > highest.getLevel()) {
							highest = p;
						}
					}
					if (highest != null) {
						leader = highest.getMakeAgent(AgentGoal.OWN_SOMETHING);
					}
				}else {
					List<Person> alive = c.getNonSummonSurvivors();
					Person wleader = alive.stream()
							.filter(p -> !p.getFlag(PersonFlag.IS_MOOK) && !p.getFlag(PersonFlag.IS_SUMMON))
							.findAny().orElse(null);
					/*
					Person wleader = c.streamAllSurvivors()
							.filter(p -> !p.getFlag(PersonFlag.IS_MOOK) && !p.getFlag(PersonFlag.IS_SUMMON))
							.findAny().orElse(null);*/
					if (wleader == null) {
						fightCooldownTimer = 12;//12 hours
						extra.println("The docks are overrun by the drudger force.");
						old_attackers.addAll(alive);
					}else {
						fightCooldownTimer = 48;//2 days
						extra.println(wleader.getName() +"'s army takes over the docks!");
						leader =  wleader.getMakeAgent(AgentGoal.OWN_SOMETHING);
						
						alive.remove(wleader);
						old_attackers.addAll(alive);
					}

				}
				Player.addTime(3);//3 hour battle
				return true;
			}else {
				extra.println("They don't think you capable of helping.");
				return false;
			}
		}
	}
}
