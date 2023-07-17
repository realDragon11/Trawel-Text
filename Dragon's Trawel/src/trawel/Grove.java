package trawel;
import java.awt.Color;
import java.util.List;

import trawel.FeatureNodes.NodeFeature;
import trawel.time.ContextType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

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
		start.go();
	}
	
	public void generate() {
		start = new GroveNode(size,town.getTier(),this);
		reload();
	}
	public Shape getShape() {
		return Shape.STANDARD;
	}
	
	public enum Shape{
		STANDARD;
	}

}
