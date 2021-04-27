package scimech.units.mounts;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.mech.Mount;

public class Foil extends Mount {

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = false;
		map.put(DamageMods.HOLLOW,1.5f,1f);//major weakness
		map.subMaps.add(currentMech.resistMap());
		return map;
	}

	@Override
	public String getName() {
		return "Foil Mount";
	}

	@Override
	public int baseWeight() {
		return 2;
	}

	@Override
	public int baseComplexity() {
		return 2;
	}

	@Override
	public int baseSlots() {
		return 4;
	}

	@Override
	public float dodgeMult() {
		return 1.2f;
	}

}
