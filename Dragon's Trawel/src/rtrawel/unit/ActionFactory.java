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
				caster.drainMp(2);
				Weapon w = caster.getWeapon();
				List<DamageType> dList = new ArrayList<DamageType>();
				dList.add(DamageType.SHARP);
				for (RUnit u: target.targets) {
					
					if (RCore.doAttack(caster, u,caster.getStrength(),w.getBaseHit(), (w.getDamage()+w.damageBonuses(u)),false,dList) > -1) {
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
	}
	
	
	
	public static Action getActionByName(String str) {
		return data.get(str);
	}
}
