package trawel.personal.classless;

import trawel.core.Rand;
import trawel.helper.methods.extra;

public interface IEffectiveLevel {

	public int getLevel();
	
	default int getEffectiveLevel() {
		return getLevel()+10;
	}
	
	default float getUnEffectiveLevel() {
		return unEffective(getEffectiveLevel());
	}
	
	//attributes start at 100 and then go up
	//many level ups award multiple stats distributed differently
	/*
	 * how many attributes should be granted with each on average:
	 * 3 normal skills ~= 5
	 * 2 normal skills ~= 15
	 * 1 normal skills ~= 30
	 */
	
	/**
	 * primary scaling (+15), secondary scaling (+10) [15 * 2/3=10], offstat scaling (+5) [15/3=5], minimal scaling (+3) [15/5=3]
	 */
	
	/**
	 * 20 v 100 base, +3<br>
	 * 1/5th scaling
	 */
	public static int attributeChallengeTrival(int level) {
		return 20+(3*level);
	}
	
	/**
	 * 30 v 100 base, +5<br>
	 * 1/3rd scaling
	 */
	public static int attributeChallengeEasy(int level) {
		return 30+(5*level);
	}
	
	/**
	 * 50 v 100 base, +7<br>
	 * 1/2th scaling
	 */
	public static int attributeChallengeMedium(int level) {
		return 50+(7*level);
	}
	
	/**
	 * 70 v 100 base, +10<br>
	 * 2/3rds scaling
	 */
	public static int attributeChallengeHard(int level) {
		return 70+(10*level);
	}
	/**
	 * 100 v 100 base, +15<br>
	 * 100% scaling
	 */
	public static int attributeChallengeImpossible(int level) {
		return 100+(15*level);
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
		return Math.round(extra.lerp(1,amountMult*IEffectiveLevel.unclean(level),extra.lerp(lowPer,1,Rand.randFloat())));
	}
}
