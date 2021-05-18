package scimech.units.systems;

import rtrawel.unit.DamageType;
import scimech.combat.DamageEffect;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class UnshieldedCycleReactor extends Systems {

	public UnshieldedCycleReactor() {
		passive = true;
		powered = true;
	}
	
	@Override
	public int getBaseComplexity() {
		return 12;
	}

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		if (powered) {
			map.put(DamageTypes.SHOCK,1.2f,1f);
		}else {
			map.put(DamageTypes.SHOCK,1.1f,1f);
		}
		return map;
	}

	@Override
	public String getTitleAdditions() {
		float r = rating();
		int pow = (int) (12*Math.pow(r,2));
		return (pow > 0) ? " " + pow : " offline";
	}

	@Override
	public String getName() {
		return "unshielded cycle reactor";
	}

	@Override
	public String getDescription() {
		return "Produces up to 12 energy and 2 heat. EMPs your mech each turn, weak to damage.";
	}

	@Override
	public int getEnergyDraw() {
		return (int) (-12*Math.pow(rating(),2));//weak to damage
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		currentMech.takeHeat(2);
		currentMech.takeEMPDamage(30);
	}

	@Override
	public int getWeight() {
		return 4;
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new UnshieldedCycleReactor());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.SARATOGA_SYSTEMS;
	}

}
