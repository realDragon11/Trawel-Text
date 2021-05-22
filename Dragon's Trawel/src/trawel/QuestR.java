package trawel;

import java.io.Serializable;

public abstract class QuestR implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract String getName();
	
	public abstract boolean go();
	
	public Quest overQuest;
	public Town locationT;
	public Feature locationF;
	public void cleanup() {
		locationF.removeQR(this);
	}
}
