package trawel;

public abstract class MenuLine implements MenuItem{

	public abstract String title();
	
	/**
	 * 
	 * @return whether to go back or not
	 */
	public boolean go() {
		return true;};
	
	public boolean canClick() {
		return false;
	}
}
