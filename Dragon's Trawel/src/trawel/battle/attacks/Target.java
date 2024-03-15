package trawel.battle.attacks;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.battle.attacks.ImpairedAttack.DamageType;

public class Target{

	public String name;
	public double blunt, sharp, pierce;
	public double hit, rarity;
	public int slot;
	/**
	 * used for multi target types
	 * if something is in both, set the mappingNumber to the same in both
	 * suggested is
	 * 1 = head
	 * 2 = arms/front legs
	 * 3 = torso
	 * 4 = (back) legs
	 * 5 = feet (fails for quads)
	 * 
	 * cannot do negatives
	 * should keep numbers below 100
	 */
	public int mappingNumber = 0;
	/**
	 * items that have an attach number can't have a mapping number
	 * attach number makes a variant set spawn for all variants on the attached parts in mapping number
	 * the attached part must have the same number of variants on all mapping numbers
	 * it will attempt to learn the number of variants by finding the first target with that mapping number
	 * attached also generate's it's own condition, but it is linked with the base.
	 * <br>
	 * The 'passthrough' variable makes it not have it's own condition, but otherwise be the same.
	 * <br>
	 * <br>
	 * most mapping system behavior can be made with attach behavior, but mapping is simpler
	 * <br>
	 * <br>
	 * name format is: [base part variant name]'s [variant name or normal name]
	 * if there are no base variants it just displays as if it were freestanding
	 * <br>
	 * <br>
	 * zero is reserved. Will offset by 100000 internally, so limit numbers to abs(num) < 100
	 * 100_000 is the base offset, and then 1_000 more is used every time a duplicate of that offset is encounted
	 * <br>
	 * <br>
	 * if negative, is attached to another attach
	 * if positive, attached to a mapping number
	 * <br>
	 * <br>
	 * there must always be a base mapping number
	 */
	public int attachNumber = 0;
	public TargetFactory.TargetType type;
	/*
	private List<Wound> sharpWounds = new ArrayList<Wound>();
	private List<Float> sharpWeights = new ArrayList<Float>();
	private List<Wound> bluntWounds = new ArrayList<Wound>();
	private List<Float> bluntWeights = new ArrayList<Float>();
	private List<Wound> pierceWounds = new ArrayList<Wound>();
	private List<Float> pierceWeights = new ArrayList<Float>();
	
	private List<Wound> igniteWounds = new ArrayList<Wound>();
	private List<Float> igniteWeights = new ArrayList<Float>();
	private List<Wound> frostWounds = new ArrayList<Wound>();
	private List<Float> frostWeights = new ArrayList<Float>();
	private List<Wound> elecWounds = new ArrayList<Wound>();
	private List<Float> elecWeights = new ArrayList<Float>();*/

	private static class WoundRarityTuple{
		public final Wound wound;
		public final float rarity;
		
		public WoundRarityTuple(Wound _wound, float _rarity) {
			wound = _wound;
			rarity = _rarity;
		}
	}
	
	public EnumMap<DamageType,List<WoundRarityTuple>> tupleLists = new EnumMap<DamageType,List<WoundRarityTuple>>(DamageType.class); 
	
	public void addWound(DamageType dt, Wound wound, float rarity) {
		List<WoundRarityTuple> list = tupleLists.getOrDefault(dt,null);
		if (list == null) {
			list = new ArrayList<WoundRarityTuple>();
			tupleLists.put(dt, list);
		}
		list.add(new WoundRarityTuple(wound,rarity));
	}
	
	/**
	 * for mapped parts, variants must be in same order
	 * supports inserting
	 * {} = insert the base name here, but base name will be overridden with children names if they don't have variants themselves
	 * [] = insert base name here
	 * if neither is present, will not use name
	 */
	public String[] variants = null;
	//used for 'left' and 'right' arms- they should be mechanically the same
	//that doesn't mean you can't have an 'arm' mechanic, just that either arm should always count
	
	/**
	 * if set to true, this attach part won't have it's own condition
	 * only applies to attachNumber things
	 */
	public boolean passthrough = false;
	/**
	 * what wound the part should do when it's condition drops below 100%
	 * parts that don't have wounds may do null, but note that all of the same final slot parts
	 * (ie, passthrough attaches or shared maps) must have the same wound or else it might not do the one you want
	 * <br>
	 * in the future these should be 'crippling' wounds, for now they're just the normal mostly temp ones
	 */
	public Wound condWound;
	
	private EnumMap<DamageType,WeightedTable> woundTables = new EnumMap<DamageType, WeightedTable>(DamageType.class);
	
	public void finish() {
		for (DamageType dt: DamageType.values()) {
			List<WoundRarityTuple> list = tupleLists.getOrDefault(dt,null);
			if (list == null) {
					switch (dt) {
					case IGNITE:
						list = new ArrayList<WoundRarityTuple>();
						for (Wound w: TargetFactory.fireWounds) {
							list.add( new WoundRarityTuple(w, 1));
						}
						tupleLists.put(dt,list);
						break;
					case FROST:
						list = new ArrayList<WoundRarityTuple>();
						for (Wound w: TargetFactory.freezeWounds) {
							list.add( new WoundRarityTuple(w, 1));
						}
						tupleLists.put(dt,list);
						break;
					case ELEC:
						list = new ArrayList<WoundRarityTuple>();
						for (Wound w: TargetFactory.shockWounds) {
							list.add( new WoundRarityTuple(w, 1));
						}
						tupleLists.put(dt,list);
						break;
					case DECAY:
						break;
					default:
						throw new RuntimeException("invalid empty target wound list for " + name + " " + type + dt);
					}
			}
			if (list == null) {
				break;//no list needed
			}
			int size = list.size();
			float[] fls = new float[size];
			for (int i = 0; i < size;i++) {
				fls[i] = list.get(i).rarity;
				assert list.get(i).wound != null;
			}
			
			woundTables.put(dt,new WeightedTable(fls));
		}
		TargetFactory.finishTarget(this);
	}
	
	public Wound rollWound(DamageType dt) {
		WeightedTable t = woundTables.getOrDefault(dt, null);
		if (t == null) {
			return Wound.ERROR;//TODO
		}else {
			return tupleLists.get(dt).get(t.random(extra.getRand())).wound;
		}
	}
}
