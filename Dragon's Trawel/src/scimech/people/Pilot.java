package scimech.people;

import trawel.randomLists;

public class Pilot {

	protected String name;
	
	public String getName() {
		return name;
	}
	
	public Pilot() {
		name = randomLists.randomFirstName();
	}
}
