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
		
		
		t = new Target();
		t.name = "lid";
		t.hit = 1;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = 1;
		t.slot = 0;
		t.type = TargetType.MIMIC;
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
		targetList.add(t);
		
		t = new Target();
		t.name = "eye";
		t.hit = .5;
		t.sharp = 2;
		t.blunt = 2;
		t.pierce = 2;
		t.rarity = 1;
		t.slot = 0;
		t.type = TargetType.MIMIC;
		targetList.add(t);
	}
	
	public enum TargetType{
		HUMANOID, MIMIC, NONE;
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
