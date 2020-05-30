package rtrawel.unit;

import java.util.List;

import rtrawel.items.Item;

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
	
	public int getXp();
	public int getGold();
	
	public String getDrop();
	public double getDropChance();
	public String getRareDrop();
	public double getRareDropChance();
	
	public int getKillsTilKnown();
	public int getKillsTilVeryKnown();
	
	public String getWeapon();
	
	//TODO Sprites
	
	public SpriteData getSpriteData();
	public double shieldBlockChance();
	
	public void initer(RMonster r);
	
}
