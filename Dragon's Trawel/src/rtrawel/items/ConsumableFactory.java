package rtrawel.items;

import java.util.HashMap;

import rtrawel.unit.Action;
import rtrawel.unit.ActionFactory;

public class ConsumableFactory {
	private static HashMap<String,Consumable> data = new HashMap<String, Consumable>();
	public static void init() {
		data.put("medicine herb",new Consumable() {

			@Override
			public int cost() {
				return 12;
			}

			@Override
			public String getName() {
				return "medicine herb";
			}

			@Override
			public String getDesc() {
				return "A small but important herb that heals around 12 hp.";
			}

			@Override
			public Action getAction() {
				return ActionFactory.getActionByName("medicine heal");
			}
			
		});
		
		data.put("basic tincture",new Consumable() {

			@Override
			public int cost() {
				return 40;
			}

			@Override
			public String getName() {
				return "basic tincture";
			}

			@Override
			public String getDesc() {
				return "A basic tincture that heals around 30 hp.";
			}

			@Override
			public Action getAction() {
				return ActionFactory.getActionByName("basic tincture heal");
			}
			
		});
		
		data.put("root of resilence",new Consumable() {

			@Override
			public int cost() {
				return 60;
			}

			@Override
			public String getName() {
				return "root of resilence";
			}

			@Override
			public String getDesc() {
				return "A special root that increases your resilence.";
			}

			@Override
			public Action getAction() {
				return ActionFactory.getActionByName("root of resilence");
			}
			
		});
		
		data.put("ink sac",new Consumable() {

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public String getName() {
				return "ink sac";
			}

			@Override
			public String getDesc() {
				return "Sprays a cloud of agility-reducing ink.";
			}

			@Override
			public Action getAction() {
				return ActionFactory.getActionByName("ink spray");
			}
			
		});
	}
	
	public static Consumable getConsumableByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}
	
	public static Consumable getConsumableByName(String str, boolean b) {
		return data.get(str);
	}
}
