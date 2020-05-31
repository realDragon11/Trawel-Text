package rtrawel.battle;

import java.util.ArrayList;
import java.util.List;

import rtrawel.EventFlag;
import rtrawel.unit.RMonster;
import rtrawel.unit.RUnit;

public class Fight {
/**
 * used for storing fights
 */
	
	List<List<String>> foes = new ArrayList<List<String>>();
	public String flag;
	public int counter = 1;
	public boolean go() {
		counter = 1;
		List<List<RUnit>> li = new ArrayList<List<RUnit>>();
		for (List<String> l: foes) {
			List<RUnit> list = new ArrayList<RUnit>();
			for (String s: l) {
				RMonster r = new RMonster(s,counter++);
				r.refresh();
				list.add(r);
			}
			li.add(list);
		}
		Battle b = new Battle(Party.party.getAlive(),li);
		if (b.go()) {
			if (flag != null) {
				EventFlag.eventFlag.setEF(flag,1);
			}
			return true;
		}
		return false;
	}
	
	
	public void addFoes(String name, int amount) {
		List<String> l = new ArrayList<String>();
		for (int i = 0;i < amount;i++) {
			l.add(name);
		}
		foes.add(l);
	}


	public void addFlag(String string) {
		flag = string;
		
	}
}
