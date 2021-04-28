package scimech.combat;

import scimech.combat.Target.TargetType;

public enum AimType {
	LASER(1f,0.9f),
	BALLISTIC(1f,0.8f),
	MELEE(1f,1f),
	ARCING(1f,0.4f),
	SPECIAL(1f,1f),
	;
	
	private float mechHit, mountHit;
	AimType(float mechHit,float mountHit) {
		this.mechHit = mechHit;
		this.mountHit = mountHit;
	}
	public float getMechHit() {
		return mechHit;
	}
	public float getMountHit() {
		return mountHit;
	}
	
	public float getMultFor(TargetType tt) {
		switch (tt) {
		case MECH:
			return getMechHit();
		case MOUNT:
			return getMountHit();
		}
		throw new RuntimeException("targettype not found");
	}
}
