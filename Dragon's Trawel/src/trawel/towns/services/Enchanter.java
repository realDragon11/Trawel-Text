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

public class Enchanter extends Feature {
	
	private static final long serialVersionUID = 1L;
	private Store store;
	
	public Enchanter(String name,int tier, Store s){
		this.name = name;
		this.tier = tier;
		this.store = s;
		tutorialText = "Enchanter";
		area_type = Area.MISC_SERVICE;
	}
	
	public Enchanter(int tier, Store s){
		this.tier = tier;
		this.store = s;
		name = store.getName() +" " + extra.choose("Enchanter");
		tutorialText = "Enchanter";
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
				double enchantMult = 1.5f*getUnEffectiveLevel();
				int maxLevel = tier+1;
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Enchant equipment up to +" + maxLevel +" level.";
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
						float enchantMult = item.getEnchantMult();
						if (enchantMult == 0) {
							extra.println("This item is not enchantable.");
							return false;
						}
						if (item.getLevel() >= maxLevel) {
							extra.println("This item is too high in level to enchant here!");
							return false;
						}
						//quality tier is typically 0 to 12
						int quality = extra.clamp(item.getQualityTier(),3,7);
						int mcost = Math.round(quality*(IEffectiveLevel.unclean(item.getLevel()+1)));
						int acost = (int) ((quality * 100f)*(IEffectiveLevel.unclean(item.getLevel()+1)));
						String costString = World.currentMoneyDisplay(mcost) + " and " +acost + " aether";
						
						//>=.5 enchant mult = 100% chance
						int successRate = Math.min(100,(int)(enchantMult*200));
						
						if (Player.player.getGold() < mcost) {
							if (Player.bag.getAether() < acost) {
								extra.println("You can't afford to enchant '"+item.getName()+"'. ("+costString+")");
							}else {
								extra.println("You can't afford to pay the enchanter to enchant '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Player.bag.getAether() < acost) {
							extra.println("You don't have enough aether to enchant '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						extra.println("Enchant your item with a "+successRate+"% success chance for "+costString+"? ("+World.currentMoneyString()+" will only be taken on success.)");
						if (item.getEnchant() != null) {
							extra.println(item.getName() +" is already enchanted, and the enchanter will still take their payment if a worse enchantment is rejected.");
						}
						if (extra.yesNo()) {
							if (successRate < 100 && extra.chanceIn(successRate,100)) {
								extra.println("The enchantment failed and your "+World.currentMoneyString()+" was returned. You lost "+acost+" aether.");
								Player.bag.addAether(-acost);
								return false;
							}
							boolean didChange = item.improveEnchantChance(item.getLevel());
							if (didChange) {
								extra.println("Item enchanted: " + item.getName());
							}else {
								extra.println("Item unchanged: " + item.getName());
							}
							Player.player.loseGold(mcost);
							Player.bag.addAether(-acost);
						}
						return false;
					}});
				list.add(new MenuBack("Leave."));
				return list;
			}});
	}

	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
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
