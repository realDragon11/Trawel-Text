package trawel;

import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.towns.World;

/**
 * 
 * @author Brian Malone
 * Before 2/11/2018
 * The shop services, ie how gold can be spent.
 */

public class Services {

	/**
	 * only takes goldmod
	 * @param theEnchant
	 * @param level
	 * @param mod
	 * @return
	 */
	public static Enchant improveEnchantChance(Enchant theEnchant, int level, float mod) {
		Enchant newEnchant = EnchantConstant.makeEnchant(1.2f*mod,5);
		if (newEnchant.getGoldMod() > theEnchant.getGoldMod()) {
			return newEnchant;
		}else {
			return theEnchant;
		}
	}
	//instance methods
	/**
	 * Attempt to improve the armor's enchantment.
	 * Level = ceil(gold spent / cost of armor)
	 * @param theArm (Armor)
	 * @param goldSpent (int)
	 */
	public void renchantBasic(Armor theArm,int goldSpent) {
		theArm.improveEnchantChance((int)Math.ceil(((double)goldSpent)/theArm.getBaseCost()));
	}
	
	/**
	 * Attempt to improve the armor's enchantment ten times.
	 * Level = ceil((gold spent *.5) / cost of armor)
	 * @param theArm
	 * @param goldSpent
	 */
	public void renchantPremium(Armor theArm,int goldSpent) {
		int attempts = 0;
		do {
		theArm.improveEnchantChance((int)Math.ceil((goldSpent*.5)/theArm.getBaseCost()));
		attempts++;
		}while(attempts <= 10);
	}
	
	
	/**
	 * Attempt to improve the weapon's enchantment.
	 * Level = ceil(gold spent / cost of armor)
	 * @param theArm (Armor)
	 * @param goldSpent (int)
	 */
	public void renchantBasic(Weapon theWeap,int goldSpent) {
		theWeap.improveEnchantChance((int)Math.ceil(((double)goldSpent)/theWeap.getBaseCost()));
	}
	
	/**
	 * Attempt to improve the armor's enchantment ten times.
	 * Level = ceil((gold spent *.5) / cost of armor)
	 * @param theArm (Armor)
	 * @param goldSpent (int)
	 */
	public void renchantPremium(Weapon theWeap,int goldSpent) {
		//boolean didEnchant; //was going to have it take ten tries, but now it's the best of 10
		int attempts = 0;
		do {
		theWeap.improveEnchantChance((int)Math.ceil((goldSpent*.5)/theWeap.getBaseCost()));
		attempts++;
		}while(attempts <= 10);
	}
	
	/**
	 * Sells and item from inventory bag, and puts the resulting cash in inventory purse.
	 * @param item - the item you want to sell (Item)
	 * @param bag - the inventory to sell from (Inventory)
	 * @param purse - where the gold goes (Inventory)
	 * @param getNew - (boolean), if you want a new item to replace the sold item, put true.
	 * Otherwise, the current item is kept, and the calling function is responsible for getting rid of it.
	 */
	public static void sellItem(Weapon item, Inventory bag, Inventory purse, boolean getNew) {
		int val = item.getMoneyValue();
		purse.addGold(val);
		if (getNew) {
			bag.swapWeapon(new Weapon(extra.zeroOut(item.getLevel()-2)+1));
		}
		if (extra.getPrint()) {
			return;
		}
		extra.println("The " + item.getName() + " "+extra.pluralIs(item.getBaseName())+" sold for " + World.currentMoneyDisplay(val) + "." );
	}
	
	//purse.addAether(item.getCost());
	
	/**
	 * Sells and item from inventory bag, and puts the resulting cash in that inventory.
	 * @param item - the item you want to sell (Item)
	 * @param bag - the inventory to sell from and to put the cash in (Inventory)
	 * @param getNew - (boolean), if you want a new item to replace the sold item, put true.
	 * Otherwise, the current item is kept, and the calling function is responsible for getting rid of it.
	 */
	public static void sellItem(Weapon item, Inventory bag, boolean getNew) {
		sellItem(item,bag,bag,getNew);
	}
	
	/**
	 * Sells and item from inventory bag, and puts the resulting cash in inventory purse.
	 * @param item - the item you want to sell (Item)
	 * @param bag - the inventory to sell from (Inventory)
	 * @param purse - where the gold goes (Inventory)
	 * @param getNew - (boolean), if you want a new item to replace the sold item, put true.
	 * Otherwise, the current item is kept, and the calling function is responsible for getting rid of it.
	 */
	public static void sellItem(Armor item, Inventory bag, Inventory purse, boolean getNew) {
		int val = item.getMoneyValue();
		purse.addGold(val);
		if (getNew) {
			boolean soldIt = false;
			int i = 0;
			while (i < 5) {
				if (bag.getArmorSlot(i) == item) {
					bag.swapArmorSlot(new Armor(extra.zeroOut(item.getLevel()-2)+1,i),i);
					soldIt = true;
				}
				i++;
			}
			if (soldIt == false) {
				throw new RuntimeException("Couldn't find the item they were trying to sell, a " + item.getName()+ "!");
			}
		}
		if (extra.getPrint()) {
			return;
		}
		extra.println("The " + item.getName() + " "
				+extra.pluralIs(item.getName())
				+" sold for " + World.currentMoneyDisplay(val)+ "." );
	}
	/**
	 * Sells and item from inventory bag, and puts the resulting cash in that inventory.
	 * @param item - the item you want to sell (int)
	 * @param bag - the inventory to sell from and to put the cash in (Inventory)
	 * @param getNew - (boolean), if you want a new item to replace the sold item, put true.
	 * Otherwise, the current item is kept, and the calling function is responsible for getting rid of it.
	 */
	public static void sellItem(Armor item, Inventory bag, boolean getNew) {
		sellItem(item,bag,bag,getNew);
	}
	
	public static void sellItem(DrawBane item, Inventory purse) {
		int val = item.getValue();
		purse.addGold(val);
		if (extra.getPrint()) {
			return;
		}
		extra.println("The " + item.getName() + " "+extra.pluralIs(item.getName())+" sold for " + World.currentMoneyDisplay(val) + "." );
	}
	
	/**
	 * used for removing items that should no longer exist
	 * <br>
	 * make sure to null them in local inventory yourself
	 * @param item
	 * @param dest
	 */
	public static void aetherifyItem(Item item, Inventory dest) {
		int val = item.getAetherValue();
		dest.addAether(val);
		if (extra.getPrint()) {
			return;
		}
		extra.println("The " + item.getName() + " dissolves into " + val + " aether." );
	}
}