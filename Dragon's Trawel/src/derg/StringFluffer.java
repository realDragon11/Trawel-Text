package derg;

import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * StringFluffer contains find and replace code centered around variable replacements
 * They can be chained, and have behavior to aid in debugging their fallbacks.
 * @author realDragon11 "dragon"
 * created 7/16/2023
 */
public class StringFluffer {

	
	public static Pattern commandMatch = Pattern.compile("\\|[^!(]+!?([^)]+)");
	public static Pattern subMatch = Pattern.compile("\\|sub!?([^)]+)");
	public static Pattern commandDataMatch = Pattern.compile("([^)]+");//note you need to start from index 1
	/*
	 * regex breakdown:
	 * \\ escapes the escape character in case pipe is reserved
	 * sub is the name of the command
	 * ! is a special code added to a regex for debugging that indicates that the fluffer saw the command, but did not have the ability
	 * to deal with the words within. This is because a chained fluffer later might be able to deal with it
	 * if a fluffer doesn't know a command, it won't add a !- if it does but doesn't know the words, it will
	 * 
	 * if it finds a malformed command it will attempt to replace the starting pipe with a ! and make no other changes
	 * 
	 * [^)] tells it that we don't want to match close paren- this is useful because we want it to grab all
	 * the characters until the next paren, and other regex would still backtrack when doing so
	 * the + after means 'one or more'- empty () will be ignored
	 */
	
	//StringFluffers call on StringResults, so they can share lists
	public Map<String,StringResult> shufflers = new HashMap<String,StringResult>();
	/**
	 * command syntax is type:sublist
	 * types must be at least two characters long, and alphabet only
	 * case determines behavior, all matches are lowercase
	 * if only the first letter is uppercase (only the first two are checked) then the string will get capFirst'd
	 * if both the first and second letters are uppercase the whole thing will get toUppercase
	 * 
	 * behavior does not actually require this have a : or type and sublist, but for potential uses elsewhere this is used
	 * it would work with any string of two characters or more
	 */
	
	//TODO: decide if I want some 'ignore X sublists if present' for better chain control
	/**
	 * Reject results that occur ANYWHERE in the string we're dealing with, up to X times per command attempt
	 * the nth attempt where n = the dupecount will ignore this restriction
	 * typically should be set to 10 at max to cut off bad cases
	 * applied in metadata, 0 is no rejections. -1 used as a command code, so do not set to that
	 * 
	 * suggested default is 0 (off)
	 */
	public final int globalDupes;
	/**
	 * Reject results that occur in a previous command result, up to X times per command attempt
	 * the nth attempt where n = the dupecount will ignore this restriction
	 * global and local dupes are not added together, whichever limit is hit first is applied
	 * each time it fails, it will try another random list it has, if in a multicommand
	 * 
	 * typically should be set to 10 at max to cut off bad cases
	 * applied in metadata, 0 is no rejections. -1 used as a command code, so do not set to that
	 * 
	 * suggested default is 3
	 */
	public final int localDupes;
	
	/**
	 * when strictdupe = true, dupes only count if the found dupe doesn't have alphabetical characters on either side
	 * so, ".test!" and " test)" would both still count under strict, but "atest" would not
	 * 
	 * when off, localdupes checks if any prior results are contained with the new result as well
	 * 
	 * suggested default is true
	 */
	public final boolean strictDupe;
	
	private boolean realOperation = false;
	
	/**
	 * SHOULD NOT BE SET ANYHWHERE BUT ON PROCESS START
	 */
	private MetaData currentTask;
	
	/**
	 * for debugging purposes
	 * @return whether the fluffer completed a command last process- ! additions don't count.
	 */
	public boolean operated() {
		return realOperation;
	}
	/**
	 * default StringFluffer can handle all base commands, and is good as a fallback
	 */
	public StringFluffer() {
		globalDupes = 0;
		localDupes = 3;
		strictDupe = true;
	}

	/**
	 * processes all command in string. Default metadata
	 * @param str
	 * @return a new string, or str if there was no changes
	 */
	public String process(String str) {
		realOperation = false;//we didn't complete a command yet
		currentTask = new MetaData(str);
		//process should operate backwards
		return str;
	}
	/**
	 * processes one command
	 * @param command
	 * @return the new string, or null if the operation didn't go through
	 * if the switchLookup was able to detect a malformed request, it will return the command with the pipe replaced with a !
	 */
	public String switchLookup(String command) {
		//result = command.replace("|","!"); //this isn't actually malformed :|
		String[] dataArr = commandDataMatch.matcher(command).group().split(",");
		StringResult[] srArr = new StringResult[dataArr.length];
		for (int i = 0;i < dataArr.length; i++) {
			StringResult cur = shufflers.get(dataArr[i]);
			if (cur == null) {
				return null;
			}
			srArr[i] = cur;
		}
		String result = null;
		int rejections = 0;
		while (result == null) {
			result = srArr[(int)(Math.random()*srArr.length)].next();
			if (rejections < currentTask.getGlobalDupes()) {
				Pattern resultMatch = resultMatcher(result);
				if (resultMatch.matcher(currentTask.string).find()) {
					rejections++;
					result = null;
					continue;
				}
			}
			if (rejections < currentTask.getLocalDupes()) {
				if (currentTask.getStrictDupe() == true) {
					if (currentTask.results.contains(result)) {
						rejections++;
						result = null;
						continue;
					}
				}else {
					for (String s: currentTask.results) {
						if (s.contains(result) || result.contains(s)) {
							rejections++;
							result = null;
							continue;
						}
					}
				}
			}
		}
		if (result != null) {
			currentTask.results.add(result);
		}
		return result;
	}
	
	//private to avoid external sources calling it while we don't have metadata
	private Pattern resultMatcher(String str) {
		if (currentTask.getStrictDupe()) {
			return Pattern.compile(""+Pattern.quote(str)+"");
		}else {
			return Pattern.compile(Pattern.quote(str));
		}		
	}
	
	/**
	 * lookup a replacement. Advances any StringResult state, which is fine even if the caller rejects the returned string
	 * @param data; the sublist to shuffle from
	 * @return null if no sublist found, or the new string
	 */
	public String lookup(String data) {
		StringResult sublist = shufflers.get(data.toLowerCase());
		if (sublist == null) {
			return null;
		}
		return sublist.next();
	}
	
	public MetaData customMeta(String str, int globalDupes, int localDupes,boolean strictDupe) {
		return new MetaData(str,globalDupes,localDupes,strictDupe);
	}
	
	protected class MetaData {
		//flag: avoid substring elsewhere
		//flag: avoid doublepicks but substring is fine
		//numberflag: avoid word proximity
		public final String string;
		private final int globalDupesL;
		private final int localDupesL;
		private final int strictDupeL;
		public final List<String> results;
		public MetaData(String str) {
			string = str;
			globalDupesL = -1;
			localDupesL = -1;
			strictDupeL = -1;
			results = new ArrayList<String>();
		}
		public MetaData(String str, int globalDupes, int localDupes,boolean strictDupe) {
			string = str;
			globalDupesL = globalDupes;
			localDupesL = localDupes;
			strictDupeL = 1;
			results = new ArrayList<String>();
		}
		
		public int getGlobalDupes() {
			return globalDupesL == -1 ? globalDupes : globalDupesL;
		}
		public int getLocalDupes() {
			return localDupesL == -1 ? localDupes : localDupesL;
		}
		
		public boolean getStrictDupe() {
			if (strictDupeL == -1) {
				return strictDupeL == 1 ? true : false;
			}
			return strictDupe;//from top level class
		}
		
	}
}
