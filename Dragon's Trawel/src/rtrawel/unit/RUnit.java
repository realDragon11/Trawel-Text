package rtrawel.unit;

import java.util.ArrayList;

import rtrawel.battle.Battle;
import rtrawel.items.Weapon;
import trawel.extra;

public abstract class RUnit {
	
	protected int hp, mp, ten;
	protected double warmUp, coolDown, upComing;
	protected Action a;
	protected ArrayList<RaceType> raceTypes = new ArrayList<RaceType>();
	protected TargetGroup savedTarget;
	
	public Battle curBattle;

	protected BuffMap buffMap = new BuffMap();
	protected DamMultMap dmm = new DamMultMap();
	
	private FightingStance fStance;
	
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
		return (int) (((this.getBaseDexterity()+this.getEquipDexterity())*buffMap.getTotalBuffMult(Buff.BuffType.DEX_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.DEX_MOD));
	}
	protected abstract int getEquipDexterity();
	protected abstract int getBaseDexterity();
	public int getResilence(){
		return (int) (((this.getBaseResilence()+this.getEquipResilence())*buffMap.getTotalBuffMult(Buff.BuffType.RES_MULT))+buffMap.getTotalBuffMod(Buff.BuffType.RES_MOD));
	}
	protected abstract int getEquipResilence();
	protected abstract int getBaseResilence();
	
	public void takeDamage(int dam) {
		//int d = (int)(dam*getDamageMultFor(t));
		hp = (int) extra.clamp(hp-dam,0,this.getMaxHp());
		//return d;
	}
	public double getDamageMultFor(DamageType t) {
		return dmm.getMult(t)*getEquipDamMultMap().getMult(t);
	}
	
	protected abstract DamMultMap getEquipDamMultMap();
	
	public void advanceTime(double d) {
		buffMap.advanceTime(d);
		while (d > 0) {
			if (warmUp >0) {
				if (warmUp > d) {
					warmUp-=d;
					d = 0;
				}else {
					d-=warmUp;
					warmUp =0;
					coolDown = upComing;
					upComing = 0;
					a.go(this,savedTarget);
				}
			}
			if (coolDown > 0) {
				if (coolDown > d) {
					coolDown-=d;
					d = 0;
				}else {
					d-=coolDown;
					coolDown =0;
					decide();
				}
			}
		}
	}
	
	/**
	 * decide what action to take
	 */
	public abstract void decide();
	public double timeTilNext() {
		return Math.max(warmUp, coolDown);
	}
	
	public void refresh() {
		hp = this.getMaxHp();
		mp = this.getMaxMana();
		ten = 0;
		buffMap.clear();
		fStance = FightingStance.BALANCED;
	}
	
	public boolean getRaceType(RaceType r) {
			return raceTypes.contains(r);
	}
	
	
	public enum RaceType{
		PLAYER, MONSTER, FISH, ETC;
	}


	public abstract Weapon getWeapon();
	
	public void EarnXp(int totalxp) {}
	
	public abstract double shieldBlockChance();
	
	public FightingStance getStance() {
		return fStance;
	}
	
	public enum FightingStance{
		BALANCED, DEFENSIVE, OFFENSIVE;
	}


	public abstract String getName();
	
	public abstract String getBaseName();
}
