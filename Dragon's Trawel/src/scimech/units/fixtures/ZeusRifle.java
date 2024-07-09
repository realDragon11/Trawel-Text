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
import trawel.core.Rand;

public class ZeusRifle extends Fixture{

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int acc = (int) (18*rating());
		int hits = 0;
		for (int i = 0; i < 10;i++) {
			double hit = MechCombat.computeHit(t, AimType.BALLISTIC, acc,this);
			if (hit  >=0) {
				t.takeDamage().take(DamageTypes.SHOCK,DamageMods.NORMAL,5, t);
				t.takeDamage().suffer(DamageEffect.EMP,3*rating(), t);
				hits++;
			}
			
			acc=Math.max(acc-Rand.randRange(2,4),1);
		}
		if (!t.isDummy()) {
			currentMount.takeHeat(8);
			Print.print("The Zeus Rifle attacks! " + hits + " hits! ");
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
	public int getBaseComplexity() {
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
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new ZeusRifle());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
