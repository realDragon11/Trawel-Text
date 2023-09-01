package trawel.personal;

import trawel.Effect;
import trawel.personal.classless.Skill;
import trawel.personal.item.DummyInventory;

public class DummyPerson extends Person {
	
	private DummyInventory directRef;
	
	public DummyPerson(DummyInventory di) {
		super();
		bag = di;
		directRef = di;
	}
	
	@Override
	public double getWoundDodgeCalc() {
		return 1;
	}
	
	@Override
	public boolean hasSkill(Skill o) {
		return false;
	}
	
	@Override
	public double getConditionForPart(int i) {
		return 1;
	}
	
	@Override
	public boolean hasEffect(Effect e) {
		return false;
	}

	public Person atLevel(int level) {
		directRef.atLevel(level);
		return this;
	}

}
