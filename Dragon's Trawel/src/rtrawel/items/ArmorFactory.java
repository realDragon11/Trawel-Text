package rtrawel.items;

import java.util.HashMap;

public class ArmorFactory {
	private static HashMap<String,Armor> data = new HashMap<String, Armor>();
	public static void init() {
		
	}
	
	public static Armor getArmorByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}
}
