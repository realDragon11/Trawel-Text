package trawel.battle.attacks;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import derg.SRInOrder;
import derg.StringResult;
import trawel.extra;
import trawel.battle.attacks.IAttack.AttackType;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;

public class WeaponAttackFactory {
	private static Map<Weapon.WeaponType,Stance> stanceMap = new HashMap<Weapon.WeaponType,Stance>();
	
	//FIXME: update stancemap to new weapon naming system, and also every attack
	
	public WeaponAttackFactory() {
		Attack tempAttack;
		Stance martialStance = new Stance();//FIXME attacks need reworked
		tempAttack = new Attack("slash","X` slashes at Y` with their Z`!",1.5d,100.0d,40,5,0);
		//new Attack("slash",1.5,100.0,40,5,0,"X` slashes at Y` with their Z`!",1,"sharp");
		martialStance.addAttack(tempAttack,3f);
		tempAttack = new Attack("stab","X` stabs at Y` with their Z`",1.0d,90.0d,10,0,25);
		//tempAttack = new Attack("stab",1.0,90.0,1,2,20,"X` stabs at Y` with their Z`!",1,"pierce");
		martialStance.addAttack(tempAttack,3f);
		tempAttack = new Attack("thrust","X` thrusts at Y` with their Z`!",0.5d,60.0d,5,0,30);
		//tempAttack = new Attack("thrust",.4,60.0,1,4,20,"X` thrusts at Y` with their Z`!",2,"pierce");
		martialStance.addAttack(tempAttack,1f);
		tempAttack = new Attack("pommel","X` hits Y` with the pommel of their Z`!",1d,120.0d,0,20,0);//probably needs a bonus of some sort
		//tempAttack = new Attack("pommel",1,110.0,0,12,0,"X` hits Y` with the pommel of their Z`!",0,"blunt");
		martialStance.addAttack(tempAttack,.2f);
		tempAttack = new Attack("slap","X` slaps Y` with the side of their Z`!",1.1d,100.0d,0,15,0);
		//tempAttack = new Attack("slap",1.1,100.0,0,10,0,"X` slaps Y` with the side of their Z`!",1,"blunt");		
		martialStance.addAttack(tempAttack,1f);
		martialStance.finish();
		stanceMap.put(WeaponType.LONGSWORD, martialStance);
		
		martialStance = new Stance();
		tempAttack = new Attack("slash",1.3,110.0,37,8,0,"X` slashes at Y` with their Z`!",1,"sharp");
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		tempAttack = new Attack("stab",1.0,110.0,2,4,10,"X` stabs at Y` with their Z`!",1,"pierce");
		martialStance.addAttack(tempAttack);	
		martialStance.addAttack(new Attack("pommel",1,110.0,0,12,0,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.2,120.0,0,15,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.5,180.0,50,20,0,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"sharp"));
		martialStance.finish();
		stanceMap.put(WeaponType.BROADSWORD, martialStance);
		
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bash",1.5,150.0,4,25,4,"X` bashes Y` with their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("smash",1,100.0,4,20,4,"X` smashes Y` with their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.5,180.0,10,50,10,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"blunt"));
		martialStance.finish();
		stanceMap.put(WeaponType.MACE, martialStance);
		martialStance = new Stance();
		tempAttack = new Attack("skewer",1.1,120.0,1,2,45,"X` skewers Y` with their Z`!",1,"pierce");
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(new Attack("thrust",.4,60.0,1,2,30,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pole",.6,120.0,0,10,0,"X` hits Y` with the pole of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("smack",1,100.0,1,8,0,"X` smacks Y` with the side of their Z`!",0,"blunt"));
		martialStance.finish();
		stanceMap.put(WeaponType.SPEAR, martialStance);
		martialStance = new Stance();
		tempAttack = new Attack("hack",.9,90.0,20,18,0,"X` hacks at Y` with their Z`!",1,"sharp");
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(new Attack("slap",.8,100.0,0,30,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("heft",1,120.0,0,10,0,"X` hits Y` with the heft of their Z`!",0,"blunt"));
		martialStance.finish();
		stanceMap.put(WeaponType.AXE, martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slash",1.3,90.0,25,1,0,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("slice",.9,60.0,18,0,0,"X` slices up Y` with their Z`!",0,"sharp"));
		martialStance.addAttack(new Attack("stab",1.2,100.0,5,0,25,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.6,50.0,10,0,20,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pommel",1,110.0,0,10,0,"X` hits Y` with the pommel of their Z`!",1,"blunt"));
		martialStance.finish();
		stanceMap.put(WeaponType.RAPIER, martialStance);
		martialStance = new Stance();
		tempAttack = new Attack("slash",1,70.0,20,2,0,"X` slashes at Y` with their Z`!",0,"sharp");
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		tempAttack = new Attack("stab",1.1,70.0,1,1,15,"X` stabs at Y` with their Z`!",0,"pierce");
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		tempAttack = new Attack("thrust",.6,60.0,1,2,20,"X` thrusts at Y` with their Z`!",1,"pierce");
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		//martialStance.addAttack(new Attack("slap",.8,85.0,0,10,0,"X` slaps Y` with the side of their Z`!",0,"blunt"));
		martialStance.finish();
		stanceMap.put(WeaponType.DAGGER, martialStance);
		martialStance = new Stance();
		tempAttack = new Attack("slash",1.4,200.0,60,20,0,"X` slashes at Y` with their Z`!",1,"sharp");
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(tempAttack);
		martialStance.addAttack(new Attack("stab",0.5,300.0,30,10,3,"X` stabs at Y` with their Z`!",1,"pierce"));	
		//martialStance.addAttack(new Attack("pommel",.2,300.0,0,17,0,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.3,200.0,0,30,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.4,400.0,100,50,0,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"sharp"));
		martialStance.finish();
		stanceMap.put("claymore", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",0.8,150.0,1,5,35,"X` skewers Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.6,100.0,1,5,20,"X` thrusts at Y` with their Z`!",1,"pierce"));
		martialStance.addAttack(new Attack("smack",1,100.0,0,15,0,"X` smacks Y` with the side of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("charge",1.4,300.0,1,20,80,"X` charges forward with their Z`!",2,"pierce"));
		martialStance.finish();
		stanceMap.put("lance", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("thrust",.4,60.0,2,30,3,"X` thrusts at Y` with their Z`!",2,"blunt"));
		martialStance.addAttack(new Attack("slap",1.2,100.0,0,20,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("pole",.6,120.0,0,10,0,"X` hits Y` with the pole of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("smack",1,100.0,2,25,0,"X` smacks Y` with the side of their Z`!",1,"blunt"));
		martialStance.finish();
		stanceMap.put("shovel", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bite",1,100.0,5,0,35,"X` bites at Y` with their teeth!",2,"pierce"));
		martialStance.addAttack(new Attack("tear",1.4,140.0,20,0,20,"X` tears into Y`'s flesh with their teeth!",1,"pierce"));
		martialStance.addAttack(new Attack("rip",.6,120.0,30,0,30,"X` rips up Y`'s flesh with their teeth!",0,"pierce"));
		martialStance.finish();
		stanceMap.put("generic teeth", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("kick",1,100.0,1,35,0,"X` kicks at Y`!",2,"blunt"));
		martialStance.addAttack(new Attack("kick",1,100.0,1,35,0,"X` kicks at Y`!",2,"blunt"));
		martialStance.addAttack(new Attack("kick",1,100.0,1,35,0,"X` kicks at Y`!",2,"blunt"));
		martialStance.finish();
		stanceMap.put("standing reaver", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bite",1,100.0,5,0,35,"X` bites at Y` with their teeth!",2,"pierce"));
		martialStance.addAttack(new Attack("tear",1.4,140.0,20,0,20,"X` tears into Y`'s flesh with their claws!",1,"pierce"));
		martialStance.addAttack(new Attack("rip",.6,120.0,30,0,30,"X` rips up Y`'s flesh with their claws!",0,"pierce"));
		martialStance.finish();
		stanceMap.put("generic teeth and claws", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("rake",1.1,110.0,20,10,0,"X` rakes Y`!",1,"sharp"));
		martialStance.addAttack(new Attack("bash",1,120.0,0,35,35,"X` bashes Y` with their branches!",1,"blunt"));
		martialStance.addAttack(new Attack("rake",1.1,110.0,20,10,0,"X` rakes Y`!",1,"sharp"));
		martialStance.addAttack(new Attack("bash",1,120.0,0,35,35,"X` bashes Y` with their branches!",1,"blunt"));
		martialStance.finish();
		stanceMap.put("branches", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bash",1.5,150.0,0,35,0,"X` bashes Y` with their fists!",1,"blunt"));
		martialStance.addAttack(new Attack("smash",1,100.0,0,30,0,"X` smashes Y` with their fists!",1,"blunt"));
		martialStance.addAttack(new Attack("power punch",.5,180.0,0,80,0,"X` lifts their fists over their head, and then brings them down on Y`!",2,"blunt"));
		martialStance.finish();
		stanceMap.put("generic fists", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",0.5,150.0,1,5,35,"X` skewers Y` with their horn!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,100.0,1,5,20,"X` thrusts at Y` with their horn!",1,"pierce"));
		martialStance.addAttack(new Attack("smack",1,100.0,0,15,0,"X` smacks Y` with the side of their horn!",0,"blunt"));
		martialStance.addAttack(new Attack("charge",1.4,300.0,1,20,80,"X` charges forward with their horn!",2,"pierce"));
		martialStance.finish();
		stanceMap.put("unicorn horn", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("grip",1,100.0,5,0,35,"X` grips Y` with their talons!",2,"pierce"));
		martialStance.addAttack(new Attack("tear",1.4,140.0,20,0,20,"X` tears into Y`'s flesh with their talons!",1,"pierce"));
		martialStance.addAttack(new Attack("rip",.6,120.0,30,0,30,"X` rips up Y`'s flesh with their talons!",0,"pierce"));
		martialStance.finish();
		stanceMap.put("generic talons", martialStance);
		
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",1,100.0,1,2,45,"X` skewers Y` with their rusty fishing spear!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,50.0,1,2,30,"X` thrusts at Y` with their rusty fishing spear!",2,"pierce"));
		martialStance.addAttack(new Attack("pole",.6,110.0,0,8,0,"X` hits Y` with the pole of their rusty fishing spear!",1,"blunt"));
		martialStance.finish();
		stanceMap.put("fishing spear", martialStance);
		
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slam",.5,300.0,0,40,0,"X` hits Y` with their rusty anchor!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.3,200.0,0,30,0,"X` slaps Y` with the side of their rusty anchor!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.9,400.0,100,50,0,"X` lifts their rusty anchor over their head, and then brings it down on Y`!",2,"sharp"));
		martialStance.finish();
		stanceMap.put("anchor", martialStance);
	}
	
	public static Stance getStance(String str) {
		return stanceMap.get(str);
	}
	
	public static void weaponMetrics() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("wmetrics.csv");
		int hold = Weapon.battleTests;
		Weapon.battleTests = 1000;
		List<Weapon> weaponList = new ArrayList<Weapon>();
		int mats = 0;
		int weaps = 0;
		writer.write(",");
		for (String str: Weapon.weaponTypes) {
			writer.write(str+",");
		}
		writer.write("\n");
		for (Material m: MaterialFactory.matList) {
			if (!m.weapon) {
				continue;
			}
			mats++;
			writer.write(m.name+",");
			for (WeaponType str: Weapon.WeaponType.values()) {
				weaps++;
				weaponList.add(new Weapon(1,m,str));
				writer.write(weaponList.get(weaponList.size()-1).score()+",");
			}
			writer.write("\n");
		}
		writer.flush();
		weaponList.sort(new Comparator<Weapon>(){

			@Override
			public int compare(Weapon o1, Weapon o2) {
				double comp = (o1.score()-o2.score());
				if (comp == 0) {
					return 0;
				}
				return (comp > 0 ? -1 : 1);
			}
		});
		HashMap<Integer,Double> matMap = new HashMap<Integer, Double>();
		HashMap<String,Double> weapMap = new HashMap<String, Double>();
		for (int i = 0; i< weaponList.size();i++) {
			Weapon weapon = weaponList.get(i);
			weapon.display(0);
			Double get = matMap.get(weapon.getMat().curNum);
			if (get == null) {
				matMap.put(weapon.getMat().curNum,weapon.score());
			}else {
				matMap.put(weapon.getMat().curNum,weapon.score()+get);
			}
			get = weapMap.get(weapon.getBaseName());
			if (get == null) {
				weapMap.put(weapon.getBaseName(),weapon.score());
			}else {
				weapMap.put(weapon.getBaseName(),weapon.score()+get);
			}
		}
		
		List<WeaponMetric> metrics = new ArrayList<WeaponMetric>();
		for (int i: matMap.keySet()) {
			String str = MaterialFactory.getMat(i).name;
			metrics.add(new WeaponAttackFactory().new WeaponMetric(str,(matMap.get(i)/weaps),MaterialFactory.getMat(str).rarity));
		}
		for (String str: weapMap.keySet()) {
			metrics.add(new WeaponAttackFactory().new WeaponMetric(str,(weapMap.get(str)/mats),Weapon.getRarity(str)));
		}
		metrics.sort(new Comparator<WeaponMetric>(){

			@Override
			public int compare(WeaponMetric o1, WeaponMetric o2) {
				double comp = ((o1.raw/*o1.rarity*/)-(o2.raw/*o2.rarity*/));
				if (comp == 0) {
					return 0;
				}
				return (comp > 0 ? -1 : 1);
			}
		});
		for (WeaponMetric wm: metrics) {
			extra.println(wm.toString());
		}
		Weapon.battleTests = hold;
		writer.close();
	}
	
	public class WeaponMetric{
		public String name;
		public double raw;
		public double rarity;
		
		public WeaponMetric(String nam, double ra, double rarit) {
			name = nam;
			raw = ra;
			rarity = rarit;
		}
		
		@Override
		public String toString() {
			java.text.DecimalFormat formata = new java.text.DecimalFormat("0.0000");
			return name +": "+ formata.format(raw) +")(" +formata.format(rarity*raw); 
		}
		
	}
	
	public enum DamageTier{
		NONE(0), WEAK(10), LOW(20), AVERAGE(30), HIGH(40), ASTOUNDING(50);
		private int damage;
		DamageTier(int _damage){
			damage = _damage;
		}
		
		public static float totalDamage(DamageTier start, DamageTier end, float lerp) {
			return extra.lerp(start.damage,end.damage,lerp);
		}
		
		public static int[] distribute(float total, float sharpW, float bluntW, float pierceW) {
			int[] arr = new int[3];
			arr[0] = Math.round(sharpW*total);
			arr[0] = Math.round(sharpW*total);
			arr[0] = Math.round(sharpW*total);
			return arr;
		}
	}
	
	private static final AttackMaker make(String name) {
		return new AttackMaker(name);
	}
	
	public static class AttackMaker{
		private DamageTier start = DamageTier.AVERAGE, end = DamageTier.AVERAGE;
		private float slant = .5f, hitmult = 1f;
		private float sharpW = 1f, bluntW = 1f, pierceW = 1f;
		private float warmup = 50f, cooldown = 50f;
		private String name, desc = "", fluff = "X` attacks Y` with their Z`!";
		
		AttackMaker(String _name){
			name = name;
		}
		
		public AttackMaker setFluff(String fluff) {
			this.fluff = fluff;
			return this;
		}
		
		public AttackMaker setDesc(String description) {
			desc = description;
			return this;
		}
		
		public AttackMaker setDamage(DamageTier low, DamageTier high, float damageSlant) {
			start = low;
			end = high;
			slant = damageSlant;
			return this;
		}
		
		public AttackMaker setMix(float sharp, float blunt, float pierce) {
			sharpW = sharp;
			bluntW = blunt;
			pierceW = pierce;
			return this;
		}
		
		/**
		 * mix = 1 -> only warmup
		 * <br>
		 * mix = 0 -> only cooldown
		 * @param time
		 * @param mix
		 * @return
		 */
		public AttackMaker setTime(int time, float mix) {
			warmup = time * mix;
			cooldown = time * (1f-mix);
			return this;
		}
		
		public Attack finish() {
			int[] arr = DamageTier.distribute(DamageTier.totalDamage(start, end, slant),sharpW,bluntW,pierceW);
			return new Attack(name, desc, new SRInOrder(fluff), hitmult, AttackType.REAL_WEAPON, arr,
					warmup, cooldown);
		}
		
	}
	
	/*
	public static Attack attackMaker(String name, String desc, String fluff, float totalDamage, int sharp, int blunt, int pierce,
			double hitMult, float warmup, float cooldown){
		int[] arr = DamageTier.distribute(totalDamage,sharp,blunt,pierce);
		return new Attack(name, desc, new SRInOrder(fluff), hitMult, AttackType.REAL_WEAPON, arr,
				warmup, cooldown);
	}*/
}
