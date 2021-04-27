package scimech.combat;

public interface Target{

	public boolean checkFire();
	
	public int dodgeValue();

	public TargetType targetType();
	
	public TakeDamage takeDamage();
	
	public enum TargetType{
		MECH, MOUNT;
	}
	
	public boolean isDummy();
	
	public Dummy constructDummy();
	
	public void takeHPDamage(int i);
	
	public String targetName();
	
	public ResistMap resistMap();

	public int getHP();
}
