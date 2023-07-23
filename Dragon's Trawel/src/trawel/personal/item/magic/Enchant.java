package trawel.personal.item.magic;
/**
 * @author Brian Malone
 * 2/5/2018
 * Enchantments are mostly just a passthrough class:
 * There was a plan to add constant and on-hit enchantments, but
 * on-hit was dropped.
 */
public abstract class Enchant implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//instance methods
	public abstract Enchant.Type getEnchantType();

	public abstract void display(int i);
	
	public int enchantstyle = 0;
	
	public abstract int getGoldMod();
	public abstract float getGoldMult();
	
	
	public enum Type{
		CONSTANT, HIT
	}


	public float getAimMod() {return 1;}

	public float getDamMod() {return 1;}

	public float getDodgeMod() {return 1;}

	public float getHealthMod() {return 1;}

	public float getSpeedMod() {return 1;}

	public float getFireMod() {
		return 0;
	}

	public float getShockMod() {
		return 0;
	}

	public float getFreezeMod() {
		return 0;
	}

}
