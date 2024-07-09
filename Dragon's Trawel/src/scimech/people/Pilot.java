package scimech.people;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.methods.randomLists;

public class Pilot {

	protected String name;
	
	protected TraitKeeper keeper = new TraitKeeper();
	
	public String getName() {
		return name;
	}
	
	public void statistics() {
		Print.println(name);
		Print.println(keeper.toString());
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
		return Rand.choose(Trait.HARDENED,Trait.DUELIST,Trait.GUN_NUT,Trait.LASER_SPEC,Trait.LASER_SPEC,Trait.LOBBER,Trait.GREASE_MONKEY);
	}
	public Trait randCapstoneTrait() {
		return Rand.choose(Trait.ACCURATE,Trait.MOBILE,Trait.TOUGH,Trait.EVASIVE);
	}
	
	public int getTrait(Trait t) {
		return keeper.getTrait(t);
	}
}
