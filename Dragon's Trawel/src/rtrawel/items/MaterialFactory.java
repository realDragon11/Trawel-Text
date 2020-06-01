package rtrawel.items;

import java.util.HashMap;

import rtrawel.unit.Action;
import rtrawel.unit.ActionFactory;

public class MaterialFactory {
	private static HashMap<String,MaterialItem> data = new HashMap<String, MaterialItem>();
	public static void init() {
		data.put("canine's canine",new MaterialItem() {

			@Override
			public int cost() {
				return 4;
			}

			@Override
			public String getName() {
				return "canine's canine";
			}

			@Override
			public String getDesc() {
				return "todo";
			}
			
		});
		
		data.put("leather pelt",new MaterialItem() {

			@Override
			public int cost() {
				return 10;
			}

			@Override
			public String getName() {
				return "leather pelt";
			}

			@Override
			public String getDesc() {
				return "todo";
			}
			
			
			
		});
		
		data.put("much 'o mushroom",new MaterialItem() {

			@Override
			public int cost() {
				return 10;
			}

			@Override
			public String getName() {
				return "much 'o mushroom";
			}

			@Override
			public String getDesc() {
				return "todo";
			}});
		
		data.put("iron chunk",new MaterialItem() {

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public String getName() {
				return "iron chunk";
			}

			@Override
			public String getDesc() {
				return "todo";
			}});
	}
	
	public static MaterialItem getMaterialByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}
	
	public static MaterialItem getMaterialByName(String str, boolean b) {
		return data.get(str);
	}
}
