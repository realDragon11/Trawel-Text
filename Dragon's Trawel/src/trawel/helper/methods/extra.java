package trawel.helper.methods;


import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.personal.DummyPerson;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.people.Player;
import trawel.towns.contexts.World;
import trawel.towns.data.WorldGen;

public final class extra {
	private static ReentrantLock mainThreadLock = new ReentrantLock();

	private static final ThreadLocal<ThreadData> threadLocalData = new ThreadLocal<ThreadData>() {
		@Override protected ThreadData initialValue() {
			return new ThreadData();
		}
	};

	public static final class ThreadData {
		public World world;
	}


	private static final ThreadLocal<List<DummyPerson>> localDumInvs = new ThreadLocal<List<DummyPerson>>() {
		@Override protected List<DummyPerson> initialValue() {
			return WorldGen.initDummyInvs();
		}
	};

	public static final List<DummyPerson> getDumInvs() {
		return localDumInvs.get();
	}

	//static methods


	public static final boolean isMainThread() {
		return mainThreadLock.isHeldByCurrentThread();
	}

	public static final void setMainThread() {
		System.out.print("booting");
		mainThreadLock.lock();
		System.out.println("...");
	}

	/**
	 * since each thread will only ever be dealing with one world at a time
	 * this method lets you store that world to be accessed later
	 * 
	 * this is true because we made the assumption that threads will never trip over each other
	 * for the purposes of not needing to give everything in the game locks
	 * @return a container
	 */
	public static final ThreadData getThreadData() {
		//https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html
		return threadLocalData.get();
	}

	/**
	 * should be called after you update one of the following, in the main thread:
	 * <br>
	 * 1. the player's world
	 * @return getThreadData()
	 */
	public static final ThreadData mainThreadDataUpdate() {
		if (!isMainThread()) {
			throw new RuntimeException("trying to main update a non main thread");
		}
		ThreadData temp = getThreadData();
		temp.world = Player.player.getWorld();
		return temp;
	}

	/**
	 * Makes sure that something isn't less than zero, else it returns 0
	 * @param i (int)
	 * @return (int)
	 */
	public static final int zeroOut(int i) {
		return Math.max(i,0);
	}

	/**
	 * Makes sure that something isn't less than zero, else it returns 0
	 * @param i (double)
	 * @return (double)
	 */
	public static final double zeroOut(double i) {
		return Math.max(i,0);
	}
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

	public static String current_display_style = "visual";

	public static String CHAR_INSTANTS = "_";
	//was having trouble finding something that narrator read 
	//should probably make instants display an actual icon in the graphical with an insert sprite code
	//should handle replacement on gms2.3 side for compat + brevity

	public static String CHAR_HITCHANCE = "%";
	//also probably something better out there

	//TODO: make some better global DRY damage code system
	public static String CHAR_DAMAGE = "*";
	
	public static String CHAR_SHARP = TrawelColor.COLOR_SHARP+"S";
	public static String CHAR_BLUNT = TrawelColor.COLOR_BLUNT+"B";
	public static String CHAR_PIERCE = TrawelColor.COLOR_PIERCE+"P";
	public static String CHAR_IGNITE = TrawelColor.COLOR_IGNITE+"I";
	public static String CHAR_FROST = TrawelColor.COLOR_FROST+"F";
	public static String CHAR_ELEC = TrawelColor.COLOR_ELEC+"E";
	public static String CHAR_DECAY = TrawelColor.COLOR_DECAY+"D";

	public static String DISP_WEIGHT = "weight";
	public static String DISP_AETHER = "aether";
	public static String DISP_AMP = "AMP";
	public static String DISP_QUALS = "q";

	public static String HP_I_FULL = "█";
	public static String HP_I_MOSTLY = "▓";
	public static String HP_I_HALF = "▒";
	public static String HP_I_SOME = "░";
	public static String HP_I_DEAD = "┼";

	public static String DAM_I_NONE = "·";
	public static String DAM_I_SOME = "¦";
	public static String DAM_I_HEAVY = "■";
	public static String DAM_I_KILL = "█";

	public static final String[] EXPLAIN_CHARS_DAMAGE = new String[]{
		CHAR_SHARP + " sharp",
		CHAR_BLUNT + " blunt",
		CHAR_PIERCE + " blunt",
	};

	private static String stringBuilder;

	static {
		stringBuilder = "damage: ";
		for (int i = 0;;i++) {
			stringBuilder += EXPLAIN_CHARS_DAMAGE[i];
			if (i == EXPLAIN_CHARS_DAMAGE.length-1) {
				break;
			}
			stringBuilder+=", ";
		}

	}

	public static final String EXPLAIN_SIMPLE_CHARS_DAMAGE = stringBuilder;

	public static String explainDam() {
		return CHAR_SHARP+" sharp, " + CHAR_BLUNT + " blunt, " +CHAR_PIERCE +" pierce."+TrawelColor.PRE_WHITE;
	}

	public static void charSwitchNone() {
		CHAR_INSTANTS = "_";
		CHAR_HITCHANCE = ">";
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
		CHAR_HITCHANCE = "%";
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
		CHAR_HITCHANCE = "h";
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
		CHAR_HITCHANCE = "🎯";//ʘ◎🎯◉⦿
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

	public static final float clamp(float d, float min, float max) {
		return Math.min(max, Math.max(d, min));
	}
	public static final double clamp(double d, double min, double max) {
		return Math.min(max, Math.max(d, min));
	}
	public static final int clamp(int d, int min, int max) {
		return Math.min(max, Math.max(d, min));
	}


	public static final float lerp(float a, float b, float f) 
	{
		return (a * (1.0f - f)) + (b * f);
	}

	public static final double lerp(double a, double b, double f) 
	{
		return (a * (1.0 - f)) + (b * f);
	}

	/**
	 * https://stackoverflow.com/a/13091759
	 * @param a - How deep the curve is - 0 <-> 1
	 * @return
	 */
	public static float bellCurve(float a){
		double x = Rand.getRand().nextDouble();
		return (float) (4*a*Math.pow(x,3) - 6*a*Math.pow(x,2) + 2*a*x + x);//TODO fix
	}

	public static float curveLerp(float start, float end, float depth) {
		return extra.lerp(start, end, bellCurve(depth));
	}

	public static float lerpDepth(float start, float end, float f,float depth) {
		float midpoint = start+(end-start)/2;
		float x = 1-(2*Math.abs(midpoint-f)/(end-start));//TODO fix
		//System.out.println(start + ", "+end +", " +f+";"+midpoint+": "+x);
		return (float) (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);//4*.25*x^3-6*.25*x^2+2*.25*x+x
	}

	public static float lerpSetup(float start, float end, float f) {
		return 1-(2*Math.abs((start+(end-start)/2)-f)/(end-start));//TODO fix
	}
	/*
		public static double upDamCurve(double depth, double midpoint) {
			double rand = Math.random();
			double distance = (Math.abs(rand-midpoint));
			//double x = rand/midpoint;
			double x = (rand < midpoint ? rand/midpoint : (midpoint-distance)/midpoint);
			//double x = (midpoint-(1-(Math.abs(Math.random()-midpoint))))/midpoint;
			return (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);
		}*/

	public static final double upDamCurve(double depth, double midpoint) {
		double x = 1-(2*Math.abs(Rand.getRand().nextDouble()-midpoint));
		x = extra.clamp(x,0,1);
		return (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);
	}

	/**
	 * must be non empty
	 */
	public static Person getNonAddOrFirst(List<Person> peeps) {
		for (Person p: peeps) {
			if (!p.getFlag(PersonFlag.IS_MOOK)) {
				return p;
			}
		}
		return peeps.get(0);
	}

}

