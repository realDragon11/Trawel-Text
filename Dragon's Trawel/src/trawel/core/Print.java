package trawel.core;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Stack;

import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.TagFormatter;
import trawel.threads.ThreadData;

public class Print {

	private static Boolean printMode = false;
	//private static long lastMod = -1;
	private static String printStuff = "";
	private static Stack<Boolean> printStack = new Stack<Boolean>();
	private static final int debugChunkPer = 10;
	private static boolean debugPrint = false;
	private static int debugChunk = 0;
	public static final DecimalFormat F_TWO_TRAILING = new java.text.DecimalFormat("0.00");
	public static final DecimalFormat F_WHOLE = new java.text.DecimalFormat("0");
	public static final DecimalFormat format1 = new java.text.DecimalFormat("0.0");
	public static final DecimalFormat format2 = F_TWO_TRAILING;

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
		if (!ThreadData.isMainThread()) {
			return;
		}
		printMode = disable;
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

	public static final void println() {
		println("");
	}

	public static final void println(String str) {
		if (!ThreadData.isMainThread()) {
			return;
		}
		if (!printMode || debugPrint) {
			//mainGame.log(str);
			printStuff+=str;
			Networking.printlocalln(stripPrint(printStuff));
			detectInputString(stripPrint(printStuff));
			Networking.printlnTo(formatTags(printStuff));
			printStuff = "";
	
			if (debugPrint) {
				debugChunk++;
				if (debugChunk > debugChunkPer) {
					Input.inString();
					debugChunk = 0;
				}
			}
		}
	
	}

	public static final void print(String str) {
		if (!ThreadData.isMainThread()) {
			return;
		}
		if (!printMode) {
			printStuff+=str;
		}
	}
	
	protected static final String formatTags(String str) {
		StringBuilder out = new StringBuilder(str);
		int index = out.indexOf("[");
		while (index != -1) {
			int lastindex = out.indexOf("]",index);
			//replace, but leave in []'s
			out.replace(index+1,lastindex,TagFormatter.get(out.substring(index+1,lastindex)));
			//"]" might have shifted back or forwards, but we know where it starts and can start searching from there instead
			index = out.indexOf("[",index+1);
		}
		TagFormatter.clearStack();
		return out.toString();
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
	
	/**
	 * prints to System.out, but still strips printing of tags beforehand
	 */
	public static void debugPrint(String str) {
		System.out.println(stripPrint(str));
	}

	public static void detectInputString(String str) {
		if (str.length() > 1) {
			if (Character.isDigit(str.charAt(0)) && str.charAt(1) == " ".charAt(0)) {
				Networking.send("Input|" + str.charAt(0) +"|"+str+"|");
			}
		}
	}

	/**
	 * true = do not print
	 * <br>
	 * false = can print
	 * @return if you can't print
	 */
	public static final Boolean getPrint() {
		if (!ThreadData.isMainThread()){
			return true;
		}
		return printMode;
	}
	
	/**
	 * @return if you should attempt to println
	 */
	public static final Boolean canPrint() {
		if (!ThreadData.isMainThread()){
			return false;
		}
		return !printMode;
	}

	public static String format2(double d) {
		String str = format2.format(d);
		if (d > 0) {
			str = "+" + str;
		}
		return(str);
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
			String str = Print.stripPrint(strs[i]);
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
		Print.println(TrawelColor.PRE_WHITE);
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
		if (!ThreadData.isMainThread()){
			return;
		}
		printStack.push(printMode);
		printMode = true;
	}

	public static void popPrintStack() {
		if (!ThreadData.isMainThread()){
			return;
		}
		printMode = printStack.pop();
	}

	public static String formatPerSubOne(double percent) {
		String str = F_TWO_TRAILING.format(percent);
		return "%"+str.substring(1);
	}

	public static String spaceBuffer(int size) {
		// TODO upgrade to java 11 with " ".repeat() and just do that everywhere this is used
		return String.join("", Collections.nCopies(size," "));
	}

	public static String padIf(String str) {
		if (str != "") {
			return str+" ";
		}
		return str;
	}

}
