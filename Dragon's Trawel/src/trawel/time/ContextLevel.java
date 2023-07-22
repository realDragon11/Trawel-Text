package trawel.time;

public enum ContextLevel {
	FEATURE(1), TOWN(2), ISLAND(3), WORLD(4), PLANE(5);
	private final int tier;
	ContextLevel(int i){
		tier = i;
	}
	public int tier() {
		return tier;
	}
}
