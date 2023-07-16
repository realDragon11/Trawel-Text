package derg;

import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

import com.github.yellowstonegames.core.GapShuffler;
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
	
	//StringFluffers call on GapShufflers, so they can share lists
	public Map<String,GapShuffler<String>> shufflers = new HashMap<String,GapShuffler<String>>();
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
	
	private boolean realOperation = false;
	
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
		
	}

	/**
	 * processes all command in string
	 * @param str
	 * @return a new string, or str if there was no changes
	 */
	public String process(String str) {
		realOperation = false;//we didn't complete a command yet
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
		String[] dataArr = commandDataMatch.matcher(command).group().split(",");
		String result = null;
		try {
			result = lookup(dataArr[0]);
		} catch (NullPointerException e) {
			result = command.replace("|","!");
		}
		return result;
		
		 
	}
	
	/**
	 * lookup a replacement. Advances GapShuffler state, which is fine even if the caller rejects the returned string
	 * @param data; the sublist to shuffle from
	 * @return null if no sublist found, or the new string
	 */
	public String lookup(String data) {
		GapShuffler<String> sublist = shufflers.get(data.toLowerCase());
		if (sublist == null) {
			return null;
		}
		return sublist.next();
	}
	
	private class Metadata {
		//flag: avoid substring elsewhere
		//flag: avoid doublepicks but substring is fine
		//numberflag: avoid word proximity
	}
}
