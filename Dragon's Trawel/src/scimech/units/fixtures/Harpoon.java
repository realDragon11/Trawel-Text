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
import trawel.core.Print;

public class Harpoon extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (15*rating());
			double hit = MechCombat.computeHit(t, AimType.BALLISTIC, acc,this);
			if (!t.isDummy()) {
				Print.print("The Breakaway Harpoon " + ( hit >= 0 ? "hits!" : "misses!") + " ");
			}
			if (hit >=0) {
				t.takeDamage().take(DamageTypes.KINETIC,DamageMods.AP,15, t);
				t.takeDamage().suffer(DamageEffect.SLOW,10+hit, t);
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
		return 18;
	}

	@Override
	public String getName() {
		return "Breakaway Harpoon";
	}

	@Override
	public int getEnergyDraw() {
		return 6;
	}

	@Override
	public String getDescription() {
		return "Deals a moderate amount of AP kinetic damage as well as slowing the target.";
	}

	@Override
	public int getBaseComplexity() {
		return 10;
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
		return Fixture.internalDeserial(s,new Harpoon());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
