package scimech.mech;

import trawel.extra;

public abstract class MechPart {

	protected boolean locked = false;
	protected float discount = 1f;
	
	public static <Z> Z lock(Z toLock){
		((MechPart)toLock).locked = true;
		return toLock;
	}
	public static <Z> Z discount(Z toDis,float discountMult){
		((MechPart)toDis).discount = Float.parseFloat(extra.format(discountMult));
		return toDis;
	}
	
	public boolean canRemove() {
		return !locked;
	}
	
	public abstract Corpo getCorp();
}
