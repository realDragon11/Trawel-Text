package trawel.personal;

import trawel.battle.attacks.TargetFactory;
import trawel.personal.classless.Skill;

public class DummyPerson extends Person {
	
	public static DummyPerson single; 
	
	public static void init() {
		single = new DummyPerson();
	}
	
	protected DummyPerson() {
		super();
	}
	
	@Override
	public double getTornCalc() {
		return 1;
	}
	
	@Override
	public boolean hasSkill(Skill o) {
		return false;
	}
	
	@Override
	public double getConditionForPart() {
		return 1;
	}

}
