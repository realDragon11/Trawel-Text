package rtrawel.unit;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Battle;
import rtrawel.items.Weapon;
import trawel.extra;

public abstract class RUnit {
	
	protected int hp, mp, ten;
	protected double warmUp, coolDown, upComing;
	protected Action a;
	protected List<RaceType> raceTypes = new ArrayList<RaceType>();
	protected TargetGroup savedTarget;
	
	public Battle curBattle;

	protected BuffMap buffMap = new BuffMap();
	protected DamMultMap dmm = new DamMultMap();
	
	protected FightingStance fStance = FightingStance.BALANCED;
	
	public boolean alive = true;
	
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
		this.increaseTen(dam/5);
		hp = (int) extra.clamp(hp-dam,0,this.getMaxHp());
		//return d;
	}
	
	public void increaseTen(int v) {
		ten = (int) extra.clamp(ten+v,0,this.getMaxTension());
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
					//decide();
				}
			}
		}
	}
	
	public void decideNOW() {
		if (warmUp == 0 && coolDown == 0) {
			decide();
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
	}
	
	public void cleanUp() {
		buffMap.clear();
		ten = 0;
	}
	
	public boolean getRaceType(RaceType r) {
			return raceTypes.contains(r);
	}
	
	
	public enum RaceType{
		PLAYER, MONSTER, FISH, ETC, BEAST, MACHINE, PLANT, HUMANOID, OOZE, MATERIAL, ANIMATED;
	}


	public abstract Weapon getWeapon();
	
	public void earnXp(int totalxp) {}
	
	public abstract double shieldBlockChance();
	
	public FightingStance getStance() {
		return fStance;
	}
	
	public enum FightingStance{
		BALANCED, DEFENSIVE, OFFENSIVE;
	}


	public abstract String getName();
	
	public abstract String getBaseName();
	public void drainMp(int i) {
		mp = (int)extra.clamp(mp-i,0,this.getMaxMana());
		
	}
	
	public void addRaceType(RaceType r) {
		raceTypes.add(r);
	}
	public void heal(int i, int dexterity) {
		double d = (i * (100.0+dexterity))/(100.0);
		int oldhp = hp;
		hp = (int)extra.clamp(hp+d,0,this.getMaxHp());
		extra.println(hp-oldhp + " healed.");
	}
	public boolean isAlive() {
		return alive;
	}
	public void addBuff(Buff b) {
		buffMap.buffs.add(b);
		
	}
	public void drainTen(int i) {
		ten = (int)extra.clamp(ten-i,0,this.getMaxTension());
	}
	
	public boolean knockStun(double stunChance, int knockAmount) {
		if (Math.random() < stunChance*this.dmm.getMult(DamageType.STUN)) {
			coolDown = knockAmount;
			extra.println(this.getName() + " is stunned!");
			warmUp = 0;
			return true;
		}else {
			return false;
		}
	}
	
	public void cure(Buff.BuffType bt) {
		List<Buff> rList = new ArrayList<Buff>();
		for (int i = 0; i < buffMap.buffs.size(); i++) {
			Buff b = buffMap.buffs.get(i);
			if (b.isDebuff && !b.passive && b.type == bt) {
				rList.add(b);
			}
		}
		buffMap.buffs.removeAll(rList);
	}
	public void addBuffUq(Buff b) {
		buffMap.addUniqueBuff(b);
		
	}
	public void setStance(FightingStance s) {
		fStance = s;
		
	}
	public void restoreMana(int manaDrain) {
		int oldhp = hp;
		hp = (int)extra.clamp(hp+manaDrain,0,this.getMaxHp());
		extra.println(hp-oldhp + " mp restored.");
		
	}
}
