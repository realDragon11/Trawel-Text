package derg.menus;

public abstract class MenuLast implements MenuItem{

	public abstract String title();
	
	/**
	 * 
	 * @return whether to go back or not
	 */
	public abstract boolean go();
	
	public boolean canClick() {
		return true;
	}
	
	@Override
	public boolean forceLast() {
		return true;
	}
}
