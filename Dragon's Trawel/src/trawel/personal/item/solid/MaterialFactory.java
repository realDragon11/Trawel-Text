package trawel.personal.item.solid;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.personal.item.solid.variants.ArmorStyle;

public class MaterialFactory {
	public static List<Material> matList = new ArrayList<Material>();
	public static Map<String,Material> matMap = new HashMap<String,Material>();
	private static WeightedTable weapMats, armMats;
	
	//public final List<String> matKeys = new ArrayList<String>();
	
	private void register(Material m) {
		//matKeys.add(m.name);
		//DOLATER: if supporting multiple material versions (right now game is too updated for that to matter)
		//add them to a list to save, which can be used to decode updates
		//probably in a dedicated save updater function
		m.curNum = matList.size();
		matList.add(m);
	}
	
	public static Material getMat(String string) {
		return matMap.get(string);
	}
	
	public static Material getMat(int i) {
		return matList.get(i);
	}
	
	/**
	 * Set up the static flyweights for the materials.
	 */
	public MaterialFactory() {
		
		//baseEnchant > rand*3f, so any baseEnchant >=3f will always enchant
		
		Material misc = new Material();
		misc.name = "patchwork";//mostly combined cotton and linen
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;//heavy for a cloth
		misc.cost = 3;
		misc.baseEnchant = .8f;
		misc.baseResist = 3;
		misc.sharpResist = .5f;
		misc.bluntResist = 1;
		misc.pierceResist = .5f;
		misc.dexMod = 1f;
		misc.sharpMult = 0;
		misc.bluntMult = 0;
		misc.pierceMult = 0;
		misc.tier = 0;
		misc.rarity = .5f;
		misc.fireVul = 2;
		misc.shockVul = .2f;
		misc.freezeVul = .2f;
		misc.shimmer = .05f;
		misc.palIndex = 0;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(Color.LIGHT_GRAY);
		register(misc);
		
		misc = new Material();
		misc.name = "wool";//https://en.wikipedia.org/wiki/Wool
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 7;
		misc.cost = 10;
		misc.baseEnchant = .7f;
		misc.baseResist = 4;
		misc.sharpResist = 1f;
		misc.bluntResist = 1f;
		misc.pierceResist = 1f;
		misc.dexMod = .95f;//mostly for balance
		misc.sharpMult = 0;
		misc.bluntMult = 0;
		misc.pierceMult = 0;
		misc.tier = 0;
		misc.rarity = .5f;
		misc.fireVul = .3f;//highly resistant compared to other mats, neither conductive nor truly flammable
		misc.shockVul = .2f;
		misc.freezeVul = .2f;
		misc.palIndex = 0;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(Color.WHITE);
		register(misc);
		
		
		//https://en.wikipedia.org/wiki/Cotton
		misc = new Material();
		misc.name = "cotton";
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 5;
		misc.cost = 4;
		misc.baseEnchant = .9f;
		misc.baseResist = 2.5f;
		misc.sharpResist = .5f;
		misc.bluntResist = 1;
		misc.pierceResist = .5f;
		misc.dexMod = 1f;
		misc.sharpMult = 0;
		misc.bluntMult = 0;
		misc.pierceMult = 0;
		misc.tier = 0;
		misc.rarity = .1f;
		misc.fireVul = 2;//flammable
		misc.shockVul = .2f;
		misc.freezeVul = .3f;//not as effective alone
		misc.palIndex = 0;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(Color.WHITE);
		register(misc);
		
		//https://en.wikipedia.org/wiki/Linen
		misc = new Material();
		misc.name = "linen";
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 3;
		misc.cost = 4;
		misc.baseEnchant = 1.2f;
		misc.baseResist = 2;
		misc.sharpResist = .5f;
		misc.bluntResist = 1;
		misc.pierceResist = .5f;
		misc.dexMod = 1f;
		misc.sharpMult = 0;
		misc.bluntMult = 0;
		misc.pierceMult = 0;
		misc.tier = 0;
		misc.rarity = .1f;
		misc.fireVul = 1.5f;
		misc.shockVul = .3f;
		misc.freezeVul = 1f;//apparently kinda conductive
		misc.palIndex = 0;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(Color.WHITE);
		register(misc);
		
		misc = new Material();
		misc.name = "silk";
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 2;
		misc.cost = 20;
		misc.baseEnchant = 2.5f;
		misc.baseResist = 0.5f;
		misc.sharpResist = 1f;
		misc.bluntResist = 1;
		misc.pierceResist = 1f;
		misc.dexMod = 1f;
		misc.sharpMult = 0;
		misc.bluntMult = 0;
		misc.pierceMult = 0;
		misc.tier = 1;
		misc.rarity = .2f;
		misc.fireVul = 2;
		misc.shockVul = .6f;
		misc.freezeVul = .6f;
		misc.shimmer = .05f;
		misc.palIndex = 1;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(TrawelColor.colorMix(Color.red,Color.white,.4f));
		register(misc);
		
		/*TODO readd leather
		misc = new Material();
		misc.name = "leather";
		misc.typeList.add("light");
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 5;
		misc.cost = 5;
		misc.baseEnchant = 1;
		misc.baseResist = 3;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = 1;
		misc.sharpMult = 0;
		misc.bluntMult = 0;
		misc.pierceMult = 0;
		misc.tier = 1;
		misc.rarity = 5;
		misc.fireVul = 1.1;
		misc.shockVul = .1;
		misc.freezeVul = .1;
		misc.palIndex = 0;
		misc.soundType = "padding";
		register(misc);
		*/
		misc = new Material();
		misc.name = "iron";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 20;
		misc.cost = 30;
		misc.baseEnchant = .3f;
		misc.baseResist = 6;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 1;
		misc.rarity = 5;
		misc.fireVul = .8f;
		misc.shockVul = 1.5f;
		misc.freezeVul = 2;
		misc.shimmer = .02f;
		misc.sturdy = .3f;
		misc.palIndex = 0;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(131,145,169));
		misc.veinReward = 2;
		register(misc);
		
		misc = new Material();//DOLATER: beta numbers
		misc.name = "tin";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 20;
		misc.cost = 15;
		misc.baseEnchant = .3f;
		misc.baseResist = 4;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 1;
		misc.rarity = 2.5f;
		misc.fireVul = .8f;
		misc.shockVul = 1.5f;
		misc.freezeVul = 2;
		misc.shimmer = .02f;
		misc.sturdy = .3f;
		misc.palIndex = 1;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(169,94,60));
		misc.veinReward = 1;
		register(misc);
		
		misc = new Material();//DOLATER: beta numbers
		misc.name = "copper";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 20;
		misc.cost = 15;
		misc.baseEnchant = .3f;
		misc.baseResist = 4;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 1;
		misc.rarity = 2.5f;
		misc.fireVul = .8f;
		misc.shockVul = 1.5f;
		misc.freezeVul = 2;
		misc.shimmer = .02f;
		misc.sturdy = .3f;
		misc.palIndex = 2;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(198,198,198));
		misc.veinReward = 1;
		register(misc);
		
		misc = new Material();//DOLATER: beta numbers
		misc.name = "bronze";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 30;
		misc.cost = 40;
		misc.baseEnchant = .1f;
		misc.baseResist = 9;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1.15f;
		misc.bluntMult = 1.3f;
		misc.pierceMult = 1.15f;
		misc.tier = 3;
		misc.rarity = 2.5f;
		misc.fireVul = .8f;
		misc.freezeVul = 2;
		misc.shockVul = 1.5f;
		misc.shimmer = .05f;
		misc.sturdy = .4f;
		misc.palIndex = 3;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(169,94,60));
		register(misc);
		
		misc = new Material();
		misc.name = "steel";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 25;
		misc.cost = 50;
		misc.baseEnchant = 0;
		misc.baseResist = 12;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1.3f;
		misc.bluntMult = 1.1f;
		misc.pierceMult = 1.3f;
		misc.tier = 3;
		misc.rarity = 2.5f;
		misc.fireVul = .8f;
		misc.shockVul = 1.5f;
		misc.freezeVul = 2f;
		misc.shimmer = .02f;
		misc.sturdy = .5f;
		misc.palIndex = 4;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(204,204,204));
		register(misc);
		
		misc = new Material();
		misc.name = "silver";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 25;
		misc.cost = 30;
		misc.baseEnchant = 1.2f;
		misc.baseResist = 6;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1;
		misc.bluntMult = 1.2f;
		misc.pierceMult = 1;
		misc.tier = 3;
		misc.rarity = 3;
		misc.fireVul = .8f;
		misc.shockVul = 1.8f;
		misc.freezeVul = 2;
		misc.shimmer = .2f;
		misc.sturdy = .3f;
		misc.palIndex = 5;
		misc.soundType = "metal";
		misc.moneyMultTradeMult = .8f;
		misc.color = TrawelColor.inlineColor(new Color(236,236,236));
		misc.veinReward = 4;
		register(misc);
		
		misc = new Material();
		misc.name = "gold";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 50;
		misc.cost = 50;
		misc.baseEnchant = 2.1f;
		misc.baseResist = 8;
		misc.sharpResist = .1f;
		misc.bluntResist = 1;
		misc.pierceResist = .2f;
		misc.dexMod = .7f;
		misc.sharpMult = .1f;
		misc.bluntMult = 2.7f;
		misc.pierceMult = .1f;
		misc.tier = 3;
		misc.rarity = .5f;
		misc.fireVul = .8f;
		misc.shockVul = 2;
		misc.freezeVul = 2;
		misc.shimmer = .2f;
		misc.sturdy = .2f;
		misc.palIndex = 6;
		misc.soundType = "metal";
		misc.moneyMultTradeMult = .6f;
		misc.color = TrawelColor.inlineColor(new Color(203,185,83));
		misc.veinReward = 6;
		register(misc);
		
		misc = new Material();
		misc.name = "platinum";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 55;
		misc.cost = 60;
		misc.baseEnchant = 1.8f;
		misc.baseResist = 6;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .65f;
		misc.sharpMult = 1;
		misc.bluntMult = 2.2f;
		misc.pierceMult = 1;
		misc.tier = 3;
		misc.rarity = .2f;
		misc.fireVul = .8f;
		misc.shockVul = 1.6f;//??
		misc.freezeVul = 2;
		misc.shimmer = .2f;
		misc.sturdy = .2f;
		misc.palIndex = 7;
		misc.soundType = "metal";
		misc.moneyMultTradeMult = .7f;
		misc.color = TrawelColor.inlineColor(new Color(236,236,236));
		misc.veinReward = 8;
		register(misc);
		
		misc = new Material();
		misc.name = "mythril";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 15;
		misc.cost = 80;
		misc.baseEnchant = 2f;
		misc.baseResist = 14;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1.5f;
		misc.bluntMult = .9f;
		misc.pierceMult = 1.5f;
		misc.tier = 4;
		misc.rarity = .5f;
		misc.fireVul = .6f;
		misc.shockVul = 1;
		misc.freezeVul = 1.5f;
		misc.shimmer = .2f;
		misc.sturdy = .5f;
		misc.palIndex = 9;
		misc.soundType = "metal";
		misc.veinReward = 10;
		misc.color = TrawelColor.inlineColor(new Color(151,131,169));
		register(misc);
		
		misc = new Material();
		misc.name = "adamantine";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.typeList.add(ArmorStyle.SEWN);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 1;
		misc.cost = 120;
		misc.baseEnchant = 1f;
		misc.baseResist = 16;
		misc.sharpResist = 1.2f;
		misc.bluntResist = .8f;
		misc.pierceResist = 1.2f;
		misc.dexMod = 1;
		misc.sharpMult = 3;
		misc.bluntMult = .1f;
		misc.pierceMult = 2;
		misc.tier = 4;
		misc.rarity = .1f;
		misc.fireVul = .5f;
		misc.shockVul = .5f;
		misc.freezeVul = .5f;
		misc.shimmer = .2f;
		misc.sturdy = .5f;
		misc.palIndex = 10;
		misc.soundType = "metal";
		misc.veinReward = 12;
		misc.color = TrawelColor.inlineColor(new Color(160,182,255));
		register(misc);
		
		misc = new Material();
		misc.name = "sunsteel";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;//was disabled but it was still generating?
		misc.weapon = true;
		misc.weight = 25;
		misc.cost = 120;
		misc.baseEnchant = 0;
		misc.baseResist = 16;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 2.3f;
		misc.bluntMult = 2.1f;
		misc.pierceMult = 2.3f;
		misc.tier = 4;
		misc.rarity = .25f;
		misc.fireVul = .8f;
		misc.shockVul = 1.5f;
		misc.freezeVul = 2;
		misc.shimmer = .2f;
		misc.sturdy = .5f;
		misc.palIndex = 11;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(249,255,160));
		register(misc);
		
		misc = new Material();
		misc.name = "ectoplasm";
		misc.typeList.add(ArmorStyle.FABRIC);
		misc.armor = false;
		misc.weapon = true;
		misc.weight = 2;
		misc.cost = 40;
		misc.baseEnchant = 1;
		misc.baseResist = 4;
		misc.sharpResist = .8f;
		misc.bluntResist = 2;
		misc.pierceResist = .6f;
		misc.dexMod = 1f;
		misc.sharpMult = 1;
		misc.bluntMult = .8f;
		misc.pierceMult = 1;
		misc.tier = 2;
		misc.rarity = .25f;
		misc.fireVul = 2;
		misc.shockVul = 1;
		misc.freezeVul = 1;
		misc.shimmer = .1f;//midrange
		misc.palIndex = 10;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(Color.BLUE);
		register(misc);
		
		misc = new Material();
		misc.name = "moonsilver";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 25;
		misc.cost = 60;
		misc.baseEnchant = 1.5f;
		misc.baseResist = 7;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1.2f;
		misc.bluntMult = 1.3f;
		misc.pierceMult = 1.2f;
		misc.tier = 4;
		misc.rarity = .25f;
		misc.fireVul = .8f;
		misc.shockVul = 1.8f;
		misc.freezeVul = 2;
		misc.shimmer = .6f;//extremely high chance
		misc.sturdy = .3f;
		misc.palIndex = 13;
		misc.soundType = "metal";
		misc.veinReward = 8;
		misc.color = TrawelColor.inlineColor(new Color(255,255,255));
		register(misc);
		
		misc = new Material();
		misc.name = "solar gold";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.typeList.add(ArmorStyle.MAIL);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 100;
		misc.cost = 100;
		misc.baseEnchant = 3;
		misc.baseResist = 10;
		misc.sharpResist = .3f;
		misc.bluntResist = 1.3f;
		misc.pierceResist = .4f;
		misc.dexMod = .5f;
		misc.sharpMult = .3f;
		misc.bluntMult = 3f;
		misc.pierceMult = .3f;
		misc.tier = 4;
		misc.rarity = .1f;
		misc.fireVul = .8f;
		misc.shockVul = 2;
		misc.freezeVul = 2;
		misc.shimmer = .8f;//extremely high chance
		misc.sturdy = .2f;
		misc.palIndex = 12;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(255,210,73));
		register(misc);
		
		
		misc = new Material();
		misc.name = "diamond";
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 100;
		misc.baseEnchant = 1.5f;
		misc.baseResist = 6;
		misc.sharpResist = 4;
		misc.bluntResist = .1f;
		misc.pierceResist = 4;
		misc.dexMod = .8f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 4;
		misc.rarity = .05f;
		misc.fireVul = 0.05f;
		misc.shockVul = .5f;
		misc.freezeVul = .5f;
		misc.shimmer = .5f;
		misc.palIndex = 0;
		misc.soundType = "crystal";
		misc.color = TrawelColor.inlineColor(new Color(210,227,255));
		register(misc);
		
		misc = new Material();//yeah these crystal stats will be totally inaccurate DOLATER
		misc.name = "emerald";
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 60;
		misc.baseEnchant = 1.5f;
		misc.baseResist = 6;
		misc.sharpResist = 2;
		misc.bluntResist = .5f;
		misc.pierceResist = 2;
		misc.dexMod = .8f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 4;
		misc.rarity = .1f;
		misc.fireVul = .5f;
		misc.shockVul = 0.05f;
		misc.freezeVul = .5f;
		misc.shimmer = .5f;
		misc.palIndex = 1;
		misc.soundType = "crystal";
		misc.color = TrawelColor.inlineColor(new Color(210,255,216));
		register(misc);
		
		misc = new Material();
		misc.name = "ruby";
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 60;
		misc.baseEnchant = 2;
		misc.baseResist = 6;
		misc.sharpResist = 2;
		misc.bluntResist = .1f;
		misc.pierceResist = 2;
		misc.dexMod = .8f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 4;
		misc.rarity = .1f;
		misc.fireVul = 0.05f;
		misc.shockVul = .7f;
		misc.freezeVul = .3f;
		misc.shimmer = .5f;
		misc.palIndex = 2;
		misc.soundType = "crystal";
		misc.color = TrawelColor.inlineColor(new Color(237,163,175));
		register(misc);
		
		misc = new Material();
		misc.name = "sapphire";
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 60;
		misc.baseEnchant = 1;
		misc.baseResist = 6;
		misc.sharpResist = 2;
		misc.bluntResist = .3f;
		misc.pierceResist = 2;
		misc.dexMod = .8f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 4;
		misc.rarity = .1f;
		misc.fireVul = .5f;
		misc.shockVul = .5f;
		misc.freezeVul = 0.05f;
		misc.shimmer = .5f;
		misc.palIndex = 3;
		misc.soundType = "crystal";
		misc.color = TrawelColor.inlineColor(new Color(158,184,228));
		register(misc);
		
		misc = new Material();
		misc.name = "nevermelt ice";
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 50;
		misc.baseEnchant = 1;
		misc.baseResist = 8;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .8f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 3;
		misc.rarity = .2f;
		misc.fireVul = 0.1f;
		misc.shockVul = .5f;
		misc.freezeVul = 2;
		misc.shimmer = .5f;
		misc.palIndex = 4;
		misc.color = TrawelColor.inlineColor(new Color(229,238,255));
		misc.soundType = "crystal";
		register(misc);
		
		misc = new Material();
		misc.name = "topaz";
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 60;
		misc.baseEnchant = 2;
		misc.baseResist = 6;
		misc.sharpResist = 2;
		misc.bluntResist = .1f;
		misc.pierceResist = 2;
		misc.dexMod = .8f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 4;
		misc.rarity = .1f;
		misc.fireVul = 0.05f;
		misc.shockVul = .7f;
		misc.freezeVul = .3f;
		misc.shimmer = .5f;
		misc.palIndex = 5;
		misc.soundType = "crystal";
		misc.color = TrawelColor.inlineColor(new Color(255,226,210));
		register(misc);
		
		misc = new Material();
		misc.name = "amethyst";
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.GEM);
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 60;
		misc.baseEnchant = 1;
		misc.baseResist = 6;
		misc.sharpResist = 2;
		misc.bluntResist = .3f;
		misc.pierceResist = 2;
		misc.dexMod = .8f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 4;
		misc.rarity = .1f;
		misc.fireVul = .5f;
		misc.shockVul = .5f;
		misc.freezeVul = 0.05f;
		misc.shimmer = .5f;
		misc.palIndex = 6;
		misc.soundType = "crystal";
		misc.color = TrawelColor.inlineColor(new Color(224,166,225));
		register(misc);
		
		misc = new Material();
		misc.name = "hardwood";
		misc.typeList.add(ArmorStyle.GROWN);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 15;
		misc.cost = 20;
		misc.baseEnchant = 1f;
		misc.baseResist = 6;
		misc.sharpResist = 1f;
		misc.bluntResist = 1f;
		misc.pierceResist = 1f;
		misc.dexMod = .9f;
		misc.sharpMult = 1f;
		misc.bluntMult = 1f;
		misc.pierceMult = 1f;
		misc.tier = 0;
		misc.rarity = 1f;
		misc.palIndex = 8;
		misc.soundType = "wood";
		misc.color = TrawelColor.inlineColor(new Color(131,94,35));
		register(misc);
		
		misc = new Material();
		misc.name = "softwood";
		misc.typeList.add(ArmorStyle.GROWN);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 10;
		misc.cost = 20;
		misc.baseEnchant = 1.5f;
		misc.baseResist = 4;
		misc.sharpResist = .8f;
		misc.bluntResist = 1.2f;
		misc.pierceResist = 1f;
		misc.dexMod = .95f;
		misc.sharpMult = .6f;
		misc.bluntMult = .6f;
		misc.pierceMult = .6f;
		misc.tier = 0;
		misc.rarity = 0.5f;
		misc.palIndex = 8;
		misc.soundType = "wood";
		misc.color = TrawelColor.inlineColor(new Color(131,94,35));
		register(misc);
		
		//beast materials
		misc = new Material();
		misc.name = "flesh";
		misc.typeList.add(ArmorStyle.BODY);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 0;
		misc.cost = 0;
		misc.baseEnchant = 0;
		misc.baseResist = 1f;
		misc.sharpResist = .75f;
		misc.bluntResist = 1;
		misc.pierceResist = .9f;
		misc.dexMod = 1f;
		misc.sharpMult = .5f;
		misc.bluntMult = 1.1f;
		misc.pierceMult = .5f;
		misc.tier = 1;
		misc.rarity = 0;
		misc.fireVul = 1.2f;
		misc.shockVul = 1.2f;
		misc.freezeVul = 1.2f;
		misc.palIndex = 0;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(new Color(212,89,107));
		register(misc);
		
		misc = new Material();
		misc.name = "hide";//will compete with 'hide armor', sadly
		misc.typeList.add(ArmorStyle.BODY);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 20;
		misc.cost = 30;
		misc.baseEnchant = 0;
		misc.baseResist = 4;
		misc.sharpResist = .75f;
		misc.bluntResist = 1;
		misc.pierceResist = .9f;
		misc.dexMod = 1f;
		misc.sharpMult = .5f;
		misc.bluntMult = 1.1f;
		misc.pierceMult = .5f;
		misc.tier = 1;
		misc.rarity = 0;
		misc.fireVul = 1.1f;
		misc.shockVul = 1.1f;
		misc.freezeVul = 1.1f;
		misc.sturdy = 1f;//always sturdy
		misc.palIndex = 0;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(new Color(212,89,107));
		register(misc);
		
		misc = new Material();
		misc.name = "fishscales";
		misc.typeList.add(ArmorStyle.BODY);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 0;
		misc.cost = 0;
		misc.baseEnchant = 0;
		misc.baseResist = 3f;
		misc.sharpResist = 1.5f;
		misc.bluntResist = 1;
		misc.pierceResist = 1.1f;
		misc.dexMod = .9f;
		misc.sharpMult = 1f;
		misc.bluntMult = 1f;
		misc.pierceMult = 1f;
		misc.tier = 1;
		misc.rarity = 0;
		misc.fireVul = .5f;
		misc.shockVul = 2f;
		misc.freezeVul = .7f;
		misc.shimmer = .2f;//chance of shimmer on scales
		misc.palIndex = 0;
		misc.soundType = "flesh";
		misc.color = TrawelColor.inlineColor(new Color(212,89,107));
		register(misc);
		
		misc = new Material();
		misc.name = "bone";
		misc.typeList.add(ArmorStyle.BODY);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 20;
		misc.cost = 30;
		misc.baseEnchant = 0;
		misc.baseResist = 10;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .95f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 1;
		misc.rarity = 0;
		misc.fireVul = 1;
		misc.shockVul = 1;
		misc.freezeVul = 1;
		misc.palIndex = 0;
		misc.soundType = "flesh";
		register(misc);
		
		misc = new Material();
		misc.name = "wood";//now only mimic bodywood
		misc.typeList.add(ArmorStyle.BODY);
		misc.armor = true;
		misc.weapon = false;
		misc.weight = 2;
		misc.cost = 3;
		misc.baseEnchant = 0;
		misc.baseResist = 6;
		misc.sharpResist = 1;
		misc.bluntResist = 1.5f;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = .2f;
		misc.bluntMult = .8f;
		misc.pierceMult = .3f;
		misc.tier = 0;
		misc.rarity = 0f;
		misc.fireVul = 2;
		misc.shockVul = 0.05f;
		misc.freezeVul = .5f;
		misc.palIndex = 8;
		misc.soundType = "wood";
		misc.color = TrawelColor.inlineColor(new Color(131,94,35));
		register(misc);
		
		misc = new Material();//used for drudgers right now
		misc.name = "rusty iron";
		misc.typeList.add(ArmorStyle.PLATE);//might make it's own style later
		misc.armor = false;
		misc.weapon = false;
		misc.weight = 25;
		misc.cost = 20;
		misc.baseEnchant = .1f;
		misc.baseResist = 5;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .8f;
		misc.sharpMult = .95f;
		misc.bluntMult = .95f;
		misc.pierceMult = .95f;
		misc.tier = 1;
		misc.rarity = 0;
		misc.fireVul = .8f;
		misc.shockVul = 1.3f;
		misc.freezeVul = 1.5f;
		misc.palIndex = 0;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(105,113,128));
		register(misc);
		
		misc = new Material();//used for testing
		misc.name = "test";
		misc.typeList.add(ArmorStyle.PLATE);
		misc.armor = true;
		misc.weapon = true;
		misc.weight = 20;
		misc.cost = 1;
		misc.baseEnchant = 1f;
		misc.baseResist = 1;
		misc.sharpResist = 1;
		misc.bluntResist = 1;
		misc.pierceResist = 1;
		misc.dexMod = .9f;
		misc.sharpMult = 1;
		misc.bluntMult = 1;
		misc.pierceMult = 1;
		misc.tier = 1;
		misc.rarity = 0;
		misc.fireVul = 1f;
		misc.shockVul = 1f;
		misc.freezeVul = 1f;
		misc.palIndex = 0;
		misc.soundType = "metal";
		misc.color = TrawelColor.inlineColor(new Color(131,145,169));
		register(misc);
		
		
		
		for (Material m: matList) {
			matMap.put(m.name, m);
		}
		
		tableSetup();
	}
	
	public static void tableSetup() {
		float[] wWeightList = new float[matList.size()];
		float[] aWeightList = new float[matList.size()];
		Material curMat;
		for (int i = matList.size()-1;i>=0;--i) {
			curMat = matList.get(i);
			wWeightList[i] = curMat.weapon ? (float) curMat.rarity : 0f;
			aWeightList[i] = curMat.armor ? (float) curMat.rarity : 0f;
		}
		weapMats = new WeightedTable(wWeightList);
		armMats = new WeightedTable(aWeightList);
		
	}
	
	public static Material randArmorMat() {
		return matList.get(armMats.random(Rand.getRand()));
	}
	
	public static Material randWeapMat() {
		return matList.get(weapMats.random(Rand.getRand()));
	}
	
	public static void materialWeapDiag() {
		ArrayList<Material> copyList = new ArrayList<Material>();
		double totalRarity = 0;
		for (Material mat: matList){
			if (mat.weapon == true) {
				copyList.add(mat);
				totalRarity +=mat.rarity;
			}
		}
		for (int i = 0; i < copyList.size();i++) {
			Print.println(copyList.get(i).name+"% "+copyList.get(i).rarity/totalRarity);
		}
	}
}
