package scimech.units.systems;

import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class FrostAegis extends Systems {

	@Override
	public int getComplexity() {
		return 10;
	}

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		if (powered) {
			float r = rating();
			map.put(DamageTypes.BURN, extra.lerp(1f,0.5f, r), extra.lerp(1f,0.7f, r));
			map.put(DamageTypes.SHOCK, extra.lerp(1f,0.9f, r), extra.lerp(1f,0.9f, r));
			map.put(DamageTypes.KINETIC, extra.lerp(1f,0.8f, r), extra.lerp(1f,0.8f, r));
			map.put(DamageTypes.BLAST, extra.lerp(1f,0.7f, r), extra.lerp(1f,0.7f, r));
		}
		return map;
	}

	@Override
	public String getTitleAdditions() {
		return "";
	}

	@Override
	public String getName() {
		return "Frost Aegis";
	}

	@Override
	public String getDescription() {
		return "Provides good protection against burn. Consumes power, and cools your mech slightly.";
	}

	@Override
	public int getEnergyDraw() {
		return 6;
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		currentMech.clearHeat((int) (3*rating()));
		
	}

	@Override
	public int getWeight() {
		return 6;
	}

}
