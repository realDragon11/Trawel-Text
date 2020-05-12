import java.util.ArrayList;

public class TargetFactory {

	public static ArrayList<Target> targetList = new ArrayList<Target>();
	public static Target noTarget;
	public TargetFactory() {
		
		Target t = new Target();
		t.name = "head";
		t.hit = .8;
		t.sharp = .6;
		t.blunt = 3;
		t.pierce = 1;
		t.rarity = .6;
		t.slot = 0;
		t.type = TargetType.HUMANOID;
		t.bluntWounds.add(Attack.Wound.CONFUSED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.DIZZY);
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DICE);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "neck";
		t.hit = .5;
		t.sharp = 3;
		t.blunt = .5;
		t.pierce = 3;
		t.rarity = .4;
		t.slot = 0;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "chest";
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 2;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
		
		t = new Target();
		t.name = "arm";
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.bluntWounds.add(Attack.Wound.DISARMED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "leg";
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 3;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		targetList.add(t);
		
		t = new Target();
		t.name = "eye";
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		t.bluntWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "heart";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 2;
		t.rarity = .4;
		t.slot = 2;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "guts";
		t.hit = 1;
		t.sharp = 1.3;
		t.blunt = 1;
		t.pierce = 1.3;
		t.rarity = 1;
		t.slot = 2;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
		
		
		t = new Target();
		t.name = "ERROR NO TARGET";
		t.hit = 1;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 0;
		t.slot = -1;
		t.type = TargetType.NONE;
		targetList.add(t);
		noTarget = t;
		
		
		//mimic
		t = new Target();
		t.name = "lid";
		t.hit = 1;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.MIMIC;
		t.slashWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "body";
		t.hit = 1.2;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = 2;
		t.slot = 2;
		t.type = TargetType.OPEN_MIMIC;
		t.slashWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		targetList.add(t);
		
		
		//open mimic
		t = new Target();
		t.name = "lid";
		t.hit = 1;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.OPEN_MIMIC;
		t.slashWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "body";
		t.hit = 1.2;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = 2;
		t.slot = 2;
		t.type = TargetType.MIMIC;
		t.slashWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "tongue";
		t.hit = .9;
		t.sharp = 2;
		t.blunt = 2;
		t.pierce = 2;
		t.rarity = 1;
		t.slot = 0;
		t.type = TargetType.OPEN_MIMIC;
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		targetList.add(t);
		
		
		
		//quad
		
		
		t = new Target();
		t.name = "head";
		t.hit = .8;
		t.sharp = .6;
		t.blunt = 3;
		t.pierce = 1;
		t.rarity = .6;
		t.slot = 0;
		t.type = TargetType.QUAD;
		t.bluntWounds.add(Attack.Wound.CONFUSED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.DIZZY);
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DICE);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.bluntWounds.add(Attack.Wound.DISARMED);
		targetList.add(t);
		
		t = new Target();
		t.name = "neck";
		t.hit = .5;
		t.sharp = 3;
		t.blunt = .5;
		t.pierce = 3;
		t.rarity = .4;
		t.slot = 0;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "chest";
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 2;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
		
		t = new Target();
		t.name = "foreleg";
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		targetList.add(t);
		
		t = new Target();
		t.name = "hindleg";
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 3;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		targetList.add(t);
		
		t = new Target();
		t.name = "eye";
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		t.bluntWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "heart";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 2;
		t.rarity = .4;
		t.slot = 2;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "guts";
		t.hit = 1;
		t.sharp = 1.3;
		t.blunt = 1;
		t.pierce = 1.3;
		t.rarity = 1;
		t.slot = 2;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
	}
	
	public enum TargetType{
		HUMANOID, MIMIC, OPEN_MIMIC, NONE,QUAD;
	}
	
	public static Target randTarget(TargetType targetType) {
		ArrayList<Target> copyList = new ArrayList<Target>();
		ArrayList<Target> copyList2 = new ArrayList<Target>();
		for (Target mat: targetList){
			if (mat.type == targetType) {
			copyList.add(mat);}
		}
		double totalRarity = 0;
		Target mat;
		do {
			int i = (int) Math.floor((Math.random()*copyList.size()));
			mat = copyList.get(i);
			copyList2.add(mat);
			totalRarity += mat.rarity;
			copyList.remove(i);
		}while(!copyList.isEmpty());
		totalRarity*=Math.random();
		do {
			mat = copyList2.get(0);
			if (totalRarity > mat.rarity) {
				totalRarity-=mat.rarity;
				copyList2.remove(0);
			}else {
				totalRarity = 0;
			} 
				
				
		}while(totalRarity > 0);
		return mat;
	}
}
