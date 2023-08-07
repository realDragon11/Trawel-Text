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
import trawel.WorldGen;
import trawel.extra;
import trawel.battle.Combat;
import trawel.battle.Combat.ATK_ResultCode;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.IAttack.AttackType;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;

public class WeaponAttackFactory {
	private static Map<Weapon.WeaponType,Stance> stanceMap = new HashMap<Weapon.WeaponType,Stance>();
	
	//FIXME: update stancemap to new weapon naming system, and also every attack
	
	public WeaponAttackFactory() {
		Stance sta;
		
		//TEMPLATE SECTION
		sta = new Stance(null);
		sta.addAttack(
				make("")
				.setFluff("X` does to Y` with their Z`!")
				.setRarity(1f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.AVERAGE,.5f)
				.setMix(1,1,1)
				.setTime(TimeTier.NORMAL,.5f)
				);
		addStance(null,sta);
		//END TEMPLATE
		
		sta = new Stance(WeaponType.LONGSWORD);
		sta.addAttack(
				make("slash")
				.setFluff("X` slashes at Y` with their Z`!")
				.setRarity(4f)
				.setAcc(1.5f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.4f)
				.setMix(12,1,3)
				.setTime(TimeTier.NORMAL,.5f)
				);
		sta.addAttack(
				make("stab")
				.setFluff("X` stabs at Y` with their Z`")
				.setRarity(2.5f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.8f)
				.setMix(3,0,10)
				.setTime(TimeTier.SLOW,.3f)//longer cooldown
				);
		sta.addAttack(
				make("thrust")
				.setFluff("X` quickly thrusts at Y` with their Z`!")
				.setRarity(1f)
				.setAcc(.6f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.5f)
				.setMix(3,0,20)
				.setTime(TimeTier.NORMAL,.2f)//little warmup
				);
		sta.addAttack(
				make("pommel")
				.setFluff("X` hits Y` with the pommel of their Z`!")
				.setRarity(.8f)
				.setAcc(1f)
				.setDamage(DamageTier.LOW,DamageTier.WEAK,.2f)
				.setMix(0,1,0)
				.setWarmupOfTotal(TimeTier.FASTEST,TimeTier.NORMAL)//lower cooldown but still normal time
				);
		sta.addAttack(
				make("slap")
				.setFluff("X` slaps Y` with the side of their Z`!")
				.setRarity(1.5f)
				.setAcc(1.3f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.3f)
				.setMix(0,1,0)
				.setTime(TimeTier.NORMAL,.5f)
				);
		addStance(WeaponType.LONGSWORD,sta);
		
		
		sta = new Stance(WeaponType.BROADSWORD);
		sta.addAttack(
				make("slash")
				.setFluff("X` slashes at Y` with their Z`!")
				.setRarity(3f)
				.setAcc(1.3f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.5f)
				.setMix(12,3,3)
				.setTime(TimeTier.NORMAL,.5f)
				);
		sta.addAttack(
				make("thrust")
				.setFluff("X` thrusts towards Y` with their Z`!")
				.setRarity(1.5f)
				.setAcc(.9f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.1f)
				.setMix(5,1,10)
				.setTime(TimeTier.SLOW,.6f)
				);
		sta.addAttack(
				make("slap")
				.setFluff("X` slaps Y` with their Z`!")
				.setRarity(2f)
				.setAcc(1.6f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(0,1,0)
				.setTimeTiers(TimeTier.NORMAL,TimeTier.SLOW,.3f)
				.setTimeMix(.6f)
				);
		sta.addAttack(
				make("power")
				.setFluff("X` prepares their Z`, then brings it down on Y` with a furious swing!")
				.setRarity(1f)
				.setAcc(1.2f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.1f)
				.setMix(15,6,3)
				.setTime(TimeTier.SLOWEST,.8f)//mostly warmup
				);
		addStance(WeaponType.BROADSWORD,sta);
		
		sta = new Stance(WeaponType.MACE);
		sta.addAttack(
				make("bash")
				.setFluff("X` bashes Y` with their Z`!")
				.setRarity(2f)
				.setAcc(1.5f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.5f)
				.setMix(3,20,6)
				.setTimeTiers(TimeTier.NORMAL,TimeTier.SLOW,.5f)
				.setTimeMix(.6f)
				);
		sta.addAttack(
				make("smash")
				.setFluff("X` smashes Y` with their Z`!")
				.setRarity(1.5f)
				.setAcc(.8f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.3f)
				.setMix(3,20,6)
				.setTimeTiers(TimeTier.NORMAL,TimeTier.FAST,.2f)
				.setTimeMix(.4f)
				);
		sta.addAttack(
				make("power")
				.setFluff("X` prepares their Z`, then brings it down on Y` with a furious swing!")
				.setRarity(1f)
				.setAcc(1.2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.8f)
				.setMix(3,20,6)
				.setTimeTiers(TimeTier.SLOWER,TimeTier.SLOWEST,.4f)
				.setTimeMix(.7f)
				);
		addStance(WeaponType.MACE,sta);
		
		sta = new Stance(WeaponType.SPEAR);
		sta.addAttack(
				make("stab")
				.setFluff("X` stabs at Y` with their Z`!")
				.setRarity(2f)
				.setAcc(2.5f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.1f)
				.setMix(3,0,30)
				.setTime(TimeTier.SLOWEST,.5f)
				);
		sta.addAttack(
				make("skewer")
				.setFluff("X` charges to skewer Y`!")
				.setRarity(5f)
				.setAcc(1.1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.AVERAGE,.5f)
				.setMix(5,0,30)
				.setTime(TimeTier.SLOW,.7f)
				);
		sta.addAttack(
				make("thrust")
				.setFluff("X` thrusts at Y` with their Z`!")
				.setRarity(3f)
				.setAcc(.5f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.1f)
				.setMix(3,0,35)
				.setTime(TimeTier.FAST,.4f)
				);
		sta.addAttack(
				make("smack")
				.setFluff("X` smacks Y` with the side of their Z`!")
				.setRarity(1f)
				.setAcc(.9f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.5f)
				.setMix(0,1,0)
				.setTime(TimeTier.NORMAL,.5f)
				);
		addStance(WeaponType.SPEAR,sta);
		
		sta = new Stance(WeaponType.AXE);
		sta.addAttack(
				make("hack")
				.setFluff("X` hacks at Y` with their Z`!")
				.setRarity(6f)
				.setAcc(.95f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.4f)
				.setMix(10,2,0)
				.setTime(TimeTier.NORMAL,.6f)
				);
		sta.addAttack(
				make("chop")
				.setFluff("X` chops Y` with their Z`!")
				.setRarity(4f)
				.setAcc(1.1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.5f)
				.setMix(1,0,0)
				.setTime(TimeTier.SLOW,.6f)
				);
		sta.addAttack(
				make("power")
				.setFluff("X` prepares their Z`, then brings it down on Y` with a furious swing!")
				.setRarity(2f)
				.setAcc(1.2f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.5f)
				.setMix(20,5,0)
				.setWarmupOfTotal(TimeTier.SLOWER, TimeTier.SLOWEST)
				);
		sta.addAttack(
				make("heft")
				.setFluff("X` bats the heft of their Z` at Y`!")
				.setRarity(.8f)
				.setAcc(1.3f)
				.setDamage(DamageTier.AVERAGE,DamageTier.AVERAGE,.5f)
				.setMix(0,1,0)
				.setTime(TimeTier.SLOW,.4f)
				);
		addStance(WeaponType.AXE,sta);
		
		sta = new Stance(WeaponType.RAPIER);
		sta.addAttack(
				make("slice")
				.setFluff("X` slices up Y` with their Z`!")
				.setRarity(3f)
				.setAcc(.8f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.1f)
				.setMix(10,0,4)
				.setTime(TimeTier.FASTER,.6f)
				);
		sta.addAttack(
				make("stab")
				.setFluff("X` stabs at Y` with their Z`!")
				.setRarity(3f)
				.setAcc(1.1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.AVERAGE,.5f)
				.setMix(2,0,10)
				.setTime(TimeTier.NORMAL,.5f)
				);
		sta.addAttack(
				make("thrust")
				.setFluff("X` thrusts towards Y` with their Z`!")
				.setRarity(1f)
				.setAcc(.4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.1f)
				.setMix(1,0,10)
				.setTime(TimeTier.FAST,.5f)
				);
		sta.addAttack(
				make("slash")
				.setFluff("X` slashes up Y` with their Z`!")
				.setRarity(2.5f)
				.setAcc(1.2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(10,0,2)
				.setWarmupOfTotal(TimeTier.FAST, TimeTier.SLOW)
				);
		sta.addAttack(
				make("pommel")
				.setFluff("X` brings the pommel of their Z` down on Y`!")
				.setRarity(2f)
				.setAcc(1.2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.7f)
				.setMix(0,1,0)
				.setWarmupOfTotal(TimeTier.NORMAL, TimeTier.SLOWER)
				);
		addStance(WeaponType.RAPIER,sta);
		
		sta = new Stance(WeaponType.DAGGER);
		sta.addAttack(
				make("slice")
				.setFluff("X` slices up Y` with their Z`!")
				.setRarity(2f)
				.setAcc(1f)//more accurate than the normal slice
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.4f)
				.setMix(5,0,1)
				.setWarmupOfTotal(TimeTier.FASTEST, TimeTier.FAST)
				);
		sta.addAttack(
				make("stab")
				.setFluff("X` stabs at Y` with their Z`!")
				.setRarity(2f)
				.setAcc(2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.2f)
				.setMix(1,0,5)
				.setTime(TimeTier.NORMAL,.4f)
				);
		sta.addAttack(
				make("slash")//different from a normal slash group
				.setFluff("X` slashes with their Z`, intent at cutting up Y`!")
				.setRarity(1f)
				.setAcc(1.2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.1f)
				.setMix(12,0,2)
				.setWarmupOfTotal(TimeTier.NORMAL,TimeTier.SLOW)
				);
		sta.addAttack(
				make("thrust")
				.setFluff("X` thrusts with their Z`, intent at stabbing Y` to death!")
				.setRarity(.8f)
				.setAcc(.4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(2,0,12)
				.setWarmupOfTotal(TimeTier.FAST,TimeTier.SLOW)
				);
		addStance(WeaponType.DAGGER,sta);
		
		sta = new Stance(WeaponType.CLAYMORE);
		sta.addAttack(
				make("slash")
				.setFluff("X` slashes at Y` with their Z`!")
				.setRarity(4f)
				.setAcc(1f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.8f)//DOLATER: too fancy
				.setMix(12,4,2)
				.setTime(TimeTier.SLOWER,.5f)
				);
		sta.addAttack(
				make("slap")
				.setFluff("X` slaps Y` with their Z`!")
				.setRarity(2f)
				.setAcc(1.3f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.5f)
				.setMix(0,1,0)
				.setTimeTiers(TimeTier.NORMAL,TimeTier.SLOW,.5f)
				.setTimeMix(.6f)
				);
		sta.addAttack(
				make("power")
				.setFluff("X` prepares their Z`, then brings it down on Y` with a furious swing!")
				.setRarity(2f)
				.setAcc(1.3f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,1.3f)//DOLATER: too fancy
				.setMix(15,8,2)
				.setTime(TimeTier.SLOWER, TimeTier.SLOWEST,1.2f, .6f)//DOLATER: too fancy
				);
		addStance(WeaponType.CLAYMORE,sta);
		
		//DOLATER:
		copyStanceTo(WeaponType.SPEAR,WeaponType.LANCE);
		/*
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",0.8,150.0,1,5,35,"X` skewers Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.6,100.0,1,5,20,"X` thrusts at Y` with their Z`!",1,"pierce"));
		martialStance.addAttack(new Attack("smack",1,100.0,0,15,0,"X` smacks Y` with the side of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("charge",1.4,300.0,1,20,80,"X` charges forward with their Z`!",2,"pierce"));
		martialStance.finish();
		stanceMap.put("lance", martialStance);*/
		//DOLATER:
		copyStanceTo(WeaponType.BROADSWORD,WeaponType.SHOVEL);
		/*
		martialStance = new Stance();
		martialStance.addAttack(new Attack("thrust",.4,60.0,2,30,3,"X` thrusts at Y` with their Z`!",2,"blunt"));
		martialStance.addAttack(new Attack("slap",1.2,100.0,0,20,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("pole",.6,120.0,0,10,0,"X` hits Y` with the pole of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("smack",1,100.0,2,25,0,"X` smacks Y` with the side of their Z`!",1,"blunt"));
		martialStance.finish();
		stanceMap.put("shovel", martialStance);*/
		
		sta = new Stance(WeaponType.TEETH_GENERIC);
		sta.addAttack(
				make("bite")
				.setFluff("X` bites at Y`!")
				.setRarity(2f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.AVERAGE,.5f)
				.setMix(2,0,10)
				.setTime(TimeTier.NORMAL,.5f)
				);
		sta.addAttack(
				make("tear")
				.setFluff("X` tears into Y`'s flesh with their teeth!")
				.setRarity(1f)
				.setAcc(1.4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(10,1,7)
				.setTime(TimeTier.SLOW,.7f)
				);
		sta.addAttack(
				make("rip")
				.setFluff("X` rips up Y`'s flesh with their teeth!")
				.setRarity(1f)
				.setAcc(.7f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.8f)
				.setMix(1,0,1)
				.setTime(TimeTier.SLOWER,.8f)
				);
		addStance(WeaponType.TEETH_GENERIC,sta);
		
		sta = new Stance(WeaponType.REAVER_STANDING);
		sta.addAttack(
				make("kick")
				.setFluff("X` kicks Y`!")
				.setRarity(1f)
				.setAcc(.9f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.3f)
				.setMix(1,30,1)
				.setTime(TimeTier.SLOW,.4f)
				);
		addStance(WeaponType.REAVER_STANDING,sta);
		
		sta = new Stance(WeaponType.CLAWS_TEETH_GENERIC);
		sta.addAttack(
				make("bite")
				.setFluff("X` bites at Y`!")
				.setRarity(2f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.AVERAGE,.5f)
				.setMix(2,0,10)
				.setTime(TimeTier.NORMAL,.5f)
				);
		sta.addAttack(
				make("tear")
				.setFluff("X` tears into Y`'s flesh with their claws!")
				.setRarity(1f)
				.setAcc(1.4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(10,1,7)
				.setTime(TimeTier.SLOW,.7f)
				);
		sta.addAttack(
				make("rip")
				.setFluff("X` rips up Y`'s flesh with their claws!")
				.setRarity(1f)
				.setAcc(.7f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.8f)
				.setMix(1,0,1)
				.setTime(TimeTier.SLOWER,.8f)
				);
		addStance(WeaponType.CLAWS_TEETH_GENERIC,sta);
		
		sta = new Stance(WeaponType.BRANCHES);
		sta.addAttack(
				make("")
				.setFluff(new SRInOrder("X` rakes Y`!","X` rakes Y` with their branches!"))
				.setRarity(1f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.WEAK,.2f)
				.setMix(10,2,4)
				.setTime(TimeTier.SLOW,.4f)
				);
		sta.addAttack(
				make("")
				.setFluff(new SRInOrder("X` bashes Y`!","X` bashes Y` with their branches!"))
				.setRarity(1f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.1f)
				.setMix(1,40,0)
				.setTime(TimeTier.SLOWER,.7f)
				);
		addStance(WeaponType.BRANCHES,sta);
		
		/*
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bash",1.5,150.0,0,35,0,"X` bashes Y` with their fists!",1,"blunt"));
		martialStance.addAttack(new Attack("smash",1,100.0,0,30,0,"X` smashes Y` with their fists!",1,"blunt"));
		martialStance.addAttack(new Attack("power punch",.5,180.0,0,80,0,"X` lifts their fists over their head, and then brings them down on Y`!",2,"blunt"));
		martialStance.finish();
		stanceMap.put("generic fists", martialStance);*/
		copyStanceTo(WeaponType.REAVER_STANDING,WeaponType.GENERIC_FISTS);//TODO
		
		/*
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",0.5,150.0,1,5,35,"X` skewers Y` with their horn!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,100.0,1,5,20,"X` thrusts at Y` with their horn!",1,"pierce"));
		martialStance.addAttack(new Attack("smack",1,100.0,0,15,0,"X` smacks Y` with the side of their horn!",0,"blunt"));
		martialStance.addAttack(new Attack("charge",1.4,300.0,1,20,80,"X` charges forward with their horn!",2,"pierce"));
		martialStance.finish();
		stanceMap.put("unicorn horn", martialStance);*/
		copyStanceTo(WeaponType.SPEAR,WeaponType.UNICORN_HORN);

		
		sta = new Stance(WeaponType.TALONS_GENERIC);
		sta.addAttack(
				make("grip")
				.setFluff("X` grips Y` tightly!")
				.setRarity(1f)
				.setAcc(.9f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.6f)
				.setMix(5,0,10)
				.setWarmupOfTotal(TimeTier.FASTEST,TimeTier.SLOWER)
				);
		sta.addAttack(
				make("tear")
				.setFluff("X` tears into Y`'s flesh with their talons!")
				.setRarity(1f)
				.setAcc(1.4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(10,0,2)
				.setTime(TimeTier.SLOWEST,.5f)
				);
		sta.addAttack(
				make("rip")
				.setFluff("X` rips up Y`'s flesh with their talons!")
				.setRarity(1f)
				.setAcc(.6f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.8f)
				.setMix(1,0,1)
				.setTime(TimeTier.SLOWER,.8f)
				);
		addStance(WeaponType.TALONS_GENERIC,sta);
		
		/*
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",1,100.0,1,2,45,"X` skewers Y` with their rusty fishing spear!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,50.0,1,2,30,"X` thrusts at Y` with their rusty fishing spear!",2,"pierce"));
		martialStance.addAttack(new Attack("pole",.6,110.0,0,8,0,"X` hits Y` with the pole of their rusty fishing spear!",1,"blunt"));
		martialStance.finish();
		stanceMap.put("fishing spear", martialStance);*/
		
		/*
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slam",.5,300.0,0,40,0,"X` hits Y` with their rusty anchor!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.3,200.0,0,30,0,"X` slaps Y` with the side of their rusty anchor!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.9,400.0,100,50,0,"X` lifts their rusty anchor over their head, and then brings it down on Y`!",2,"sharp"));
		martialStance.finish();
		stanceMap.put("anchor", martialStance);*/
		
		//FIXME:
		copyStanceTo(WeaponType.MACE,WeaponType.FISH_ANCHOR);
		copyStanceTo(WeaponType.SPEAR,WeaponType.FISH_SPEAR);
	}


	public static Stance getStance(WeaponType t) {
		return stanceMap.get(t);
	}
	
	public static void weaponMetrics() throws FileNotFoundException {
		/*
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
				double comp = ((o1.raw)-(o2.raw));
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
		*/
	}
	
	public static final void dispTestWeapon(WeaponType t, List<Material> mats) {
		int tests = 1000;
		int totalTests = tests*WorldGen.getDummyInvs().size();
		
		for (Attack a: WeaponAttackFactory.getStance(t).giveList()) {
			a.display(0);
		}
		
		for (Material m: mats) {
			List<AttackMetric> metrics = new ArrayList<AttackMetric>();
			
			double totalDPS = 0;
			Weapon w = new Weapon(10,m,t);
			
			
			int i = 0;
			int size = w.getMartialStance().getAttackCount();
			
			while (i < size) {
				double damage = 0;
				double speed = 0;
				Attack holdAttack;
				holdAttack = w.getMartialStance().getAttack(i);
				double hits = 0;
				double fullhits = 0;
				for (int ta = 0; ta < tests;ta++) {
					for (int j = WorldGen.getDummyInvs().size()-1; j >=0;j--) {
						AttackReturn ret = Combat.handleTestAttack(holdAttack.impair(null,w,null)
								,WorldGen.getDummyInvs().get(j).atLevel(w.getLevel())
								,Armor.armorEffectiveness);
						damage += ret.damage;
						if (ret.code == ATK_ResultCode.DAMAGE) {
							hits++;
							fullhits++;
						}else {
							if (ret.code == ATK_ResultCode.ARMOR) {
								hits++;
							}
						}
						speed += ret.attack.getTime();
					}
				}
				
				damage/=totalTests;
				speed/=totalTests;
				hits/=totalTests;
				fullhits/=totalTests;
				AttackMetric am = new AttackMetric(w.getNameNoTier(), holdAttack.getName(), w.getMartialStance().getWeight(i)
						, hits,fullhits, damage, speed);
				metrics.add(am);
				totalDPS+=am.average_dps;
				
				i++;
			}
			
			w.display(2);//unsure how to handle qualities impacting the test, because I need to know what they do
			for (AttackMetric a: metrics) {
				a.total_percent_dps = a.average_dps/totalDPS;
				extra.println(a.toString());
			}
			extra.println("1 continue");
			extra.inInt(1);
		}
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
	
	public static class AttackMetric{
		public String basename, weaponname;
		public double rarity;
		public double average_hit, average_full, average_damage, average_time, average_dps;
		public double total_percent_dps;
		
		public AttackMetric(String weap, String nam, double rare, 
				double averagehit,double averagefull, double averagedam, double averagetime) {
			weaponname = weap;
			basename = nam;
			rarity = rare;
			average_hit = averagehit;
			average_full = averagefull;
			average_damage = averagedam;
			average_time = averagetime;
			average_dps = averagedam/averagetime;
		}
		
		@Override
		public String toString() {
			java.text.DecimalFormat formata = extra.F_TWO_TRAILING;
			return weaponname + "'s " + basename +": rng%"
					+ formata.format(rarity)
					+" hit%" +formata.format(average_hit)
					+" full%" +formata.format(average_full)
					+" raw: d"+formata.format(average_damage)
					+" _"+formata.format(average_time)
					+" avg: "+formata.format(average_dps)
					+ " contrib %"+ formata.format(total_percent_dps); 
		}
		
	}
	
	public enum DamageTier{
		NONE(0), WEAK(10), LOW(20), AVERAGE(30), HIGH(40), ASTOUNDING(50);
		private int damage;
		DamageTier(int _damage){
			damage = _damage;
		}
		
		/**
		 * used to set sound intensity
		 */
		public int getDam() {
			return damage;
		}
		
		public static float totalDamage(DamageTier start, DamageTier end, float lerp) {
			return extra.lerp(start.damage,end.damage,lerp);
		}
		
		public static int[] distribute(float damage, float sharpW, float bluntW, float pierceW) {
			int[] arr = new int[3];
			float total = sharpW+bluntW+pierceW;
			arr[0] = Math.round((sharpW/total)*damage);
			arr[1] = Math.round((bluntW/total)*damage);
			arr[2] = Math.round((pierceW/total)*damage);
			return arr;
		}
	}
	
	public enum TimeTier{
		SLOWEST(160),SLOWER(140),SLOW(120),NORMAL(100),FAST(80),FASTER(65), FASTEST(55);
		public final float time;
		TimeTier(float t){
			time = t;
		}
	}
	
	private static final AttackMaker make(String name) {
		return new AttackMaker(name);
	}
	
	public static class AttackMaker{
		private DamageTier start = DamageTier.AVERAGE, end = DamageTier.AVERAGE;
		private TimeTier t_tier1 = TimeTier.NORMAL, t_tier2 = TimeTier.NORMAL;
		private float slant = .5f, hitmult = 1f, timeSlant = 1f;
		private float sharpW = 1f, bluntW = 1f, pierceW = 1f;
		private float warmup = 50f, cooldown = 50f;
		private String name, desc = "", fluff = "X` attacks Y` with their Z`!";
		private StringResult fluffer;
		
		private float rarity = 1f;
		
		AttackMaker(String _name){
			name = _name;
		}

		public AttackMaker setFluff(String fluff) {
			this.fluff = fluff;
			return this;
		}
		public AttackMaker setFluff(StringResult fluff) {
			fluffer = fluff;
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
		
		public AttackMaker setAcc(float acc) {
			hitmult = acc;
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
		
		public AttackMaker setTime(TimeTier tier, float mix) {
			warmup = tier.time * mix;
			cooldown = tier.time * (1f-mix);
			return this;
		}
		
		public AttackMaker setTime(TimeTier tier1, TimeTier tier2, float slant, float mix) {
			float total = extra.lerp(tier1.time,tier2.time,slant);
			warmup = total * mix;
			cooldown = total * (1f-mix);
			return this;
		}
		
		public AttackMaker setTimeTiers(TimeTier tier1, TimeTier tier2, float slant) {
			t_tier1 = tier1;
			t_tier2 = tier2;
			timeSlant = slant;
			return this;
		}
		
		public AttackMaker setTimeMix(float time_mix) {
			setTime(t_tier1,t_tier2,timeSlant,time_mix);
			return this;
		}
		
		public AttackMaker setWarmup(float time) {
			warmup = time;
			return this;
		}
		
		public AttackMaker setCooldown(float time) {
			cooldown = time;
			return this;
		}
		
		public AttackMaker setWarmupOfTotal(TimeTier warmup_time, TimeTier total) {
			warmup = warmup_time.time;
			cooldown = total.time-warmup;
			return this;
		}
		
		public Attack finish() {
			int[] arr = DamageTier.distribute(DamageTier.totalDamage(start, end, slant),sharpW,bluntW,pierceW);
			return new Attack(name, desc,
					fluffer == null ? new SRInOrder(fluff) : fluffer,
					hitmult, AttackType.REAL_WEAPON, arr,
					warmup, cooldown);
		}
		
		public AttackMaker setRarity(float rare) {
			rarity = rare;
			return this;
		}
		
		public float getRarity() {
			return rarity;
		}
		
	}
	
	public static void addStance(WeaponType t, Stance s) {
		assert !stanceMap.containsKey(t);
		assert !stanceMap.containsValue(s);
		assert t == s.getWType();//we do this so it requires listing the stance at the bottom and top of the declaration
		//double checking ensures that human error is less likely, and top/bottom is considerably more readable
		//which is likely to prevent human error in the first place
		s.finish();
		stanceMap.put(t, s);
	}
	
	
	private void copyStanceTo(WeaponType from, WeaponType to) {
		Stance a = getStance(from);
		Stance b = new Stance(to);
		List<Attack> list = a.giveList();
		for (int i = 0; i < list.size(); i++) {
			b.addAttack(list.get(i).copy(),a.getRarity(i));
		}
		addStance(to, b);
	}
	
	/*
	public static Attack attackMaker(String name, String desc, String fluff, float totalDamage, int sharp, int blunt, int pierce,
			double hitMult, float warmup, float cooldown){
		int[] arr = DamageTier.distribute(totalDamage,sharp,blunt,pierce);
		return new Attack(name, desc, new SRInOrder(fluff), hitMult, AttackType.REAL_WEAPON, arr,
				warmup, cooldown);
	}*/
}
