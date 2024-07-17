package trawel.helper.constants;

public class TrawelChar {

	/**
	 * CHARACTER NOTES:
	 * this can only be one width of DISPLAY
	 * <br>
	 * it's fine if it's a long string,
	 * as long as ultimately it gets reduced to one IMAGE or CHARACTER when displayed
	 * on either end
	 * <br>
	 * FIXME: some string code processes it before displaying it, in which case the data should get stripped out
	 * before then
	 */
	
	public static String current_display_style = "narrator";
	
	//was having trouble finding something that narrator read 
	//should probably make instants display an actual icon in the graphical with an insert sprite code
	//should handle replacement on gms2.3 side for compat + brevity
	/**
	 * instants of time
	 */
	public static String CHAR_INSTANTS = null;
	
	
	//also probably something better out there
	/**
	 * hit mult
	 */
	public static String CHAR_HITMULT = null;
	
	
	public static String CHAR_DAMAGE = null;
	public static String CHAR_SHARP = null;
	public static String CHAR_BLUNT = null;
	public static String CHAR_PIERCE = null;
	public static String CHAR_IGNITE = null;
	public static String CHAR_FROST = null;
	public static String CHAR_ELEC = null;
	public static String CHAR_DECAY = null;
	
	public static String DISP_WEIGHT = null;
	public static String DISP_AETHER = null;
	//TODO: have world currency have it's own DISP and logos for each world?
	public static String DISP_AMP = null;
	public static String DISP_QUALS = null;
	public static String HP_I_FULL = null;
	public static String HP_I_MOSTLY = null;
	public static String HP_I_HALF = null;
	public static String HP_I_SOME = null;
	public static String HP_I_DEAD = null;
	public static String DAM_I_NONE = null;
	public static String DAM_I_SOME = null;
	public static String DAM_I_HEAVY = null;
	public static String DAM_I_KILL = null;

	public static void charSwitchNone() {
		CHAR_INSTANTS = "_";
		CHAR_HITMULT = ">";
		CHAR_DAMAGE = "*";
		CHAR_SHARP = TrawelColor.COLOR_SHARP+"S";
		CHAR_BLUNT = TrawelColor.COLOR_BLUNT+"B";
		CHAR_PIERCE = TrawelColor.COLOR_PIERCE+"P";
		CHAR_IGNITE = TrawelColor.COLOR_IGNITE+"I";
		CHAR_FROST = TrawelColor.COLOR_FROST+"F";
		CHAR_ELEC = TrawelColor.COLOR_ELEC+"E";
		CHAR_DECAY = TrawelColor.COLOR_DECAY+"D";
		DISP_WEIGHT = "weight";
		DISP_AETHER = "aether";
		DISP_AMP = "AMP";
		DISP_QUALS = "q";
		HP_I_FULL = "";
		HP_I_MOSTLY = "";
		HP_I_HALF = "";
		HP_I_SOME = "";
		HP_I_DEAD = "";
		DAM_I_NONE = "";
		DAM_I_SOME = "";
		DAM_I_HEAVY = "";
		DAM_I_KILL = "";
		current_display_style = "none";
	}

	public static void charSwitchVisual() {
		CHAR_INSTANTS = "_";
		CHAR_HITMULT = "%";
		CHAR_DAMAGE = "*";
		CHAR_SHARP = TrawelColor.COLOR_SHARP+"S";
		CHAR_BLUNT = TrawelColor.COLOR_BLUNT+"B";
		CHAR_PIERCE = TrawelColor.COLOR_PIERCE+"P";
		CHAR_IGNITE = TrawelColor.COLOR_IGNITE+"I";
		CHAR_FROST = TrawelColor.COLOR_FROST+"F";
		CHAR_ELEC = TrawelColor.COLOR_ELEC+"E";
		CHAR_DECAY = TrawelColor.COLOR_DECAY+"D";
		DISP_WEIGHT = "weight";
		DISP_AETHER = "aether";
		DISP_AMP = "AMP";
		DISP_QUALS = "q";
		HP_I_FULL = "█";
		HP_I_MOSTLY = "▓";
		HP_I_HALF = "▒";
		HP_I_SOME = "░";
		HP_I_DEAD = "┼";//"ø";
		DAM_I_NONE = "·";
		DAM_I_SOME = "¦";
		DAM_I_HEAVY = "■";
		DAM_I_KILL = "█";
		current_display_style = "visual";
	}

	public static void charSwitchNarrator() {
		CHAR_INSTANTS = "t";
		CHAR_HITMULT = "h";
		CHAR_DAMAGE = "d";
		CHAR_SHARP = TrawelColor.COLOR_SHARP+"S";
		CHAR_BLUNT = TrawelColor.COLOR_BLUNT+"B";
		CHAR_PIERCE = TrawelColor.COLOR_PIERCE+"P";
		CHAR_IGNITE = TrawelColor.COLOR_IGNITE+"I";
		CHAR_FROST = TrawelColor.COLOR_FROST+"F";
		CHAR_ELEC = TrawelColor.COLOR_ELEC+"E";
		CHAR_DECAY = TrawelColor.COLOR_DECAY+"D";
		DISP_WEIGHT = "w";
		DISP_AETHER = "a";
		DISP_AMP = "AMP";
		DISP_QUALS = "q";
		HP_I_FULL = "";
		HP_I_MOSTLY = "¾";
		HP_I_HALF = "½";
		HP_I_SOME = "¼";
		HP_I_DEAD = "Ø";//make sure it says this sanely
		DAM_I_NONE = "¼";
		DAM_I_SOME = "½";
		DAM_I_HEAVY = "¾";
		DAM_I_KILL = "Ø";
		current_display_style = "narrator";
	}

	public static void charSwitchEmote() {
		//https://www.compart.com/en/unicode/search godly resource
		//https://www.amp-what.com/unicode/search/knife
		CHAR_INSTANTS = "⧗";//🮚⌛⧗
		CHAR_HITMULT = "🎯";//ʘ◎🎯◉⦿
		CHAR_DAMAGE = "🕱";
		CHAR_SHARP = TrawelColor.COLOR_SHARP+"🟆";//✂⚔🔪🪒💇🗡️⸸⸷🞣🟆
		CHAR_BLUNT = TrawelColor.COLOR_BLUNT+"●";//🔨⚒️🪨🛞●
		CHAR_PIERCE = TrawelColor.COLOR_PIERCE+"▲";//⇫♆➳➹➴♂➴⚩➛▲
		CHAR_IGNITE = TrawelColor.COLOR_IGNITE+"🔥";
		CHAR_FROST = TrawelColor.COLOR_FROST+"❄";//❄❄️❆🧊🥶
		CHAR_ELEC = TrawelColor.COLOR_ELEC+"🗲";//⚡🗲🌩
		CHAR_DECAY = TrawelColor.COLOR_DECAY+"🕱";//⛤🕱
		DISP_WEIGHT = "🏋";//🏋𐄷⚖
		DISP_AETHER = "🜀";//🜀¤⚖ //currency, of a sort
		DISP_AMP = "𝧧";//https://www.compart.com/en/unicode/search?q=movement#characters
		DISP_QUALS = "🎖";
		HP_I_FULL = "💖";
		HP_I_MOSTLY = "💗";
		HP_I_HALF = "💓";
		HP_I_SOME = "💔";
		HP_I_DEAD = "💀";
		DAM_I_NONE = "🞌";//🞍🞌▫
		DAM_I_SOME = "🞍";//▪🞍
		DAM_I_HEAVY = "◼";//◼◾◼️
		DAM_I_KILL = "⬛";//💀☠☠️ ⬛
		current_display_style = "unicode";
		//⛊⛨☠♨☥⛧⚚⛏⚀⸸
	}
	//set default character displays
	static {
		charSwitchVisual();
	}

}
