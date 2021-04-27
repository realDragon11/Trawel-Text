package scimech.combat;

public class Dummy implements Target {
	
	public int dodgeValue;
	public TargetType targetType;
	
	public TakeDamage takeDamage;

	@Override
	public boolean checkFire() {
		return false;
	}

	@Override
	public int dodgeValue() {
		return dodgeValue;
	}

	@Override
	public TargetType targetType() {
		return targetType;
	}

	@Override
	public TakeDamage takeDamage() {
		return takeDamage;
	}

}
