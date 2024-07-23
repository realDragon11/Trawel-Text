package trawel.towns.features.services.guilds;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.factions.FBox;
import trawel.factions.FBox.FSub;
import trawel.helper.constants.TrawelColor;
import trawel.factions.Faction;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.locations.QBMenuItem;
import trawel.quests.locations.QRMenuItem;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.quests.types.FetchSideQuest;
import trawel.quests.types.KillSideQuest;
import trawel.quests.types.Quest;
import trawel.quests.types.FetchSideQuest.FetchType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.World;
import trawel.towns.features.Feature;

public class RogueGuild extends Feature implements QuestBoardLocation{
	
	private static final long serialVersionUID = 1L;
	
	private double activityTimer;
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private boolean canQuest = true;
	private int credits = 0;
	
	public RogueGuild(String _name, int _tier){
		name = _name;
		tier = _tier;
		activityTimer = 24f+Rand.randFloat()*24f;
	}
	
	@Override
	public QRType getQRType() {
		return QRType.RGUILD;
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_GUILD;
	}
	
	@Override
	public String nameOfFeature() {
		return "Rogue Guild";
	}
	
	@Override
	public String nameOfType() {
		return "Rogue Guild";
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
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.ROGUE);
						return "Current Rogue Reputation: " + (sub == null ? "Unknown" : ""+Print.format2(sub.forFac-sub.againstFac));
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Launder Gems.";
					}

					@Override
					public boolean go() {
						gemLaunder();
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_QUEST+"Job Board (Sidequests).";
					}

					@Override
					public boolean go() {
						Input.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								
								for (Quest q: sideQuests) {
									list.add(new QBMenuItem(q,RogueGuild.this));
								}
								for (QuestR qr: Player.player.QRFor(RogueGuild.this)) {
									list.add(new QRMenuItem(qr));
								}
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});
				int gemAmount = Math.round(1.5f*IEffectiveLevel.unclean(tier));
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Request Sapphires.";
					}

					@Override
					public boolean go() {
						while (true) {
							int cost = 25*gemAmount;
							float spenda = FBox.getSpendableFor(Faction.ROGUE);
							Print.println("Request "+gemAmount+" "+(gemAmount == 1 ? Gem.SAPPHIRE.name : Gem.SAPPHIRE.plural)+"? cost: " +cost + "/"+Print.format2(spenda));
							if (Input.yesNo()) {
								if (cost <= spenda) {
									Player.player.factionSpent.addFactionRep(Faction.ROGUE,cost,0);
									Gem.SAPPHIRE.changeGem(gemAmount);
									Print.println(TrawelColor.RESULT_PASS+"Gained "+gemAmount+" "+(gemAmount == 1 ? Gem.SAPPHIRE.name : Gem.SAPPHIRE.plural)+", new total: " + Gem.SAPPHIRE.getGem()+".");
								}else {
									Print.println(TrawelColor.RESULT_ERROR+"You do not have enough spendable reputation.");
									break;
								}
							}else {
								break;
							}
						}
						return false;
					}
				});
				if (Gem.SAPPHIRE.knowsGem() && Gem.SAPPHIRE.getGem() >= gemAmount) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Donate "+gemAmount+" "+(gemAmount == 1 ? Gem.SAPPHIRE.name : Gem.SAPPHIRE.plural)+". (Have "+Gem.SAPPHIRE.getGem()+")";
						}

						@Override
						public boolean go() {
							while (true) {
								int reward = 8;
								Print.println("Donate "+gemAmount+" "+(gemAmount == 1 ? Gem.SAPPHIRE.name : Gem.SAPPHIRE.plural)+"? You have " + Gem.SAPPHIRE.getGem());
								if (Input.yesNo()) {
									if (Gem.SAPPHIRE.getGem() >=gemAmount) {
										float gain = reward*gemAmount;
										Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,gain,0);
										Gem.SAPPHIRE.changeGem(-gemAmount);
										Print.println(TrawelColor.RESULT_PASS+"You gained "+Print.F_TWO_TRAILING.format(gain) + " reputation.");
									}else {
										Print.println(TrawelColor.RESULT_ERROR+"You do not have any sapphires.");
										break;
									}
								}else {
									break;
								}
							}
							return false;
						}
					});
				}
				mList.add(new MenuBack("Leave."));
				return mList;
			}});
	}
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (canQuest) {
			activityTimer-=time;
			if (activityTimer <= 0) {
				activityTimer+=12f+(36f*Rand.randFloat());
				generateSideQuest();
			}
		}
		return null;
	}
	
	public void gemLaunder() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						FSub sub = Player.player.getPerson().facRep.getFacRep(Faction.ROGUE);
						return "Current Reputation: " + (sub == null ? "Unknown" : ""+Print.format2(sub.forFac-sub.againstFac));
					}
				});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Current Money: " + Player.showGold();
					}
				});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Current Gems: " + Gem.playerGems();
					}
				});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Conversion Credits: " + credits;
					}
				});
				int cost = Math.round(IEffectiveLevel.unclean(tier)*6);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Buy a launder credit for "+cost+" " + World.currentMoneyString()+".";
					}

					@Override
					public boolean go() {
						while (true) {
						
						Print.println("Buy a launder credit? cost: " +cost + "/"+Player.player.getGold());
						if (Input.yesNo()) {
							if (cost <= Player.player.getGold()) {
								Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0.2f,0);
								credits++;
								Player.player.addGold(-cost);
								Print.println(TrawelColor.RESULT_PASS+"You gain 1 credit.");
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You cannnot afford a credit.");
								break;
							}
						}else {
							break;
						}
						}
						return false;
					}
				});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Request a launder credit for 2 Rogue Rep.";
					}

					@Override
					public boolean go() {
						int cost = Math.round(IEffectiveLevel.unclean(tier)*6);
						while (true) {
							float spenda = FBox.getSpendableFor(Faction.ROGUE);
							Print.println("Request a launder credit? cost: " +cost + " of "+Print.format2(spenda));
							if (Input.yesNo()) {
								if (cost <= spenda) {
									Player.player.factionSpent.addFactionRep(Faction.ROGUE,cost,0);
									credits++;
									Print.println(TrawelColor.RESULT_PASS+"You gain 1 credit.");
								}
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You do not have enough spendable reputation.");
								break;
							}
						}
						return false;
					}
				});
				final List<Gem> known = Gem.knownGems();
				if (known.size() < 2) {
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return TrawelColor.RESULT_ERROR+"Not enough Gems scouted.";
						}});
				}else {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Select Gems Target.";
						}

						@Override
						public boolean go() {
							
							Input.menuGo(new ScrollMenuGenerator(known.size(),"last <> gems","next <> gems") {

								@Override
								public List<MenuItem> forSlot(int i) {
									return Collections.singletonList(new MenuSelect() {
										@Override
										public String title() {
											return known.get(i).plural;
										}
										@Override
										public boolean go() {
											List<Gem> others = new ArrayList<Gem>();
											others.addAll(known);
											others.remove(known.get(i));
											Input.menuGo(new ScrollMenuGenerator(others.size(),"last <> gems","next <> gems") {

												@Override
												public List<MenuItem> forSlot(int i) {
													return Collections.singletonList(new LaunderGem(known.get(i),others.get(i)));
												}

												@Override
												public List<MenuItem> header() {
													List<MenuItem> list = new ArrayList<MenuItem>();
													list.add(new MenuLine() {
														@Override
														public String title() {
															return "Credits: " + credits;
														}});
													list.add(new MenuLine() {
														@Override
														public String title() {
															return "Current Gems: " + Gem.playerGems();
														}});
													return list;
												}

												@Override
												public List<MenuItem> footer() {
													return Collections.singletonList(new MenuBack());
												}});
											return false;
										}}
									);
								}

								@Override
								public List<MenuItem> header() {
									List<MenuItem> list = new ArrayList<MenuItem>();
									list.add(new MenuLine() {
										@Override
										public String title() {
											return "Credits: " + credits;
										}});
									list.add(new MenuLine() {
										@Override
										public String title() {
											return "Current Gems: " + Gem.playerGems();
										}});
									return list;
								}

								@Override
								public List<MenuItem> footer() {
									return Collections.singletonList(new MenuBack());
								}
							});
							return false;
						}});
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	private class LaunderGem implements MenuItem{

		private Gem from, to;
		private final int fromInt, toInt;
		
		public LaunderGem(Gem _from, Gem _to) {
			from = _from;
			to = _to;
			fromInt = Math.round(from.unitSize*IEffectiveLevel.unclean(tier));
			toInt = Math.round(to.unitSize*IEffectiveLevel.unclean(tier));
		}
		
		@Override
		public String title() {
			return "Launder "+from.unitSize + " " +from.plural +" ("+from.getGem()+") to " + toInt + " " + to.plural + "("+to.getGem()+")" + (credits == 0 ? " NO CREDIT" : "");
		}

		@Override
		public boolean go() {
			if (credits == 0) {
				Print.println(TrawelColor.RESULT_ERROR+"You have no credits.");
				return false;
			}
			if (from.getGem() >= fromInt) {
				Print.println(TrawelColor.RESULT_PASS+"You trade "+fromInt+" " + from.plural + " for " + toInt + to.plural+".");
				from.changeGem(-fromInt);
				to.changeGem(toInt);
				credits--;
			}else {
				Print.println(TrawelColor.RESULT_ERROR+"You need " + (fromInt-from.getGem()) + " more " + from.plural+"!");
			}
			return false;
		}

		@Override
		public boolean canClick() {
			return true;//credits > 0 && from.getGem() > 0;
		}

		@Override
		public boolean forceLast() {
			return false;
		}
		
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void generateSideQuest() {
		if (sideQuests.size() >= 3) {
			sideQuests.remove(Rand.randList(sideQuests));
		}
		switch (Rand.randRange(1,2)) {
		case 1:
			if (Rand.randFloat() > .8f) {//20% chance for a merchant quest instead
				sideQuests.add(FetchSideQuest.generate(this,FetchType.MERCHANT));
				break;
			}
			sideQuests.add(FetchSideQuest.generate(this,FetchType.CRIME));
			break;
		case 2:
			sideQuests.add(KillSideQuest.generate(this,Rand.randFloat() > .1f));//90% chance to be a murder quest
			break;
		}
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}

}
