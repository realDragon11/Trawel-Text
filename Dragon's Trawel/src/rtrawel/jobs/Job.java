package rtrawel.jobs;

import java.util.List;

import rtrawel.items.Armor;
import rtrawel.items.Weapon;

public abstract class Job {
	
	public abstract String getName();

	public abstract String getPath1();
	public abstract String getPath2();
	public abstract String getPath3();
	
	public abstract int getHpAtLevel(int level);
	public abstract int getMPAtLevel(int level);
	public abstract int getTenAtLevel(int level);
	public abstract int getStrAtLevel(int level);
	public abstract int getDexAtLevel(int level);
	public abstract int getAgiAtLevel(int level);
	public abstract int getSpdAtLevel(int level);
	public abstract int getKnoAtLevel(int level);
	public abstract int getResAtLevel(int level);
	
	public abstract List<Weapon.WeaponType> weaponTypes();
	public abstract List<Armor.ArmorClass> armorClasses();
}
