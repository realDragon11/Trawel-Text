package trawel.battle.attacks;

import java.util.ArrayList;
import java.util.List;

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
	 */
	public double getStatus(int spot) {
		return condition[plan.getMap(spot)];
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
		return store;
	}
	
	public double addStatus(int spot, double add) {
		double store = condition[plan.getMap(spot)]+add;
		condition[plan.getMap(spot)] = store;
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
			extra.print("   ");
			for (int i = 0; i < plan.getPartCount();i++) {
				extra.print(getPartName(i) + ": " + plan.getMap(i) + "-" + getStatus(i) + " ");
			}
			extra.println();
		}
		extra.print("   ");
		for (int i = 0; i < condition.length;i++) {
			extra.print(condition[i]+" ");
		}
		extra.println();
		
	}

}