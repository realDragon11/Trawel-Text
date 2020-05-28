package rtrawel.jobs;

import java.util.HashMap;

import rtrawel.unit.RPlayer;

public class JobFactory {
	private static HashMap<String,Job> data = new HashMap<String, Job>();
	
	public static Job getJobByName(String str) {
		return data.get(str);
	}
	
	public static void init() {
		
	}
}
