package scimech.people;

import java.util.HashMap;
import java.util.Map;

import scimech.handlers.Savable;
import scimech.units.fixtures.AcidFoam;

public class TraitKeeper implements Savable{

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

	@Override
	public String saveString() {
		String internals = "";
		for (Object t: traits.keySet().toArray()) {
			internals +=((Trait)t).name() + ":"+ traits.get((Trait)t) + ",";
		}
		return this.getClass().getName() + "&|" + internals +"|";
	}
	
	public static Savable deserialize(String s) {
		TraitKeeper tk = new TraitKeeper();
		String working = s.split("|")[1];
		for (String sub: working.split(",")) {
			String[] sSub = sub.split(":");
			tk.addTrait(Trait.valueOf(sSub[0]),Integer.parseInt(sSub[1]));
		}
		return tk;
	}
}
