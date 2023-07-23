package derg.menus;

public interface MenuItem {

	public String title();
	
	/**
	 * 
	 * @return whether to go back or not
	 */
	public boolean go();

	public boolean canClick();
	
	public boolean forceLast();
}
