package scimech.mech;

import scimech.combat.ResistMap;
import trawel.extra;

public abstract class Systems implements TurnSubscriber{

	public abstract int getComplexity();
	
	protected boolean powered;
	
	public int activated = 0;
	
	protected int damage = 0;//max 100
	public Mech currentMech;
	
	public abstract int heatCap();
	
	public void takeDamage(int toTake) {
		damage = extra.clamp(damage+toTake, 0, 100);
	}
	
	public float rating() {
		float total = ((100f-damage)/100f)*currentMech.rating();
		return total;
	}
	
	public abstract ResistMap resistMap();
	
	@Override
	public void roundStart() {
		activated = 0;
	}

}
