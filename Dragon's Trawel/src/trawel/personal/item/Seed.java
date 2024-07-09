package trawel.personal.item;

import trawel.core.Rand;

public enum Seed {
	EMPTY(""),
	
	SEED_GARLIC("garlic seed"),
	GROWN_GARLIC("garlic"),
	
	SEED_APPLE("apple seed"),
	GROWN_APPLE("apple tree"),
	HARVESTED_APPLE("picked apple tree"),
	
	SEED_BEE("bee larva"),
	GROWN_BEE("bee hive"),
	HARVESTED_BEE("angry bee hive"),
	
	SEED_ENT("ent sapling"),
	GROWN_ENT("ent"),
	
	SEED_PUMPKIN("pumpkin seed"),
	GROWN_PUMPKIN("pumpkin patch"),
	HARVESTED_PUMPKIN("empty pumpkin patch"),
	
	SEED_EGGCORN("eggcorn seed"),
	GROWN_EGGCORN("eggcorn"),
	
	SEED_TRUFFLE("truffle spores"),
	GROWN_TRUFFLE("truffle"),
	
	SEED_FAE("fairy dust"),
	GROWN_FAE("unicorn horn"),
	
	SEED_FUNGUS("corpsebloom spores"),
	GROWN_FUNGUS("corpsebloom")
	;
	
	private String name;
	Seed(String n){
		name = n;
	}
	public static Seed randSeed() {
		return Rand.choose(SEED_GARLIC,SEED_APPLE,SEED_PUMPKIN,SEED_EGGCORN,SEED_TRUFFLE);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
