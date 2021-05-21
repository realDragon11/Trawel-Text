package trawel;
import java.util.ArrayList;

/**
 * A flyweight class
 * @author Brian Malone
 * 5/29/2018
 */
public class Material implements java.io.Serializable{
	public String name;
	public double baseResist, baseEnchant, sharpResist, bluntResist, pierceResist, weight, cost, dexMod;
	public double rarity, tier, sharpMult, bluntMult, pierceMult;
	public Boolean armor, weapon;
	public double fireVul, shockVul, freezeVul;//vulernability
	public ArrayList<String> typeList = new ArrayList<String>();
	public int palIndex;
	public String soundType;
	public String color = "";

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
