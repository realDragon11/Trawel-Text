package rtrawel.jobs;

import java.util.HashMap;
import java.util.List;

import rtrawel.items.Armor.ArmorClass;
import rtrawel.items.Weapon.WeaponType;
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
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getPath1() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getPath2() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getPath3() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getHpAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getMPAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getTenAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getStrAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getDexAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getAgiAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getSpdAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getKnoAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getResAtLevel(int level) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<WeaponType> weaponTypes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ArmorClass> armorClasses() {
				// TODO Auto-generated method stub
				return null;
			}})
	}
}
