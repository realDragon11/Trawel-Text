package trawel;
import java.util.ArrayList;

public abstract class SuperPerson implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<String> titleList = new ArrayList<String>();
	private Town location;
	public void addTitle(String title) {
		titleList.add(title);
	}

	public abstract void passTime(double time);
	
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
