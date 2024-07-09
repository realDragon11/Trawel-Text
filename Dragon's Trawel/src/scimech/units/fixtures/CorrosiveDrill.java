package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Fixture;
import scimech.mech.TurnSubscriber;
import trawel.core.Print;

public class CorrosiveDrill extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (10*rating());
		int hits = 0;
		for (int i = 0; i < 5;i++) {
			double hit = MechCombat.computeHit(t, AimType.MELEE, acc,this);
			if (hit  >=0) {
				hits++;
				t.takeDamage().take(DamageTypes.KINETIC,DamageMods.AP,4, t);
				acc += 4;
			}
			if (hit < -5) {
				break;
			}
		}
		if (hits > 0) {
			t.takeDamage().take(DamageTypes.CAUSTIC,DamageMods.HOLLOW,4, t);
		}
		if (!t.isDummy()) {
			currentMount.takeHeat(2);
			Print.print("The Corrosive Drill attacks! " + hits + " hits! ");
		}
	}

	@Override
	public void roundStart() {

	}

	@Override
	public int heatCap() {
		return 15;
	}

	@Override
	public String getName() {
		return "Corrosive Drill";
	}

	@Override
	public int getEnergyDraw() {
		return 8;
	}

	@Override
	public String getDescription() {
		return "Deals some hollow caustic damage if it hits as repeated AP kinetic. Chain hits.";
	}

	@Override
	public int getBaseComplexity() {
		return 12;
	}

	@Override
	public int getWeight() {
		return 10;
	}

	@Override
	public int getSlots() {
		return 6;
	}
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new CorrosiveDrill());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
