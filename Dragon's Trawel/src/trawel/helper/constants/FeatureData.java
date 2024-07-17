package trawel.helper.constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.core.Input;
import trawel.towns.features.Feature;

public abstract class FeatureData {

	public enum FeatureTutorialCategory{
		VITAL_SERVICES("Vital Services"),
		ADVANCED_SERVICES("Advanced Services"),
		MAJOR_GUILDS("Major Guilds"),
		NODE_EXPLORATION("Node Exploration"),
		ENCOUNTERS("Encounters");
		
		public final String name;
		FeatureTutorialCategory(String _name){
			name = _name;
		}
	}

	//public static final List<String> CATEGORY_LIST = new ArrayList<String>();
	//public static final List<List<Class<? extends Feature>>> ELEMENT_LISTS = new ArrayList<List<Class<? extends Feature>>>();
	
	private static final List<Class<? extends Feature>> class_list = new ArrayList<Class<? extends Feature>>();
	private static final List<FeatureData> data_list = new ArrayList<FeatureData>();
	
	private static final Map<FeatureTutorialCategory,List<FeatureData>> by_category = new HashMap<FeatureData.FeatureTutorialCategory, List<FeatureData>>();
	static {
		for (FeatureTutorialCategory c: FeatureTutorialCategory.values()) {
			by_category.put(c,new ArrayList<FeatureData>());
		}
	}
	
	/*
	public static final void finishInit() {
		for (FeatureData d: data_list) {
			//since we add it here, we can avoid a full sort + adding by just sorting while we add
			List<FeatureData> insert = by_category.get(d.category());
			int index = 0;
			//while we're still in the list, and of higher line number than the current element, keep going down to list to find the insert point
			while (index < insert.size() && insert.get(index).priority() <= d.priority()) {
				index++;
			}
			insert.add(index,d);
		}
	}*/
	
	public static final void registerFeature(Class<? extends Feature> clazz,FeatureData data) {
		class_list.add(clazz);
		data_list.add(data);
		
		//due to limitations, classes that aren't loaded won't be statically inited, but it should load all the classes actually in the world
		//and the menu is only useable in-game for now
		
		//since we add it here, we can avoid a full sort + adding by just sorting while we add
		List<FeatureData> insert = by_category.get(data.category());
		int index = 0;
		//while we're still in the list, and of higher line number than the current element, keep going down to list to find the insert point
		while (index < insert.size() && insert.get(index).priority() <= data.priority()) {
			index++;
		}
		insert.add(index,data);
	}
	
	public static final String getName(Class<? extends Feature> clazz) {
		return data_list.get(class_list.indexOf(clazz)).name();
	}
	
	public static final void printTutorial(Class<? extends Feature> clazz) {
		data_list.get(class_list.indexOf(clazz)).tutorial();;
	}
	
	public static final FeatureData getData(Class<? extends Feature> clazz) {
		return data_list.get(class_list.indexOf(clazz));
	}
	
	public static final List<FeatureData> getFeaturesOf(FeatureTutorialCategory category){
		return by_category.get(category);
	}
	
	public static final void getGlossary() {
		Input.menuGo(new MenuGenerator() {
			
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				for (FeatureTutorialCategory c: FeatureTutorialCategory.values()) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return c.name+".";
						}

						@Override
						public boolean go() {
							final List<MenuItem> fList = new ArrayList<MenuItem>();
							for (FeatureData f: getFeaturesOf(c)) {
								fList.add(new MenuSelect() {

									@Override
									public String title() {
										return f.name();
									}

									@Override
									public boolean go() {
										f.tutorial();
										return false;
									}});
							}
							Input.menuGo(new ScrollMenuGenerator(fList.size(),"Prior <> Features","Next <> Features") {

								@Override
								public List<MenuItem> forSlot(int i) {
									return Collections.singletonList(fList.get(i));
								}

								@Override
								public List<MenuItem> header() {
									return null;
								}

								@Override
								public List<MenuItem> footer() {
									return Collections.singletonList(new MenuBack());
								}
							});
							return false;
						}});
				}
				list.add(new MenuBack());
				return list;
			}
		});
	}
	
	public abstract void tutorial();
	public abstract String name();
	public abstract FeatureTutorialCategory category();
	public abstract int priority();
	/*
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
	}*/
	
	/*
	 static {
		FeatureTutorialLists.registerFeature(.class,"",
				new FeatureTutorialPrinter() {
					
					@Override
					public void print() {
						Print.println("test ");
					}
				},FeatureTutorialCategory.);
	}
	 */
}
