package derg.strings.fluffer;

import java.util.HashMap;
import java.util.Map;

public class StringLembda {

	public Map<Integer,String> variants = new HashMap<Integer,String>();
	public Map<CanLembdaUpdate,Integer> users = new HashMap<CanLembdaUpdate,Integer>();
	
	public StringLembda() {
		//
	}
	
	public void updateAll() {
		for (CanLembdaUpdate user: users.keySet()) {
			user.update(this);
		}
	}
	
	@Override
	public String toString() {
		return variants.get((int)'a');
	}
	
}
