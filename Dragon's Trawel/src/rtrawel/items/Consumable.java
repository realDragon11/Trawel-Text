package rtrawel.items;

import rtrawel.unit.Action;

public abstract class Consumable implements Item {

	@Override
	public ItemType getItemType() {
		return ItemType.CONSUMABLE;
	}


	public abstract Action getAction();

}
