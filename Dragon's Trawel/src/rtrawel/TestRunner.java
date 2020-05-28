package rtrawel;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Battle;
import rtrawel.battle.Party;
import rtrawel.unit.RCore;
import rtrawel.unit.RMonster;
import rtrawel.unit.RUnit;

public class TestRunner {

	public static void main(String[] args) {
		RCore.init();
		
		Party.party.list.add(new RPlayer());
		List<RUnit> foeList = new ArrayList<RUnit>();
		for (int i = 0; i < 2; i++) {
		foeList.add(new RMonster("wolf pup", i+1));
		foeList.get(i).refresh();
		}
		Battle b = new Battle(Party.party.list,foeList);
	}

}
