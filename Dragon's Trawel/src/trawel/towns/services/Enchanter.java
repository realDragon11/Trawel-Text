package trawel.towns.services;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
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
		return extra.F_SERVICE_MAGIC;
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
						return extra.SERVICE_BOTH_PAYMENT+"Enchant equipment up to +" + maxLevel +" level.";
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
							extra.println(extra.RESULT_ERROR+"This item is not enchantable.");
							return false;
						}
						if (item.getLevel() >= maxLevel) {
							extra.println(extra.RESULT_ERROR+"This item is too high in level to enchant here!");
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
								extra.println(extra.RESULT_ERROR+"You can't afford to enchant '"+item.getName()+"'. ("+costString+")");
							}else {
								extra.println(extra.RESULT_ERROR+"You can't afford to pay the enchanter to enchant '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Player.bag.getAether() < acost) {
							extra.println(extra.RESULT_ERROR+"You don't have enough aether to enchant '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						extra.println("Enchant "+item.getName()+" with a "+successRate+"% success chance for "+costString+"? ("+World.currentMoneyString()+" will only be taken on success.)");
						boolean wasEnchanted = false;
						if (item.getEnchant() != null) {
							extra.println(item.getName()+extra.RESULT_WARN+" is already enchanted, and the enchanter will still take their payment if a worse enchantment is rejected.");
							wasEnchanted = true;
						}
						if (extra.yesNo()) {
							if (successRate < 100 && extra.chanceIn(successRate,100)) {
								extra.println(extra.RESULT_FAIL+"The enchantment failed and your "+World.currentMoneyString()+" was returned. You lost "+acost+" aether.");
								Player.bag.addAether(-acost);
								return false;
							}
							boolean didChange = item.improveEnchantChance(item.getLevel());
							if (didChange) {
								extra.println(extra.RESULT_PASS+"Item enchanted: " + item.getName());
								item.display(1);
							}else {
								extra.println(extra.RESULT_FAIL+"Item unchanged: " + item.getName());
								if (wasEnchanted == true) {
									extra.println("The new enchantment was considered worse than the old one, so it was not completed.");
								}
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
						return extra.SERVICE_AETHER+"Sell Aether ("+perAether+" for "+World.currentMoneyDisplay(sellAmount)+")";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "You have " + World.currentMoneyDisplay(Player.player.getGold()) + " and "+Player.bag.getAether()+ " aether.";
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return (sellAmount) +" for " + (perAether) + " Aether.";
									}

									@Override
									public boolean go() {
										sellAether(1,perAether);
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return (sellAmount*10) +" for " + (perAether*10) + " Aether.";
									}

									@Override
									public boolean go() {
										sellAether(10,perAether);
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return (sellAmount*50) +" for " + (perAether*50) + " Aether.";
									}

									@Override
									public boolean go() {
										sellAether(50,perAether);
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return (sellAmount*500) +" for " + (perAether*500) + " Aether.";
									}

									@Override
									public boolean go() {
										sellAether(500,perAether);
										return false;
									}});
								list.add(new MenuBack("Cancel."));
								return list;
							}});
						return false;
					}});
				list.add(new MenuBack("Leave."));
				return list;
			}});
	}
	
	private boolean sellAether(int amountMult, int perAether) {
		int aether = amountMult*perAether;
		int gold = sellAmount*amountMult;
		if (Player.bag.getAether() < perAether) {
			extra.println(extra.RESULT_FAIL+"You do not have enough aether to trade in. ("+Player.bag.getAether()+" of "+aether+")");
			return false;
		}
		Player.bag.addAether(-aether);
		Player.bag.addGold(gold);
		extra.println("Gained " + World.currentMoneyDisplay(gold)+", sold "+aether +" Aether.");
		return true;
	}

	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		return null;
	}

}
