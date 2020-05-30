package rtrawel.battle;

import java.util.HashMap;

import rtrawel.items.Armor;

public class FightFactory {
	private static HashMap<String,Fight> data = new HashMap<String, Fight>();
	public static void init() {
		Fight f = new Fight();
		f.addFoes("wolf pup",4);
		data.put("homa_pup1",f);
		f = new Fight();
		f.addFoes("fearless fella",1);
		f.addFoes("wolf pup",2);
		data.put("homa_fella1",f);
		
		f = new Fight();
		f.addFoes("fearless fella",3);;
		data.put("homa_unun_boss",f);
	}
	
	public static Fight getFightByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}
}
