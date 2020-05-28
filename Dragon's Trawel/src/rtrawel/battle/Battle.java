package rtrawel.battle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import rtrawel.unit.RMonster;
import rtrawel.unit.RUnit;

public class Battle {

	public static Comparator<RUnit> comp = new Comparator<RUnit>(){

		@Override
		public int compare(RUnit arg0, RUnit arg1) {
			return (int) Math.signum(arg1.timeTilNext()-arg0.timeTilNext());
		}};
	
	public List<RUnit> party = new ArrayList<RUnit>();
	public List<RUnit> foes = new ArrayList<RUnit>();
	public List<RUnit> global = new ArrayList<RUnit>();
	public List<RUnit> foesDown = new ArrayList<RUnit>();
	public List<RUnit> partyDown = new ArrayList<RUnit>();
	public List<RUnit> killList = new ArrayList<RUnit>();
	public List<List<RUnit>> foeGroups = new ArrayList<List<RUnit>>();//this is the only one that's unsafe
	public Battle(List<RUnit> party, List<List<RUnit>> foes) {
		this.party.addAll(party);
		foeGroups = foes;
		for (List<RUnit> f: foes) {
		this.foes.addAll(f);
		}
		global.addAll(party);
		global.addAll(this.foes);
	}
	
	
	public boolean go() {
		for (RUnit r: global) {
			r.curBattle = this;
			r.decide();
		}
		while (party.size() > 0 && foes.size() > 0) {
			global.sort(comp);
			double d = global.get(0).timeTilNext();
			for (RUnit r: global) {
				if (r.getHp() > 0) {
				r.advanceTime(d);}else {
					kill(r);
				}
			}
			
			for (RUnit r: killList) {
				global.remove(r);
				if (party.contains(r)) {
					party.remove(r);
					partyDown.add(r);
				}else {
					for (List<RUnit> f: foeGroups) {
						if (f.remove(r)) {
							break;
						}
						}
					foes.remove(r);
					foesDown.add(r);
				}
			}
			killList.clear();
		}
		
		if (party.size() > 0) {
			loot();
			return true;
		}else {
			return false;
		}
		
	}
	
	private void loot() {
		int totalxp = 0;
		for (RUnit r: foes) {
			Party.party.gold+=((RMonster)r).getGoldWorth();
			totalxp += ((RMonster)r).getXpWorth();
			((RMonster)r).loot();
		}
		totalxp/=Party.party.list.size();
		for (RUnit p: Party.party.list) {
			p.earnXp(totalxp);
		}
	}
	
	public void kill(RUnit r) {
		if (!killList.contains(r)) {
			killList.add(r);
		}
	}
}
