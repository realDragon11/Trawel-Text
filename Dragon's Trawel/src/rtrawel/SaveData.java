package rtrawel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import rtrawel.items.ArmorFactory;
import rtrawel.items.Item;
import rtrawel.items.WeaponFactory;
import rtrawel.unit.RCore;
import rtrawel.unit.RPlayer;
import rtrawel.unit.RUnit;

public class SaveData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8571608622587482496L;
	
	private List<String> items = new ArrayList<String>();
	private List<Integer> itemCounts = new ArrayList<Integer>();
	private List<String> kills = new ArrayList<String>();
	private List<Integer> killCounts = new ArrayList<Integer>();
	
	public String curVillage;
	private int gold;
	private List<PersonSave> people = new ArrayList<PersonSave>();
	private EventFlag ev;

	public Party getParty() {
		Party p = new Party();
		EventFlag.eventFlag = ev;
		p.gold = gold;
		for (PersonSave ps: people) {
			List<Item> inv = new ArrayList<Item>();
			for (String str: ps.inventory) {
				inv.add(RCore.getItemByName(str));
			}
			p.list.add(new RPlayer(ps.name,ps.currentJob, ps.progression, WeaponFactory.getWeaponByName(ps.weap,true), WeaponFactory.getWeaponByName(ps.shield,true),ArmorFactory.getArmorByName(ps.head,true), ArmorFactory.getArmorByName(ps.torso,true),ArmorFactory.getArmorByName(ps.arms,true), ArmorFactory.getArmorByName(ps.pants,true), ArmorFactory.getArmorByName(ps.feet,true), ArmorFactory.getArmorByName(ps.assec1,true),ArmorFactory.getArmorByName(ps.assec2,true),inv,ps.hp,ps.mana));
		}
		for (int i = 0;i< kills.size();i++) {
			p.addKill(kills.get(i),killCounts.get(i));
		}
		return p;
	}

	
	
	public SaveData() {
		ev = EventFlag.eventFlag;
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
