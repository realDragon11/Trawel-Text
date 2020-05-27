package rtrawel;

import rtrawel.Buff.BuffType;

public abstract class RUnit {
	
	protected int hp, mp, ten;

	private BuffMap buffMap = new BuffMap();
	
	public int getStrength() {
		return (int) (((this.getBaseStrength()+this.getEquipStrength())*buffMap.getTotalBuffMult(Buff.BuffType.STR_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.STR_MOD));
	}
	protected abstract int getEquipStrength();
	protected abstract int getBaseStrength();
	public int getKnowledge(){
		return (int) (((this.getBaseKnowledge()+this.getEquipKnowledge())*buffMap.getTotalBuffMult(Buff.BuffType.KNO_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.KNO_MOD));
	}
	protected abstract int getEquipKnowledge();
	protected abstract int getBaseKnowledge();
	public abstract int getMaxHp();
	public abstract int getMaxMana();
	public abstract int getMaxTension();
	
	public int getHp() {
		return hp;
	}
	public int getMana() {
		return mp;
	}
	public int getTension() {
		return ten;
	}
	
	public int getSpeed(){
		return (int) (((this.getBaseSpeed()+this.getEquipSpeed())*buffMap.getTotalBuffMult(Buff.BuffType.SPD_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.SPD_MOD));
	}
	protected abstract int getEquipSpeed();
	protected abstract int getBaseSpeed();
	public int getAgility(){
		return (int) (((this.getBaseAgility()+this.getEquipAgility())*buffMap.getTotalBuffMult(Buff.BuffType.AGI_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.AGI_MOD));
	}
	protected abstract int getEquipAgility();
	protected abstract int getBaseAgility();
	public int getDexterity(){
		return (int) (((this.getBaseDexterity()+this.getEquipDexterity())*buffMap.getTotalBuffMult(Buff.BuffType.AGI_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.AGI_MOD));
	}
	protected abstract int getEquipDexterity();
	protected abstract int getBaseDexterity();
	public int getResilence(){
		return (int) (((this.getBaseResilence()+this.getEquipResilence())*buffMap.getTotalBuffMult(Buff.BuffType.AGI_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.AGI_MOD));
	}
	protected abstract int getEquipResilence();
	protected abstract int getBaseResilence();
	
	public int takeDamage(DamageType t, int dam) {
		int d = (int)(dam*getDamageMultFor(t));
		hp-=d;
		return d;
	}
	public double getDamageMultFor(DamageType t) {
		return 1;//TODO
	}
}
