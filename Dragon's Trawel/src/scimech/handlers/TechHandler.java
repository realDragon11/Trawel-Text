package scimech.handlers;

import java.util.HashMap;
import java.util.Map;

public class TechHandler {

	private Map<String,Integer> techs = new HashMap<String,Integer>();
	
	public int getZeroTech(String name) {
		
		Integer i = techs.get(name);
		if (i == null) {
			return 0;
		}
		return i.intValue();
	}
	public void addTech(String name, int value) {
		if (!techs.containsKey(name)) {
			techs.put(name, value);
			return;
		}
		techs.put(name,techs.get(name)+value);
	}
}
