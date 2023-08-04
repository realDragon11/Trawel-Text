package rtrawel.items;

public abstract class Item {

	
	public abstract ItemType getItemType();
	
	
	public enum ItemType{
		WEAPON, CONSUMABLE, NONE, ARMOR;
	}
	
	public abstract int cost();
	
	public abstract String getName();
	public abstract String getDesc();
	public abstract String display();
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
	public int getResilienceMod() {
		return 0;
	}
}
