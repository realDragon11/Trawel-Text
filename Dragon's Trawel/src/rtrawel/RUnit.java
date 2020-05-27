package rtrawel;

public interface RUnit {

	public int getStrength();
	public int getKnowledge();
	public int getHp();
	public int getMana();
	public int getTension();
	public int getSpeed();
	public int getAgility();
	public int getDexterity();
	public int getResilence();
	
	public int takeDamage(DamageType t, int dam);
}
