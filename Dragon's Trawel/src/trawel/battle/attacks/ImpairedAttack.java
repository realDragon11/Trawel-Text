package trawel.battle.attacks;

import trawel.Effect;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Attack.Wound;
import trawel.battle.attacks.IAttack.AttackType;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;
import trawel.personal.Person;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponQual;

public class ImpairedAttack implements IAttack{
	private Attack attack;
	
	private TargetReturn target;
	private Style style;
	private Wound wound;
	private Weapon weapon;
	private Person attacker, defender;//only used for mass battles
	
	private double warmup, cooldown;
	
	private int[] vals;
	
	private float hitroll;
	
	private double potencyMult;
	private int level;
	
	public ImpairedAttack(Attack _attack,
			TargetReturn _target, Style _style,
			Weapon _weapon, Person _attacker, Person _defender) {
		attack = _attack;
		
		target = _target;
		style = _style;
		weapon = _weapon;
		attacker = _attacker;
		defender = _defender;
		
		potencyMult = 1;
		//compution
		Target t = target.tar;
		vals = new int[attack.valueSize()];
		
		double speedMult = _style.speed;
		double speedModUp = extra.randRange(0,10);
		double speedModDown = extra.randRange(-5,5);
		float hitMult = (float) (_style.hit*t.hit);
		
		switch (getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
			assert !(_weapon == null && _attacker == null);//only one can be null
			double sMult = attack.getSharp()*t.sharp;
			double bMult = attack.getBlunt()*t.blunt;
			double pMult = attack.getPierce()*t.pierce;
			int w_lvl;
			boolean alwaysWound = (_weapon != null && _weapon.isKeen());
			boolean isAttackTest = defender == null;
			
			if (_weapon != null) {
				w_lvl = isAttackTest ? 10 :_weapon.getLevel();
				sMult *= _weapon.getMat().sharpMult;
				bMult *= _weapon.getMat().bluntMult;
				pMult *= _weapon.getMat().pierceMult;
			}else {
				if (_attacker != null) {
					w_lvl = _attacker.getLevel();
				}else {
					throw new RuntimeException("invalid weapon resolved level for impaired attack");
				}

			}
			double damMult = w_lvl*_style.damage;

			vals[0] = damageRoll(DamageType.SHARP,sMult*damMult);
			vals[1] = damageRoll(DamageType.BLUNT,bMult*damMult);
			vals[2] = damageRoll(DamageType.PIERCE,pMult*damMult);
			if (attack.getStance().elementBypass || !(_weapon != null && _weapon.isEnchantedHit())) {
				vals[3] = damageRoll(DamageType.IGNITE,attack.getIgnite()*damMult);
				vals[4] = damageRoll(DamageType.FROST,attack.getFrost()*damMult);
				vals[5] = damageRoll(DamageType.ELEC,attack.getElec()*damMult);
			}else {//enchant hit weapon with no bypass
				int totalDam = attack.getTotalDam();
				Enchant enc = _weapon.getEnchant();
				vals[3] = damageRoll(DamageType.IGNITE,enc.getFireMod()*totalDam*damMult);
				vals[4] = damageRoll(DamageType.FROST,enc.getFreezeMod()*totalDam*damMult);
				vals[5] = damageRoll(DamageType.ELEC,enc.getShockMod()*totalDam*damMult);
			}
			

			if (!alwaysWound && extra.randRange(1,10) == 1) {
				this.wound = Attack.Wound.GRAZE;
			}else {
				double counter = extra.getRand().nextDouble() * (vals[0] + vals[1] + vals[2] + vals[3] + vals[4] + vals[5]);
				counter-=vals[0];
				if (counter<=0) {
					this.wound = extra.randList(target.tar.slashWounds);
				}else {
					counter-=vals[2];
					if (counter<=0) {
						this.wound = extra.randList(target.tar.pierceWounds);
					}else {
						counter-=vals[3];
						if (counter<=0) {
							this.wound = extra.randList(TargetFactory.fireWounds);
						}else {
							counter-=vals[4];
							if (counter<=0) {
								this.wound = extra.randList(TargetFactory.freezeWounds);
							}else {
								counter-=vals[5];
								if (counter<=0) {
									this.wound = extra.randList(TargetFactory.shockWounds);
								}else {
									//blunt is last to be a default for rounding errors
									this.wound = extra.randList(target.tar.bluntWounds);
								}
							}
						}
						
					}
				}
			}
			
			level = w_lvl;
			break;
		case SKILL:
			break;
		}
		if (_attacker != null) {
			speedMult *= _attacker.getSpeed();
			if (_attacker.hasEffect(Effect.SLICE)) {
				hitMult*=1.1;//WET
				speedMult*=.9;
			}
			if (_attacker.hasEffect(Effect.DICE)) {
				speedMult*=.9;//WET
			}
		}
		cooldown = (attack.getCooldown()*speedMult)+speedModDown;
		warmup = (attack.getWarmup()*speedMult)+speedModUp;
		
		warmup = extra.clamp(warmup,10,400);
		cooldown = extra.clamp(cooldown,10,400);

		//
		hitroll = extra.lerp(hitMult*.8f,hitMult*1.2f, extra.hrandomFloat());
		if (_weapon != null) {
			hitroll +=_weapon.hasQual(Weapon.WeaponQual.ACCURATE) ? .1 : 0;
		}
		
	}
	
	public enum DamageType{
		SHARP, BLUNT, PIERCE, IGNITE, FROST, ELEC, DECAY
	}
	
	private int damageRoll(DamageType dt, double max) {
		switch (dt) {
		case SHARP: case BLUNT: case PIERCE:
			if (attacker != null) {
				max*= attacker.getBag().getDam();//DOLATER make physical damage mult seperate;
				max*= attacker.fetchAttributes().multStrength();//DOLATER see if working
			}else {
				if (weapon != null) {
					if (weapon.getEnchant() != null) {
						max*=weapon.getEnchant().getDamMod();//DOLATER make physical damage mult seperate
					}	
				}
			}
			return extra.randRange((int)(max*.7),(int)max);
		case IGNITE: case FROST: case ELEC:
			if (attacker != null) {
				max*= attacker.getBag().getDam();//DOLATER make physical damage mult seperate;
			}else {
				if (weapon != null) {
					if (weapon.getEnchant() != null) {
						max*=weapon.getEnchant().getDamMod();//DOLATER make physical damage mult seperate
					}	
				}
			}
			return extra.randRange((int)(max*.7),(int)max);
		default: 
			return 0;
		}
	}
	
	/**
	 * can be null
	 */
	public Person getDefender() {
		return defender;
	}
	public void setDefender(Person defender) {
		this.defender = defender;
	}

	public Person getAttacker() {
		return attacker;
	}

	public Wound getWound() {
		return wound;
	}

	@Override
	public double getWarmup() {
		return warmup;
	}

	@Override
	public double getCooldown() {
		return cooldown;
	}

	/**
	 * can be null
	 */
	public Weapon getWeapon() {
		return weapon;
	}
	public Style getStyle() {
		return style;
	}
	public TargetReturn getTarget() {
		return target;
	}
	
	/**
	 * discourged use
	 */
	public Attack getAttack() {//should probably do direct functions
		return attack;
	}
	@Override
	public boolean physicalDamage() {
		return attack.physicalDamage();
	}
	@Override
	public String getName() {
		return style.name + attack.getName() + " " + target.getName();
	}
	@Override
	public String getDesc() {
		return attack.getDesc();
	}
	@Override
	public String fluff(AttackReturn attret) {
		// TODO Auto-generated method stub
		return attack.fluff(attret) + (mainGame.advancedCombatDisplay ? attret.getNotes() : "");
	}
	@Override
	public int getSharp() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getSharpFromWeap(vals));
		}
		return 0;
	}
	@Override
	public int getBlunt() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getBluntFromWeap(vals));
		}
		return 0;
	}
	@Override
	public int getPierce() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getPierceFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public int getIgnite() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getIgniteFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public int getFrost() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getFrostFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public int getElec() {
		switch (attack.getType()) {
		case FAKE_WEAPON:
		case REAL_WEAPON:
		case SKILL:
			return (int) (getPotencyMult()*IAttack.getElecFromWeap(vals));
		}
		return 0;
	}
	
	@Override
	public double getHitMult() {
		return hitroll;
	}
	
	@Override
	public AttackType getType() {
		return attack.getType();
	}

	@Override
	public int valueSize() {
		return vals.length;
	}
	
	public int getSlot() {
		return target.tar.slot;
	}
	
	public int getTargetSpot() {
		return target.spot;
	}

	public double getTime() {
		return warmup+cooldown;
	}

	@Override
	public int getTotalDam() {
		// TODO add other damage types when they get added
		return getSharp()+getBlunt()+getPierce();
	}
	
	public float multiplyHit(float mult) {
		return hitroll*=mult;
	}

	@Override
	public double getPotencyMult() {
		return potencyMult;
	}

	@Override
	public void multPotencyMult(double multMult) {
		potencyMult*= multMult;
	}

	public void display(int style) {
		int[] in;
		switch (style) {
		case 0://not impaired style
		case 1://classic
			in = new int[6];
			in[0] = 8+12;
			in[1] = 7;
			in[2] = 9;
			in[3] = 9;
			in[4] = 10;
			in[5] = 6+4;
			extra.specialPrint(in,getName() ,extra.format(getHitMult()) , extra.format(getWarmup()+getCooldown())  ,""+ (getSharp())  ,""+(getBlunt())  ,  ""+(getPierce()));
			break;
		case 2://two line 1
			extra.println(getName());
				in = new int[8];
				in[0] = 9;//hitchance, should be %9.99 >= x > 0.00 | 9 gives a 2 spot gap to the times
				in[1] = 4;//instants, should be _999 >= x > 0 | 4 because we're okay with pressing up against the next one
				in[2] = 1;//warmup/cooldown seperator
				in[3] = 5;//instants, should be _999 >= x > 0 | 5 because we want to give the next a one-two spot gap
				//
				in[4] = 2;//details seperator
				//sbp 6 should be fine for 3 digits, 7 for 4
				//these can vary based on the attack
				in[5] = 6;
				in[6] = 6;
				in[7] = 6;
				extra.specialPrint(in,"  "+extra.CHAR_HITCHANCE + extra.format(getHitMult()),
						extra.CHAR_INSTANTS +extra.formatInt(getWarmup()),"-",extra.CHAR_INSTANTS+extra.formatInt(getCooldown()),
						"=",
						"S "+(getSharp()),"B "+(getBlunt()),"P "+(getPierce())//unsure if spacing messes up narrator
						);
			}
			if (wound != null) {
				extra.println("  "+this.wound.name + " - " + String.format(this.wound.desc,(Object[])Combat.woundNums(this,attacker,defender,null)));
			}
		}

	public int getLevel() {
		return level;
	}
	
	public boolean hasWeaponQual(WeaponQual qual) {
		return weapon != null && weapon.hasQual(qual);
	}
	
}
