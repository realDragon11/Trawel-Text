package rtrawel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
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
		return p;
	}
	
	
	public SaveData() {
		curVillage = Party.party.curVillage.name;
		Party.party.refreshItemKeys();
		for (String str: Party.party.itemKeys) {
			items.add(str);
			itemCounts.add(Party.party.items.get(str));
		}
	}

}
