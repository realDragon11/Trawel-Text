package scimech.units.mounts;

import scimech.combat.DamageMods;
import scimech.combat.ResistMap;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mount;

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
		return 0.7f;
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mount.internalDeserial(s,new Blunderbuss());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
