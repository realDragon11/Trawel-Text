package trawel.personal.item.solid;
import java.util.ArrayList;
import java.util.List;

import trawel.personal.item.solid.variants.ArmorStyle;

/**
 * @author dragon
 * 5/29/2018
 */
public class Material{
	public String name;
	/**
	 * 0f = disable
	 * <br>
	 * .3f = normal metals
	 * <b5>
	 * 1 - 1.5f = many magic or better metals
	 * <br>
	 * around 2f = magic inclined materials like gold and mythril
	 * <br>
	 * 3f = highest material we have currently
	 */
	public float baseEnchant;
	public float baseResist, sharpResist, bluntResist, pierceResist, weight, cost, dexMod;
	public float rarity, tier;
	/**
	 * should be between .1f and 3f
	 */
	public float sharpMult, bluntMult, pierceMult;
	public Boolean armor, weapon;
	public float fireVul, shockVul, freezeVul;//vulernability
	public List<ArmorStyle> typeList = new ArrayList<ArmorStyle>();
	public int palIndex;
	public String soundType;
	public String color = "";
	/**
	 * used for mined veins, don't set if not needed
	 */
	public int veinReward;
	/**
	 * chance to get the 'displacing' armor quality currently, 0 default
	 * <br>
	 * >=.5 for select magic metals and gems
	 * <br>
	 * .2 for common (silver/gold/plat/magic)
	 * <br>
	 * .05 for decent chance
	 * <br>
	 * .02 for lower chance
	 */
	public float shimmer = 0f;
	/**
	 * chance to get the 'sturdy' armor quality currently, .1 default
	 * <br>
	 * metals should have between .2 and .5
	 */
	public float sturdy = .1f;
	
	/**
	 * defaults to 1f
	 * <br>
	 * the multiplier on how much the material impacts the trading price vs the aether price
	 * <br>
	 * the normal value is used to determine trading price
	 * <br>
	 * materials that are expensive in general but are bad for weapons should have a sub 1 mult
	 * <br>
	 * TODO: unsure how this should interact with weird materials like blunt gold
	 */
	public float moneyMultTradeMult = 1f;
	
	/**
	 * used for reverse lookup so we don't even have to store them as strings
	 */
	public transient int curNum;//material singletons not stored anyway, but marked transient
}
