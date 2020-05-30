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
		
		data.put("ranger",new Job() {

			@Override
			public String getName() {
				return "ranger";
			}

			@Override
			public String getPath1() {
				return "hunting";
			}

			@Override
			public String getPath2() {
				return "gathering";
			}

			@Override
			public String getPath3() {
				return "surveying";
			}

			@Override
			public int getHpAtLevel(int level) {
				return RCore.levelLynchPin(level, 18, 35, 80, 999, 999, 999, 999, 999);
			}

			@Override
			public int getMpAtLevel(int level) {
				return RCore.levelLynchPin(level, 6, 14, 30, 999, 999, 999, 999, 999);
			}

			@Override
			public int getTenAtLevel(int level) {
				return RCore.levelLynchPin(level, 6, 12,20, 999, 999, 999, 999, 999);
			}

			@Override
			public int getStrAtLevel(int level) {
				return RCore.levelLynchPin(level, 10, 20,40, 999, 999, 999, 999, 999);
			}

			@Override
			public int getDexAtLevel(int level) {
				return RCore.levelLynchPin(level, 12,35,60, 999, 999, 999, 999, 999);
			}

			@Override
			public int getAgiAtLevel(int level) {
				return RCore.levelLynchPin(level, 12,35,60, 999, 999, 999, 999, 999);
			}

			@Override
			public int getSpdAtLevel(int level) {
				return RCore.levelLynchPin(level, 12,20,30, 999, 999, 999, 999, 999);
			}

			@Override
			public int getKnoAtLevel(int level) {
				return RCore.levelLynchPin(level, 6, 12,20, 999, 999, 999, 999, 999);
			}

			@Override
			public int getResAtLevel(int level) {
				return RCore.levelLynchPin(level, 2, 6, 12, 999, 999, 999, 999, 999);
			}

			@Override
			public List<WeaponType> weaponTypes() {
				List<WeaponType> list = new ArrayList<WeaponType>();
				list.add(WeaponType.BOW);
				list.add(WeaponType.CROSSBOW);
				list.add(WeaponType.KNIFE);
				list.add(WeaponType.BOOMERANG);
				list.add(WeaponType.SLING);
				return list;
			}

			@Override
			public List<ArmorClass> armorClasses() {
				List<ArmorClass> list = new ArrayList<ArmorClass>();
				list.add(ArmorClass.LIGHT);
				list.add(ArmorClass.MEDIUM);
				return list;
			}});
	}
}
