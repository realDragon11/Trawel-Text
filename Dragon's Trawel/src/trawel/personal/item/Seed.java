package trawel.personal.item;

import trawel.extra;

public enum Seed {
	GARLIC("garlic seed"),
	APPLE("apple seed"),
	BEE("bee larva"),
	ENT("ent sapling"),
	PUMPKIN("pumpkin seed"),
	EGGCORN("eggcorn seed"),
	TRUFFLE("truffle spores");
	
	private String name;
	Seed(String n){
		name = n;
	}
	public static Seed randSeed() {
		return extra.choose(GARLIC,APPLE,PUMPKIN,EGGCORN,TRUFFLE);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
