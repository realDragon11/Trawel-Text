package rtrawel.items;

public abstract class MaterialItem extends Item{

	@Override
	public ItemType getItemType() {
		return ItemType.NONE;
	}

	@Override
	public String display() {
		return this.getName() + " " + this.getDesc();
	}

}
