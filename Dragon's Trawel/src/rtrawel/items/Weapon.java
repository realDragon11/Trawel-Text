package rtrawel.items;

import java.util.List;

import rtrawel.unit.DamageType;

public abstract class Weapon implements Item {

	@Override
	public ItemType getItemType() {
		return Item.ItemType.WEAPON;
	}
	
	public abstract OnHit getOnHit();
	
	public abstract List<DamageType> getDamageTypes();

}
