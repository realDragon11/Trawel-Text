package trawel.helper.constants;

import java.util.ArrayList;
import java.util.List;

import trawel.towns.features.Feature;
import trawel.towns.features.fight.Arena;
import trawel.towns.features.fight.Champion;
import trawel.towns.features.fight.Forest;
import trawel.towns.features.fight.Mountain;
import trawel.towns.features.fight.Slum;
import trawel.towns.features.misc.Altar;
import trawel.towns.features.misc.Docks;
import trawel.towns.features.misc.Garden;
import trawel.towns.features.misc.Lot;
import trawel.towns.features.misc.TravelingFeature;
import trawel.towns.features.nodes.Beach;
import trawel.towns.features.nodes.Dungeon;
import trawel.towns.features.nodes.Graveyard;
import trawel.towns.features.nodes.Grove;
import trawel.towns.features.nodes.Mine;
import trawel.towns.features.services.Blacksmith;
import trawel.towns.features.services.Doctor;
import trawel.towns.features.services.Enchanter;
import trawel.towns.features.services.Inn;
import trawel.towns.features.services.Library;
import trawel.towns.features.services.Oracle;
import trawel.towns.features.services.Store;
import trawel.towns.features.services.WitchHut;
import trawel.towns.features.services.guilds.HeroGuild;
import trawel.towns.features.services.guilds.HunterGuild;
import trawel.towns.features.services.guilds.MerchantGuild;
import trawel.towns.features.services.guilds.RogueGuild;

public class FeatureTutorialLists {

	public static final List<String> CATEGORY_LIST = new ArrayList<String>();
	public static final List<List<Class<? extends Feature>>> ELEMENT_LISTS = new ArrayList<List<Class<? extends Feature>>>();
	
	static {
		List<Class<? extends Feature>> list;
		
		CATEGORY_LIST.add("Vital Services");
		list = new ArrayList<Class<? extends Feature>>();
		list.add(Store.class);
		list.add(Doctor.class);
		list.add(Inn.class);
		list.add(Blacksmith.class);
		ELEMENT_LISTS.add(list);
		
		CATEGORY_LIST.add("Advanced Services");
		list = new ArrayList<Class<? extends Feature>>();
		list.add(Lot.class);
		list.add(Docks.class);
		list.add(WitchHut.class);
		list.add(Garden.class);
		list.add(Library.class);
		list.add(Enchanter.class);
		list.add(Slum.class);
		list.add(TravelingFeature.class);
		list.add(Altar.class);
		list.add(Oracle.class);
		ELEMENT_LISTS.add(list);
		
		CATEGORY_LIST.add("Major Guilds");
		list = new ArrayList<Class<? extends Feature>>();
		list.add(MerchantGuild.class);
		list.add(HeroGuild.class);
		list.add(HunterGuild.class);
		list.add(RogueGuild.class);
		ELEMENT_LISTS.add(list);
		
		CATEGORY_LIST.add("Node Exploration");
		list = new ArrayList<Class<? extends Feature>>();
		list.add(Grove.class);
		list.add(Mine.class);
		list.add(Dungeon.class);
		list.add(Graveyard.class);
		list.add(Beach.class);
		ELEMENT_LISTS.add(list);
		
		CATEGORY_LIST.add("Encounters");
		list = new ArrayList<Class<? extends Feature>>();
		list.add(Arena.class);
		list.add(Champion.class);
		list.add(Forest.class);
		list.add(Mountain.class);
		ELEMENT_LISTS.add(list);
	}
	
	public static final List<Class<? extends Feature>> getFeatures(String categoryName) {
		int index = CATEGORY_LIST.indexOf(categoryName);
		return ELEMENT_LISTS.get(index);//can just throw a -1 error if invalid category name
	}
}
