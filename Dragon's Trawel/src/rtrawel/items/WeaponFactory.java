package rtrawel.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.unit.DamageType;
import rtrawel.unit.RUnit;

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
			}});
	}
	
	
	public static Weapon getWeaponByName(String str) {
		return data.get(str);
	}
}
