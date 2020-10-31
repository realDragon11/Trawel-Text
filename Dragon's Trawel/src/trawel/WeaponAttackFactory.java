package trawel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeaponAttackFactory {
	private static Map<String,Stance> stanceMap = new HashMap<String,Stance>(); 
	
	public WeaponAttackFactory() {
		
		Stance martialStance = new Stance();
		martialStance.addAttack(new Attack("slash",1.5,100.0,40,5,0,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("stab",1.0,90.0,1,2,20,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.4,60.0,1,4,20,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pommel",1,110.0,0,12,0,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.1,100.0,0,10,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		stanceMap.put("longsword", martialStance);
		
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slash",1.3,110.0,35,8,0,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("stab",1.0,110.0,2,3,10,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("pommel",1,110.0,0,12,0,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.2,120.0,0,15,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.5,180.0,50,20,0,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"sharp"));
		stanceMap.put("broadsword", martialStance);
		
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bash",1.5,150.0,0,35,1,"X` bashes Y` with their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("smash",1,100.0,0,30,1,"X` smashes Y` with their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.5,180.0,1,80,5,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"blunt"));
		stanceMap.put("mace", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",1.1,120.0,1,2,50,"X` skewers Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.4,60.0,1,2,30,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pole",.6,120.0,0,10,0,"X` hits Y` with the pole of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("smack",1,100.0,1,8,0,"X` smacks Y` with the side of their Z`!",0,"blunt"));
		stanceMap.put("spear", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("hack",.9,90.0,20,20,0,"X` hacks at Y` with their Z`!",1,"sharp"));	
		martialStance.addAttack(new Attack("slap",.8,100.0,0,30,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("heft",1,120.0,0,10,0,"X` hits Y` with the heft of their Z`!",0,"blunt"));
		stanceMap.put("axe", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slash",1.3,80.0,30,1,0,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("slice",.9,50.0,20,0,0,"X` slices up Y` with their Z`!",0,"sharp"));
		martialStance.addAttack(new Attack("stab",1.2,80.0,5,0,35,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.6,50.0,10,0,30,"X` thrusts at Y` with their Z`!",2,"pierce"));
		martialStance.addAttack(new Attack("pommel",1,110.0,0,10,0,"X` hits Y` with the pommel of their Z`!",1,"blunt"));
		stanceMap.put("rapier", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slash",1,60.0,20,2,0,"X` slashes at Y` with their Z`!",0,"sharp"));
		martialStance.addAttack(new Attack("stab",1.1,60.0,1,1,12,"X` stabs at Y` with their Z`!",0,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.4,60.0,1,2,20,"X` thrusts at Y` with their Z`!",1,"pierce"));
		martialStance.addAttack(new Attack("slap",.8,80.0,0,4,0,"X` slaps Y` with the side of their Z`!",0,"blunt"));
		stanceMap.put("dagger", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slash",1.4,200.0,60,20,0,"X` slashes at Y` with their Z`!",1,"sharp"));
		martialStance.addAttack(new Attack("stab",0.4,300.0,30,10,3,"X` stabs at Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("pommel",.1,300.0,0,15,0,"X` hits Y` with the pommel of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.3,200.0,0,30,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.4,400.0,100,50,0,"X` lifts their Z` over their head, and then brings it down on Y`!",2,"sharp"));
		stanceMap.put("claymore", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",0.5,150.0,1,5,35,"X` skewers Y` with their Z`!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,100.0,1,5,20,"X` thrusts at Y` with their Z`!",1,"pierce"));
		martialStance.addAttack(new Attack("smack",1,100.0,0,15,0,"X` smacks Y` with the side of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("charge",1.4,300.0,1,20,80,"X` charges forward with their Z`!",2,"pierce"));
		stanceMap.put("lance", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("thrust",.4,60.0,2,30,3,"X` thrusts at Y` with their Z`!",2,"blunt"));
		martialStance.addAttack(new Attack("slap",1.2,100.0,0,20,0,"X` slaps Y` with the side of their Z`!",1,"blunt"));
		martialStance.addAttack(new Attack("pole",.6,120.0,0,10,0,"X` hits Y` with the pole of their Z`!",0,"blunt"));
		martialStance.addAttack(new Attack("smack",1,100.0,2,25,0,"X` smacks Y` with the side of their Z`!",1,"blunt"));
		stanceMap.put("shovel", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bite",1,100.0,5,0,35,"X` bites at Y` with their teeth!",2,"pierce"));
		martialStance.addAttack(new Attack("tear",1.4,140.0,20,0,20,"X` tears into Y`'s flesh with their teeth!",1,"pierce"));
		martialStance.addAttack(new Attack("rip",.6,120.0,30,0,30,"X` rips up Y`'s flesh with their teeth!",0,"pierce"));
		stanceMap.put("generic teeth", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("kick",1,100.0,1,35,0,"X` kicks at Y`!",2,"blunt"));
		martialStance.addAttack(new Attack("kick",1,100.0,1,35,0,"X` kicks at Y`!",2,"blunt"));
		martialStance.addAttack(new Attack("kick",1,100.0,1,35,0,"X` kicks at Y`!",2,"blunt"));
		stanceMap.put("standing reaver", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bite",1,100.0,5,0,35,"X` bites at Y` with their teeth!",2,"pierce"));
		martialStance.addAttack(new Attack("tear",1.4,140.0,20,0,20,"X` tears into Y`'s flesh with their claws!",1,"pierce"));
		martialStance.addAttack(new Attack("rip",.6,120.0,30,0,30,"X` rips up Y`'s flesh with their claws!",0,"pierce"));
		stanceMap.put("generic teeth and claws", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("rake",1.1,110.0,20,10,0,"X` rakes Y`!",1,"sharp"));
		martialStance.addAttack(new Attack("bash",1,120.0,0,35,35,"X` bashes Y` with their branches!",1,"blunt"));
		martialStance.addAttack(new Attack("rake",1.1,110.0,20,10,0,"X` rakes Y`!",1,"sharp"));
		martialStance.addAttack(new Attack("bash",1,120.0,0,35,35,"X` bashes Y` with their branches!",1,"blunt"));
		stanceMap.put("branches", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("bash",1.5,150.0,0,35,0,"X` bashes Y` with their fists!",1,"blunt"));
		martialStance.addAttack(new Attack("smash",1,100.0,0,30,0,"X` smashes Y` with their fists!",1,"blunt"));
		martialStance.addAttack(new Attack("power punch",.5,180.0,0,80,0,"X` lifts their fists over their head, and then brings them down on Y`!",2,"blunt"));
		stanceMap.put("generic fists", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",0.5,150.0,1,5,35,"X` skewers Y` with their horn!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,100.0,1,5,20,"X` thrusts at Y` with their horn!",1,"pierce"));
		martialStance.addAttack(new Attack("smack",1,100.0,0,15,0,"X` smacks Y` with the side of their horn!",0,"blunt"));
		martialStance.addAttack(new Attack("charge",1.4,300.0,1,20,80,"X` charges forward with their horn!",2,"pierce"));
		stanceMap.put("unicorn horn", martialStance);
		martialStance = new Stance();
		martialStance.addAttack(new Attack("grip",1,100.0,5,0,35,"X` grips Y` with their talons!",2,"pierce"));
		martialStance.addAttack(new Attack("tear",1.4,140.0,20,0,20,"X` tears into Y`'s flesh with their talons!",1,"pierce"));
		martialStance.addAttack(new Attack("rip",.6,120.0,30,0,30,"X` rips up Y`'s flesh with their talons!",0,"pierce"));
		stanceMap.put("generic talons", martialStance);
		
		martialStance = new Stance();
		martialStance.addAttack(new Attack("skewer",1,100.0,1,2,45,"X` skewers Y` with their rusty fishing spear!",1,"pierce"));	
		martialStance.addAttack(new Attack("thrust",.3,50.0,1,2,30,"X` thrusts at Y` with their rusty fishing spear!",2,"pierce"));
		martialStance.addAttack(new Attack("pole",.6,110.0,0,8,0,"X` hits Y` with the pole of their rusty fishing spear!",1,"blunt"));
		stanceMap.put("fishing spear", martialStance);
		
		martialStance = new Stance();
		martialStance.addAttack(new Attack("slam",.1,300.0,0,15,0,"X` hits Y` with their rusty anchor!",0,"blunt"));
		martialStance.addAttack(new Attack("slap",1.3,200.0,0,30,0,"X` slaps Y` with the side of their rusty anchor!",1,"blunt"));
		martialStance.addAttack(new Attack("power",.4,400.0,100,50,0,"X` lifts their rusty anchor over their head, and then brings it down on Y`!",2,"sharp"));
		stanceMap.put("anchor", martialStance);
	}
	
	public static Stance getStance(String str) {
		return stanceMap.get(str);
	}
}
