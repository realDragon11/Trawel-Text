package trawel.personal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import trawel.Effect;
import trawel.extra;
import trawel.randomLists;
import trawel.battle.attacks.TargetFactory;
import trawel.factions.Faction;
import trawel.factions.HostileTask;
import trawel.personal.Person.AIJob;
import trawel.personal.Person.PersonType;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.item.body.Race;
import trawel.personal.item.body.SoundBox;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.DrawBane.DrawList;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;

@SuppressWarnings("deprecation")
public class RaceFactory {
	public static Race misc = new Race(RaceID.EMPTY);
	public static List<Race> raceList = new ArrayList<Race>();
	public static Map<String,Race> raceMap = new HashMap<String,Race>();
	
	public static float audioSteps(int steps) {
		/*if (steps < 0) {
			return (float) (1.0f-(Math.log(Math.abs(steps*10))*0.004f));
		}*/
		//return 1.0f+(steps);
		if (steps < 0) {
			return (extra.lerp(1f,0.75f,(-steps)/10f));
		}
		return (extra.lerp(1f,1.5f,(steps)/10f));
	}
	
	public enum RaceID {
		EMPTY(null,null,null,null),
		HUMAN("human","human","humans","human"),
		ORC("orc","orc","orcs","orcish"),
		LIZARDFOLK("","lizardfolk","lizardfolk","lizardfolkian"),
		HIGH_ELF("elf","high elf","high elves","high-elvy"),
		TREE_ELF("elf","tree elf","tree elves","treeish"),
		WOLF_ANTHRO("","wolf bound","wolf bound","wolvian"),
		TURTLE_ANTHRO("","sheller","shelled","shell"),
		CAT_ANTHRO("cat_kin","cat kin","cat kin","catish"),
		SLUG_ANTHRO("","slug","slugs","sluggy"),
		TENDERHEART("","tenderheart","tenderhearts","tender"),
		SKELETON_NON_BEASTLY("skeleton","skeleton","skeletons","skeletal"),
		ZAP_PEOPLE("","voltican","volticans","electric"),
		FUGUE("","fugue","fugues","forgotten"),
		FISHFOLK("mermaid","mermaid","mermaids","oceanic"),
		B_WOLF("wolf","wolf","wolves",null),
		B_MIMIC_OPEN("open_mimic","mimic","mimics",null),
		B_MIMIC_CLOSED("hiding_mimic","hiding mimic","hiding mimics",null),
		B_REAVER_TALL("","reaver","reavers",null),
		B_REAVER_SHORT("","crouching reaver","crouching reavers",null),
		B_ENT("","ent","ent",null),
		B_BEAR("bear","bear","bears",null),
		B_BAT("bat","bat","bats",null),
		B_FLESH_GOLEM("flesh_golem","flesh golem","flesh golems",null),
		B_UNICORN("","unicorn","unicorns",null),
		B_HARPY("harpy","harpy","harpies",null),
		B_DRUDGER_STOCK("","drudger","stock-drudgers",null),
		B_DRUDGER_TITAN("","drudger titan","titan-drudgers",null),
		
		
		;
		public final String name, namePlural, adjective, map;
		
		RaceID(String map, String name,String namePlural, String adjective){
			this.name = name;
			this.namePlural = namePlural;
			this.adjective = adjective;
			this.map = map;
		}
	}
	
	public enum RaceClass{
		HUMAN_LIKE(LegacyType.HUMAN), ELF(LegacyType.ORC), ANTHRO_FUR(LegacyType.CAT),
		ANTHRO_REPTILE(LegacyType.MERMAID), SKELETON(LegacyType.SKELETON),
		OTHER(LegacyType.NONE),VARIES(LegacyType.VARIES), GOLEM(LegacyType.GOLEM),
		WOLF(LegacyType.WOLF), BEAR(LegacyType.BEAR), UNDONE_BEAST(LegacyType.WOLF);
		private LegacyType ltype;
		RaceClass(LegacyType _ltype) {
			ltype = _ltype;
		}
		
		public LegacyType getLegacy() {
			return ltype;
		}
	}
	public enum LegacyType{
		HUMAN("human",3,"human"), ORC("orc",0,null), MERMAID("mermaid",0,null), CAT("cat_kin",5,"cat"),
		WOLF("wolf",5,"wolf"), BEAR("bear",0,null), NONE(null,0,null),
		SKELETON("skeleton",0, null), VARIES(null,5,"mimic"), GOLEM("flesh_golem",0,"flesh_golem");
		private String spritename;
		private String mapname;
		private int maps;
		private LegacyType(String spritename, int maps, String mapname) {
			this.mapname = mapname;
			this.maps = maps;
			this.mapname = mapname;
		}
		
		public String getSpriteName(RaceID id) {
			if (spritename != null) {
				return spritename;
			}
			if (this != LegacyType.VARIES) {
				return "";
			}
			switch (id) {
			case B_MIMIC_OPEN: case B_REAVER_SHORT:
				return "open-mimic";
			case B_MIMIC_CLOSED: case B_REAVER_TALL:
				return "hiding-mimic";
			}
			
			return null;
		}
		public String getMapName(RaceID id) {
			if (mapname != null) {
				return mapname;
			}
			return "";
		}
		/**
		 * use to convert a new number to a legacy map number
		 * <br>
		 * so you don't have to store a legacy number, just have a number you associate with it dervivable from the modern data
		 */
		public int getMap(int offset) {
			if (maps == 0) {
				return 0;
			}
			return offset%maps;
		}
		
	}
	
	public enum CultType{
		BLOOD;
	}
	
	public RaceFactory() {
		misc = new Race(RaceID.HUMAN);
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
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.HUMAN_LIKE;
		raceList.add(misc);
		
		misc = new Race(RaceID.ORC);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 0;
		misc.defPower = 2;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-10);
		misc.maxPitch = audioSteps(0);
		misc.raceClass = RaceClass.ELF;
		raceList.add(misc);
		
		misc = new Race(RaceID.LIZARDFOLK);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 1;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		raceList.add(misc);
		
		misc = new Race(RaceID.HIGH_ELF);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 2;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-3);
		misc.maxPitch = audioSteps(7);
		misc.raceClass = RaceClass.ELF;
		raceList.add(misc);
		
		misc = new Race(RaceID.TREE_ELF);
		misc.swears.add("tree-hugger");
		misc.swears.add("pointy-ear");
		misc.swears.add("hippie");
		misc.swears.add("low-elf");
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
		misc.raceMaps.add("-1");
		misc.magicPower = 1;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-3);
		misc.maxPitch = audioSteps(7);
		misc.raceClass = RaceClass.ELF;
		raceList.add(misc);
		
		misc = new Race(RaceID.WOLF_ANTHRO);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 0;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_FUR;
		raceList.add(misc);
		
		misc = new Race(RaceID.TURTLE_ANTHRO);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 1;
		misc.defPower = 3;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-8);
		misc.maxPitch = audioSteps(2);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		raceList.add(misc);
		
		misc = new Race(RaceID.CAT_ANTHRO);//also maybe make nekos for funny reasons
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
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(2);
		misc.maxPitch = audioSteps(8);
		misc.raceClass = RaceClass.ANTHRO_FUR;
		raceList.add(misc);
		
		misc = new Race(RaceID.SLUG_ANTHRO);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 2;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-10);
		misc.maxPitch = audioSteps(0);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		raceList.add(misc);
		
		misc = new Race(RaceID.TENDERHEART);
		misc.swears.add("exposed");
		misc.aimMod = 1;
		misc.damMod = .7;
		misc.dodgeMod = 1.3;
		misc.hpMod = 1.1;
		misc.speedMod = 1;
		misc.tradeMod = 1.2;
		misc.rarity = .2;
		misc.insultList.add("Look, your heart's hanging out!");
		misc.baseMap = "nada";
		misc.raceMaps.add("-1");
		misc.magicPower = 2;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ELF;
		raceList.add(misc);
		
		misc = new Race(RaceID.SKELETON_NON_BEASTLY);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = false;
		misc.voice = SoundBox.Voice.NONE;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.SKELETON;
		raceList.add(misc);
		
		misc = new Race(RaceID.ZAP_PEOPLE);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 2;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = false;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(0);
		misc.maxPitch = audioSteps(10);
		misc.raceClass = RaceClass.HUMAN_LIKE;
		raceList.add(misc);
		
		misc = new Race(RaceID.FUGUE);
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
		misc.raceMaps.add("-1");
		misc.magicPower = 1;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-10);
		misc.maxPitch = audioSteps(0);
		misc.raceClass = RaceClass.HUMAN_LIKE;
		raceList.add(misc);
		
		misc = new Race(RaceID.FISHFOLK);
		misc.swears.add("fish");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = 1.2;
		misc.hpMod = 1;
		misc.speedMod = 1.01;
		misc.tradeMod = .9;
		misc.rarity = .7;
		misc.insultList.add("Go, swim away!");
		misc.baseMap = "mermaid";
		misc.raceMaps.add("-1");
		misc.magicPower = 1;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.HUMANOID;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		raceList.add(misc);
		
		
		
		//////beasts
		misc = new Race(RaceID.B_WOLF);
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
		misc.baseMap = "wolf";
		misc.raceMaps.add("0");
		misc.raceMaps.add("1");
		misc.raceMaps.add("2");
		misc.raceMaps.add("3");
		misc.raceMaps.add("4");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.QUAD;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.WOLF;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.WOLF;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_MIMIC_OPEN);
		misc.swears.add("box");
		misc.aimMod = 1.2;
		misc.damMod = 1;
		misc.dodgeMod = .8;
		misc.hpMod = 1.6;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, box!");
		misc.baseMap = "mimic";
		misc.raceMaps.add("0");
		misc.raceMaps.add("1");
		misc.raceMaps.add("2");
		misc.raceMaps.add("3");
		misc.raceMaps.add("4");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.OPEN_MIMIC;
		misc.emitsBlood = true;
		misc.raceClass = RaceClass.VARIES;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_MIMIC_CLOSED);
		misc.swears.add("box");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = .6;
		misc.hpMod = 1.6;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, box!");
		misc.baseMap = "chest";
		misc.raceMaps.add("0");
		misc.raceMaps.add("1");
		misc.raceMaps.add("2");
		misc.raceMaps.add("3");
		misc.raceMaps.add("4");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.MIMIC;
		misc.emitsBlood = false;
		misc.raceClass = RaceClass.VARIES;
		raceList.add(misc);
		
		
		misc = new Race(RaceID.B_REAVER_TALL);
		misc.swears.add("scum");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = .8;
		misc.hpMod = 1.2;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you scum!");
		misc.baseMap = "";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.S_REAVER;
		misc.emitsBlood = true;
		misc.raceClass = RaceClass.VARIES;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_REAVER_SHORT);
		misc.swears.add("scum");
		misc.aimMod = 1.3;
		misc.damMod = 1.1;
		misc.dodgeMod = .5;
		misc.hpMod = 1.2;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you scum!");
		misc.baseMap = "";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.C_REAVER;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.MONSTER1;
		misc.raceClass = RaceClass.VARIES;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_ENT);
		misc.swears.add("tree");
		misc.aimMod = 1;
		misc.damMod = .9;
		misc.dodgeMod = .4;
		misc.hpMod = 2;
		misc.speedMod = .9;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, tree!");
		misc.baseMap = "ent";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.STATUE;
		misc.emitsBlood = false;
		misc.voice = SoundBox.Voice.ENT;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.UNDONE_BEAST;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_BEAR);
		misc.swears.add("beast");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = .6;
		misc.hpMod = 2;
		misc.speedMod = .7;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you bear!");
		misc.insultList.add("Die, bear!");
		misc.baseMap = "bear";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.QUAD;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.BEAR;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.BEAR;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_BAT);
		misc.swears.add("bat");
		misc.aimMod = 1;
		misc.damMod = .8;
		misc.dodgeMod = 1.4;
		misc.hpMod = .7;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you bat!");
		misc.insultList.add("Die, bat!");
		misc.baseMap = "bat";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.FLY;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.BAT;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.UNDONE_BEAST;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_FLESH_GOLEM);
		misc.swears.add("meatsack");
		misc.aimMod = .9;
		misc.damMod = 1.1;
		misc.dodgeMod = .6;
		misc.hpMod = 1.4;
		misc.speedMod = .9;
		misc.tradeMod = .9;
		misc.rarity = 1;
		misc.insultList.add("Die, meatsack!");
		misc.baseMap = "flesh_golem";
		misc.raceMaps.add("0");;
		misc.magicPower = 0;
		misc.defPower = 1;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.F_GOLEM;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.GOLEM;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_UNICORN);
		misc.swears.add("horsey");
		misc.aimMod = 1.3;
		misc.damMod = .9;
		misc.dodgeMod = .8;
		misc.hpMod = 1.5;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, stupid horse!");
		misc.baseMap = "unicorn";
		misc.raceMaps.add("0");
		misc.magicPower = 3;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.QUAD;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.WOLF;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.UNDONE_BEAST;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_HARPY);
		misc.swears.add("harpy");
		misc.aimMod = 1;
		misc.damMod = 1.1;
		misc.dodgeMod = 1.25;
		misc.hpMod = .9;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you feathery fiend!");
		misc.baseMap = "harpy";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.FLY;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.HARPY;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.UNDONE_BEAST;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_DRUDGER_STOCK);
		misc.swears.add("drowner");
		misc.swears.add("pond scum");
		misc.aimMod = 1;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1;
		misc.speedMod = 1;
		misc.tradeMod = .5;
		misc.rarity = 1;
		misc.insultList.add("Drudger!");
		misc.insultList.add("Die pond scum!");
		misc.baseMap = "flesh_golem";
		misc.raceMaps.add("0");;
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.F_GOLEM;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_DRUDGER_TITAN);
		misc.swears.add("drowner");
		misc.aimMod = 1;
		misc.damMod = 1.2;
		misc.dodgeMod = .4;
		misc.hpMod = 2;
		misc.speedMod = .5;
		misc.tradeMod = .5;
		misc.rarity = 1;
		misc.insultList.add("Drudger!");
		misc.baseMap = "flesh_golem";
		misc.raceMaps.add("0");;
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.F_GOLEM;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		raceList.add(misc);
		
		for (Race r: raceList) {
			raceMap.put(r.raceID().name(), r);
		}
		
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
			mat = extra.randList(copyList);
			copyList2.add(mat);
			totalRarity += mat.rarity;
			copyList.remove(mat);
		}while(!copyList.isEmpty());
		totalRarity*= extra.getRand().nextDouble();
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
	
	public static Race getRace(RaceID id) {
		return raceMap.get(id.name());
	}
	
	public static Person makeLootBody(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		extra.popPrintStack();
		return w;
	}
	
	public static Person makeGeneric(int level) {
		extra.offPrintStack();
		Person p = new Person(level);
		p.hTask = HostileTask.DUEL;
		//p.updateSkills();
		extra.popPrintStack();
		return p;
	}
	
	public static Person makePlayerValid() {
		extra.offPrintStack();
		Person p = new Person(1,false,Race.RaceType.HUMANOID,null,Person.RaceFlag.NONE,true);
		p.hTask = HostileTask.DUEL;
		//p.updateSkills();
		extra.popPrintStack();
		return p;
	}
	
	public static Person makeOld(int level) {
		Person p = new Person(level);
		p.setPersonType(PersonType.GRIZZLED);
		p.hTask = HostileTask.DUEL;
		if (extra.chanceIn(1,5)) {
			p.getBag().getDrawBanes().add(DrawBane.KNOW_FRAG);
		}
		//p.updateSkills();
		return p;
	}
	
	public static Person makeQuarterMaster(int level) {
		Person p = new Person(level);
		p.setPersonType(PersonType.GRIZZLED);
		p.hTask = HostileTask.RICH;
		//p.updateSkills();
		return p;
	}
	
	public static Person makeWolf(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_WOLF, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		if (extra.chanceIn(1,5)) {
			w.getBag().getDrawBanes().add(DrawBane.MEAT);
		}
		extra.popPrintStack();
		w.setFirstName(randomLists.randomWolfName());
		w.hTask = HostileTask.ANIMAL;
		//w.updateSkills();
		return w;
	}
	
	public static Person makeAlphaWolf(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_WOLF, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		if (extra.chanceIn(4,5)) {
			w.getBag().getDrawBanes().add(DrawBane.MEAT);
		}
		extra.popPrintStack();
		w.setFirstName(randomLists.randomWolfName());
		w.setTitle(randomLists.randomAlphaName());
		w.hTask = HostileTask.ANIMAL;
		//w.updateSkills();
		return w;
	}

	public static Person makeMimic(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_MIMIC_OPEN, MaterialFactory.getMat("wood"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		w.getBag().swapArmorSlot(new Armor(level,0,MaterialFactory.getMat("flesh")),0);
		//w.getBag().swapRace(RaceFactory.getRace("hiding-mimic"));
		w.setPerk(Perk.RACIAL_SHIFTS);
		if (extra.chanceIn(1,3)) {
			w.getBag().getDrawBanes().add(DrawBane.MIMIC_GUTS);
		}
		w.setFirstName(randomLists.randomFirstName());
		
		extra.popPrintStack();
		w.hTask = HostileTask.MONSTER;
		//w.updateSkills();
		return w;
	}
	
	public static Person makeStatue(int level) {
		extra.offPrintStack();
		Person w = new Person(level,true, Race.RaceType.HUMANOID,MaterialFactory.getMat("flesh"),Person.RaceFlag.CRACKS,false);
		//w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),"generic teeth"));
		if (extra.chanceIn(1,2)) {
			w.getBag().getDrawBanes().add(DrawBane.CEON_STONE);
		}
		extra.popPrintStack();
		//w.targetOverride = TargetFactory.TargetType.STATUE;
		w.hTask = HostileTask.GUARD_DUNGEON;
		//w.updateSkills();
		return w;
	}
	
	public static Person makeFellReaver(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_REAVER_TALL, MaterialFactory.getMat("flesh"), false);//DOLATER change flesh type
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("flesh"),WeaponType.REAVER_STANDING));
		w.setPerk(Perk.RACIAL_SHIFTS);
		w.setFirstName(randomLists.randomFirstName());
		extra.popPrintStack();
		w.hTask = HostileTask.MONSTER;
		//w.updateSkills();
		return w;
	}

	public static Person getShaman(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.getBag().getDrawBanes().add(DrawBane.PROTECTIVE_WARD);
		extra.popPrintStack();
		w.hTask = HostileTask.DUEL;
		//w.updateSkills();
		return w;
	}
	public static Person makeEnt(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_ENT, MaterialFactory.getMat("wood"), false);
		//Person w = new Person(level,true, Race.RaceType.BEAST,MaterialFactory.getMat("wood"),Person.RaceFlag.NONE,false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("wood"),WeaponType.BRANCHES));
		w.getBag().getDrawBanes().add(DrawBane.ENT_CORE);
		//w.getBag().swapRace(RaceFactory.getRace("ent"));
		extra.popPrintStack();
		w.setFirstName(randomLists.randomEntName());
		w.setTitle("");
		w.hTask = HostileTask.ANIMAL;
		w.updateSkills();
		return w;
	}
	
	public static Person makeVampire(int level) {
		extra.offPrintStack();
		Person w = new Person(level,true, Race.RaceType.HUMANOID,MaterialFactory.getMat("flesh"),Person.RaceFlag.UNDEAD,false);
		w.setScar(biteFor(w.getBag().getRace().raceID()));
		w.getBag().getDrawBanes().add(DrawBane.GRAVE_DUST);
		if (extra.chanceIn(1,10)) {
			w.getBag().getDrawBanes().add(DrawBane.BLOOD);
		}else {
			if (extra.chanceIn(1,6)) {
				w.getBag().getDrawBanes().add(DrawBane.GRAVE_DIRT);
			}
		}
		extra.popPrintStack();
		w.hTask = HostileTask.MONSTER;
		return w;
	}
	
	public static Person makeBear(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_BEAR, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.CLAWS_TEETH_GENERIC));
		w.getBag().getDrawBanes().add(DrawBane.MEAT);
		extra.popPrintStack();
		w.setFirstName(randomLists.randomBearName());
		w.setTitle("");
		w.hTask = HostileTask.ANIMAL;
		//w.updateSkills();
		return w;
	}
	
	public static Person makeBat(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_BAT, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		w.getBag().getDrawBanes().add(DrawBane.BAT_WING);
		if (extra.chanceIn(1,7)) {
			w.getBag().getDrawBanes().add(DrawBane.MEAT);
		}
		extra.popPrintStack();
		w.setFirstName(randomLists.randomBatName());
		w.setTitle("");
		w.hTask = HostileTask.ANIMAL;
		return w;
	}
	
	public static Person getFleshGolem(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_FLESH_GOLEM, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("flesh"),WeaponType.GENERIC_FISTS));
		w.getBag().getDrawBanes().add(DrawBane.BEATING_HEART);
		extra.popPrintStack();
		w.setFirstName(randomLists.randomFirstName());
		//w.targetOverride = TargetFactory.TargetType.HUMANOID;
		w.hTask = HostileTask.MONSTER;
		//w.updateSkills();
		return w;
	}
	
	public static Person makeUnicorn(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_UNICORN, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.UNICORN_HORN));
		if (extra.chanceIn(1,3)) {
			w.getBag().getDrawBanes().add(DrawBane.UNICORN_HORN);
		}
		w.setFirstName(randomLists.randomFirstName());
		extra.popPrintStack();
		w.hTask = HostileTask.ANIMAL;
		//w.updateSkills();
		return w;
	}
	
	public static Person makeHarpy(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_HARPY, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TALONS_GENERIC));
		if (extra.chanceIn(1,6)) {
			w.getBag().getDrawBanes().add(DrawBane.MEAT);
		}
		if (extra.chanceIn(1,50)) {
			w.getBag().getDrawBanes().add(DrawBane.GOLD);
		}
		if (extra.chanceIn(1,20)) {
			w.getBag().getDrawBanes().add(DrawBane.SILVER);
		}
		w.setFirstName(randomLists.randomFirstName());
		extra.popPrintStack();
		w.hTask = HostileTask.MONSTER;
		//w.updateSkills();
		return w;
	}
	
	public static Person makeDrudgerStock(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_DRUDGER_STOCK, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat(extra.choose("rusty iron","rusty iron","iron")),WeaponType.FISH_SPEAR));
		for (byte i=0;i<5;i++) {
			if (extra.chanceIn(1,8)) {
				w.getBag().swapArmorSlot(new Armor(level,i,MaterialFactory.getMat("rusty iron"),null),i);
			}
		}
		if (extra.chanceIn(1,6)) {
			w.getBag().getDrawBanes().add(DrawBane.MEAT);
		}
		extra.popPrintStack();
		w.setFirstName(randomLists.randomWaterName());
		w.hTask = HostileTask.MONSTER;
		//w.updateSkills();
		return w;
	}
	public static Person makeDrudgerTitan(int level) {
		extra.offPrintStack();
		Person w = Person.animal(level, RaceID.B_DRUDGER_TITAN, MaterialFactory.getMat("flesh"), false);
		w.getBag().swapWeapon(new Weapon(level,MaterialFactory.getMat(extra.choose("rusty iron","iron")),WeaponType.FISH_ANCHOR));
		w.getBag().swapArmorSlot(new Armor(level,(byte)2,MaterialFactory.getMat("rusty iron"),null),2);
		w.getBag().getDrawBanes().add(DrawBane.MEAT);
		if (extra.chanceIn(1,2)) {
			w.getBag().getDrawBanes().add(DrawBane.MEAT);
		}
		extra.popPrintStack();
		w.setFirstName(randomLists.randomWaterName());
		w.setTitle("the "+randomLists.randomLargeName());
		w.hTask = HostileTask.MONSTER;
		//w.updateSkills();
		return w;
	}
	
	public static Person getMugger(int level) {
		extra.offPrintStack();
		Person w;
		if (extra.chanceIn(1,4)) {
			w = new Person(level,AIJob.ROGUE);
		}else {
			w = new Person(level);
		}
		w.facRep.addFactionRep(Faction.ROGUE,extra.randRange(10,20)*level, 0);
		w.facRep.addFactionRep(Faction.HEROIC,0, 10*level);
		w.hTask = HostileTask.MUG;
		if (extra.chanceIn(1,100)) {
			w.getBag().getDrawBanes().add(DrawBane.GOLD);
		}
		if (extra.chanceIn(1,50)) {
			w.getBag().getDrawBanes().add(DrawBane.SILVER);
		}
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	
	public static Person makeMuggerWithTitle(int level) {
		Person w = getMugger(level);
		w.setTitle("the " + extra.capFirst(randomLists.randomMuggerName()));
		return w;
	}
	
	
	public static Person getDueler(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.facRep.addFactionRep(Faction.DUEL,extra.randRange(10,20)*level, 0);
		w.hTask = HostileTask.DUEL;
		extra.popPrintStack();
		return w;
	}
	
	public static Person makeDuelerWithTitle(int level) {
		Person w = getDueler(level);
		w.setTitle("the " + extra.capFirst(randomLists.randomWarrior()));
		return w;
	}
	
	public static Person makeCultistLeader(int level, CultType ct) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.GUARD_DUNGEON;
		switch (ct) {
		case BLOOD:
			List<DrawBane> list = w.getBag().getDrawBanes();
			if (extra.chanceIn(1,3)) {
				list.add(DrawBane.BEATING_HEART);
			}else {
				list.add(DrawBane.SINEW);
			}
			list.add(DrawBane.BLOOD);
			w.setTitle(extra.choose("the Blood Queen","Chosen by The Blood","Blood Champion"));
			w.setPerk(Perk.CULT_LEADER_BLOOD);
			break;		
		}
		extra.popPrintStack();
		return w;
	}
	public static Person makeCultist(int level, CultType ct) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.GUARD_DUNGEON;
		switch (ct) {
		case BLOOD:
			List<DrawBane> list = w.getBag().getDrawBanes();
			if (extra.chanceIn(1,3)) {
				list.add(DrawBane.SINEW);
			}else {
				list.add(DrawBane.BLOOD);
			}
			w.setTitle(extra.choose("Servant of Blood","the Bloodtender","","","Bloodguard"));
			break;		
		}
		extra.popPrintStack();
		return w;
	}
	
	public static Person getRacist(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.RACIST;
		w.setRacism(true);
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	public static Person getPeace(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.PEACE;
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	public static Person getBoss(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.BOSS;
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	
	public static Person getDryad(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.ANIMAL;
		w.facRep.addFactionRep(Faction.FOREST, level*15,0);
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	public static Person getDGuard(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.GUARD_DUNGEON;
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	
	public static Person getLumberjack(int level) {
		extra.offPrintStack();
		Person w = new Person(level,AIJob.LUMBERJACK);
		w.hTask = HostileTask.LUMBER;
		w.facRep.addFactionRep(Faction.FOREST,0,100);
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	public static Person getRich(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.RICH;
		w.facRep.addFactionRep(Faction.MERCHANT,10*level,0);
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	public static Person makeCollector(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.DUEL;
		w.facRep.addFactionRep(Faction.MERCHANT,5*level,0);
		List<DrawBane> dbs = w.getBag().getDrawBanes();
		dbs.add(DrawBane.draw(DrawList.COLLECTOR));
		if (extra.chanceIn(2,3)) {
			dbs.add(DrawBane.draw(DrawList.COLLECTOR));
		}
		if (extra.chanceIn(2,4)) {
			dbs.add(DrawBane.draw(DrawList.COLLECTOR));
		}
		extra.popPrintStack();
		w.setTitle(randomLists.randomCollectorName());
		return w;
	}
	public static Person getCultist(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.GUARD_DUNGEON;
		if (extra.chanceIn(1, 3)) {
			w.getBag().getDrawBanes().add(DrawBane.BLOOD);
		}
		if (extra.chanceIn(1, 3)) {
			w.getBag().getDrawBanes().add(DrawBane.BLOOD);
		}
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	
	public static Person getLawman(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.hTask = HostileTask.LAW;
		w.facRep.addFactionRep(Faction.HEROIC,5*level,0);
		extra.popPrintStack();
		//w.updateSkills();
		return w;
	}
	
	public static Person makeGraverobber(int level) {
		extra.offPrintStack();
		Person w;
		List<DrawBane> list;
		int rarityMult;
		if (extra.chanceIn(3,4)) {
			//comes here often
			w = new Person(level,AIJob.GRAVER);
			list = w.getBag().getDrawBanes();
			w.setPerk(Perk.GRAVEYARD_SIGHT);
			if (extra.chanceIn(1,3)) {
				list.add(DrawBane.SILVER);
			}else {
				list.add(DrawBane.GRAVE_DIRT);
			}
			w.facRep.addFactionRep(Faction.ROGUE,15*level, 0);
			rarityMult = 2;
		}else {
			//more generic robber
			if (extra.chanceIn(1,2)) {
				//higher cut of thief
				w = new Person(level,AIJob.ROGUE);
				w.facRep.addFactionRep(Faction.ROGUE,20*level, 0);
				rarityMult = 3;
			}else {//afraid of graveyard
				w = new Person(level);
				w.setPersonType(PersonType.COWARDLY);
				w.addEffect(Effect.CURSE);
				w.facRep.addFactionRep(Faction.ROGUE,10*level, 0);
				rarityMult = 1;
			}
			list = w.getBag().getDrawBanes();
		}
		w.hTask = HostileTask.MUG;
		w.facRep.addFactionRep(Faction.HEROIC,0, 10*level);
		if (extra.chanceIn(rarityMult,8)) {
			if (extra.chanceIn(rarityMult,20)) {
				list.add(DrawBane.GOLD);
			}else {
				list.add(DrawBane.SILVER);
			}
			
		}
		extra.popPrintStack();
		return w;
	}
	
	public static Person makeGravedigger(int level) {
		extra.offPrintStack();
		Person w = new Person(level,AIJob.GRAVER);
		w.setPerk(Perk.GRAVEYARD_SIGHT);
		w.hTask = HostileTask.PEACE;
		List<DrawBane> list = w.getBag().getDrawBanes();
		list.add(DrawBane.GRAVE_DIRT);
		if (extra.chanceIn(1,8)) {//can afford full protection
			list.add(DrawBane.PROTECTIVE_WARD);
			w.getBag().addGold(level);//extra money
			w.facRep.addFactionRep(Faction.HUNTER,level,0);
			w.facRep.addFactionRep(Faction.MERCHANT,level,0);
			w.getBag().getHand().improveEnchantChance(level);//improve weapon enchant
			w.setTitle(randomLists.randomCollectorName());
		}else {
			if (extra.chanceIn(3,4)) {//undead tool for protection
				if (extra.chanceIn(1,3)) {
					list.add(DrawBane.SILVER);
					list.add(DrawBane.GRAVE_DUST);
					//combatative
					w.addBlood(2);
					w.facRep.addFactionRep(Faction.HUNTER,5*level,0);
					w.facRep.addFactionRep(Faction.HEROIC,2*level,0);
					w.getBag().getHand().transmuteWeapMat(MaterialFactory.getMat("silver"));
					w.hTask = HostileTask.HUNT;
					w.setTitle(randomLists.randomHunterTitle());
				}else {
					list.add(DrawBane.GARLIC);
				}
				if (extra.chanceIn(1,2)) {
					list.add(DrawBane.GARLIC);//chance of more for either outcome
				}
			}else {
				list.add(DrawBane.GRAVE_DIRT);//didn't get any, so just more grave dirt
			}
		}
		extra.popPrintStack();
		return w;
	}
	
	public static Person makeHunter(int level) {
		extra.offPrintStack();
		Person w = new Person(level);
		w.facRep.addFactionRep(Faction.HUNTER,10*level,0);
		w.facRep.addFactionRep(Faction.HEROIC,5*level,0);
		w.facRep.addFactionRep(Faction.MERCHANT,level,0);
		w.hTask = HostileTask.HUNT;
		w.setTitle(randomLists.randomHunterTitle());
		List<DrawBane> list = w.getBag().getDrawBanes();
		switch (extra.getRand().nextInt(6)) {
		case 0: case 1:
			list.add(DrawBane.SILVER);
			list.add(DrawBane.GARLIC);
			w.getBag().getHand().transmuteWeapMat(MaterialFactory.getMat("silver"));
			break;
		case 2: case 3:
			list.add(DrawBane.REPEL);
			break;
		case 4:
			w.addBlood(3);
			w.getBag().getHand().transmuteWeapMat(MaterialFactory.getMat("silver"));
			w.setPerk(Perk.GRAVEYARD_SIGHT);
			list.add(DrawBane.SILVER);
			list.add(DrawBane.GRAVE_DUST);
			break;
		case 5:
			list.add(DrawBane.PROTECTIVE_WARD);
			w.getBag().getHand().improveEnchantChance(level);
			break;
		}
		return w;
	}

	public static String scarFor(RaceID race) {
		switch (race) {
		case HUMAN:
			if (extra.chanceIn(1, 3)) {
				return extra.choose("hscar_1","H_wound1","H_wound2","H_wound3","H_wound4","H_wound5","H_wound6","H_wound7","H_wound8","H_wound9","H_wound10","H_wound11");
			}
			break;
		}
		return "";
	}
	
	public static String biteFor(RaceID race) {
		switch (race) {
		case HUMAN:
				return extra.choose("H_vampbite1","H_vampbite2","H_vampbite3","H_vampbite4","H_vampbite5");
		}
		return "";
	}

	

	
	
}
