package scimech.mech;

public class MechPart {

	protected boolean locked = false;
	
	public static <Z> Z lock(Z toLock){
		((MechPart)toLock).locked = true;
		return toLock;
	}
	
	public boolean canRemove() {
		return !locked;
	}
}
