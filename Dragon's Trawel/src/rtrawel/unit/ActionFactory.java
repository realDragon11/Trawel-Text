package rtrawel.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.items.Weapon;
import rtrawel.items.Weapon.WeaponType;
import trawel.extra;

public class ActionFactory {
	private static HashMap<String,Action> data = new HashMap<String, Action>();
	public static void init() {
		data.put("attack",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " basic attacks " + target.toString());
				Weapon w = caster.getWeapon();
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit(), (w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),w.getDamageTypes()) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 4;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 50;
			}

			@Override
			public double coolDown() {
				return 50;
			}

			@Override
			public String getName() {
				return "attack";
			}

			@Override
			public String getDesc() {
				return "the bread and butter of any physical attacker";
			}});
		
		data.put("cleave",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " cleaves " + target.toString());
				caster.drainMp(3);
				Weapon w = caster.getWeapon();
				List<DamageType> dList = new ArrayList<DamageType>();
				dList.add(DamageType.SHARP);
				for (RUnit u: target.targets) {
					
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit()*.9, (w.getDamage()+w.damageBonuses(u)),false,dList) > -1) {
						w.getOnHit().go(caster,u);//can't crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (!caster.getWeapon().getWeaponType().equals(WeaponType.SWORD)) {
					return false;
				}
				return caster.getMana() >= 2;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.GROUP;
			}

			@Override
			public double warmUp() {
				return 50;
			}

			@Override
			public double coolDown() {
				return 70;
			}

			@Override
			public String getName() {
				return "cleave";
			}

			@Override
			public String getDesc() {
				return "2mp: cleave a group of enemies with your sword";
			}});
		
		data.put("sword thrust",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " thrust their sword at " + target.toString());
				Weapon w = caster.getWeapon();
				List<DamageType> dList = new ArrayList<DamageType>();
				dList.add(DamageType.PIERCE);
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,(int)(caster.getStrength()*.9),w.getBaseHit()*.9, (w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),dList) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 1;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return caster.getWeapon().getWeaponType().equals(WeaponType.SWORD);
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 40;
			}

			@Override
			public double coolDown() {
				return 50;
			}

			@Override
			public String getName() {
				return "sword thrust";
			}

			@Override
			public String getDesc() {
				return "Deals piercing damage.";
			}});
		
		data.put("medicine heal",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " heals " + target.toString() + " with medicine!");
				for (RUnit u: target.targets) {
					u.heal(12,caster.getDexterity());
				}
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.HURT_FRIEND;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 80;
			}

			@Override
			public double coolDown() {
				return 5;
			}

			@Override
			public String getName() {
				return "medicine heal";
			}

			@Override
			public String getDesc() {
				return "a quick patch me up with a herb";
			}});
		
		data.put("basic tincture heal",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " heals " + target.toString() + " with medicine!");
				for (RUnit u: target.targets) {
					u.heal(30,caster.getDexterity());
				}
			}

			@Override
			public double getWeight() {
				return 3;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.HURT_FRIEND;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 70;
			}

			@Override
			public double coolDown() {
				return 10;
			}

			@Override
			public String getName() {
				return "basic tincture heal";
			}

			@Override
			public String getDesc() {
				return "a quick patch me up with a tincture";
			}});
		
		data.put("defend",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " defends!");
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = 1.5;
				b.passive = false;
				b.timeLeft = 100;
				b.type = Buff.BuffType.RES_MULT;
				caster.addBuff(b);
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.SELF_ONLY;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 10;
			}

			@Override
			public double coolDown() {
				return 90;
			}

			@Override
			public String getName() {
				return "block";
			}

			@Override
			public String getDesc() {
				return "defend";
			}});
		
		data.put("sword dance",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " dances with their sword!");
				caster.drainTen(4);
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = 1.4;
				b.passive = false;
				b.timeLeft = 400;
				b.type = Buff.BuffType.AGI_MULT;
				caster.addBuff(b);
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getTension() < 4) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.SELF_ONLY;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 60;
			}

			@Override
			public double coolDown() {
				return 60;
			}

			@Override
			public String getName() {
				return "sword dance";
			}

			@Override
			public String getDesc() {
				return "4 tsn: Increase your agility for a long duration.";
			}});
		
		data.put("hammer stun",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " hammer stuns " + target.toString());
				caster.drainMp(10);
				Weapon w = caster.getWeapon();
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit(), (w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),w.getDamageTypes()) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
						u.knockStun(.6,30);
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 4;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getMana() < 10) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 70;
			}

			@Override
			public double coolDown() {
				return 40;
			}

			@Override
			public String getName() {
				return "hammer stun";
			}

			@Override
			public String getDesc() {
				return "10mp: has a chance to stun the target out of their action";
			}});
		
		data.put("ink spray",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " sprays ink on " + target.toString());
				//caster.drainTen(4);
				for (RUnit u: target.targets) {
					if (RCore.doesHit(caster,u,.7, true)) {
						Buff b = new Buff();
						b.isDebuff = true;
						b.mag = .5;
						b.passive = false;
						b.timeLeft = 400;
						b.type = Buff.BuffType.AGI_MULT;
						b.source = " blind mult";
						u.addBuffUq(b);
						
						b = new Buff();
						b.isDebuff = true;
						b.mag = -20;
						b.passive = false;
						b.timeLeft = 400;
						b.type = Buff.BuffType.AGI_MOD;
						b.source = " blind mod";
						u.addBuffUq(b);
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 4568;
			}

			@Override
			public boolean canCast(RUnit caster) {
				//if (caster.getTension() < 4) {
				//	return false;
				//}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.GROUP;
			}

			@Override
			public double warmUp() {
				return 40;
			}

			@Override
			public double coolDown() {
				return 70;
			}

			@Override
			public String getName() {
				return "ink spray";
			}

			@Override
			public String getDesc() {
				return "sprays ink all over the targets, lowering their agility";
			}});
		
		data.put("enfeebling spores",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " uses their enfeebling spores on " + target.toString());
				caster.drainMp(2);
				for (RUnit u: target.targets) {
					if (RCore.doesHit(caster,u,.6, true)) {
						Buff b = new Buff();
						b.isDebuff = true;
						b.mag = .8;
						b.passive = false;
						b.timeLeft = 300;
						b.type = Buff.BuffType.STR_MULT;
						u.addBuff(b);
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 3;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getMana() < 2) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.GROUP;
			}

			@Override
			public double warmUp() {
				return 80;
			}

			@Override
			public double coolDown() {
				return 10;
			}

			@Override
			public String getName() {
				return "enfeebling spores";
			}

			@Override
			public String getDesc() {
				return "Lowers the strength of a group.";
			}});
		data.put("root of resilience",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " takes the root!");
				for (RUnit u: target.targets) {
						Buff b = new Buff();
						b.isDebuff = false;
						b.mag = 1.05;
						b.passive = false;
						b.timeLeft = 500;
						b.source = "root of resilence mult";
						b.type = Buff.BuffType.RES_MULT;
						u.addBuffUq(b);
						b = new Buff();
						b.isDebuff = false;
						b.mag = 20;
						b.passive = false;
						b.timeLeft = 500;
						b.source = "root of resilence mod";
						b.type = Buff.BuffType.RES_MOD;
						u.addBuffUq(b);
				}
			}

			@Override
			public double getWeight() {
				return 4;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.GROUP;
			}

			@Override
			public double warmUp() {
				return 40;
			}

			@Override
			public double coolDown() {
				return 5;
			}

			@Override
			public String getName() {
				return "root of resilence";
			}

			@Override
			public String getDesc() {
				return "Increases resilence for a long duration.";
			}});
		
		data.put("body slam",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " body slams " + target.toString());
				Weapon w = caster.getWeapon();
				caster.drainTen(8);
				List<DamageType> types = new ArrayList<DamageType>();
				types.add(DamageType.BLUNT);
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,caster.getStrength()*2,1, 10,false,types) > -1) {
					//no on hits
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 4;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getTension() < 8) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 50;
			}

			@Override
			public double coolDown() {
				return 50;
			}

			@Override
			public String getName() {
				return "body slam";
			}

			@Override
			public String getDesc() {
				return "attack with raw brawn";
			}});
		
		data.put("sudden spear",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " suddenly spears " + target.toString());
				Weapon w = caster.getWeapon();
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit(), (w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),w.getDamageTypes()) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 3;
			}

			@Override
			public boolean canCast(RUnit caster) {
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 30;
			}

			@Override
			public double coolDown() {
				return 90;
			}

			@Override
			public String getName() {
				return "sudden spear";
			}

			@Override
			public String getDesc() {
				return "A special attack that attacks quickly but has a large cooldown.";
			}});
		
		data.put("triple thrust",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " uses triple thrust!");
				Weapon w = caster.getWeapon();
				caster.drainMp(4);
				for (int i = 0; i < 3;i++) {
					RUnit u = extra.randList(target.targets);
					if (RCore.doAttack(caster, u,(int) (caster.getStrength()*.9),w.getBaseHit(), (w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),w.getDamageTypes()) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 3;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getMana() < 4) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.ALL;
			}

			@Override
			public double warmUp() {
				return 60;
			}

			@Override
			public double coolDown() {
				return 60;
			}

			@Override
			public String getName() {
				return "triple thrust";
			}

			@Override
			public String getDesc() {
				return "5mp: attack three times at random targets.";
			}});
		
		data.put("mend",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " mends " + target.toString() + " with magic!");
				caster.drainMp(2);
				for (RUnit u: target.targets) {
					u.heal(10,caster.getKnowledge());
				}
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getMana() <2) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.HURT_FRIEND;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 200;
			}

			@Override
			public double coolDown() {
				return 5;
			}

			@Override
			public String getName() {
				return "mend";
			}

			@Override
			public String getDesc() {
				return "2mp: A slow but efficient healing spell";
			}});
		
		data.put("battle heal",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " heals " + target.toString() + " quickly with magic!");
				caster.drainMp(6);
				for (RUnit u: target.targets) {
					u.heal(12,caster.getKnowledge());
				}
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getMana() <6) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.HURT_FRIEND;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 50;
			}

			@Override
			public double coolDown() {
				return 5;
			}

			@Override
			public String getName() {
				return "battle heal";
			}

			@Override
			public String getDesc() {
				return "6mp: A very quick healing spell.";
			}});
		
		data.put("backlash",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " backlashes " + target.toString());
				Weapon w = caster.getWeapon();
				caster.drainMp(1);
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit() * 0.5, (w.getDamage()+w.damageBonuses(u)* w.critMult()),w.isRanged(),w.getDamageTypes()) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 4;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getMana() < 1) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 40;
			}

			@Override
			public double coolDown() {
				return 50;
			}

			@Override
			public String getName() {
				return "backlash";
			}

			@Override
			public String getDesc() {
				return "1mp: mostly misses but is certain to crit if it connects";
			}});
		
		data.put("smite",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " suddenly spears " + target.toString());
				Weapon w = caster.getWeapon();
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.HOLY);
				list.add(DamageType.MAGIC);
				caster.drainMp(3);
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit(), (1.2*w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),list) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 3;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.mp < 3) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 50;
			}

			@Override
			public double coolDown() {
				return 50;
			}

			@Override
			public String getName() {
				return "smite";
			}

			@Override
			public String getDesc() {
				return "A zealous smite that deals extra holy damage..";
			}});
		
		data.put("ranga",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " throws their boomerang at " + target.toString());
				Weapon w = caster.getWeapon();
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,(int) (.9*caster.getStrength()),w.getBaseHit(), (.9*w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),w.getDamageTypes()) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (!caster.getWeapon().getWeaponType().equals(WeaponType.BOOMERANG)) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.GROUP;
			}

			@Override
			public double warmUp() {
				return 50;
			}

			@Override
			public double coolDown() {
				return 80;
			}

			@Override
			public String getName() {
				return "ranga";
			}

			@Override
			public String getDesc() {
				return "Throws the boomerang through a group of foes.";
			}});
		
		data.put("dead bolt",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " fires a bolt perfectly at " + target.toString());
				Weapon w = caster.getWeapon();
				caster.drainMp(10);
				List<DamageType> list = new ArrayList<DamageType>();
				list.add(DamageType.PIERCE);
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,(int) (caster.getStrength()),w.getBaseHit()*1000.0, (1.5*w.getDamage()+w.damageBonuses(u)* ( RCore.doesHit(caster,u,w.critChance(),w.isRanged())? w.critMult() : 1)),w.isRanged(),list) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 2;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (!caster.getWeapon().getWeaponType().equals(WeaponType.CROSSBOW)) {
					return false;
				}
				if (caster.getMana() < 10) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 100;
			}

			@Override
			public double coolDown() {
				return 20;
			}

			@Override
			public String getName() {
				return "dead bolt";
			}

			@Override
			public String getDesc() {
				return "10mp: A narrow needle that nearly never misses.";
			}});
		
		data.put("stunning sling",new Action(){

			@Override
			public void go(RUnit caster, TargetGroup target) {
				extra.println(caster.getName() + " hammer stuns " + target.toString());
				caster.drainMp(3);
				Weapon w = caster.getWeapon();
				for (RUnit u: target.targets) {
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit(), 0,w.isRanged(),w.getDamageTypes()) > -1) {
						w.getOnHit().go(caster,u);//note: not all abilities should be able to crit
						u.knockStun(.9,10);
					}
					
				}
			}

			@Override
			public double getWeight() {
				return 4;
			}

			@Override
			public boolean canCast(RUnit caster) {
				if (caster.getMana() < 3) {
					return false;
				}
				return true;
			}

			@Override
			public TargetType getTargetType() {
				return TargetType.FOE;
			}

			@Override
			public TargetGrouping getTargetGrouping() {
				return TargetGrouping.SINGLE;
			}

			@Override
			public double warmUp() {
				return 70;
			}

			@Override
			public double coolDown() {
				return 40;
			}

			@Override
			public String getName() {
				return "stunning sling";
			}

			@Override
			public String getDesc() {
				return "3mp: has a high chance to stun the target out of their action, but deals no damage";
			}});
		
			data.put("wand drain",new Action(){

				@Override
				public void go(RUnit caster, TargetGroup target) {
					extra.println(caster.getName() + " drains the mana out of " + target.toString());
					Weapon w = caster.getWeapon();
					double chanceTo = ((100.0 + caster.getKnowledge())/100.0);
					for (RUnit u: target.targets) {
						if (Math.random() < chanceTo*u.getDamageMultFor(DamageType.MAGIC)) {
							int manaDrain = Math.min(u.getMana(),w.getDamage()/2);
							u.drainMp(manaDrain);
							caster.restoreMana(manaDrain/2);
						}
						
					}
				}

				@Override
				public double getWeight() {
					return 4;
				}

				@Override
				public boolean canCast(RUnit caster) {
					return true;
				}

				@Override
				public TargetType getTargetType() {
					return TargetType.FOE;
				}

				@Override
				public TargetGrouping getTargetGrouping() {
					return TargetGrouping.SINGLE;
				}

				@Override
				public double warmUp() {
					return 50;
				}

				@Override
				public double coolDown() {
					return 50;
				}

				@Override
				public String getName() {
					return "wand drain";
				}

				@Override
				public String getDesc() {
					return "drains mp";
				}});
			
			data.put("rile up",new Action(){

				@Override
				public void go(RUnit caster, TargetGroup target) {
					extra.println(caster.getName() + " gets all riled up!");
					for (RUnit u: target.targets) {
							Buff b = new Buff();
							b.isDebuff = false;
							b.mag = 1.2;
							b.passive = false;
							b.timeLeft = 70;
							b.source = "rile up mult";
							b.type = Buff.BuffType.STR_MULT;
							u.addBuffUq(b);
							b = new Buff();
							b.isDebuff = false;
							b.mag = 20;
							b.passive = false;
							b.timeLeft = 70;
							b.source = "rile up mod";
							b.type = Buff.BuffType.STR_MOD;
							u.addBuffUq(b);
					}
				}

				@Override
				public double getWeight() {
					return 4;
				}

				@Override
				public boolean canCast(RUnit caster) {
					return true;
				}

				@Override
				public TargetType getTargetType() {
					return TargetType.SELF_ONLY;
				}

				@Override
				public TargetGrouping getTargetGrouping() {
					return TargetGrouping.SINGLE;
				}

				@Override
				public double warmUp() {
					return 30;
				}

				@Override
				public double coolDown() {
					return 5;
				}

				@Override
				public String getName() {
					return "rile up";
				}

				@Override
				public String getDesc() {
					return "Increases your strength for a short duration.";
				}});
		
	}
	
	
	
	public static Action getActionByName(String str) {
		return data.get(str);
	}
}
