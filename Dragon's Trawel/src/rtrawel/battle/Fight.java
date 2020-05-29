package rtrawel.battle;

import java.util.ArrayList;
import java.util.List;

import rtrawel.unit.RMonster;
import rtrawel.unit.RUnit;

public class Fight {
/**
 * used for storing fights
 */
	
	List<List<RUnit>> foes = new ArrayList<List<RUnit>>();
	public int counter = 1;
	public boolean go() {
		Battle b = new Battle(Party.party.list,foes);
		return b.go();
	}
	
	
	public void addFoes(String name, int amount) {
		List<RUnit> l = new ArrayList<RUnit>();
		for (int i = 0;i < amount;i++) {
			l.add(new RMonster(name, counter++));
		}
		foes.add(l);
	}
}
