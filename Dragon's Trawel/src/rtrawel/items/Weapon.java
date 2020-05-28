package rtrawel.items;

import java.util.List;

import rtrawel.unit.DamageType;
import rtrawel.unit.RUnit;

public abstract class Weapon implements Item {

	@Override
	public ItemType getItemType() {
		return Item.ItemType.WEAPON;
	}
	
	public abstract WeaponType getWeaponType();
	
	public abstract OnHit getOnHit();
	
	public abstract int damageBonuses(RUnit defender);
	
	public abstract List<DamageType> getDamageTypes();

	public abstract double getBaseHit();

	public abstract int getDamage();
	
	public abstract String getName();
	public abstract String getDesc();
	
	public abstract double critChance();
	public abstract double critMult();
	
	
	public enum WeaponType{
		SWORD, SHIELD, SPEAR, KNIFE, BOW, CROSSBOW, AXE, HAMMER, SLING, BOOMERANG;
	}

}
