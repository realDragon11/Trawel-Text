package trawel;
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
		return "legendary";
	}
	
	public static String getModiferName(int inlevel) {
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
		return "legendary";
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
