package rtrawel.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DamMultMap {
	//NOTE: do not serialize, must be reconstructed
	private HashMap<DamageType,Double> hm = new HashMap<DamageType,Double>();
	
	public double getMult(DamageType t) {
		if (hm.containsKey(t)) {
			return hm.get(t);
		}
		return 1;
	}
	
	public void insert(DamageType key,Double value) {
		if (hm.containsKey(key)) {
			throw new RuntimeException("damagemult already in map");
		}
		hm.put(key, value);
	}
	
	public void insertOrAdd(DamageType key,Double value) {
		if (hm.containsKey(key)) {
			hm.put(key, value*hm.remove(key));
			return;
		}
		hm.put(key, value);
	}
	
	public List<DamageType> getKeys(){
		List<DamageType> list =  new ArrayList<DamageType>();
		hm.keySet().stream().forEach(list::add);
	 return list;
	}
}
