package rtrawel.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rtrawel.unit.RUnit;

public class Party {

	public static Party party = new Party();
	
	public List<RUnit> list = new ArrayList<RUnit>();
	
	public int gold = 0;
	public Map<String,Integer> items = new HashMap<String,Integer>();
	
	public void addItem(String str,int num) {
		if (items.containsKey(str)) {
			items.replace(str,items.get(str) + num);
		}else {
			items.put(str,num);
		}
	}
}
