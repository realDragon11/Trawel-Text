package scimech.units.fixtures;

import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Fixture;
import scimech.mech.TurnSubscriber;

public class HeatVent extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		if (!t.isDummy()) {
			currentMount.clearHeat((int)(4*rating()));
			currentMount.currentMech.clearHeat((int)(6*rating()));
		}
	}

	@Override
	public void roundStart() {

	}

	@Override
	public int heatCap() {
		return 30;
	}

	@Override
	public String getName() {
		return "Heat Vent";
	}

	@Override
	public int getEnergyDraw() {
		return 5;
	}

	@Override
	public String getDescription() {
		return "Clears up to 4 heat from your mount and 6 from your mech";
	}

	@Override
	public int getComplexity() {
		return 5;
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public int getSlots() {
		return 2;
	}
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new HeatVent());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
