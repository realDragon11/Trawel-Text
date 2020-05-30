package rtrawel;

import java.util.ArrayList;
import java.util.List;

public class EventFlag implements java.io.Serializable {

	public static EventFlag eventFlag;
	private List<String> events = new ArrayList<String>();
	private List<Integer> flags = new ArrayList<Integer>();
	
	public static void init() {
		eventFlag = new EventFlag();
	}
	
}
