package rtrawel;

import java.util.ArrayList;
import java.util.List;

public class EventFlag implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static EventFlag eventFlag;
	private List<String> events = new ArrayList<String>();
	private List<Integer> flags = new ArrayList<Integer>();
	
	public static void init() {
		eventFlag = new EventFlag();
	}
	
	
	public int getEF(String str) {
		for (int i = 0;i <events.size();i++) {
			if (events.get(i).equals(str)) {
				return flags.get(i);
			}
		}
		return 0;
	}
	
	public void addEF(String str, int a) {
		for (int i = 0;i <events.size();i++) {
			if (events.get(i).equals(str)) {
				a =flags.get(i)+a;
				flags.add(i,a);
				flags.remove(i+1);
				TestRunner.story.flagAt(str, a);
				return;
			}
		}
		events.add(str);
		flags.add(a);
		TestRunner.story.flagAt(str, a);
	}
	
	public void setEF(String str, int a) {
		for (int i = 0;i <events.size();i++) {
			if (events.get(i).equals(str)) {
				flags.add(i,a);
				flags.remove(i+1);
				TestRunner.story.flagAt(str, a);
				return;
			}
		}
		events.add(str);
		flags.add(a);
		TestRunner.story.flagAt(str, a);
	}
	
}
