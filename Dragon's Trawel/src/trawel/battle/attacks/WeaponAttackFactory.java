package trawel.battle.attacks;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.yellowstonegames.core.WeightedTable;

import derg.strings.fluffer.StringResult;
import derg.strings.random.SRInOrder;
import trawel.battle.Combat;
import trawel.battle.Combat.ATK_ResultCode;
import trawel.battle.Combat.ATK_ResultType;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Stance.AttackLevel;
import trawel.core.Input;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.methods.extra;
import trawel.personal.Person;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.Feat;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;

public class WeaponAttackFactory {
	private static final Map<Weapon.WeaponType,Stance> stanceMap = new HashMap<Weapon.WeaponType,Stance>();
	
	private static final Map<IHasSkills,Stance> skillOwnerMap = new HashMap<IHasSkills,Stance>();
	/**
	 * so we can lookup what sources we have and select from them
	 */
	private static final Map<Skill,List<IHasSkills>> skillStances = new HashMap<Skill,List<IHasSkills>>();
	
	private static final Map<Skill,Attack> tacticMap = new HashMap<Skill,Attack>();
	
	private static WeightedTable weapTypeTable;
	
	public static WeaponType randWeapType() {
		return WeaponType.values()[weapTypeTable.random(Rand.getRand())];
	}
	
	/**
	 * metric testing only
	 * @return
	 */
	public static Map<Skill,List<IHasSkills>> getMetricStances(){
		return skillStances;
	}
	
	public WeaponAttackFactory() {
		int weapTypesSize = WeaponType.values().length;
		float[] weapTypeWeights = new float[weapTypesSize];
		for (int i = 0; i < weapTypesSize;i++) {
			weapTypeWeights[i] = WeaponType.values()[i].getRarity();
		}
		weapTypeTable = new WeightedTable(weapTypeWeights);
		
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
		addStance((WeaponType)null,sta);
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
				.setAcc(.7f)
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
		
		sta = new Stance(WeaponType.LANCE);
		sta.addAttack(
				make("charge")
				.setFluff("X` charges at Y` with their Z`!")
				.setRarity(3f)
				.setAcc(2.15f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.2f)
				.setMix(1,3,30)
				.setWarmupOfTotal(TimeTier.SLOWER,TimeTier.SLOWEST)
				);
		sta.addAttack(
				make("skewer")
				.setFluff("X` skewers Y` with their Z`!")
				.setRarity(3f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.4f)
				.setMix(2,1,30)
				.setWarmupOfTotal(TimeTier.NORMAL, TimeTier.SLOWER)
				);
		sta.addAttack(
				make("thrust")
				.setFluff("X` thrusts at Y` with their Z`!")
				.setRarity(3f)
				.setAcc(.9f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.2f)
				.setMix(3,3,35)
				.setWarmupOfTotal(TimeTier.HALF_FAST, TimeTier.NORMAL)
				);
		sta.addAttack(
				make("smack")
				.setFluff("X` smacks Y` with the side of their Z`!")
				.setRarity(2f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(0,1,0)
				.setWarmupOfTotal(TimeTier.HALF_NORMAL,TimeTier.SLOW)
				);
		addStance(WeaponType.LANCE,sta);
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
				make("rake")
				.setFluff(new SRInOrder("X` rakes Y`!","X` rakes Y` with their branches!"))
				.setRarity(1f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.WEAK,.2f)
				.setMix(10,2,4)
				.setTime(TimeTier.SLOW,.4f)
				);
		sta.addAttack(
				make("bash")
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
		//this is intentionaly a copy
		copyStanceTo(WeaponType.LANCE,WeaponType.UNICORN_HORN);

		
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
		
		sta = new Stance(WeaponType.FISH_ANCHOR);
		sta.addAttack(
				make("bash")
				.setFluff("X` bashes Y` with their Z`!")
				.setRarity(4f)
				.setAcc(1.3f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.33f)
				.setMix(2,20,4)
				.setWarmupOfTotal(TimeTier.NORMAL, TimeTier.SLOWER)
				);
		sta.addAttack(
				make("smash")
				.setFluff("X` smashes the ground near Y` with their Z`, sending barbs into the air!")
				.setRarity(2f)
				.setAcc(3.5f)//insanely accurate
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.4f)
				.setMix(4,10,5)
				.setWarmupOfTotal(TimeTier.HALF_NORMAL, TimeTier.FAST)
				);
		sta.addAttack(
				make("spray")
				.setFluff("X` swings their Z` in an arc, spraying barbs at Y`!")
				.setRarity(1f)
				.setAcc(2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.44f)
				.setMix(4,0,10)
				.setWarmupOfTotal(TimeTier.HALF_NORMAL, TimeTier.SLOW)
				);
		sta.addAttack(
				make("power")
				.setFluff("X` prepares their Z`, then brings it down on Y` with a furious swing!")
				.setRarity(1f)
				.setAcc(1.8f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.95f)
				.setMix(2,20,4)
				.setWarmupOfTotal(TimeTier.SLOW, TimeTier.SLOWEST)
				);
		addStance(WeaponType.FISH_ANCHOR,sta);
		
		sta = new Stance(WeaponType.FISH_SPEAR);
		sta.addAttack(
				make("jab")
				.setFluff("X` jabs at Y` with their Z`!")
				.setRarity(3f)
				.setAcc(3f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.25f)
				.setMix(6,0,30)
				.setWarmupOfTotal(TimeTier.HALF_FAST, TimeTier.NORMAL)
				);
		sta.addAttack(
				make("harpoon")
				.setFluff("X` throws their Z` at Y` and reels it back!")
				.setRarity(2f)
				.setAcc(1.2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.18f)
				.setMix(20,10,45)
				.setWarmupOfTotal(TimeTier.FAST, TimeTier.SLOWER)
				);
		sta.addAttack(
				make("hook")
				.setFluff("X` hooks Y` with the blade of their Z`!")
				.setRarity(3f)
				.setAcc(1.5f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.08f)
				.setMix(1,0,0)
				.setWarmupOfTotal(TimeTier.HALF_NORMAL,TimeTier.FAST)
				);
		sta.addAttack(
				make("smack")
				.setFluff("X` smacks Y` with the pole of their Z`!")
				.setRarity(.5f)
				.setAcc(.9f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.38f)
				.setMix(0,1,0)
				.setWarmupOfTotal(TimeTier.HALF_NORMAL,TimeTier.NORMAL)
				);
		addStance(WeaponType.FISH_SPEAR,sta);
		
		//skill attack section
		//since skill attacks don't have weapons they should be a bit better since a good material is often 1x to 3x damage
		sta = new Stance(Archetype.HEDGE_MAGE,Skill.ARCANIST);
		sta.addAttack(
				make("sparks")//best DPS, no bonus effect
				.setFluff("X` conjures sparks at Y`!")
				.setRarity(1f)
				.setAcc(1.5f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.8f)
				.setElementalMix(1,0,3)
				.setWarmupOfTotal(TimeTier.FASTER, TimeTier.FAST)
				);
		sta.addAttack(
				make("candle")//damaging, burns armor bonus
				.setFluff("X` shoots a small gout of flame at Y`!")
				.setRarity(1f)
				.setAcc(1.1f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.5f)
				.setElementalMix(1, 0, 0)
				.setWarmupOfTotal(TimeTier.NORMAL, TimeTier.SLOW)
				.setAttackBonus(AttackBonus.CHAR)
				);
		sta.addAttack(
				make("chill")//very accurate, slow bonus
				.setFluff("X` forces Y` to suffer through a deep chill!")
				.setRarity(1f)
				.setAcc(3f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.45f)
				.setElementalMix(0, 1, 0)
				.setWarmupOfTotal(TimeTier.NORMAL, TimeTier.SLOW)
				.setAttackBonus(AttackBonus.GLACIATE)
				);
		addStance(Archetype.HEDGE_MAGE,sta, new AttackLevel() {
			
			@Override
			public int getEffectiveLevel(Person p) {
				AttributeBox a = p.fetchAttributes();
				return a.getEffectiveAttributeLevel(a.getClarity());
			}
		});
		
		sta = new Stance(Archetype.SEA_SAGE,Skill.ARCANIST);
		sta.addAttack(
				make("sudden squall")//fastest, meh damage, low acc
				.setFluff("X` quickly forces a squall to form around Y`!")
				.setRarity(2f)
				.setAcc(.8f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.1f)
				.setElementalMix(0, 2, 0)
				.setTime(TimeTier.FASTEST,.8f)
				);
		sta.addAttack(
				make("brackish burst")//accurate, fast, meh damage, partly physical, slow bonus
				.setFluff("X` buffets Y` with cold salty water!")
				.setRarity(2f)
				.setAcc(2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.05f)
				.setMix(0,1,0)
				.setElementalRider(.3f,0,1,0)
				.setTime(TimeTier.FASTER,.6f)
				.setAttackBonus(AttackBonus.GLACIATE)
				);
		sta.addAttack(
				make("stormy swell")//slow, amazing damage
				.setFluff("X` conjures a localized coldfront, whips it into a storm, shocking and freezing Y`!")
				.setRarity(.7f)
				.setAcc(1.5f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,1.1f)
				.setElementalMix(0, 2, 3)
				.setWarmupOfTotal(TimeTier.SLOW, TimeTier.SLOWEST)
				);
		addStance(Archetype.SEA_SAGE,sta, new AttackLevel() {
			
			@Override
			public int getEffectiveLevel(Person p) {
				AttributeBox a = p.fetchAttributes();
				return a.getEffectiveAttributeLevel(a.getClarity());
			}
		});
		//drudger/monster version
		copyStanceTo(Skill.ARCANIST,Archetype.SEA_SAGE,Archetype.FISH_MONSOON);

		
		//TODO: skill attack metric tester, fix needing to cast fist
		sta = new Stance(WeaponType.NULL_WAND);
		sta.addAttack(//too troublesome to track down this error rn, fix entirely in next release
				make("fist")
				.setFluff("X` casts fist on Y`!")
				.setRarity(1f)
				.setAcc(1f)
				.setDamage(DamageTier.AVERAGE,DamageTier.AVERAGE,.5f)
				.setMix(1,1,1)
				.setTime(TimeTier.NORMAL,.5f)
				);
		sta.setBonusSkillAttacks(8);//attempt to turn all attacks into bonus attacks
		addStance(WeaponType.NULL_WAND,sta);
		
		sta = new Stance(Feat.FLAME_WARDEN,Skill.ARCANIST);//slow, powerful attacks
		sta.addAttack(
				make("fireball")//very slow, insane damage, burn armor bonus
				.setFluff("X` conjures a fireball and hurls it at Y`!")
				.setRarity(1f)
				.setAcc(2f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,1.2f)
				.setElementalMix(1,0,0)
				.setWarmupOfTotal(TimeTier.SLOWER, TimeTier.SLOWEST)
				.setAttackBonus(AttackBonus.CHAR)
				);
		sta.addAttack(
				make("kindle")//normal speed, highish damage
				.setFluff("X` conjures sparks at Y`!")
				.setRarity(1f)
				.setAcc(1.4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.9f)
				.setElementalMix(2, 0, 3)
				.setWarmupOfTotal(TimeTier.FAST, TimeTier.NORMAL)
				);
		sta.addAttack(
				make("flame blast")//low acc, slower speed, high damage, damage armor bonus
				.setFluff("X` shoots a gout of flame at Y`!")
				.setRarity(1f)
				.setAcc(.8f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.45f)
				.setElementalMix(1, 0, 0)
				.setWarmupOfTotal(TimeTier.FASTER, TimeTier.SLOW)
				.setAttackBonus(AttackBonus.CHAR)
				);
		addStance(Feat.FLAME_WARDEN,sta, new AttackLevel() {
			
			@Override
			public int getEffectiveLevel(Person p) {
				AttributeBox a = p.fetchAttributes();
				return a.getEffectiveAttributeLevel(a.getClarity());
			}
		});
		
		sta = new Stance(Feat.FROST_WARDEN,Skill.ARCANIST);//mix of attacks, usually fairly accurate
		sta.addAttack(//accurate, low damage, fast, chill bonus
				make("chill")
				.setFluff("X` forces Y` to suffer through a deep chill!")
				.setRarity(1f)
				.setAcc(4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.3f)
				.setElementalMix(0, 1, 0)
				.setWarmupOfTotal(TimeTier.FASTER, TimeTier.FAST)
				.setAttackBonus(AttackBonus.GLACIATE)
				);
		sta.addAttack(//powerful, fast out slow cooldown, chill bonus
				make("frostbite")
				.setFluff("X` quickly cools the air near Y`, forcing them to endure the cold!")
				.setRarity(1f)
				.setAcc(1.5f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.3f)
				.setElementalMix(0, 1, 0)
				.setWarmupOfTotal(TimeTier.FAST, TimeTier.SLOWER)
				.setAttackBonus(AttackBonus.GLACIATE)
				);
		sta.addAttack(
				make("icicle")//physical, no chill bonus, highest damage, slowest
				.setFluff("X` conjures a spear of ice and hurls it at Y`!")
				.setRarity(.5f)
				.setAcc(1.2f)
				.setDamage(DamageTier.HIGH,DamageTier.ASTOUNDING,.9f)
				.setMix(0,1,6)
				.setElementalRider(.3f,0,1,0)
				.setWarmupOfTotal(TimeTier.SLOW, TimeTier.SLOWEST)
				);
		sta.addAttack(
				make("iceball")//physical, no chill bonus, fast, lowest damage and acc
				.setFluff("X` forms a ball of ice and shatters it on Y`!")
				.setRarity(.5f)
				.setAcc(1f)//lowest accuracy
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.05f)
				.setMix(1,10,0)
				.setElementalRider(.3f,0,1,0)
				.setWarmupOfTotal(TimeTier.HALF_FAST, TimeTier.FASTER)
				);
		addStance(Feat.FROST_WARDEN,sta, new AttackLevel() {
			
			@Override
			public int getEffectiveLevel(Person p) {
				AttributeBox a = p.fetchAttributes();
				return a.getEffectiveAttributeLevel(Math.max(a.getClarity(),a.getStrength()));
			}
		});
		
		//attacks are erratic, one is insanely accurate but slower and low damage, another is middling
		//and a final one is insanely fast but below average accuracy and damage
		sta = new Stance(Feat.SHOCK_SAVANT,Skill.ARCANIST);
		sta.addAttack(//overall good stats, armor damage bonus
				make("sparks")
				.setFluff("X` conjures sparks at Y`!")
				.setRarity(1f)
				.setAcc(1.2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.8f)
				.setElementalMix(1, 0, 3)//more shock inclined
				.setWarmupOfTotal(TimeTier.FAST, TimeTier.NORMAL)
				.setAttackBonus(AttackBonus.CHAR)
				);
		sta.addAttack(//very high accuracy, slow but decent damage
				make("zap")
				.setFluff("X` zaps Y` with ambient charge!")
				.setRarity(1f)
				.setAcc(4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.75f)
				.setElementalMix(0, 0, 1)
				.setWarmupOfTotal(TimeTier.NORMAL, TimeTier.SLOW)
				);
		sta.addAttack(//very fast, inaccurate
				make("bolt")
				.setFluff("X` casts bolts of lightning around Y`!")
				.setRarity(1f)
				.setAcc(.8f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.10f)
				.setElementalMix(0, 0, 1)
				.setWarmupOfTotal(TimeTier.HALF_FASTEST, TimeTier.FASTEST)
				);
		addStance(Feat.SHOCK_SAVANT,sta, new AttackLevel() {
			
			@Override
			public int getEffectiveLevel(Person p) {
				AttributeBox a = p.fetchAttributes();
				return a.getEffectiveAttributeLevel(Math.max(a.getClarity(),a.getDexterity()));
			}
		});
		
		tacticMap.put(Skill.TACTIC_TEST,
				make("test tactic")
				.setFluff("X` examines V`!")
				.setWarmupOfTotal(TimeTier.HALF_FAST, TimeTier.FAST)
				.finish().setSkill_for(Skill.TACTIC_TEST).setRider(AttackBonus.ROLL)
				);
		
		tacticMap.put(Skill.TACTIC_DUCK_ROLL,
				make("duck 'n roll")
				.setFluff("X` prepares for a tactical roll towards V`!")
				.setWarmupOfTotal(TimeTier.HALF_NORMAL, TimeTier.NORMAL)
				.finish().setSkill_for(Skill.TACTIC_DUCK_ROLL).setRider(AttackBonus.ROLL)
				);
		tacticMap.put(Skill.TACTIC_SINGLE_OUT,
				make("single out")
				.setFluff("X` singles out V`!")
				.setWarmupOfTotal(TimeTier.INSTANT, TimeTier.FASTEST)
				.finish().setSkill_for(Skill.TACTIC_SINGLE_OUT).setRider(AttackBonus.SINGLE_OUT)
				);
		tacticMap.put(Skill.TACTIC_CHALLENGE,
				make("challenge")
				.setFluff("X` challenges V`!")
				.setWarmupOfTotal(TimeTier.INSTANT, TimeTier.FASTEST)
				.finish().setSkill_for(Skill.TACTIC_CHALLENGE).setRider(AttackBonus.CHALLENGE)
				);
		tacticMap.put(Skill.TACTIC_TAKEDOWN,
				make("planned takedown")
				.setFluff("X` sizes up `V and plans a takedown!")
				.setWarmupOfTotal(TimeTier.INSTANT, TimeTier.FAST)
				.finish().setSkill_for(Skill.TACTIC_TAKEDOWN).setRider(AttackBonus.TAKEDOWN)
				);
		
		sta = new Stance(Archetype.ACRO_DAREDEVIL,Skill.OPPORTUNIST);
		sta.addAttack(
				make("rough tumble")//Minstrel DQ9
				.setFluff("X` tumbles towards Y`, attempting to bodycheck them!")
				.setRarity(1f)
				.setAcc(1.4f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.4f)
				.setMix(0, 1, 0)
				.setWarmupOfTotal(TimeTier.HALF_NORMAL, TimeTier.SLOW)
				,AttackBonus.ROLL);
		sta.addAttack(
				make("daring dive")
				.setFluff("X` dives into Y`, leaving themselves open!")
				.setRarity(1f)
				.setAcc(.8f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.5f)
				.setMix(0, 1, 0)
				.setWarmupOfTotal(TimeTier.HALF_FASTEST, TimeTier.FASTER)
				,AttackBonus.CHALLENGE);
		addStance(Archetype.ACRO_DAREDEVIL,sta, new AttackLevel() {
			
			@Override
			public int getEffectiveLevel(Person p) {
				AttributeBox a = p.fetchAttributes();
				return a.getEffectiveAttributeLevel(Math.max(a.getStrength(),a.getDexterity()));
			}
		});
		sta = new Stance(Archetype.FISH_TALL,Skill.OPPORTUNIST);
		sta.addAttack(
				make("bitch slap")//idk the proper name for disrespectful slapping
				.setFluff("X` slaps Y`!")
				.setRarity(1f)
				.setAcc(1.5f)
				.setDamage(DamageTier.AVERAGE,DamageTier.HIGH,.2f)
				.setMix(0, 1, 0)
				.setWarmupOfTotal(TimeTier.HALF_FAST, TimeTier.NORMAL)
				,AttackBonus.CHALLENGE);
		sta.addAttack(
				make("bitch hand")
				.setFluff("X` backhands Y` and leaves their army to it!")
				.setRarity(3f)//worse and more common so they aren't always slapping
				.setAcc(1.2f)
				.setDamage(DamageTier.AVERAGE,DamageTier.LOW,.4f)
				.setMix(0, 1, 0)
				.setWarmupOfTotal(TimeTier.HALF_NORMAL, TimeTier.SLOW)
				,AttackBonus.SINGLE_OUT);
		addStance(Archetype.FISH_TALL,sta, new AttackLevel() {
			
			@Override
			public int getEffectiveLevel(Person p) {
				AttributeBox a = p.fetchAttributes();
				return a.getEffectiveAttributeLevel(a.getStrength());
			}
		});
		
		assert skillStances.size() > 0;
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
	
	public static final void dispTestWeapon(int level,WeaponType t,IHasSkills has, List<Material> mats) {
		int tests = 1000;
		int totalTests = tests*extra.getDumInvs().size();
		
		if (has != null) {
			for (Attack a: WeaponAttackFactory.getStance(has).giveList()) {
				a.display(0);
			}
		}else {
			for (Attack a: WeaponAttackFactory.getStance(t).giveList()) {
				a.display(0);
			}
		}
		
		
		for (Material m: mats) {
			List<AttackMetric> metrics = new ArrayList<AttackMetric>();
			
			double totalDPS = 0;
			Weapon w = new Weapon(level,m,t);
			
			Stance stance = has == null ? w.getMartialStance() : WeaponAttackFactory.getStance(has);
			
			int i = 0;
			int size = stance.getAttackCount();
			
			while (i < size) {
				double damage = 0;
				double speed = 0;
				Attack holdAttack;
				holdAttack = stance.getAttack(i);
				double hits = 0;
				double fullhits = 0;
				for (int ta = 0; ta < tests;ta++) {
					for (int j = extra.getDumInvs().size()-1; j >=0;j--) {
						AttackReturn ret = Combat.handleTestAttack(holdAttack.impair(null,w,null)
								,extra.getDumInvs().get(j).atLevel(w.getLevel())
								);
						damage += ret.damage;
						if (ret.type == ATK_ResultType.IMPACT) {
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
				AttackMetric am = new AttackMetric(w.getNameNoTier(), holdAttack.getName(), stance.getWeight(i)
						, hits,fullhits, damage, speed);
				metrics.add(am);
				totalDPS+=am.average_dps;
				
				i++;
			}
			
			w.display(2);//unsure how to handle qualities impacting the test, because I need to know what they do
			for (AttackMetric a: metrics) {
				a.total_percent_dps = a.average_dps/totalDPS;
				Print.println(a.toString());
			}
			Print.println("1 continue");
			Input.inInt(1);
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
			java.text.DecimalFormat formata = Print.F_TWO_TRAILING;
			return weaponname + "'s " + Print.cutPadLenFront(8, basename) +": rng%"
					+ formata.format(rarity)
					+" hit%" +formata.format(average_hit)
					+" full%" +formata.format(average_full)
					+" raw: d"+Print.cutPadLenFront(6, formata.format(average_damage))
					+" _"+Print.cutPadLenFront(6,formata.format(average_time))
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
		
		public static int[] distribute(float damage, float sharpW, float bluntW, float pierceW, float igniteW, float frostW, float elecW) {
			int[] arr = new int[7];
			float total = sharpW+bluntW+pierceW+igniteW+frostW+elecW;
			arr[0] = Math.round((sharpW/total)*damage);
			arr[1] = Math.round((bluntW/total)*damage);
			arr[2] = Math.round((pierceW/total)*damage);
			arr[3] = Math.round((igniteW/total)*damage);
			arr[4] = Math.round((frostW/total)*damage);
			arr[5] = Math.round((elecW/total)*damage);
			arr[6] = 0;//decay damage, needed here
			return arr;
		}
	}
	
	public enum TimeTier{
		SLOWEST(160),SLOWER(140),SLOW(120),NORMAL(100),FAST(80),FASTER(65), FASTEST(55)
		,
		//used for 
		HALF_FASTEST(TimeTier.FASTEST.time/2),HALF_FAST(TimeTier.FAST.time/2),
		HALF_NORMAL(TimeTier.NORMAL.time/2),
		INSTANT(0)
		;
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
		private float sharpW = 0f, bluntW = 0f, pierceW = 0f;
		private float igniteW = 0f, frostW = 0f, elecW = 0f;
		private float warmup = 50f, cooldown = 50f;
		private boolean customWeight = false, magicbypass = false, magicRider = false;
		private String name, desc = "", fluff = "X` attacks Y` with their Z`!";
		private StringResult fluffer;
		private float percentAsRider = 0f;
		private AttackBonus attackBonus = null;
		
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
		
		/**
		 * note that this gets +.5f base added to it, to make hitting happen more often
		 * a .5+.5 = 1 = ~50% chance to hit against a dodge 1x
		 */
		public AttackMaker setAcc(float acc) {
			hitmult = acc+.5f;
			return this;
		}
		
		public AttackMaker setMix(float sharp, float blunt, float pierce) {
			assert customWeight == false;//no mixing mixes for now
			sharpW = sharp;
			bluntW = blunt;
			pierceW = pierce;
			customWeight = true;
			return this;
		}
		
		public AttackMaker setElementalMix(float ignite, float frost, float elec) {
			assert customWeight == false;//no mixing mixes for now
			igniteW = ignite;
			frostW = frost;
			elecW = elec;
			customWeight = true;
			magicbypass = true;
			return this;
		}
		public AttackMaker setElementalRider(float _percentAsRider,float ignite, float frost, float elec) {
			assert customWeight == true;
			assert magicbypass == false;
			percentAsRider = _percentAsRider;
			igniteW = ignite;
			frostW = frost;
			elecW = elec;
			customWeight = true;
			magicRider = true;
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
		
		public AttackMaker setAttackBonus(AttackBonus bonus) {
			attackBonus = bonus;
			return this;
		}
		
		public Attack finish() {
			if (!customWeight) {
				sharpW = 1f;
				bluntW = 1f;
				pierceW = 1f;
			}
			if (magicRider) {//distribute the distribution :D
				if (percentAsRider == 1) {
					float totalBaseWeight = sharpW+bluntW+pierceW;
					float totalRiderWeight = igniteW+frostW+elecW;
					float totalTotalWeight = totalBaseWeight+totalRiderWeight;
					float baseAdjust = totalBaseWeight/totalTotalWeight;
					float riderAdjust = totalRiderWeight/totalTotalWeight;
					sharpW*=baseAdjust;
					bluntW*=baseAdjust;
					pierceW*=baseAdjust;
					igniteW*=riderAdjust;
					frostW*=riderAdjust;
					elecW*=riderAdjust;
				}else {
					float totalBaseWeight = (1-percentAsRider)*(sharpW+bluntW+pierceW);
					float totalRiderWeight = percentAsRider*(igniteW+frostW+elecW);
					float totalTotalWeight = totalBaseWeight+totalRiderWeight;
					float baseAdjust = totalBaseWeight/totalTotalWeight;
					float riderAdjust = totalRiderWeight/totalTotalWeight;
					sharpW*=baseAdjust;
					bluntW*=baseAdjust;
					pierceW*=baseAdjust;
					igniteW*=riderAdjust;
					frostW*=riderAdjust;
					elecW*=riderAdjust;
				}
				
			}
			int[] arr = DamageTier.distribute(DamageTier.totalDamage(start, end, slant),sharpW,bluntW,pierceW,igniteW,frostW,elecW);
			return new Attack(name, desc,
					fluffer == null ? new SRInOrder(fluff) : fluffer,
					hitmult, arr,
					warmup, cooldown,magicbypass).setRider(attackBonus);
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
	
	public static void addStance(IHasSkills iHas, Stance s,AttackLevel leveler) {
		assert !skillOwnerMap.containsKey(iHas);
		assert !skillOwnerMap.containsValue(s);
		assert s.getSkillSource() == iHas;
		s.setLeveler(leveler);
		s.finish();
		skillOwnerMap.put(iHas, s);
		List<IHasSkills> list = skillStances.getOrDefault(s.getSkill(), new ArrayList<IHasSkills>());
		list.add(iHas);
		skillStances.put(s.getSkill(),list);//in case we created it
		assert list.size() > 0;
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
	
	private void copyStanceTo(Skill skill,IHasSkills from, IHasSkills to) {
		Stance a = getStance(from);
		Stance b = new Stance(to,skill);
		List<Attack> list = a.giveList();
		for (int i = 0; i < list.size(); i++) {
			b.addAttack(list.get(i).copy(),a.getRarity(i));
		}
		addStance(to, b,a.getLeveler());
	}


	public static Stance getStance(IHasSkills source) {
		return skillOwnerMap.get(source);
	}
	
	public static boolean hasStance(IHasSkills source) {
		return skillOwnerMap.containsKey(source);
	}
	
	/**
	 * do not modify, contains backing info
	 */
	public static List<IHasSkills> getSources(Skill s){
		return skillStances.get(s);
	}


	public static ImpairedAttack rollAttack(IHasSkills source, IHasSkills source2, Person attacker, Person defender) {
		return Rand.randFloat() >= .5f ? getStance(source).randAtts(1, null, attacker, defender).get(0) : getStance(source2).randAtts(1, null, attacker, defender).get(0);
	}
	public static ImpairedAttack rollAttack(IHasSkills source, Person attacker, Person defender) {
		return getStance(source).randAtts(1, null, attacker, defender).get(0);
	}
	
	public static Attack getTactic(Skill skill) {
		return tacticMap.get(skill);
	}
	
	public static ImpairedAttack getFinalTactic(Skill skill, Person attacker, Person defender) {
		return tacticMap.get(skill).impairTactic(attacker, defender);
	}
	
	/*
	public static Attack attackMaker(String name, String desc, String fluff, float totalDamage, int sharp, int blunt, int pierce,
			double hitMult, float warmup, float cooldown){
		int[] arr = DamageTier.distribute(totalDamage,sharp,blunt,pierce);
		return new Attack(name, desc, new SRInOrder(fluff), hitMult, AttackType.REAL_WEAPON, arr,
				warmup, cooldown);
	}*/
}
