package trawel.personal.item;

import java.awt.Color;

import trawel.extra;
import trawel.personal.item.magic.Enchant;

/**
 * 
 * @author dragon
 * 2/8/2018
 * 
 * an abstract class for use with the Weapon and Armor classes.
 */
public abstract class Item implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	protected int level;
	public int bloodSeed = extra.randRange(0,2000);
	private float bloodCount = 0;

	//abstracted instance methods
	/**
	 * get the enchantment on the item
	 * @return (EnchantConstant)
	 */
	public abstract Enchant getEnchant();
	/**
	 * Returns the cost of the item
	 * @return (int) - aether cost
	 */
	public abstract int getAetherValue();
	
	public int getMoneyValue() {
		return getAetherValue()/10;//DOLATER maybe conversion and exchange rates???
	}
	
	public abstract void display(int style);
	public abstract void display(int style,float markup);
	public String getModiferName() {
		return getModiferNameColored(level);
	}
	
	public static String getModiferNameColored(int inlevel) {
		switch (inlevel) {
		case 0: return extra.inlineColor(new Color(60,60,60))+"broken[c_white]";
		case 1: return extra.inlineColor(new Color(128,128,128))+"crude[c_white]";
		case 2: return extra.inlineColor(new Color(160,160,160))+"shoddy[c_white]";
		case 3: return extra.inlineColor(new Color(220,220,220))+"poor[c_white]";
		case 4: return (extra.inlineColor(extra.colorMix(Color.PINK,Color.WHITE,.5f)))+"fair[c_white]";//pink
		case 5: return (extra.inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f)))+"okay[c_white]";//green
		case 6: return (extra.inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f)))+"good[c_white]";//blue
		case 7: return (extra.inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.5f)))+"great[c_white]";//purple
		case 8: return (extra.inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f)))+"heroic[c_white]";//orange
		case 9: return (extra.inlineColor(extra.colorMix(Color.yellow,Color.WHITE,.5f)))+"amazing[c_white]";//yellow
		case 10: return (extra.PRE_RED)+"masterwork[c_white]";//red
		case 11: return extra.inlineColor(Color.RED)+"legendary[c_white]";//vibrant red
		}
		return extra.inlineColor(Color.RED)+"legendary+"+(inlevel-10)+"[c_white]";//vibrant red
	}
	
	public abstract ItemType getType();
	
	public enum ItemType{
		RACE, ARMOR, WEAPON
	}
	
	public abstract void levelUp();
	
	public int getLevel() {
		return level;
	}
	
	public void wash() {
		bloodSeed = extra.randRange(0,2000);
		bloodCount = 0;
	}
	
	public int getBloodCount() {
		return (int)bloodCount;
	}
	
	public void addBlood(float i) {
		bloodCount+=i;
		if (bloodCount > 16) {
			bloodCount = 16;
		}
	}
	
	/**
	 * FIXME: rename to aetherloot or something
	 * 
	 * used to indicate things that can't be wielded or worn but still can be sold
	 * things that CAN be looted normally still should return true, since Huge Battles
	 * will go over the unclaimed items in a final pass to convert to gold
	 * @return true if can be looted for money
	 */
	public abstract boolean canAetherLoot();
	
	public abstract String getName();
	public abstract String storeString(float markup, boolean canShow);
}
