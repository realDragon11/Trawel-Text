package trawel;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.github.tommyettinger.random.*;

import derg.menus.MenuGenerator;
import derg.menus.MenuGeneratorPaged;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.personal.people.Player;
import trawel.towns.World;

import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

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
		temp.world = Player.getWorld();
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
	 * @param (int)
	 * @return (int)
	 */
	public static final int zeroOut(int i) {
		return Math.max(i,0);
		/*if (i > 0){
			return i;
		}
		return 0;*/
	}
	
	/**
	 * Makes sure that something isn't less than zero, else it returns 0
	 * @param (double)
	 * @return (double)
	 */
	public static final double zeroOut(double i) {
		return Math.max(i,0);
		/*
		if (i > 0){
			return i;
		}
		return 0;*/
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
	
	public static final String CHAR_INSTANTS = "_";
	//was having trouble finding something that narrator read 
	//should probably make instants display an actual icon in the graphical with an insert sprite code
	//should handle replacement on gms2.3 side for compat + brevity
	
	public static final String CHAR_HITCHANCE = "%";
	//also probably something better out there
	
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
			String str;
			trawel.threads.BlockTaskManager.start();
			while (true) {
				extra.println("1 yes");
				extra.println("9 no");
				Networking.sendStrong("Entry|yesno|");
				if ((Networking.connected() && mainGame.GUIInput)  || Networking.autoconnectSilence) {
					//while(true) {
					int ini = Networking.nextInt();
					while(ini != 1 && ini != 9) {
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

				}else {
					
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

				}
			}
		}
		public static final int randRange(int i, int j) {
			//return (int)(Math.random()*(j+1-i))+i;
			return getRand().nextInt((j+1)-i)+i;
		}
		
		public static final int inInt(int max) {
			return inInt(max,false);
		}

		public static final int inInt(int max, boolean alwaysNine) {
			Networking.sendStrong("Entry|Activate|" + max + "|");
			trawel.threads.BlockTaskManager.start();
			int ini=  Networking.nextInt();
			while(!(alwaysNine && ini == 9) && (ini < 1 || ini > max)) {
				if (ini != -2) {//silent loading
					extra.println("Please type a number from 1 to " + max + "." + (alwaysNine ? " (or 9)" : ""));
				}
				ini= Networking.nextInt();
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
			if (!printMode) {
				mainGame.log(str);
				Networking.printlocalln(stripPrint(printStuff+str));
				detectInputString(stripPrint(printStuff +str));
				Networking.printlnTo(printStuff + str);
				printStuff = "";
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

		/*
		public static final double hrandom() {
			if (randRange(1,5) != 5) {
			return  (((double)randRange(45,55))/100.0);}else {
				return Math.random();
			}
		}*/
		
		public static final double hrandom() {
			   return ((Long.bitCount(getRand().nextLong()) - 32. + getRand().nextDouble() - getRand().nextDouble()) / 66.0 + 0.5);
		}//given by TEtt from squidsquad
		
		public static final float hrandomFloat() {
			return ((Long.bitCount(getRand().nextLong()) - 32f + getRand().nextFloat() - getRand().nextFloat()) / 66f + 0.5f);
		}
		
		public static void specialPrint(int[] in,String...strs) {
			int j = 0;
			while (j < in.length) {
				while(!strs[j].isEmpty() && in[j] > 0) {
					print(strs[j].substring(0, 1));
					if (strs[j].length() > 1){
					strs[j] = strs[j].substring(1,strs[j].length());}else {
						strs[j] = "";
					}
					
					in[j] -=1;
				}
				if (in[j] > 0 && j < in.length-1) {
					while (in[j] > 0) {
						if (strs[j].length() > 1){
							strs[j] = strs[j].substring(1,strs[j].length());}else {
								strs[j] = "";
							}
					print(" ");
					in[j] -=1;
					}
				}
				
				
				j++;
			}
			extra.println();
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
			List<MenuItem> subList;
			while (true) {
				mList = mGen.gen();
				if (mList == null) {
					return -1;//used for nodes so they can force interactions cleanly
				}
				v = 1;
				forceLast = false;
				subList = new ArrayList<MenuItem>();
				for (MenuItem m: mList) {
					if (m.forceLast()) {
						subList.add(m);
						extra.println("9 " + m.title());
						forceLast = true;
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
					val = extra.inInt(subList.size()-1,true)-1;
				}
				boolean ret;
				if (val < subList.size()) {
					ret = subList.get(val).go();
				}else {
					ret = subList.get(subList.size()-1).go();
				}
				 
				//mList = mGen.gen();
				if (ret) {
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
							if (true == true) {//TODO: figure out a last page condition
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

		public static final Color colorMix(Color c1, Color c2, float f) {
			return new Color((int) extra.lerp(c1.getRed(),c2.getRed(), f),(int) extra.lerp(c1.getGreen(),c2.getGreen(), f),(int) extra.lerp(c1.getBlue(),c2.getBlue(), f));
		}

		public static final String inlineColor(Color col) {
			return "[#"+Integer.toHexString(col.getRGB()).substring(2)+"]";
		}
		
		//NOTE: predefined color mixes inlined
		public static final String PRE_WHITE = inlineColor(Color.WHITE);
		public static final String PRE_RED = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.5f));
		public static final String PRE_ORANGE = inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f));
		public static final String PRE_YELLOW = inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.5f));
		public static final String PRE_BLUE = inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f));
		public static final String PRE_GREEN = inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f));
		public static final String PRE_MAGENTA = inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.5f));
		
		public static final String PRE_ROAD = PRE_GREEN;
		public static final String PRE_SHIP = PRE_GREEN;
		public static final String PRE_TELE = PRE_GREEN;
		
		//timid colors that are slight, used for bad and good hinting
		public static final String TIMID_GREEN = inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.8f));
		public static final String TIMID_RED = inlineColor(extra.colorMix(Color.RED,Color.WHITE,.8f));
		/** 
		 * do not use for no change whatsoever, use white for that- this is a change that might be bad or good but is a net 0 to this stat
		 */
		public static final String TIMID_GREY = inlineColor(extra.colorMix(Color.BLACK,Color.WHITE,.95f));
		
		//not directly used yet
		public static final String TIMID_BLUE = inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.8f));
		public static final String TIMID_MAGENTA = inlineColor(extra.colorMix(Color.MAGENTA,Color.WHITE,.8f));
		
		public static final String COLOR_NEW = inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f));
		public static final String COLOR_SEEN = inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.5f));
		public static final String COLOR_BEEN = inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f));
		public static final String COLOR_OWN = inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f));
		
		/**
		 * used for "it's a miss!" after the attack proper
		 */
		public static final String AFTER_ATTACK_MISS = extra.inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.3f));
		public static final String ATTACK_DAMAGED = PRE_ORANGE;
		public static final String ATTACK_KILL = PRE_RED;
		public static final String ATTACK_BLOCKED = PRE_BLUE;
		public static final String ATTACK_MISS = PRE_YELLOW;
		
		public static final String F_SPECIAL = PRE_MAGENTA;
		public static final String F_SERVICE = PRE_BLUE;
		public static final String F_AUX_SERVICE = TIMID_BLUE;
		public static final String F_SERVICE_MAGIC = TIMID_MAGENTA;
		public static final String F_COMBAT = PRE_RED;
		public static final String F_NODE = TIMID_RED;
		public static final String F_FORT = TIMID_GREY;
		public static final String F_BUILDABLE = PRE_ORANGE;
		public static final String F_GUILD = PRE_YELLOW;
		
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
		
}

