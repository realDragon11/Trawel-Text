package trawel.towns.services;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking.Area;
import trawel.Effect;
import trawel.extra;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Item;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.World;

public class Blacksmith extends Feature {
	
	private static final long serialVersionUID = 1L;
	private double time = 10.;
	private Store store;
	
	public Blacksmith(String name,int tier, Store s){
		this.name = name;
		this.tier = tier;
		this.store = s;
		tutorialText = "Blacksmith";
		area_type = Area.MISC_SERVICE;
	}
	
	public Blacksmith(int tier, Store s){
		this.tier = tier;
		this.store = s;
		name = store.getName() +" " + extra.choose("Smith","Blacksmith","Smithy","Forge");
		tutorialText = "Blacksmith";
		area_type = Area.MISC_SERVICE;
	}
	
	@Override
	public String getColor() {
		return extra.F_AUX_SERVICE;
	}
	
	@Override
	public void go() {
		extra.menuGo(new MenuGenerator() {

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
						return extra.SERVICE_CURRENCY+"Forge +"+tier+" item for "+store.getName()+" (" + World.currentMoneyDisplay(forgePrice)+")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() >= forgePrice) {
							Player.player.loseGold(forgePrice);
							Item i = store.addAnItem();
							if (i == null) {
								extra.println(extra.RESULT_PASS+"An item has been forged and sent to " + store.getName() + "!");
							}else {
								extra.println(i.getName() +extra.RESULT_PASS+ " was created and put on sale in " + store.getName()+"!");
							}
						}else {
							extra.println(extra.RESULT_ERROR+"You can't afford that!");
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_BOTH_PAYMENT+"Improve any equipment up to +" + tier +" level.";
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
							extra.println(extra.RESULT_ERROR+"This item is too high in level to improve here!");
							return false;
						}
						int mcost = item.getMoneyValue();
						int acost = (int) ((mcost * 100f)*(IEffectiveLevel.unclean(item.getLevel()+1)));
						String costString = World.currentMoneyDisplay(mcost) + " and " +acost + " aether";
						if (Player.player.getGold() < mcost) {
							if (Player.bag.getAether() < acost) {
								extra.println(extra.RESULT_ERROR+"You can't afford to improve '"+item.getName()+"'. ("+costString+")");
							}else {
								extra.println(extra.RESULT_ERROR+"You can't afford to pay the blacksmith to improve '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Player.bag.getAether() < acost) {
							extra.println(extra.RESULT_ERROR+"You don't have enough aether to improve '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						extra.println("Improve your item to +" + (item.getLevel()+1) + " for "+costString+"?");
						if (extra.yesNo()) {
							extra.println(extra.RESULT_PASS+"Item improved.");
							Player.player.loseGold(mcost);
							Player.bag.addAether(-acost);
							item.levelUp();
							item.display(1);
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_SPECIAL_PAYMENT+"Temper any equipment up to +" + tier +" level.";
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
							extra.println(item.getName() +extra.RESULT_ERROR+ " does not have any negative qualities!");
							return false;
						}
						if (item.getLevel() > tier) {
							extra.println(item.getName()+extra.RESULT_ERROR+" is too high in level ("+item.getLevel()+") to temper here!");
							return false;
						}
						int mcost = item.getMoneyValue();
						int acost = Math.round((3f+item.getQualityTier())*(IEffectiveLevel.unclean(item.getLevel())));
						String costString = World.currentMoneyDisplay(mcost) + " and " +acost + " amber";
						if (Player.player.getGold() < mcost) {
							if (Gem.AMBER.getGem() < acost) {
								extra.println(extra.RESULT_ERROR+"You can't afford to temper '"+item.getName()+"'. ("+costString+")");
							}else {
								extra.println(extra.RESULT_ERROR+"You can't afford to pay the blacksmith to temper '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Gem.AMBER.getGem() < acost) {
							extra.println(extra.RESULT_ERROR+"You don't have enough amber to temper '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						extra.println("Temper your "+item.getName()+" for "+costString+"?");
						if (extra.yesNo()) {
							if (item.temperNegQuality(1) == 0) {
								extra.println(extra.RESULT_FAIL+"Tempering failed.");
								return false;
							}
							extra.println(extra.RESULT_PASS+"Item tempered.");
							Player.player.loseGold(mcost);
							Gem.AMBER.changeGem(-acost);
							item.display(1);
						}
						return false;
					}});
				if (Player.player.getPerson().hasEffect(Effect.DAMAGED)) {
					int repairCost = (int) Math.ceil(getUnEffectiveLevel());
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.SERVICE_CURRENCY+"Repair Damaged Effect. ("+World.currentMoneyDisplay(repairCost)+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= repairCost) {
								Player.player.loseGold(repairCost);
								extra.println(extra.RESULT_PASS+" They tinker with your equipment.");
								//next call displays the effect results
								Player.player.getPerson().repairEffects();
							}else {
								extra.println(extra.RESULT_ERROR+"You can't afford that!");
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
			time = 12+(extra.randFloat()*30);
		}
		return null;
	}

}
