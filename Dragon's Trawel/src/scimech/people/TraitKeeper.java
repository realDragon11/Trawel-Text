package scimech.people;

import java.util.HashMap;
import java.util.Map;

public class TraitKeeper {

	protected Map<Trait,Integer> traits = new HashMap<Trait,Integer>();
	
	public int getTrait(Trait t) {
		return traits.get(t);
	}
	
	public void addTrait(Trait t, int i) {
		int k;
		if (traits.containsKey(t)) {
			traits.put(t,traits.get(t)+i);
		}else {
			traits.put(t, i);
		}
	}
}
