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
import trawel.helper.methods.extra;

public class LightAutocannon extends Fixture{

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (20*rating());
		int hits = 0;
		for (int i = 0; i < 3;i++) {
			double hit = MechCombat.computeHit(t, AimType.BALLISTIC, acc,this);
			if (hit  >=0) {
				t.takeDamage().take(DamageTypes.KINETIC,DamageMods.NORMAL,4, t);
				hits++;
			}
			if (!t.isDummy()) {
				if (extra.chanceIn(2,3)) {
					currentMount.takeHeat(1);
				}
			}
			acc-=extra.randRange(1, 3);
		}
		if (!t.isDummy()) {
			extra.print("The Light Autocannon attacks! " + hits + " hits! ");
		}
		
	}

	@Override
	public void roundStart() {
		
	}

	@Override
	public int heatCap() {
		return 12;
	}

	@Override
	public String getName() {
		return "Light Autocannon";
	}

	@Override
	public int getEnergyDraw() {
		return 2;
	}

	@Override
	public String getDescription() {
		return "An accurate full-auto weapon that shoots 3 times.";
	}

	@Override
	public int getBaseComplexity() {
		return 4;
	}

	@Override
	public int getWeight() {
		return 2;
	}

	@Override
	public int getSlots() {
		return 2;
	}
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new LightAutocannon());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
