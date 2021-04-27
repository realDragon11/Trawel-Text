package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.mech.Fixture;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class Mortar extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (8*rating());
		double hit = MechCombat.computeHit(t, AimType.ARCING, acc);
		if (!t.isDummy()) {
			extra.print("The Mortar " + ( hit >= 0 ? "hits!" : "misses!") + " ");
		}
		if (hit  >=0) {
			t.takeDamage().take(DamageTypes.KINETIC,DamageMods.NORMAL,20, t);	
			t.takeDamage().take(DamageTypes.BLAST,DamageMods.HOLLOW,30, t);	
		}else {
			t.takeDamage().take(DamageTypes.BLAST,DamageMods.HOLLOW,10, t);	
		}
		if (!t.isDummy()) {
			currentMount.takeHeat(4);
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
		return "Mortar";
	}

	@Override
	public int getEnergyDraw() {
		return 8;
	}

	@Override
	public String getDescription() {
		return "Deals hollow blast damage and normal kinetic, as well as some blast on a miss.";
	}

	@Override
	public int getComplexity() {
		return 10;
	}

	@Override
	public int getWeight() {
		return 12;
	}

	@Override
	public int getSlots() {
		return 6;
	}

}
