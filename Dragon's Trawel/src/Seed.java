
public enum Seed {
	GARLIC("garlic seed"),APPLE("apple seed"),BEE("bee larva");
	
	private String name;
	Seed(String n){
		name = n;
	}
	public static Seed randSeed() {
		return extra.choose(GARLIC,APPLE);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
