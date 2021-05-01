package scimech.units.systems;

import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;

public class InternalRepair extends Systems {
	
	public InternalRepair() {
		passive = true;
		powered = true;
	}

	@Override
	public int getBaseComplexity() {
		return 8;
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
		return "Internal Repair";
	}

	@Override
	public String getDescription() {
		return "Repairs mounts and systems each turn.";
	}

	@Override
	public int getEnergyDraw() {
		return Math.round(4);
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		currentMech.repair((int) (10*rating()));

	}

	@Override
	public int getWeight() {
		return 1;
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new InternalRepair());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
