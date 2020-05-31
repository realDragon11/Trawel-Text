package rtrawel.battle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import rtrawel.unit.RMonster;
import rtrawel.unit.RUnit;
import rtrawel.unit.TargetGroup;
import trawel.extra;

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
		TargetGroup bt = new TargetGroup();
		for (RUnit f: foes) {
			bt.targets.add(f);
		}
		extra.println("A battle begins! [" + bt.toString() + "]");
		for (RUnit r: global) {
			r.cleanUp();
			r.curBattle = this;
			r.decide();
		}
		while (party.size() > 0 && foes.size() > 0) {
			global.sort(comp);
			double d = 999.0;
			for (RUnit r: global) {
				if (d > r.timeTilNext()) {
					d = r.timeTilNext();
				}
			}
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
			for (RUnit r: global) {
				r.decideNOW();
			}
			
		}
		Party.party.cleanUp();
		if (party.size() > 0) {
			assessKills();
			loot();
			return true;
		}else {
			return false;
		}
		
	}
	
	private void loot() {
		int totalxp = 0;
		int totalg = 0 ;
		for (RUnit r: foesDown) {
			totalg+=((RMonster)r).getGoldWorth();
			totalxp += ((RMonster)r).getXpWorth();
			((RMonster)r).loot();
		}
		extra.println("You got " + totalxp + " xp and " + totalg + " gold!");
		totalxp/=Party.party.list.size();
		Party.party.gold += totalg; 
		for (RUnit p: Party.party.list) {
			p.earnXp(totalxp);
		}
	}
	private void assessKills() {
		for (RUnit r: foesDown) {
			Party.party.addKill(r.getBaseName(),1);
		}
	}
	
	public void kill(RUnit r) {
		extra.println(r.getName() + " dies!");
		r.alive = false;
		if (!killList.contains(r)) {
			killList.add(r);
		}
	}
	
	public boolean stillFight() {
		boolean playersAlive = false;
		for (RUnit r: party) {
			if (r.getHp() > 0 && r.alive) {
				playersAlive = true;
				break;
			}
		}
		boolean foesAlive = false;
		for (RUnit r: foes) {
			if (r.getHp() > 0 && r.alive) {
				foesAlive = true;
				break;
			}
		}
		
		return playersAlive && foesAlive;
	}
}
