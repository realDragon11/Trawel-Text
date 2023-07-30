package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.List;

public class Target{

	public String name;
	public double blunt, sharp, pierce;
	public double hit, rarity;
	public int slot;
	/**
	 * used for multi target types
	 * if something is in both, set the mappingNumber to the same in both
	 * suggested is
	 * 0 = head
	 * 1 = arms/front legs
	 * 2 = torso
	 * 3 = (back) legs
	 * 4 = feet (fails for quads)
	 */
	public int mappingNumber = -1;
	public TargetFactory.TargetType type;
	public List<Attack.Wound> slashWounds = new ArrayList<Attack.Wound>();
	public List<Attack.Wound> bluntWounds = new ArrayList<Attack.Wound>();
	public List<Attack.Wound> pierceWounds = new ArrayList<Attack.Wound>();
	/**
	 * for mapped parts, variants must be in same order
	 */
	public String[] variants = null;
	//used for 'left' and 'right' arms- they should be mechanically the same
	//that doesn't mean you can't have an 'arm' mechanic, just that either arm should always count
}
