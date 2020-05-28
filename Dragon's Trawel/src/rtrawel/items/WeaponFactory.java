package rtrawel.items;

import java.util.HashMap;

public class WeaponFactory {
	private static HashMap<String,Weapon> data = new HashMap<String, Weapon>();
	public static void init() {
		
	}
	
	
	public static Weapon getWeaponByName(String str) {
		return data.get(str);
	}
}
