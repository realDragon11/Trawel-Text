package trawel.personal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trawel.Effect;
import trawel.extra;
import trawel.randomLists;
import trawel.battle.attacks.TargetFactory;
import trawel.factions.Faction;
import trawel.factions.HostileTask;
import trawel.personal.Person.AIJob;
import trawel.personal.Person.PersonFlag;
import trawel.personal.Person.PersonType;
import trawel.personal.Person.RaceFlag;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Perk;
import trawel.personal.item.body.Race;
import trawel.personal.item.body.SoundBox;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.DrawBane.DrawList;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.quests.CleanseSideQuest;

public class RaceFactory {
	public static List<Race> raceList = new ArrayList<Race>();
	public static Map<String,Race> raceMap = new HashMap<String,Race>();
	private static Map<RaceID,PersonMaker> mookMakerIDMap = new EnumMap<RaceID,PersonMaker>(RaceID.class);
	
	private static EnumMap<RaceID,List<String>> scarMap = new EnumMap<RaceFactory.RaceID, List<String>>(RaceID.class);
	
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
		B_SWARMBAT("bat","bat","bats",null),
		MAJOR_DEMON("","demon","demons","demonic")
		
		
		;
		public final String name, namePlural, adjective, map;
		
		RaceID(String map, String name,String namePlural, String adjective){
			this.name = name;
			this.namePlural = namePlural;
			this.adjective = adjective;
			this.map = map;
		}
	}
	/**
	 * racial class used for racism comparisons now
	 */
	public enum RaceClass{
		HUMAN_LIKE(LegacyType.HUMAN),
		ELF(LegacyType.ORC),
		ORC(LegacyType.ORC),
		ANTHRO_FUR(LegacyType.CAT),
		ANTHRO_REPTILE(LegacyType.MERMAID),
		ANTHRO_EXOTIC(LegacyType.MERMAID),
		SKELETON(LegacyType.SKELETON),
		OTHER(LegacyType.NONE),
		VARIES(LegacyType.VARIES),
		GOLEM(LegacyType.GOLEM),
		WOLF(LegacyType.WOLF),
		BEAR(LegacyType.BEAR),
		UNDONE_BEAST(LegacyType.WOLF),
		DRUDGER(LegacyType.MERMAID)
		,DEMON(LegacyType.GOLEM);
		private LegacyType ltype;
		RaceClass(LegacyType _ltype) {
			ltype = _ltype;
		}
		
		public LegacyType getLegacy() {
			return ltype;
		}
	}
	public enum LegacyType{
		HUMAN("human",2,"human"), ORC("orc",0,null), MERMAID("mermaid",0,null), CAT("cat_kin",4,"cat"),
		WOLF("wolf",4,"wolf"), BEAR("bear",0,null), NONE(null,0,null),
		SKELETON("skeleton",0, null), VARIES(null,4,"mimic"), GOLEM("flesh_golem",0,"flesh_golem");
		private String spritename;
		private String mapname;
		/**
		 * is used to modulo, and is zero indexed how many maps there are
		 */
		private int maps;
		private LegacyType(String spritename, int maps, String mapname) {
			this.spritename = spritename;
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
				return "open_mimic";
			case B_MIMIC_CLOSED: case B_REAVER_TALL:
				return "hiding_mimic";
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

		public String friendlyName() {
			if (spritename != null) {
				return spritename;
			}
			return mapname;
		}
		
	}
	
	public enum CultType{
		BLOOD, SKY;
	}
	
	public RaceFactory() {
		//FIXME: have a larger setup where it allocates ranges to added sprites and they're universal, not per race
		//will have to include extra space inbetween each for forward compat, but because we're working with int size
		//100 or even 1000 per is fairly reasonable to allocate
		String[] scarArr = new String[]{"hscar_1","H_wound1","H_wound2","H_wound3","H_wound4","H_wound5","H_wound6","H_wound7","H_wound8","H_wound9","H_wound10","H_wound11"};
		scarMap.put(RaceID.HUMAN, Arrays.asList(scarArr));
		
		Race misc;
		/**
		 * lore implication that a lot of the past empires were human ones
		 * and they really resent the rulers of those fallen empires
		 * so being compared to their past is degrading
		 */
		misc = new Race(RaceID.HUMAN);
		misc.swears.add("thinskin");
		misc.swears.add("dominator");
		misc.swears.add("inquisitor");
		misc.aimMod = 1.05;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1;
		misc.speedMod = 1.05;
		//+.1 total
		misc.rarity = 2;
		misc.insultList.add("Your skin is as thin as your brain!");
		misc.insultList.add("All your empires fell, and so shall you!");
		misc.insultList.add("The sins of your past are unforgivable!");
		misc.baseMap = "human";
		misc.raceMaps.add("0");
		misc.raceMaps.add("1");
		misc.raceMaps.add("2");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.HUMAN_LIKE;
		raceList.add(misc);
		
		/*lore implication being their tribes banded together to forge an empire
		and then just ended up being tossed around by despots and empires as cannon fodder in their own wars
		the orcs are respected for their physical strength but their ambition is insulted
		*/
		misc = new Race(RaceID.ORC);
		misc.swears.add("greenskin");
		misc.swears.add("blockhead");//idk what insults do people use that could be fantasy slurs
		misc.swears.add("grunt");
		misc.aimMod = .95;
		misc.damMod = 1.10;
		misc.dodgeMod = 1.05;
		misc.hpMod = 1.05;
		misc.speedMod = .95;
		//+.1 total
		misc.rarity = 1;
		misc.insultList.add("They say 'as dumb as an orc' for a reason!");
		misc.insultList.add("Your kind never delivered on those empires!");
		misc.insultList.add("How does it feel to be living magefire fodder?!");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-10);
		misc.maxPitch = audioSteps(0);
		misc.raceClass = RaceClass.ORC;
		misc.archetype = Archetype.GLADIATOR;
		raceList.add(misc);
		
		misc = new Race(RaceID.LIZARDFOLK);
		misc.swears.add("lizard");
		misc.swears.add("reptile");
		misc.swears.add("walking belt");
		misc.aimMod = 1.1;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1;
		misc.speedMod = 1;
		//+.1 total
		misc.rarity = 1;
		misc.insultList.add("What do you want, lizard?");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		raceList.add(misc);
		
		/**
		 * lore implication that high elves don't really do much despite their talk
		 * and that leads to much derision
		 */
		misc = new Race(RaceID.HIGH_ELF);
		misc.swears.add("pointy-ear");
		misc.swears.add("pretentious elf");
		misc.aimMod = 1.10;
		misc.damMod = 1;
		misc.dodgeMod = 1.10;
		misc.hpMod = .85;
		misc.speedMod = 1.05;
		//+.1 total
		misc.rarity = .5;
		misc.insultList.add("Have you heard of the 'high' elves?");//Originally a TES joke that I don't understand
		misc.insultList.add("What are you going to do, stab me with your ears?");
		misc.insultList.add("Have you heard of the 'high' elves? I haven't!");
		misc.insultList.add("Have you heard of the 'high' elves? You better stay away from the alchemists!");//drug
		misc.insultList.add("What are you going to do, talk me to death?");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-3);
		misc.maxPitch = audioSteps(7);
		misc.raceClass = RaceClass.ELF;
		raceList.add(misc);
		
		/**
		 * they're close to the standard 'tree loving elf' but a lot less tree loving
		 * and more just have the magic to shape wood
		 * most aren't druidic but their smaller settlements tend to just be treehouses,
		 * so the threat of burning them down is very real to them
		 */
		misc = new Race(RaceID.TREE_ELF);
		misc.swears.add("tree-hugger");
		misc.swears.add("pointy-ear");
		misc.swears.add("hippie");
		misc.swears.add("low-elf");
		misc.aimMod = 1.05;
		misc.damMod = .95;
		misc.dodgeMod = 1.1;
		misc.hpMod = .95;
		misc.speedMod = 1.05;
		//+.1 total
		misc.rarity = .5;
		misc.insultList.add("Go, hug a tree!");
		misc.insultList.add("Chop and burn!");
		misc.insultList.add("I'll set fire to your people!");
		//generic elf insult
		misc.insultList.add("What are you going to do, stab me with your ears?");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-3);
		misc.maxPitch = audioSteps(7);
		misc.raceClass = RaceClass.ELF;
		raceList.add(misc);
		
		misc = new Race(RaceID.WOLF_ANTHRO);
		misc.swears.add("dog");
		misc.swears.add("mutt");
		misc.swears.add("howler");
		misc.aimMod = 1;
		misc.damMod = 1.1;
		misc.dodgeMod = 1.05;
		misc.hpMod = .95;
		misc.speedMod = 1;
		//+.1 total
		misc.rarity = .5;
		misc.insultList.add("Go fetch, dog!");
		misc.insultList.add("Howl somewhere else, dog!");
		//needs cat maps for now
		misc.baseMap = "cat";
		misc.raceMaps.add("0");
		misc.raceMaps.add("1");
		misc.raceMaps.add("2");
		misc.raceMaps.add("3");
		misc.raceMaps.add("4");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_FUR;
		raceList.add(misc);
		
		misc = new Race(RaceID.TURTLE_ANTHRO);
		misc.swears.add("shelly");
		misc.swears.add("slowpoke");
		misc.aimMod = 1.1;
		misc.damMod = 1;
		misc.dodgeMod = 1;
		misc.hpMod = 1.2;
		misc.speedMod = .8;
		//+.1 total
		misc.rarity = .25;
		misc.insultList.add("I'll rip that shell right off!");
		misc.insultList.add("Maybe you'll react to your death in a few days!");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-8);
		misc.maxPitch = audioSteps(2);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		misc.archetype = Archetype.ARMORMASTER;
		raceList.add(misc);
		
		misc = new Race(RaceID.CAT_ANTHRO);//also maybe make nekos for funny reasons
		misc.swears.add("fleabag");
		misc.swears.add("hairball");
		misc.swears.add("kitten");
		misc.swears.add("curious cat");
		misc.aimMod = 1.1;
		misc.damMod = .95;
		misc.dodgeMod = 1.1;
		misc.hpMod = .95;
		misc.speedMod = 1;
		//+.1 total
		misc.rarity = 1;
		misc.insultList.add("Stay away, I don't want fleas!");
		misc.insultList.add("Go meow somewhere else!");
		misc.insultList.add("You'd make a nice rug, cat.");
		misc.insultList.add("Curiosity isn't all that's gonna kill you, cat!");
		misc.baseMap = "cat";
		misc.raceMaps.add("0");
		misc.raceMaps.add("1");
		misc.raceMaps.add("2");
		misc.raceMaps.add("3");
		misc.raceMaps.add("4");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(2);
		misc.maxPitch = audioSteps(8);
		misc.raceClass = RaceClass.ANTHRO_FUR;
		misc.archetype = Archetype.ACRO_DAREDEVIL;//temp
		raceList.add(misc);
		
		misc = new Race(RaceID.SLUG_ANTHRO);
		misc.swears.add("slowpoke");
		misc.swears.add("sluggy");
		misc.aimMod = 1.2;
		misc.damMod = 1.1;
		misc.dodgeMod = .9;
		misc.hpMod = 1;
		misc.speedMod = .9;
		//+.1 total
		misc.rarity = .2;
		misc.insultList.add("Clean up after your trail!");
		misc.insultList.add("I bet you taste good with salt!");
		misc.insultList.add("Your herbs will make fine seasoning for you!");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-10);
		misc.maxPitch = audioSteps(0);
		misc.raceClass = RaceClass.ANTHRO_EXOTIC;
		misc.archetype = Archetype.VIRAGO;
		raceList.add(misc);
		
		/**
		 * people with exposed hearts and with semi-plant based flesh
		 */
		misc = new Race(RaceID.TENDERHEART);
		misc.swears.add("exposed");
		misc.swears.add("tenderized");
		misc.swears.add("kindling");
		misc.aimMod = 1;
		misc.damMod = .8;
		misc.dodgeMod = 1.15;
		misc.hpMod = 1.15;
		misc.speedMod = 1;
		//+.1 total
		misc.rarity = .2;
		misc.insultList.add("Look, your heart's hanging out!");
		misc.insultList.add("Time to tenderize that heart!");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_EXOTIC;
		raceList.add(misc);
		
		misc = new Race(RaceID.SKELETON_NON_BEASTLY);
		misc.swears.add("boneman");
		misc.aimMod = 1.05;
		misc.damMod = 1;
		misc.dodgeMod = 1.05;
		misc.hpMod = .8;
		misc.speedMod = 1.2;
		//+.1 total
		misc.rarity = .4;
		misc.insultList.add("Rattle your bones somewhere else!");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
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
		misc.damMod = 1.1;
		misc.dodgeMod = 1.1;
		misc.hpMod = .9;
		misc.speedMod = 1.1;
		//+.1 total
		misc.rarity = .2;
		misc.insultList.add("Oh good, I needed a firestarter!");
		misc.insultList.add("I'll cook my dinner with your corpse!");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = false;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(0);
		misc.maxPitch = audioSteps(10);
		misc.raceClass = RaceClass.ANTHRO_EXOTIC;
		misc.archetype = Archetype.HEDGE_MAGE;
		raceList.add(misc);
		
		misc = new Race(RaceID.FUGUE);
		misc.swears.add("reclaimer");
		misc.swears.add("lost");
		misc.aimMod = 1.025;
		misc.damMod = 1.025;
		misc.dodgeMod = 1.025;
		misc.hpMod = 1.025;
		misc.speedMod = 1.025;
		//+.1 total
		misc.rarity = .2;
		misc.insultList.add("You won't be reclaiming this!");
		misc.insultList.add("Empires fall, and you are all that remains!");
		misc.insultList.add("I'll end your story right here!");
		misc.baseMap = "nada";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-10);
		misc.maxPitch = audioSteps(0);
		misc.raceClass = RaceClass.HUMAN_LIKE;
		raceList.add(misc);
		
		misc = new Race(RaceID.FISHFOLK);
		misc.swears.add("fish");
		misc.swears.add("drudger");//actual fullblown racism
		misc.swears.add("drowner");
		misc.aimMod = 1.05;
		misc.damMod = 1;
		misc.dodgeMod = 1.05;
		misc.hpMod = 1;
		misc.speedMod = 1;
		//+.1 total
		misc.rarity = .7;
		misc.insultList.add("Go, swim away!");
		misc.insultList.add("The drudgers would have an excellent time with you!");
		misc.baseMap = "mermaid";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.FEMALE_BASIC;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.ANTHRO_REPTILE;
		misc.archetype = Archetype.SEA_SAGE;
		raceList.add(misc);
		
		
		
		//////beasts
		misc = new Race(RaceID.B_WOLF);
		misc.swears.add("dog");
		misc.swears.add("mutt");
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
		misc.insultList.add("Time to close your career!");
		misc.insultList.add("Taste this!");
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
		misc.archetype = Archetype.MIMIC;
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
		misc.insultList.add("Open up!");
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
		misc.archetype = Archetype.MIMIC;
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
		misc.archetype = Archetype.FELL_REAVER;
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
		misc.archetype = Archetype.FELL_REAVER;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_ENT);
		misc.swears.add("tree");
		misc.swears.add("shrub");
		misc.swears.add("timber");
		misc.aimMod = 1;
		misc.damMod = .9;
		misc.dodgeMod = .4;
		misc.hpMod = 2;
		misc.speedMod = .9;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, tree!");
		misc.insultList.add("Chop and burn!");
		misc.insultList.add("Timber!");
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
		misc.insultList.add("Die, beast!");
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
		misc.swears.add("big flying rat");
		misc.aimMod = 1;
		misc.damMod = .8;
		misc.dodgeMod = 1.4;
		misc.hpMod = .7;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you bat!");
		misc.insultList.add("Die, bat!");
		misc.insultList.add("Die, you rat!");
		misc.insultList.add("Die, rat!");
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
		
		misc = new Race(RaceID.B_SWARMBAT);//1/4th level power is the goal for this worth
		misc.swears.add("bat");
		misc.swears.add("flying rat");
		misc.aimMod = 1;
		misc.damMod = .2;
		misc.dodgeMod = 1.4;
		misc.hpMod = .15;
		misc.speedMod = 1.1;
		misc.rarity = 1;
		misc.insultList.add("Die, you bat!");
		misc.insultList.add("Die, bat!");
		misc.insultList.add("Die, you rat!");
		misc.insultList.add("Die, rat!");
		misc.baseMap = "bat";
		misc.raceMaps.add("0");
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
		misc.insultList.add("You look gross!");
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
		misc.swears.add("freak of nature");
		misc.swears.add("preposterous pony");
		misc.swears.add("unreal ungulate");//this means cloven hoof I think
		misc.aimMod = 1.3;
		misc.damMod = .9;
		misc.dodgeMod = .8;
		misc.hpMod = 1.5;
		misc.speedMod = 1.1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, stupid horse!");
		misc.insultList.add("Bet your horn will fetch a high price!");
		misc.insultList.add("I'll be beating you for hours yet!");
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
		misc.archetype = Archetype.HEDGE_MAGE;
		raceList.add(misc);
		
		misc = new Race(RaceID.B_HARPY);
		misc.swears.add("windbag");
		misc.swears.add("hussy");//lmao
		misc.swears.add("muckraker");
		misc.swears.add("cuckoo");
		misc.swears.add("featherbrain");
		misc.swears.add("birdbrain");
		misc.swears.add("silly goose");
		misc.swears.add("flying dodo");
		misc.swears.add("fusspot");
		misc.swears.add("calumniator");
		misc.swears.add("buzzard");
		misc.swears.add("vulture");
		misc.aimMod = 1;
		misc.damMod = 1.1;
		misc.dodgeMod = 1.25;
		misc.hpMod = .9;
		misc.speedMod = 1;
		misc.tradeMod = 1;
		misc.rarity = 1;
		misc.insultList.add("Die, you feathery fiend!");
		misc.insultList.add("Aren't birds supposed to sing?");
		misc.insultList.add("Your greed is almost as bad as your voice!");
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
		misc.insultList.add("Die drudger!");
		misc.insultList.add("Die pond scum!");
		misc.insultList.add("Die mage fodder!");
		misc.baseMap = "flesh_golem";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.F_GOLEM;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.DRUDGER;
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
		misc.insultList.add("Die drudger!");
		misc.insultList.add("Fall to me, titan!");
		misc.baseMap = "flesh_golem";
		misc.raceMaps.add("0");
		misc.magicPower = 0;
		misc.defPower = 0;
		misc.racialType = Race.RaceType.BEAST;
		misc.targetType = TargetFactory.TargetType.HUMANOID;
		misc.emitsBlood = true;
		misc.voice = SoundBox.Voice.F_GOLEM;
		misc.minPitch = audioSteps(-5);
		misc.maxPitch = audioSteps(5);
		misc.raceClass = RaceClass.DRUDGER;
		misc.archetype = Archetype.FISH_TALL;
		raceList.add(misc);
		
		misc = new Race(RaceID.MAJOR_DEMON);
		misc.swears.add("demon");
		misc.aimMod = 1.1;
		misc.damMod = 1.1;
		misc.dodgeMod = .9;
		misc.hpMod = 1.1;
		misc.speedMod = 1;
		misc.rarity = 0;
		misc.insultList.add("Die, you demon!");
		misc.insultList.add("Die, demon!");
		misc.baseMap = "flesh_golem";
		misc.raceMaps.add("0");
		misc.racialType = Race.RaceType.PERSONABLE;
		misc.targetType = TargetFactory.TargetType.DEMON;
		misc.emitsBlood = true;
		misc.raceClass = RaceClass.DEMON;
		raceList.add(misc);
		
		for (Race r: raceList) {
			raceMap.put(r.raceID().name(), r);
		}
		
		mookMakerIDMap.put(RaceID.B_SWARMBAT, new PersonMaker() {

			@Override
			public Person make(int level) {
				return makeSwarmBat(level);
			}

			@Override
			public float worth() {
				return .25f;
			}});
		mookMakerIDMap.put(RaceID.B_BAT, new PersonMaker() {

			@Override
			public Person make(int level) {
				return RaceFactory.makeBat(level);
			}

			@Override
			public float worth() {
				return 1;
			}});
		
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
	
	private static final float
		WEALTH_SMALL = 1f
		,WEALTH_STANDARD = 2f
		,WEALTH_WORKER = 3f
		,WEALTH_WELL_OFF = 5f
		,WEALTH_HIGH = 10f
		,WEALTH_RICH = 20f;
	;
	
	public static Person makeLootBody(int level) {
		Person w = new Person(level);
		return w;
	}
	
	public static Person makeGeneric(int level) {
		Person p = new Person(level);
		p.hTask = HostileTask.DUEL;
		p.finishGeneration();
		return p;
	}
	
	public static Person makePlayerValid(boolean startingLevel) {
		Person p = new Person(1,startingLevel,Race.RaceType.PERSONABLE,null,Person.RaceFlag.NONE,true);
		p.setFlag(PersonFlag.AUTOLEVEL,false);
		p.hTask = HostileTask.DUEL;
		p.updateSkills();
		return p;
	}
	
	public static Person makeOld(int level) {
		Person p = new Person(level);
		addWealth(WEALTH_WELL_OFF,.5f, p);
		p.setPersonType(PersonType.GRIZZLED);
		p.setTitle(randomLists.randomOldTitle());
		p.hTask = HostileTask.DUEL;
		if (extra.chanceIn(1,8)) {
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
		}
		p.liteSetSkillHas(Archetype.ARMORMASTER);//might already have
		p.finishGeneration();
		return p;
	}
	
	public static Person makeQuarterMaster(int level) {
		Person p = new Person(level,AIJob.KNIGHT);
		addWealth(WEALTH_WELL_OFF,.7f, p);
		p.setPersonType(PersonType.GRIZZLED);
		p.hTask = HostileTask.RICH;
		p.liteSetSkillHas(Archetype.ARMORMASTER);//might already have
		p.finishGeneration();
		return p;
	}
	
	public static Person makeWolf(int level) {
		Person w = Person.animal(level, RaceID.B_WOLF, MaterialFactory.getMat("hide"), false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		if (extra.chanceIn(1,5)) {
			w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		}
		w.setFirstName(randomLists.randomWolfName());
		w.hTask = HostileTask.ANIMAL;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.WOLF.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeAlphaWolf(int level) {
		Person w = Person.animal(level, RaceID.B_WOLF, MaterialFactory.getMat("hide"), false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		if (extra.chanceIn(4,5)) {
			w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		}
		w.setFirstName(randomLists.randomWolfName());
		w.setTitle(randomLists.randomAlphaName());
		w.hTask = HostileTask.ANIMAL;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.WOLF.ordinal();
		w.finishGeneration();
		return w;
	}

	public static Person makeMimic(int level) {
		Person w = Person.animal(level, RaceID.B_MIMIC_CLOSED, MaterialFactory.getMat("wood"), false);
		addWealth(WEALTH_HIGH,.3f, w);
		w.setFlag(PersonFlag.HAS_WEALTH,true);//what's in the box? 'money'
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		w.getBag().swapArmorSlot(new Armor(level,0,MaterialFactory.getMat("flesh")),0);
		//w.getBag().swapRace(RaceFactory.getRace("hiding-mimic"));
		w.setPerk(Perk.RACIAL_SHIFTS);
		if (extra.chanceIn(1,3)) {
			w.getBag().addDrawBaneSilently(DrawBane.MIMIC_GUTS);
		}
		w.setFirstName(randomLists.randomFirstName());
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.MONSTERS.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeStatue(int level) {
		Person w = new Person(level,true, Race.RaceType.PERSONABLE,null,Person.RaceFlag.CRACKS,false);
		//currently doesn't need the HAS_WEALTH flag, if that would ever come up, because is personable
		if (extra.chanceIn(1,2)) {
			w.getBag().addDrawBaneSilently(DrawBane.CEON_STONE);
		}
		w.hTask = HostileTask.MONSTER;
		w.finishGeneration();
		return w;
	}
	
	public static Person makeFellReaver(int level) {
		Person w = Person.animal(level, RaceID.B_REAVER_TALL, MaterialFactory.getMat("hide"), false);//DOLATER change flesh type
		w.setFlag(PersonFlag.HAS_WEALTH,true);//unsure if would care
		w.setPersonType(PersonType.FELL_MONSTER);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("flesh"),WeaponType.REAVER_STANDING));
		w.setPerk(Perk.RACIAL_SHIFTS);
		w.setFirstName(randomLists.randomFirstName());
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.FELL.ordinal();
		w.finishGeneration();
		return w;
	}

	public static Person getShaman(int level) {
		Person w = new Person(level);
		addWealth(WEALTH_WORKER,.3f, w);
		w.getBag().addDrawBaneSilently(DrawBane.PROTECTIVE_WARD);
		w.hTask = HostileTask.DUEL;
		w.setArch(Archetype.HEDGE_MAGE);//might already have
		Agent a = new Agent(w);//is assigned in the agent code, required for archetype skill configs
		w.finishGeneration();
		//a.fillSkillConfigs(); included in finish generation, just need to make an agent for it
		return w;
	}
	public static Person makeEnt(int level) {
		Person w = Person.animal(level, RaceID.B_ENT, MaterialFactory.getMat("wood"), false);
		//Person w = new Person(level,true, Race.RaceType.BEAST,MaterialFactory.getMat("wood"),Person.RaceFlag.NONE,false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("wood"),WeaponType.BRANCHES));
		w.getBag().addDrawBaneSilently(DrawBane.ENT_CORE);
		//w.getBag().swapRace(RaceFactory.getRace("ent"));
		w.setFirstName(randomLists.randomEntName());
		w.setTitle("");
		w.hTask = HostileTask.ANIMAL;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.ANIMALS.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeVampire(int level) {
		Person w = new Person(level,true, Race.RaceType.PERSONABLE,null,Person.RaceFlag.UNDEAD,false);
		addWealth(WEALTH_WORKER,.3f, w);
		//w.setScar(biteFor(w.getBag().getRace().raceID()));//TODO: readd bites as some larger 'added image' mechanism
		w.getBag().addDrawBaneSilently(DrawBane.GRAVE_DUST);
		if (extra.chanceIn(1,10)) {
			w.getBag().addDrawBaneSilently(DrawBane.BLOOD);
		}else {
			if (extra.chanceIn(1,6)) {
				w.getBag().addDrawBaneSilently(DrawBane.GRAVE_DIRT);
			}
		}
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.VAMPIRE.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeBear(int level) {
		Person w = Person.animal(level, RaceID.B_BEAR, MaterialFactory.getMat("hide"), false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.CLAWS_TEETH_GENERIC));
		w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		w.setFirstName(randomLists.randomBearName());
		w.setTitle("");
		w.hTask = HostileTask.ANIMAL;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.BEAR.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeBat(int level) {
		Person w = Person.animal(level, RaceID.B_BAT, MaterialFactory.getMat("hide"), false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		w.getBag().addDrawBaneSilently(DrawBane.BAT_WING);
		if (extra.chanceIn(1,7)) {
			w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		}
		w.setFirstName(randomLists.randomBatName());
		//w.setTitle("");
		w.hTask = HostileTask.ANIMAL;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.ANIMALS.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeSwarmBat(int level) {
		Person w = Person.animal(level, RaceID.B_SWARMBAT, MaterialFactory.getMat("hide"), false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TEETH_GENERIC));
		if (extra.chanceIn(1,3)) {
			w.getBag().addDrawBaneSilently(DrawBane.BAT_WING);
		}
		w.setFirstName(randomLists.randomBatName());
		w.hTask = HostileTask.ANIMAL;
		//no cleanse type, is too small
		w.finishGeneration();
		return w;
	}
	
	public static Person getFleshGolem(int level) {
		Person w = Person.animal(level, RaceID.B_FLESH_GOLEM, MaterialFactory.getMat("hide"), false);
		//uses hide because it always turns into re-occuring on player loss so we don't need to worry about it being rare and vanishing after a win
		//cannot get wealth
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.GENERIC_FISTS));
		w.getBag().addDrawBaneSilently(DrawBane.BEATING_HEART);
		w.getBag().addDrawBaneSilently(DrawBane.SINEW);
		w.setFirstName(randomLists.randomFirstName());
		//w.targetOverride = TargetFactory.TargetType.HUMANOID;
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.MONSTERS.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeUnicorn(int level) {
		Person w = Person.animal(level, RaceID.B_UNICORN, MaterialFactory.getMat("hide"), false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.UNICORN_HORN));
		w.getBag().addDrawBaneSilently(DrawBane.UNICORN_HORN);
		w.setFirstName(randomLists.randomFirstName());
		w.hTask = HostileTask.ANIMAL;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.UNICORN.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeHarpy(int level) {
		Person w = Person.animal(level, RaceID.B_HARPY, MaterialFactory.getMat("hide"), false);
		addWealth(WEALTH_STANDARD,.3f, w);//has money
		w.setFlag(PersonFlag.HAS_WEALTH,true);
		w.setPersonType(PersonType.HARPY_GENERIC);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("bone"),WeaponType.TALONS_GENERIC));
		if (extra.chanceIn(1,6)) {
			w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		}
		if (extra.chanceIn(1,50)) {
			w.getBag().addDrawBaneSilently(DrawBane.GOLD);
		}
		if (extra.chanceIn(1,20)) {
			w.getBag().addDrawBaneSilently(DrawBane.SILVER);
		}
		w.setFirstName(randomLists.randomFirstName());
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.HARPY.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person makeDrudgerStock(int level) {
		Person w = Person.animal(level, RaceID.B_DRUDGER_STOCK, MaterialFactory.getMat("fishscales"), false);
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat(extra.choose("rusty iron","rusty iron","iron")),WeaponType.FISH_SPEAR));
		w.setFlag(PersonFlag.HAS_WEALTH,true);
		w.setPersonType(PersonType.DRUDGER_GENERIC);
		for (byte i=0;i<5;i++) {
			if (extra.chanceIn(1,8)) {
				w.getBag().swapArmorSlot(new Armor(level,i,MaterialFactory.getMat("rusty iron"),null),i);
			}
		}
		if (extra.chanceIn(1,6)) {
			w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		}
		w.setFirstName(randomLists.randomWaterName());
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.DRUDGER.ordinal();
		w.finishGeneration();
		return w;
	}
	public static Person makeDrudgerTitan(int level) {
		Person w = Person.animal(level, RaceID.B_DRUDGER_TITAN, MaterialFactory.getMat("fishscales"), false);
		w.setFlag(PersonFlag.HAS_WEALTH,true);
		w.setPersonType(PersonType.DRUDGER_GENERIC);
		w.cleanSetSkillHas(Perk.STAND_TALL);//added to their 'fish tall' so they have some base strength
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat(extra.choose("rusty iron","iron")),WeaponType.FISH_ANCHOR));
		w.getBag().swapArmorSlot(new Armor(level,(byte)2,MaterialFactory.getMat("rusty iron"),null),2);
		w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		if (extra.chanceIn(1,2)) {
			w.getBag().addDrawBaneSilently(DrawBane.MEAT);
		}
		w.setFirstName(randomLists.randomWaterName());
		w.setTitle("the "+randomLists.randomLargeName());
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.DRUDGER.ordinal();
		w.finishGeneration();
		return w;
	}
	public static Person makeDrudgerMage(int level) {
		Person w = Person.animal(level, RaceID.B_DRUDGER_STOCK, MaterialFactory.getMat("fishscales"), false);
		addWealth(WEALTH_SMALL,.5f, w);//has money
		w.setFlag(PersonFlag.HAS_WEALTH,true);
		w.setPersonType(PersonType.DRUDGER_GENERIC);
		w.liteSetSkillHas(Archetype.FISH_MONSOON);
		Agent a = new Agent(w);//is assigned in the agent code, required for archetype skill configs
		w.addFeatPoint(level/2);//bonus feat points for magic
		w.getBag().setWeapon(new Weapon(level,MaterialFactory.getMat("rusty iron"),WeaponType.NULL_WAND));//not a weapon
		w.getBag().swapArmorSlot(new Armor(level,(byte)0,ArmorStyle.GEM.getMatFor(),ArmorStyle.GEM),0);//gem helmet for some reason
		for (byte i=1;i<5;i++) {
			if (extra.chanceIn(1,2)) {
				w.getBag().swapArmorSlot(new Armor(level,i,ArmorStyle.FABRIC.getMatFor(),ArmorStyle.FABRIC),i);
			}
		}
		w.getBag().addDrawBaneSilently(DrawBane.UNICORN_HORN);//maybe to represent wand?
		w.setFirstName(randomLists.randomWaterName());
		w.setTitle("scion of "+randomLists.randomWaterName());
		w.hTask = HostileTask.MONSTER;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.DRUDGER.ordinal();
		w.finishGeneration();
		return w;
	}
	
	public static Person getMugger(int level) {
		Person w;
		if (extra.chanceIn(1,4)) {
			w = new Person(level,AIJob.ROGUE);
			addWealth(WEALTH_WELL_OFF,.3f, w);
			w.setFacLevel(Faction.ROGUE,20, 0);
		}else {
			w = new Person(level);
			addWealth(WEALTH_STANDARD,.3f,w);
			w.setFacLevel(Faction.ROGUE,10, 0);
		}
		w.setFacLevel(Faction.HEROIC,0, 10);
		w.hTask = HostileTask.MUG;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.BANDIT.ordinal();
		if (extra.chanceIn(1,100)) {
			w.getBag().addDrawBaneSilently(DrawBane.GOLD);
			addWealth(WEALTH_WELL_OFF,.7f, w);
		}
		if (extra.chanceIn(1,50)) {
			w.getBag().addDrawBaneSilently(DrawBane.SILVER);
			addWealth(WEALTH_STANDARD,.7f, w);
		}
		w.finishGeneration();
		return w;
	}
	
	public static Person makeMuggerWithTitle(int level) {
		Person w = getMugger(level);
		w.setTitle("the " + extra.capFirst(randomLists.randomMuggerName()));
		return w;
	}
	
	
	public static Person getDueler(int level) {
		Person w = new Person(level,AIJob.DUELER);
		w.setFacLevel(Faction.DUEL,extra.randRange(10,20), 0);
		w.hTask = HostileTask.DUEL;
		addWealth(WEALTH_STANDARD,.3f, w);
		w.finishGeneration();
		return w;
	}
	
	public static Person makeDuelerWithTitle(int level) {
		Person w = getDueler(level);
		w.setTitle("the " + extra.capFirst(randomLists.randomWarrior()));
		return w;
	}
	
	public static Person makeCultistLeader(int level, CultType ct) {
		Person w = null;
		//unlike normal cultists, leaders have more specialized creation code
		switch (ct) {
		default:
			 w = new Person(level,AIJob.CULTIST_WORSHIPER);
			 addWealth(WEALTH_WELL_OFF,.6f, w);
			 NPCMutator.cultistLeader_Switch(w,ct,true);
			 break;
		case BLOOD:
			w = new Person(level,AIJob.KNIGHT);//blood for the blood queen
			addWealth(WEALTH_WELL_OFF,.6f, w);
			NPCMutator.cultLeader_Blood(w,true);
			break;
		case SKY:
			w = new Person(level,AIJob.ROGUE);
			addWealth(WEALTH_WELL_OFF,.6f, w);
			NPCMutator.cultLeader_Sky(w,true);
			break;
		}
		w.finishGeneration();
		return w;
	}

	public static Person makeCultist(int level, CultType ct) {
		Person w = new Person(level,AIJob.CULTIST_WORSHIPER);
		addWealth(WEALTH_SMALL,.2f, w);
		NPCMutator.cultist_Switch(w,ct,true);
		w.finishGeneration();
		return w;
	}
	
	public static Person makeMaybeRacist(int level) {
		Person w = new Person(level);
		addWealth(WEALTH_STANDARD,.3f, w);
		if (w.isRacist()) {
			w.hTask = HostileTask.RACIST;
		}else {
			if (w.isAngry()) {
				w.hTask = HostileTask.DUEL;
			}else {
				w.hTask = HostileTask.PEACE;
			}
		}
		w.finishGeneration();
		return w;
	}
	
	public static Person getRacist(int level) {
		Person w = new Person(level);
		addWealth(WEALTH_STANDARD,.3f, w);
		w.hTask = HostileTask.RACIST;
		w.setRacism(true);
		w.finishGeneration();
		return w;
	}
	public static Person getPeace(int level) {
		Person w = new Person(level);
		addWealth(WEALTH_STANDARD,.3f, w);
		w.hTask = HostileTask.PEACE;
		w.finishGeneration();
		return w;
	}
	public static Person getBoss(int level) {
		Person w = new Person(level);
		w.hTask = HostileTask.BOSS;
		Agent a = new Agent(w,AgentGoal.OWN_SOMETHING);//should have an agent
		return w;
	}
	
	public static Person makeDemonOverlord(int level) {
		Person w = new Person(level,true, Race.RaceType.PERSONABLE,null,RaceFlag.NONE,false,AIJob.KNIGHT,RaceFactory.getRace(RaceID.MAJOR_DEMON));
		w.getBag().addDrawBaneSilently(DrawBane.VIRGIN);
		w.setPerk(Perk.HELL_BARON_NPC);
		w.hTask = HostileTask.BOSS;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.MONSTERS.ordinal();
		Agent a = new Agent(w,AgentGoal.OWN_SOMETHING);//should have an agent
		w.finishGeneration();
		return w;
	}
	
	public static Person getDryad(int level) {
		Person w = new Person(level);
		w.hTask = HostileTask.ANIMAL;
		w.setFacLevel(Faction.FOREST,15,0);
		w.finishGeneration();
		return w;
	}
	
	public static Person makeDGuard(int level) {
		Person w = new Person(level);
		addWealth(WEALTH_WORKER,.4f, w);
		w.setTitle(randomLists.randomDGuardTitle());
		w.hTask = HostileTask.GUARD_DUNGEON;
		w.finishGeneration();
		return w;
	}
	
	public static Person getLumberjack(int level) {
		Person w = new Person(level,AIJob.LUMBERJACK);
		addWealth(WEALTH_WORKER,.5f, w);
		w.hTask = HostileTask.LUMBER;
		w.setFacLevel(Faction.FOREST,0,100);
		w.finishGeneration();
		return w;
	}
	public static Person makeRich(int level) {
		Person w = new Person(level);
		w.getBag().setLocalGold(10);
		addWealth(WEALTH_RICH,.6f, w);
		w.hTask = HostileTask.RICH;
		w.setFacLevel(Faction.MERCHANT,10,0);
		w.finishGeneration();
		return w;
	}
	public static Person makeCollector(int level) {
		Person w = new Person(level,AIJob.COLLECTOR);
		addWealth(WEALTH_HIGH,.5f, w);
		w.hTask = HostileTask.DUEL;
		w.setFacLevel(Faction.MERCHANT,10,0);
		List<DrawBane> dbs = w.getBag().getDrawBanes();
		dbs.add(DrawBane.draw(DrawList.COLLECTOR));
		if (extra.chanceIn(2,3)) {
			dbs.add(DrawBane.draw(DrawList.COLLECTOR));
		}
		if (extra.chanceIn(2,4)) {
			dbs.add(DrawBane.draw(DrawList.COLLECTOR));
		}
		w.setTitle(randomLists.randomCollectorName());
		w.finishGeneration();
		return w;
	}
	
	public static Person getLawman(int level) {
		Person w = new Person(level);
		addWealth(WEALTH_HIGH,.6f, w);
		w.hTask = HostileTask.LAW_EVIL;
		w.setFacLevel(Faction.HEROIC,5,0);
		w.finishGeneration();
		return w;
	}
	
	public static Person makeGraverobber(int level) {
		Person w;
		List<DrawBane> list;
		int rarityMult;
		if (extra.chanceIn(3,4)) {
			//comes here often
			w = new Person(level,AIJob.GRAVER);
			list = w.getBag().getDrawBanes();
			w.cleanSetSkillHas(Perk.GRAVEYARD_SIGHT);//clean, doesn't count as a point
			if (extra.chanceIn(1,3)) {
				list.add(DrawBane.SILVER);
			}else {
				list.add(DrawBane.GRAVE_DIRT);
			}
			w.setFacLevel(Faction.ROGUE,15, 0);
			rarityMult = 2;
			addWealth(WEALTH_WORKER,.5f, w);
		}else {
			//more generic robber
			if (extra.chanceIn(1,2)) {
				//higher cut of thief
				w = new Person(level,AIJob.ROGUE);
				w.setFacLevel(Faction.ROGUE,20, 0);
				rarityMult = 3;
				addWealth(WEALTH_WELL_OFF,.5f, w);
			}else {//afraid of graveyard
				w = new Person(level);
				w.setPersonType(PersonType.COWARDLY);
				w.addEffect(Effect.CURSE);
				w.setFacLevel(Faction.ROGUE,10, 0);
				rarityMult = 1;
				addWealth(0.5f,.3f, w);
			}
			list = w.getBag().getDrawBanes();
		}
		w.hTask = HostileTask.MUG;
		w.cleanseType = (byte)CleanseSideQuest.CleanseType.BANDIT.ordinal();
		w.setFacLevel(Faction.HEROIC,0,10);
		if (extra.chanceIn(rarityMult,8)) {
			if (extra.chanceIn(rarityMult,20)) {
				list.add(DrawBane.GOLD);
			}else {
				list.add(DrawBane.SILVER);
			}
			
		}
		w.finishGeneration();
		return w;
	}
	
	public static Person makeGravedigger(int level) {
		Person w = new Person(level,AIJob.GRAVER);
		w.cleanSetSkillHas(Perk.GRAVEYARD_SIGHT);//clean, doesn't count as a point
		w.hTask = HostileTask.PEACE;
		List<DrawBane> list = w.getBag().getDrawBanes();
		list.add(DrawBane.GRAVE_DIRT);
		if (extra.chanceIn(1,8)) {//can afford full protection
			list.add(DrawBane.PROTECTIVE_WARD);
			w.setFacLevel(Faction.HUNTER,10,0);
			w.setFacLevel(Faction.MERCHANT,10,0);
			w.getBag().getHand().improveEnchantChance(level);//improve weapon enchant
			w.setTitle(randomLists.randomCollectorName());
			addWealth(WEALTH_WELL_OFF,.5f, w);
		}else {
			if (extra.chanceIn(3,4)) {//undead tool for protection
				if (extra.chanceIn(1,3)) {
					list.add(DrawBane.SILVER);
					list.add(DrawBane.GRAVE_DUST);
					//combatative
					w.addBlood(2);
					w.setFacLevel(Faction.HUNTER,15,0);
					w.setFacLevel(Faction.HEROIC,10,0);
					w.getBag().getHand().transmuteWeapMat(MaterialFactory.getMat("silver"));
					w.hTask = HostileTask.HUNT;
					w.setTitle(randomLists.randomHunterTitle());
				}else {
					list.add(DrawBane.GARLIC);
				}
				if (extra.chanceIn(1,2)) {
					list.add(DrawBane.GARLIC);//chance of more for either outcome
				}
				addWealth(WEALTH_WORKER,.4f, w);
			}else {
				list.add(DrawBane.GRAVE_DIRT);//didn't get any, so just more grave dirt
				addWealth(WEALTH_STANDARD,.3f, w);
			}
		}
		w.finishGeneration();
		return w;
	}
	
	public static Person makeHunter(int level) {
		Person w = new Person(level);
		addWealth(WEALTH_WORKER,.3f, w);
		w.setFacLevel(Faction.HUNTER,20,0);
		w.setFacLevel(Faction.HEROIC,6,0);
		w.setFacLevel(Faction.MERCHANT,2,0);
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
			w.cleanSetSkillHas(Perk.GRAVEYARD_SIGHT);
			list.add(DrawBane.SILVER);
			list.add(DrawBane.GRAVE_DUST);
			break;
		case 5:
			list.add(DrawBane.PROTECTIVE_WARD);
			w.getBag().getHand().improveEnchantChance(level);
			break;
		}
		w.finishGeneration();
		return w;
	}

	public static int scarFor(RaceID race) {
		switch (race) {
		case HUMAN:
			if (extra.chanceIn(1, 3)) {
				return extra.randRange(0,scarMap.get(RaceID.HUMAN).size()-1);
			}
			break;
		}
		return -1;
	}
	//TODO: readd later
	public static String biteFor(RaceID race) {
		switch (race) {
		case HUMAN:
				return extra.choose("H_vampbite1","H_vampbite2","H_vampbite3","H_vampbite4","H_vampbite5");
		}
		return "";
	}
	
	public static String scarLookup(RaceID race, int num) {
		if (num < 0) {
			return null;
		}
		return scarMap.get(race).get(num);
	}

	/**
	 * if this returns <=0, you should not add the add
	 */
	public static int addAdjustLevel(int inLevel,int downSwing) {
		if (downSwing <=0) {
			return inLevel;
		}
		return inLevel/(downSwing+1);
	}
	
	/**
	 * if mag is sub 1f will roll that as a chance to give gold
	 * <br>
	 * reliable is as in cleanRangeReward
	 * @param magnitude
	 * @param reliable
	 * @param p
	 * @return gold given
	 */
	public static int addWealth(float magnitude, float reliable, Person p) {
		//if <1 might not add it
		if (magnitude < 1f || extra.randFloat() > magnitude) {
			return 0;
		}
		int gold = IEffectiveLevel.cleanRangeReward(p.getLevel(),Math.max(1f,magnitude),reliable);
		//the local gold will be rolled into a superperson when their location is set, if needed
		p.getBag().setLocalGold(p.getBag().getLocalGold()+gold);
		return gold;
	}
	
	private static List<Person> groupMakeWithMooks(float power,int minMooks, int maxMooks, float mookWorthMult,PersonMaker mookMaker){
		int minNumLevel = (int) (power/(mookWorthMult*minMooks)); 
		if (minNumLevel < 1) {//if we can't allocate enough level to make the min number
			return null;
		}//FIXME: this is still making bad assumptions about level being worth one person, but it should still be better than the old ways
		int maxNumLevel = (int) Math.max(1,power/(mookWorthMult*maxMooks));//don't let us make so many mooks they fall under the threshold
		int levelRoll = extra.randRange(minNumLevel,maxNumLevel);
		//now we need to round the level roll to the nearest mook amount that fits it
		int amount = Math.round((mookWorthMult*levelRoll)/power);
		levelRoll = (int) (power/(mookWorthMult*amount));
		List<Person> people = new ArrayList<Person>();
		for (int i =0; i < amount;i++) {
			Person p = mookMaker.make(levelRoll);
			p.setFlag(PersonFlag.IS_MOOK,true);
			people.add(p);
		}
		return people;
	}
	
	public static interface PersonMaker{
		Person make(int level);
		float worth();
	}
	
	/**
	 * note that group power does not go 1 to 1 with level power
	 * <br>
	 * suggested behavior is treating the fight as a 1v2 against the player??? TODO needs math thinking
	 */
	public static List<Person> wrapMakeGroupForLeader(Person leader,PersonMaker mookMaker,float mookPower,int minMooks,int maxMooks){
		List<Person> people = groupMakeWithMooks(mookPower,minMooks,maxMooks,mookMaker.worth(),mookMaker);
		if (people == null) {
			people = new ArrayList<Person>();
		}
		people.add(leader);
		return people;
	}
	
	public static List<Person> makeGroupOrDefault(float power, int minMooks, int maxMooks, PersonMaker mookMaker, PersonMaker elseDefault){
		List<Person> people = groupMakeWithMooks(power,minMooks,maxMooks,mookMaker.worth(),mookMaker);
		if (people == null || people.size() == 0) {//FIXME: shouldn't really be size 0 but I need to redo entirely anyway
			people = new ArrayList<Person>();
			people.add(elseDefault.make(Math.round(power)));
		}
		return people;
	}
	
	public static PersonMaker getMakerForID(RaceID id) {
		return mookMakerIDMap.get(id);
	}
	
}
