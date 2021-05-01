package scimech.units.systems;

import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;

public class Ramjet extends Systems {

	public Ramjet() {
		passive = true;
		powered = true;
	}
	
	@Override
	public int getBaseComplexity() {
		return 4;
	}

	@Override
	public ResistMap resistMap() {
		return null;
	}

	@Override
	public String getTitleAdditions() {
		return "";
	}

	@Override
	public String getName() {
		return "Ramjet";
	}

	@Override
	public String getDescription() {
		return "Produces up to 3 dodge and 4 speed. Also produces 2 heat.";
	}

	@Override
	public int getEnergyDraw() {
		return 3;
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		currentMech.addDodgeBonus((int)(3*rating()));
		currentMech.addSpeed((int)(4*rating()));
		currentMech.takeHeat(2);
	}

	@Override
	public int getWeight() {
		return 4;
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new Ramjet());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
