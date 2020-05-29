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
	
	public List<Fight> spawns = new ArrayList<Fight>();
	
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
		extra.randList(spawns).go();
	}
	
	public void addFight(Fight f) {
		spawns.add(f);
	}
	
	public boolean go() {
		int i = 1;
		for (Content c: conts) {
			extra.println(i + " " + c.name());
			i++;
		}
		int in = extra.inInt(i-1);
		i = 1;
		for (Content c: conts) {
			if (in == i) {
				return c.go();
			}
			i++;
		}
		return false;
	}
	
	public void addRoad(Connection e) {
		r.connects.add(e);
	}
}
