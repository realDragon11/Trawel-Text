package scimech.people;

import trawel.extra;
import trawel.randomLists;

public class Pilot {

	protected String name;
	
	protected TraitKeeper keeper = new TraitKeeper();
	
	public String getName() {
		return name;
	}
	
	public void statistics() {
		extra.println(name);
		extra.println(keeper.toString());
	}
	
	public Pilot() {
		name = randomLists.randomFirstName();
		keeper.addTrait(randStartingTrait() , 1);
		keeper.addTrait(randStartingTrait() , 1);
		keeper.addTrait(randStartingTrait() , 1);
	}
	
	public Trait randStartingTrait() {
		return extra.choose(Trait.HARDENED,Trait.DUELIST,Trait.GUN_NUT,Trait.LASER_SPEC,Trait.LASER_SPEC,Trait.LOBBER,Trait.GREASE_MONKEY);
	}
	
	public int getTrait(Trait t) {
		return keeper.getTrait(t);
	}
}
