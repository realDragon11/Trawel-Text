package rtrawel.unit;

import java.util.HashMap;

import rtrawel.items.Weapon;
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
	}
	
	public static Action getActionByName(String str) {
		return data.get(str);
	}
}
