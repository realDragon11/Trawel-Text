package rtrawel.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rtrawel.unit.RUnit;
import rtrawel.village.Village;

public class Party {

	public static Party party = new Party();
	
	public List<RUnit> list = new ArrayList<RUnit>();
	
	public int gold = 0;
	public Map<String,Integer> items = new HashMap<String,Integer>();
	public Map<String,Integer> killCounter = new HashMap<String,Integer>();

	public Village curVillage;
	
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
}
