package rtrawel.unit;

import java.util.HashMap;

public class MonsterFactory {
	private static HashMap<String,MonsterData> data = new HashMap<String, MonsterData>();
	static {
		
	}
	
	
	public static MonsterData getMonsterByName(String str) {
		return data.get(str);
	}
}
