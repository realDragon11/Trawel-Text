package scimech.units.mounts;

import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mount;
import trawel.extra;

public class Hoof extends Mount {

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = false;
		map.subMaps.add(currentMech.resistMap());
		return map;
	}

	@Override
	public String getName() {
		return "Hoof Mount";
	}

	@Override
	public int baseWeight() {
		return 7;
	}

	@Override
	public int baseComplexity() {
		return 5;
	}

	@Override
	public int baseSlots() {
		return 4;
	}

	@Override
	public float dodgeMult() {
		return 1.05f;
	}
	
	@Override
	public void bonusEffect(Target t, int damage) {
		float bonus = Math.max(2.5f-(this.currentMech.totalWeight()/100f),0);
		this.currentMech.addDodgeBonus(bonus);
		extra.println("Hoof Dodge: "+extra.format(bonus)+"!");
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mount.internalDeserial(s,new Hoof());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.SARATOGA_SYSTEMS;
	}

}
