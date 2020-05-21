
public enum Seed {
	GARLIC("garlic seed"),APPLE("apple seed"),BEE("bee larva"),ENT("ent sapling"),PUMPKIN("pumpkin seeds");
	
	private String name;
	Seed(String n){
		name = n;
	}
	public static Seed randSeed() {
		return extra.choose(GARLIC,APPLE,PUMPKIN);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
