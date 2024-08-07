package trawel.battle.attacks;

import trawel.battle.Combat.AttackReturn;

public interface IAttack {

	public boolean physicalDamage();
	
	public String getName();
	public String getDesc();
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
	/**
	 * @return 0 if not present or isn't elemental
	 */
	public int getIgnite();
	/**
	 * @return 0 if not present or isn't elemental
	 */
	public int getFrost();
	/**
	 * @return 0 if not present or isn't elemental
	 */
	public int getElec();
	/**
	 * @return 0 if not present or isn't elemental
	 */
	public int getDecay();
	
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
	public static int getIgniteFromWeap(int[] vals) {
		return vals[3];
	}
	public static int getFrostFromWeap(int[] vals) {
		return vals[4];
	}
	public static int getElecFromWeap(int[] vals) {
		return vals[5];
	}
	public static int getDecayFromWeap(int[] vals) {
		return vals[6];
	}
	public static double getSharpFromWeap(double[] vals) {
		return vals[0];
	}
	public static double getBluntFromWeap(double[] vals) {
		return vals[1];
	}
	public static double getPierceFromWeap(double[] vals) {
		return vals[2];
	}
	public static double getIgniteFromWeap(double[] vals) {
		return vals[3];
	}
	public static double getFrostFromWeap(double[] vals) {
		return vals[4];
	}
	public static double getElecFromWeap(double[] vals) {
		return vals[5];
	}
	public static double getDecayFromWeap(double[] vals) {
		return vals[6];
	}

	public int getTotalDam();
}
