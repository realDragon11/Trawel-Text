package rtrawel.items;

import java.util.HashMap;

public class ArmorFactory {
	private static HashMap<String,Armor> data = new HashMap<String, Armor>();
	public static void init() {
		
	}
	
	public static Armor getArmorByName(String str) {
		return data.get(str);
	}
}
