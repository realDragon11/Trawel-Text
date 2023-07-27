package derg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringLembda {

	public String[] variants;
	public Map<CanLembdaUpdate,Integer> users = new HashMap<CanLembdaUpdate,Integer>();
	
	public StringLembda(String...strings) {
		variants = strings;
	}
	
	public void updateAll() {
		for (CanLembdaUpdate user: users.keySet()) {
			user.update(this);
		}
	}
	
}
