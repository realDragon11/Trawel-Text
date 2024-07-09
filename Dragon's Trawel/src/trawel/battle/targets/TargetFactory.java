package trawel.battle.targets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.battle.attacks.Wound;
import trawel.battle.attacks.ImpairedAttack.DamageType;
import trawel.helper.methods.extra;

public class TargetFactory {

	private List<Target> targetList = new ArrayList<Target>();
	private static TargetFactory handler;
	public static List<Wound> fireWounds = new ArrayList<Wound>();
	public static List<Wound> shockWounds = new ArrayList<Wound>();
	public static List<Wound> freezeWounds = new ArrayList<Wound>();
	
	//public static Map<TargetType,List<Target>> targetTypeMap = new HashMap<TargetType,List<Target>>();
	public static Map<TargetType,WeightedTable> targetTypeTable = new HashMap<TargetType,WeightedTable>();
	private static Map<String,Target> targetMap = new HashMap<String,Target>();
	
	public static final byte TORSO_MAPPING = 3;
	
	/**
	 * 3 slash 1 blunt 1 pierce
	 */
	private void addLeg_LimbWounds(Target t,float weightMult) {
		t.addWound(DamageType.SHARP, Wound.DICE, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.SLICE, weightMult*.5f);
		t.addWound(DamageType.PIERCE, Wound.HAMSTRUNG, weightMult);
		t.addWound(DamageType.PIERCE, Wound.PUNCTURED, weightMult*.5f);
		t.addWound(DamageType.PIERCE, Wound.RUPTURED, weightMult*.5f);
		t.addWound(DamageType.BLUNT, Wound.TRIPPED, weightMult);
		t.addWound(DamageType.BLUNT, Wound.HAMSTRUNG, weightMult*.5f);
		//t.addWound(DamageType.PIERCE, Wound.TAT, .1f);
	}
	
	private void addArm_LimbWounds(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.DICE, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.SLICE, weightMult*.5f);
		t.addWound(DamageType.PIERCE, Wound.DISARMED, weightMult);
		t.addWound(DamageType.PIERCE, Wound.PUNCTURED, weightMult*.5f);
		t.addWound(DamageType.PIERCE, Wound.RUPTURED, weightMult*.5f);
		t.addWound(DamageType.BLUNT, Wound.DISARMED, weightMult);
		//t.addWound(DamageType.PIERCE, Wound.DISARMED, weightMult*.4f);
		//t.addWound(DamageType.PIERCE, Wound.TAT, weightMult*.2f);
	}
	
	private void addBleed_LimbWounds(Target t,float weightMult) {
		addMinorBleed(t,weightMult*.6f);
	}
	

	private void addMinorBleed(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.BLEED, weightMult*1.5f);
		t.addWound(DamageType.BLUNT, Wound.BLEED_BLUNT, weightMult*.3f);
		t.addWound(DamageType.PIERCE, Wound.BLEED, weightMult*.7f);
	}

	private void addMajorBleed(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.MAJOR_BLEED, weightMult*1.5f);
		t.addWound(DamageType.BLUNT, Wound.MAJOR_BLEED_BLUNT, weightMult*.4f);
		t.addWound(DamageType.PIERCE, Wound.MAJOR_BLEED, weightMult*.6f);
	}
	
	/**
	 * adds bleed even to blunt
	 */
	private void addForcedMajorBleed(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.MAJOR_BLEED, weightMult);
		t.addWound(DamageType.BLUNT, Wound.MAJOR_BLEED_BLUNT, weightMult);
		t.addWound(DamageType.PIERCE, Wound.MAJOR_BLEED, weightMult);
	}
	
	private void add_eye_Bloody(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.BLOODY, weightMult);
		t.addWound(DamageType.BLUNT, Wound.BLOODY, weightMult);
		t.addWound(DamageType.PIERCE, Wound.BLOODY, weightMult);
	}
	private void addEyeBlinds(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.BLINDED, weightMult);
		t.addWound(DamageType.BLUNT, Wound.BLINDED, weightMult);
		t.addWound(DamageType.PIERCE, Wound.BLINDED, weightMult);
	}
	
	private void addWingTears(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.TEAR, weightMult);
		t.addWound(DamageType.BLUNT, Wound.TEAR, weightMult);
		t.addWound(DamageType.PIERCE, Wound.TEAR, weightMult);
	}
	
	private void addStatueWounds(Target t) {
		
		t.addWound(DamageType.SHARP, Wound.NEGATED, 3);
		t.addWound(DamageType.BLUNT, Wound.NEGATED, 3);
		t.addWound(DamageType.PIERCE, Wound.NEGATED, 3);
		
		t.addWound(DamageType.SHARP, Wound.CRUSHED, 1);
		t.addWound(DamageType.BLUNT, Wound.CRUSHED, 1);
		t.addWound(DamageType.PIERCE, Wound.CRUSHED, 1);
	}
	
	private void addMangled_Wounds(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.MANGLED, weightMult);
		t.addWound(DamageType.BLUNT, Wound.MANGLED, weightMult);
		t.addWound(DamageType.PIERCE, Wound.MANGLED, weightMult);
	}
	
	private void add_head_Knockout(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.BLINDED, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.DICE, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.SLICE, weightMult*.5f);
		t.addWound(DamageType.BLUNT, Wound.CONFUSED, weightMult);
		t.addWound(DamageType.BLUNT, Wound.KO, weightMult*1.5f);
		t.addWound(DamageType.BLUNT, Wound.DIZZY, weightMult);
		t.addWound(DamageType.PIERCE, Wound.BLINDED, weightMult);
		t.addWound(DamageType.PIERCE, Wound.CONFUSED, weightMult*.5f);
		t.addWound(DamageType.PIERCE, Wound.RUPTURED, weightMult*.5f);
	}
	
	private void add_neck_Winded(Target t, float weightMult) {
		//t.addWound(DamageType.SHARP, Wound.WINDED, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.SLICE, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.DICE, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.FLAYED, weightMult);
		t.addWound(DamageType.BLUNT, Wound.WINDED, weightMult*2f);
		t.addWound(DamageType.BLUNT, Wound.KO, weightMult*.2f);
		//t.addWound(DamageType.PIERCE, Wound.TAT, weightMult);//TODO: pierce impair wound
		t.addWound(DamageType.PIERCE, Wound.PUNCTURED, weightMult*.5f);
		t.addWound(DamageType.PIERCE, Wound.WINDED, weightMult*1f);
	}
	
	private void addChestGeneric(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.SLICE, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.DICE, weightMult*.5f);
		t.addWound(DamageType.SHARP, Wound.HACK, weightMult);
		t.addWound(DamageType.SHARP, Wound.FLAYED, weightMult*.5f);
		t.addWound(DamageType.BLUNT, Wound.WINDED, weightMult*2f);
		t.addWound(DamageType.BLUNT, Wound.CRUSHED, weightMult);
		t.addWound(DamageType.PIERCE, Wound.WINDED, weightMult);
		t.addWound(DamageType.PIERCE, Wound.PUNCTURED, weightMult*1.5f);
		t.addWound(DamageType.PIERCE, Wound.RUPTURED, weightMult*1.5f);
	}
	
	private void addHandGeneric(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.DISARMED, 3f);
		t.addWound(DamageType.BLUNT, Wound.DISARMED, 3f);
		t.addWound(DamageType.PIERCE, Wound.DISARMED, 3f);
	}
	
	private void addGutsGeneric(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.SLICE, weightMult*.4f);
		t.addWound(DamageType.SHARP, Wound.DICE, weightMult*.4f);
		t.addWound(DamageType.SHARP, Wound.HACK, weightMult*2f);
		t.addWound(DamageType.SHARP, Wound.FLAYED, weightMult*2f);
		t.addWound(DamageType.BLUNT, Wound.WINDED, weightMult);
		t.addWound(DamageType.BLUNT, Wound.CRUSHED, weightMult*2f);
		//t.addWound(DamageType.PIERCE, Wound.TAT, weightMult*2f);
		t.addWound(DamageType.PIERCE, Wound.PUNCTURED, weightMult);
		t.addWound(DamageType.PIERCE, Wound.RUPTURED, weightMult);
	}
	
	private void addMimicLid(Target t, float grazeMult, float windedMult, float crushedMult) {
		t.addWound(DamageType.SHARP, Wound.NEGATED, grazeMult);
		t.addWound(DamageType.BLUNT, Wound.NEGATED, grazeMult);
		t.addWound(DamageType.PIERCE, Wound.NEGATED, grazeMult);
		
		t.addWound(DamageType.SHARP, Wound.WINDED, windedMult);
		t.addWound(DamageType.BLUNT, Wound.WINDED, windedMult);
		t.addWound(DamageType.PIERCE, Wound.WINDED, windedMult);
		
		t.addWound(DamageType.SHARP, Wound.CRUSHED, crushedMult);
		t.addWound(DamageType.BLUNT, Wound.CRUSHED, crushedMult);
		t.addWound(DamageType.PIERCE, Wound.CRUSHED, crushedMult);
	}
	
	/**
	 * only includes major non bash bleeds
	 */
	private void add_neck_Bleeds(Target t, float weightMult) {
		t.addWound(DamageType.SHARP, Wound.MAJOR_BLEED, weightMult*1.5f);
		t.addWound(DamageType.BLUNT, Wound.BLEED_BLUNT, weightMult*.1f);
		t.addWound(DamageType.PIERCE, Wound.MAJOR_BLEED, weightMult*.5f);
	}
	
	private void add_head_Bleeds(Target t, float weightMult) {
		addMinorBleed(t,weightMult*.2f);
		addMajorBleed(t,weightMult*.3f);
		t.addWound(DamageType.SHARP, Wound.BLOODY, weightMult*.5f);
		t.addWound(DamageType.BLUNT, Wound.BLOODY, weightMult*.5f);
		t.addWound(DamageType.PIERCE, Wound.BLOODY, weightMult*.5f);
	}
	
	/**
	 * low base chance, but less low for major internal than relative
	 */
	private void addChestBleeds(Target t, float weightMult) {
		addMinorBleed(t,weightMult*.2f);
		t.addWound(DamageType.SHARP, Wound.MAJOR_BLEED, weightMult*.1f);
		t.addWound(DamageType.BLUNT, Wound.MAJOR_BLEED_BLUNT, weightMult*.1f);
		t.addWound(DamageType.PIERCE, Wound.MAJOR_BLEED, weightMult*.1f);
	}
	
	private void addGutsBleeds(Target t, float weightMult) {
		addMinorBleed(t,weightMult*.7f);
	}
	
	private void addHandBleeds(Target t, float weightMult) {
		addMinorBleed(t,weightMult*.5f);
	}
	
	private void set_as_neck(Target t) {
		t.hit = .5;
		t.sharp = 2.5;
		t.blunt = .5;
		t.pierce = 2.5;
		t.rarity = .4;
		t.slot = 0;
		t.mappingNumber = 1;
	}
	
	private void set_as_head(Target t) {
		t.hit = .8;
		t.sharp = .6;
		t.blunt = 2.5;
		t.pierce = 1;
		t.rarity = .6;
		t.slot = 0;
		t.mappingNumber = 1;
		t.condWound = Wound.BRAINED;
	}
	
	private void set_as_arm(Target t) {
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 1;
		t.mappingNumber = 2;
		t.condWound = Wound.MAIMED;
	}
	
	private void set_as_leg(Target t) {
		t.hit = 1;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1;
		t.slot = 3;
		t.mappingNumber = 4;
		t.condWound = Wound.CRIPPLED;
	}
	
	private void set_as_eye(Target t) {
		t.hit = .1;
		t.sharp = 5;
		t.blunt = 3;
		t.pierce = 5;
		t.rarity = .1;
		t.slot = 0;
	}
	
	private void set_as_heart(Target t) {
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 2;
		t.rarity = .4;
		t.slot = 2;
		t.mappingNumber = TORSO_MAPPING;
	}
	
	private void set_as_guts(Target t) {
		t.hit = 1;
		t.sharp = 1.3;
		t.blunt = 1;
		t.pierce = 1.3;
		t.rarity = 1;
		t.slot = 2;
		t.mappingNumber = TORSO_MAPPING;
	}
	
	private void set_as_torso(Target t) {
		t.hit = 1.3;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = 1.5;
		t.slot = 2;
		t.mappingNumber = TORSO_MAPPING;
		t.condWound = Wound.HIT_VITALS;
	}
	
	
	public TargetFactory() {
		handler = this;
		
		fireWounds.add(Wound.SCALDED);
		fireWounds.add(Wound.SCREAMING);
		fireWounds.add(Wound.BLACKENED);
		
		shockWounds.add(Wound.SCREAMING);
		shockWounds.add(Wound.JOLTED);
		shockWounds.add(Wound.STATIC);
		
		freezeWounds.add(Wound.FROSTED);
		freezeWounds.add(Wound.FROSTBITE);
		freezeWounds.add(Wound.SHIVERING);
		
		Target t = new Target();
		t.name = "head";
		set_as_head(t);
		add_head_Bleeds(t, 1);
		add_head_Knockout(t, 1);
		t.type = TargetType.HUMANOID;
		t.finish();
		
		t = new Target();
		t.name = "neck";
		set_as_neck(t);
		t.type = TargetType.HUMANOID;
		add_neck_Bleeds(t, 1);
		add_neck_Winded(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "chest";
		set_as_torso(t);
		t.type = TargetType.HUMANOID;
		addChestGeneric(t, 1);
		addChestBleeds(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right {}","left {}"};
		set_as_arm(t);
		t.type = TargetType.HUMANOID;
		addArm_LimbWounds(t,1f);
		addBleed_LimbWounds(t, 1f);
		t.finish();
		
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
		addHandGeneric(t, 1);
		addHandBleeds(t, 1);
		t.attachNumber = 2;
		//maimed is handled by arm
		t.finish();
		
		t = new Target();
		t.name = "finger";//might be able to?
		t.variants = new String[] {"thumb","index []","middle []","ring []","pinkie []"};
		t.hit = .6;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .05;
		t.slot = 1;
		t.type = TargetType.HUMANOID;
		addMangled_Wounds(t,1);
		t.attachNumber = -2;
		t.passthrough = true;
		t.finish();
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right {}","left {}"};
		set_as_leg(t);
		t.type = TargetType.HUMANOID;
		addLeg_LimbWounds(t,1f);
		addBleed_LimbWounds(t, 1f);
		t.finish();
		
		t = new Target();
		t.name = "foot";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .2;
		t.slot = 4;//slot is different than attached part
		t.type = TargetType.HUMANOID;
		addLeg_LimbWounds(t,1f);
		addBleed_LimbWounds(t, 1f);
		t.attachNumber = 4;
		t.finish();
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right []","left []"};
		set_as_eye(t);
		t.type = TargetType.HUMANOID;
		add_eye_Bloody(t,1);
		t.attachNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "heart";
		set_as_heart(t);
		t.type = TargetType.HUMANOID;
		addForcedMajorBleed(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "guts";
		set_as_guts(t);
		t.type = TargetType.HUMANOID;
		addGutsGeneric(t,1f);
		addGutsBleeds(t,1f);
		t.finish();
		
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
		addMimicLid(t, 1, 2, 1);
		t.mappingNumber = 0;
		t.condWound = Wound.MAIMED;
		t.finish();
		
		t = new Target();
		t.name = "body";
		t.hit = 1.2;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = 2;
		t.slot = 2;
		t.type = TargetType.OPEN_MIMIC;
		addMimicLid(t, 3, 2, 1);
		t.mappingNumber = TORSO_MAPPING;
		t.finish();
		
		
		//open mimic
		t = new Target();
		t.name = "lid";
		t.hit = 1;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = .5;
		t.slot = 1;
		t.type = TargetType.OPEN_MIMIC;
		addMimicLid(t, .5f, 2, 1);
		t.mappingNumber = 0;
		t.condWound = Wound.MAIMED;
		t.finish();
		
		t = new Target();
		t.name = "body";
		t.hit = 1.2;
		t.sharp = .9;
		t.blunt = 1.2;
		t.pierce = .9;
		t.rarity = 2;
		t.slot = 2;
		t.type = TargetType.MIMIC;
		addMimicLid(t, 1, 2, 1);
		t.mappingNumber = TORSO_MAPPING;
		t.condWound = Wound.CRIPPLED;
		t.finish();
		
		t = new Target();
		t.name = "tongue";
		t.hit = .9;
		t.sharp = 2;
		t.blunt = 2;
		t.pierce = 2;
		t.rarity = 3;
		t.slot = 0;
		t.type = TargetType.OPEN_MIMIC;
		addForcedMajorBleed(t, 1);
		t.mappingNumber = 1;
		t.condWound = Wound.I_BLEED;
		t.finish();
		
		
		
		//quad

		t = new Target();
		t.name = "head";
		set_as_head(t);
		t.rarity = 1.2;
		t.type = TargetType.QUAD;
		add_head_Knockout(t, 1f);
		add_head_Bleeds(t, 1f);
		t.finish();
		
		t = new Target();
		t.name = "neck";
		set_as_neck(t);
		t.type = TargetType.QUAD;
		add_neck_Winded(t, 1f);
		add_neck_Bleeds(t, 1f);
		t.finish();
		
		t = new Target();
		t.name = "trunk";//iirc this is quad torso
		set_as_torso(t);
		t.type = TargetType.QUAD;
		addChestBleeds(t, 1f);
		addChestGeneric(t, 1f);
		t.finish();
		
		t = new Target();
		t.variants = new String[] {"right fore{}","left fore{}","right hind{}","left hind{}"};
		t.name = "leg";
		set_as_leg(t);
		t.rarity = 2;
		t.type = TargetType.QUAD;
		addLeg_LimbWounds(t,1f);
		addBleed_LimbWounds(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "paw";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .2;
		t.slot = 2;//slot is different
		t.type = TargetType.QUAD;
		addMangled_Wounds(t,1);
		t.attachNumber = 4;
		t.finish();
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right []","left []"};
		set_as_eye(t);
		t.type = TargetType.QUAD;
		add_eye_Bloody(t,1);
		t.attachNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "heart";
		set_as_heart(t);
		t.type = TargetType.QUAD;
		addForcedMajorBleed(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "guts";
		set_as_guts(t);
		t.type = TargetType.QUAD;
		addGutsGeneric(t, 1);
		addGutsBleeds(t, 1);
		t.finish();
		
		
		//statue
		
		t = new Target();
		t.name = "head";
		set_as_head(t);
		t.condWound = null;
		t.type = TargetType.STATUE;
		addStatueWounds(t);
		t.mappingNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "neck";
		t.hit = .5;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .4;
		t.slot = 0;
		t.type = TargetType.STATUE;
		addStatueWounds(t);
		t.mappingNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "chest";
		set_as_torso(t);
		t.condWound = null;
		t.type = TargetType.STATUE;
		addStatueWounds(t);
		t.finish();
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right {}","left {}"};
		set_as_arm(t);
		t.condWound = null;
		t.type = TargetType.STATUE;
		addStatueWounds(t);
		t.finish();
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right {}","left {}"};
		set_as_leg(t);
		t.condWound = null;
		t.type = TargetType.STATUE;
		addStatueWounds(t);
		t.finish();
		
		//fell reaver standing
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right {}","left {}"};
		set_as_leg(t);
		t.type = TargetType.S_REAVER;
		addLeg_LimbWounds(t,1f);
		addBleed_LimbWounds(t,1f);
		t.finish();
		
		//fell reaver crouched
		
		t = new Target();
		t.name = "head";
		set_as_head(t);
		t.type = TargetType.C_REAVER;
		add_head_Knockout(t, 1);
		add_head_Bleeds(t, 0.5f);
		t.finish();
		
		t = new Target();
		t.name = "neck";
		t.hit = .5;
		t.sharp = 3;
		t.blunt = .5;
		t.pierce = 3;
		t.rarity = .4;
		t.slot = 0;
		t.type = TargetType.C_REAVER;
		add_neck_Winded(t, 1);
		add_neck_Bleeds(t, .5f);
		t.mappingNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "chest";
		set_as_torso(t);
		t.type = TargetType.C_REAVER;
		addChestGeneric(t, 1);
		addChestBleeds(t, 0.5f);
		t.finish();
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right {}","left {}"};
		set_as_arm(t);
		t.type = TargetType.C_REAVER;
		addArm_LimbWounds(t,1f);
		addBleed_LimbWounds(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right {}","left {}"};
		set_as_leg(t);
		t.type = TargetType.C_REAVER;
		addArm_LimbWounds(t,1f);
		addBleed_LimbWounds(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right {}","left {}"};
		set_as_eye(t);
		t.type = TargetType.C_REAVER;
		add_eye_Bloody(t,1);
		t.attachNumber = 1;
		t.finish();
		
		//undead
		t = new Target();
		t.name = "head";
		set_as_head(t);
		t.condWound = Wound.GLOW;
		t.type = TargetType.UNDEAD_H;
		add_head_Knockout(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "neck";
		t.hit = .5;
		t.sharp = 3;
		t.blunt = .5;
		t.pierce = 3;
		t.rarity = .4;
		t.slot = 0;
		t.type = TargetType.UNDEAD_H;
		add_neck_Winded(t, 1);
		t.mappingNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "chest";
		set_as_torso(t);
		t.condWound = Wound.SHINE;
		t.type = TargetType.UNDEAD_H;
		addChestGeneric(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right {}","left {}"};
		set_as_arm(t);
		t.condWound = Wound.GLOW;
		t.type = TargetType.UNDEAD_H;
		addArm_LimbWounds(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right {}","left {}"};
		set_as_leg(t);
		t.condWound = Wound.GLOW;
		t.type = TargetType.UNDEAD_H;
		addLeg_LimbWounds(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right []","left []"};
		set_as_eye(t);
		t.condWound = Wound.GLOW;
		t.type = TargetType.UNDEAD_H;
		addEyeBlinds(t,1);
		t.finish();
		
		t = new Target();
		t.name = "guts";
		set_as_guts(t);
		t.condWound = Wound.SHINE;
		t.type = TargetType.UNDEAD_H;
		addGutsGeneric(t, 1);
		t.finish();
		
		//flying
		t = new Target();
		t.name = "head";
		set_as_head(t);
		t.type = TargetType.FLY;
		add_head_Knockout(t, 1);
		add_head_Bleeds(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "chest";
		set_as_torso(t);
		t.type = TargetType.FLY;
		addChestGeneric(t, 1);
		addChestBleeds(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "wing";
		t.variants = new String[] {"right {}","left {}"};
		t.hit = 2;
		t.sharp = 1.1;
		t.blunt = .8;
		t.pierce = .8;
		t.rarity = 1.5;
		t.slot = 1;
		t.type = TargetType.FLY;
		addWingTears(t,1f);
		t.mappingNumber = 2;
		t.condWound = Wound.CRIPPLED;
		t.finish();
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right {}","left {}"};
		set_as_leg(t);
		t.condWound = Wound.TEAR;
		t.type = TargetType.FLY;
		addMinorBleed(t,.5f);
		addWingTears(t,.3f);
		addMangled_Wounds(t,.5f);
		t.finish();
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right {}","left {}"};
		set_as_eye(t);
		t.type = TargetType.FLY;
		add_eye_Bloody(t,1);
		t.attachNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "heart";
		set_as_heart(t);
		t.type = TargetType.FLY;
		addForcedMajorBleed(t,1);
		t.finish();
		
		t = new Target();
		t.name = "guts";
		set_as_guts(t);
		t.type = TargetType.FLY;
		addGutsGeneric(t,1f);
		addGutsBleeds(t,1f);
		t.finish();
		
		
		//demon
		
		t = new Target();
		t.name = "head";
		set_as_head(t);
		t.type = TargetType.DEMON;
		add_head_Knockout(t, 1);
		t.finish();
		
		t = new Target();
		t.name = "skull";
		t.rarity = 0;//no chance, only passthrough for the two horns
		t.condWound = Wound.DEPOWER;
		t.type = TargetType.DEMON;
		//mostly unused
		addMangled_Wounds(t,1);
		t.hit = 1;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.slot = 0;
		t.attachNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "horn";
		t.variants = new String[] {"right []","left []"};
		t.hit = 1;
		t.sharp = .5;
		t.blunt = 2;
		t.pierce = .5;
		t.rarity = 1;
		t.slot = 1;
		t.type = TargetType.DEMON;
		addMangled_Wounds(t,1);//used to damage skull and inflict the depower wound
		t.attachNumber = -1;
		t.passthrough = true;
		t.finish();
		
		t = new Target();
		t.name = "torso";
		set_as_torso(t);
		t.type = TargetType.DEMON;
		addChestGeneric(t,1f);
		addChestBleeds(t,.5f);//torso is one of the few places demons can bleed from
		t.finish();
		
		t = new Target();
		t.name = "arm";
		t.variants = new String[] {"right {}","left {}"};
		set_as_arm(t);
		t.type = TargetType.DEMON;
		addArm_LimbWounds(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "claw";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .3;
		t.slot = 1;
		t.type = TargetType.DEMON;
		addHandGeneric(t, 1);
		t.attachNumber = 2;
		//maimed is handled by arm
		t.finish();
		
		t = new Target();
		t.name = "leg";
		t.variants = new String[] {"right {}","left {}"};
		set_as_leg(t);
		t.type = TargetType.DEMON;
		addLeg_LimbWounds(t,1f);
		t.finish();
		
		t = new Target();
		t.name = "hoof";
		t.hit = .8;
		t.sharp = 1;
		t.blunt = 1;
		t.pierce = 1;
		t.rarity = .2;
		t.slot = 4;//slot is different than attached part
		t.type = TargetType.DEMON;
		addLeg_LimbWounds(t,1f);
		t.attachNumber = 4;
		t.finish();
		
		t = new Target();
		t.name = "eye";
		t.variants = new String[] {"right []","left []"};
		set_as_eye(t);
		t.type = TargetType.DEMON;
		add_eye_Bloody(t, 1);
		//can bleed from eyes
		t.attachNumber = 1;
		t.finish();
		
		t = new Target();
		t.name = "darkness";//equal to heart
		set_as_heart(t);
		t.type = TargetType.DEMON;
		addForcedMajorBleed(t, 1);
		t.finish();
		
		
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
			//dispPlan(tb);
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
		HUMANOID, MIMIC, OPEN_MIMIC, NONE,QUAD, STATUE, S_REAVER, C_REAVER, UNDEAD_H, FLY, DEMON;
	}
	
	public enum BodyPlan {
		HUMANOID, QUAD, FLY, NO_VARIANTS_ALL, DEMON;
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
		UNDEAD(BodyPlan.HUMANOID,BloodType.NONE,TargetType.UNDEAD_H),
		DEMON(BodyPlan.DEMON,BloodType.NORMAL,TargetType.DEMON)
		;
		
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
		private int[] map;//MAYBELATER???: do an int[][] where the first one is the actual map
		//and the others are linked maps
		//should probably compute chains at compile time to avoid recursion issues
		//but in such a case the mapping would be useless and should be reduced anyway?
		private int[] attached;
		
		protected Wound[] condWounds;
		
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
			uniqueParts = 0;
			for (int i = 0; i < allTargets.size();i++) {
				Target t = allTargets.get(i);
				if (t.mappingNumber == 0 && t.attachNumber == 0) {
					singles.add(t);
					uniqueParts++;
				}else {
					int special = getSpecialNum(t,allVariants.get(i));
					if (t.mappingNumber > 0) {
						int index = multiNums.indexOf(special);
						Integer variNum = allVariants.get(i).variant;
						if (index == -1) {
							multiNums.add(special);
							subTargets.add(new ArrayList<Target>());
							subVariants.add(new ArrayList<Integer>());
							if (variNum == -1) {//if we don't have variants, set 1
								multiCount.add(1);
								uniqueParts++;
							}else {
								multiCount.add(0);
							}
							index = multiNums.size()-1;
						}
						if (variNum != -1 && !subVariants.get(index).contains(variNum)) {//add all variants
							multiCount.set(index,multiCount.get(index) + 1);
							uniqueParts++;
						}
						subTargets.get(index).add(t);
						subVariants.get(index).add(variNum);
					}else {
						multiNums.add(special);//DOLATER might be a better way
						if (t.passthrough) {
							multiCount.add(0);
						}else {
							multiCount.add(1);
							uniqueParts++;
						}
						subTargets.add(null);
						subVariants.add(null);
					}
					
				}
			}
			offset = singles.size();
			//uniqueParts = offset + multiSum(multiNums.size())+1;//DOLATER off by one error???
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
						if (t.passthrough) {
							map[i] = -1;//need to do it in a later pass
						}else {
							VariantResolver vr = allVariants.get(i);
							int index = multiNums.indexOf(getSpecialNum(t,vr));
							int a = mapNumForAttach(offset,index,vr.combo);
							map[i] = a;
							//extra.println(t.name + i + " " +a + " " + map[i]);
						}
					}
				}else {
					int index = multiNums.indexOf(getSpecialNum(t,null));
					int subindex;
					if (t.variants == null) {
						subindex = 0;
					}else {
						subindex = subVariants.get(index).indexOf(allVariants.get(i).variant);
					}
					map[i] = offset + multiSum(index) + subindex;
				}
			}
			//two pass makes this much easier to write from this point, probably could have done it better
			
			condWounds = new Wound[uniqueParts];
			
			for (int i = 0;i < attached.length;i++) {
				Target t = allTargets.get(i);
				if (t.condWound != null && condWounds[map[i]] == null) {//just get the first one at this point
					condWounds[map[i]] = t.condWound;
				}
				if (t.attachNumber == 0) {
					attached[i] = -1;
					//tRets.add(new TargetReturn(t,allVariants.get(i),map[i],i,-1));
				}else {
					attached[i] = -1;
					VariantResolver vr = allVariants.get(i);
					
					int raw = t.attachNumber;
					if (raw < 0) {//attach chain
						//not a very effective way, but it should work.
						int[] copy = new int[vr.vAttaches.length-1];
						for (int j = 0; j < vr.vAttaches.length-1;j++) {
							copy[j] = vr.vAttaches[j];
						}
						for (int j = 0; j < allVariants.size();j++) {
							Target t2 = allTargets.get(j);
							VariantResolver subvr = allVariants.get(j);
							if (t2.attachNumber != -raw) {
								continue;
							}
							boolean isSame = true;
							for (int k = 0;k < Math.max(subvr.vAttaches.length,copy.length);k++) {
								if (subvr.vAttaches[k] != copy[k]) {
									isSame = false;
									break;
								}
							}
							//assert isSame == subvr.vAttaches.equals(copy);//apparently something weird makes these not equal
							if (isSame) {
								attached[i] = j;//map[j];
								//tRets.add(new TargetReturn(t,vr,map[i],i,attached[i]));
								break;
							}
						}
						if (attached[i] >=0) {
							continue;
						}
						throw new RuntimeException(this.name()+ "could not find attach "+raw+" to attach to");
					}else {//to a base map
						for (int j = 0; j < allTargets.size();j++) {
							Target t2 = allTargets.get(j);
							if (t2.mappingNumber == raw) {
								VariantResolver subvr = allVariants.get(j);
								int subvariants = t.variants == null ? 1 : t.variants.length;
								if (vr.vAttaches[0] != subvr.variant) {//%subvariants
									continue;//not the one we need
								}
								attached[i] = j;//map[j];//connect to base map
								//tRets.add(new TargetReturn(t,vr,map[i],i,attached[i]));
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
			//third pass :DDDDD
			//which is actually any number of passes D:
			boolean canEnd = false;
			while (!canEnd) {
				canEnd = true;
				for (int i = 0;i < map.length;i++) {
					if (map[i] == -1) {
						if (map[attached[i]] == -1) {
							canEnd = false;
							continue;
						}else {
							map[i] = map[attached[i]];
						}
					}
				}
			}
			for (int i = 0; i < map.length;i++) {
				tRets.add(new TargetReturn(allTargets.get(i),allVariants.get(i),map[i],i,attached[i]));
			}
		}
		
		protected int getSpecialNum(Target t,VariantResolver vr) {
			return (t.mappingNumber > 0 ? t.mappingNumber : t.attachNumber + ((vr.combo+1)*1_000));
		}
		
		protected int getSlotByMappingNumber(int mapping,int variant) {
			for (int i = 0;i < allTargets.size();i++) {
				if (allTargets.get(i).mappingNumber == mapping) {
					return map[i];//why did I even try to complicate it
				}
			}
			return -1;//create issues
			/*
			int multIndex = multiNums.indexOf(mapping);
			if (multIndex == -1) {
				return -1;
			}
			return map[multIndex];
			/*
			int total = multiSum(multiNums.size()-1);
			return (allTargets.size() - total)+multiSum(multIndex)+variant;*/
		}
		
		protected int mapNumForAttach(int offset, int index, int combo) {
			return offset + multiSum(index);//combo part of multisum
			//-1 because that's the end of the arrlist
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
		
		public boolean isRoot(int spot) {
			return attached[spot] == -1;
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
			public final int slot;
			public final int spot;
			public final int attachSpot;
			
			public TargetReturn(Target tar, VariantResolver vari, int mapLoc, int spot, int attachSpot){
				this.variant = vari;
				this.tar = tar;
				this.slot = mapLoc;
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
			return map[spot];
		}
		
		public int getAttach(int spot) {
			return attached[spot];
		}
		public TargetReturn getTargetReturn(int spot) {
			return tRets.get(spot);
		}
		public BloodType getBlood() {
			return blood;
		}
	}
	
	public static void dispPlan(TypeBody plan) {
		for (int i = 0; i < plan.getPartCount();i++) {
			extra.println(i + " m" + plan.getMap(i) + " a: " +plan.getAttach(i) + " " + plan.spotName(i));
		}
	}
	
	public static Target randTarget(TargetType targetType) {
		return handler.targetList.get(targetTypeTable.get(targetType).random(extra.getRand()));
	}
	
	public static int totalNeededVariants(TargetType type, int attach) {
		for (Target subt: TargetFactory.tList()) {
			if (subt.type == type && (subt.mappingNumber == attach || subt.attachNumber == -attach)) {
				return (subt.variants != null ? subt.variants.length : 1)
						* (subt.attachNumber == 0 ? 1 : totalNeededVariants(type,subt.attachNumber));
			}
		}
		throw new RuntimeException("attach part not found");
	}
	
	public static List<Target> neededVariants(TargetType type, int attach) {
		for (Target subt: TargetFactory.tList()) {
			if (subt.type == type && (subt.mappingNumber == attach || subt.attachNumber == -attach)) {
				if (subt.attachNumber == 0) {
					List<Target> list = new ArrayList<Target>();
					list.add(subt);
					return list;
				}else {
					List<Target> list = neededVariants(type,subt.attachNumber);
					//list.add(0,subt);//add to base of list
					list.add(subt);
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
						String holdReplaceName = null;
						String strLast = null;
						String strCur = "";
						int passing = -1;
						int[] vAttaches = new int[list.size()];
						for (int j = 0;j < list.size(); j++) {
							Target subT = list.get(j);
							boolean lastPass = (j == list.size()-1);
							if (lastPass) {
								strCur = (subT.variants == null ? subT.name : subT.variants[moduloTarget(subT,i)]);
							}else {
								strCur= namePermutation(list.get(j),i);
								if (passing >= 0) {
									passing++;
								}else {
									passing = strCur.contains("{}") ? 0 : -1;
								}
							}
							
							strCur = strCur.replaceAll(Pattern.quote("[]"),subT.name + (!lastPass ? "'s " : ""));
							if (passing < 0 || (lastPass && strLast == null)) {
								str += strCur.replaceAll(Pattern.quote("{}"),subT.name + (!lastPass ? "'s " : ""));//idk if 's will ever trigger here rn
							}else {
								if ((strCur.equals(strLast) || passing == 0)
										&& !lastPass) {
									//pass down
									holdReplaceName = subT.name;
								}else {
									//different
									if (subT.variants == null || holdReplaceName == null) {
										strCur = strLast.replaceAll(Pattern.quote("{}"),subT.name + (!lastPass ? "'s " : ""));
										passing = -1;
									}else {
										strCur = (strLast + strCur).replaceAll(Pattern.quote("{}"),holdReplaceName + (!lastPass ? "'s " : ""));
										passing = -1;
									}
									
									holdReplaceName = subT.name;
									str+=strCur;
								}
							}
							
							strLast = strCur;
							
							vAttaches[j] = (subT.variants == null ? -1 : moduloTarget(subT,i));
						}
						targets.add(t);
						variants.add(new VariantResolver(str,i,vAttaches));
					}
					
				}else {
					for (int i = 0; i < t.variants.length;i++) {
						targets.add(t);
						variants.add(new VariantResolver(i,t.variants[i].replaceAll(Pattern.quote("[]"),t.name).replaceAll(Pattern.quote("{}"),t.name)));
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
		return (t.variants == null ? "" : t.variants[moduloTarget(t,toMod)]);
		//return (t.variants == null ? "" : t.variants[moduloTarget(t,toMod)] +t.name+"'s ");
	}
	
	public static int moduloTarget(Target t, int i) {
		return i%(t.variants == null ? 1 : t.variants.length);
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
	
	public static void finishTarget(Target t) {
		if (!handler.targetList.contains(t)) {
			handler.targetList.add(t);
		}
	}
}
