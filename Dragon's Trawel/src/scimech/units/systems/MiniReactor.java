package scimech.units.systems;

import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;

public class MiniReactor extends Systems {

	@Override
	public int getComplexity() {
		return 2;
	}

	@Override
	public ResistMap resistMap() {
		return null;
	}

	@Override
	public String getTitleAdditions() {
		float r = rating();
		return (r > .5f) ? " " + Math.round(2*rating()) : " offline";
	}

	@Override
	public String getName() {
		return "Mini-Reactor";
	}

	@Override
	public String getDescription() {
		return "Produces up to 2 Energy";
	}

	@Override
	public int getEnergyDraw() {
		return Math.round(-2*rating());
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		//no effect

	}

}
