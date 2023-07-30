package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.battle.Combat.AttackReturn;
import trawel.battle.attacks.Attack.Wound;
import trawel.personal.Person;

public class TargetFactory {

	private List<Target> targetList = new ArrayList<Target>();
	private static TargetFactory handler;
	public static Target noTarget;
	public static List<Attack.Wound> fireWounds = new ArrayList<Attack.Wound>();
	public static List<Attack.Wound> shockWounds = new ArrayList<Attack.Wound>();
	public static List<Attack.Wound> freezeWounds = new ArrayList<Attack.Wound>();
	
	//public static Map<TargetType,List<Target>> targetTypeMap = new HashMap<TargetType,List<Target>>();
	public static Map<TargetType,WeightedTable> targetTypeTable = new HashMap<TargetType,WeightedTable>();
	private static Map<String,Target> targetMap = new HashMap<String,Target>();
	
	/**
	 * 3 slash 1 blunt 1 pierce
	 */
	private void addLeg_LimbWounds(Target t,Wound bleedReplace) {
		if (bleedReplace == null) {
			bleedReplace = Wound.BLEED;
		}
		t.slashWounds.add(Attack.Wound.DICE);
		t.slashWounds.add(Attack.Wound.HAMSTRUNG);
		t.slashWounds.add(bleedReplace);
		t.bluntWounds.add(Attack.Wound.TRIPPED);
		t.pierceWounds.add(bleedReplace);
	}
	
	/**
	 * 1 slash 1 pierce
	 */
	private void addMinorBleed(Target t) {
		t.slashWounds.add(Attack.Wound.BLEED);
		t.pierceWounds.add(Attack.Wound.BLEED);
	}
	/**
	 * 1 slash 1 blunt 1 pierce
	 */
	private void add_IBleed_MBleed(Target t) {
		t.slashWounds.add(Attack.Wound.MAJOR_BLEED);
		t.bluntWounds.add(Attack.Wound.I_BLEED);
		t.pierceWounds.add(Attack.Wound.MAJOR_BLEED);
	}
	
	public TargetFactory() {
		handler = this;
		
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 1;
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
		addLeg_LimbWounds(t,null);
		t.mappingNumber = 3;
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
		t.mappingNumber = 0;
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
		add_IBleed_MBleed(t);
		t.bluntWounds.add(Attack.Wound.KO);
		t.mappingNumber = 2;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 1;
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
		add_IBleed_MBleed(t);
		t.mappingNumber = 3;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 2;
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
		addLeg_LimbWounds(t,null);
		t.mappingNumber = 3;
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
		t.mappingNumber = 0;
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
		add_IBleed_MBleed(t);
		t.bluntWounds.add(Attack.Wound.KO);
		t.mappingNumber = 2;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
		targetList.add(t);
		
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
		addLeg_LimbWounds(t,null);
		t.mappingNumber = 3;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 1;
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
		addLeg_LimbWounds(t,null);
		t.mappingNumber = 1;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 0;
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
		t.bluntWounds.add(Attack.Wound.CRUSHED);
		t.pierceWounds.add(Attack.Wound.TAT);
		t.mappingNumber = 2;
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
		t.mappingNumber = 1;
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
		addLeg_LimbWounds(t,Wound.GRAZE);
		t.mappingNumber = 3;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 0;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 0;
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
		add_IBleed_MBleed(t);
		t.bluntWounds.add(Attack.Wound.KO);
		t.mappingNumber = 2;
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
		t.mappingNumber = 2;
		targetList.add(t);
		
		
		for (Target ta: targetList) {
			toMap(ta);
		}
		
		//new system with variants
		for (TypeBody tb: TypeBody.values()) {
			try {
				tb.setup();
			}catch (Exception e) {
				System.out.println("error with " +tb.name());
				e.printStackTrace();
			}
		}
		
		//DOLATER: this is an old system now, likely will be removed
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
	
	public enum TargetMode{
		ALL
	}
	
	public enum BloodType{
		NORMAL, NONE, VARIES
	}
	
	public enum TypeBody {
		HUMAN_LIKE(BodyPlan.HUMANOID,BloodType.NORMAL,TargetType.HUMANOID),
		BASIC_QUAD(BodyPlan.QUAD,BloodType.NORMAL,TargetType.QUAD),
		MIMIC(BodyPlan.NO_VARIANTS_ALL,BloodType.VARIES,TargetType.MIMIC,TargetType.OPEN_MIMIC),
		STATUE(BodyPlan.HUMANOID,BloodType.NONE,TargetType.STATUE),
		REAVER(BodyPlan.NO_VARIANTS_ALL,BloodType.NONE,TargetType.S_REAVER,TargetType.C_REAVER),//DOLATER needs it's variants
		BASIC_FLY(BodyPlan.FLY,BloodType.NORMAL,TargetType.FLY),
		UNDEAD(BodyPlan.HUMANOID,BloodType.NONE,TargetType.UNDEAD_H);
		
		public final BodyPlan plan;
		private final BloodType blood;
		public final TargetType[] types;
		
		private WeightedTable[] tables;
		private List<List<Object>>backmap;
		private int uniqueParts;
		/**
		 * contains dupes, the other code handles them being variants
		 */
		private List<Target> allTargets;
		
		private List<TargetReturn> tRets;
		/**
		 * links a type/variant to a final part
		 * used mostly for mapping to different parts
		 * otherwise will just be counting from 0 to size-1
		 */
		private int[] map;
		
		TypeBody(BodyPlan plan, BloodType blood, TargetType... types){
			this.plan = plan;
			this.blood = blood;
			this.types = types;
		}
		//could use another function to call if needs to have different behavior
		public void setup() {
			tRets = new ArrayList<TargetReturn>();
			backmap = TargetHolder.makeMap(this);
			tables = new WeightedTable[types.length+1];
			allTargets = new ArrayList<Target>();
			for (Object o: backmap.get(0)) {
				allTargets.add((Target) o);
			}
			tables[0] = buildTable(allTargets, null);//table of 'all', used for null masks
			for (int i = 1; i < tables.length-1;i++) {
				tables[i] = buildTable(allTargets, new TargetType[]{types[i-1]});
			}
			List<Target> singles = new ArrayList<Target>();
			List<Integer> multiNums = new ArrayList<Integer>();
			List<List<Target>> subTargets = new ArrayList<List<Target>>();
			List<List<Integer>> subVariants = new ArrayList<List<Integer>>();
			for (int i = 0; i < allTargets.size();i++) {
				Target t = allTargets.get(i);
				if (t.mappingNumber == -1) {
					singles.add(t);
				}else {
					int index = multiNums.indexOf(t.mappingNumber);
					if (index == -1) {
						multiNums.add(t.mappingNumber);
						subTargets.add(new ArrayList<Target>());
						subVariants.add(new ArrayList<Integer>());
						index = multiNums.size()-1;
					}
					subTargets.get(index).add(t);
					subVariants.get(index).add((Integer)backmap.get(1).get(i));
				}
			}
			int[] multiCount = new int[multiNums.size()];//the number of different variants in the mapping
			for (int i = 0; i < multiNums.size();i++) {
				multiCount[i] = (int) subVariants.get(i).stream().distinct().count();
			}
			int offset = singles.size();
			int totalMulti = 0;
			for (int i = 0; i < multiCount.length;i++) {
				totalMulti += multiCount[i];
			}
			uniqueParts = offset + totalMulti;
			map = new int[allTargets.size()];
			//the maps converts an alltarget number to a condition arr number
			//the outcome numbers don't matter as long as they're consistent, so we make them here
			for (int i = 0;i < map.length;i++) {
				Target t = allTargets.get(i);
				if (t.mappingNumber == -1) {
					map[i] = singles.indexOf(t);
					tRets.add(new TargetReturn(t,-1,map[i],i));
				}else {
					int index = multiNums.indexOf(t.mappingNumber);
					int subindex = subTargets.get(index).indexOf(t);
					//very fancy system to make each variant mappable
					map[i] = offset + multiSum(multiCount,index) + subindex;
					//offset = singlets
					//multisum gets all the prior counts of multis taking up space
					//the last bit gets our value within the current multi
					if (t.variants != null) {
						tRets.add(new TargetReturn(t,subVariants.get(index).get(subindex),map[i],i));
					}else {
						tRets.add(new TargetReturn(t,-1,map[i],i));
					}
					//System.err.println(subindex +" " +subVariants.get(index).get(subindex));
				}
				
			}
		}
		
		/**
		 * returns the sum of the ints inside nums up to the array index of upTo
		 */
		private static int multiSum(int[] nums,int upTo) {
			int total = 0;
			for (int i = 0; i < upTo;i++) {
				total+=nums[i];
			}
			return total;
		}
		
		public int getTotalParts() {
			return uniqueParts;
		}
		
		//were added later so don't get used when passing arrays around
		//the arrays work better for certain internal things anyway
		public class TargetReturn{
			public final int variant;
			public final Target tar;
			public final int mapLoc;
			public final int spot;
			
			public TargetReturn(Target tar, int vari, int mapLoc, int spot){
				this.variant = vari;
				this.tar = tar;
				this.mapLoc = mapLoc;
				this.spot = spot;
			}
			
			public String getName() {
				return (variant >= 0 ? tar.variants[variant] : "" )+ tar.name;
			}
		}
		
		/**
		 * changed
		 */
		public TargetReturn randTarget(TargetType config) {
			int val;
			if (config == null) {
				return tRets.get(tables[0].random(extra.getRand()));//0 is the global table
			}
			for (val = types.length-1; val >= 0;val--) {
				if (config == types[val]) {
					break;//we found what list we're in, it's stored now
				}
			}
			return tRets.get(tables[val+1].random(extra.getRand()));//0 is the global table, so offset
		}
		
		public String spotName(int spot) {
			return tRets.get(spot).getName();
		}
		
		public int getMap(int spot) {
			return tRets.get(spot).mapLoc;
		}
		public TargetReturn getTargetReturn(int spot) {
			return tRets.get(spot);
		}
		public BloodType getBlood() {
			return blood;
		}
	}
	
	public static Target randTarget(TargetType targetType) {
		return handler.targetList.get(targetTypeTable.get(targetType).random(extra.getRand()));
	}
	
	
	/*
	public static TargetHolder battleSetup(Person p) {
		TargetHolder hold = new TargetHolder(p.getBodyType());
		
		p.setBody(hold);
		
		return hold;
	}*/
	

	/**
	 * for overrides, all of the tables should be the same, so only need to calculate one of them
	 * use masks if a mode doesn't expose all the current targets
	 * @param masks used to filter out all other types if non-null
	 */
	protected static WeightedTable buildTable(List<Target> targets, TargetType[] masks) {
		float[] weights = new float[targets.size()];
		Map<Target,Integer> counts = new HashMap<Target,Integer>();
		//hashmap is needless overkill but idk if the tree thing is better
		for (Target t: targets) {
			counts.put(t,counts.getOrDefault(t, 0)+1);
		}
		for (int i = 0; i < targets.size(); i++) {
			if (masks != null) {
				boolean contains = false;
				TargetType ty = targets.get(i).type;
				for (TargetType tyin: masks) {
					if (ty == tyin) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					continue;
				}
			}
			weights[i] = (float) (targets.get(i).rarity/counts.get(targets.get(i)));
		}
		return new WeightedTable(weights);
	}
	
	private static void toMap(Target t) {
		targetMap.put(t.type.ordinal() + t.name,t);
	}
	
	public static Target fromMap(TargetType type, String name) {
		return targetMap.get(type.ordinal() + name);
	}
	
	//package visibility
	static List<Target> tList(){
		return handler.targetList;
	}
}
