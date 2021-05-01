package scimech.units.systems;

import scimech.combat.DamageEffect;
import scimech.combat.ResistMap;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.mech.Corpo;
import scimech.mech.Systems;
import scimech.mech.TurnSubscriber;

public class FusionReactor extends Systems {

	public FusionReactor() {
		passive = true;
		powered = true;
	}
	
	@Override
	public int getBaseComplexity() {
		return 10;
	}

	@Override
	public ResistMap resistMap() {
		return null;
	}

	@Override
	public String getTitleAdditions() {
		float r = rating();
		int pow = Math.round(10*r);
		return (pow > 0) ? " " + pow : " offline";
	}

	@Override
	public String getName() {
		return "fusion reactor";
	}

	@Override
	public String getDescription() {
		return "Produces up to 10 energy and 3 heat.";
	}

	@Override
	public int getEnergyDraw() {
		return (int) (-10*rating());
	}

	@Override
	protected void activateInternal(Target t, TurnSubscriber ts) {
		//t.takeDamage().suffer(DamageEffect.BURN,3,t);
		currentMech.takeHeat(3);
	}

	@Override
	public int getWeight() {
		return 5;
	}
	
	public static Savable deserialize(String s) {
		return Systems.internalDeserial(s,new FusionReactor());
	}
	
	@Override
	public Corpo getCorp() {
		return Corpo.GENERIC_REFACTOR;
	}

}
