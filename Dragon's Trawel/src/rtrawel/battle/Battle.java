package rtrawel.battle;

import java.util.ArrayList;
import java.util.List;

import rtrawel.unit.RMonster;
import rtrawel.unit.RUnit;

public class Battle {

	public List<RUnit> party = new ArrayList<RUnit>();
	public List<RUnit> foes = new ArrayList<RUnit>();
	public List<RUnit> global = new ArrayList<RUnit>();
	public List<RUnit> foesDown = new ArrayList<RUnit>();
	public List<RUnit> partyDown = new ArrayList<RUnit>();
	public Battle(List<RUnit> party, List<RUnit> foes) {
		this.party.addAll(party);
		this.foes.addAll(foes);
		global.addAll(party);
		global.addAll(foes);
	}
	
	
	public boolean go() {
		for (RUnit r: global) {
			r.curBattle = this;
			r.decide();
		}
		while (party.size() > 0 && foes.size() > 0) {
			
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
		}
		totalxp/=Party.party.list.size();
		for (RUnit p: Party.party.list) {
			p.EarnXp(totalxp);
		}
	}
}
