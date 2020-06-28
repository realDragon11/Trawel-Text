package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Fight;
import rtrawel.battle.Party;
import trawel.extra;

public class Village {

	public List<Content> conts = new ArrayList<Content>();
	public String name = "";
	public static Menu menu = new Menu();
	public Roads r;
	
	private List<String> lootData = new ArrayList<String>();
	private List<Double> lootChance = new ArrayList<Double>();
	
	public List<Fight> spawns = new ArrayList<Fight>();
	public int wanderCombo = 0;
	
	/**
	 * adds the non-unique menu
	 */
	public Village(String name) {
		this.name = name;
		conts.add(menu);
		r = new Roads();
		conts.add(r);
	}

	public void doRandomBattle() {
		if (spawns.size() == 0) {
			extra.println("There are no monsters here.");
			return;
		}
		extra.randList(spawns).go();
		if (!Party.party.allDead()) {
			wanderCombo++;
		loot(Party.party.lootChance());}
	}
	
	public void addFight(Fight f) {
		spawns.add(f);
	}
	
	public boolean go() {
		int i = 1;
		extra.println(name);
		for (Content c: conts) {
			extra.println(i + " " + c.name());
			i++;
		}
		int in = extra.inInt(i-1);
		//i = 1;
		for (i = 0; i< conts.size();i++) {
			if (in == i+1) {
				return conts.get(i).go();
			}
		}
		return false;
	}
	
	public void addRoad(Connection e) {
		r.connects.add(e);
	}
	
	public void loot(double chance) {
		for (int i = 0; i < lootData.size();i++) {
			if (Math.random() < lootChance.get(i)*chance) {
				extra.println("You looted the " + lootData.get(i));
				Party.party.addItem(lootData.get(i),1);
				return;
			}
		}
	}

	public void addLoot(String string, double d) {
		lootData.add(string);
		lootChance.add(d);
	}
}
