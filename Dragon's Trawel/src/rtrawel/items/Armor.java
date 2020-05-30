package rtrawel.items;

import rtrawel.unit.DamMultMap;

public abstract class Armor extends Item {

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
	

	public abstract DamMultMap getDMM();
}
