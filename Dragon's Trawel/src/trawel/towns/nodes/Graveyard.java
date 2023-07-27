package trawel.towns.nodes;
import java.awt.Color;
import java.util.List;

import trawel.Networking;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Town;

public class Graveyard extends NodeFeature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Graveyard(String name,Town t) {
		this.name = name;
		town = t;
		size = 40;
		tutorialText = "Graveyards are teeming with undead.";
		generate();
		color = Color.RED;
	}
	@Override
	public void go() {
		Networking.setArea("dungeon");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		NodeConnector.enter(start);
	}

	@Override
	protected void generate() {
		start = new GraveyardNode(size,town.getTier(),town,this);
	}

}
