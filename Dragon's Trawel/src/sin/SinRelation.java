package sin;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;

public class SinRelation {

	//only add to end
	public enum RelationType{
		PARANOIA(0);
		
		public int defaultValue;
		RelationType(int defaultValue) {
			this.defaultValue = defaultValue;
		}
	}
	
	public List<Integer> relations = new ArrayList<Integer>();
	
	public SinRelation() {
		RelationType[] arr = RelationType.values();
		for (int i = 0; i < arr.length;i++) {
			relations.add(arr[i].defaultValue);
		}
	}
	
	
	public void addRelation(RelationType rType, int amount) {
		int a = relations.get(rType.ordinal());
		relations.set(rType.ordinal(),extra.clamp(a+amount, 0, 100));
	}
}
