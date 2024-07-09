package scimech.units.mounts;

import scimech.combat.DamageEffect;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Mount;
import trawel.core.Print;

public class Pulsar extends Mount {

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = false;
		map.subMaps.add(currentMech.resistMap());
		
		map.put(DamageTypes.SHOCK,0.8f,0.9f);
		map.put(DamageMods.HOLLOW,1.2f,1.1f);
		return map;
	}

	@Override
	public String getName() {
		return "Pulsar Mount";
	}

	@Override
	public int baseWeight() {
		return 3;
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
		return 1f;
	}
	
	@Override
	public void bonusEffect(Target t, int damage) {
			t.takeDamage().suffer(DamageEffect.EMP,20, t);
			Print.println("Pulsar EMP!");
	}
	
	public static Savable deserialize(String s) throws Exception {
		return Mount.internalDeserial(s,new Pulsar());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
