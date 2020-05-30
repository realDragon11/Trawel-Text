package rtrawel.items;

import rtrawel.unit.Action;

public abstract class Consumable extends Item {

	@Override
	public ItemType getItemType() {
		return ItemType.CONSUMABLE;
	}


	public abstract Action getAction();
	@Override
	public String display() {
		return this.getName() + " "+ this.getDesc();
	}

}
