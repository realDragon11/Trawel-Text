import java.util.ArrayList;

public class RaceFactory {
	public static Race misc = new Race();
	public static ArrayList<Race> raceList = new ArrayList<Race>();
	
	public RaceFactory() {
		misc = new Race();
		misc.name = "human";
		misc.namePlural = "humans";
		misc.swears.add("thinskin");
		misc.aimMod = 1.05;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1;
		misc.speedMod = 1.05;
		misc.tradeMod = 1.2;
		misc.rarity = 3;
		misc.insultList.add("Your skin is as thin as your brain!");
		misc.baseMap = "human";
		misc.raceMaps.add("0");
		misc.raceMaps.add("1");
		misc.raceMaps.add("2");
		misc.magicPower = 1;
		misc.defPower = 2;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "orc";
		misc.namePlural = "orcs";
		misc.swears.add("greenskin");
		misc.aimMod = .9;
		misc.damMod = 1.2;
		misc.dodgeMod = 1.1;
		misc.hpMod = 1.2;
		misc.speedMod = .9;
		misc.tradeMod = .8;
		misc.rarity = 1;
		misc.insultList.add("They say 'as dumb as an orc' for a reason!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 0;
		misc.defPower = 2;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "lizardfolk";
		misc.namePlural = "lizardfolk";
		misc.swears.add("lizard");
		misc.swears.add("reptile");
		misc.aimMod = 1.2;
		misc.damMod = 1;
		misc.dodgeMod = 1.1;
		misc.hpMod = 1;
		misc.speedMod = 1;
		misc.tradeMod = .9;
		misc.rarity = 1;
		misc.insultList.add("What do you want, lizard?");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 1;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "high elf";
		misc.namePlural = "high elves";
		misc.swears.add("pointy-ear");
		misc.aimMod = 1.2;
		misc.damMod = 1;
		misc.dodgeMod = 1.1;
		misc.hpMod = .7;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = .5;
		misc.insultList.add("Have you heard of the 'high' elves?");
		misc.insultList.add("What are you going to do, stab me with your ears?");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 2;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "low elf";
		misc.namePlural = "low elves";
		misc.swears.add("tree-hugger");
		misc.swears.add("pointy-ear");
		misc.swears.add("hippie");
		misc.aimMod = 1.1;
		misc.damMod = 1;
		misc.dodgeMod = 1.3;
		misc.hpMod = .75;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = .5;
		misc.insultList.add("Go, hug a tree!");
		misc.insultList.add("What are you going to do, stab me with your ears?");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 1;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "wolf-bound";
		misc.namePlural = "wolf-bound";
		misc.swears.add("dog");
		misc.aimMod = 1;
		misc.damMod = .9;
		misc.dodgeMod = 1.2;
		misc.hpMod = .9;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = .25;
		misc.insultList.add("Go fetch, dog!");
		misc.insultList.add("Howl somewhere else, dog!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 0;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "turtle-kin";
		misc.namePlural = "turtle-kin";
		misc.swears.add("shelly");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1.5;
		misc.speedMod = .7;
		misc.tradeMod = 1;
		misc.rarity = .25;
		misc.insultList.add("I'll rip that shell right off!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 1;
		misc.defPower = 3;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "cat-kin";
		misc.namePlural = "cat-kin";
		misc.swears.add("fleabag");
		misc.swears.add("hairball");
		misc.aimMod = 1.2;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = .25;
		misc.insultList.add("Stay away, I don't want fleas!");
		misc.insultList.add("Go meow somewhere else!");
		misc.insultList.add("You'd make a nice rug, cat.");
		misc.baseMap = "cat";
		misc.raceMaps.add("0");
		misc.raceMaps.add("2");
		misc.raceMaps.add("3");
		misc.raceMaps.add("4");
		misc.magicPower = 0;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "slugman";
		misc.namePlural = "slugmen";
		misc.swears.add("slowpoke");
		misc.aimMod = 2;
		misc.damMod = 1;
		misc.dodgeMod = .5;
		misc.hpMod = 1.2;
		misc.speedMod = .6;
		misc.tradeMod = 1.2;
		misc.rarity = .2;
		misc.insultList.add("Clean up after your trail!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 2;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "tenderheart";
		misc.namePlural = "tenderhearts";
		misc.swears.add("exposed");
		misc.aimMod = 1;
		misc.damMod = .7;
		misc.dodgeMod = 1.3;
		misc.hpMod = 1.1;
		misc.speedMod = 1;
		misc.tradeMod = 1.2;
		misc.rarity = .2;
		misc.insultList.add("Look out, your hearts hanging out!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 2;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "skeleton";
		misc.namePlural = "skeletons";
		misc.swears.add("boneman");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = 1.3;
		misc.hpMod = .5;
		misc.speedMod = 1.3;
		misc.tradeMod = 1;
		misc.rarity = .2;
		misc.insultList.add("Rattle your bones somewhere else!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "voltican";
		misc.namePlural = "volticans";
		misc.swears.add("sparky");
		misc.aimMod = .9;
		misc.damMod = 1.2;
		misc.dodgeMod = 1.2;
		misc.hpMod = .9;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = .2;
		misc.insultList.add("Oh good, I needed a firestarter!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 2;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "fugue";
		misc.namePlural = "fugues";
		misc.swears.add("reclaimer");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = .2;
		misc.insultList.add("You won't be reclaiming this!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 1;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "mermaid";
		misc.namePlural = "mermaids";
		misc.swears.add("fish");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = 1.2;
		misc.hpMod = 1;
		misc.speedMod = 1.01;
		misc.tradeMod = .9;
		misc.rarity = .7;
		misc.insultList.add("Go, swim away!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 1;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		
		
		//////beasts
		misc = new Race();
		misc.name = "wolf";
		misc.namePlural = "wolves";
		misc.swears.add("dog");
		misc.aimMod = 1.1;
		misc.damMod = .9;
		misc.dodgeMod = 1.3;
		misc.hpMod = .9;
		misc.speedMod = 1.2;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you mutt!");
		misc.insultList.add("Die, dog!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "open mimic";
		misc.namePlural = "open mimics";
		misc.swears.add("box");
		misc.aimMod = 1.2;
		misc.damMod = 1;
		misc.dodgeMod = .8;
		misc.hpMod = 1.6;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, box!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.OPEN_MIMIC;
		raceList.add(misc);
		
		misc = new Race();
		misc.name = "hiding mimic";
		misc.namePlural = "hiding mimics";
		misc.swears.add("box");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = .6;
		misc.hpMod = 1.6;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, box!");
		misc.baseMap = "nada";
		misc.raceMaps.add("nuthin");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.MIMIC;
		raceList.add(misc);
	}
	
	public static Race randRace(Race.RaceType type) {
		ArrayList<Race> copyList = new ArrayList<Race>();
		ArrayList<Race> copyList2 = new ArrayList<Race>();
		for (Race mat: raceList){
			if (type == mat.racialType) {
			copyList.add(mat);}
		}
		double totalRarity = 0;
		Race mat;
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
	
	public static Race getRace(String string) {
		for (Race m: raceList) {
			if (m.name.equals(string)) {
				return m;
			}
		}
		
		return null;
	}
	
	public static Person makeWolf(int level) {
		extra.printMode = true;
		Person w = new Person(level,true, Race.RaceType.BEAST,MaterialFactory.getMat("flesh"));
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),"generic teeth"));
		w.getBag().swapRace(RaceFactory.getRace("wolf"));
		extra.printMode = false;
		return w;
	}

	public static Object makeMimic(int level) {
		extra.printMode = true;
		Person w = new Person(level,true, Race.RaceType.BEAST,MaterialFactory.getMat("wood"));
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),"generic teeth"));
		w.getBag().swapArmorSlot(new Armor(level,0,MaterialFactory.getMat("flesh")),0);
		w.getBag().swapRace(RaceFactory.getRace("hiding mimic"));
		extra.printMode = false;
		return w;
	}
}
