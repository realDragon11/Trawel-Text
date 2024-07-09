package derg.menus;

import trawel.core.mainGame;
import trawel.helper.constants.TrawelColor;
import trawel.personal.people.Player;
import trawel.towns.features.Feature;

public class MenuSelectFeature implements MenuItem {
	
	public Feature feature;

	@Override
	public String title() {
		String tut = feature.getTutorialText();
		return feature.getColor()+feature.getTitle()
		+ (tut != null && mainGame.displayFeatureText ? TrawelColor.TIMID_MAGENTA+" ("+tut+")" : "")
		;
	}
	
	public MenuSelectFeature(Feature f) {
		feature = f;
	}
	@Override
	public boolean go() {
		Player.player.getStory().enterFeature(feature);
		feature.enter();
		Player.player.atFeature = null;
		return true;
	}
	
	@Override
	public boolean canClick() {
		return true;
	}

	@Override
	public boolean forceLast() {
		return false;
	}

}
