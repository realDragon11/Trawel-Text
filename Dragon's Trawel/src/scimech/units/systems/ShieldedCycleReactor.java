package scimech.units.systems;

import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;

public class ShieldedCycleReactor extends Systems {

	public ShieldedCycleReactor() {
		passive = true;
		powered = true;
	}
	
	@Override
	public int getBaseComplexity() {
		return 12;
	}

	@Override
	public ResistMap resistMap() {
		return null;
	}

	@Override
	public String getTitleAdditions() {
		float r = rating();
		int pow = Math.round(10*r);
		return (pow > 0) ? " " + pow : " offline";
	}

	@Override
	public String getName() {
		return "shielded cycle reactor";
	}

	@Override
	public String getDescription() {
		return "Produces up to 12 energy and 2 heat. Exceptionally heavy.";
	}

	@Override
	public int getEnergyDraw() {
		return (int) (-12*rating());
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		currentMech.takeHeat(2);
	}

	@Override
	public int getWeight() {
		return 20;
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new ShieldedCycleReactor());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.SARATOGA_SYSTEMS;
	}

}
