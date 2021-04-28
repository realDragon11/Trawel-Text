package scimech.people;

import java.util.HashMap;
import java.util.Map;

public class TraitKeeper {

	protected Map<Trait,Integer> traits = new HashMap<Trait,Integer>();
	
	public int getTrait(Trait t) {
		if (!traits.containsKey(t)) {
			return 0;
		}
		return traits.get(t);
	}
	
	/***
	 * 
	 * @param t
	 * @param i
	 * @return whether you went over the cap or not, blocking the addition
	 */
	public boolean addTrait(Trait t, int i) {
		if (traits.containsKey(t)) {
			int newval = traits.get(t)+i;
			if (newval > 10) {
				return true;
			}
			traits.put(t,Math.min(10,newval));
		}else {
			traits.put(t, i);
		}
		return false;
	}
	
	@Override
	public String toString() {
		String str = "Traits: ";
		for (Object t: traits.keySet().toArray()) {
			str +=t.toString() + ": "+ traits.get((Trait)t) + ", ";
		}
		return str;
		
	}
}
