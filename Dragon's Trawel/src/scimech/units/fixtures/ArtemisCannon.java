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

public class ArtemisCannon extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (40*rating());
		double hit = MechCombat.computeHit(t, AimType.BALLISTIC, acc,this);
		if (!t.isDummy()) {
			extra.print("The Artemis Cannon " + ( hit >= 0 ? "hits!" : "misses!") + " ");
		}
		if (hit  >=0) {
			t.takeDamage().take(DamageTypes.KINETIC,DamageMods.NORMAL,10+((int)hit/2), t);	
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
		return 8;
	}

	@Override
	public String getName() {
		return "Artemis Cannon";
	}

	@Override
	public int getEnergyDraw() {
		return 6;
	}

	@Override
	public String getDescription() {
		return "A normal kinetic weapon that deals more damage to targets that fail to dodge properly.";
	}

	@Override
	public int getComplexity() {
		return 8;
	}

	@Override
	public int getWeight() {
		return 3;
	}

	@Override
	public int getSlots() {
		return 3;
	}
	
	public static Savable deserialize(String s) {
		return new ArtemisCannon();
	}

}
