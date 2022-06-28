package sin;

import java.util.HashMap;

public class SinE {

	private int guid = -1;
	
	
	private HashMap<String,SinRelation> relations = new HashMap<String,SinRelation>();
	
	
	public int getGUID() {
		return guid;
	}
	
	
	public static SinE loadString(String load) {
		return null;
	}
	
	public String saveString() {
		String str = "";
		//relations section
		str+="|";
		String[] srArr = (String[]) relations.keySet().toArray();
		for(String sr: srArr) {
			str+=">"+sr + relations.get(sr).saveString();
		}
		str+="|";
		return str;
	}
}
