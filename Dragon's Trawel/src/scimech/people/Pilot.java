package scimech.people;

import trawel.randomLists;

public class Pilot {

	protected String name;
	
	protected TraitKeeper keeper = new TraitKeeper();
	
	public String getName() {
		return name;
	}
	
	public Pilot() {
		name = randomLists.randomFirstName();
	}
	
	public int getTrait(Trait t) {
		return keeper.getTrait(t);
	}
}
