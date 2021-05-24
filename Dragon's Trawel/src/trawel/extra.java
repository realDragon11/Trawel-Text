package trawel;


import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class extra {
/**
 * Brian Malone
 * Various static methods to be used in other classes
 * is in another package so import statements can work
 * 2/5/2018
 */
	
	public static Boolean printMode = false;
	private static Boolean oldPrintMode = false;
	private static long lastMod = -1;
	private static String printStuff = "";
	
	//static methods

	
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
		return options[(int)(Math.random()*(double)options.length)];
	}
	
	public static <E> E choose(E... options) {
		return options[(int)(Math.random()*(double)options.length)];
	}
	
	/**
	 * Makes sure that something isn't less than zero, else it returns 0
	 * @param (int)
	 * @return (int)
	 */
	public static int zeroOut(int i) {
		if (i > 0){
			return i;
		}
		return 0;
	}
	
	/**
	 * Makes sure that something isn't less than zero, else it returns 0
	 * @param (double)
	 * @return (double)
	 */
	public static double zeroOut(double i) {
		if (i > 0){
			return i;
		}
		return 0;
	}
	/**
	 * Has a (a) in (b) chance of returning true
	 * @param a (int)
	 * @param b (int)
	 * @return (boolean)
	 */
	public static boolean chanceIn(int a,int b) {
		if (((double)a/b) < Math.random()) {return false;}
		return true;
	}
	
	/**
	 * Takes a string and makes the first letter capital
	 * @param str (String)
	 * @return Str (String)
 	 */
	public static String capFirst(String str){
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * Decides whether to become 'are' or 'is' based on plurality of the input
	 * @param str (String)
	 * @return (String)
	 */
	public static String pluralIs(String str) {
		if (str.endsWith("s")){
			return "are";
		}else {
			return "is";
		}
	}
	
	/**
	 * Changes the output to be the 'output.txt' file, or the console.
	 * @param file - (boolean) true if you want to swap to a file output, false if you want to swap to the standard output system.
	 */
	public static void changePrint(boolean file) {
		printMode = file;
		/*if (file) {	//https://stackoverflow.com/a/1994283/9320090
			//redirecting printing
			PrintStream out;
			try {
				out = new PrintStream(new FileOutputStream("output.txt"));
				System.setOut(out);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}}else {
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			}*/
	}
	
	/**
	 * Formats a double into a string that looks nicer.
	 * @param str - (double)
	 * @return (String)
	 */
	public static String format(double str) {
		java.text.DecimalFormat format2 = new java.text.DecimalFormat("0.00");
		return(format2.format(str));
	}
	
	//extra.linebreak();
		public static void linebreak() {
			extra.println("------------");
			//clear the synth
		}
		
		public static boolean yesNo() {
			String str;
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
						return ini == 1;
						/*
						try {
							String str2 = System.getenv("APPDATA")+"\\Local\\Trawel\\buff.txt";
							str2 = str2.replace("Roaming\\","");
							File f = new File(str2);
							if (lastMod == -1) {
								lastMod = f.lastModified();
							}
							if (f.lastModified() < lastMod+50) {
								throw new Exception();
							}
							lastMod = f.lastModified();
							if (!f.exists()){
								throw new Exception();
							}
							InputStream input = new FileInputStream(f);
							int temp = input.read();
							input.close();
							str = temp +"";
							break;
						} catch (Exception e) {	}
						try {
							if (System.in.available() > 0) {
								str = mainGame.scanner.next();break;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}*/
					//}
					
					}else {
				str = mainGame.scanner.next();
					
				extra.linebreak();
				str = str.toLowerCase();
				//extra.println(str);
				if (str.equals("yes") || str.equals("y")|| str.equals("1")) {
					return true;
				}
				if (str.equals("no") || str.equals("n") || str.equals("0") || str.equals("9")) {
					return false;
				}
				extra.println("Yes or No?");
						
				}
			}
		}
		public static int randRange(int i, int j) {
			return (int)(Math.random()*(j+1-i))+i;
		}

		public static int inInt(int max) {
			String str;
			int in =0;
			Networking.sendStrong("Entry|Activate|" + max + "|");
			if ((Networking.connected() && mainGame.GUIInput) || Networking.autoconnectSilence) {
				int ini=  Networking.nextInt();
				while(ini < 1 || ini > max) {
					extra.println("Please type a number from 1 to " + max + ".");
					ini=  Networking.nextInt();
					if (ini == -99 || ini == -1) {
						Networking.unConnect();
						throw new RuntimeException("invalid input stream error");
					}
				}
				extra.linebreak();
				return ini;
				/*while(true) {
				
				try {
					String str2 = System.getenv("APPDATA")+"\\Local\\Trawel\\buff.txt";
					str2 = str2.replace("Roaming\\","");
					File f = new File(str2);
					if (lastMod == -1) {
						lastMod = f.lastModified();
					}
					if (f.lastModified() < lastMod+50) {
						throw new Exception();
					}
					lastMod = f.lastModified();
					if (!f.exists()){
						throw new Exception();
					}
					InputStream input = new FileInputStream(f);
					int temp = input.read();
					input.close();
					extra.linebreak();
					if((temp < 1 || temp > max)) {
						extra.println("You've encountered a disk error. Please re-enter your input");
						extra.println("1 ");
						extra.println("2 ");
						extra.println("3 ");
						extra.println("4 ");
						extra.println("5 ");
						extra.println("6 ");
						extra.println("7 ");
						extra.println("8 ");
						extra.println("9 ");
						throw new Exception("blah");
					}
					return temp;
				} catch (Exception e) {	}
				
				try {
					if (System.in.available() > 0) {
						str = mainGame.scanner.next();
						try {
							in = Integer.parseInt(str);
						}catch(NumberFormatException e) {
							if (!str.equals("\n")) {
							in = 0;}
						}catch(Exception e) {
							extra.println("error");
						}
						if((in < 1 || in > max)) {
							extra.println("Please type a number from 1 to " + max + ".");
							continue;
						}extra.linebreak();
						return in;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				
				
			}*/
			
			
			}else {
				
			do{
			str = extra.inString(); 
			try {
				in = Integer.parseInt(str);
			}catch(NumberFormatException e) {
				if (!str.equals("\n")) {
				in = 0;}
			}catch(Exception e) {
				extra.println("error");
			}
			if((in < 1 || in > max)) {
				extra.println("Please type a number from 1 to " + max + ".");
			}
			} while (in < 1 || in > max); 
			extra.linebreak();
			return in;
			}
		}
		
		public static void println() {
			println("");
		}
		
		public static void println(String str) {
			if (!printMode) {
			System.out.println(stripPrint(str));
			detectInputString(stripPrint(printStuff +str));
			Networking.send("println|"+ printStuff + str + "|");
			printStuff = "";
			}
			
		}
		
		public static void print(String str) {
			if (!printMode) {
			System.out.print(stripPrint(str));
			printStuff+=str;}
		}
		
		private static String stripPrint(String str) {
			int index = str.indexOf('[');
			while (index != -1) {
				int lastindex = str.indexOf(']');
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

		public static Boolean getPrint() {
			return printMode;
		}

		public static String format2(double d) {
			java.text.DecimalFormat format3 = new java.text.DecimalFormat("0.00");
			String str = format3.format(d);
			if (d > 0) {
				str = "+" + str;
			}
			return(str);
		}

		public static double hrandom() {
			if (randRange(1,5) != 5) {
			return  (((double)randRange(45,55))/100.0);}else {
				return Math.random();
			}
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

		public static void enablePrintSubtle() {
			printMode = oldPrintMode;
		}

		public static void disablePrintSubtle() {
			oldPrintMode = printMode;
			printMode = true;
			
		}

		public static <E> E randList(ArrayList<E> list) {
			return list.get(randRange(0,list.size()-1));
			
		}
		
		public static <E> E randList(List<E> list) {
			return list.get(randRange(0,list.size()-1));
			
		}

		public static double clamp(double d, double min, double max) {
			return Math.min(max, Math.max(d, min));
		}
		public static int clamp(int d, int min, int max) {
			return Math.min(max, Math.max(d, min));
		}

		public static int menuGo(MenuGenerator mGen) {
			List<MenuItem> mList = new ArrayList<MenuItem>();
			mList = mGen.gen();
			int v = 1;
			for (MenuItem m: mList) {
				if (m.canClick()) {
				extra.println(v + " " + m.title());
				v++;}else {
					extra.println(m.title());
				}
			}
			while (true) {
				List<MenuItem> subList = new ArrayList<MenuItem>();
				mList.stream().filter(m -> m.canClick() == true).forEach(subList::add);
				int val = extra.inInt(subList.size())-1;
				boolean ret = subList.get(val).go();
				//mList = mGen.gen();
				if (ret) {
					return val;
				}
				mList = mGen.gen();
				v = 1;
				for (MenuItem m: mList) {
					if (m.canClick()) {
						extra.println(v + " " + m.title());
						v++;}else {
							extra.println(m.title());
						}
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
		
		public static float lerp(float a, float b, float f) 
		{
		    return (a * (1.0f - f)) + (b * f);
		}

		public static Color colorMix(Color c1, Color c2, float f) {
			return new Color((int) extra.lerp(c1.getRed(),c2.getRed(), f),(int) extra.lerp(c1.getGreen(),c2.getGreen(), f),(int) extra.lerp(c1.getBlue(),c2.getBlue(), f));
		}

		public static String inlineColor(Color col) {
			return "[#"+Integer.toHexString(col.getRGB()).substring(2)+"]";
		}
		
		/**
		 * https://stackoverflow.com/a/13091759
		 * @param a - How deep the curve is - 0 <-> 1
		 * @return
		 */
		public static float bellCurve(float a){
			double x = Math.random();
			return (float) (4*a*Math.pow(x,3) - 6*a*Math.pow(x,2) + 2*a*x + x);
		}
		
		public static float curveLerp(float start, float end, float depth) {
			return extra.lerp(start, end, bellCurve(depth));
		}
		
		public static float lerpDepth(float start, float end, float f,float depth) {
			float x = (f-start)/(end-start);
			return (float) (4*depth*Math.pow(x,3) - 6*depth*Math.pow(x,2) + 2*depth*x + x);
		}
		
}

