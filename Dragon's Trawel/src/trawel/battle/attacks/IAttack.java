package trawel.battle.attacks;

import trawel.battle.Combat.AttackReturn;

public interface IAttack {

	public boolean physicalDamage();
	
	public String getName();
	public String getDesc();
	public AttackType getType();
	public enum AttackType{
		REAL_WEAPON, FAKE_WEAPON, SKILL 
	}
	public int valueSize();
	public String fluff(AttackReturn attret);
	/**
	 * @return 0 if not present or isn't physical
	 */
	public int getSharp();
	/**
	 * @return 0 if not present or isn't physical
	 */
	public int getBlunt();
	/**
	 * @return 0 if not present or isn't physical
	 */
	public int getPierce();
	
	public double getWarmup();
	public double getCooldown();
	
	public double getHitMult();
	/**
	 * value used so that the class doesn't have to constantly shift it's vals
	 * <br>
	 * lets you decrease an internal value instead of knowing which vals need to be decreased
	 */
	public double getPotencyMult();
	public void multPotencyMult(double multMult);
	
	public static int getSharpFromWeap(int[] vals) {
		return vals[0];
	}
	public static int getBluntFromWeap(int[] vals) {
		return vals[1];
	}
	public static int getPierceFromWeap(int[] vals) {
		return vals[2];
	}

	public int getTotalDam();
}
