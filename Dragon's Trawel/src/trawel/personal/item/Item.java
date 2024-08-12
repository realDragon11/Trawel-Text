package trawel.personal.item;

import java.awt.Color;

import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.solid.Material;
import trawel.personal.people.Player;
import trawel.towns.features.services.Store;

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
	public int bloodSeed = Rand.randRange(0,2000);
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
		return TrawelColor.inlineColor(new Color(val,val,val))+"+"+level+TrawelColor.COLOR_RESET;
	}
	
	private static String[] modifiersColored = new String[] {
			//0
			TrawelColor.inlineColor(new Color(60,60,60))+"broken"+TrawelColor.COLOR_RESET,
			//1
			TrawelColor.inlineColor(new Color(80,80,80))+"crude"+TrawelColor.COLOR_RESET,
			//2
			TrawelColor.inlineColor(new Color(140,140,140))+"shoddy"+TrawelColor.COLOR_RESET,
			//3
			TrawelColor.inlineColor(new Color(180,180,180))+"poor"+TrawelColor.COLOR_RESET,
			//4
			TrawelColor.inlineColor(new Color(220,220,220))+"subpar"+TrawelColor.COLOR_RESET,
			//5
			TrawelColor.inlineColor(new Color(240,240,240))+"fine"+TrawelColor.COLOR_RESET,
			//6
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.GREEN,Color.WHITE,.5f))+"good"+TrawelColor.COLOR_RESET,
			//7
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.WHITE,.5f))+"great"+TrawelColor.COLOR_RESET,
			//8
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.MAGENTA,Color.WHITE,.5f))+"amazing"+TrawelColor.COLOR_RESET,
			//9
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.YELLOW,Color.WHITE,.5f))+"heroic"+TrawelColor.COLOR_RESET,
			//10
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.ORANGE,Color.WHITE,.5f))+"masterwork"+TrawelColor.COLOR_RESET,
			//11
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.5f))+"legendary"+TrawelColor.COLOR_RESET,
			//12, -.5 * 4
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.3f))+"artifact"+TrawelColor.COLOR_RESET,
			//13, -.5 * 3
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.15f))+"artifact"+TrawelColor.COLOR_RESET,
			//14, -.5 * 2
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.05f))+"artifact"+TrawelColor.COLOR_RESET,
			//15+ -.5 * 1
			TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,0f))+"artifact"+TrawelColor.COLOR_RESET
	};
	
	public static String getModiferNameColored(int inlevel) {
		return modifiersColored[extra.clamp(inlevel,0,15)];
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
		bloodSeed = Rand.randRange(0,2000);
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
	 * remove a negative quality if one is present
	 * @return how many were removed
	 */
	public int temperNegQuality(int amount) {
		return 0;
	}
	
	public boolean hasNegQuality() {
		return false;
	}
	
	/**
	 * attempt to add a positive quality based on type (weapon type and armor style)
	 * @return how many positive were added
	 */
	public int improvePosQuality(int amount) {
		return 0;
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
