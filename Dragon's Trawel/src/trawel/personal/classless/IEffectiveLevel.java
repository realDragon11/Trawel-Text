package trawel.personal.classless;

public interface IEffectiveLevel {

	public int getLevel();
	
	default int getEffectiveLevel() {
		return getLevel()+10;
	}

	public static float effective(int w_lvl) {
		return w_lvl+10;
	}
	
	public static float unEffective(float eLevel) {
		return eLevel/10;
	}
}
