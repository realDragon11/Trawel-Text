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
				return 10;
			}

			@Override
			public String getName() {
				return "medicine herb";
			}

			@Override
			public String getDesc() {
				return "A small but important herb that heals small ailments.";
			}

			@Override
			public Action getAction() {
				return ActionFactory.getActionByName("medicine heal");
			}
			
		});
	}
	
	public static Consumable getConsumableByName(String str) {
		return data.get(str);
	}
}
