package trawel.helper.methods;

import java.util.EmptyStackException;
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
		addTag("clear",TrawelColor.PRE_WHITE);
		//revert hardcoded
		
		addTag("p_new",TrawelColor.COLOR_NEW);
		addTag("p_seen",TrawelColor.COLOR_SEEN);
		addTag("p_own",TrawelColor.COLOR_OWN);
		addTag("p_been",TrawelColor.COLOR_BEEN);
		addTag("p_regrown",TrawelColor.COLOR_REGROWN);
		
		addTag("opt_a",TrawelColor.COLOR_OPTION_A);
		addTag("opt_b",TrawelColor.COLOR_OPTION_B);
		addTag("opt_c",TrawelColor.COLOR_OPTION_C);
		addTag("opt_exit",TrawelColor.RESULT_WARN);
		
		addTag("a1",TrawelColor.ADVISE_1);
		addTag("a2",TrawelColor.ADVISE_2);
		addTag("a3",TrawelColor.ADVISE_3);
		addTag("a4",TrawelColor.ADVISE_4);
		addTag("a5",TrawelColor.ADVISE_5);
		addTag("a6",TrawelColor.ADVISE_6);
		
		addTag("ibw",TrawelColor.INFORM_BAD_WEAK);//inform bad weak
		addTag("ibm",TrawelColor.INFORM_BAD_MID);//mid/moderate
		addTag("ibs",TrawelColor.INFORM_BAD_STRONG);
		addTag("igw",TrawelColor.INFORM_GOOD_WEAK);//inform good weak
		addTag("igm",TrawelColor.INFORM_GOOD_MID);
		addTag("igs",TrawelColor.INFORM_GOOD_STRONG);
		
		addTag("pay_aether",TrawelColor.SERVICE_AETHER);
		addTag("pay_money",TrawelColor.SERVICE_CURRENCY);
		addTag("pay_both",TrawelColor.SERVICE_BOTH_PAYMENT);
		addTag("pay_free",TrawelColor.SERVICE_FREE);
		addTag("pay_flavor",TrawelColor.SERVICE_FLAVOR);
		addTag("pay_rep",TrawelColor.SERVICE_REP);
		addTag("pay_special",TrawelColor.SERVICE_SPECIAL_PAYMENT);
		addTag("pay_time",TrawelColor.SERVICE_TIME);
		
		addTag("act_explore",TrawelColor.SERVICE_EXPLORE);
		addTag("act_combat",TrawelColor.SERVICE_COMBAT);
		addTag("act_quest",TrawelColor.SERVICE_QUEST);
		
		addTag("f_node",TrawelColor.F_NODE);
		addTag("f_guild",TrawelColor.F_GUILD);
		addTag("f_multi",TrawelColor.F_MULTI);
		addTag("f_service",TrawelColor.F_SERVICE);
		addTag("f_aux",TrawelColor.F_AUX_SERVICE);
		addTag("f_magic",TrawelColor.F_SERVICE_MAGIC);
		addTag("f_special",TrawelColor.F_SPECIAL);
		addTag("f_combat",TrawelColor.F_COMBAT);
		addTag("f_build",TrawelColor.F_BUILDABLE);
		
		addTag("r_pass",TrawelColor.RESULT_PASS);
		addTag("r_good",TrawelColor.RESULT_GOOD);
		addTag("r_bad",TrawelColor.RESULT_BAD);
		addTag("r_same_bad",TrawelColor.RESULT_NO_CHANGE_BAD);
		addTag("r_same_good",TrawelColor.RESULT_NO_CHANGE_GOOD);
		addTag("r_same",TrawelColor.RESULT_NO_CHANGE_NONE);
		addTag("r_warn",TrawelColor.RESULT_WARN);
		addTag("r_error",TrawelColor.RESULT_ERROR);
	}
	
	private static final void addTag(String code, String full) {
		tagMap.put(code,full.substring(1,full.length()-1));
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
		try {
			//remove current
			tagStack.pop();
			//get last, do not pop so pop->peek works if revert twice
			return tagStack.peek();
		}catch (EmptyStackException e) {
			return tagMap.get("clear");
		}
	}
}
