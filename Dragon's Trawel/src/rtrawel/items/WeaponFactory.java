package rtrawel.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.unit.DamageType;
import rtrawel.unit.RUnit;
import rtrawel.unit.RUnit.RaceType;

public class WeaponFactory {
	private static HashMap<String,Weapon> data = new HashMap<String, Weapon>();
	public static void init() {
		
		data.put("copper sword",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.SWORD;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 6;
			}

			@Override
			public String getName() {
				return "copper sword";
			}

			@Override
			public String getDesc() {
				return "Not the best sword out there, but it'll have to do.";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 20;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("lumber axe",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.AXE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return defender.getRaceType(RaceType.PLANT) ? 5 : 0 ;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 7;
			}

			@Override
			public String getName() {
				return "lumber axe";
			}

			@Override
			public String getDesc() {
				return "A simple splitter that's steller against plants.";
			}

			@Override
			public double critChance() {
				return .025;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 30;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("simple stabber",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.KNIFE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 3;
			}

			@Override
			public String getName() {
				return "simple stabber";
			}

			@Override
			public String getDesc() {
				return "Not the best knife out there, but it'll have to do.";
			}

			@Override
			public double critChance() {
				return .15;
			}

			@Override
			public double critMult() {
				return 4;
			}

			@Override
			public int cost() {
				return 10;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("wolf pup teeth",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.SHARP);
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 3;
			}

			@Override
			public String getName() {
				return "wolf pup teeth";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .05;
			}

			@Override
			public double critMult() {
				return 1.5;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
		
		data.put("fella knife",new Weapon() {

			@Override
			public WeaponType getWeaponType() {
				return WeaponType.MONSTER_MELEE;
			}

			@Override
			public OnHit getOnHit() {
				return OnHit.empty;
			}

			@Override
			public int damageBonuses(RUnit defender) {
				return 0;
			}

			@Override
			public List<DamageType> getDamageTypes() {
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.PIERCE);
				return list;
			}

			@Override
			public double getBaseHit() {
				return 1;
			}

			@Override
			public int getDamage() {
				return 5;
			}

			@Override
			public String getName() {
				return "fella knife";
			}

			@Override
			public String getDesc() {
				return "";
			}

			@Override
			public double critChance() {
				return .1;
			}

			@Override
			public double critMult() {
				return 1.25;
			}

			@Override
			public int cost() {
				return 0;
			}

			@Override
			public double blockChance() {
				return 0;
			}});
	}
	
	
	public static Weapon getWeaponByName(String str) {
		if (!data.containsKey(str)) {
			throw new RuntimeException("key not found");
		}
		return data.get(str);
	}


	public static Weapon getWeaponByName(String str, boolean permitsNull) {
		return data.get(str);
	}
}
