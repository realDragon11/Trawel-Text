package trawel.towns.features.services;
import java.util.ArrayList;
import java.util.List;

import derg.ds.Chomp;
import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.types.BasicSideQuest;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.Calender;
import trawel.towns.data.FeatureData;
import trawel.towns.features.Feature;

public class Library extends Feature {
	
	static {
		FeatureData.registerFeature(Library.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" have the materials needed to study Feat Fragments to turn them into Feat Points." +fancyNamePlural()+" also have rooms to reserve which can be rested in to cure "+Effect.BURNOUT.getName()+" and "+Effect.TIRED.getName()+".");
			}
			
			@Override
			public int priority() {
				return 0;
			}
			
			@Override
			public String name() {
				return "Library";
			}
			
			@Override
			public String namePlural() {
				return "Libraries";
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
	private byte libFlags = Chomp.emptyByte;
	private double reserveTime = 0d;
	
	public Library(String _name, Town _town) {
		name = _name;
		town = _town;
	}
	public enum LibraryFlag{
		HAS_BONUS_FEAT_PICKED
	}
	
	public boolean getLibFlag(LibraryFlag flag) {
		return Chomp.getEnumByteFlag(flag.ordinal(),libFlags);
	}
	
	public void setLibFlag(LibraryFlag flag, boolean bool) {
		libFlags = Chomp.setEnumByteFlag(flag.ordinal(),libFlags,bool);
	}
	
	@Override
	public String nameOfType() {
		return "Library";
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
				boolean playerOwns = owner == Player.player;
				if (reserveTime > 0 || playerOwns){
					list.add(new MenuSelect() {

						@Override
						public String title() {
							if (playerOwns) {
								return TrawelColor.SERVICE_FREE+"Your Reservation (Owned Library)";
							}
							//special payment is the time you already bought
							return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Your Reservation ("+Print.F_TWO_TRAILING.format(reserveTime)+" hours left)";
						}

						@Override
						public boolean go() {
							Input.menuGo(new MenuGenerator() {

								@Override
								public List<MenuItem> gen() {
									List<MenuItem> list = new ArrayList<MenuItem>();
									list.add(new MenuLine() {

										@Override
										public String title() {
											if (playerOwns) {
												return Calender.dateFull(town);
											}
											return Calender.dateFull(town)+": ("+Print.F_TWO_TRAILING.format(reserveTime)+" hours left)";
										}});
									if (reserveTime <= 24 && !playerOwns) {
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return (playerOwns ? TrawelColor.SERVICE_FREE : TrawelColor.SERVICE_SPECIAL_PAYMENT)+"Rest " + Print.F_TWO_TRAILING.format(reserveTime)+" hours.";
											}

											@Override
											public boolean go() {
												Player.addTime(reserveTime+.1);
												TrawelTime.globalPassTime();
												Player.player.getPerson().restEffects();
												Networking.unlockAchievement("tavern1");
												return true;
											}});
									}else {
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return (playerOwns ? TrawelColor.SERVICE_FREE : TrawelColor.SERVICE_SPECIAL_PAYMENT)+"Rest 24 hours.";
											}

											@Override
											public boolean go() {
												Player.addTime(24);
												TrawelTime.globalPassTime();
												Player.player.getPerson().restEffects();
												Networking.unlockAchievement("tavern1");
												return false;
											}});
										if (reserveTime > 72 || playerOwns) {
											list.add(new MenuSelect() {

												@Override
												public String title() {
													return (playerOwns ? TrawelColor.SERVICE_FREE : TrawelColor.SERVICE_SPECIAL_PAYMENT)+"Rest 3 days.";
												}

												@Override
												public boolean go() {
													Player.addTime(72);
													TrawelTime.globalPassTime();
													Player.player.getPerson().restEffects();
													Networking.unlockAchievement("tavern1");
													return false;
												}});
										}
									}
									if (reserveTime < 24*7 && !playerOwns) {//can rent for one week tops
										list.add(new MenuSelect() {

											@Override
											public String title() {
												return TrawelColor.SERVICE_CURRENCY+"Reserve more time.";
											}

											@Override
											public boolean go() {
												reserveRoom();
												return false;
											}});
									}
									list.add(new MenuBack());
									return list;
								}});
							return false;
						}});
				}else {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Reserve a Room";
						}

						@Override
						public boolean go() {
							reserveRoom();
							return false;
						}
					});
				}
				//doesn't let you hoard picks so you're more likely to find some later if you need to look around
				if (!getLibFlag(LibraryFlag.HAS_BONUS_FEAT_PICKED) && Player.player.getFeatPicks() < 3) {
					list.add(new MenuSelect(){

						@Override
						public String title() {
							return TrawelColor.SERVICE_FREE+"Study general lore. (Free Feat Pick)";
						}

						@Override
						public boolean go() {
							setLibFlag(LibraryFlag.HAS_BONUS_FEAT_PICKED,true);
							Player.player.addFeatPick(1);
							Print.println("You learn enough to reassess you future. You gained one feat pick as a result.");
							return false;
						}
						
					});
				}
				int frag_count = Player.player.currentKFrags;
				if (frag_count > 0) {
					list.add(new MenuSelect(){

						@Override
						public String title() {
							return TrawelColor.SERVICE_SPECIAL_PAYMENT+"Study feat fragments (have: "+frag_count+")";
						}

						@Override
						public boolean go() {
							Print.println("You study the "+frag_count+" scraps of knowledge you've accumulated.");
							for (int i = 0; i < frag_count;i++) {
								Player.player.addKnowFrag();
							}
							Player.player.currentKFrags = 0;
							Print.println(TrawelColor.RESULT_PASS+"You are now " + Player.player.strKnowFrag());
							DrawBane gain = BasicSideQuest.attemptCollectAlign(QKey.KNOW_ALIGN,frag_count*.5f,2*frag_count);
							if (gain != null) {
								Print.println(TrawelColor.RESULT_PASS+"You find "+(2*frag_count)+" " + gain.getName() + " pieces while studying!");
							}
							return false;
						}
						
					});
				}
				list.add(new MenuBack("Leave"));
				return list;
			}});
	}
	
	private void reserveRoom() {
		if (reserveTime < 0) {
			reserveTime = 0;
		}
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				int perDay = reservePrice();
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return Player.player.getGoldDisp();
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "One Day: "+World.currentMoneyDisplay(perDay);
					}

					@Override
					public boolean go() {
						if (Player.player.askBuyMoney(perDay, "one day")) {
							reserveTime+=24;
							return true;
						}
						return false;
					}
				});
				list.add(new MenuSelect() {//7/5 = 1.4

					@Override
					public String title() {
						return "One Week: "+World.currentMoneyDisplay(perDay*5);
					}

					@Override
					public boolean go() {
						if (Player.player.askBuyMoney(perDay*5, "seven days")){
							reserveTime+=168;
							return true;
						}
						return false;
					}
				});
				list.add(new MenuBack("Cancel."));
				return list;
			}});
	}

	//cheaper than inn, also no bath
	private int reservePrice() {
		return (int) Math.ceil(1.2f*getUnEffectiveLevel());
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		reserveTime-=time;
		return null;

	}

}
