

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class extra {
/**
 * Brian Malone
 * Various static methods to be used in other classes
 * is in another package so import statements can work
 * 2/5/2018
 */
	
	static Boolean printMode = false;
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
				if (Networking.connected() && mainGame.GUIInput) {
					while(true) {
						
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
						}
					}
					
					}else {
				str = mainGame.scanner.next();
					}
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
		public static int randRange(int i, int j) {
			return (int)(Math.random()*(j+1-i))+i;
		}

		public static int inInt(int max) {
			String str;
			int in =0;
			Networking.sendStrong("Entry|Activate|" + max + "|");
			if (Networking.connected() && mainGame.GUIInput) {
			while(true) {
				
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
				
			}
			
			
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
			System.out.println(str);
			detectInputString(printStuff +str);
			Networking.send("println|"+ printStuff + str + "|");
			printStuff = "";
			}
			
		}
		
		public static void print(String str) {
			if (!printMode) {
			System.out.print(str);
			printStuff+=str;}
		}
		
		private static void detectInputString(String str) {
			if (str.length() > 1) {
				if (str.charAt(1) == " ".charAt(0)) {
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

		public static double clamp(double d, double min, double max) {
			return Math.min(max, Math.max(d, min));
		}
		
}

