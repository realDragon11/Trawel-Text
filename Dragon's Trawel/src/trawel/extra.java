package trawel;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import com.github.tommyettinger.random.EnhancedRandom;
import com.github.tommyettinger.random.WhiskerRandom;

import derg.UnitAssertions;
import derg.menus.MenuGenerator;
import derg.menus.MenuGeneratorPaged;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.personal.DummyPerson;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.people.Player;
import trawel.towns.World;

public final class extra {
	/**
	 * Brian Malone
	 * Various static methods to be used in other classes
	 * is in another package so import statements can work
	 * 2/5/2018
	 */

	public static final byte emptyByte = 0b00000000;
	public static final byte emptyInt = 0b0000000000000000;

	private static Boolean printMode = false;
	//private static long lastMod = -1;
	private static String printStuff = "";

	private static Stack<Boolean> printStack = new Stack<Boolean>();

	private static ReentrantLock mainThreadLock = new ReentrantLock();

	private static final int debugChunkPer = 10;
	private static boolean debugPrint = false;
	private static int debugChunk = 0;
	private static int backingQueue = 0;

	public static final String IMPACT_TIP = "Impactful attacks are typically those that hit and deal damage without reliable forcing damage.";


	private static final ThreadLocal<EnhancedRandom> localRands = new ThreadLocal<EnhancedRandom>() {
		@Override protected EnhancedRandom initialValue() {
			return new WhiskerRandom();
		}
	};

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
	 * gets the rand instance for the current thread, should be used
	 * instead of making your own.
	 * @return
	 */
	public static final EnhancedRandom getRand() {
		//https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html
		return localRands.get();
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

	public static final float randFloat() {
		return getRand().nextFloat();
	}

	/**
	 * randomly returns one of the parameters
	 * @param a variable amount of objects (Object)
	 * @return (Object)
	 */
	/*public static Object choose(Object... options) {
		return options[(int)(Math.random()*(double)options.length)];
	}*/

	/**
	 * randomly returns one of the parameters
	 * @param a variable amount of strings (String)
	 * @return (String)
	 */
	public static String choose(String... options) {
		return options[getRand().nextInt(options.length)];
	}

	public static <E> E choose(E... options) {
		return options[getRand().nextInt(options.length)];
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
	 * Has a (a) in (b) chance of returning true
	 * @param a (int)
	 * @param b (int)
	 * @return (boolean)
	 */
	public static final boolean chanceIn(int a,int b) {
		return (getRand().nextInt(b+1)+1 <= a);
	}

	/**
	 * Takes a string and makes the first letter capital
	 * @param str (String)
	 * @return Str (String)
	 */
	public static final String capFirst(String str){
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Decides whether to become 'are' or 'is' based on the guessed plurality of the input
	 * @param str (String)
	 * @return (String)
	 */
	public static final String pluralIs(String str) {
		if (str.endsWith("s")){
			return "are";
		}else {
			return "is";
		}
	}
	/**
	 * Decides whether to become 'are some' or 'is a' based on the guessed plurality of the input
	 */
	public static final String pluralIsA(String str) {
		if (str.endsWith("s")){
			return "are some";
		}else {
			return "is a";
		}
	}

	/**
	 * can set to true to disable normal outputs
	 * some graphical functions still write, as well as most error messages
	 */
	public static final void changePrint(boolean disable) {
		if (!isMainThread()) {
			return;
		}
		printMode = disable;
	}


	public static final java.text.DecimalFormat F_TWO_TRAILING = new java.text.DecimalFormat("0.00");
	public static final java.text.DecimalFormat F_WHOLE = new java.text.DecimalFormat("0");

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
	
	public static String COLOR_SHARP = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.4f));
	public static String CHAR_SHARP = COLOR_SHARP+"S";
	public static String COLOR_BLUNT = inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.4f));
	public static String CHAR_BLUNT = COLOR_BLUNT+"B";
	public static String COLOR_PIERCE = inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.4f));
	public static String CHAR_PIERCE = COLOR_PIERCE+"P";
	public static String COLOR_IGNITE = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.2f));
	public static String CHAR_IGNITE = COLOR_IGNITE+"I";
	public static String COLOR_FROST = inlineColor(extra.colorMix(Color.CYAN,Color.WHITE,.4f));
	public static String CHAR_FROST = COLOR_FROST+"F";
	public static String COLOR_ELEC = inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.4f));
	public static String CHAR_ELEC = COLOR_ELEC+"E";
	public static String COLOR_DECAY = inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.4f));
	public static String CHAR_DECAY = COLOR_DECAY+"D";

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
		return CHAR_SHARP+" sharp, " + CHAR_BLUNT + " blunt, " +CHAR_PIERCE +" pierce."+extra.PRE_WHITE;
	}

	public static void charSwitchNone() {
		CHAR_INSTANTS = "_";
		CHAR_HITCHANCE = ">";
		CHAR_DAMAGE = "*";
		CHAR_SHARP = COLOR_SHARP+"S";
		CHAR_BLUNT = COLOR_BLUNT+"B";
		CHAR_PIERCE = COLOR_PIERCE+"P";
		CHAR_IGNITE = COLOR_IGNITE+"I";
		CHAR_FROST = COLOR_FROST+"F";
		CHAR_ELEC = COLOR_ELEC+"E";
		CHAR_DECAY = COLOR_DECAY+"D";
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
		CHAR_SHARP = COLOR_SHARP+"S";
		CHAR_BLUNT = COLOR_BLUNT+"B";
		CHAR_PIERCE = COLOR_PIERCE+"P";
		CHAR_IGNITE = COLOR_IGNITE+"I";
		CHAR_FROST = COLOR_FROST+"F";
		CHAR_ELEC = COLOR_ELEC+"E";
		CHAR_DECAY = COLOR_DECAY+"D";
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
		CHAR_SHARP = COLOR_SHARP+"S";
		CHAR_BLUNT = COLOR_BLUNT+"B";
		CHAR_PIERCE = COLOR_PIERCE+"P";
		CHAR_IGNITE = COLOR_IGNITE+"I";
		CHAR_FROST = COLOR_FROST+"F";
		CHAR_ELEC = COLOR_ELEC+"E";
		CHAR_DECAY = COLOR_DECAY+"D";
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
		CHAR_SHARP = COLOR_SHARP+"🟆";//✂⚔🔪🪒💇🗡️⸸⸷🞣🟆
		CHAR_BLUNT = COLOR_BLUNT+"●";//🔨⚒️🪨🛞●
		CHAR_PIERCE = COLOR_PIERCE+"▲";//⇫♆➳➹➴♂➴⚩➛▲
		CHAR_IGNITE = COLOR_IGNITE+"🔥";
		CHAR_FROST = COLOR_FROST+"❄";//❄❄️❆🧊🥶
		CHAR_ELEC = COLOR_ELEC+"🗲";//⚡🗲🌩
		CHAR_DECAY = COLOR_DECAY+"🕱";//⛤🕱
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

	/**
	 * Formats a double into a string that looks nicer.
	 * @param str - (double)
	 * @return (String)
	 */
	public static final String format(double str) {
		return(F_TWO_TRAILING.format(str));
	}

	public static final String formatInt(double str) {
		return(F_WHOLE.format(str));
	}

	//extra.linebreak();
	public static final void linebreak() {
		extra.println("------------");
		//clear the synth
	}

	public static final boolean yesNo() {
		if (backingQueue > 0) {
			backingQueue--;
			return false;
		}
		trawel.threads.BlockTaskManager.start();
		while (true) {
			extra.println("1 yes");
			extra.println("9 no");
			Networking.sendStrong("Entry|yesno|");//need to add this coloring behavior to normal inputs
			//if ((Networking.connected() && mainGame.GUIInput)  || Networking.autoconnectSilence) {
			//while(true) {
			int ini = Networking.nextInt();
			while(ini != 1 && ini != 9) {
				if (ini == 0) {
					return false;
				}
				if (ini == 10) {
					backingQueue = 10;
					return false;
				}
				extra.println("Please type 1 or 9.");
				extra.println("1 yes");
				extra.println("9 no");
				ini=  Networking.nextInt();
				if (ini == -99 || ini == -1) {
					Networking.unConnect();
					throw new RuntimeException("invalid input stream error");
				}

			}
			extra.linebreak();
			trawel.threads.BlockTaskManager.halt();
			return ini == 1;

			/*}else {

					str = mainGame.scanner.next();
					extra.linebreak();
					str = str.toLowerCase();
					//extra.println(str);
					if (str.equals("yes") || str.equals("y")|| str.equals("1")) {
						trawel.threads.BlockTaskManager.halt();
						return true;
					}
					if (str.equals("no") || str.equals("n") || str.equals("0") || str.equals("9")) {
						trawel.threads.BlockTaskManager.halt();
						return false;
					}
					extra.println("Yes or No?");

				}*/
		}
	}
	public static final int randRange(int i, int j) {
		//return (int)(Math.random()*(j+1-i))+i;
		return getRand().nextInt((j+1)-i)+i;
	}
	public static final float randRange(float i, float j) {
		return getRand().nextInclusiveFloat((j)-i)+i;
	}

	public static final int inInt(int max) {
		return inInt(max,false,false);
	}

	/**
	 * prevents the player from automatically backing out from one case to another
	 * <br>
	 * example: player might want to back out of drawbane selection, inventory selection, etc manually
	 */
	public static final void endBackingSegment() {
		backingQueue = 0;
	}

	public static final int inInt(int max, boolean alwaysNine, boolean canBack) {
		if (backingQueue > 0) {
			if (canBack) {
				backingQueue--;
				return 9;
			}else {
				backingQueue = 0;
			}
		}
		Networking.sendStrong("Entry|Activate|" + max + "|");
		trawel.threads.BlockTaskManager.start();
		int ini=  Networking.nextInt();
		while((ini < 1 || ini > max)) {
			if (alwaysNine && ini == 9) {
				break;
			}
			if (canBack && ini == 10) {
				backingQueue = 10;
				ini = 9;
				break;
			}
			if (canBack && ini == 0) {
				ini = 9;
				break;
			}
			if (ini != -2) {//silent loading
				extra.println("Please type a number from 1 to " + max + "." + (alwaysNine ? " (or 9)" : ""));
			}
			ini = Networking.nextInt();

			if (ini == -99) {
				Networking.unConnect();
				throw new RuntimeException("invalid input stream error");
			}
			if (ini == -1) {
				Networking.unConnect();
				throw new RuntimeException("input stream ended");
			}
		}
		trawel.threads.BlockTaskManager.halt();
		extra.linebreak();
		return ini;
	}

	public static final void println() {
		println("");
	}

	public static final void println(String str) {
		if (!isMainThread()) {
			return;
		}
		if (!printMode || debugPrint) {
			//mainGame.log(str);
			Networking.printlocalln(stripPrint(printStuff+str));
			detectInputString(stripPrint(printStuff +str));
			Networking.printlnTo(printStuff + str);
			printStuff = "";

			if (debugPrint) {
				debugChunk++;
				if (debugChunk > debugChunkPer) {
					extra.inString();
					debugChunk = 0;
				}
			}
		}

	}

	public static final void print(String str) {
		if (!isMainThread()) {
			return;
		}
		if (!printMode) {
			printStuff+=str;
		}
	}

	private static final String stripPrint(String str) {
		int index = str.indexOf('[');
		while (index != -1) {
			int lastindex = str.indexOf(']');
			//str = str.substring(0, index) + str.substring(lastindex+1, str.length());
			str = str.replace(str.substring(index,lastindex+1), "");
			index = str.indexOf('[');
		}
		return str;
	}

	private static void detectInputString(String str) {
		if (str.length() > 1) {
			if (Character.isDigit(str.charAt(0)) && str.charAt(1) == " ".charAt(0)) {
				Networking.send("Input|" + str.charAt(0) +"|"+str+"|");
			}
		}
	}

	public static String inString() {
		return mainGame.scanner.nextLine().toLowerCase();
	}

	public static void inputContinue() {
		extra.println("1 continue");
		extra.inInt(1);
	}

	/**
	 * true = do not print
	 * <br>
	 * false = can print
	 * @return if you can't print
	 */
	public static final Boolean getPrint() {
		if (!isMainThread()){
			return true;
		}
		return printMode;
	}
	public static final java.text.DecimalFormat format1 = new java.text.DecimalFormat("0.0");
	public static final java.text.DecimalFormat format2 = new java.text.DecimalFormat("0.00");
	public static String format2(double d) {
		String str = format2.format(d);
		if (d > 0) {
			str = "+" + str;
		}
		return(str);
	}

	/**
	 * given by TEtt from squidsquad
	 */
	public static final double hrandom() {
		return ((Long.bitCount(getRand().nextLong()) - 32. + getRand().nextDouble() - getRand().nextDouble()) / 66.0 + 0.5);
	}
	/**
	 * given by TEtt from squidsquad
	 */
	public static final float hrandomFloat() {
		return ((Long.bitCount(getRand().nextLong()) - 32f + getRand().nextFloat() - getRand().nextFloat()) / 66f + 0.5f);
	}

	/**
	 * can only handle color codes and special printing options at start of string, and only one token per strs
	 */
	public static void specialPrint(int[] in,String...strs) {
		for (int i = 0; i < strs.length;i++) {
			String base = strs[i].trim();
			if (!base.isEmpty() && base.charAt(0) == '[') {
				print(base.substring(0,base.indexOf(']')+1));
			}
			String str = extra.stripPrint(strs[i]);
			if (str.length() >= in[i]) {
				print(str.substring(0,in[i]));
			}else {
				print(str);
				for (int j = in[i]-str.length();j > 0;j--) {// > 0 because 0 is same length so we're good
					print(" ");
				}
			}
		}
		/*for (int i = 0; i < strs.length;i++) {
			String str = extra.stripPrint(strs[i]);
			if (str.length() >= in[i]) {
				print(str.substring(0,in[i]));
			}else {
				print(str);
				for (int j = in[i]-str.length();j > 0;j--) {// > 0 because 0 is same length so we're good
					print(" ");
				}
			}
		}*/
		/*
		int j = 0;
		while (j < in.length) {
			while(!strs[j].isEmpty() && in[j] > 0) {
				print(strs[j].substring(0, 1));
				if (strs[j].length() > 1){
					strs[j] = strs[j].substring(1,strs[j].length());
				}else {
					strs[j] = "";
				}
				in[j] -=1;
			}
			if (in[j] > 0 && j < in.length-1) {
				while (in[j] > 0) {
					if (strs[j].length() > 1){
						strs[j] = strs[j].substring(1,strs[j].length());
					}else {
						strs[j] = "";
					}
					print(" ");
					in[j] -=1;
				}
			}
			j++;
		}*/
		extra.println(extra.PRE_WHITE);
	}

	public static String cutPadLenFront(int len, String str) {
		if (len > str.length()) {
			while (len > str.length()) {
				str= str+" ";
			}
			return str;
		}else {
			return str.substring(0, len);
		}
	}

	public static String cutPadLen(int len, String str) {
		if (len > str.length()) {
			while (len > str.length()) {
				str= " "+str;//java 8 sadness, I think the better ways to do this without a stringbuilder are later
			}
			return str;
		}else {
			return str.substring(0, len);
		}
	}
	public static String cutPadLenError(int len, String str) {
		if (len == str.length()) {
			return str;
		}
		if (len > str.length()) {
			while (len > str.length()) {
				str= " "+str;//java 8 sadness, I think the better ways to do this without a stringbuilder are later
			}
			return str;
		}else {
			str= "C"+str;//show that it's been cut
			return str.substring(0, len);
		}
	}

	public static void offPrintStack() {
		if (!isMainThread()){
			return;
		}
		printStack.push(printMode);
		printMode = true;
	}

	public static void popPrintStack() {
		if (!isMainThread()){
			return;
		}
		printMode = printStack.pop();
	}

	public static <E> E randList(ArrayList<E> list) {
		return list.get(getRand().nextInt(list.size()));
	}

	public static <E> E randList(List<E> list) {
		return list.get(getRand().nextInt(list.size()));
	}
	public static <E> E randList(E[] list) {
		return list[getRand().nextInt(list.length)];
	}
	/**
	 * adapted from https://stackoverflow.com/a/68640122
	 * <br>
	 * will return null if couldn't find anything
	 */
	public static <E> E randCollection(Collection<E> collect) {
		return collect.stream().skip(extra.getRand().nextInt(collect.size())).findAny().orElse(null);
	}

	/**
	 * in most cases you might want to implement a better way with size yourself
	 */
	public static <E> E randStream(Stream<E> stream, int size) {
		return stream.skip(extra.getRand().nextInt(size)).findAny().orElse(null);
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


	//TODO: menuGoCategory that takes MenuItems that have categories- (up to 8 usually, but allows nesting)
	//if a category (and it's nested categories) only have on option
	//it is displayed directly, otherwise a new option that just lets you enter the category is created
	//this will have different logic code but maintains the 'store menu until an actual option is picked'
	//logic of menuGo
	public static int menuGo(MenuGenerator mGen) {
		List<MenuItem> mList = new ArrayList<MenuItem>();
		int v;
		boolean forceLast;
		boolean canBack;
		List<MenuItem> subList;
		while (true) {
			mGen.onRefresh();
			mList = mGen.gen();
			if (mList == null) {
				return -1;//used for nodes so they can force interactions cleanly
			}
			v = 1;
			forceLast = false;
			canBack = false;
			subList = new ArrayList<MenuItem>();
			for (MenuItem m: mList) {
				if (m.forceLast()) {
					subList.add(m);
					extra.println("9 " + m.title());
					forceLast = true;
					canBack = m.canBack();
					//force last must be last, and pickable
					//UPDATE: it works with other labels after it now
					continue;
				}else {
					if (m.canClick()) {
						assert forceLast == false;
						extra.println(v + " " + m.title());
						v++;
						subList.add(m);
					}else {
						extra.println(m.title());
					}
				}
			}

			//mList.stream().filter(m -> m.canClick() == true).forEach(subList::add);
			int val;
			if (!forceLast) {
				val = extra.inInt(subList.size())-1;
			}else {
				val = extra.inInt(subList.size()-1,true,canBack)-1;
			}
			boolean ret;
			if (val < subList.size()) {
				ret = subList.get(val).go();
			}else {
				ret = subList.get(subList.size()-1).go();
			}
			if (ret) {
				if (mGen instanceof ScrollMenuGenerator) {
					return ((ScrollMenuGenerator)mGen).getVal(val);
				}
				return val;
			}
		}
	}

	public static int menuGoPaged(MenuGeneratorPaged mGen) {
		List<MenuItem> mList = new ArrayList<MenuItem>();
		mList = mGen.gen();

		mGen.page = 0;
		while (true) {
			mGen.lists.clear();
			mGen.maxPage = 0;
			int j = 0;
			int count = 0;
			int start = 0;
			mList.add(new MenuLine() {//dummy node
				@Override
				public String title() {
					return "end of pages";
				}});
			while (j < mList.size()) {

				if (mList.get(j).canClick() == true) {
					count++;
					j++;
				}else {
					j++;
					continue;
				}
				if (count == 8 && mList.size()-1 > 8) {
					mList.add(j,new MenuLine() {

						@Override
						public String title() {
							return (mGen.page+1) + "/" + (mGen.maxPage+1);
						}});
					j++;
					mList.add(j,new MenuSelect() {

						@Override
						public String title() {
							return "next page";
						}

						@Override
						public boolean go() {
							mGen.page++;
							return false;
						}});
					count++; j++;
					mGen.lists.add(new ArrayList<MenuItem>());
					for (int k = 0;k < j;k++) {
						mGen.lists.get(0).add(mList.get(k));
					}
					start = j;
					mGen.maxPage++;
					count+=2;
				}else {
					if (count > 10 && (count%9 == 0 || j == mList.size()-1)) {
						mList.add(j,new MenuLine() {

							@Override
							public String title() {
								return (mGen.page+1) + "/" + (mGen.maxPage+1);
							}});
						j++;
						mList.add(j,new MenuSelect() {

							@Override
							public String title() {
								return "last page";
							}

							@Override
							public boolean go() {
								mGen.page--;
								return false;
							}});
						count++; j++;
						if (true == true) {//figure out a last page condition
							mList.add(j,new MenuSelect() {

								@Override
								public String title() {
									return "next page";
								}

								@Override
								public boolean go() {
									mGen.page++;
									return false;
								}});
							count++; j++;
							//int start = 1+mList.indexOf(mGen.lists.get(mGen.maxPage-1).get(mGen.lists.get(mGen.maxPage-1).size()-1));
							mGen.lists.add(new ArrayList<MenuItem>());

							for (int k = start;k < j;k++) {
								mGen.lists.get(mGen.maxPage).add(mList.get(k));
							}
							//if (count%9==0) {//doesn't work in every case
							mGen.maxPage++;//}
							start = j;
						}
					}

				}

			}

			if (mGen.maxPage == 0) {
				mGen.lists.add(new ArrayList<MenuItem>());
				for (int k = 0;k < j;k++) {
					mGen.lists.get(0).add(mList.get(k));
				}
			}

			if (start == j-1) {
				mGen.maxPage--;
				mGen.lists.get(mGen.maxPage).remove(mGen.lists.get(mGen.maxPage).size()-1);
			}
			mGen.page = extra.clamp(mGen.page,0,mGen.maxPage);
			if (mGen.header != null) {
				extra.println(mGen.header.title());
			}
			int v = 1;
			List<MenuItem> subList = new ArrayList<MenuItem>();
			for (int i = 0;i < mGen.lists.get(mGen.page).size();i++)
				if (mGen.lists.get(mGen.page).get(i).canClick() == true) {
					subList.add(mGen.lists.get(mGen.page).get(i));
					extra.println(v + " " +mGen.lists.get(mGen.page).get(i).title());
					v++;
				}else {
					extra.println(mGen.lists.get(mGen.page).get(i).title());
				}
			int val = extra.inInt(subList.size())-1;
			boolean ret = subList.get(val).go();
			if (ret) {
				return val;
			}
			mList = mGen.gen();

		}
	}

	public static final float lerp(float a, float b, float f) 
	{
		return (a * (1.0f - f)) + (b * f);
	}

	public static final double lerp(double a, double b, double f) 
	{
		return (a * (1.0 - f)) + (b * f);
	}

	public static final Color colorMix(Color c1, Color c2, float f) {
		return new Color((int) extra.lerp(c1.getRed(),c2.getRed(), f),(int) extra.lerp(c1.getGreen(),c2.getGreen(), f),(int) extra.lerp(c1.getBlue(),c2.getBlue(), f));
	}

	public static final String inlineColor(Color col) {
		return "[#"+Integer.toHexString(col.getRGB()).substring(2)+"]";
	}

	//NOTE: predefined color mixes inlined
	public static final String PRE_WHITE = inlineColor(Color.WHITE);
	public static final String PRE_RED = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.5f));
	public static final String PRE_BATTLE = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.4f));
	public static final String PRE_MAYBE_BATTLE = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.6f));
	public static final String PRE_ORANGE = inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f));
	public static final String PRE_YELLOW = inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.5f));
	public static final String PRE_BLUE = inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f));
	public static final String PRE_GREEN = inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f));
	public static final String PRE_MAGENTA = inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.5f));

	public static final String PRE_ROAD = PRE_GREEN;
	public static final String PRE_SHIP = PRE_GREEN;
	public static final String PRE_TELE = PRE_GREEN;

	//timid colors that are slight, used for bad and good hinting
	public static final String TIMID_GREEN = inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.7f));
	public static final String TIMID_RED = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.7f));
	/** 
	 * do not use for no change whatsoever, use white for that- this is a change that might be bad or good but is a net 0 to this stat
	 */
	public static final String TIMID_GREY = inlineColor(extra.colorMix(Color.BLACK,Color.WHITE,.95f));

	//not directly used yet
	public static final String TIMID_BLUE = inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.7f));
	public static final String TIMID_MAGENTA = inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.8f));
	public static final String TIMID_ORANGE = inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.7f));

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

	public static final String COLOR_NEW = inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f));
	public static final String COLOR_SEEN = inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.5f));
	public static final String COLOR_BEEN = inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f));
	public static final String COLOR_OWN = inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f));
	public static final String COLOR_REGROWN = inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.8f));

	public static final String VISIT_NEW = COLOR_NEW + "new! ";
	public static final String VISIT_SEEN = COLOR_SEEN + "! ";
	public static final String VISIT_BEEN = COLOR_BEEN;
	public static final String VISIT_OWN = COLOR_OWN + "(owned) ";
	public static final String VISIT_DONE = COLOR_OWN;
	public static final String VISIT_REGROWN = TIMID_MAGENTA + "new? ";

	/**
	 * used for "it's a miss!" after the attack proper
	 */
	public static final String AFTER_ATTACK_MISS = extra.inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.3f));
	public static final String ATTACK_DAMAGED = PRE_ORANGE;
	public static final String ATTACK_KILL = PRE_RED;
	public static final String ATTACK_BLOCKED = PRE_BLUE;
	public static final String AFTER_ATTACK_BLOCKED = extra.inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.3f));
	public static final String ATTACK_MISS = PRE_YELLOW;
	public static final String ATTACK_DAMAGED_WITH_ARMOR = 
			extra.inlineColor(extra.colorMix(Color.orange,extra.colorMix(Color.BLUE,Color.WHITE,.3f),.9f));

	public static final String F_SPECIAL = PRE_MAGENTA;
	public static final String F_SERVICE = PRE_BLUE;
	public static final String F_AUX_SERVICE = TIMID_BLUE;
	public static final String F_SERVICE_MAGIC = TIMID_MAGENTA;
	public static final String F_COMBAT = PRE_RED;
	public static final String F_NODE = TIMID_RED;
	public static final String F_FORT = TIMID_GREY;
	public static final String F_BUILDABLE = PRE_ORANGE;
	public static final String F_GUILD = PRE_YELLOW;

	//item values
	public static final String ITEM_WANT_HIGHER = inlineColor(extra.colorMix(Color.WHITE,extra.colorMix(Color.BLUE,Color.GREEN,.5f),.3f));
	public static final String ITEM_WANT_LOWER = inlineColor(extra.colorMix(Color.WHITE,extra.colorMix(Color.YELLOW,Color.ORANGE,.5f),.3f));
	public static final String ITEM_DESC_PROP = TIMID_BLUE;
	public static final String ITEM_VALUE = TIMID_MAGENTA;
	public static final String STAT_HEADER = inlineColor(extra.colorMix(Color.WHITE,extra.colorMix(Color.BLUE,Color.MAGENTA,.5f),.6f));

	//attributes
	public static final String ATT_TRUE = PRE_MAGENTA;
	public static final String ATT_EFFECTIVE = TIMID_MAGENTA;
	
	//attack picking
	public static final String ATK_BONUS = STAT_HEADER;
	public static final String ATK_WOUND_NORMAL = ITEM_DESC_PROP;
	public static final String ATK_WOUND_NEGATE = PRE_RED;
	public static final String ATK_WOUND_GRAZE = TIMID_RED;

	public static String colorBasedAtOne(double number, String plus, String minus, String empty) {
		String str = format2.format(number);
		if (number < 1) {
			return minus+str;
		}
		if (number > 1) {
			return plus+str;
		}
		return empty+str;
	}

	public static String formatPerSubOne(double percent) {
		String str = F_TWO_TRAILING.format(percent);
		return "%"+str.substring(1);
	}

	/**
	 * used to indicate that < 0 might be bad, > 0 might be good, and that =0 is not the same, but not bad or good
	 * caller should display = instead of a number if they are TRULY equal, higher up in the chain
	 * @param i
	 * @return green +1 OR red -1 OR grey ~ with no zero
	 */
	public static String colorBaseZeroTimid(int i) {
		if (i > 0) {
			return extra.TIMID_GREEN+"+"+i;
		}
		if (i < 0) {
			return extra.TIMID_RED+i;
		}
		return extra.TIMID_GREY+"~";
	}

	/**
	 * 
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= green/red/white 0.00
	 */
	public static final String hardColorDelta2(double to, double was) {
		if (to > was) {
			return extra.PRE_GREEN+"+"+format2.format(to-was);
		}
		if (to == was) {
			return extra.PRE_WHITE+"=0.00";
		}
		return extra.PRE_RED+"-"+format2.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}
	/**
	 * this variant will display a white = is equal, and nothing else
	 */
	public static final String hardColorDelta2Elide(double to, double was) {
		if (to > was) {
			return extra.PRE_GREEN+"+"+format2.format(to-was);
		}
		if (to == was) {
			return extra.PRE_WHITE+"=";
		}
		return extra.PRE_RED+"-"+format2.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}

	/**
	 * 
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= green/red/white 0.0
	 */
	public static final String hardColorDelta1(double to, double was) {
		if (to > was) {
			return extra.PRE_GREEN+"+"+format1.format(to-was);
		}
		if (to == was) {
			return extra.PRE_WHITE+"=0.0";
		}
		return extra.PRE_RED+"-"+format1.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}
	/**
	 * this variant will display a white = if equal, and nothing else
	 */
	public static final String hardColorDelta1Elide(double to, double was) {
		if (to > was) {
			return extra.PRE_GREEN+"+"+format1.format(to-was);
		}
		if (to == was) {
			return extra.PRE_WHITE+"=";
		}
		return extra.PRE_RED+"-"+format1.format(was-to);//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
	}
	/**
	 * integer,
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= timid green/timid red/white 0
	 */
	public static final String softColorDelta0(double to, double was) {
		if (to > was) {
			return extra.TIMID_GREEN+"+"+(int)(to-was);//round towards 0
		}
		if (to == was) {
			return extra.PRE_WHITE+"=";
		}//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
		return extra.TIMID_RED +"-"+(int)(was-to);//round towards 0 because we add - ourselves
	}

	public static final String softColorDelta2Elide(double to, double was) {
		if (to > was) {
			return extra.TIMID_GREEN+"+"+format2.format(to-was);
		}
		if (to == was) {
			return extra.PRE_WHITE+"=";
		}
		return extra.TIMID_RED+"-"+format2.format(was-to);
	}
	/**
	 * integer, Reversed from normal, so positive is bad and negative is good
	 * @param to the number moving into, is green if better
	 * @param was the old number, is red if better
	 * @return +/-/= timid red/timid green/white 0
	 */
	public static final String softColorDelta0Reversed(double to, double was) {
		if (to > was) {
			return extra.TIMID_RED+"+"+(int)(to-was);//round towards 0
		}
		if (to == was) {
			return extra.PRE_WHITE+"=";
		}//reverse order so it's positive so we can add - ourselves, in case it rounds to 0
		return extra.TIMID_GREEN +"-"+(int)(was-to);//round towards 0 because we add - ourselves
	}

	/**
	 * https://stackoverflow.com/a/13091759
	 * @param a - How deep the curve is - 0 <-> 1
	 * @return
	 */
	public static float bellCurve(float a){
		double x = extra.getRand().nextDouble();
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
		double x = 1-(2*Math.abs(extra.getRand().nextDouble()-midpoint));
		x = extra.clamp(x,0,1);
		return (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);
	}

	public static String spaceBuffer(int size) {
		// TODO upgrade to java 11 with " ".repeat() and just do that everywhere this is used
		return String.join("", Collections.nCopies(size," "));
	}

	/*
		public static <E> E randSet(Set<E> set) {
			assert !set.isEmpty();
			int i = extra.getRand().nextInt(set.size());
			for (E e: set) {
				if (i <= 0) {
					return e;
				}
				i--;
			}
			throw new RuntimeException("Wrong randset size");
		}*/

	public static boolean getEnumByteFlag(int flag, byte flags) {
		return (byte) (flags & (1 << flag)) != 0b0;
	}

	public static byte setEnumByteFlag(int flag, byte flags, boolean bool) {
		if (bool) {
			flags |= (1 << flag);
			return flags;
		}
		flags &= ~(1 << flag);
		return flags;
	}

	public static boolean getEnumShortFlag(int flag, short flags) {
		return (short) (flags & (1 << flag)) != 0b0;
	}

	public static short setEnumShortFlag(int flag, short flags, boolean bool) {
		if (bool) {
			flags |= (1 << flag);
			return flags;
		}
		flags &= ~(1 << flag);
		return flags;
	}

	/**
	 * adapted from
	 * https://stackoverflow.com/a/18083093
	 */
	public static byte extractByteFromInt(final int in, final int offset)
	{
		//final int rightShifted = in >>> offset;
		//final int mask = (1 << 8) - 1;
		return (byte) ((in >>> offset) & ((1 << 8) - 1));
	}

	/**
	 * adapted from
	 * https://stackoverflow.com/a/18083093
	 */
	public static int extractIntFromLong(final long l, final int offset)
	{
		//final long rightShifted = l >>> offset;
		//final long mask = (1L << 32) - 1L;
		return (int) ((l >>> offset) & ((1L << 32) - 1L));
	}

	/**
	 * adapted from
	 * https://stackoverflow.com/a/18083093
	 */
	public static byte extractByteFromLong(final long l, final int offset){
		//final long rightShifted = l >>> offset;
		//final long mask = (1L << 8) - 1L;
		return (byte) ((l >>> offset) & ((1L << 8) - 1L));
	}
	/**
	 * must be passed an UNSIGNED number,
	 * so a byte can't actually store the values passing in due to lack of size and
	 * java deleting information when auto(un)boxing
	 * <BR>
	 * WARNING: if you use a binary literal to create the int,
	 * it MUST have the first bit be a 0. If it is a 1,
	 * just pad it with a leading 0,
	 * which is needed to make it 'know' it's negative,
	 * otherwise it will treat it as a two's complement negative number.
	 * Said complement number is strange because it autoconverts it to the bit length instead of taking it literally as a literal.
	 * <br>
	 * always remember to set what this returns! (l parameter) it can't know where to put it!
	 */
	public static long setXInLong(final long l,final int length,final int start_offset, final long toSet) {
		final long allon = ~(0b0);
		return  ((~((allon << start_offset) & (allon >>> (64-(start_offset+length)))) & l) | (toSet) << (start_offset));
	}
	/**
	 * see setXInLong for more details
	 * <BR>
	 * WARNING: if you use a binary literal to create the int,
	 * it MUST have the first bit be a 0. If it is a 1,
	 * just pad it with a leading 0,
	 * <br>
	 * always remember to set what this returns! (l parameter) it can't know where to put it!
	 */
	public static int setXInInt(final int l,final int length,final int start_offset, final int toSet) {
		final int allon = ~(0b0);
		return  ((~((allon << start_offset) & (allon >>> (32-(start_offset+length)))) & l) | (toSet) << (start_offset));
	}

	/**
	 * with my insane commentary
	 */
	public static long setXInLongVerbose(final long l,final int length,final int start_offset, final long toSet) {
		//starting point at https://stackoverflow.com/a/22664554
		//"""return (UINT_MAX >> (CHAR_BIT*sizeof(int)-to)) & (UINT_MAX << (from-1));"""
		//it's really unfortunate that I couldn't find any remotely decent java resources on this, and only bad ones for other languages
		//are these people really counting from 1 or am I going insane
		//this took more trial and error and also just using different language resources than it should have
		//end result was more my work than anything else... what a sad world for search engines to live in
		//looking back I should have just looked for 'entire bitwise tutorial' instead of wanting a basic, universal function like this to be written down
		final long allon = ~(0b0);
		final long offBottom = allon << start_offset;
		final long offTop = allon >>> ((64-(start_offset+length)));
		final long flipoff = ~(offBottom & offTop);
		final long set = (toSet) << (start_offset);//need to cast to long so if it runs up against the end of the bits it doesn't become negative
		final long prep = (flipoff & l);

		System.out.println("in"+UnitAssertions.pad(l)+" all" + UnitAssertions.pad(allon));
		System.out.println("top"+UnitAssertions.pad(offTop)+" bot" + UnitAssertions.pad(offBottom));
		System.out.println("off"+UnitAssertions.pad(flipoff));
		System.out.println("prep"+ UnitAssertions.pad(prep)+" set"+UnitAssertions.pad(set));
		return  (prep | set);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that
	 * <br>
	 * b must be a short, int, or long, due to unsigned issues
	 */
	public static long setByteInLong(final long l,final long toset, final int offset) {
		return setXInLong(l,8,offset,toset);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that
	 * <br>
	 * b must be an int or long, due to unsigned issues
	 */
	public static long setShortInLong(final long l,final long toset, final int offset) {
		return setXInLong(l,16,offset,toset);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that
	 * <br>
	 * b must be a long, due to unsigned issues
	 */
	public static long setIntInLong(final long l,final long toset, final int offset) {
		return setXInLong(l,32,offset,toset);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that through setByteInLong
	 * <br>
	 * b must be a short, int, or long, due to unsigned issues
	 * <br>
	 * 0 <= num <= 7 (0 indexed)
	 */
	public static long setNthByteInLong(final long l,long toset, final int number_of_byte) {
		assert toset <= 255;
		assert toset >= 0;
		/*if (toset > Byte.MAX_VALUE) {
				toset-=Byte.MAX_VALUE;
			}*/
		return setXInLong(l,8,number_of_byte*8,toset);
	}
	/**
	 * see disclaimers in 'setXInLong', this just wraps setXInInt through setByteInInt
	 * <br>
	 * b must be a short, int, or long, due to unsigned issues
	 * <br>
	 * 0 <= num <= 7 (0 indexed)
	 */
	public static int setNthByteInInt(final int l,final int toset, final int number_of_byte) {
		assert toset <= 255;
		assert toset >= 0;
		/*if (toset > Byte.MAX_VALUE) {
				toset-=Byte.MAX_VALUE;
			}*/
		return setXInInt(l,8,number_of_byte*8,toset);
	}

	/**
	 * returns it as a NUMBER in an int. (0 indexed)
	 * <br>
	 * effectively reads the byte as unsigned
	 */
	public static int intGetNthByteFromLong(final long l, final int number_of_byte)
	{
		//final long rightShifted = l >>> number_of_byte*8;
		//final long mask = (1L << 8) - 1L;
		return (int) ((l >>> number_of_byte*8) & ((1L << 8) - 1L));
	}

	/**
	 * returns it as a NUMBER in an int. (0 indexed)
	 * <br>
	 * effectively reads the byte as unsigned
	 */
	public static int intGetNthByteFromInt(final int l, final int number_of_byte)
	{//doesn't need to be different
		return intGetNthByteFromLong(l,number_of_byte);
	}

	/**
	 * returns it as a NUMBER in an int. (0 indexed)
	 * <br>
	 * effectively reads the short as unsigned
	 */
	public static int intGetNthShortFromLong(final long l, final int number_of_short)
	{
		//final long rightShifted = l >>> number_of_short*16;
		//final long mask = (1L << 16) - 1L;
		return (int) ((l >>> number_of_short*16) & ((1L << 16) - 1L));
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

	public static String padIf(String str) {
		if (str != "") {
			return str+" ";
		}
		return str;
	}

}

