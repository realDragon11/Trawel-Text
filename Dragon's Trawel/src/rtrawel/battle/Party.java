package rtrawel.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rtrawel.items.Item;
import rtrawel.items.Item.ItemType;
import rtrawel.unit.RCore;
import rtrawel.unit.RPlayer;
import rtrawel.unit.RUnit;
import rtrawel.village.Village;
import trawel.extra;

public class Party {

	public static Party party = new Party();
	
	public List<RUnit> list = new ArrayList<RUnit>();
	
	public int gold = 0;
	public Map<String,Integer> items = new HashMap<String,Integer>();
	public Map<String,Integer> killCounter = new HashMap<String,Integer>();
	
	
	public List<String> itemKeys = new ArrayList<String>();

	public Village curVillage;

	public List<String> killKeys = new ArrayList<String>();
	
	public void addItem(String str,int num) {
		if (items.containsKey(str)) {
			items.replace(str,items.get(str) + num);
		}else {
			items.put(str,num);
			}
	}
	
	public void addKill(String str,int num) {
		if (killCounter.containsKey(str)) {
			killCounter.replace(str,killCounter.get(str) + num);
		}else {
			killCounter.put(str,num);
			}
	}
	
	public void refreshItemKeys() {
		itemKeys.clear();
		items.keySet().stream().forEach(itemKeys::add);
	}

	public void displayItems() {
		for (int i = 0;i < itemKeys.size();i++) {
			extra.println((i+1)+ " " + itemKeys.get(i) + ": " + items.get(itemKeys.get(i)) + "\n" + RCore.getItemByName(itemKeys.get(i)).display());
		}
	}
	
	public String popItem(int i) {
		String str = itemKeys.get(i);
		int count = items.get(str);
		if (count == 1) {
			items.remove(str);
		}else {
			items.replace(str,count-1);
		}
		
		return str;
	}
	
	public String popItem(Item item) {
		String str = item.getName();
		int count = items.get(str);
		if (count == 1) {
			items.remove(str);
		}else {
			items.replace(str,count-1);
		}
		
		return str;
	}


	public RUnit getUnit() {
		for (int i = 0;i < list.size();i++) {
			extra.println( (i +1) + " " + list.get(i).getName());
		}
		return list.get(extra.inInt(list.size())-1);
	}

	public Item getPersonItem() {
		refreshItemKeys();
		List<Item> canTake = new ArrayList<Item>();
		for (String str: itemKeys) {
			Item it = RCore.getItemByName(str);
			if (it.getItemType().equals(ItemType.CONSUMABLE) || it.getItemType().equals(ItemType.WEAPON)) {
				canTake.add(it);
			}
		}
		int i;
		for (i = 0; i < canTake.size();i++) {
			extra.println((i + 1) + " " + canTake.get(i).getName());
		}
		i++;//extra i++;
		extra.println(i + " nothing");
		int in = extra.inInt(i);
		if (in == i) {
		return null;
		}
		return canTake.get(in-1);
	}

	public boolean allDead() {
		for (RUnit r: list) {
			if (r.isAlive()) {
				return false;
			}
		}
		return true;
	}

	public void cleanUp() {
		for (RUnit r: this.list) {
			r.cleanUp();
		}
		
	}

	public List<RUnit> getAlive() {
		List<RUnit> alive = new ArrayList<RUnit>();
		list.stream().filter(p -> p.isAlive()).forEach(alive::add);
		return alive;
	}
	
	public List<RUnit> getDead() {
		List<RUnit> alive = new ArrayList<RUnit>();
		list.stream().filter(p -> !p.isAlive()).forEach(alive::add);
		return alive;
	}
	
	public void refreshKillKeys() {
		killKeys.clear();
		killCounter.keySet().stream().forEach(killKeys::add);
	}

	public double lootChance() {
		double total = 1;
		for (RUnit r: list) {
			total *= ((RPlayer)r).lootChance();
		}
		return 0;
	}
}
