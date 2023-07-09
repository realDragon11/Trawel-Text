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
	
	public String getName();
	public String getDesc();
	
	
	public enum TargetType{
		FOE, FRIEND, HURT_FRIEND, SELF_ONLY, OOC;
	}
	
	public enum TargetGrouping{
		SINGLE, GROUP, ALL;
	}
	
	//should have a map for common data types, like crit and mana cost, then have a function for detecting those elsewhere that can be called on
}
