package rtrawel.items;

import java.util.List;

import rtrawel.unit.DamageType;
import rtrawel.unit.RUnit;

public abstract class Weapon implements Item {

	@Override
	public ItemType getItemType() {
		return Item.ItemType.WEAPON;
	}
	
	public abstract OnHit getOnHit();
	
	public abstract int damageBonuses(RUnit defender);
	
	public abstract List<DamageType> getDamageTypes();

	public abstract double getBaseHit();

	public abstract int getDamage();

}
