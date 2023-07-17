package trawel;
import java.util.ArrayList;

import trawel.time.CanPassTime;

public abstract class SuperPerson implements java.io.Serializable, CanPassTime{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<String> titleList = new ArrayList<String>();
	private Town location;
	public void addTitle(String title) {
		titleList.add(title);
	}
	
	public void displayTitles() {
		if (titleList.isEmpty()) {extra.println("They have no titles.");}else {
		extra.println("They have the following titles:");
		for (String str: titleList) {
			extra.print(str+",");
		}
		extra.println();
		}
		
	}
	
	public Town getLocation() {
		return location;
	}
	public void setLocation(Town location) {
		this.location = location;
	}
}
