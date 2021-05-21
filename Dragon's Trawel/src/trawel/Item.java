package trawel;

import java.awt.Color;

/**
 * 
 * @author Brian Malone
 * 2/8/2018
 * 
 * an abstract class for use with the Weapon and Armor classes.
 */
public abstract class Item implements java.io.Serializable{
	
	protected int level;
	public int bloodSeed = extra.randRange(0,2000);
	private float bloodCount = 0;

	//abstracted instance methods
	/**
	 * get the enchantment on the item
	 * @return (EnchantConstant)
	 */
	public abstract EnchantConstant getEnchant();
	/**
	 * Returns the cost of the item
	 * @return (int) - gold cost
	 */
	public abstract int getCost();
	public abstract void display(int style);
	public abstract void display(int style,float markup);
	public String getModiferName() {
		/*
		switch (level) {
		case 0: return "broken";
		case 1: return "crude";
		case 2: return "shoddy";
		case 3: return "poor";
		case 4: return "fair";
		case 5: return "okay";
		case 6: return "good";
		case 7: return "great";
		case 8: return "heroic";
		case 9: return "amazing";
		case 10: return "masterwork";
		}
		return "legendary";*/
		return getModiferNameColored(level);
	}
	
	public static String getModiferName(int inlevel) {
		/*
		switch (inlevel) {
		case 0: return "broken";
		case 1: return "crude";
		case 2: return "shoddy";
		case 3: return "poor";
		case 4: return "fair";
		case 5: return "okay";
		case 6: return "good";
		case 7: return "great";
		case 8: return "heroic";
		case 9: return "amazing";
		case 10: return "masterwork";
		}
		return "legendary";*/
		return getModiferNameColored(inlevel);
	}
	
	public static String getModiferNameColored(int inlevel) {
		switch (inlevel) {
		case 0: return extra.inlineColor(new Color(60,60,60))+"broken[c_white]";
		case 1: return extra.inlineColor(new Color(128,128,128))+"crude[c_white]";
		case 2: return extra.inlineColor(new Color(160,160,160))+"shoddy[c_white]";
		case 3: return extra.inlineColor(new Color(220,220,220))+"poor";
		case 4: return (extra.inlineColor(extra.colorMix(Color.PINK,Color.WHITE,.5f)))+"fair[c_white]";//pink
		case 5: return (extra.inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f)))+"okay[c_white]";//green
		case 6: return (extra.inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f)))+"good[c_white]";//blue
		case 7: return (extra.inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.5f)))+"great[c_white]";//purple
		case 8: return (extra.inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f)))+"heroic[c_white]";//orange
		case 9: return (extra.inlineColor(extra.colorMix(Color.yellow,Color.WHITE,.5f)))+"amazing[c_white]";//yellow
		case 10: return (extra.inlineColor(extra.colorMix(Color.RED,Color.WHITE,.5f)))+"masterwork[c_white]";//red
		}
		return extra.inlineColor(Color.RED)+"legendary[c_white]";//vibrant red
	}
	
	public abstract String getType();
	
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
}
