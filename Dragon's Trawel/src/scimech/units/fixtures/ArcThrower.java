package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageEffect;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.combat.Target.TargetType;
import scimech.mech.Fixture;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class ArcThrower extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (25*rating());
		int hits = 0;
		for (int i = 0; i < 5;i++) {
			double hit = MechCombat.computeHit(t, AimType.MELEE, acc);
			if (hit  >=0) {
				t.takeDamage().take(DamageTypes.SHOCK,DamageMods.HOLLOW,2, t);
				hits++;
				t.takeDamage().suffer(DamageEffect.EMP,40*rating(), t);
			}
			currentMount.takeHeat(1);
			}
		if (!t.isDummy()) {
			extra.print("The Arc Thrower attacks! " + hits + " hits! ");
		}

	}

	@Override
	public void roundStart() {
	}

	@Override
	public int heatCap() {
		return 10;
	}

	@Override
	public String getName() {
		return "Hot Laser";
	}

	@Override
	public int getEnergyDraw() {
		return 12;
	}

	@Override
	public String getDescription() {
		return "Deals hollow shock damage and lots of EMP.";
	}

	@Override
	public int getComplexity() {
		return 12;
	}

	@Override
	public int getWeight() {
		return 4;
	}

	@Override
	public int getSlots() {
		return 4;
	}

}
