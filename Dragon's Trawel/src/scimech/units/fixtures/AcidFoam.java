package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageEffect;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Fixture;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class AcidFoam extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (10*rating());
		int hits = 0;
		for (int i = 0; i < 20;i++) {
			double hit = MechCombat.computeHit(t, AimType.ARCING, acc,this);
			if (hit  >=0) {
				hits++;
			}
			}
		t.takeDamage().take(DamageTypes.CAUSTIC,DamageMods.HOLLOW,(int) (hits/1.1f), t);
		t.takeDamage().suffer(DamageEffect.SLOW,hits*1.5, t);
		t.takeDamage().suffer(DamageEffect.ACID,hits*5, t);
		if (!t.isDummy()) {
			//currentMount.takeHeat(5);
			extra.print("The Acid Foamer attacks! " + (hits*5) + "% effective! ");
		}
	}

	@Override
	public void roundStart() {

	}

	@Override
	public int heatCap() {
		return 9;
	}

	@Override
	public String getName() {
		return "Acid Foamer";
	}

	@Override
	public int getEnergyDraw() {
		return 5;
	}

	@Override
	public String getDescription() {
		return "Deals some hollow caustic damage as well as slowing the enemy and bonus system damage.";
	}

	@Override
	public int getBaseComplexity() {
		return 10;
	}

	@Override
	public int getWeight() {
		return 4;
	}

	@Override
	public int getSlots() {
		return 4;
	}
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new AcidFoam());
	}

	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
