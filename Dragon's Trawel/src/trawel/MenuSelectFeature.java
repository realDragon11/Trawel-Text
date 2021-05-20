package trawel;

public class MenuSelectFeature implements MenuItem {
	
	public Feature feature;

	public String title() {
		if (feature.getName() == null) {
			return "nullname";
		}
		return extra.inlineColor(feature.getColor())+extra.capFirst(feature.getName());
	}
	
	public MenuSelectFeature(Feature f) {
		feature = f;
	}
	/**
	 * 
	 * @return whether to go back or not
	 */
	public boolean go() {
		feature.go();
		return true;
	}
	
	public boolean canClick() {
		return true;
	}

}
