package scimech.units.mounts;

import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mount;

public class Broadside extends Mount {

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = false;
		map.subMaps.add(currentMech.resistMap());
		return map;
	}

	@Override
	public String getName() {
		return "Broadside Mount";
	}

	@Override
	public int baseWeight() {
		return 12;
	}

	@Override
	public int baseComplexity() {
		return 5;
	}

	@Override
	public int baseSlots() {
		return 18;
	}

	@Override
	public float dodgeMult() {
		return 0.8f;
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mount.internalDeserial(s,new Broadside());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
