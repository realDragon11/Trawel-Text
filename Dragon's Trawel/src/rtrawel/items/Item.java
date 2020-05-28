package rtrawel.items;

public interface Item {

	
	public ItemType getItemType();
	
	
	public enum ItemType{
		WEAPON, CONSUMABLE, NONE, ARMOR;
	}
	
	public int cost();
	
	public abstract String getName();
	public abstract String getDesc();
}
