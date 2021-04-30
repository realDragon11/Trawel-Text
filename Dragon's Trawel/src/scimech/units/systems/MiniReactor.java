package scimech.units.systems;

import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;

public class MiniReactor extends Systems {
	
	public MiniReactor() {
		passive = true;
		powered = true;
	}

	@Override
	public int getComplexity() {
		return 3;
	}

	@Override
	public ResistMap resistMap() {
		return null;
	}

	@Override
	public String getTitleAdditions() {
		float r = rating();
		int pow = Math.round(2*r);
		return (pow > 0) ? " " + pow : " offline";
	}

	@Override
	public String getName() {
		return "Mini-Reactor";
	}

	@Override
	public String getDescription() {
		return "Produces up to 2 Energy, without creating Heat.";
	}

	@Override
	public int getEnergyDraw() {
		return Math.round(-2*rating());
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		//no effect

	}

	@Override
	public int getWeight() {
		return 1;
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new MiniReactor());
	}

}
