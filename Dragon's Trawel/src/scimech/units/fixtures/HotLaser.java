package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.combat.Target.TargetType;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Fixture;
import scimech.mech.Mech;
import scimech.mech.Mount;
import scimech.mech.TurnSubscriber;
import trawel.helper.methods.extra;

public class HotLaser extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (30*rating());
		double hit = MechCombat.computeHit(t, AimType.LASER, acc,this);
		if (!t.isDummy()) {
			extra.print("The Hot Laser " + ( hit >= 0 ? "hits!" : "misses!") + " ");
		}
		if (hit  >=0) {
			t.takeDamage().take(DamageTypes.BURN,DamageMods.HOLLOW,(int) (13*rating()), t);
			if (!t.isDummy()) {
				if (t.targetType() == TargetType.MECH) {
					Mech m = (Mech)t;
					m.takeHeat(3);
				}else {
					if (t.targetType() == TargetType.MOUNT) {
						Mount m = (Mount)t;
						m.takeHeat(1);
					}
				}
			}
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
		return 10;
	}

	@Override
	public String getName() {
		return "Hot Laser";
	}

	@Override
	public int getEnergyDraw() {
		return 6;
	}

	@Override
	public String getDescription() {
		return "Deals hollow burn damage and heat.";
	}

	@Override
	public int getBaseComplexity() {
		return 10;
	}

	@Override
	public int getWeight() {
		return 3;
	}

	@Override
	public int getSlots() {
		return 2;//TODO: changed from 3 because that's  what I was using it as
	}
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new HotLaser());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
