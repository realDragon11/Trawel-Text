package derg.menus;

public abstract class MenuLine implements MenuItem{

	@Override
	public abstract String title();
	
	/**
	 * 
	 * @return whether to go back or not
	 */
	@Override
	public boolean go() {
		return true;};
	
	@Override
	public boolean canClick() {
		return false;
	}
	
	@Override
	public boolean forceLast() {
		return false;
	}
}
