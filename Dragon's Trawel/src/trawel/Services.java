package trawel;

import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.Armor;
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
		purse.addGold(item.getMoneyValue());
		if (getNew) {
			bag.swapWeapon(new Weapon(extra.zeroOut(item.getLevel()-2)+1));
		}
		extra.println("The " + item.getName() + " "+extra.pluralIs(item.getBaseName())+" sold for " + item.getAetherValue()+ " " + extra.choose("gold pieces","pieces of gold","gold") + "." );
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
		purse.addGold(item.getMoneyValue());
		extra.println("The " + item.getName() + " "
				+extra.pluralIs(item.getName())
				+" sold for " + World.currentMoneyDisplay(item.getAetherValue())+ "." );
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
	
	/**
	 * used for removing items that should no longer exist
	 * @param item
	 * @param dest
	 */
	public static void aetherifyItem(Item item, Inventory dest) {
		int val = item.getAetherValue();
		dest.addAether(val);
		extra.println("The " + item.getName() + "dissolves into " + val + " aether." );
	}
	
	/**
	 * Generates a random amount of items ~level inputted, which are then browsed by an inventory
	 * with intelligence smarts. Better items will be bought, selling the items that they already have.
	 * @param inv (inventory) - the inventory of the browser
	 * @param smarts (int)
	 * @param level (int)
	 */
	@Deprecated
	public static void storeFront(Inventory inv, int smarts, int level) {
		level = Math.max(level+extra.getRand().nextInt(5)-2,1);
		extra.println("");
		extra.println(extra.choose("They start browsing a shop.","They come across a wandering merchant.","They take the chance to go to the nearest store."));
		extra.println("They start with " + inv.getGold() + extra.choose(" gold."," gold pieces."," pieces of gold."));
		int i = (int) (Math.random()*6);
		while (i > 0) {
			if (extra.chanceIn(1,3)) {
				Weapon weap = new Weapon(extra.zeroOut(level + (int)(Math.random()*6)-3)+1);
				if (weap.getAetherValue() <= inv.getGold() && AIClass.compareItem(inv.getHand(),weap, smarts, false,null) ) {
					inv.setGold(inv.getGold()-weap.getAetherValue());
					sellItem(inv.getHand(),inv, false);
					inv.swapWeapon(weap);
					extra.println(" They bought them for " + weap.getAetherValue() + extra.choose(" gold."," gold pieces."," pieces of gold."));
				}
			}else {
				Armor arm = new Armor(extra.zeroOut(level + (int)(Math.random()*6)-3)+1);
				int slot = arm.getArmorType();
				if (arm.getAetherValue() <= inv.getGold() && AIClass.compareItem(inv.getArmorSlot(slot),arm, smarts, false,null)) {
					inv.setGold(inv.getGold()-arm.getAetherValue());
					sellItem(inv.getArmorSlot(slot),inv, false);
					inv.swapArmorSlot(arm,slot);
					extra.println(" They bought them for " + arm.getAetherValue() + extra.choose(" gold."," gold pieces."," pieces of gold."));
				}
			}
			i--;
		}
		extra.println(extra.choose("They finish browsing.","They wrap up their shopping."));
		extra.println("They end with " + inv.getGold() + extra.choose(" gold."," gold pieces."," pieces of gold."));
		extra.println("");
	}
}