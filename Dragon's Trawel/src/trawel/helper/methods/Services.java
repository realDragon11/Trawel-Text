package trawel.helper.methods;

import trawel.core.Print;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.towns.contexts.World;

/**
 * 
 * @author dragon
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
		if (newEnchant == null) {
			return theEnchant;
		}
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
	
	public static void sellItem(DrawBane item, Inventory purse) {
		int val = item.getValue();
		purse.addGold(val);
		if (Print.getPrint()) {
			return;
		}
		Print.println("The " + item.getName() + " "+Print.pluralIs(item.getName())+" sold for " + World.currentMoneyDisplay(val) + "." );
	}
	
	/**
	 * used for removing items that should no longer exist
	 * <br>
	 * make sure to null them in local inventory yourself
	 * @param item
	 * @param dest
	 */
	public static void aetherifyItem(Item item, Inventory dest,boolean disp) {
		int val = item.getAetherValue();
		dest.addAether(val);
		if (!disp) {
			return;
		}
		Print.println("The " + item.getName() + " dissolves into " + val + " aether." );
	}
}