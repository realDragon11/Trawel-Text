package rtrawel.unit;

import java.util.HashMap;

public class ActionFactory {
	private static HashMap<String,Action> data = new HashMap<String, Action>();
	static {
		
	}
	
	public static Action getActionByName(String str) {
		return data.get(str);
	}
}
