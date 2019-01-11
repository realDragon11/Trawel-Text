/**
 * 
 * @author Brian Malone
 * 2/8/2018
 * 
 * an abstract class for use with the Weapon and Armor classes.
 */
public abstract class Item implements java.io.Serializable{
	
	protected int level;

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
	
	public abstract String getType();
	
	public abstract void levelUp();
}
