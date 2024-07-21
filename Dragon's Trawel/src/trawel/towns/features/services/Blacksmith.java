package trawel.towns.features.services;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Item;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.World;
import trawel.towns.data.FeatureData;
import trawel.towns.data.FeatureData.FeatureTutorialCategory;
import trawel.towns.features.Feature;

public class Blacksmith extends Feature {
	
	static {
		FeatureData.registerFeature(Blacksmith.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" forge items for shops. "+fancyNamePlural()+" level and temper equipment."
						+(Player.isGameMode_NoPunishments() ? "" : " "+fancyNamePlural() +" also fix "+Effect.DAMAGED.getName()+"."));
			}
			
			@Override
			public int priority() {
				return 20;
			}
			
			@Override
			public String name() {
				return "Blacksmith";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_AUX_SERVICE;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.VITAL_SERVICES;
			}
		});
	}
	
	private static final long serialVersionUID = 1L;
	private double time = 10.;
	private Store store;
	
	public Blacksmith(String name,int tier, Store s){
		this.name = name;
		this.tier = tier;
		this.store = s;
	}
	
	public Blacksmith(int tier, Store s){
		this.tier = tier;
		this.store = s;
		name = store.getName() +" " + Rand.choose("Smith","Blacksmith","Smithy","Forge");
	}
	
	@Override
	public String nameOfType() {
		return "Blacksmith";
	}
	
	@Override
	public Area getArea() {
		return Area.MISC_SERVICE;
	}
	
	@Override
	public void go() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "You have " + World.currentMoneyDisplay(Player.player.getGold()) + " and "+Player.bag.getAether()+ " aether.";
					}});
				int forgePrice = (int) Math.ceil(getUnEffectiveLevel());
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"Forge +"+tier+" item for "+store.getName()+" (" + World.currentMoneyDisplay(forgePrice)+")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() >= forgePrice) {
							Player.player.loseGold(forgePrice);
							Item i = store.addAnItem();
							if (i == null) {
								Print.println(TrawelColor.RESULT_PASS+"An item has been forged and sent to " + store.getName() + "!");
							}else {
								Print.println(i.getName() +TrawelColor.RESULT_PASS+ " was created and put on sale in " + store.getName()+"!");
							}
						}else {
							Print.println(TrawelColor.RESULT_ERROR+"You can't afford that!");
						}
						return false;
					}});
				//MAYBELATER: level is represented by aether, so this would probably be better off in the enchanter
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_BOTH_PAYMENT+"Improve any equipment up to +" + tier +" level.";
					}

					@Override
					public boolean go() {
						int in = Player.player.askSlot();
						if (in == 9) {
							return false;
						}
						Item item;
						if (in <=5) {
							item = Player.bag.getArmorSlot(in-1);
						}else {
							item = Player.bag.getHand();
						}
						if (item.getLevel() >= tier) {
							Print.println(TrawelColor.RESULT_ERROR+"This item is too high in level to improve here!");
							return false;
						}
						int mcost = item.getMoneyValue();
						int acost = (int) ((mcost * 100f)*(IEffectiveLevel.unclean(item.getLevel()+1)));
						String costString = World.currentMoneyDisplay(mcost) + " and " +acost + " aether";
						if (Player.player.getGold() < mcost) {
							if (Player.bag.getAether() < acost) {
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford to improve '"+item.getName()+"'. ("+costString+")");
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford to pay the blacksmith to improve '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Player.bag.getAether() < acost) {
							Print.println(TrawelColor.RESULT_ERROR+"You don't have enough aether to improve '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						Print.println("Improve your item to +" + (item.getLevel()+1) + " for "+costString+"?");
						if (Input.yesNo()) {
							Print.println(TrawelColor.RESULT_PASS+"Item improved.");
							Player.player.loseGold(mcost);
							Player.bag.addAether(-acost);
							item.levelUp();
							item.display(1);
							Networking.unlockAchievement("smith1");
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Temper any equipment up to +" + tier +" level.";
					}

					@Override
					public boolean go() {
						int in = Player.player.askSlot();
						if (in == 9) {
							return false;
						}
						Item item;
						if (in <=5) {
							item = Player.bag.getArmorSlot(in-1);
						}else {
							item = Player.bag.getHand();
						}
						if (!item.hasNegQuality()) {
							Print.println(item.getName() +TrawelColor.RESULT_ERROR+ " does not have any negative qualities!");
							return false;
						}
						if (item.getLevel() > tier) {
							Print.println(item.getName()+TrawelColor.RESULT_ERROR+" is too high in level ("+item.getLevel()+") to temper here!");
							return false;
						}
						int mcost = item.getMoneyValue();
						int acost = Math.round((3f+item.getQualityTier())*(IEffectiveLevel.unclean(item.getLevel())));
						String costString = World.currentMoneyDisplay(mcost) + " and " +acost + " amber";
						if (Player.player.getGold() < mcost) {
							if (Gem.AMBER.getGem() < acost) {
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford to temper '"+item.getName()+"'. ("+costString+")");
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford to pay the blacksmith to temper '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Gem.AMBER.getGem() < acost) {
							Print.println(TrawelColor.RESULT_ERROR+"You don't have enough amber to temper '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						Print.println("Temper your "+item.getName()+" for "+costString+"?");
						if (Input.yesNo()) {
							if (item.temperNegQuality(1) == 0) {
								Print.println(TrawelColor.RESULT_FAIL+"Tempering failed.");
								return false;
							}
							Print.println(TrawelColor.RESULT_PASS+"Item tempered.");
							Player.player.loseGold(mcost);
							Gem.AMBER.changeGem(-acost);
							item.display(1);
							Networking.unlockAchievement("smith1");
						}
						return false;
					}});
				if (Player.player.getPerson().hasEffect(Effect.DAMAGED)) {
					int repairCost = (int) Math.ceil(getUnEffectiveLevel());
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Repair Damaged Effect. ("+World.currentMoneyDisplay(repairCost)+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= repairCost) {
								Player.player.loseGold(repairCost);
								Print.println(TrawelColor.RESULT_PASS+"They tinker with your equipment.");
								//next call displays the effect results
								Player.player.getPerson().repairEffects();
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford that!");
							}
							return false;
						}});
				}
				list.add(new MenuBack("Leave."));
				return list;
			}});
	}

	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		time -= addtime;
		if (time <= 0) {
			store.addAnItem();//TODO: should probably add in event?
			time = 12+(Rand.randFloat()*30);
		}
		return null;
	}

}
