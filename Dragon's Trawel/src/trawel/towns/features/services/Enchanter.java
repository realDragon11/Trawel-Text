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
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Skill;
import trawel.personal.item.Item;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.World;
import trawel.towns.data.FeatureData;
import trawel.towns.data.FeatureData.FeatureTutorialCategory;
import trawel.towns.features.Feature;

public class Enchanter extends Feature {
	
	static {
		FeatureData.registerFeature(Enchanter.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" "+TrawelColor.SERVICE_CURRENCY+"apply"+TrawelColor.COLOR_RESET+" enchantments to equipment."
			+ (Player.isGameMode_NoPunishments() ? "" : 
				" "+fancyNamePlural()+" also "+TrawelColor.SERVICE_CURRENCY+"lift "+TrawelColor.COLOR_RESET+Effect.CURSE.getName()+".")
			);
			}
			
			@Override
			public int priority() {
				return 15;
			}
			
			@Override
			public String name() {
				return "Enchanter";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_SERVICE_MAGIC;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.VITAL_SERVICES;
			}
		});
	}
	
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
		aetherLerp = Rand.randFloat()/3f;
		sellAmount = Rand.randRange(3, 6);
	}
	
	@Override
	public String nameOfType() {
		return "Enchanter";
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
				int maxLevel = tier+1;
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_BOTH_PAYMENT+"Enchant equipment up to +" + maxLevel +" level.";
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
							Print.println(TrawelColor.RESULT_ERROR+"This item is not enchantable. ("+item.getName()+")");
							return false;
						}
						if (item.getLevel() >= maxLevel) {
							Print.println(TrawelColor.RESULT_ERROR+"This item is too high in level to enchant here! ("+item.getName()+")");
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
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford to enchant '"+item.getName()+"'. ("+costString+")");
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford to pay the enchanter to enchant '"+item.getName()+"'. ("+costString+")");
							}
							return false;
						}
						if (Player.bag.getAether() < acost) {
							Print.println(TrawelColor.RESULT_ERROR+"You don't have enough aether to enchant '"+item.getName()+"'. ("+costString+")");
							return false;
						}
						Print.println("Enchant "+item.getName()+" with a "+successRate+"% success chance for "+costString+"? ("+World.currentMoneyString()+" will only be taken on success.)");
						boolean wasEnchanted = false;
						if (item.getEnchant() != null) {
							Print.println(item.getName()+TrawelColor.RESULT_WARN+" is already enchanted, and the enchanter will still take their payment if a worse enchantment is rejected.");
							wasEnchanted = true;
						}
						if (Input.yesNo()) {
							if (successRate < 100 && Rand.chanceIn(successRate,100)) {
								Print.println(TrawelColor.RESULT_FAIL+"The enchantment failed and your "+World.currentMoneyString()+" was returned. You lost "+acost+" aether.");
								Player.bag.addAether(-acost);
								return false;
							}
							boolean didChange = item.improveEnchantChance(item.getLevel());
							if (didChange) {
								Print.println(TrawelColor.RESULT_PASS+"Item enchanted: " + item.getName());
								item.display(1);
								Networking.unlockAchievement("enchant1");
							}else {
								Print.println(TrawelColor.RESULT_FAIL+"Item unchanged: " + item.getName());
								if (wasEnchanted == true) {
									Print.println("The new enchantment was considered worse than the old one, so it was not completed.");
								}
							}
							Player.player.loseGold(mcost);
							Player.bag.addAether(-acost);
						}
						return false;
					}});
				if (Player.player.getPerson().hasSkill(Skill.RUNESMITH)) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_BOTH_PAYMENT+"Apply On-Hit Runes up to +" + maxLevel +" level.";
						}

						@Override
						public boolean go() {
							Weapon item = Player.bag.getHand();
							float enchantMult = item.getEnchantMult();
							if (enchantMult == 0) {
								Print.println(TrawelColor.RESULT_ERROR+"This item is not enchantable and thus cannot be runed. ("+item.getName()+")");
								return false;
							}
							if (item.getLevel() >= maxLevel) {
								Print.println(TrawelColor.RESULT_ERROR+"This item is too high in level to rune here! ("+item.getName()+")");
								return false;
							}
							//quality tier is typically 0 to 12
							int quality = extra.clamp(item.getQualityTier(),3,7);
							int mcost = Math.round(quality*(IEffectiveLevel.unclean(item.getLevel()+1)));
							int acost = (int) ((quality * 100f)*(IEffectiveLevel.unclean(item.getLevel()+1)));
							String costString = World.currentMoneyDisplay(mcost) + " and " +acost + " aether";
							
							//rune onhit enchanting can't fail as long as enchantMult > 0
							
							if (Player.player.getGold() < mcost) {
								if (Player.bag.getAether() < acost) {
									Print.println(TrawelColor.RESULT_ERROR+"You can't afford to rune '"+item.getName()+"'. ("+costString+")");
								}else {
									Print.println(TrawelColor.RESULT_ERROR+"You can't afford to pay the enchanter to rune '"+item.getName()+"'. ("+costString+")");
								}
								return false;
							}
							if (Player.bag.getAether() < acost) {
								Print.println(TrawelColor.RESULT_ERROR+"You don't have enough aether to enchant '"+item.getName()+"'. ("+costString+")");
								return false;
							}
							Print.println("Apply Runes to "+item.getName()+" for "+costString+"?");
							if (item.getEnchant() != null) {
								Print.println(item.getName()+TrawelColor.RESULT_WARN+" is already enchanted, and the new enchantment will replace the old one.");
							}
							if (Input.yesNo()) {
								//forces new enchantment in case they want a different type
								item.forceEnchantHitElemental();
								Print.println(TrawelColor.RESULT_PASS+"Item enchanted: " + item.getName());
								item.display(1);
								Player.player.loseGold(mcost);
								Player.bag.addAether(-acost);
								Networking.unlockAchievement("enchant1");
							}
							return false;
						}});
				}
				
				if (Player.player.getPerson().hasEffect(Effect.CURSE)) {
					//reduced cost from doctor since it only cures curse
					//should increase to doctor if add more effects it can cure
					int cureCost = Math.round(getUnEffectiveLevel());//*(1+Player.player.getPerson().punishmentSize())
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Lift Curse ("+World.currentMoneyDisplay(cureCost)+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() < cureCost) {
								Print.println(TrawelColor.RESULT_ERROR+"Not enough "+World.currentMoneyString()+"!");
								return false;
							}
							Print.println(TrawelColor.SERVICE_CURRENCY+"Pay to lift your curse?");
							if (Input.yesNo()) {
								Player.addTime(.5);//treatment time
								TrawelTime.globalPassTime();
								Player.player.addGold(-cureCost);
								Print.println(TrawelColor.RESULT_PASS+"You pay and receive treatment.");
								Player.player.getPerson().magicEffects();
							}
							return false;
						}});
				}
				
				/*
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
					}});*/
				list.add(new MenuBack("Leave."));
				return list;
			}});
	}
	
	private boolean sellAether(int amountMult, int perAether) {
		int aether = amountMult*perAether;
		int gold = sellAmount*amountMult;
		if (Player.bag.getAether() < perAether) {
			Print.println(TrawelColor.RESULT_FAIL+"You do not have enough aether to trade in. ("+Player.bag.getAether()+" of "+aether+")");
			return false;
		}
		Player.bag.addAether(-aether);
		Player.bag.addGold(gold);
		Print.println("Gained " + World.currentMoneyDisplay(gold)+", sold "+aether +" Aether.");
		return true;
	}

	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		return null;
	}

}
