package rtrawel.unit;

import java.util.HashMap;

public class MonsterFactory {
	private static HashMap<String,MonsterData> data = new HashMap<String, MonsterData>();
	public static void init(){
		
	}
	
	
	public static MonsterData getMonsterByName(String str) {
		return data.get(str);
	}
}
