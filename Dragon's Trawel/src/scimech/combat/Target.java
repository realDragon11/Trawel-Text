package scimech.combat;

public interface Target{

	public boolean checkFire();
	
	public int dodgeValue();

	public TargetType targetType();
	
	public TakeDamage takeDamage();
	
	public enum TargetType{
		MECH, MOUNT, DUMMY;
	}
}
