package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.battle.attacks.Attack.Wound;
import trawel.battle.attacks.TargetFactory.TargetType;
import trawel.battle.attacks.TargetFactory.TypeBody;

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
		t.mappingNumber = 1;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 2;
		targetList.add(t);
		
		t = new Target();
		t.name = "hand";
		//attached to arms, so no variants needed
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .3;
		t.slot = 1;
		t.type = TargetType.HUMANOID;
		t.slashWounds.add(Attack.Wound.DISARMED);
		t.bluntWounds.add(Attack.Wound.DISARMED);
		t.pierceWounds.add(Attack.Wound.DISARMED);
		t.attachNumber = 2;
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
		t.mappingNumber = 4;
		targetList.add(t);
		
		t = new Target();
		t.name = "foot";
		t.variants = new String[] {"ty a ","np b "};
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .2;
		t.slot = 4;//slot is different than attached part
		t.type = TargetType.HUMANOID;
		addLeg_LimbWounds(t,null);
		t.attachNumber = 4;
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
		t.attachNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 1;
		targetList.add(t);
		
		t = new Target();
		t.name = "chest";
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 3;
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
		t.slot = 4;
		t.type = TargetType.QUAD;
		addLeg_LimbWounds(t,null);
		t.mappingNumber = 4;
		targetList.add(t);
		
		t = new Target();
		t.name = "paw";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .2;
		t.slot = 2;//slot is different
		t.type = TargetType.QUAD;
		addLeg_LimbWounds(t,null);
		t.attachNumber = 4;
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
		t.attachNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 4;
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
		t.mappingNumber = 4;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 4;
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
		t.attachNumber = 1;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 4;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 2;
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
		t.mappingNumber = 4;
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
		t.attachNumber = 1;
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
		t.mappingNumber = 3;
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
		t.mappingNumber = 3;
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
		private List<VariantResolver> allVariants;
		
		private List<Integer> multiNums;
		private List<Integer> multiCount;
		
		/**
		 * how many 'single' non-multi cond slots there are
		 */
		int offset;
		
		//private List<VariantResolver> attachNums;
		
		private List<TargetReturn> tRets;
		/**
		 * links a type/variant to a final part
		 * used mostly for mapping to different parts
		 * otherwise will just be counting from 0 to size-1
		 */
		private int[] map;//FIXME: do an int[][] where the first one is the actual map
		//and the others are linked maps
		//should probably compute chains at compile time to avoid recursion issues
		//but in such a case the mapping would be useless and should be reduced anyway?
		private int[] attached;
		
		TypeBody(BodyPlan plan, BloodType blood, TargetType... types){
			this.plan = plan;
			this.blood = blood;
			this.types = types;
		}
		//could use another function to call if needs to have different behavior
		public void setup() {
			tRets = new ArrayList<TargetReturn>();
			backmap = handler.makeMap(this);
			tables = new WeightedTable[types.length+1];
			allTargets = new ArrayList<Target>();
			allVariants = new ArrayList<VariantResolver>();
			for (int i = 0;i < backmap.get(0).size();i++) {
				allTargets.add((Target)backmap.get(0).get(i));
				allVariants.add((VariantResolver)backmap.get(1).get(i));
			}
			tables[0] = buildTable(allTargets, null);//table of 'all', used for null masks
			for (int i = 1; i < tables.length-1;i++) {
				tables[i] = buildTable(allTargets, new TargetType[]{types[i-1]});
			}
			List<Target> singles = new ArrayList<Target>();
			multiNums = new ArrayList<Integer>();
			//attachNums = new ArrayList<VariantResolver>();
			List<List<Target>> subTargets = new ArrayList<List<Target>>();
			List<List<Integer>> subVariants = new ArrayList<List<Integer>>();
			multiCount = new ArrayList<Integer>();
			for (int i = 0; i < allTargets.size();i++) {
				Target t = allTargets.get(i);
				if (t.mappingNumber == 0 && t.attachNumber == 0) {
					singles.add(t);
				}else {
					int special = getSpecialNum(t,allVariants.get(i));
					if (t.mappingNumber > 0) {
						int index = multiNums.indexOf(special);
						Integer variNum = allVariants.get(i).variant;
						if (index == -1) {
							multiNums.add(special);
							subTargets.add(new ArrayList<Target>());
							subVariants.add(new ArrayList<Integer>());
							multiCount.add(variNum == -1 ? 1 : 0);//if we don't have variants, set 1
							index = multiNums.size()-1;
						}
						if (variNum != -1 && !subVariants.get(index).contains(variNum)) {//add all variants
							multiCount.set(index,multiCount.get(index) + 1);
						}
						subTargets.get(index).add(t);
						subVariants.get(index).add(variNum);
					}else {
						/*
						while (multiNums.indexOf(special) != -1) {
							special += 1_000;
						}*/
						multiNums.add(special);//DOLATER might be a better way
						multiCount.add(1);
						subTargets.add(null);
						subVariants.add(null);
						//attachNums.add(allVariants.get(i));
					}
					
				}
			}
			offset = singles.size();
			uniqueParts = offset + multiSum(multiNums.size());
			map = new int[allTargets.size()];
			attached = new int[allTargets.size()];
			//the maps converts an alltarget number to a condition arr number
			//the outcome numbers don't matter as long as they're consistent, so we make them here
			for (int i = 0;i < map.length;i++) {
				Target t = allTargets.get(i);
				if (t.mappingNumber == 0) {
					if (t.attachNumber == 0) {
						map[i] = singles.indexOf(t);
					}else {
						VariantResolver vr = allVariants.get(i);
						int index = multiNums.indexOf(getSpecialNum(t,vr));
						map[i] = mapNumForAttach(offset,multiSum(index),vr.combo);
					}
				}else {
					int index = multiNums.indexOf(getSpecialNum(t,null));
					int subindex;
					if (t.variants == null) {
						subindex = 0;
					}else {
						subindex = subVariants.get(index).indexOf(allVariants.get(i).variant);
					}
					//very fancy system to make each variant mappable
					map[i] = offset + multiSum(index) + subindex;
					//offset = singlets
					//multisum gets all the prior counts of multis taking up space
					//the last bit gets our value within the current multi
					//System.err.println(subindex +" " +subVariants.get(index).get(subindex));
				}
			}
			//two pass makes this much easier to write from this point, probably could have done it better
			for (int i = 0;i < attached.length;i++) {
				Target t = allTargets.get(i);
				if (t.attachNumber == 0) {
					attached[i] = -1;
					tRets.add(new TargetReturn(t,allVariants.get(i),map[i],i,-1));
				}else {
					attached[i] = -1;
					VariantResolver vr = allVariants.get(i);
					
					int raw = t.attachNumber;
					if (raw < 0) {//attach chain
						int special = getSpecialNum(t,vr);
						int index = multiNums.indexOf(special);
						attached[i] = mapNumForAttach(offset, index, vr.combo);
						assert attached[i] >= 0 && attached[i] < attached.length;
						tRets.add(new TargetReturn(t,allVariants.get(i),map[i],i,attached[i]));
						continue;
					}else {//to a base map
						for (int j = 0; j < allTargets.size();j++) {
							if (allTargets.get(j).mappingNumber == raw) {
								attached[i] = map[j];//connect to base map
								tRets.add(new TargetReturn(t,allVariants.get(i),map[i],i,attached[i]));
								break;
							}
						}
						if (attached[i] >=0) {
							continue;
						}
						throw new RuntimeException(this.name()+ "could not find base "+raw+" map to attach to");
					}
				}
			}
		}
		
		protected int getSpecialNum(Target t,VariantResolver vr) {
			return (t.mappingNumber > 0 ? t.mappingNumber : t.attachNumber + ((vr.combo+1)*1_000));
		}
		
		protected int getSlotByMappingNumber(int mapping,int variant) {
			int multIndex = multiNums.indexOf(mapping);
			if (multIndex == -1) {
				return -1;
			}
			int total = multiSum(multiNums.size()-1);
			return (allTargets.size() - total)+multiSum(multIndex)+variant;
		}
		
		protected int mapNumForAttach(int offset, int index, int combo) {
			return offset + multiSum(index-1)-1;//combo part of multisum
		}
		
		/*
		protected int getBaseMappingNumber(VariantResolver vr) {
			
		}
		*/
		
		/**
		 * returns the sum of the ints inside nums up to the array index of upTo
		 */
		private int multiSum(int upTo) {
			int total = 0;
			for (int i = 0; i < upTo;i++) {
				total+=multiCount.get(i);
			}
			return total;
		}
		
		public int refParts(int slot) {
			return (slot < offset ? 1 : multiCount.get(slot-offset));
		}
		
		/**
		 * slow and recursive, debug use only
		 * can do refPartsFrom-refParts to get child count
		 */
		public int refPartsFrom(int slot) {
			int total = 0;
			for (int i = 0; i < allVariants.size();i++) {
				TargetReturn tr = tRets.get(i);
				if (tr.attachSpot == slot) {
					total += refPartsFrom(tr.spot);
				}else {
					if (tr.spot == slot) {
						total++;
					}
				}
			}
			return total;
		}
		/**
		 * fairly slow
		 */
		public List<TargetReturn> getDirectChildren(int spot){
			List<TargetReturn> list = new ArrayList<TargetReturn>();
			for (int i = 0; i < tRets.size();i++) {
				TargetReturn tr = tRets.get(i);
				if (tr.attachSpot == spot) {
					list.add(tr);
				}
			}
			return list;
		}
		
		public List<TargetReturn> getForSlot(int slot){
			List<TargetReturn> list = new ArrayList<TargetReturn>();
			for (int i = 0; i < tRets.size();i++) {
				TargetReturn tr = tRets.get(i);
				if (getMap(tr.spot) == slot) {
					list.add(tr);
				}
			}
			return list;
		}
		
		public boolean isRoot(int slot) {
			return tRets.get(slot).attachSpot == -1;
		}
		
		public List<Integer> rootSlots(){
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < map.length;i++) {
				if (attached[i] == -1 && list.indexOf(map[i]) == -1) {
					list.add(map[i]);
				}
			}
			return list;
		}
		
		public List<TargetReturn> rootTargetReturns(){
			List<TargetReturn> list = new ArrayList<TargetReturn>();
			for (int i = 0; i < map.length;i++) {
				if (attached[i] == -1) {
					list.add(getTargetReturn(i));
				}
			}
			return list;
		}
		
		public int getUniqueParts() {
			return uniqueParts;
		}
		
		public int getPartCount() {
			return allTargets.size();
		}
		
		//were added later so don't get used when passing arrays around
		//the arrays work better for certain internal things anyway
		public class TargetReturn{
			public final VariantResolver variant;
			public final Target tar;
			public final int mapLoc;
			public final int spot;
			public final int attachSpot;
			
			public TargetReturn(Target tar, VariantResolver vari, int mapLoc, int spot, int attachSpot){
				this.variant = vari;
				this.tar = tar;
				this.mapLoc = mapLoc;
				this.spot = spot;
				this.attachSpot = attachSpot;
			}
			
			public String getName() {
				return variant.name;//(variant.variant >= 0 ? tar.variants[variant.variant] : "" )+ tar.name;
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
		
		public int getAttach(int spot) {
			return tRets.get(spot).attachSpot;
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
	
	public static int totalNeededVariants(TargetType type, int attach) {
		for (Target subt: TargetFactory.tList()) {
			if (subt.type == type && subt.mappingNumber == attach) {
				return (subt.variants != null ? subt.variants.length : 1)
						* (subt.attachNumber == 0 ? 1 : totalNeededVariants(type,subt.attachNumber));
			}
		}
		throw new RuntimeException("attach part not found");
	}
	
	public static List<Target> neededVariants(TargetType type, int attach) {
		for (Target subt: TargetFactory.tList()) {
			if (subt.type == type && subt.mappingNumber == attach) {
				if (subt.attachNumber == 0) {
					List<Target> list = new ArrayList<Target>();
					list.add(subt);
					return list;
				}else {
					List<Target> list = neededVariants(type,subt.attachNumber);
					list.add(0,subt);//add to base of list
					return list;
				}
			}
		}
		throw new RuntimeException("attach part not found");
	}
	
	/**
	 * constructs an array of targets with variants
	 * the variants are ints, you must fetch them from the strings
	 * variants will be null if it's a singlet
	 * @param typeBody 
	 * @return the array of parts, Object[2][count] where Target and Integer are sub arrays
	 */
	protected List<List<Object>> makeMap(TypeBody typeBody) {
		List<Object> targets = new ArrayList<Object>();
		List<Object> variants = new ArrayList<Object>();
		for (Target t: TargetFactory.tList()) {
			boolean contains = false;
			for (TargetType taty: typeBody.types) {
				if (t.type == taty) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				continue;
			}
			
			if (t.variants == null && t.attachNumber == 0) {
				targets.add(t);
				variants.add(new VariantResolver(-1,t.name));
			}else {
				int variantNum = (t.variants == null ? 1 : t.variants.length);
				if (t.attachNumber != 0) {
					List<Target> list = TargetFactory.neededVariants(t.type, t.attachNumber);
					list.add(t);
					int total = variantNum*TargetFactory.totalNeededVariants(t.type,t.attachNumber);
					for (int i = 0; i < total;i++) {
						String str = "";
						int[] vAttaches = new int[list.size()];
						for (int j = 0;j < list.size(); j++) {
							Target subT = list.get(j);
							if (j == list.size()-1) {
								str += (subT.variants == null ? subT.name : subT.variants[i%subT.variants.length] +subT.name);
							}else {
								str += namePermutation(list.get(j),i);
							}
							vAttaches[j] = (subT.variants == null ? -1 : i%subT.variants.length);
						}
						targets.add(t);
						variants.add(new VariantResolver(str,i,vAttaches));
					}
					
				}else {
					int j = 0;
					for (int i = 0; i < t.variants.length;i++) {
						targets.add(t);
						variants.add(new VariantResolver(i,t.variants[i]+ t.name));
					}
				}
			}
		}
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(targets);
		list.add(variants);
		return list;
	}
	public static String namePermutation(Target t,int toMod) {
		return (t.variants == null ? "" : t.variants[toMod%t.variants.length] +t.name+"'s ");
	}
	
	public class VariantResolver{
		public final int variant;
		public final String name;
		//public final int[] attaches;
		public final int[] vAttaches;
		public final int combo;
		public VariantResolver(int variant, String name) {
			this.variant = variant;
			this.name = name;
			//attaches = null;
			vAttaches = null;
			combo = Math.min(0,variant);
		}
		public VariantResolver(String name, int combo, int[] vAttaches) {
			this.variant = vAttaches[vAttaches.length-1];
			this.name = name;
			assert vAttaches[vAttaches.length-1] == variant;
			this.vAttaches = vAttaches;
			this.combo = combo;
		}
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
