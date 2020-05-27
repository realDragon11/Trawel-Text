package rtrawel.unit;

import java.util.List;

public interface MonsterData {
	public int getStrength();
	public int getKnowledge();
	public int getMaxHp();
	public int getMaxMana();
	public int getMaxTension();
	public int getSpeed();
	public int getAgility();
	public int getDexterity();
	public int getResilence();
	public List<Action> getActions();
	public String getName();
	public String getDesc();
	public DamMultMap getDamMultMap();
	
	//TODO Sprites
}
