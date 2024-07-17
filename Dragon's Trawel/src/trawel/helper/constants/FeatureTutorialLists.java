package trawel.helper.constants;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.helper.methods.FeatureTutorialPrinter;
import trawel.towns.features.Feature;

public class FeatureTutorialLists {

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
	private static final List<String> name_list = new ArrayList<String>();
	private static final List<FeatureTutorialPrinter> printer_list = new ArrayList<FeatureTutorialPrinter>();
	private static final List<FeatureTutorialCategory> category_list = new ArrayList<FeatureTutorialLists.FeatureTutorialCategory>();
	
	public static final void registerFeature(Class<? extends Feature> clazz,String name, FeatureTutorialPrinter printer, FeatureTutorialCategory category) {
		class_list.add(clazz);
		name_list.add(name);
		printer_list.add(printer);
		category_list.add(category);
	}
	
	public static final String getName(Class<? extends Feature> clazz) {
		return name_list.get(class_list.indexOf(clazz));
	}
	
	public static final void printTutorial(Class<? extends Feature> clazz) {
		printer_list.get(class_list.indexOf(clazz)).print();;
	}
	
	public static final List<Class<? extends Feature>> getFeaturesOf(FeatureTutorialCategory category){
		List<Class<? extends Feature>> list = new ArrayList<Class<? extends Feature>>();
		for (int i = 0; i < category_list.size();i++) {
			//the order will be all out of whack, might fix later by just making it have a dedicated type I can sort by priority instead of a bunch of lists
			if (category_list.get(i) == category) {
				list.add(class_list.get(i));
			}
		}
		return list;
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
							Input.menuGo(new MenuGenerator() {
								
								@Override
								public List<MenuItem> gen() {
									List<MenuItem> list = new ArrayList<MenuItem>();
									final List<Class<? extends Feature>> fList = getFeaturesOf(c);
									for (Class<? extends Feature> f: fList) {
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return getName(f);
											}

											@Override
											public boolean go() {
												printTutorial(f);
												return false;
											}});
									}
									list.add(new MenuBack());
									return list;
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
