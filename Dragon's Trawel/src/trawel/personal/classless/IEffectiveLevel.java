package trawel.personal.classless;

import trawel.extra;

public interface IEffectiveLevel {

	public int getLevel();
	
	default int getEffectiveLevel() {
		return getLevel()+10;
	}
	
	default float getUnEffectiveLevel() {
		return unEffective(getEffectiveLevel());
	}

	public static float effective(int w_lvl) {
		return w_lvl+10;
	}
	
	public static float unEffective(float eLevel) {
		return eLevel/10;
	}
	
	public static float unclean(int in) {
		return unEffective(effective(in));
	}

	public static int clean(int in) {
		return (int) unEffective(effective(in));
	}
	public static int cleanLHP(double in, double percent) {
		//10f*((in+10))
		return (int) Math.ceil((in+10)*10f*percent);
	}
	public static double uncleanLHP(double in, double percent) {
		//10f*((in+10))
		return (in+10)*10f*percent;
	}
	
	/**
	 * 
	 * @param level (1+)
	 * @param amountMult the amount of intensity returned max per unLevel
	 * @param lowPer the 'low roll' of the reward, ie .5f would equal 50% to 100% of amountMult uneffective
	 * @return 
	 */
	public static int cleanRangeReward(int level, float amountMult, float lowPer) {
		return Math.round(extra.lerp(1,amountMult*IEffectiveLevel.unclean(level),extra.lerp(lowPer,1,extra.randFloat())));
	}
}
