package sin;

import java.util.HashMap;

/***
 * 
 * @author dragon
 * Contains handler methods and data. None of this class should be saved normally, except the GUID counters
 */
public class SinHandler {

	private int guid = 0;
	
	
	//not saved
	public HashMap<String,SinE> guidMap = new HashMap<String,SinE>();
	
	public int nextGUID() {
		return ++guid;
	}
	
	public SinE fetchGUID(int guid) {
		return guidMap.get(""+guid);
	}
}
