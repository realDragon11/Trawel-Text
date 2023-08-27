package trawel.personal.classless;

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
}
