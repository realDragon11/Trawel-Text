package rtrawel.unit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import rtrawel.EventFlag;
import rtrawel.SaveData;
import rtrawel.battle.FightFactory;
import rtrawel.battle.Party;
import rtrawel.items.ArmorFactory;
import rtrawel.items.ConsumableFactory;
import rtrawel.items.Item;
import rtrawel.items.MaterialFactory;
import rtrawel.items.WeaponFactory;
import rtrawel.jobs.JobFactory;
import rtrawel.jobs.PathFactory;
import rtrawel.unit.RUnit.FightingStance;
import rtrawel.unit.RUnit.RaceType;
import rtrawel.village.RecipeFactory;
import trawel.Plane;
import trawel.Player;
import trawel.extra;

public class RCore {

	
	public static double calcDamageMod(double attackStat,double defendStat) {
		return (100+attackStat)/(extra.zeroOut(80+defendStat)+20);
	}
	
	public static boolean doesHit(RUnit attacker, RUnit defender, double baseHitMult,boolean ranged) {
		if (Math.random() < defender.shieldBlockChance()) {
			extra.println(defender.getName() + " blocks!");
			return false;
		}
		double evadeMult = 1.35;
		switch (defender.getStance()) {
		case OFFENSIVE: evadeMult -= .1;break;
		case DEFENSIVE: evadeMult += .1;break;
		}
		double hitMult = 1.1 + (attacker.getStance().equals(FightingStance.DEFENSIVE) && !ranged ? -.1 : 0);
		double agi = attacker.getAgility();
		if (attacker.getRaceType(RaceType.KNO_AS_TOHIT)) {
			agi = attacker.getKnowledge();
		}
		double hitChance = (hitMult*(agi+100))/(evadeMult*(extra.zeroOut(defender.getAgility()+80)+20));
		if (hitChance < .9) {
		return hitChance > Math.random();}else {
			return lerp(.9,1,hitChance/3) > Math.random();
		}
	}
	
	public static int dealDamage(RUnit defender, int attackStat, double damage, List<DamageType> types ) {
		double mult = calcDamageMod(attackStat, defender.getResilence());
		for (DamageType t: types) {
			mult*= defender.getDamageMultFor(t);
		}
		int ret = (int) (damage*mult);
		defender.takeDamage(ret);
		return ret;
	}
	
	/**
	 * Proper usage: detect bonus damage in advance to load into 'damage', then apply on-hit bonuses if you detect >-1 damage dealt.
	 * 
	 * @param attacker
	 * @param defender
	 * @param attackStat
	 * @param baseHitMult
	 * @param d
	 * @param list
	 * @return
	 */
	public static int doAttack(RUnit attacker, RUnit defender, int attackStat, double baseHitMult, double d, boolean ranged, List<DamageType> list ) {
		if (doesHit(attacker,defender,baseHitMult, ranged)) {
			int dam = dealDamage(defender,attackStat,d * (!ranged && attacker.getStance().equals(RUnit.FightingStance.OFFENSIVE) && !ranged ? 1.1 : 1),list);
			extra.println("It deals " + dam +" to "+ defender.getName() +"!");
			attacker.increaseTen(dam/10);
			return dam;
		}else {
			extra.println("It misses "  + defender.getName()+ "!");
			return -1;
		}
	}
	
	
	public static void init() {
		EventFlag.init();
		ActionFactory.init();
		WeaponFactory.init();
		ConsumableFactory.init();
		MaterialFactory.init();
		ArmorFactory.init();
		MonsterFactory.init();
		WeaponFactory.init();
		PathFactory.init();
		JobFactory.init();
		FightFactory.init();
		RecipeFactory.init();
	}
	
	public static int levelLynchPin(int level, int level1, int level5, int level15, int level25, int level40, int level60, int level80, int level99) {
		if (level >=80) {
			return intLerp(level80,level99,level,80,90);
		}
		if (level >=60) {
			return intLerp(level60,level80,level,60,80);
		}
		if (level >=40) {
			return intLerp(level40,level60,level,40,60);
		}
		if (level >=25) {
			return intLerp(level25,level40,level,25,40);
		}
		if (level >=15) {
			return intLerp(level15,level25,level,15,25);
		}
		if (level >=5) {
			return intLerp(level5,level15,level,5,15);
		}
		return intLerp(level1,level5,level,1,5);
	}
	
	private static int intLerp(int one, int two, int level, int minLevel, int maxLevel) {
		double d = ((double)(level-minLevel))/(maxLevel-minLevel);
		return (int)((1 - d) * one + d * two);
	}
	
	public static int lerp(double one, double two, double amount) {
		return (int)((1 - amount) * one + amount * two);
	}

	public static Item getItemByName(String str) {
		try {
			return WeaponFactory.getWeaponByName(str);
		}catch(Exception e) {}
		try {
			return ArmorFactory.getArmorByName(str);
		}catch(Exception e) {}
		try {
			return ConsumableFactory.getConsumableByName(str);
		}catch(Exception e) {}
		try {
			return MaterialFactory.getMaterialByName(str);
		}catch(Exception e) {}
		
		throw new RuntimeException("Item not found.");
	}
	public static Item getItemByName(String str, boolean b) {
		try {
			return WeaponFactory.getWeaponByName(str);
		}catch(Exception e) {}
		try {
			return ArmorFactory.getArmorByName(str);
		}catch(Exception e) {}
		try {
			return ConsumableFactory.getConsumableByName(str);
		}catch(Exception e) {}
		try {
			return MaterialFactory.getMaterialByName(str);
		}catch(Exception e) {}
		return null;
	}

	public static void save() {
		extra.println("saving...");
		   FileOutputStream fos;
			try {
				fos = new FileOutputStream("rtrawel.save");
				 ObjectOutputStream oos = new ObjectOutputStream(fos);
				 oos.writeObject(new SaveData());
			     oos.close();
			     fos.close();
			     extra.println("saved!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static String load() {
		extra.println("loading...");
		FileInputStream fos;
		try {
			fos = new FileInputStream("rtrawel.save");
			 ObjectInputStream oos = new ObjectInputStream(fos);
			 SaveData sd = (SaveData) oos.readObject();
			 Party.party = sd.getParty();
			 oos.close();
			 fos.close();
			 extra.println("loaded!");
			 return sd.curVillage;
		} catch (Exception e) {
			e.printStackTrace();
			extra.println("Invalid load. Either no save file was found or it was outdated.");
			return null;
		}
	}
	
	/*
	 * public static void save() {
		   FileOutputStream fos;
		try {
			fos = new FileOutputStream("trawel.save");//Player.player.getPerson().getName()
			 ObjectOutputStream oos = new ObjectOutputStream(fos);
			 oos.writeObject(plane);
		     oos.close();
		     fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void load() {
		FileInputStream fos;
		try {
			fos = new FileInputStream("trawel.save");
			 ObjectInputStream oos = new ObjectInputStream(fos);
			 plane = (Plane) oos.readObject();
			 Player.player = plane.getPlayer();
			 //World worlda = world;
			 Player.bag = Player.player.getPerson().getBag();
			 Player.passTime = 0;
			 Player.world = Player.player.world2;
			 oos.close();
			 fos.close();
		} catch (ClassNotFoundException | IOException e) {
			extra.println("Invalid load. Either no save file was found or it was outdated.");
		}
		
	}
	 */
	
	
	
}
