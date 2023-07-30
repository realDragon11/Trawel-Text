package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.personal.Person;

public class TargetFactory {

	public static List<Target> targetList = new ArrayList<Target>();
	public static Target noTarget;
	public static List<Attack.Wound> fireWounds = new ArrayList<Attack.Wound>();
	public static List<Attack.Wound> shockWounds = new ArrayList<Attack.Wound>();
	public static List<Attack.Wound> freezeWounds = new ArrayList<Attack.Wound>();
	
	//public static Map<TargetType,List<Target>> targetTypeMap = new HashMap<TargetType,List<Target>>();
	public static Map<TargetType,WeightedTable> targetTypeTable = new HashMap<TargetType,WeightedTable>();
	private static Map<String,Target> targetMap = new HashMap<String,Target>();
	
	public TargetFactory() {
		
		
		fireWounds.add(Attack.Wound.SCALDED);
		fireWounds.add(Attack.Wound.SCREAMING);
		
		shockWounds.add(Attack.Wound.SCREAMING);
		
		freezeWounds.add(Attack.Wound.FROSTED);
		freezeWounds.add(Attack.Wound.FROSTBITE);
		
		Target t = new Target();
		t.name = "head";
		t.hit = .8;
		t.sharp = .6;
		t.blunt = 3;
		t.pierce = 1;
		t.rarity = .6;
		t.slot = 0;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DICE);
		t.bluntWounds.add(Attack.Wound.CONFUSED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.DIZZY);
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
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
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
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.TAT);
		targetList.add(t);
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.DISARMED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right ","left "};
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
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right ","left "};
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.BLINDED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
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
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
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
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.TAT);
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
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
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
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
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
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
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
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.WINDED);
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
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
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
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.bluntWounds.add(Attack.Wound.CONFUSED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.DIZZY);
		t.bluntWounds.add(Attack.Wound.DISARMED);
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
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.TAT);
		targetList.add(t);
		
		t = new Target();
		t.variants = new String[] {"right fore","left fore","right hind","left hind"};
		t.name = "leg";
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 2;
		t.slot = 1;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		/*
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
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(Attack.Wound.BLEED);
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
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		*/
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right ","left "};
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
		t.type = TargetType.QUAD;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.BLINDED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
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
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
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
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		
		//statue
		
		
		t = new Target();
		t.name = "head";
		t.hit = .8;
		t.sharp = .5;
		t.blunt = 2;
		t.pierce = .75;
		t.rarity = .6;
		t.slot = 0;
		t.type = TargetType.STATUE;
		t.slashWounds.add(Attack.Wound.GRAZE);
		t.slashWounds.add(Attack.Wound.CRUSHED);
		t.bluntWounds.add(Attack.Wound.GRAZE);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		t.pierceWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
		
		t = new Target();
		t.name = "neck";
		t.hit = .5;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .4;
		t.slot = 0;
		t.type = TargetType.STATUE;
		t.slashWounds.add(Attack.Wound.GRAZE);
		t.slashWounds.add(Attack.Wound.CRUSHED);
		t.bluntWounds.add(Attack.Wound.GRAZE);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		t.pierceWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
		
		t = new Target();
		t.name = "chest";
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 2;
		t.type = TargetType.STATUE;
		t.slashWounds.add(Attack.Wound.GRAZE);
		t.slashWounds.add(Attack.Wound.CRUSHED);
		t.bluntWounds.add(Attack.Wound.GRAZE);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		t.pierceWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1;
		t.blunt = .6;
		t.pierce = .5;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.STATUE;
		t.slashWounds.add(Attack.Wound.GRAZE);
		t.slashWounds.add(Attack.Wound.CRUSHED);
		t.bluntWounds.add(Attack.Wound.GRAZE);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		t.pierceWounds.add(Attack.Wound.CRUSHED);
		targetList.add(t);
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 3;
		t.type = TargetType.STATUE;
		t.slashWounds.add(Attack.Wound.GRAZE);
		t.slashWounds.add(Attack.Wound.CRUSHED);
		t.bluntWounds.add(Attack.Wound.GRAZE);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		
		//fell reaver standing
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 3;
		t.type = TargetType.S_REAVER;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		//fell reaver crouched
		
		t = new Target();
		t.name = "head";
		t.hit = .8;
		t.sharp = .6;
		t.blunt = 3;
		t.pierce = 1;
		t.rarity = .6;
		t.slot = 0;
		t.type = TargetType.C_REAVER;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DICE);
		t.bluntWounds.add(Attack.Wound.CONFUSED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.DIZZY);
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
		t.type = TargetType.C_REAVER;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "chest";
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 2;
		t.type = TargetType.C_REAVER;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.C_REAVER;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.DISARMED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 3;
		t.type = TargetType.C_REAVER;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right ","left "};
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
		t.type = TargetType.C_REAVER;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.bluntWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		targetList.add(t);
		
		//undead
		t = new Target();
		t.name = "head";
		t.hit = .8;
		t.sharp = .6;
		t.blunt = 3;
		t.pierce = 1;
		t.rarity = .6;
		t.slot = 0;
		t.type = TargetType.UNDEAD_H;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DICE);
		t.bluntWounds.add(Attack.Wound.CONFUSED);
		t.bluntWounds.add(Attack.Wound.DIZZY);
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
		t.type = TargetType.UNDEAD_H;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		targetList.add(t);
		
		t = new Target();
		t.name = "chest";
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 2;
		t.type = TargetType.UNDEAD_H;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.TAT);
		targetList.add(t);
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.UNDEAD_H;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.bluntWounds.add(Attack.Wound.DISARMED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		targetList.add(t);
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 3;
		t.type = TargetType.UNDEAD_H;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(Attack.Wound.GRAZE);
		targetList.add(t);
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right ","left "};
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
		t.type = TargetType.UNDEAD_H;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.bluntWounds.add(Attack.Wound.BLINDED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "guts";
		t.hit = 1;
		t.sharp = 1.3;
		t.blunt = 1;
		t.pierce = 1.3;
		t.rarity = 1;
		t.slot = 2;
		t.type = TargetType.UNDEAD_H;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.TAT);
		targetList.add(t);
		
		//flying
		t = new Target();
		t.name = "head";
		t.hit = .8;
		t.sharp = .6;
		t.blunt = 3;
		t.pierce = 1;
		t.rarity = .6;
		t.slot = 0;
		t.type = TargetType.FLY;
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.DICE);
		t.bluntWounds.add(Attack.Wound.CONFUSED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.DIZZY);		
		t.bluntWounds.add(Attack.Wound.DISARMED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "chest";
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 2;
		t.type = TargetType.FLY;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.TAT);
		targetList.add(t);
		
		t = new Target();
		t.name = "wing";
		t.variants = new String[] {"right ","left "};
		t.hit = 2;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1.5;
		t.slot = 1;
		t.type = TargetType.FLY;
		t.slashWounds.add(Attack.Wound.TEAR);
		t.bluntWounds.add(Attack.Wound.TEAR);
		t.pierceWounds.add(Attack.Wound.TEAR);
		targetList.add(t);
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right ","left "};
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = .5;
		t.slot = 3;
		t.type = TargetType.FLY;
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right ","left "};
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
		t.type = TargetType.FLY;
		t.slashWounds.add(Attack.Wound.BLINDED);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.bluntWounds.add(Attack.Wound.BLINDED);
		t.pierceWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLINDED);
		targetList.add(t);
		
		t = new Target();
		t.name = "heart";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 2;
		t.rarity = .4;
		t.slot = 2;
		t.type = TargetType.FLY;
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.KO);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
		targetList.add(t);
		
		t = new Target();
		t.name = "guts";
		t.hit = 1;
		t.sharp = 1.3;
		t.blunt = 1;
		t.pierce = 1.3;
		t.rarity = 1;
		t.slot = 2;
		t.type = TargetType.FLY;
		t.slashWounds.add(Attack.Wound.SLICE);
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.BLEED);
		t.slashWounds.add(Attack.Wound.HACK);
		t.bluntWounds.add(Attack.Wound.WINDED);
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.pierceWounds.add(Attack.Wound.BLEED);
		targetList.add(t);
		
		
		for (Target ta: targetList) {
			toMap(ta);
		}
		
		//setup part 2
		for (TargetType tt: TargetType.values()) {
			ArrayList<Target> copyList = new ArrayList<Target>();
			for (Target mat: targetList){
				if (mat.type == tt) {
					copyList.add(mat);
				}
			}
			float[] sWeightList = new float[targetList.size()];
			for (int i = targetList.size()-1;i>=0;--i) {
				sWeightList[i] = targetList.get(i).type == tt ? (float) targetList.get(i).rarity : 0f;
			}
			try {
			targetTypeTable.put(tt,new WeightedTable(sWeightList));
			}catch(java.lang.IllegalArgumentException e) {
				
			}
		}
	}
	
	public enum TargetType{
		HUMANOID, MIMIC, OPEN_MIMIC, NONE,QUAD, STATUE, S_REAVER, C_REAVER, UNDEAD_H, FLY;
	}
	
	public enum BodyPlan {
		HUMANOID, QUAD, FLY, NO_VARIANTS_ALL;
	}
	
	public enum TypeBody {
		HUMAN_LIKE(BodyPlan.HUMANOID,TargetType.HUMANOID),
		BASIC_QUAD(BodyPlan.QUAD,TargetType.QUAD),
		MIMIC(BodyPlan.NO_VARIANTS_ALL,TargetType.MIMIC,TargetType.OPEN_MIMIC),
		STATUE(BodyPlan.HUMANOID,TargetType.STATUE),
		REAVER(BodyPlan.NO_VARIANTS_ALL,TargetType.S_REAVER,TargetType.C_REAVER),//DOLATER needs it's variants
		BASIC_FLY(BodyPlan.FLY,TargetType.FLY),
		UNDEAD(BodyPlan.HUMANOID,TargetType.UNDEAD_H);
		
		public BodyPlan plan;
		public TargetType[] types;
		
		TypeBody(BodyPlan plan, TargetType... types){
			this.plan = plan;
			this.types = types;
		}
	}
	
	public static Target randTarget(TargetType targetType) {
		return targetList.get(targetTypeTable.get(targetType).random(extra.getRand()));
	}
	
	public static Target randTarget(Person p) {
		TargetHolder hold = p.getBody();
		return null;
	}
	
	public static TargetHolder battleSetup(Person p) {
		TargetHolder hold = new TargetHolder();
		TypeBody types = p.getBodyType();
		switch (types.plan) {
		
		}
		
		p.setBody(hold);
		
		return hold;
	}
	

	/**
	 * for masks, all of the tables should be the same, so only need to calculate one of them
	 */
	protected static WeightedTable buildTable(Target[] targets) {
		float[] weights = new float[targets.length];
		Map<Target,Integer> counts = new HashMap<Target,Integer>();
		//hashmap is needless overkill but idk if the tree thing is better
		for (Target t: targets) {
			counts.put(t,counts.getOrDefault(t, 0)+1);
		}
		for (int i = 0; i < targets.length; i++) {
			weights[i] = (float) (targets[i].rarity/counts.get(targets[i]));
		}
		return new WeightedTable(weights);
	}
	
	private static void toMap(Target t) {
		targetMap.put(t.type.ordinal() + t.name,t);
	}
	
	public static Target fromMap(TargetType type, String name) {
		return targetMap.get(type.ordinal() + name);
	}
}
