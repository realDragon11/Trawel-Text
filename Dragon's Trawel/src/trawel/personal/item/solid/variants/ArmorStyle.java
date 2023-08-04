package trawel.personal.item.solid.variants;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.extra;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;

/**
 * Stores a 'style' of armor, which, when combined with it's slot and material, give complete the physical properties of the item
 * not storable directly, but supports retrieval with shorts
 */
public enum ArmorStyle {
	PLATE,
	MAIL,
	FABRIC,
	SEWN,
	GEM,
	BODY,
	;//example
	
	public StyleNum[] genner = new StyleNum[5];
	public float sharpMult, bluntMult, pierceMult, totalMult;//might turn into arrays later
	public float enchantMult,//tends to be slightly sub 0 if not ideal, 1f if ideal like plate, and >1f if better like fabric
	weightMult, costMult;
	public float dexMultBase;//combined with material and slot weirdly
	public RestrictType restrictType;
	public boolean[] bannedSlots = new boolean[] {false,false,false,false,false};
	public WeightedTable matTable;
	
	//FIXME: if not using global pallete swapping, also needs ids
	ArmorStyle(){
		
	}
	
	public enum RestrictType{
		AGILE, LIGHT, MEDIUM, HEAVY//FIXME: change names
	}
	
	
	public static void init() {
		PLATE.sharpMult = 1.1f;
		PLATE.bluntMult = 1f;
		PLATE.pierceMult = 1.1f;
		PLATE.totalMult = 1.2f;
		PLATE.enchantMult = 1f;
		PLATE.weightMult = 2f;
		PLATE.costMult = 3f;
		PLATE.dexMultBase = .5f;
		PLATE.genner[0] = new NameStyleNum("plate helm");
		PLATE.genner[1] = new NameStyleNum("plate gauntlets");
		PLATE.genner[2] = new NameStyleNum("plate chestplate");
		PLATE.genner[3] = new NameStyleNum("plate greaves");
		PLATE.genner[4] = new NameStyleNum("plate boots");
		
		MAIL.sharpMult = 1.5f;
		MAIL.bluntMult = 1f;
		MAIL.pierceMult = 1.1f;
		MAIL.totalMult = .9f;
		MAIL.enchantMult = .5f;
		MAIL.weightMult = 4f;
		MAIL.costMult = 2.5f;
		MAIL.dexMultBase = .8f;//see if can work this into strength
		MAIL.genner[0] = new NameStyleNum("mail hood");
		MAIL.genner[1] = new NameStyleNum("mail gloves");
		MAIL.genner[2] = new NameStyleNum("mail shirt");
		MAIL.genner[3] = new NameStyleNum("mail skirt");
		MAIL.genner[4] = new NameStyleNum("mail boots");
		
		FABRIC.sharpMult = 1f;
		FABRIC.bluntMult = 1f;
		FABRIC.pierceMult = 1f;
		FABRIC.totalMult = .6f;//still worth something if adamantine
		FABRIC.enchantMult = 1.5f;
		FABRIC.weightMult = .8f;
		FABRIC.costMult = .8f;
		FABRIC.dexMultBase = 1f;//this is the lighter variant of fabric
		FABRIC.genner[0] = new NameStyleNum("cap");
		FABRIC.genner[1] = new NameStyleNum("gloves");
		FABRIC.genner[2] = new NameStyleNum("shirt");
		FABRIC.genner[3] = new NameStyleNum("trousers");
		FABRIC.genner[4] = new NameStyleNum("shoes");
		
		//https://en.wikipedia.org/wiki/Gambeson
		SEWN.sharpMult = 1f;
		SEWN.bluntMult = 1f;
		SEWN.pierceMult = 1f;
		SEWN.totalMult = 1f;
		SEWN.enchantMult = .9f;
		SEWN.weightMult = 1f;
		SEWN.costMult = 1f;
		SEWN.dexMultBase = .9f;
		SEWN.genner[0] = new NameStyleNum("helmet");//???
		SEWN.genner[1] = new NameStyleNum("bracers");
		SEWN.genner[2] = new NameStyleNum("gambeson");
		SEWN.genner[3] = new NameStyleNum("pants");//???
		SEWN.genner[4] = new NameStyleNum("boots");//???
		
		GEM.sharpMult = 1.2f;
		GEM.bluntMult = .8f;
		GEM.pierceMult = 1.2f;
		GEM.totalMult = 1f;
		GEM.enchantMult = 1.2f;
		GEM.weightMult = 1.5f;
		GEM.costMult = 4f;
		GEM.dexMultBase = .3f;
		GEM.genner[0] = new NameStyleNum("gemhelm");
		GEM.genner[1] = new NameStyleNum("gembracers");
		GEM.genner[2] = new NameStyleNum("gemplate");
		GEM.genner[3] = new NameStyleNum("gemgreaves");
		GEM.genner[4] = new NameStyleNum("gemboots");
		
		BODY.sharpMult = 1f;
		BODY.bluntMult = 1f;
		BODY.pierceMult = 1f;
		BODY.totalMult = 1f;
		BODY.enchantMult = 1f;
		BODY.weightMult = 0f;
		BODY.costMult = 1f;//n/a usually
		BODY.dexMultBase = 1f;
		BODY.genner[0] = new NameStyleNum("head");
		BODY.genner[1] = new NameStyleNum("arms");
		BODY.genner[2] = new NameStyleNum("body");
		BODY.genner[3] = new NameStyleNum("legs");
		BODY.genner[4] = new NameStyleNum("feet");

		//Set<ArmorStyle> have = EnumSet.noneOf(ArmorStyle.class);
		List<ArmorStyle> vals = Arrays.asList(ArmorStyle.values());
		float[][] lists = new float[vals.size()][MaterialFactory.matList.size()];
		for (Material m: MaterialFactory.matList) {
			for (int i = 0; i < MaterialFactory.matList.size();i++) {
				for (int j = 0; j < m.typeList.size();j++) {
					lists[vals.indexOf(m.typeList.get(j))][i] += m.rarity;
				}
			}
		}
		for (int i = 0; i < vals.size(); i++) {
			if (vals.get(i) == BODY) {
				continue;
			}
			vals.get(i).matTable = new WeightedTable(lists[i]);
		}

	};

	public static ArmorStyle fetch(short i) {
		return ArmorStyle.values()[i];
	}
	
	public Material getMatFor() {
		return MaterialFactory.getMat(matTable.random(extra.getRand()));
	}
}
