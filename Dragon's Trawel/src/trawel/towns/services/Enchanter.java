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
	/**
	 * 0f = store normal rate
	 * 1f = perfect pure rate
	 */
	private float aetherLerp;
	/**
	 * how much world currency per transaction
	 */
	private int sellAmount;
	
	public Enchanter(String name,int tier){
		this.name = name;
		this.tier = tier;
		tutorialText = "Enchanter";
		area_type = Area.MISC_SERVICE;
		aetherLerp = extra.randFloat()/3f;
		sellAmount = extra.randRange(3, 6);
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
				int maxLevel = tier+1;
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Enchant equipment up to +" + maxLevel +" level.";
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
						extra.println("Enchant "+item.getName()+" with a "+successRate+"% success chance for "+costString+"? ("+World.currentMoneyString()+" will only be taken on success.)");
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
								item.display(2);
							}else {
								extra.println("Item unchanged: " + item.getName());
								extra.println("The new enchantment was considered worse than the old one, so it was not completed.");
							}
							Player.player.loseGold(mcost);
							Player.bag.addAether(-acost);
						}
						return false;
					}});
				float aetherRate = 1f/extra.lerp(Player.NORMAL_AETHER_RATE,Player.PURE_AETHER_RATE,aetherLerp);
				//how much aether per 5 world currency
				int perAether = Math.round(aetherRate*sellAmount);
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Sell Aether ("+perAether+" for "+World.currentMoneyDisplay(sellAmount)+")";
					}

					@Override
					public boolean go() {
						if (Player.bag.getAether() < perAether) {
							extra.println("You do not have enough aether to trade in. ("+Player.bag.getAether()+" of "+perAether+")");
						}
						extra.println("Sell "+ perAether+" of your "+ Player.bag.getAether()+" for " +World.currentMoneyDisplay(sellAmount)+"?");
						if (extra.yesNo()) {
							Player.bag.addAether(-perAether);
							Player.bag.addGold(sellAmount);
							extra.println("Gained " + World.currentMoneyDisplay(sellAmount)+".");
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

}