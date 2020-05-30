package rtrawel;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Battle;
import rtrawel.battle.Party;
import rtrawel.jobs.JobWithLevel;
import rtrawel.unit.RCore;
import rtrawel.unit.RMonster;
import rtrawel.unit.RPlayer;
import rtrawel.unit.RUnit;
import rtrawel.village.Village;
import rtrawel.village.VillageFactory;
import trawel.extra;

public class TestRunner {

	public static void main(String[] args) {
		RCore.init();
		
		extra.println("Start new game?");
		if (extra.yesNo()) {
			Party.party.list.add(new RPlayer("jess","warrior"));
			((RPlayer)Party.party.list.get(0)).progression.jobs.add(new JobWithLevel("warrior",1));
			((RPlayer)Party.party.list.get(0)).cleanAbs();
			Party.party.list.get(0).earnXp(999999);
			Party.party.list.get(0).refresh();
			
			Party.party.curVillage = VillageFactory.init();
			RCore.save();
		}
		
		
		while (true) {
			//load
			VillageFactory.init();//TODO save village data/event flags
			RCore.load();
			while (true) {
				Party.party.curVillage.go();
				if (Party.party.allDead()) {
					break;
				}
			}
		}
		/*
		List<List<RUnit>> foeFoeList = new ArrayList<List<RUnit>>();
		List<RUnit> foeList = new ArrayList<RUnit>();
		foeFoeList.add(foeList);
		int totalCounter = 1;
		for (int i = 0; i < 2; i++) {
		foeList.add(new RMonster("wolf pup", totalCounter));
		foeList.get(i).refresh();
		totalCounter++;
		}
		foeList = new ArrayList<RUnit>();
		foeFoeList.add(foeList);
		for (int i = 0; i < 1; i++) {
		foeList.add(new RMonster("fearless fella", totalCounter));
		foeList.get(i).refresh();
		totalCounter++;
		}
		Battle b = new Battle(Party.party.list,foeFoeList);
		b.go();
		extra.println("Battle over!");
		*/
	}

}
