package trawel;

public abstract class MenuSelectNumber implements MenuItem {
	
	public int number;

	public abstract String title();
	
	/**
	 * 
	 * @return whether to go back or not
	 */
	public abstract boolean go();
	
	public boolean canClick() {
		return true;
	}

}
