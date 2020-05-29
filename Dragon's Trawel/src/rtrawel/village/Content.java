package rtrawel.village;

public interface Content {

	/**
	 * 
	 * @return if you went to a new town/if you should reload the current town
	 */
	public boolean go();
	
	public String name();
}
