package trawel;

public abstract class MenuSelect implements MenuItem{

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
		return false;
	}
}
