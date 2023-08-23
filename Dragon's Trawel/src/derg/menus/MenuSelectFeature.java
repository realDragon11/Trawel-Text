package derg.menus;

import trawel.extra;
import trawel.personal.people.Player;
import trawel.towns.Feature;
import trawel.towns.Lot;
import trawel.towns.nodes.NodeFeature;
import trawel.towns.services.WitchHut;

public class MenuSelectFeature implements MenuItem {
	
	public Feature feature;

	@Override
	public String title() {
		return feature.getColor()+feature.getTitle();
	}
	
	public MenuSelectFeature(Feature f) {
		feature = f;
	}
	@Override
	public boolean go() {
		Player.player.storyHold.enterFeature(feature);
		feature.go();
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
