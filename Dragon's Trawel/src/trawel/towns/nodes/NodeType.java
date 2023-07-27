package trawel.towns.nodes;

import java.util.List;

import trawel.personal.item.solid.DrawBane;
import trawel.time.CanPassTime;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public interface NodeType{
	
	NodeType getSingleton();

	boolean interact(NodeConnector node);
	
	DrawBane[] dbFinds();
	
	List<TimeEvent> passTime(NodeConnector node, double time, TimeContext calling);
	
	NodeConnector getNode(NodeFeature owner, int tier);
	
	NodeConnector generate(NodeFeature owner, int sizeLeft, int tier);
	
	NodeConnector getStart(NodeFeature owner, int sizeLeft, int tier);

}
