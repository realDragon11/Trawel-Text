package rtrawel.items;

import rtrawel.unit.DamMultMap;

public abstract class Armor implements Item {

	@Override
	public ItemType getItemType() {
		return ItemType.ARMOR;
	}
	
	public abstract ArmorType getArmorType();
	
	public enum ArmorType{
		HEAD,TORSO,HANDS,PANTS,FEET,ASSEC;
	}
	
	public abstract ArmorClass getArmorClass();
	
	public enum ArmorClass{
		NONE, LIGHT, MEDIUM, HEAVY;;
	}
	
	public abstract String getName();
	public abstract String getDesc();
	
	public int getStrengthMod() {
		return 0;
	}
	public int getKnowledgeMod() {
		return 0;
	}
	public int getSpeedMod() {
		return 0;
	}
	public int getAgilityMod() {
		return 0;
	}
	public int getDexterityMod() {
		return 0;
	}
	public abstract int getResilenceMod();

	public abstract DamMultMap getDMM();
}
