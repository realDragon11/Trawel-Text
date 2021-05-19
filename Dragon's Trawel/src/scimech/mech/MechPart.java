package scimech.mech;

public abstract class MechPart {

	protected boolean locked = false;
	
	public static <Z> Z lock(Z toLock){
		((MechPart)toLock).locked = true;
		return toLock;
	}
	
	public boolean canRemove() {
		return !locked;
	}
	
	public abstract Corpo getCorp();
}
