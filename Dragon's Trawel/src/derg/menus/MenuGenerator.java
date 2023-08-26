package derg.menus;

import java.util.List;

public interface MenuGenerator {

	
	public abstract List<MenuItem> gen();
	
	/**
	 * called right before gen is called. Should put methods which have side effects here if possible.
	 * <br>
	 * can include printing output before displaying the menu
	 */
	public default void onRefresh() {
		//empty
	}
}
