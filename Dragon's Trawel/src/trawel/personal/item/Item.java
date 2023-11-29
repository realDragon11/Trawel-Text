package trawel.personal.item;

import java.awt.Color;

import trawel.extra;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.solid.Material;
import trawel.personal.people.Player;
import trawel.towns.services.Store;

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
		float mult = 1f;
		Material mat = getMat();
		if (mat != null) {
			mult *= mat.moneyMultTradeMult;
		}
		//note that it's more than the rate since the rate determines automatic conversions
		return (int) Math.ceil(getAetherValue()*Player.PURE_AETHER_RATE*Player.TRADE_VALUE_BONUS*mult);//DOLATER maybe conversion and exchange rates???
	}
	
	public abstract void display(int style);
	public abstract void display(int style,float markup);
	public void display(Store s, boolean markedUp,int style) {
		display(style,markedUp ? s.getMarkup() : 1f);
	}
	
	/**
	 * will return null if no mat
	 */
	public Material getMat() {
		return null;
	}
	
	public String getQualityName(int amount) {
		switch (amount) {
		case 0:
			return getModiferNameColored(1);
		case 1:
			return getModiferNameColored(3);
		case 2:
			return getModiferNameColored(5);
		default://starts at good and goes up
			return getModiferNameColored(Math.min(11,3+amount));
		}
	}
	
	public String getLevelName() {
		int val = (int) (255f * ((10f+level)/(20f+level)));
		//int val = Math.min(255,140+(level*10));
		return extra.inlineColor(new Color(val,val,val))+"+"+level+"[c_white]";
	}
	
	public static String getModiferNameColored(int inlevel) {
		switch (inlevel) {
		case 0: return extra.inlineColor(new Color(60,60,60))+"broken[c_white]";
		case 1: return extra.inlineColor(new Color(80,80,80))+"crude[c_white]";
		case 2: return extra.inlineColor(new Color(140,140,140))+"shoddy[c_white]";
		case 3: return extra.inlineColor(new Color(180,180,180))+"poor[c_white]";
		case 4: return extra.inlineColor(new Color(220,220,220))+"subpar[c_white]";
		case 5: return "[c_white]fine";//white
		case 6: return (extra.inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f)))+"good[c_white]";//green
		case 7: return (extra.inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f)))+"great[c_white]";//blue
		case 8: return (extra.inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.5f)))+"amazing[c_white]";//purple
		case 9: return (extra.inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f)))+"heroic[c_white]";//orange
		case 10: return (extra.inlineColor(extra.colorMix(Color.yellow,Color.WHITE,.5f)))+"masterwork[c_white]";//yellow
		case 11: return (extra.PRE_RED)+"legendary[c_white]";//red
		case 12: return extra.inlineColor(Color.RED)+"artifact[c_white]";//vibrant red
		}
		return "unknown";
	}
	
	public abstract ItemType getType();
	
	public enum ItemType{
		RACE, ARMOR, WEAPON
	}
	
	public void levelUp() {
		level++;
		updateStats();
	}
	
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
	 * used to indicate things that can't be wielded or worn but still can be sold
	 * things that CAN be looted normally still should return true, since Huge Battles
	 * will go over the unclaimed items in a final pass to convert to gold
	 * @return true if can be looted for money
	 */
	public abstract boolean canAetherLoot();
	
	public abstract String getName();
	public abstract String getNameNoTier();
	public abstract String storeString(double markup, int canShow);
	
	protected void updateStats() {
		
	}
	
	protected boolean forceDownGradeIf(int lowerlevel) {
		if (level > lowerlevel) {
			level = lowerlevel;
			updateStats();
			return true;
		}
		return false;
	}
	
	public float getEnchantMult() {
		return 0;
	}
	
	public int getQualityTier() {
		return 5;
	}
	
	/**
	 * Swap out the current enchantment for a new one, if a better one is generated.
	 * Returns if a better one was generated or not.
	 * @param level (int)
	 * @return changed enchantment? (boolean)
	 */
	public boolean improveEnchantChance(int level) {
		return false;
	}
}
