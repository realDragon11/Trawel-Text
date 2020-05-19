
public enum Seed {
	GARLIC,APPLE;
	
	public static Seed randSeed() {
		return extra.choose(GARLIC);
	}
}
