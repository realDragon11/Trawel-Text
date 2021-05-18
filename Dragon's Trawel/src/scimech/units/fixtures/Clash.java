package scimech.units.fixtures;

import scimech.combat.AimType;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Fixture;
import scimech.mech.Mech;
import scimech.mech.TurnSubscriber;
import trawel.extra;

public class Clash extends Fixture{

	@Override
	public void activate(Target t, TurnSubscriber ts) {
		float speedBonus = Math.min(this.currentMount.currentMech.getSpeed(),30)-Mech.SPEED_DIE/2;
		int acc = (int) (speedBonus*rating());
		int hits = 0;
		for (int i = 0; i < 4;i++) {
			double hit = MechCombat.computeHit(t, AimType.BALLISTIC, acc,this);
			if (hit  >=0) {
				t.takeDamage().take(DamageTypes.KINETIC,DamageMods.NORMAL,3+Math.round(speedBonus/8f), t);
				hits++;
			}
		}
		if (!t.isDummy()) {
			extra.print("The Clash Shotgun attacks at +"+speedBonus+"! " + hits + " hits! ");
			currentMount.takeHeat(2);
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
		return "Clash Shotgun";
	}

	@Override
	public int getEnergyDraw() {
		return 4;
	}

	@Override
	public String getDescription() {
		return "A shotgun with improved power when the user is moving at high speeds.";
	}

	@Override
	public int getBaseComplexity() {
		return 6;
	}

	@Override
	public int getWeight() {
		return 3;
	}

	@Override
	public int getSlots() {
		return 4;
	}
	
	public static Savable deserialize(String s) {
		return Fixture.internalDeserial(s,new Clash());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.SARATOGA_SYSTEMS;
	}

}
