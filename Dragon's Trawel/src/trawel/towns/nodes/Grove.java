package trawel.towns.nodes;
import java.awt.Color;
import java.util.List;

import trawel.Networking;
import trawel.time.ContextType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Town;

public class Grove extends NodeFeature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Grove(String name,Town t) {
		this.name = name;
		town = t;
		size = 50;//t.getTier()*10;
		tutorialText = "Explore groves to progress in level.";
		generate();
		color = Color.RED;
		background_area = "forest";
		background_variant = 1;
	}
	@Override
	public void go() {
		Networking.setArea("forest");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|grove|Grove|");
		NodeConnector.enter(start);
	}
	
	@Override
	protected void generate() {
		start = GroveNode.getSingleton().getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}
	@Override
	public NodeType numType(int i) {
		switch (i) {
		case 0: return GroveNode.getSingleton();
		case 1: return CaveNode.getSingleton();
		}
		return null;
	}

}
