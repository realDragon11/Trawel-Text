package trawel.towns.services;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Item;
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
						return "Forge +"+tier+" item for "+store.getName()+" (" + World.currentMoneyDisplay(forgePrice)+")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() >= forgePrice) {
							Player.player.loseGold(forgePrice);
							Item i = store.addAnItem();
							if (i == null) {
								extra.println("An item has been forged and sent to " + store.getName() + "!");
							}else {
								extra.println(i.getName() + " was created and put on sale in " + store.getName()+"!");
							}
						}else {
							extra.println("You can't afford that!");
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Improve any equipment up to +" + tier +" level.";
					}

					@Override
					public boolean go() {
						int in = askSlot();
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
							extra.println("This item is too high in level to improve here!");
							return false;
						}
						int mcost = item.getMoneyValue();
						int acost = (int) ((mcost * 100f)*(IEffectiveLevel.unclean(item.getLevel()+1)));
						String costString = World.currentMoneyDisplay(mcost) + " and " +acost + " aether";
						if (Player.player.getGold() < mcost) {
							if (Player.bag.getAether() < acost) {
								extra.println("You can't afford to improve '"+item.getName()+"'. ("+costString+")");
							}else {
								extra.println("You can't afford to pay the blacksmith to improve '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Player.bag.getAether() < acost) {
							extra.println("You don't have enough aether to improve '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						extra.println("Improve your item to +" + (item.getLevel()+1) + " for "+costString+"?");
						if (extra.yesNo()) {
							extra.println("Item improved.");
							Player.player.loseGold(mcost);
							Player.bag.addAether(-acost);
							item.levelUp();
						}
						return false;
					}});
				list.add(new MenuBack("leave"));
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
	
	private static int askSlot() {
		extra.println("1 head");
		extra.println("2 arms");
		extra.println("3 chest");
		extra.println("4 legs");
		extra.println("5 feet");
		extra.println("6 weapon");
		extra.println("9 cancel");
		return extra.inInt(6,true,true);
	}

}
