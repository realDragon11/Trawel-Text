package rtrawel;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Battle;
import rtrawel.battle.Party;
import rtrawel.unit.RCore;
import rtrawel.unit.RMonster;
import rtrawel.unit.RPlayer;
import rtrawel.unit.RUnit;
import trawel.extra;

public class TestRunner {

	public static void main(String[] args) {
		RCore.init();
		
		Party.party.list.add(new RPlayer("jess","warrior"));
		Party.party.list.get(0).refresh();
		List<List<RUnit>> foeFoeList = new ArrayList<List<RUnit>>();
		List<RUnit> foeList = new ArrayList<RUnit>();
		foeFoeList.add(foeList);
		for (int i = 0; i < 2; i++) {
		foeList.add(new RMonster("wolf pup", i+1));
		foeList.get(i).refresh();
		}
		Battle b = new Battle(Party.party.list,foeFoeList);
		b.go();
		extra.println("Battle over!");
	}

}
