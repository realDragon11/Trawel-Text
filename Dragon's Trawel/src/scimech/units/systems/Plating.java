package scimech.units.systems;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class Plating extends Systems {

	@Override
	public int getComplexity() {
		return 1;
	}

	@Override
	public ResistMap resistMap() {
		//normally would check for powered
		ResistMap map = new ResistMap();
		map.isSub = true;
		float r = rating();
		//map.put(DamageMods.AP, 1f, 1f);
		map.put(DamageMods.NORMAL, extra.lerp(1f, 0.9f,r), extra.lerp(1f, 0.9f,r));
		map.put(DamageMods.HOLLOW, extra.lerp(1f, 0.7f,r), extra.lerp(1f, 0.7f,r));
		return map;
	}

	@Override
	public String getTitleAdditions() {
		return "";
	}

	@Override
	public String getName() {
		return "plating";
	}

	@Override
	public String getDescription() {
		return "provides protection without an energy cost";
	}

	@Override
	public int getEnergyDraw() {
		return 0;
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		// NONE

	}

	@Override
	public int getWeight() {
		return 3;
	}

}
