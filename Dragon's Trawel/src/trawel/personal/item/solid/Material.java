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
	public float baseEnchant;
	public float baseResist, sharpResist, bluntResist, pierceResist, weight, cost, dexMod;
	public float rarity, tier, sharpMult, bluntMult, pierceMult;
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
	public transient int curNum;//not stored anyway, but marked transient

	public Material() {}
}
