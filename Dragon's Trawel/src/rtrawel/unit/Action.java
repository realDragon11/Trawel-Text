package rtrawel.unit;

public interface Action {

	
	public void go(RUnit caster, TargetGroup target);
	
	public double getWeight();
	
	public double warmUp();
	public double coolDown();
	
	/**
	 * do I have enough mana/tension?
	 * @param caster
	 * @return
	 */
	public boolean canCast(RUnit caster);
	
	public TargetType getTargetType();
	
	public TargetGrouping getTargetGrouping();
	
	
	public enum TargetType{
		FOE, FRIEND, HURT_FRIEND;
	}
	
	public enum TargetGrouping{
		SINGLE, GROUP, ALL;
	}
}
