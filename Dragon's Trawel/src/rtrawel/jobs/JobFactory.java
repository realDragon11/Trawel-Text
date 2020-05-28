package rtrawel.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.items.Armor.ArmorClass;
import rtrawel.items.Weapon.WeaponType;
import rtrawel.unit.RCore;
import rtrawel.unit.RPlayer;

public class JobFactory {
	private static HashMap<String,Job> data = new HashMap<String, Job>();
	
	public static Job getJobByName(String str) {
		return data.get(str);
	}
	
	public static void init() {
		data.put("warrior",new Job() {

			@Override
			public String getName() {
				return "warrior";
			}

			@Override
			public String getPath1() {
				return "courage";
			}

			@Override
			public String getPath2() {
				return "honor";
			}

			@Override
			public String getPath3() {
				return "valor";
			}

			@Override
			public int getHpAtLevel(int level) {
				return RCore.levelLynchPin(level, 25, 50, 100, 999, 999, 999, 999, 999);
			}

			@Override
			public int getMpAtLevel(int level) {
				return RCore.levelLynchPin(level, 4, 7, 14, 999, 999, 999, 999, 999);
			}

			@Override
			public int getTenAtLevel(int level) {
				return RCore.levelLynchPin(level, 10, 20,30, 999, 999, 999, 999, 999);
			}

			@Override
			public int getStrAtLevel(int level) {
				return RCore.levelLynchPin(level, 20, 35, 75, 999, 999, 999, 999, 999);
			}

			@Override
			public int getDexAtLevel(int level) {
				return RCore.levelLynchPin(level, 4, 7, 14, 999, 999, 999, 999, 999);
			}

			@Override
			public int getAgiAtLevel(int level) {
				return RCore.levelLynchPin(level, 4, 7, 14, 999, 999, 999, 999, 999);
			}

			@Override
			public int getSpdAtLevel(int level) {
				return RCore.levelLynchPin(level, 4, 7, 14, 999, 999, 999, 999, 999);
			}

			@Override
			public int getKnoAtLevel(int level) {
				return RCore.levelLynchPin(level, 0, 0, 0, 0, 0, 0, 0, 0);
			}

			@Override
			public int getResAtLevel(int level) {
				return RCore.levelLynchPin(level, 20, 35, 75, 999, 999, 999, 999, 999);
			}

			@Override
			public List<WeaponType> weaponTypes() {
				List<WeaponType> list = new ArrayList<WeaponType>();
				list.add(WeaponType.AXE);
				list.add(WeaponType.HAMMER);
				list.add(WeaponType.SHIELD);
				list.add(WeaponType.SPEAR);
				list.add(WeaponType.SWORD);
				return list;
			}

			@Override
			public List<ArmorClass> armorClasses() {
				List<ArmorClass> list = new ArrayList<ArmorClass>();
				list.add(ArmorClass.HEAVY);
				list.add(ArmorClass.MEDIUM);
				return list;
			}});
	}
}
