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
	 * 1 = head
	 * 2 = arms/front legs
	 * 3 = torso
	 * 4 = (back) legs
	 * 5 = feet (fails for quads)
	 * 
	 * cannot do negatives
	 */
	public int mappingNumber = 0;
	/**
	 * items that have an attach number can't have a mapping number
	 * attach number makes a variant set spawn for all variants on the attached parts in mapping number
	 * the attached part must have the same number of variants on all mapping numbers
	 * it will attempt to learn the number of variants by finding the first target with that mapping number
	 * attached also generate's it's own condition, but it is linked with the base? //TODO: maybe not do this, idk
	 * 
	 * most mapping system behavior can be made with attach behavior, but mapping is simpler
	 * 
	 * name format is: [base part variant name]'s [variant name or normal name]
	 * if there are no base variants it just displays as if it were freestanding
	 * 
	 * cannot do negatives, zero is also reserved
	 */
	public int attachNumber = 0;
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
