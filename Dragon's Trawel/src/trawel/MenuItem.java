package trawel;

public interface MenuItem {

	public String title();
	
	/**
	 * 
	 * @return whether to go back or not
	 */
	public boolean go();
}
