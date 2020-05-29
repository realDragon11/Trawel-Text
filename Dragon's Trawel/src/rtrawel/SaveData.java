package rtrawel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import rtrawel.unit.RPlayer;
import rtrawel.unit.RUnit;
import rtrawel.village.Village;
import rtrawel.village.VillageFactory;

public class SaveData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8571608622587482496L;
	
	private List<String> items = new ArrayList<String>();
	private List<Integer> itemCounts = new ArrayList<Integer>();
	private List<String> kills = new ArrayList<String>();
	private List<Integer> killCounts = new ArrayList<Integer>();
	
	private String curVillage;
	private int gold;
	private List<PersonSave> people = new ArrayList<PersonSave>();

	public Party getParty() {
		Party p = new Party();
		Village v = null;
		for (Village vs: VillageFactory.villages) {
			if (vs.name.equals(curVillage)) {
				v = vs;
				break;
			}
		}
		p.curVillage = v;
		p.gold = gold;
		for (PersonSave ps: people) {
			p.list.add(new RPlayer(ps.name,ps.currentJob, ps.progression, ps.weap, ps.shield, ps.head, ps.torso,ps.arms, ps.pants, ps.feet, ps.assec1,ps.assec2,ps.inventory));
		}
		return p;
	}
	
	
	public SaveData() {
		gold = Party.party.gold;
		curVillage = Party.party.curVillage.name;
		Party.party.refreshItemKeys();
		for (String str: Party.party.itemKeys) {
			items.add(str);
			itemCounts.add(Party.party.items.get(str));
		}
		Party.party.refreshKillKeys();
		for (String str: Party.party.killKeys) {
			kills.add(str);
			killCounts.add(Party.party.killCounter.get(str));
		}
		for (RUnit r: Party.party.list) {
			people.add(new PersonSave((RPlayer)r));
		}
	}

}
