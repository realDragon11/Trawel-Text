package trawel;

import java.awt.Color;

public class MenuSelectFeature implements MenuItem {
	
	public Feature feature;

	public String title() {
		if (feature.getName() == null) {
			return "nullname";
		}
		String append = "";
		if (feature instanceof Lot) {
			if (((Lot)feature).getConstructTime() != -1) {
				append += " ("+extra.format2(((Lot)feature).getConstructTime())+")";
			}
		}
		return extra.inlineColor(extra.colorMix(feature.getColor(),Color.WHITE,.5f))+extra.capFirst(feature.getName()) + append;
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
