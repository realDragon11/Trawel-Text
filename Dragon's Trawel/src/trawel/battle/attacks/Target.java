package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.List;

public class Target{

	public String name;
	public double blunt, sharp, pierce;
	public double hit, rarity;
	public int slot;
	public TargetFactory.TargetType type;
	public List<Attack.Wound> slashWounds = new ArrayList<Attack.Wound>();
	public List<Attack.Wound> bluntWounds = new ArrayList<Attack.Wound>();
	public List<Attack.Wound> pierceWounds = new ArrayList<Attack.Wound>();
	public String[] variants;
	//used for 'left' and 'right' arms- they should be mechanically the same
	//that doesn't mean you can't have an 'arm' mechanic, just that either arm should always count
}
