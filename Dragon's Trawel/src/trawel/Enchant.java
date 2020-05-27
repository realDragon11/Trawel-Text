package trawel;
/**
 * @author Brian Malone
 * 2/5/2018
 * Enchantments are mostly just a passthrough class:
 * There was a plan to add constant and on-hit enchantments, but
 * on-hit was dropped.
 */
public abstract class Enchant implements java.io.Serializable{
	//instance methods
	public abstract String getEnchantType();

	public abstract void display(int i);
	
	public int enchantstyle = 0;

}
