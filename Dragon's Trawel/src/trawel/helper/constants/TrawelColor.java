package trawel.helper.constants;

import java.awt.Color;

import trawel.core.Print;
import trawel.helper.methods.extra;

public class TrawelColor {
	//MAYBELATER: could have trawel colors be their own class, and just have the toString be the color code, which would make passing them around
	//easier in some cases but harder in others?

	public static String COLOR_SHARP = TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.4f));
	public static String COLOR_BLUNT = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.WHITE,.4f));
	public static String COLOR_PIERCE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.ORANGE,Color.WHITE,.4f));
	public static String COLOR_IGNITE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.2f));
	public static String COLOR_FROST = TrawelColor.inlineColor(TrawelColor.colorMix(Color.CYAN,Color.WHITE,.4f));
	public static String COLOR_ELEC = TrawelColor.inlineColor(TrawelColor.colorMix(Color.YELLOW,Color.WHITE,.4f));
	public static String COLOR_DECAY = TrawelColor.inlineColor(TrawelColor.colorMix(Color.MAGENTA,Color.WHITE,.4f));
	public static final String COLOR_RESET = "[reset_color]";
	//NOTE: predefined color mixes inlined
	public static final String PRE_WHITE = TrawelColor.inlineColor(Color.WHITE);
	public static final String PRE_RED = TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.5f));
	public static final String PRE_BATTLE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.4f));
	public static final String PRE_MAYBE_BATTLE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.6f));
	public static final String PRE_ORANGE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.ORANGE,Color.WHITE,.5f));
	public static final String PRE_YELLOW = TrawelColor.inlineColor(TrawelColor.colorMix(Color.YELLOW,Color.WHITE,.5f));
	public static final String PRE_BLUE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.WHITE,.5f));
	public static final String PRE_GREEN = TrawelColor.inlineColor(TrawelColor.colorMix(Color.GREEN,Color.WHITE,.5f));
	public static final String PRE_MAGENTA = TrawelColor.inlineColor(TrawelColor.colorMix(Color.MAGENTA,Color.WHITE,.5f));
	public static final String PRE_ROAD = PRE_GREEN;
	public static final String PRE_SHIP = PRE_GREEN;
	public static final String PRE_TELE = PRE_GREEN;
	//timid colors that are slight, used for bad and good hinting
	public static final String TIMID_GREEN = TrawelColor.inlineColor(TrawelColor.colorMix(Color.GREEN,Color.WHITE,.7f));
	public static final String TIMID_RED = TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.WHITE,.7f));
	/** 
	 * do not use for no change whatsoever, use white for that- this is a change that might be bad or good but is a net 0 to this stat
	 */
	public static final String TIMID_GREY = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLACK,Color.WHITE,.95f));
	//not directly used yet
	public static final String TIMID_BLUE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.WHITE,.7f));
	public static final String TIMID_MAGENTA = TrawelColor.inlineColor(TrawelColor.colorMix(Color.MAGENTA,Color.WHITE,.8f));
	public static final String TIMID_ORANGE = TrawelColor.inlineColor(TrawelColor.colorMix(Color.ORANGE,Color.WHITE,.7f));
	public static final String RESULT_NO_CHANGE_GOOD = TIMID_BLUE;
	public static final String RESULT_NO_CHANGE_BAD = TIMID_ORANGE;
	public static final String RESULT_NO_CHANGE_NONE = TIMID_GREY;
	public static final String RESULT_GOOD = TIMID_GREEN;
	public static final String RESULT_BAD = TIMID_RED;
	/**
	 * warn that this might be destructive
	 */
	public static final String RESULT_WARN = TIMID_MAGENTA;
	/**
	 * inform that nothing happened due to bad setup
	 */
	public static final String RESULT_ERROR = TIMID_RED;
	/**
	 * when an action fails, usually due to chance, or had a negative outcome
	 */
	public static final String RESULT_FAIL = TIMID_ORANGE;
	/**
	 * that the action went through and had the intended outcome
	 * <br>
	 * usually used if it was chance based, or merely accumulating progress towards a true RESULT_GOOD
	 */
	public static final String RESULT_PASS = TIMID_GREEN;
	
	public static final String COLOR_NEW = TrawelColor.inlineColor(TrawelColor.colorMix(Color.ORANGE,Color.WHITE,.5f));
	public static final String COLOR_SEEN = TrawelColor.inlineColor(TrawelColor.colorMix(Color.YELLOW,Color.WHITE,.5f));
	public static final String COLOR_BEEN = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.WHITE,.5f));
	public static final String COLOR_OWN = TrawelColor.inlineColor(TrawelColor.colorMix(Color.GREEN,Color.WHITE,.5f));
	public static final String COLOR_REGROWN = TrawelColor.inlineColor(TrawelColor.colorMix(Color.MAGENTA,Color.WHITE,.8f));
	public static final String COLOR_UNDONE_TIMID = TrawelColor.inlineColor(TrawelColor.colorMix(Color.ORANGE,Color.WHITE,.7f));
	public static final String COLOR_DONE_TIMID = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.WHITE,.7f));
	
	public static final String VISIT_NEW = COLOR_NEW + "new! ";
	public static final String VISIT_SEEN = COLOR_SEEN + "! ";
	public static final String VISIT_BEEN = COLOR_BEEN;
	public static final String VISIT_OWN = COLOR_OWN + "(owned) ";
	public static final String VISIT_DONE = COLOR_OWN;
	public static final String VISIT_REGROWN = TIMID_MAGENTA + "new? ";
	
	/**
	 * used for "it's a miss!" after the attack proper
	 */
	public static final String AFTER_ATTACK_MISS = TrawelColor.inlineColor(TrawelColor.colorMix(Color.YELLOW,Color.WHITE,.3f));
	public static final String ATTACK_DAMAGED = PRE_ORANGE;
	public static final String ATTACK_KILL = PRE_RED;
	public static final String ATTACK_BLOCKED = PRE_BLUE;
	public static final String AFTER_ATTACK_BLOCKED = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.WHITE,.3f));
	public static final String ATTACK_MISS = PRE_YELLOW;
	public static final String ATTACK_DAMAGED_WITH_ARMOR = TrawelColor.inlineColor(TrawelColor.colorMix(Color.ORANGE,TrawelColor.colorMix(Color.BLUE,Color.WHITE,.3f),.9f));
	
	public static final String F_SPECIAL = PRE_MAGENTA;
	public static final String F_SERVICE = PRE_BLUE;
	public static final String F_AUX_SERVICE = TIMID_BLUE;
	public static final String F_SERVICE_MAGIC = TIMID_MAGENTA;
	public static final String F_COMBAT = PRE_RED;
	public static final String F_NODE = TIMID_RED;
	public static final String F_FORT = TIMID_GREY;
	public static final String F_BUILDABLE = PRE_ORANGE;
	public static final String F_GUILD = PRE_YELLOW;
	//DOLATER: F_MULTI
	/**
	 * used for inns and districts
	 */
	public static final String F_MULTI = TIMID_ORANGE;
	public static final String F_VARIES = PRE_MAGENTA;
	
	//item values
	public static final String ITEM_WANT_HIGHER = TrawelColor.inlineColor(TrawelColor.colorMix(Color.WHITE,TrawelColor.colorMix(Color.BLUE,Color.GREEN,.5f),.3f));
	public static final String ITEM_WANT_LOWER = TrawelColor.inlineColor(TrawelColor.colorMix(Color.WHITE,TrawelColor.colorMix(Color.YELLOW,Color.ORANGE,.5f),.3f));
	public static final String ITEM_DESC_PROP = TIMID_BLUE;
	public static final String ITEM_VALUE = TIMID_MAGENTA;
	
	//names of things that don't have personalized tints
	public static final String NAME_ITEM_MISC = inlineColor(colorMix(Color.RED,Color.WHITE,.6f));
	public static final String NAME_ITEM_FOOD = inlineColor(colorMix(Color.ORANGE,Color.WHITE,.6f));
	public static final String NAME_ITEM_MAGIC = inlineColor(colorMix(Color.MAGENTA,Color.WHITE,.6f));
	public static final String NAME_ITEM_VALUE = inlineColor(colorMix(Color.PINK,Color.WHITE,.6f));
	public static final String NAME_ITEM_MATERIAL = inlineColor(colorMix(Color.BLUE,Color.WHITE,.6f));
	
	public static final String STAT_HEADER = TrawelColor.inlineColor(TrawelColor.colorMix(Color.WHITE,TrawelColor.colorMix(Color.BLUE,Color.MAGENTA,.5f),.6f));
	
	//attributes
	public static final String ATT_TRUE = PRE_MAGENTA;
	public static final String ATT_EFFECTIVE = TIMID_MAGENTA;
	
	//attack picking
	public static final String ATK_BONUS = STAT_HEADER;
	public static final String ATK_WOUND_NORMAL = ITEM_DESC_PROP;
	public static final String ATK_WOUND_INJURY = ITEM_DESC_PROP;
	public static final String ATK_WOUND_NEGATE = PRE_RED;
	public static final String ATK_WOUND_GRAZE = TIMID_RED;
	
	//color option groups for equal but different options
	public static final String COLOR_OPTION_A = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.RED,Color.GREEN,.3f),Color.WHITE,.5f));
	public static final String COLOR_OPTION_B = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.RED,Color.BLUE,.3f),Color.WHITE,.5f));
	public static final String COLOR_OPTION_C = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.RED,Color.MAGENTA,.3f),Color.WHITE,.5f));
	
	//https://colorbrewer2.org/ good color picking resource
	
	//colors become less noticeable as they go to 1, because 1 is less 'significant'
	//wheras a high amount is more 'critical' to know about
	//sequential 6-class PuRd
	public static final String ADVISE_1 = inlineColor(colorMix(new Color(241,238,246),Color.WHITE,.3f));
	public static final String ADVISE_2 = inlineColor(colorMix(new Color(212,185,218),Color.WHITE,.3f));
	public static final String ADVISE_3 = inlineColor(colorMix(new Color(201,148,199),Color.WHITE,.3f));
	public static final String ADVISE_4 = inlineColor(colorMix(new Color(223,101,176),Color.WHITE,.3f));
	public static final String ADVISE_5 = inlineColor(colorMix(new Color(221,28,119),Color.WHITE,.3f));
	public static final String ADVISE_6 = inlineColor(colorMix(new Color(152,0,67),Color.WHITE,.3f));
	
	//color tier indicators, diverging 6-class RdYlBu
	public static final String INFORM_BAD_STRONG = inlineColor(colorMix(new Color(215,48,39),Color.WHITE,.3f));
	public static final String INFORM_BAD_MID = inlineColor(colorMix(new Color(252,141,89),Color.WHITE,.3f));
	public static final String INFORM_BAD_WEAK = inlineColor(colorMix(new Color(254,224,144),Color.WHITE,.3f));
	public static final String INFORM_GOOD_WEAK = inlineColor(colorMix(new Color(224,243,248),Color.WHITE,.3f));
	public static final String INFORM_GOOD_MID = inlineColor(colorMix(new Color(145,191,219),Color.WHITE,.3f));
	public static final String INFORM_GOOD_STRONG = inlineColor(colorMix(new Color(69,117,180),Color.WHITE,.3f));
	
	//feature service grouping
	public static final String SERVICE_FLAVOR = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.PINK,Color.GREEN,.2f),Color.WHITE,.6f));
	public static final String SERVICE_FREE = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.PINK,Color.GREEN,.3f),Color.WHITE,.4f));
	public static final String SERVICE_AETHER = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.MAGENTA,Color.CYAN,.5f),Color.WHITE,.5f));
	public static final String SERVICE_CURRENCY = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.PINK,Color.MAGENTA,.3f),Color.WHITE,.4f));
	public static final String SERVICE_BOTH_PAYMENT = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.MAGENTA,Color.BLUE,.5f),Color.WHITE,.5f));
	public static final String SERVICE_SPECIAL_PAYMENT = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.PINK,Color.ORANGE,.3f),Color.WHITE,.4f));
	//shared common other feature behaviors
	public static final String SERVICE_QUEST = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.ORANGE,Color.GREEN,.3f),Color.WHITE,.5f));
	//TODO: tweak following colors
	public static final String SERVICE_COMBAT = inlineColor(colorMix(colorMix(Color.RED,Color.GREEN,.3f),Color.WHITE,.5f));
	public static final String SERVICE_EXPLORE = inlineColor(colorMix(colorMix(Color.ORANGE,Color.BLUE,.3f),Color.WHITE,.5f));
	
	public static final Color colorMix(Color c1, Color c2, float f) {
		return new Color((int) extra.lerp(c1.getRed(),c2.getRed(), f),(int) extra.lerp(c1.getGreen(),c2.getGreen(), f),(int) extra.lerp(c1.getBlue(),c2.getBlue(), f));
	}
	public static final String inlineColor(Color col) {
		return "[#"+Integer.toHexString(col.getRGB()).substring(2)+"]";
	}
	public static String colorBasedAtOne(double number, String plus, String minus, String empty) {
		String str = Print.format2.format(number);
		if (number < 1) {
			return minus+str;
		}
		if (number > 1) {
			return plus+str;
		}
		return empty+str;
	}
	/**
	 * used to indicate that < 0 might be bad, > 0 might be good, and that =0 is not the same, but not bad or good
	 * caller should display = instead of a number if they are TRULY equal, higher up in the chain
	 * @param i
	 * @return green +1 OR red -1 OR grey ~ with no zero
	 */
	public static String colorBaseZeroTimid(int i) {
		if (i > 0) {
			return TIMID_GREEN+"+"+i;
		}
		if (i < 0) {
			return TIMID_RED+i;
		}
		return TIMID_GREY+"~";
	}
	/**
	 * 
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= green/red/white 0.00
	 */
	public static final String hardColorDelta2(double to, double was) {
		if (to > was) {
			return PRE_GREEN+"+"+Print.format2.format(to-was);
		}
		if (to == was) {
			return PRE_WHITE+"=0.00";
		}
		return PRE_RED+"-"+Print.format2.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}
	/**
	 * this variant will display a white = is equal, and nothing else
	 */
	public static final String hardColorDelta2Elide(double to, double was) {
		if (to > was) {
			return PRE_GREEN+"+"+Print.format2.format(to-was);
		}
		if (to == was) {
			return PRE_WHITE+"=";
		}
		return PRE_RED+"-"+Print.format2.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}
	/**
	 * 
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= green/red/white 0.0
	 */
	public static final String hardColorDelta1(double to, double was) {
		if (to > was) {
			return PRE_GREEN+"+"+Print.format1.format(to-was);
		}
		if (to == was) {
			return PRE_WHITE+"=0.0";
		}
		return PRE_RED+"-"+Print.format1.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}
	/**
	 * this variant will display a white = if equal, and nothing else
	 */
	public static final String hardColorDelta1Elide(double to, double was) {
		if (to > was) {
			return PRE_GREEN+"+"+Print.format1.format(to-was);
		}
		if (to == was) {
			return PRE_WHITE+"=";
		}
		return PRE_RED+"-"+Print.format1.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}
	/**
	 * integer,
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= timid green/timid red/white 0
	 */
	public static final String softColorDelta0(double to, double was) {
		if (to > was) {
			return TIMID_GREEN+"+"+(int)(to-was);//round towards 0
		}
		if (to == was) {
			return PRE_WHITE+"=";
		}//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
		return TIMID_RED +"-"+(int)(was-to);//round towards 0 because we add - ourselves
	}
	public static final String softColorDelta2Elide(double to, double was) {
		if (to > was) {
			return TIMID_GREEN+"+"+Print.format2.format(to-was);
		}
		if (to == was) {
			return PRE_WHITE+"=";
		}
		return TIMID_RED+"-"+Print.format2.format(was-to);
	}
	/**
	 * integer, Reversed from normal, so positive is bad and negative is good
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= timid red/timid green/white 0
	 */
	public static final String softColorDelta0Reversed(double to, double was) {
		if (to > was) {
			return TIMID_RED+"+"+(int)(to-was);//round towards 0
		}
		if (to == was) {
			return PRE_WHITE+"=";
		}//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
		return TIMID_GREEN +"-"+(int)(was-to);//round towards 0 because we add - ourselves
	}

}
