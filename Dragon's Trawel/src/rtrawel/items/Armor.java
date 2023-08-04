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
	
	@Override
	public String display() {
		String str = this.getName() + "(" +this.getArmorClass().toString().toLowerCase()+ ", " +this.getArmorType().toString().toLowerCase()+ ") " + this.getDesc() +"\n";
		str+= this.getResilienceMod() + "res ";
		if (this.getKnowledgeMod() > 0) {
			str+= this.getKnowledgeMod() + "kno ";
		}
		if (this.getSpeedMod() > 0) {
			str+= this.getSpeedMod() + "spd ";
		}
		if (this.getAgilityMod() > 0) {
			str+= this.getAgilityMod() + "agi ";
		}
		if (this.getDexterityMod() > 0) {
			str+= this.getDexterityMod() + "dex ";
		}
		return str;
	}
}
