package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.mech.Fixture;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class ZeusRifle extends Fixture{

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (20*rating());
		int hits = 0;
		for (int i = 0; i < 10;i++) {
			double hit = MechCombat.computeHit(t, AimType.BALLISTIC, acc);
			if (hit  >=0) {
				t.takeDamage().take(DamageTypes.SHOCK,DamageMods.NORMAL,5, t);
				hits++;
			}
			
			acc=Math.max(acc-extra.randRange(2,4),1);
		}
		if (!t.isDummy()) {
			currentMount.takeHeat(8);
			extra.print("The Zeus Rifle attacks! " + hits + " hits! ");
		}
		
	}

	@Override
	public void roundStart() {
		
	}

	@Override
	public int heatCap() {
		return 16;
	}

	@Override
	public String getName() {
		return "Zeus Rifle";
	}

	@Override
	public int getEnergyDraw() {
		return 10;
	}

	@Override
	public String getDescription() {
		return "A ballistic shock weapon that loses it's accuracy over it's 10 shots.";
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
		return 8;
	}

}
