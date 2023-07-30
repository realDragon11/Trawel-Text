package trawel.battle.attacks;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import trawel.extra;
import trawel.battle.attacks.TargetFactory.TargetType;
import trawel.battle.attacks.TargetFactory.TypeBody;
import trawel.battle.attacks.TargetFactory.TypeBody.TargetReturn;

public class TargetHolder {
	/**
	 * do not use directly, get the map
	 * a list of %'s on how damaged a part is
	 */
	private double[] condition;

	/**
	 * if this is null, then there are no restrictions
	 * otherwise it will be limited to the targettype given
	 * DOLATER: can potentially have targettypes that indicate multiple masks as 'flags'
	 */
	private TargetType config;
	private final TypeBody plan;
	
	public TargetHolder(TypeBody type) {
		plan = type;
		config = null;
		
		condition = new double[plan.getUniqueParts()];
		for (int i = condition.length-1;i >= 0;i--) {
			condition[i] = 1;
		}
	}
	
	
	/**
	 * the damage % on a part
	 * does not list subparts
	 */
	public double getStatus(int spot) {
		return condition[plan.getMap(spot)];
	}
	
	/**
	 * the damage % on a part
	 * gets the final part in the attach chain
	 */
	public double getRootStatus(int spot) {
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			return getRootStatus(aspot);
		}
		return condition[plan.getMap(spot)];
	}
	
	public int getRootSpot(int spot) {
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			return getRootSpot(aspot);
		}
		return spot;
	}
	
	public String getPartName(int spot) {
		return plan.spotName(spot);
	}
	
	public TargetReturn getTargetReturn(int spot) {
		return plan.getTargetReturn(spot);
	}
	
	public double multStatus(int spot, double mult) {
		double store = condition[plan.getMap(spot)]*mult;
		condition[plan.getMap(spot)] = store;
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			multStatus(aspot,mult);
		}
		return store;
	}
	
	public double addStatus(int spot, double add) {
		double store = condition[plan.getMap(spot)]+add;
		condition[plan.getMap(spot)] = store;
		int aspot = plan.getAttach(spot);
		if (aspot >= 0) {
			addStatus(aspot,add);
		}
		return store;
	}

	public TargetType getConfig() {
		return config;
	}

	/**
	 * can change mid battle
	 * if this is null, then there are no restrictions
	 */
	public void setConfig(TargetType config) {
		this.config = config;
	}

	public TypeBody getPlan() {
		return plan;
	}
	
	/**
	 * returns the map spot of a target
	 * use getPartName, getTarget and getStatus on this number
	 */
	public TargetReturn randTarget() {
		return plan.randTarget(config);
	}
	
	public void debug_print(boolean full) {
		if (full) {
			//Stack<List<TargetReturn>> stack = new Stack<List<TargetReturn>>();//sadly I'm on java 8 so no '" ".repeat'
			//List<TargetReturn> children;
			//List<TargetReturn> curLevel = null;
			for (TargetReturn b: plan.rootTargetReturns()) {
				printSpot(b.spot,1);
				List<TargetReturn> children = plan.getDirectChildren(b.spot);
				if (children.size() > 0) {
					debug_printLAYER(children,2);
				}
			}
		}
		extra.print("  >");
		for (int i = 0; i < condition.length;i++) {
			extra.print(condition[i]+" ");
		}
		extra.println();
		
	}
	
	private void debug_printLAYER(List<TargetReturn> layer, int num) {
		for (TargetReturn tr: layer) {
			List<TargetReturn> children = plan.getDirectChildren(tr.spot);
			printSpot(tr.spot,num);
			if (children.size() > 0) {
				debug_printLAYER(children,num+2);
			}
		}
	}
	private void printSpot(int spot, int buffer) {
		extra.println(extra.spaceBuffer(buffer)+
				getPartName(spot) + " " +spot+": " + plan.getMap(spot) + "-" + getStatus(spot) + " attach: " +plan.getAttach(spot)
				);
	}

}