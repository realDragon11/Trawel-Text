package trawel.battle.attacks;
import java.util.ArrayList;

public class Target implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public double blunt, sharp, pierce;
	public double hit, rarity;
	public int slot;
	public TargetFactory.TargetType type;
	public ArrayList<Attack.Wound> slashWounds = new ArrayList<Attack.Wound>();
	public ArrayList<Attack.Wound> bluntWounds = new ArrayList<Attack.Wound>();
	public ArrayList<Attack.Wound> pierceWounds = new ArrayList<Attack.Wound>();
}
