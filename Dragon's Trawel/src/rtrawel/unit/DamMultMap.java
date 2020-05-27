package rtrawel.unit;

import java.util.HashMap;

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
}
