package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Fixture;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class APCannon extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (12*rating());
			double hit = MechCombat.computeHit(t, AimType.BALLISTIC, acc,this);
			if (!t.isDummy()) {
				extra.print("The AP Cannon " + ( hit >= 0 ? "hits!" : "misses!") + " ");
			}
			if (hit  >=0) {
				t.takeDamage().take(DamageTypes.KINETIC,DamageMods.AP,30, t);	
			}
			if (!t.isDummy()) {
				currentMount.takeHeat(3);
			}

	}

	@Override
	public void roundStart() {

	}

	@Override
	public int heatCap() {
		return 20;
	}

	@Override
	public String getName() {
		return "AP Cannon";
	}

	@Override
	public int getEnergyDraw() {
		return 4;
	}

	@Override
	public String getDescription() {
		return "Deals a heavy amount of AP kinetic damage.";
	}

	@Override
	public int getComplexity() {
		return 5;
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
		return new APCannon();
	}

}
