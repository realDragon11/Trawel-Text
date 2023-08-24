package trawel.personal.people;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.AIClass;
import trawel.Effect;
import trawel.Networking;
import trawel.Story;
import trawel.WorldGen;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.earts.EArt;
import trawel.earts.EArtBox;
import trawel.factions.FBox;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.Item.ItemType;
import trawel.personal.item.Potion;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Agent.AgentGoal;
import trawel.quests.BasicSideQuest;
import trawel.quests.Quest;
import trawel.quests.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Calender;
import trawel.towns.Connection;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;
import trawel.towns.misc.Docks;

public class Player extends SuperPerson{

	private static final long serialVersionUID = 1L;
	private Person person;
	private boolean isAlive;
	public static Player player;
	public static double passTime;
	public static Inventory bag;
	public static transient String lastAttackStringer;
	public static boolean isPlaying = true;
	public int wins = 0;
	public boolean cheating = false;
	/**
	 * the instance copy of the player's world
	 */
	private World world;
	private String animalName;
	public int merchantLevel = 1;
	public Town lastTown = null;
	private double merchantPoints = 0;
	public int emeralds = 0, rubies = 0, sapphires = 0;
	public float forceRelation = 0.0f;
	public int forceRewardCount = 0;
	public int merchantBookPasses = 0;
	
	public double globalFindTime = 0;
	
	public short beer;

	
	public int knowledgeFragments = 0, fragmentReq = 5;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	//public List<EArt> eArts = new ArrayList<EArt>();
	//public EArtBox eaBox = new EArtBox();
	public boolean hasCult = false;
	
	public double townEventTimer = 10;
	
	public FBox factionSpent = new FBox();
	public int launderCredits = 0;
	public float hSpentOnKno = 0f;
	
	public Story storyHold;
	
	private boolean caresAboutCapacity = true, caresAboutAMP = true;
	
	private List<Item> pouch = new ArrayList<Item>();
	public int lastNode;
	public int currentNode;
	public Feature atFeature;
	public boolean forceGoProtection;
	
	public Player(Person p) {
		person = p;
		flask = null;
		isAlive = true;
		player = this;
		bag = p.getBag();
		passTime = 0;
		animalName = randomLists.randomAnimal();
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
		
		p.setSuper(this);
	}
	public static World getPlayerWorld() {
		if (Player.player == null) {
			return WorldGen.fallBackWorld;
		}
		return Player.player.world;
	}
	/**
	 * can only be called on main thread
	 * @param world
	 */
	public static void updateWorld(World world) {
		if (world != player.getWorld()) {
			Player.player.world = world;
			world.setVisited();
			extra.mainThreadDataUpdate();
		}
	}
	@Override
	public Person getPerson() {
		return person;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void kill() {
		isAlive = false;
	}
	public static double popTime() {
		double temp = passTime;
		passTime = 0;
		Player.player.townEventTimer-=temp;
		return temp;
	}
	public static void addTime(double addTime) {
		passTime +=addTime;
		Player.player.globalFindTime+=addTime;
	}
	
	@Override
	public List<TimeEvent> passTime(double d, TimeContext caller) {
		return null;
	}
	
	public static void addXp(int amount) {
		player.getPerson().addXp(amount);
		}
	
	public static boolean hasSkill(Skill skill) {
		return player.getPerson().hasSkill(skill);
	}
	public String animalName() {
		return animalName;
	}
	
	/*
	public static void toggleTutorial() {
		player.tutorial = !player.tutorial;
	}*/
	
	public static boolean getTutorial() {
		if (player == null) {return false;}
		return mainGame.doTutorial;
	}

	public void addMPoints(double mValue) {
		merchantPoints+=mValue;
		if (merchantPoints >= merchantLevel*merchantLevel) {
			merchantPoints-=merchantLevel*merchantLevel;
			merchantLevel++;
			addMPoints(0);
		}
	}
	
	@Override
	public Effect doSip() {
		if (flask != null) {
			if (knowsFlask) {
				extra.println("Take a sip of your "+flask.effect.getName()+" potion? ("+flask.sips+" left)");
			}else {
				extra.println("Take a sip of your potion? ("+flask.sips+" left)");
			}
			if (extra.yesNo()) {
				knowsFlask = true;
				Effect e = flask.effect;
				flask.sip(person);
				Networking.sendStrong("PlayDelay|sound_swallow"+extra.randRange(1,5)+"|1|");
				Networking.unlockAchievement("potion1");
				if (flask.sips <=0) {
					flask = null;
				}
				return e;
			}
		}
		return null;
	}
	public Potion getFlask() {
		return flask;
	}
	public void showQuests() {
		
		
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new ExamineQuest(q));
				}
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	public class AbandonQuest extends MenuSelect {
		Quest quest;
		public AbandonQuest(Quest q) {
			quest = q;
		}
		@Override
		public String title() {
			return "Abandon";
		}

		@Override
		public boolean go() {
			quest.fail();
			sideQuests.remove(quest);
			return true;
		}
		
	}
	public class ExamineQuest extends MenuSelect {
		Quest quest;
		public ExamineQuest(Quest q) {
			quest = q;
		}
		@Override
		public String title() {
			return quest.name();
		}

		@Override
		public boolean go() {
			extra.println(quest.name() + ":");
			extra.println(quest.desc());
			
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add(new AbandonQuest(quest));
					mList.add(new MenuBack());
					return mList;
				}
				
			});
			return false;
		}
		
	}
	/*
	public void addEArt(EArt earta) {
		extra.println("You have chosen an Exotic Art. You may now spend skillpoints on it from the skill menu. You can have a max of 2.");
		this.eArts.add(earta);
		switch (earta) {
		case ARCANIST:
			break;
		case EXECUTIONER:
			break;
		
		}
		
	}*/
	public void questTrigger(TriggerType type, String string, int count) {
		for (Quest q: sideQuests) {
			q.questTrigger(type,string, count);
		}
		
	}
	
	/**
	 * do not use if not needed
	 * @param type
	 * @param string
	 * @return
	 */
	public BasicSideQuest anyQTrigger(TriggerType type, String string) {
		for (Quest q: sideQuests) {
			if (q instanceof BasicSideQuest) {
				if (((BasicSideQuest) q).trigger == string) {
					return ((BasicSideQuest) q);
				}
			}
		}
		return null;
	}
	
	public List<String> allQTriggers() {
		List<String> ts = new ArrayList<String>();
		for (Quest q: sideQuests) {
			ts.addAll(q.triggers());
		}
		return ts;
	}
	
	public boolean hasTrigger(String string) {
		for (Quest q: sideQuests) {
			if (q.triggers().contains(string)) {
				return true;
			}
		}
		return false;
	}
	
	public void addKnowFrag() {
		if (++this.knowledgeFragments >= this.fragmentReq) {
			knowledgeFragments-=fragmentReq;
			Player.player.getPerson().addFeatPoint();
			fragmentReq+=2;
			extra.println("Your knowledge has gained you a feat point!");
		}
	}
	
	public String strKnowFrag() {
		return (Player.player.knowledgeFragments + " of "+Player.player.fragmentReq + " to next knowledge level.");
	}

	
	/**
	 * how much aether converts into normal money
	 */
	public static final float PURE_AETHER_RATE = .02f;
	/**
	 * how much aether converts into normal money at shops
	 * <br>
	 * ie how much they charge for using aether instead of real money
	 */
	public static final float NORMAL_AETHER_RATE = PURE_AETHER_RATE*.75f;
	/**
	 * a multiplier on how much items are worth, but only applies to their trade value, not aether value
	 * <br>
	 * this effectively makes having the money itself better when buying
	 */
	public static final float TRADE_VALUE_BONUS = 4f;
	
	public static String showGold() {
		int i = Player.player.getGold();
		return player.getWorld().moneyString(i);
	}
	
	public static String loseGold(int amount,boolean commentBroke) {
		int lost = Player.player.loseGold(amount);
		if (lost == 0) {
			return "";//no change
		}
		if (lost == -1) {
			return commentBroke ? "Seems like you didn't have anything they wanted to take!" : "" ;
		}
		if (lost < amount) {//now broke
			return "They took all your " + World.currentMoneyString() +"! (lost "+lost+")";
		}else {
			return World.currentMoneyDisplay(lost) + " was stolen!";
		}
	}
	
	/**
	 * for now this is a combattwo, but if you want to check victory conditions you should use the 'playerwon' function
	 * <br>
	 *
	public static Combat playerFightWith(Person p) {
		return mainGame.CombatTwo(Player.player.getPerson(),p, Player.getWorld());
	}*/
	public void setCheating() {
		cheating = true;
	}
	public boolean getCheating() {
		return cheating;
	}
	@Override
	public void setGoal(AgentGoal none) {
		throw new RuntimeException("player cannot take agent goals");
	}
	@Override
	public void onlyGoal(AgentGoal none) {
		throw new RuntimeException("player cannot take agent goals");
	}
	@Override
	public boolean removeGoal(AgentGoal none) {
		throw new RuntimeException("player cannot take agent goals");
	}
	@Override
	public boolean hasGoal(AgentGoal goal) {
		return false;
	}
	public double getFindTime() {
		return player.globalFindTime;
	}
	public void delayFind() {
		player.globalFindTime/=2;//half
		player.globalFindTime-=extra.randRange(10,30);//then minus
		//so it doesn't get really high forever
	}
	@Override
	public boolean everDeathCheated() {
		return true;//the player is the biggest deathcheater of them all
	}
	public static void placeAsOccupant(Person p) {
		Player.player.getWorld().addReoccuring(new Agent(p));
	}
	public boolean caresAboutCapacity() {
		return caresAboutCapacity;
	}
	public void setCaresAboutCapacity(boolean caresAboutCapacity) {
		this.caresAboutCapacity = caresAboutCapacity;
	}
	public boolean caresAboutAMP() {
		return caresAboutAMP;
	}
	public void setCaresAboutAMP(boolean caresAboutAMP) {
		this.caresAboutAMP = caresAboutAMP;
	}
	public static void unlockPerk(Perk perk) {
		Player.player.storyHold.perkTrigger(perk);
		Player.player.getPerson().setPerk(perk);
	}
	
	public void swapPouch(int slot) {
		Item was = pouch.get(slot);
		pouch.set(slot,Player.bag.swapItem(was));
		extra.println("You use the " + was.getName() + " and put the " + pouch.get(slot).getName() + " in your bag.");
	}
	
	public Item peekPouch(int slot) {
		return pouch.get(slot);
	}
	public Item popPouch(int slot) {
		return pouch.get(slot);
	}
	
	/**
	 * returns false if was not able to add
	 */
	public boolean addPouch(Item i) {
		if (pouch.size() < 3) {
			pouch.add(i);
			return true;
		}
		return false;
	}
	
	public boolean canAddPouch() {
		return pouch.size() < 3;
	}
	
	public Item retconLastPouch() {
		return pouch.remove(pouch.size()-1);
	}
	
	public class PouchMenuItem extends MenuSelect{
		
		private int slot;
		public PouchMenuItem(int i) {
			slot = i;
		}

		@Override
		public String title() {
			return "Compare against " + peekPouch(slot).getName();
		}

		@Override
		public boolean go() {
			swapPouch(slot);
			return false;
		}
		
	}
	
	public List<PouchMenuItem> getPouchesAgainst(Item against){
		List<PouchMenuItem> list = new ArrayList<PouchMenuItem>();
		for (int i = 0; i < pouch.size();i++) {
			Item e = pouch.get(i);
			if (against.getType() == ItemType.ARMOR) {
				if (e.getType() == ItemType.ARMOR) {
					if (((Armor)e).getSlot() == ((Armor)against).getSlot()) {
						list.add(new PouchMenuItem(i));
					}
				}
			}else {
				if (against.getType() == ItemType.WEAPON) {
					if (e.getType() == ItemType.WEAPON) {
						list.add(new PouchMenuItem(i));
					}
				}
			}
		}
		return list;
	}
	
	public void youMenu() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Display Options";
					}

					@Override
					public boolean go() {
						mainGame.advancedDisplayOptions();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Character";
					}

					@Override
					public boolean go() {
						Player.player.getPerson().playerSkillMenu();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Inventory";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> invList = new ArrayList<MenuItem>();
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Equipment Overview";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().getBag().deepDisplay();
										extra.println("You have " + Player.player.emeralds + " emeralds, " + Player.player.rubies +" rubies, and " + Player.player.sapphires +" sapphires.");
										return false;
									}});
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Extended Bag";
									}

									@Override
									public boolean go() {
										//TODO: add item aetherification on the spot
										extra.menuGo(new MenuGenerator() {

											@Override
											public List<MenuItem> gen() {
												List<MenuItem> extList = new ArrayList<MenuItem>();
												extList.add(new MenuLine() {

													@Override
													public String title() {
														return "Pouch Size Limit: 3 Items";
													}});
												for (int i = 0; i < pouch.size();i++) {
													final int slot = i;//love that this is truly final
													extList.add(new MenuSelect() {
														@Override
														public String title() {
															return "Use " +peekPouch(slot).getName();
														}

														@Override
														public boolean go() {
															Player.player.swapPouch(slot);
															return false;
														}});
												}
												extList.add(new MenuBack());
												return extList;
											}});
										return false;
									}});
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "DrawBanes Menu (Discard?)";
									}

									@Override
									public boolean go() {
										while (Player.player.getPerson().getBag().playerDiscardDrawBane() != null);
										return false;
									}});
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Map";
									}

									@Override
									public boolean go() {
										extra.println("You take out your personal map of known towns.");
										mapScrollMenu();
										return false;
									}});
								invList.add(new MenuBack());
								return invList;
							}});
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Society";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator() {

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> socList = new ArrayList<MenuItem>();
								socList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Quests";
									}

									@Override
									public boolean go() {
										Player.player.showQuests();
										return false;
									}});
								socList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Reputation";
									}

									@Override
									public boolean go() {
										Player.player.getPerson().facRep.display();
										return false;
									}});
								socList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Titles";
									}

									@Override
									public boolean go() {
										Player.player.displayTitles();
										return false;
									}});
								socList.add(new MenuBack());
								return socList;
							}});
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Save";
					}

					@Override
					public boolean go() {
						extra.println("Really save?");
						extra.println(extra.PRE_ORANGE+"SAVES ARE NOT COMPATIBLE ACROSS VERSIONS");
						if (extra.yesNo()) {
							extra.println("Save to which slot?");
							for (int i = 1; i < 9;i++) {
								extra.println(i+ " slot:"+WorldGen.checkNameInFile(""+i));
							}
							int in = extra.inInt(8);
							extra.println("Saving...");
							WorldGen.plane.prepareSave();
							WorldGen.save(in+"");
							
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Raw Stats";
					}

					@Override
					public boolean go() {
						Player.player.getPerson().displayStats(false);
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Quit to Main Menu";
					}

					@Override
					public boolean go() {
						extra.println("Really quit? Your progress might not be saved.");
						if (extra.yesNo()) {
							Player.isPlaying  = false;
							return true;
						}
						return false;
					}});
				if (Player.player.getCheating()) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Cheats";
						}

						@Override
						public boolean go() {
							extra.menuGo(new MenuGenerator() {

								@Override
								public List<MenuItem> gen() {
									List<MenuItem> hackList = new ArrayList<MenuItem>();
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Fast Powerlevel";
										}

										@Override
										public boolean go() {
											//where we're going, level choices are no longer things...
											Player.player.getPerson().setFlag(PersonFlag.AUTOLEVEL, true);
											Player.player.getPerson().addXp(99999);
											Weapon w = Player.bag.getHand();
											for (int i = 0; i < 50;i++) {
												w.levelUp();
											}
											return true;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "XP and Money";
										}

										@Override
										public boolean go() {
											Player.player.getPerson().addXp(9999);
											Player.bag.addAether(999999);
											Player.bag.addGold(999999);
											return true;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Reveal All Towns";
										}

										@Override
										public boolean go() {
											for (World w: WorldGen.plane.worlds()) {
												w.setVisited();
											}
											for (Town t: WorldGen.plane.getTowns()) {
												t.visited = Math.max(2,t.visited);
											}
											return true;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Queue One Year";
										}

										@Override
										public boolean go() {
											Player.addTime(365*24);
											return true;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Queue One Hundred Years";
										}

										@Override
										public boolean go() {
											if (!extra.yesNo()){
												return false;
											}
											Player.addTime(100*365*24);
											return true;
										}});
									hackList.add(new MenuBack());
									return hackList;
								}});
							return false;
						}});
				}
				list.add(new MenuBack("Exit Menu."));
				return list;
			}});
	}
	
	private void mapScrollMenu() {
		final List<World> vistedWorlds = new ArrayList<World>();
		WorldGen.plane.worlds().stream().filter(w -> w.hasVisited()).forEach(vistedWorlds::add);
		extra.menuGo(new ScrollMenuGenerator(vistedWorlds.size(),"prior <> worlds","next <> worlds") {
			
			@Override
			public List<MenuItem> header() {
				return null;
			}
			
			@Override
			public List<MenuItem> forSlot(int i) {
				return Collections.singletonList(new MenuSelect() {

					@Override
					public String title() {
						return vistedWorlds.get(i).getName();
					}

					@Override
					public boolean go() {
						final World w = vistedWorlds.get(i);
						final List<Town> visitedTowns = new ArrayList<Town>();
						w.getIslands().stream().forEach(is -> is.getTowns().stream().filter(t -> t.visited > 1).forEach(visitedTowns::add));
						if (w == WorldGen.lynchPin.getIsland().getWorld() && !visitedTowns.contains(WorldGen.lynchPin)) {
							//always have a path to unun if you've been to that world
							visitedTowns.add(WorldGen.lynchPin);
						}
						extra.menuGo(new ScrollMenuGenerator(visitedTowns.size(),"prior <> towns","next <> towns") {

							@Override
							public List<MenuItem> forSlot(int i) {
								return Collections.singletonList(new MenuSelect() {

									@Override
									public String title() {
										return visitedTowns.get(i).displayLine(Player.player.lastTown);
									}

									@Override
									public boolean go() {
										final Town t = visitedTowns.get(i);
										extra.menuGo(new MenuGenerator() {

											@Override
											public List<MenuItem> gen() {
												List<MenuItem> list = new ArrayList<MenuItem>();
												list.add(new MenuLine() {

													@Override
													public String title() {
														return t.getName()+": tier " +t.getTier();
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return "Compass";
													}

													@Override
													public boolean go() {
														WorldGen.pathToTown(t);
														return false;
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return "Lore";
													}

													@Override
													public boolean go() {
														 if (!w.getAndPrintLore(t.getName())) {
															 extra.println("There are no stories about " + t.getName()+".");
														 }
														return false;
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return "Details";
													}

													@Override
													public boolean go() {
														extra.println(t.getName() + " is a tier " +t.getTier() + " "
																+ (t.isFort() ? "fort" : "town") + " located on the Island of " +t.getIsland().getName()+", which has "+t.getIsland().getTowns().size()+" regions in it."
																);
														extra.println("It has around " +t.getFeatures().size() + " things to do and around " + t.getAllOccupants().size() + " occupants.");
														double[] loc = Calender.lerpLocation(t);
														extra.println("In "+w.getName() +", it is around "+ extra.F_WHOLE.format(loc[0]) +" latitude and "+extra.F_WHOLE.format(loc[1]) +" longitude. It is about " +w.getCalender().stringLocalTime(t) + " there.");
														if (t.tTags.size() > 0) {
															String tagFluff = "It is known for its ";
															if (t.tTags.size() == 2) {
																tagFluff += t.tTags.get(0).desc + " and " + t.tTags.get(1).desc;
															}else {
																for (int i = 0; i < t.tTags.size();i++) {
																	if (i == 0) {
																		tagFluff += t.tTags.get(i).desc;
																	}else {
																		if (i == t.tTags.size()-1) {
																			tagFluff += ", and " +t.tTags.get(i).desc;
																		}else {
																			tagFluff += ", " +t.tTags.get(i).desc;
																		}
																	}
																}
															}
															extra.println(tagFluff+".");
														}
														int road = 0;
														int ship = 0;
														int tele = 0;
														for (Connection c: t.getConnects()) {
															switch (c.getType()) {
															case ROAD:
																road++;
																break;
															case SHIP:
																ship++;
																break;
															case TELE:
																tele++;
																break;
															}
														}
														boolean hasDocks = false;
														for (Feature f: t.getFeatures()) {
															if (f instanceof Docks) {
																hasDocks = true;
															}
														}
														if (road > 0) {
															extra.println("Has " + road + " land routes.");
														}
														if (tele > 0) {
															extra.println("Has "+tele+" teleport rituals.");
														}
														if (hasDocks) {
															extra.println("Has Docks with " + ship + " direct routes.");
														}else {
															if (ship > 0) {
																extra.println("Has "+ship+" locations from their port.");
															}
														}
														extra.println();
														return false;
													}});
												list.add(new MenuBack());
												return list;
											}});
										return false;
									}
									
								});
							}

							@Override
							public List<MenuItem> header() {
								return null;
							}

							@Override
							public List<MenuItem> footer() {
								return Collections.singletonList(new MenuBack());
							}});
						return false;
					}});
			}
			
			@Override
			public List<MenuItem> footer() {
				return Collections.singletonList(new MenuBack("Cancel"));
			}
		});
	}
	
}
