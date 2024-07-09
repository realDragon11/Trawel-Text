package scimech.people;

import trawel.helper.methods.extra;
import trawel.helper.methods.randomLists;

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
		for (int i = 0; i < 6;i++) {
			if (keeper.addTrait(randStartingTrait() , 3)) {
				i--;
			}
		}
		keeper.addTrait(randCapstoneTrait(), 1);
	}
	
	public Trait randStartingTrait() {
		return extra.choose(Trait.HARDENED,Trait.DUELIST,Trait.GUN_NUT,Trait.LASER_SPEC,Trait.LASER_SPEC,Trait.LOBBER,Trait.GREASE_MONKEY);
	}
	public Trait randCapstoneTrait() {
		return extra.choose(Trait.ACCURATE,Trait.MOBILE,Trait.TOUGH,Trait.EVASIVE);
	}
	
	public int getTrait(Trait t) {
		return keeper.getTrait(t);
	}
}
