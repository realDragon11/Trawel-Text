package trawel.towns.nodes;

import java.util.List;

import trawel.personal.item.solid.DrawBane;
import trawel.time.CanPassTime;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public interface NodeType{

	boolean interact(NodeConnector node);
	
	DrawBane[] dbFinds();
	
	/**
	 * should make time events happen in localevents
	 * @param node
	 * @param time
	 * @param calling
	 */
	void passTime(NodeConnector node, double time, TimeContext calling);
	
	NodeConnector getNode(NodeFeature owner, int tier);
	
	NodeConnector generate(NodeFeature owner, int sizeLeft, int tier);
	
	NodeConnector getStart(NodeFeature owner, int size, int tier);
	
	void apply(NodeConnector made);

}