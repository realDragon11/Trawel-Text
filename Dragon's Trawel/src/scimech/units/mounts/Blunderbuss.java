package scimech.units.mounts;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.mech.Mount;
import trawel.extra;

public class Blunderbuss extends Mount {

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = false;
		map.put(DamageMods.AP,1.2f,1f);
		map.put(DamageMods.HOLLOW,0.8f,1f);
		map.subMaps.add(currentMech.resistMap());
		return map;
	}

	@Override
	public String getName() {
		return "Blunderbuss Mount";
	}

	@Override
	public int baseWeight() {
		return 5;
	}

	@Override
	public int baseComplexity() {
		return 1;
	}

	@Override
	public int baseSlots() {
		return 6;
	}

	@Override
	public float dodgeMult() {
		return 0.5f;
	}

}
