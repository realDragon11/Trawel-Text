package trawel.battle.attacks;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.battle.attacks.TargetFactory.TargetType;
import trawel.battle.attacks.TargetFactory.TypeBody;

public class TargetHolder {
	/**
	 * links a type/variant to a condition
	 */
	private int[] map;
	/**
	 * do not use directly, get the map
	 * a list of %'s on how damaged a part is
	 */
	private double[] condition;
	private Target[] targs;
	private String[] names;

	/**
	 * if this is null, then there are no restrictions
	 * otherwise it will be limited to the targettype given
	 * DOLATER: can potentially have targettypes that indicate multiple masks as 'flags'
	 */
	private TargetType config;
	private TypeBody plan;
	
	public TargetHolder(TypeBody type) {
		plan = type;
		config = null;
		
		condition = new double[map.length];
		for (int i = map.length-1;i >= 0;i--) {
			condition[i] = 1;
		}
	}
	/**
	 * constructs an array of targets with variants
	 * variants will be null if it's a singlet
	 * @param typeBody 
	 * @return the array of parts, Object[2][count] where Target and String are sub arrays
	 */
	protected static Object[][] makeMap(TypeBody typeBody) {
		List<Target> targets = new ArrayList<Target>();
		List<String> variants = new ArrayList<String>();
		for (Target t: TargetFactory.targetList) {
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
				for (String s: t.variants) {
					targets.add(t);
					variants.add(s);
				}
			}
		}
		Object[][] ret = new Object[2][targets.size()];
		for (int i = targets.size()-1;i >=0;i--) {//we don't care about order
			ret[0][i] = targets.get(i);
			ret[1][i] = variants.get(i);
		}
		return ret;
	}
	
	/**
	 * the damage % on a part
	 */
	public double getStatus(int spot) {
		return condition[map[spot]];
	}
	
	public String getPartName(int spot) {
		return names[spot];
	}
	public Target getTarget(int spot) {
		return targs[spot];
	}
	
	public double multStatus(int spot, double mult) {
		double store = condition[map[spot]]*mult;
		condition[map[spot]] = store;
		return store;
	}
	
	public double addStatus(int spot, double add) {
		double store = condition[map[spot]]+add;
		condition[map[spot]] = store;
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
	public int randTarget() {
		int val;
		if (config == null) {
			return plan.tables[0].random(extra.getRand());//0 is the global table
		}
		for (val = plan.types.length-1; val >= 0;val--) {
			if (config == plan.types[val]) {
				break;//we found what list we're in, it's stored now
			}
		}
		return plan.tables[val+1].random(extra.getRand());//0 is the global table, so offset
	}

}