package rtrawel.battle;

import java.util.ArrayList;
import java.util.List;

import rtrawel.unit.RMonster;
import rtrawel.unit.RUnit;

public class Fight {
/**
 * used for storing fights
 */
	
	List<List<String>> foes = new ArrayList<List<String>>();
	public int counter = 1;
	public boolean go() {
		counter = 1;
		List<List<RUnit>> li = new ArrayList<List<RUnit>>();
		for (List<String> l: foes) {
			List<RUnit> list = new ArrayList<RUnit>();
			for (String s: l) {
				list.add(new RMonster(s,counter));
			}
			li.add(list);
		}
		Battle b = new Battle(Party.party.list,li);
		counter++;
		return b.go();
	}
	
	
	public void addFoes(String name, int amount) {
		List<String> l = new ArrayList<String>();
		for (int i = 0;i < amount;i++) {
			l.add(name);
		}
		foes.add(l);
	}
}
