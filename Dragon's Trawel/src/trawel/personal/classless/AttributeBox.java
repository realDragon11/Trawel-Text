package trawel.personal.classless;

import trawel.personal.Person;

public class AttributeBox {
	
	public final Person owner;
	
	private int strength, dexterity;
	
	public AttributeBox(Person p) {
		owner = p;
		
		reset();
	}

	public void reset() {
		strength = 0;
		dexterity = 0;
	}
	
	public void process(Feat f) {
		strength += f.getStrength();
		dexterity += f.getDexterity();
	}
	
	public void process(Perk p) {
		strength += p.getStrength();
		dexterity += p.getDexterity();
	}
	
	public void process(Archetype a) {
		strength += a.getStrength();
		dexterity += a.getDexterity();
	}

	public int getStrength() {
		return strength;
	}

	public int getDexterity() {
		return dexterity;
	}
}
