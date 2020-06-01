package rtrawel.items;

import java.util.HashMap;

import rtrawel.unit.DamMultMap;
import rtrawel.unit.DamageType;

public class ArmorFactory {
	private static HashMap<String,Armor> data = new HashMap<String, Armor>();
	public static void init() {
		
		data.put("cloth hood",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.HEAD;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.LIGHT;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public String getName() {
				return "cloth hood";
			}
			
			@Override
			public int getResilenceMod() {
				return 5;
			}

			@Override
			public String getDesc() {
				return "a simple head cover";
			}});
		
		data.put("leather hood",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.HEAD;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.MEDIUM;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int cost() {
				return 50;
			}

			@Override
			public String getName() {
				return "leather hood";
			}
			
			@Override
			public int getResilenceMod() {
				return 9;
			}

			@Override
			public String getDesc() {
				return "a simple head cover";
			}});
		
		data.put("mail hood",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.HEAD;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.HEAVY;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				map.insert(DamageType.SHARP,.9);
				return map;
			}

			@Override
			public int cost() {
				return 75;
			}

			@Override
			public String getName() {
				return "mail hood";
			}
			
			@Override
			public int getResilenceMod() {
				return 12;
			}

			@Override
			public String getDesc() {
				return "a simple head cover than insures against sharp damage";
			}});
		
			
		data.put("cloth shirt",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.TORSO;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.LIGHT;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int cost() {
				return 50;
			}

			@Override
			public String getName() {
				return "cloth shirt";
			}
			
			@Override
			public int getResilenceMod() {
				return 7;
			}

			@Override
			public String getDesc() {
				return "a simple cover";
			}});
		
		data.put("leather shirt",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.TORSO;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.MEDIUM;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int cost() {
				return 80;
			}

			@Override
			public String getName() {
				return "leather shirt";
			}
			
			@Override
			public int getResilenceMod() {
				return 12;
			}

			@Override
			public String getDesc() {
				return "a simple cover";
			}});
		
		data.put("mail shirt",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.TORSO;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.HEAVY;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				map.insert(DamageType.SHARP,.9);
				return map;
			}

			@Override
			public int cost() {
				return 120;
			}

			@Override
			public String getName() {
				return "mail shirt";
			}
			
			@Override
			public int getResilenceMod() {
				return 16;
			}

			@Override
			public String getDesc() {
				return "a simple cover than insures against sharp damage";
			}});
		
		data.put("band of brilliance",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.ASSEC;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.NONE;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public String getName() {
				return "band of brilliance";
			}
			
			@Override
			public int getResilenceMod() {
				return 0;
			}
			@Override
			public int getKnowledgeMod() {
				return 5;
			}

			@Override
			public String getDesc() {
				return "A ring that increases your knowledge.";
			}});
		
		data.put("lucky coin",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.ASSEC;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.LIGHT;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				map.insertOrAdd(DamageType.STUN,.75);
				return map;
			}

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public String getName() {
				return "lucky coin";
			}
			
			@Override
			public int getResilenceMod() {
				return 0;
			}
			@Override
			public int getAgilityMod() {
				return 5;
			}

			@Override
			public String getDesc() {
				return "Said to increase your agility as well as insure against stuns.";
			}});
		
		data.put("antimagic circlet",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.ASSEC;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.NONE;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				map.insert(DamageType.MAGIC,.75);
				return map;
			}

			@Override
			public int cost() {
				return 150;
			}

			@Override
			public String getName() {
				return "antimagic circlet";
			}
			
			@Override
			public int getResilenceMod() {
				return 0;
			}
			@Override
			public int getKnowledgeMod() {
				return 2;
			}

			@Override
			public String getDesc() {
				return "A headband that reduces magical damage by a large degree.";
			}});
		
		data.put("iron bracers",new Armor() {

			@Override
			public ArmorType getArmorType() {
				return ArmorType.HANDS;
			}

			@Override
			public ArmorClass getArmorClass() {
				return ArmorClass.HEAVY;
			}

			@Override
			public DamMultMap getDMM() {
				DamMultMap map = new DamMultMap();
				return map;
			}

			@Override
			public int cost() {
				return 40;
			}

			@Override
			public String getName() {
				return "iron bracers";
			}
			
			@Override
			public int getResilenceMod() {
				return 8;
			}

			@Override
			public String getDesc() {
				return "An armor for the arms.";
			}});
	}
	
	public static Armor getArmorByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}
	public static Armor getArmorByName(String str, boolean b) {
		return data.get(str);
	}
}
