package derg.menus;

public abstract class MenuLast implements MenuItem{

	@Override
	public abstract String title();
	
	/**
	 * 
	 * @return whether to go back or not
	 */
	@Override
	public abstract boolean go();
	
	@Override
	public boolean canClick() {
		return true;
	}
	
	@Override
	public boolean forceLast() {
		return true;
	}
}
