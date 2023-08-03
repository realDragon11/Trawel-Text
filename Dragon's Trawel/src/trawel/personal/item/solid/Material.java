package trawel.personal.item.solid;
import java.util.ArrayList;
import java.util.List;

import trawel.personal.item.solid.variants.ArmorStyle;

/**
 * A flyweight class
 * @author Brian Malone
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
	
	public transient int curNum;//not stored anyway, but marked transient
	//used for reverse lookup so we don't even have to store them as strings

	/**
	Material(String name,String clothType,
	 double baseResist, double baseEnchant,double sharpResist,double bluntResist,double pierceResist,double weight,double cost,double dexMod,
	 double rarity, double tier, double sharpMult, double bluntMult, double pierceMult,
	 Boolean armor, Boolean weapon){
			 this.name = name; this.clothType = clothType;
			 this.baseResist = baseResist; this.baseEnchant =  baseEnchant; this.sharpResist =  sharpResist; 
			 this.bluntResist = bluntResist; this.pierceResist = pierceResist; this.weight = weight; this.cost = cost;
			 this.dexMod = dexMod;
			 this.rarity = rarity; this.tier = tier; this.sharpMult =sharpMult; this.bluntMult = bluntMult; this.pierceMult = pierceMult;
			 this.armor = armor; this.weapon = weapon;
	}**/

	public Material() {}
}
