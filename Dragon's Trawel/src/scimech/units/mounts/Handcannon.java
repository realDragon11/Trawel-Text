package scimech.units.mounts;

import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.mech.Mount;
import trawel.extra;

public class Handcannon extends Mount {

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = false;
		map.subMaps.add(currentMech.resistMap());
		return map;
	}

	@Override
	public String getName() {
		return "Handcannon Mount";
	}

	@Override
	public int baseWeight() {
		return 4;
	}

	@Override
	public int baseComplexity() {
		return 3;
	}

	@Override
	public int baseSlots() {
		return 4;
	}

	@Override
	public float dodgeMult() {
		return 1f;
	}
	
	@Override
	public void bonusEffect(Target t, int damage) {
		if (damage > 2) {
			int before = t.getHP();
			t.takeDamage().take(DamageTypes.KINETIC, DamageMods.AP,3,t);
			extra.println("Handcannon: " + (before-t.getHP()) + " bonus damage!");
		}
	}

}
