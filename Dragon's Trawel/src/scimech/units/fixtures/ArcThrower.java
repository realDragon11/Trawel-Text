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
import trawel.helper.methods.extra;

public class ArcThrower extends Fixture {

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (25*rating());
		int hits = 0;
		for (int i = 0; i < 5;i++) {
			double hit = MechCombat.computeHit(t, AimType.SPECIAL, acc,this);
			if (hit  >=0) {
				t.takeDamage().take(DamageTypes.SHOCK,DamageMods.HOLLOW,2, t);
				hits++;
				t.takeDamage().suffer(DamageEffect.EMP,40*rating(), t);
			}
			
			}
		if (!t.isDummy()) {
			currentMount.takeHeat(5);
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
		return "Arc Thrower";
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
	public int getBaseComplexity() {
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
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new ArcThrower());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
