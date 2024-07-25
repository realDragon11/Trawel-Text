package trawel.helper.methods;

import java.util.HashMap;
import java.util.Stack;

import trawel.helper.constants.TrawelColor;

public class TagFormatter {
	
	/**
	 * stack of *tags*- not codes to tags, so it works with tags not in this system as well
	 */
	private static final Stack<String> tagStack = new Stack<String>();

	private static final HashMap<String,String> tagMap = new HashMap<String, String>();
	static {
		addColor("new",TrawelColor.COLOR_NEW);
		addColor("seen",TrawelColor.COLOR_SEEN);
		addColor("own",TrawelColor.COLOR_OWN);
		addColor("been",TrawelColor.COLOR_BEEN);
		addColor("regrown",TrawelColor.COLOR_REGROWN);
		
		addColor("a1",TrawelColor.ADVISE_1);
		addColor("a2",TrawelColor.ADVISE_2);
		addColor("a3",TrawelColor.ADVISE_3);
		addColor("a4",TrawelColor.ADVISE_4);
		addColor("a5",TrawelColor.ADVISE_5);
		addColor("a6",TrawelColor.ADVISE_6);
	}
	
	private static final void addColor(String code, String color) {
		tagMap.put("c:"+code,color);
	}
	
	public static final void clearStack() {
		tagStack.clear();
	}
	
	public static final String get(String code) {
		if (code.equals("revert")) {
			return revertTag();
		}
		return fetchAndStack(code);
	}
	
	public static final String fetchAndStack(String code) {
		if (tagMap.containsKey(code)) {
			String tag = tagMap.get(code);
			tagStack.push(tag);
			return tag;
		}else {
			//not a code tag, treat as it's own tag
			tagStack.push(code);
			return code;
		}
	}
	
	public static final String revertTag() {
		//remove current
		tagStack.pop();
		//get last, do not pop so pop->peek works if revert twice
		return tagStack.peek();
	}
}
