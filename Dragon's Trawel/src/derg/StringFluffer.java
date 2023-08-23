package derg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import trawel.extra;

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
	
	public static String fluffChainFinal(String str, StringContext context, StringFluffer ...fluffs) {
		for (StringFluffer fluff: fluffs) {
			str = fluff.processContext(str,context);
		}
		return str.replaceAll("\\|","$");
	}
	
	public static String staticFinalize(String str) {
		return str.replaceAll("\\|","$");
	}
	
	public String finalize(String str) {
		return lastResult.replaceAll("\\|","$");
	}

	
	public static Pattern commandMatch = Pattern.compile("\\|[^!\\(]+!?\\([^)]+\\)");
	public static Pattern subMatch = Pattern.compile("\\|sub!?\\([^)]+\\)");
	public static Pattern commandDataMatch = Pattern.compile("\\([^)]+");//note you need to start from index 1
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
	private Map<String,StringResult> shufflers = new HashMap<String,StringResult>();
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
	private String lastResult;
	private StringContext context = null;
	
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
	
	public StringFluffer addMapping(String name, StringResult sublist) {
		shufflers.put(name, sublist);
		return this;
	}
	
	//rip tuples
	public Object[][] getMappings() {
		
		Object[][] arr = new Object[shufflers.size()][2];
		int i = 0;
		for (String key: shufflers.keySet()) {
			arr[i][0] = key;
			arr[i][1] = shufflers.get(key);
			i++;
		}
		return arr;
	}

	/**
	 * processes all command in string. Default metadata and no context
	 * @param str
	 * @return a new string, or str if there was no changes
	 */
	public String process(String str) {
		context = null;
		currentTask = new MetaData(str);
		return processInternal(str);
	}
	
	//TODO: instead of doing context, we could do binary tree shufflers that work
	//on keyword strings like 'wsb' (weak sharp blunt), which would be seen as weak (low damage relatively) sharp primary blunt secondary
	//would also likely need some metadata for weapon type
	//and the shufflers would work better if they interacted to avoid repeats with other shufflers
	//maybe nested pickers that have an ultimate pool of shufflers somehow?
	//might be able to make a more complex type of tree and have 'paths'
	//and it takes the lowest node that fits and moves it back up to the top
	//where it then goes down as normal
	//this would increase the running time by a fair amount because it would have to view each level in order
	//but might be worth it for player facing content
	public String processContext(String str,StringContext context) {
		this.context = context;
		currentTask = new MetaData(str);
		return processInternal(str);
	}
	private String processInternal(String str) {
		realOperation = false;//we didn't complete a command yet
		String working = str;
		//process should operate backwards to avoid replacement issues, but matcher doesn't seem to allow that, so we store the results instead
		List<String> befores = new ArrayList<String>();
		List<String> afters = new ArrayList<String>();
		Matcher m = subMatch.matcher(working);
		while (m.find()) {
			String temp = working.substring(m.start(),m.end());
			befores.add(temp);
			afters.add(switchLookup(temp));
		}
		
		for (int i = 0; i < befores.size();i++) {//we don't actually need to go backwards anymore but it saves some steps in the for checker
			working = working.replaceFirst(Pattern.quote(befores.get(i)),afters.get(i));
		}//note that hilariously it will not respect the order if the same command is used more than once, but that shouldn't matter
		//that could cause some really weird behavior but that would mean state elsewhere impacted a replace, which is
		//not how this class is supposed to work
		lastResult = working;
		return working;
	}
	
	public String processCustom(String str, int globalDupes, int localDupes,boolean strictDupe) {
		context = null;
		currentTask= new MetaData(str,globalDupes,localDupes,strictDupe);
		return processInternal(str);
	}
	/**
	 * processes one command
	 * @param command
	 * @return the new string, or the same string with an inserted ! before the first paren if we didn't know how to deal
	 * if the switchLookup was able to detect a malformed request, it will return the command with the pipe replaced with a !
	 */
	public String switchLookup(String command) {
		//result = command.replace("|","!"); //this isn't actually malformed :|
		//ugh this is the most unfluent interface possible...
		Matcher m = commandDataMatch.matcher(command);
		if (!m.find()) {
			throw new RuntimeException("StringFluffer found a match but then couldn't find it again!");
		}
		String[] dataArr = command.substring(m.start()+1,m.end()).split(",");
		StringResult[] srArr = new StringResult[dataArr.length];
		for (int i = 0;i < dataArr.length; i++) {
			StringResult cur = shufflers.get(dataArr[i]);
			if (cur == null) {
				int firstparen = command.indexOf("(");
				if (command.charAt(firstparen)-1 == '!') {
					return command;
				}
				return command.substring(0,firstparen) +"!"+command.substring(firstparen);
			}
			srArr[i] = cur;
		}
		String result = null;
		int rejections = 0;
		int maxtrials = 2+Math.max(10, Math.max(currentTask.getLocalDupes(),currentTask.getGlobalDupes()));
		while (result == null && rejections < maxtrials) {
			result = srArr[extra.getRand().nextInt(srArr.length)].next();
			if (result == null) {//shouldn't be allowed to happen, but we fail gracefully
				rejections++;
			}else {
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
								break;
							}
						}
						//falling through if null now
					}
				}
			}
		}
		if (result != null) {
			currentTask.results.add(result);
		}else {
			//we didn't terminate earlier on, soft error because we shouldn't have even bothered
			result = command.replace("|","!why!");
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
		realOperation = true;
		return sublist.with(context);
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
