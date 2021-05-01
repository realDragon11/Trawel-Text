package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageEffect;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.TakeDamage;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Fixture;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class Krakatoa extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (10*rating());
		double hit = MechCombat.computeHit(t, AimType.ARCING, acc,this);
		if (!t.isDummy()) {
			extra.print("Krakatoa " + ( hit >= 0 ? "hits!" : "misses!") + " ");
		}
		if (hit >=0) {
			t.takeDamage().take(DamageTypes.BURN,DamageMods.AP,10, t);	
			t.takeDamage().take(DamageTypes.BLAST,DamageMods.NORMAL,10, t);
			t.takeDamage().suffer(DamageEffect.BURN,4, t);
		}else {
			t.takeDamage().take(DamageTypes.BLAST,DamageMods.NORMAL,4, t);	
		}
		if (!t.isDummy()) {
			currentMount.takeHeat(6);
		}

	}

	@Override
	public void roundStart() {

	}

	@Override
	public int heatCap() {
		return 30;
	}

	@Override
	public String getName() {
		return "Krakatoa";
	}

	@Override
	public int getEnergyDraw() {
		return 8;
	}

	@Override
	public String getDescription() {
		return "Arcing weapon that deals AP burn and normal blast, as well as a small amount of blast on a miss. Also inflict heat.";
	}

	@Override
	public int getComplexity() {
		return 14;
	}

	@Override
	public int getWeight() {
		return 10;
	}

	@Override
	public int getSlots() {
		return 4;
	}
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new Krakatoa());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
