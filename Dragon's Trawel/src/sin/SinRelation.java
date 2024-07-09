package sin;

import java.util.ArrayList;
import java.util.List;

import trawel.helper.methods.extra;

public class SinRelation {

	//only add to end
	public enum RelationType{
		PARANOIA(0);
		
		public int defaultValue;
		RelationType(int defaultValue) {
			this.defaultValue = defaultValue;
		}
	}
	
	private List<Integer> relations = new ArrayList<Integer>();
	
	public SinRelation() {
		RelationType[] arr = RelationType.values();
		for (int i = 0; i < arr.length;i++) {
			relations.add(arr[i].defaultValue);
		}
	}
	public int getRelation(RelationType rType) {
		return relations.get(rType.ordinal());
	}
	
	public void addRelation(RelationType rType, int amount) {
		int a = relations.get(rType.ordinal());
		relations.set(rType.ordinal(),extra.clamp(a+amount, 0, 100));
	}
	
	public void setRelation(RelationType rType, int amount) {
		relations.set(rType.ordinal(),amount);
	}
	
	public String saveString() {
		String str = "|";
		RelationType[] arr = RelationType.values();
		for (int i = 0; i < arr.length;i++) {
			str += getRelation(arr[i]) + ",";
		}
		str+="|";
		return str;
	}
	
	public SinRelation loadString(String str) {
		SinRelation ret = new SinRelation();
		int pos = 0;
		int pos2 = str.indexOf(",");
		int i = 0;
		RelationType[] arr = RelationType.values();
		while (pos2 > pos) {
			ret.setRelation(arr[i], Integer.parseInt(str.substring(pos, pos2)));
			i++;
			pos = pos2 +1;
			pos2 = str.indexOf(",");
		}
		
		return ret;
	}
}
