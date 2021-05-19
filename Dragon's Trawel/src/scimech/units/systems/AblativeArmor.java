package scimech.units.systems;

import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class AblativeArmor extends Systems {

	@Override
	public int getBaseComplexity() {
		return 8;
	}

	@Override
	public ResistMap resistMap() {
		ResistMap map = new ResistMap();
		map.isSub = true;
		if (powered) {
			float r = rating();
			map.put(DamageMods.AP, 1f, extra.lerp(1f, 0.7f,r));
			map.put(DamageMods.NORMAL, 1f, extra.lerp(1f, 0.65f,r));
			map.put(DamageMods.HOLLOW, 1f, extra.lerp(1f, 0.6f,r));
			
			map.put(DamageTypes.BLAST, 1f, extra.lerp(1f, 0.6f,r));
			map.put(DamageTypes.KINETIC, 1f, extra.lerp(1f, 0.9f,r));
		}
		return map;
	}

	@Override
	public String getTitleAdditions() {
		return "";
	}

	@Override
	public String getName() {
		return "Ablative Armor";
	}

	@Override
	public String getDescription() {
		return "Provides good protection against AP. Consumes power, and doesn't protect systems. Quite heavy.";
	}

	@Override
	public int getEnergyDraw() {
		return 3;
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		// NONE
	}

	@Override
	public int getWeight() {
		return 22;
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new AblativeArmor());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
