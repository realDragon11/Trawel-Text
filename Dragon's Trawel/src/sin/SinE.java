package sin;

import java.util.HashMap;

public class SinE {

	private int guid = -1;
	
	
	private HashMap<String,SinRelation> relations = new HashMap<String,SinRelation>();
	private HashMap<String,Integer> flags = new HashMap<String,Integer>();
	
	public void setGUID(int guid) {
		this.guid = guid;
	}
	public int getGUID() {
		return guid;
	}
	
	public SinRelation getRelations(int guid) {
		return relations.get(""+guid);
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
		//flags section
		str+="|";
		String[] flagArr = (String[]) flags.keySet().toArray();
		for(String fr: flagArr) {
			str+=fr + relations.get(fr)+",";
		}
		str+="|";
		return str;
	}
}
