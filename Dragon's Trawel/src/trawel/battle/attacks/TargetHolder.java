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
		
		condition = new double[plan.getTotalParts()];
		for (int i = condition.length-1;i >= 0;i--) {
			condition[i] = 1;
		}
	}
	/**
	 * constructs an array of targets with variants
	 * the variants are ints, you must fetch them from the strings
	 * variants will be null if it's a singlet
	 * @param typeBody 
	 * @return the array of parts, Object[2][count] where Target and Integer are sub arrays
	 */
	protected static List<List<Object>> makeMap(TypeBody typeBody) {
		if (typeBody == TypeBody.MIMIC) {
			boolean debug = true;
		}
		List<Object> targets = new ArrayList<Object>();
		List<Object> variants = new ArrayList<Object>();
		for (Target t: TargetFactory.tList()) {
			boolean contains = false;
			for (TargetType taty: typeBody.types) {
				if (t.type == taty) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				continue;
			}
			if (t.variants == null) {
				targets.add(t);
				variants.add(null);
			}else {
				int j = 0;
				for (String s: t.variants) {
					targets.add(t);
					variants.add(j++);
				}
			}
		}
		/*
		Object[][] ret = new Object[2][targets.size()];
		for (int i = targets.size()-1;i >=0;i--) {//we don't care about order
			ret[0][i] = targets.get(i);
			ret[1][i] = variants.get(i);
		}*/
		List<List<Object>> list = new ArrayList<List<Object>>();
		list.add(targets);
		list.add(variants);
		return list;
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

}