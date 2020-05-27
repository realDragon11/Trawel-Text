package rtrawel;

import rtrawel.Buff.BuffType;

public abstract class RUnit {

	private BuffMap buffMap = new BuffMap();
	
	public int getStrength() {
		return ((this.getBaseStrength()+this.getEquipStrength())*buffMap.getTotalBuffMult(Buff.BuffType.STR_MULT))+buffMap.getTotalBuffAdd(Buff.BuffType.STR_MOD);
	}
	protected abstract int getEquipStrength();
	protected abstract int getBaseStrength();
	public int getKnowledge();
	public int getMaxHp();
	public int getMaxMana();
	public int getMaxTension();
	
	public int getHp();
	public int getMana();
	public int getTension();
	
	public int getSpeed();
	public int getAgility();
	public int getDexterity();
	public int getResilence();
	
	public int takeDamage(DamageType t, int dam);
}
