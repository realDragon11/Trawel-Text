package trawel.personal.people;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.regex.Pattern;

import derg.ds.TwinListMap;
import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.arc.story.Story;
import trawel.arc.story.StoryNone;
import trawel.arc.story.StoryTutorial;
import trawel.battle.Combat;
import trawel.battle.attacks.Wound;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.SaveManager;
import trawel.core.mainGame;
import trawel.factions.FBox;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.Services;
import trawel.helper.methods.randomLists;
import trawel.personal.AIClass;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.Feat;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.classless.Skill.Type;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.Item.ItemType;
import trawel.personal.item.Potion;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Armor.ArmorQuality;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Gem;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponQual;
import trawel.personal.people.Agent.AgentGoal;
import trawel.quests.locations.QuestR;
import trawel.quests.types.Quest;
import trawel.quests.types.CleanseSideQuest.CleanseType;
import trawel.quests.types.Quest.TriggerType;
import trawel.threads.ThreadData;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Island;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.Calender;
import trawel.towns.data.Connection;
import trawel.towns.data.FeatureData;
import trawel.towns.data.WorldGen;
import trawel.towns.features.Feature;
import trawel.towns.features.misc.Docks;
import trawel.towns.features.misc.Altar.AltarForce;

public class Player extends SuperPerson{

	private static final long serialVersionUID = 1L;
	private Person person;
	private boolean isAlive;
	
	public static Armor[] aLootArmors = new Armor[5];
	public static Weapon aLootHand;
	public static int aLootAether, aLootLocal;
	public static Player player;
	public static double passTime;
	public static Inventory bag;
	public static transient String lastAttackStringer;
	public static boolean exitMenu = false;
	public static boolean isPlaying = false;
	public int duel_wins = 0;
	public int deaths;
	private boolean cheating = false;
	private boolean gameMode_NoPunishments = false;
	/**
	 * the instance copy of the player's world
	 */
	private World world;
	private String animalName;
	public int merchantLevel = 1;
	public Town lastTown = null;
	private double merchantPoints = 0;
	public EnumMap<Gem,Integer> gems = new EnumMap<>(Gem.class);
	
	private EnumMap<AltarForce,Integer> forceRewards = new EnumMap<>(AltarForce.class);
	private EnumMap<AltarForce,Float> forceRelations = new EnumMap<>(AltarForce.class);
	public int merchantBookPasses = 0;
	
	public double globalFindTime = 0;
	
	public short beer;

	
	public int currentKFrags = 0, knowledgeFragments = 0, fragmentReq = 5;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	//public List<EArt> eArts = new ArrayList<EArt>();
	//public EArtBox eaBox = new EArtBox();
	public boolean hasCult = false;
	
	public double townEventTimer = 10;
	
	public FBox factionSpent = new FBox();
	public float hSpentOnKno = 0f;
	
	private Story story;
	
	private boolean caresAboutCapacity = true, caresAboutAMP = true;
	
	private List<Item> pouch = new ArrayList<Item>();
	public int lastNode;
	public int currentNode;
	public Feature atFeature;
	public boolean forceGoProtection;
	/**
	 * used by QuestReactionFactory to lessen the odds of back-to-back QuestReactions
	 */
	public int roadGracePeriod = 0;
	
	private TwinListMap<Serializable,String> achieveMap = new TwinListMap<Serializable,String>();
	
	public Player() {
		flask = null;
		isAlive = true;
		player = this;
		passTime = 0;
		animalName = randomLists.randomAnimal();
		moneys = new ArrayList<Integer>();
		moneymappings = new ArrayList<World>();
	}
	
	public void setPerson(Person p) {
		person = p;
		bag = p.getBag();
		p.setSuper(this);
	}
	@Override
	public void setLocation(Town location) {
		//must call beforehand so the location is the old location with world
		Player.updateWorld(location.getIsland().getWorld());
		super.setLocation(location);
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
			//if new world, and had a previous world, set achievement
			if (!world.hasVisited() && Player.player.world != null) {
				Networking.unlockAchievement("worldtravel1");
			}
			Player.player.world = world;
			world.setVisited();
			ThreadData.mainThreadDataUpdate();
		}
	}
	
	public Story getStory() {
		return story;
	}
	
	public void setStory(Story _story) {
		story = _story;
	}
	
	@Override
	public void addAchieve(Serializable key, String title) {
		achieveMap.put(key, title);
		Networking.leaderboard("most_titles", achieveMap.size());
	}
	
	@Override
	public void addGroupedAchieve(Serializable key, String foreword, String instance) {
		String str = achieveMap.get(key);
		if (str == null) {
			achieveMap.put(key,foreword +": "+instance);
		}else {
			String[] others = str.split(Pattern.quote(": "))[1].split(", ");
			String build = foreword +": ";
			for (int i = 0; i < others.length;i++) {
				build += others[i] +", ";
			}
			//since we add to end, we don't need to deal with any fancy knowing when to not add a comma logic
			build += instance;
			achieveMap.put(key,build);
		}
		Networking.leaderboard("most_titles", achieveMap.size());
	}
	
	@Override
	public void displayAchieve() {
		if (achieveMap.size() == 0) {
			Print.println(person.getName()+" has no accomplishments.");
		}else {
			Print.println(person.getName()+"'s Accolades:");
			//interestingly this will likely show them in added order, which is a neat side effect
			for (String title: achieveMap.values()) {
				Print.println(" "+title);
			}
		}
	}
	
	/**
	 * must call on player load and skill updates
	 */
	public void skillUpdate() {
		
	}
	
	/**
	 * generated on the fly, only use for player input/display related tasks
	 */
	public List<Skill> listOfTactics() {
		List<Skill> tacticSkills = new ArrayList<Skill>();
		getPerson().fetchSkills().stream().filter(s -> s.getType() == Type.TACTIC_TYPE).forEach(tacticSkills::add);
		return tacticSkills;
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
	public static double peekTime() {
		return passTime;
	}
	public static double popTime() {
		double temp = passTime;
		passTime = 0;
		Player.player.townEventTimer-=temp;
		return temp;
	}
	public static double takeTime(double limit) {
		if (passTime < limit) {
			return popTime();
		}
		passTime -=limit;
		Player.player.townEventTimer-=limit;
		return limit;
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
				Print.println("Take a sip of your "+flask.effect.getName()+TrawelColor.PRE_WHITE+" potion? ("+TrawelColor.ITEM_VALUE+ flask.sips+TrawelColor.PRE_WHITE+" left)");
			}else {
				Print.println("Take a sip of your potion? ("+flask.sips+" left)");
			}
			if (Input.yesNo()) {
				knowsFlask = true;
				Effect e = flask.effect;
				flask.sip(person);
				Networking.sendStrong("PlayDelay|sound_swallow"+Rand.randRange(1,5)+"|1|");
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
		
		
		Input.menuGo(new MenuGenerator() {

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
			Print.println(quest.name() + ":");
			Print.println(quest.desc());
			
			Input.menuGo(new MenuGenerator() {

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
		if (type == TriggerType.CLEANSE) {
			switch (string) {
			case "bat": case "bear": case "wolf":
			case "unicorn"://unicorns aren't really self aware in this setting
				questTrigger(TriggerType.CLEANSE,CleanseType.ANIMALS.trigger,count);
				break;
			//covers self aware but largely unfriendly creatures
			case "drudger": case "vampire": case "harpy": case "fell":
				questTrigger(TriggerType.CLEANSE,CleanseType.MONSTERS.trigger,count);
				break;
			default:
				//no matching bonus type (or is a bonus type itself)
				break;
			}
		}
		for (Quest q: sideQuests) {
			q.questTrigger(type,string, count);
		}
		
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
	
	public List<QuestR> QRFor(Feature feature) {
		List<QuestR> list = new ArrayList<QuestR>();
		for (Quest q: sideQuests) {
			List<QuestR> adds = q.getActiveQRs();
			if (adds != null) {
				for (QuestR add: adds) {
					if (add.locationF == feature || (feature.getReplaced() != null && add.locationF == feature.getReplaced())) {
						list.add(add);
					}
				}
			}
		}
		return list;
	}
	
	public void addKnowFrag() {
		if (++this.knowledgeFragments >= this.fragmentReq) {
			knowledgeFragments-=fragmentReq;
			Player.player.getPerson().addFeatPoint();
			fragmentReq+=2;
			Print.println(TrawelColor.RESULT_GOOD+"Your knowledge has gained you a feat point!");
			Networking.unlockAchievement("learned1");
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
			return "Oh no, " +World.currentMoneyDisplay(lost) + " were stolen!";
		}
	}
	
	/**
	 * @return true if anything was stolen
	 */
	public boolean stealCurrencyLeveled(Person stealer,float greedFactor) {
		//MAYBELATER: could make this usable on all npcs
		int amountGold = IEffectiveLevel.cleanRangeReward(stealer.getLevel(),greedFactor*10f,.5f);
		int amountAether = IEffectiveLevel.cleanRangeReward(stealer.getLevel(),greedFactor*10_000f,.3f);
		
		//cleanrangereward won't return 0 so we don't have to worry about that
		int lostGold = Player.player.loseGold(amountGold);
		int lostAether = Player.player.loseAether(amountAether);
		stealer.getBag().addGold(amountGold);
		stealer.getBag().addAether(amountAether);
		Print.println(TrawelColor.RESULT_WARN+stealer.getName() + " rifles through your bags...");
		if (lostGold == -1) {
			if (lostAether == -1) {
				Print.println(TrawelColor.RESULT_NO_CHANGE_BAD+"They couldn't find anything they wanted to take!");
				return false;
			}else {
				Print.println(TrawelColor.RESULT_BAD+"They stole " +lostAether+" Aether!");
				return true;
			}
		}else {
			if (lostAether == -1) {
				Print.println(TrawelColor.RESULT_BAD+"They stole " + World.currentMoneyDisplay(lostGold)+"!");
				return true;
			}else {
				Print.println(TrawelColor.RESULT_BAD+"They stole " + World.currentMoneyDisplay(lostGold)+" and "+lostAether+" Aether!");
				return true;
			}
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
		player.globalFindTime-=Rand.randRange(10,30);//then minus
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
		Player.player.story.perkTrigger(perk);
		Player.player.getPerson().setPerk(perk);
	}
	
	public void swapPouch(int slot) {
		Item was = pouch.get(slot);
		pouch.set(slot,Player.bag.swapItem(was));
		Print.println("You use the " + was.getName() + " and put the " + pouch.get(slot).getName() + " in your bag.");
	}
	
	public Item peekPouch(int slot) {
		return pouch.get(slot);
	}
	public Item popPouch(int slot) {
		return pouch.remove(slot);
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
	
	public List<PouchMenuItem> getPouchesAll(){
		List<PouchMenuItem> list = new ArrayList<PouchMenuItem>();
		for (int i = 0; i < pouch.size();i++) {
			list.add(new PouchMenuItem(i));
		}
		return list;
	}
	
	public boolean isInPouch(Item thinking) {
		return pouch.contains(thinking);
	}
	
	/**
	 * when onlyDiscard == true, can be called safely without the player getting a chance to swap items
	 * <br>
	 * otherwise, they can swap all slots, this might work weirdly with looting otherwise, but might be able to do later
	 * @param onlyDiscard
	 */
	public void pouchMenu(boolean onlyDiscard) {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return TrawelColor.STAT_HEADER+"Pouch Size: "+TrawelColor.PRE_WHITE+pouch.size()+"/"+"3";
					}});
				for (Item it: pouch) {
					if (!onlyDiscard) {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Interact with "+it.getName();
							}

							@Override
							public boolean go() {
								Item newit = AIClass.askDoSwap(it, null, true);
								int index = pouch.indexOf(it);
								if (newit == null) {
									pouch.remove(index);
									return false;
								}
								pouch.set(index,newit);//will set it to itself if they reject it
								return false;
							}});
					}
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Melt "+it.getName();
						}

						@Override
						public boolean go() {
							pouch.remove(it);
							Services.aetherifyItem(it,Player.bag,true);
							return false;
						}});
				}
				list.add(new MenuBack());
				return list;
			}});
	}
	
	/**
	 * note that this uses the player it's called on when it can, not just the global player
	 */
	public void youMenu() {
		exitMenu = false;
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Settings";
					}

					@Override
					public boolean go() {
						Input.menuGo(new MenuGenerator() {

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
										return "Character Options";
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
														return "AutoLoot will make the game use the NPC looting choices instead of your own for weapons and armor. Other choices are not affected, such as DrawBanes. It will only display changes. NPCs tend to understand how to compare weapons better than armor.";
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return " AutoLoot: " + person.getFlag(PersonFlag.AUTOLOOT);
													}

													@Override
													public boolean go() {
														person.setFlag(PersonFlag.AUTOLOOT, !person.getFlag(PersonFlag.AUTOLOOT));
														return false;
													}});
												list.add(new MenuLine() {

													@Override
													public String title() {
														return "AutoLevel will make the game pick Archetypes/Feats randomly from the selected choices each level up. It will also make it reconfigure your Skill Configs every time.";
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return " AutoLevel: " + person.getFlag(PersonFlag.AUTOLEVEL);
													}

													@Override
													public boolean go() {
														person.setFlag(PersonFlag.AUTOLEVEL, !person.getFlag(PersonFlag.AUTOLEVEL));
														return false;
													}});
												list.add(new MenuLine() {

													@Override
													public String title() {
														return "AutoBattle will make the game use NPC logic to pick which attacks to use instead of displaying them to you. You must still advance the combat, but the 'back out' option works.";
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return " AutoBattle: " + person.getFlag(PersonFlag.AUTOBATTLE);
													}

													@Override
													public boolean go() {
														person.setFlag(PersonFlag.AUTOBATTLE, !person.getFlag(PersonFlag.AUTOBATTLE));
														return false;
													}});
												list.add(new MenuBack());
												return list;
											}});
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Game Options.";
									}

									@Override
									public boolean go() {
										Input.menuGo(new MenuGenerator() {
											
											@Override
											public List<MenuItem> gen() {
												List<MenuItem> list = new ArrayList<MenuItem>();
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return "Disable Story.";
													}

													@Override
													public boolean go() {
														Print.println(TrawelColor.RESULT_WARN+"Disabling story is permanent. Continue?");
														if (Input.yesNo()) {
															setStory(new StoryNone());
														}
														return false;
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return "Force Story Tutorial.";
													}

													@Override
													public boolean go() {
														Print.println(TrawelColor.RESULT_WARN+"Forcing Story Tutorial won't include any progress already achieved, and getting all triggers might be impossible. Continue?");
														if (Input.yesNo()) {
															setStory(new StoryTutorial());
														}
														return false;
													}});
												if (!getCheating()) {
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Enable Cheats.";
														}

														@Override
														public boolean go() {
															Print.println(TrawelColor.RESULT_WARN+"Cheat mode cannot be disabled one enabled, and disables achievements and leaderboards. Continue?");
															if (Input.yesNo()) {
																setCheating();
															}
															return false;
														}});
												}else {
													list.add(new MenuLine() {

														@Override
														public String title() {
															return "With Punishments off, status effects will not linger after battle or be applied outside of combat to the player.";
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Toggle No Punishments, Currently "+isGameMode_NoPunishments();
														}

														@Override
														public boolean go() {
															setGameMode_NoPunishments(!isGameMode_NoPunishments());
															if (isGameMode_NoPunishments()) {
																Print.println("Punishments disabled. Current punishments will be cleared after a battle.");
															}else {
																Print.println("Punishments re-enabled.");
															}
															//refresh gamemode featuredata
															FeatureData.refreshFeatures();
															return false;
														}});
												}
												list.add(new MenuBack());
												return list;
											}
										});
										return false;
									}});
								list.add(new MenuBack());
								return list;
							}});
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
						Input.menuGo(new MenuGenerator() {

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
										return false;
									}});
								invList.add(new MenuSelect(){

									@Override
									public String title() {
										return "Finances";
									}

									@Override
									public boolean go() {
										Print.println("Local: " +getPerson().getBag().getAether() + " aether, " + showGold()+".");
										Print.println("Planar: " +allGoldDisp()+".");
										Print.println("Gems: " +Gem.playerGems()+".");
										Print.println("Feat Fragments: " +Player.player.currentKFrags+"+"+Player.player.knowledgeFragments+"/"+Player.player.fragmentReq+".");
										return false;
									}
									
								});
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Extended Bag";
									}

									@Override
									public boolean go() {
										pouchMenu(false);
										return false;
									}});
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "DrawBanes Menu (Discard?)";
									}

									@Override
									public boolean go() {
										while (person.getBag().playerDiscardDrawBane() != null);
										return false;
									}});
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Map";
									}

									@Override
									public boolean go() {
										Print.println("You take out your personal map of known towns.");
										mapScrollMenu();
										if (exitMenu) {
											return true;
										}
										return false;
									}});
								invList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Raw Stats";
									}

									@Override
									public boolean go() {
										person.displayStats(false);
										return false;
									}});
								invList.add(new MenuBack());
								return invList;
							}});
						if (exitMenu) {
							return true;
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Society";
					}

					@Override
					public boolean go() {
						Input.menuGo(new MenuGenerator() {

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
										person.facRep.display();
										return false;
									}});
								socList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Titles";
									}

									@Override
									public boolean go() {
										displayAchieve();
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
						Print.println("Really save?");
						Print.println(TrawelColor.PRE_ORANGE+"SAVES ARE NOT COMPATIBLE ACROSS VERSIONS");
						if (Input.yesNo()) {
							Print.println("Save to which slot?");
							for (int i = 1; i < 9;i++) {
								Print.println(i+ " slot:"+SaveManager.checkNameInFile(""+i));
							}
							int in = Input.inInt(8);
							Print.println("Saving...");
							WorldGen.plane.prepareSave();
							SaveManager.save(in+"");
							
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Status";
					}

					@Override
					public boolean go() {
						if (person.hasEffect(Effect.BURNOUT)) {
							Print.println(TrawelColor.RESULT_BAD+"You are burned out. Cure at Doctor or District; rest off in Tavern or Library; or overcome at Oracle or Altar.");
						}
						if (person.hasEffect(Effect.CURSE)) {
							Print.println(TrawelColor.RESULT_BAD+"You are cursed. Lift at Enchanter, Oracle, or Altar.");
						}
						if (person.hasEffect(Effect.TIRED)) {
							Print.println(TrawelColor.RESULT_BAD+"You are tired. Rest off at Tavern or Library.");
						}
						if (person.hasEffect(Effect.WOUNDED)) {
							Print.println(TrawelColor.RESULT_BAD+"You are badly wounded. Cure at Doctor or District.");
						}
						if (person.hasEffect(Effect.DAMAGED)) {
							Print.println(TrawelColor.RESULT_BAD+"Your gear is damaged. Repair at Blacksmith or Appraiser.");
						}
						if (person.hasEffect(Effect.BEES)) {
							Print.println(TrawelColor.RESULT_BAD+"You are beset by bees. Wash off in water or bathe in Tavern.");
						}
						switch ((int)Math.round(getPerson().getBag().calculateDrawBaneFor(DrawBane.EV_DAYLIGHT))) {
						case 0:
							Print.println(TrawelColor.ADVISE_6+"It is very dark outside.");
							break;
						case 1:
							Print.println(TrawelColor.ADVISE_5+"It is dark outside.");
							break;
						case 2:
							Print.println(TrawelColor.ADVISE_4+"It is twilight outside.");
							break;
						case 3:
							Print.println(TrawelColor.ADVISE_3+"It is dim outside.");
							break;
						case 4:
							Print.println(TrawelColor.ADVISE_2+"It is light outside.");
							break;
						case 5:
							Print.println(TrawelColor.ADVISE_1+"It is bright outside.");
							break;
						}
						switch ((int)Math.round(getPerson().getBag().calculateDrawBaneFor(DrawBane.EV_WEALTH))) {
						case 0:
							Print.println(TrawelColor.ADVISE_1+"You do not look worth robbing.");
							break;
						case 1:
							Print.println(TrawelColor.ADVISE_2+"You look like a poor crime target.");
							break;
						case 2:
							Print.println(TrawelColor.ADVISE_3+"You look like you might have some meager wealth to take.");
							break;
						case 3:
							Print.println(TrawelColor.ADVISE_4+"You look like a potential crime target.");
							break;
						case 4:
							Print.println(TrawelColor.ADVISE_5+"You look like a good crime target.");
							break;
						case 5:
							Print.println(TrawelColor.ADVISE_6+"You look like you have more money than you know what to do with.");
							break;
						}
						switch ((int)Math.round(getPerson().getBag().calculateDrawBaneFor(DrawBane.EV_BLOOD))) {
						case 0:
							Print.println(TrawelColor.ADVISE_1+"You have next to no blood on your person.");
							break;
						case 1:
							Print.println(TrawelColor.ADVISE_2+"You have very little blood on your person.");
							break;
						case 2:
							Print.println(TrawelColor.ADVISE_3+"You have a small amount of blood on your person.");
							break;
						case 3:
							Print.println(TrawelColor.ADVISE_4+"You have some blood on your person.");
							break;
						case 4:
							Print.println(TrawelColor.ADVISE_5+"You have a fair bit of blood on your person.");
							break;
						case 5:
							Print.println(TrawelColor.ADVISE_6+"You smell strongly of blood.");
							break;
						}
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Quit to Main Menu";
					}

					@Override
					public boolean go() {
						Print.println("Really quit? Your progress might not be saved.");
						if (Input.yesNo()) {
							Player.isPlaying = false;
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
							Input.menuGo(new MenuGenerator() {

								@Override
								public List<MenuItem> gen() {
									List<MenuItem> hackList = new ArrayList<MenuItem>();
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "View Manual.";
										}

										@Override
										public boolean go() {
											viewManual();
											return false;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Add XP.";
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
															return person.xpDisplay();
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "XP: +1";
														}

														@Override
														public boolean go() {
															person.addXp(1);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "XP: +10";
														}

														@Override
														public boolean go() {
															person.addXp(10);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "XP: +9999";
														}

														@Override
														public boolean go() {
															person.addXp(9999);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "XP: Infinite Auto";
														}

														@Override
														public boolean go() {
															//where we're going, level choices are no longer things...
															person.setFlag(PersonFlag.AUTOLEVEL, true);
															person.addXp(99999);
															Weapon w = person.getBag().getHand();
															for (int i = 0; i < 50;i++) {
																w.levelUp();
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "XP: Next Level";
														}

														@Override
														public boolean go() {
															person.addXp(person.xpToNext());
															return false;
														}});
													list.add(new MenuBack());
													return list;
												}
											});
											return false;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Add Aether.";
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
															return "Have: "+person.getBag().getAether()+" "+TrawelChar.DISP_AETHER+".";
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Aether: +100";
														}

														@Override
														public boolean go() {
															person.getBag().addAether(100);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Aether: +1,000";
														}

														@Override
														public boolean go() {
															person.getBag().addAether(1000);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Aether: +100,000";
														}

														@Override
														public boolean go() {
															person.getBag().addAether(100_000);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Aether: +1 Million";
														}

														@Override
														public boolean go() {
															person.getBag().addAether(1_000_000);
															return false;
														}});
													list.add(new MenuBack());
													return list;
												}
											});
											return false;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "World Currency.";
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
															return allGoldDisp();
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Local ("+World.currentMoneyString()+"): +10";
														}

														@Override
														public boolean go() {
															person.getBag().addGold(10);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Local ("+World.currentMoneyString()+"): +100";
														}

														@Override
														public boolean go() {
															person.getBag().addGold(100);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Local ("+World.currentMoneyString()+"): +1,000";
														}

														@Override
														public boolean go() {
															person.getBag().addGold(100);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Local ("+World.currentMoneyString()+"): +1 Million";
														}

														@Override
														public boolean go() {
															person.getBag().addGold(1_000_000);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "All: +10";
														}

														@Override
														public boolean go() {
															for (World w: WorldGen.plane.worlds()) {
																addGold(10, w);
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "All: +100";
														}

														@Override
														public boolean go() {
															for (World w: WorldGen.plane.worlds()) {
																addGold(100, w);
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "All: +1,000";
														}

														@Override
														public boolean go() {
															for (World w: WorldGen.plane.worlds()) {
																addGold(1000, w);
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "All: +1 Million";
														}

														@Override
														public boolean go() {
															for (World w: WorldGen.plane.worlds()) {
																addGold(1_000_000, w);
															}
															return false;
														}});
													list.add(new MenuBack());
													return list;
												}
											});
											return false;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Reveal All Towns.";
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
											return "Advance Time.";
										}

										@Override
										public boolean go() {
											Input.menuGo(new MenuGenerator() {
												
												@Override
												public List<MenuItem> gen() {
													List<MenuItem> list = new ArrayList<MenuItem>();
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Fastpass One Month.";
														}

														@Override
														public boolean go() {
															for (int i = 31*24; i >=0;i-=24) {
																Player.addTime(24);
																TrawelTime.globalPassTime();
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Fastpass One Year.";
														}

														@Override
														public boolean go() {
															for (int i = 365*24; i >=0;i-=98) {
																//pass in 4 day chunks so that code that triggers once per update happens normally
																//as well as letting the threading code help a bit
																Player.addTime(98);
																TrawelTime.globalPassTime();
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Fastpass 100 Years.";
														}

														@Override
														public boolean go() {
															for (int i = 100*365*24; i >=0;i-=98) {
																Player.addTime(98);
																TrawelTime.globalPassTime();
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Queue One Month.";
														}

														@Override
														public boolean go() {
															if (!Input.yesNo()){
																return false;
															}
															Player.addTime(30*24);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Queue One Year.";
														}

														@Override
														public boolean go() {
															if (!Input.yesNo()){
																return false;
															}
															Player.addTime(365*24);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Queue One Hundred Years";
														}

														@Override
														public boolean go() {
															if (!Input.yesNo()){
																return false;
															}
															Player.addTime(100*365*24);
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Resolve Queue.";
														}

														@Override
														public boolean go() {
															if (!Input.yesNo()){
																return false;
															}
															TrawelTime.globalPassTime();
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Clear Queue.";
														}

														@Override
														public boolean go() {
															if (!Input.yesNo()){
																return false;
															}
															passTime = 0;
															return false;
														}});
													list.add(new MenuBack());
													return list;
												}
											});
											return false;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Cure Punishments.";
										}

										@Override
										public boolean go() {
											getPerson().removeEffectAll(Effect.BURNOUT);
											getPerson().removeEffectAll(Effect.CURSE);
											getPerson().removeEffectAll(Effect.TIRED);
											getPerson().removeEffectAll(Effect.DAMAGED);
											getPerson().removeEffectAll(Effect.WOUNDED);
											getPerson().removeEffectAll(Effect.BEES);
											return false;
										}});
									hackList.add(new MenuSelect() {

										@Override
										public String title() {
											return "Toggle Debug, Currently "+mainGame.debug;
										}

										@Override
										public boolean go() {
											mainGame.debug = !mainGame.debug;
											return false;
										}});
									hackList.add(new MenuBack());
									return hackList;
								}});
							return false;
						}});
				}else {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Manual";
						}

						@Override
						public boolean go() {
							viewManual();
							return false;
						}});
					};
		
				list.add(new MenuBack("Exit Menu."));
				return list;
			}});
		
	}
	
	private void viewManual() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Glossarys";
					}

					@Override
					public boolean go() {
						List<MenuItem> slist = new ArrayList<MenuItem>();
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Aether and Currency";
							}

							@Override
							public boolean go() {
								Print.println("Aether is a magical substance infused into many objects to increase their potency. When infused into items, it can be freed with a basic spell all Personable creatures are capable of.");
								Print.println("When infused in people or animals, it cannot be freed this way, however, it does build up.");
								Print.println("In both these cases, this results in a level. Improving items is an arduous process, but even a bat can level up by fighting other creatures. The conflict itself also generates some aether from the souls of the participants, but the bulk of the aether comes from creatures being slain.");
								Print.println("Aether is a universal currency, however it is not particularly easy to transfer outside of breaking things down and conflict, so stores not trading in Aether-infused items use 'World Currency' as their preferred trade item.");
								Print.println("World Currency, as the name implies, is only good for the world that it's issued in. Other worlds will not accept it, and while many are valuable for their materials, this is never worth the effort of selling compared to how much time it would take to find a buyer.");
								Print.println("World Currencies are also needed to buy land and forts. Both Aether and World Currencies are needed to build on land, but forts often have better connections once the initial hurdle of obtaining the deed is passed.");
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Level, Feat Points, Feat Picks";
							}

							@Override
							public boolean go() {
								Print.println("Every Person in Trawel has a level. This starts at 1 and goes up. They also have an Effective Level, which is usually 10+ their actual level. This effective level is used so that a level 2 person isn't twice as good as a level 1 person- often effectiveness (damage, armor, etc) is multiplied by effective level divided by 10.");
								Print.println("Every time any Person levels up, they gain a Feat Point. As a player, you can use this in your character screen, if you have a Feat Pick. You get one Feat Pick per level up. Each Feat Point can buy one Feat or Archetype, from a list of up to 8 options. If you don't like your choices, you can choose to delay spending a point.");
								Print.println("When you use a Pick, you actually get to keep choosing until you run out of Points or reject a choice. When you choose, the options are generated on the fly, however, with the exception of the 'discourage repeat skills' mechanic if you get a Perk, delaying will not change the odds or actual pool of choices you have.");
								Print.println("Thus, waiting does let you save your Picks if you want to have more chances to reroll, but this is minor and will not change the potential outcomes on it's own. You will usually want to pick as soon as you can. There are also ways to get extra picks, such as libraries.");
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Features";
							}

							@Override
							public boolean go() {
								FeatureData.getGlossary();
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Armor";
							}

							@Override
							public boolean go() {
								Print.println("Armors are Aether-infused items meant to block blows.");
								Print.println("The main three properties of an Armor are how well it defends against physical damage types. This is a result of it's effective level, material, and style.");
								Print.println("Armors also influence your agility multiplier penalty, have a weight which can weigh you down if you can't fit all your used equipment in your capacity, and have elemental damage multipliers.");
								Print.println("Unlike weapons, armors have positive (Quality), negative (Flaw), and neutral (trait) traits.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of armor traits?");
								if (Input.yesNo()) {
									for (ArmorQuality q: Armor.ArmorQuality.values()) {
										Print.println(q.addText() + (q.mechDesc != null ? " ("+q.mechDesc+")" : ""));
									}
								}
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Weapons";
							}

							@Override
							public boolean go() {
								Print.println("Weapons are Aether-infused items meant to inflict harm on other Persons and creatures.");
								Print.println("The main importance of weapons are for their attacks- every weapon type has a set, and the final numbers are determined by it's effective level and material.");
								Print.println("Weapons also tend to have weapon qualities, which are positive traits. This glossary does not include a list of all weapon attacks, but you can browse them in a format that tests their effectiveness from one of the main menu tests.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of weapon qualities?");
								if (Input.yesNo()) {
									for (WeaponQual q: WeaponQual.values()) {
										Print.println(TrawelColor.ITEM_WANT_HIGHER+q.name +TrawelColor.PRE_WHITE+": " + q.desc);
									}
								}
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Skills";
							}

							@Override
							public boolean go() {
								Print.println("Skills are abilities, typically conditional, that add things that your character can do. Most of them apply automatically, some give you more options, and others must be set up.");
								Print.println("There are a lot of skills, but having a skill is a binary state- if you get it from another skill source, you still 'only' have it once. Simply put, skills don't stack with themselves.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of skills?");
								if (Input.yesNo()) {
									for (Skill s: Skill.values()) {
										Print.println(s.explain());
									}
								}
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Archetypes";
							}

							@Override
							public boolean go() {
								Print.println("Archetypes are a skill source, and uniquely unlock Feat Types that you can pick Feats from when you level up. They also grant attributes. Some archetypes also require similar archetypes to be obtained before they can be picked.");
								Print.println("The game encourages you to have 2 + 1 for every 5 levels archetypes, but you are only required to unlock one before you can start picking Feats instead.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of archetypes?");
								List<MenuItem> alist = new ArrayList<MenuItem>();
								if (!Input.yesNo()) {
									return false;
								}
								for (Archetype a: Archetype.values()) {
									alist.addAll(IHasSkills.dispMenuItem(a));
								}
								Input.menuGo(new ScrollMenuGenerator(alist.size(), "previous <> archetypes", "next <> archetypes") {

									@Override
									public List<MenuItem> forSlot(int i) {
										return Collections.singletonList(alist.get(i));
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
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Feats";
							}

							@Override
							public boolean go() {
								Print.println("Feats are a skill source, meaning they grant skills, might grant a skill config action to use, and attributes. Feats tend to give attributes based on how many skills they grant 5 for 3 skills, 15 for two skills, or 30 for one skill.");
								Print.println("All level up skill sources that aren't Archetypes are Feats.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of feats?");
								if (!Input.yesNo()) {
									return false;
								}
								List<MenuItem> alist = new ArrayList<MenuItem>();
								for (Feat a: Feat.values()) {
									alist.addAll(IHasSkills.dispMenuItem(a));
								}
								Input.menuGo(new ScrollMenuGenerator(alist.size(), "previous <> feats", "next <> feats") {

									@Override
									public List<MenuItem> forSlot(int i) {
										return Collections.singletonList(alist.get(i));
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
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Perks";
							}

							@Override
							public boolean go() {
								Print.println("Perks are a skill source, granting skills and attributes.");
								Print.println("Unlike Feats and Archetypes, you get Perks from fulfilling specific conditions, like killing bosses or making offerings at altars, instead of by leveling up.");
								Print.println("Most of the perks displayed below are only for NPCs.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of perks?");
								if (!Input.yesNo()) {
									return false;
								}
								List<MenuItem> alist = new ArrayList<MenuItem>();
								for (Perk a: Perk.values()) {
									alist.addAll(IHasSkills.dispMenuItem(a));
								}
								Input.menuGo(new ScrollMenuGenerator(alist.size(), "previous <> perks", "next <> perks") {

									@Override
									public List<MenuItem> forSlot(int i) {
										return Collections.singletonList(alist.get(i));
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
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Wounds";
							}

							@Override
							public boolean go() {
								Print.println("Wounds are ailments caused by an attack. They can be further divided into two categories: normal wounds and condition wounds.");
								Print.println("Normal wounds have a 90% chance to occur on all attacks, and are chosen based on the attack's damage types and the body part that is being attacked.");
								Print.println("Keen weapons always roll wounds on their attacks, bypassing the normal 10% chance of a 'Grazed'. This does not change the result of a 'Negated', which is a wound signifying an attack that hits but isn't very effective outside of the damage.");
								Print.println("Rolled wounds typically have instant or short-term effects. They also can be inflicted through skills. The main exception to this is wounds involving Bleed.");
								Print.println("Condition wounds occur automatically when a body part reaches 50% 'condition'. They tend to be long lasting effects that highlight the downward spiral of combat.");
								Print.println("There are several ways to negate inflicted wounds, but Condition wounds ignore these affects. Condition wounds are also often called Injuries.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of wounds? Values will not display if they vary.");
								if (Input.yesNo()) {
									for (Wound w: Wound.values()) {
										try {
											Print.println(TrawelColor.TIMID_RED+w.name + TrawelColor.PRE_WHITE+ " - " + String.format(w.desc,(Object[])Combat.woundNums(null,null,null,null,w)) + " ("+w.active+")");
										}catch (Exception e) {
											Print.println(TrawelColor.TIMID_RED+w.name + TrawelColor.PRE_WHITE+ ": " + w.desc + " ("+w.active+")");
										}
									}
								}
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Effects";
							}

							@Override
							public boolean go() {
								Print.println("Effects are temporary status effects. They are all counters, although many have that counter limited to 1.");
								Print.println("Effects don't store any information in themselves other how many a Person has.");
								Print.println("Some effects persist after battle, and through death, which means they need to be resolved- Doctors, Shamans, Blacksmiths, water sources, and Inns can heal different types.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of Effects?");
								if (Input.yesNo()) {
									for (Effect e: Effect.values()) {
										Print.println(e.getName()+TrawelColor.PRE_WHITE + ": " + e.getDesc() + " Persists: " + e.lasts() + " Stacks: " +e.stacks());
									}
								}
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "DrawBanes";
							}

							@Override
							public boolean go() {
								Print.println("DrawBanes are minor inventory items. Many can be used as potion reagents, some can be used to build for features, and they can be sold or donated to merchants.");
								Print.println("DrawBanes, true to their name, can also attract or repel random encounters. For example, meat attracts wolves and bears, gold attracts thieves, some magic items attract fell reavers, and virgins attract unicorns.");
								Print.println("You can discard DrawBanes from your inventory using the Player menu, which you might want to do to stop getting accosted by animals.");
								Print.println("You also have a limited amount of space to store them. Note that some bumpers will also be attracted to other aspects of your character, such as vampires attacking you if you're soaked in blood.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of DrawBanes?");
								if (Input.yesNo()) {
									for (DrawBane d: DrawBane.values()) {
										Print.println(TrawelColor.TIMID_MAGENTA+d.getName()+TrawelColor.PRE_WHITE+": " + d.getFlavor()
										+ TrawelColor.ITEM_DESC_PROP+" Brewable: "+TrawelColor.TIMID_MAGENTA+ d.getCanBrew()
										+ TrawelColor.ITEM_DESC_PROP+" Value: "+TrawelColor.ITEM_VALUE+ d.getValue()
										+ TrawelColor.ITEM_DESC_PROP+ " Merchant Value: "+TrawelColor.ITEM_VALUE + Print.F_TWO_TRAILING.format(d.getMValue()));
									}
								}
								return false;
							}});
						slist.add(new MenuSelect() {

							@Override
							public String title() {
								return "Seeds";
							}

							@Override
							public boolean go() {
								Print.println("Seeds can be planted in Plant Spots, either in Node Exploration town Features, or Garden town Features. They will then grow as time passes. Some seeds grow into items that can be harvested, while others can only be taken.");
								Print.println("Seeds have a limited inventory space, but are quite rare, so it is a bit harder to reach that cap. They can't be used for anything else, but often can be used to grow DrawBanes.");
								Print.println(" ");
								Print.println(TrawelColor.STAT_HEADER+"Would you like to see a list of plant states?");
								if (Input.yesNo()) {
									for (Seed d: Seed.values()) {
										Print.println(d.toString());
									}
								}
								return false;
							}});
						Input.menuGo(new ScrollMenuGenerator(slist.size(), "previous <> terms", "next <> terms"){

							@Override
							public List<MenuItem> forSlot(int i) {
								return Collections.singletonList(slist.get(i));
							}

							@Override
							public List<MenuItem> header() {
								return Collections.singletonList(new MenuLine() {

									@Override
									public String title() {
										return "Some terms include a total list of all their contents. These are typically quite long, and may include internal information that does not show up otherwise.";
									}});
							}

							@Override
							public List<MenuItem> footer() {
								return Collections.singletonList(new MenuBack());
							}
							
						});
						return false;
					}
				});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Infodump Tutorial (partly outdated)";
					}

					@Override
					public boolean go() {
						Print.println("Thanks for playing Trawel! Here's a few tips about learning how to play:");
						Print.println("All of Trawel proper, and most of the side games, only require inputing a number between 1 and 9.");
						Print.println();
						Print.println("There are a few games in Trawel, but the one simply called 'Trawel' has the following advice:");
						Print.println("Always be on the lookout for better gear than you currently have. Your power level is largely determined by how powerful your gear is- not just it's level.");
						Print.println("There are three primary attack and defense types, sharp, blunt, and pierce.");
						Print.println("Sharp is edged and cutting. Swords are good at it, and chainmail is good at defending from it. Some materials are softer, like Gold, and thus bad at it.");
						Print.println("Blunt is heavy and crushing. Maces are good at it, and gold is good at defending from it- and also dealing it.");
						Print.println("Pierce is pointy and puncturing. Spears are good at it, and metals are better at defending from it.");
						Print.println("If you're feeling tactical, you can read your opponent's equipment to try to determine which type they are weak to.");
						Print.println("As you play the game, you'll get a grasp of the strengths and weaknesses of varying materials and weapons. It's part of the fun of the game!");
						Print.println("Attacks have a delay amount (further broken down into warmup/cooldown) and a hitchance, along with damage types.");
						Print.println("Delay is how long it takes for the attack to happen- it can be thought of how 'slow' the attack is, so lower is better. Warmup is the period before you act, and Cooldown is the period after- but you can't choose another action until both elapse.");
						Print.println("Hitchance is the opposite- higher is more accurate. However, it is not a percent chance to hit, as it does not account for the opponent's dodge, which can change over time.");
						Print.println("Enchantments can be both good and bad, so keep an eye out for gear that has low stats but boosts overall stats a high amount- or gear that makes you much weaker!");
						Print.println("When looting equipment, you are shown the new item, then your current item, and then the stat changes between the two- plus for stat increases, minus for stat decreases. The difference will not show stats that remain the same.");
						Print.println("Value can be a good rough indicator of quality, but it does not account for the actual effectiveness of the item, just the rarity and tier.");
						Print.println("For example, gold (a soft metal) sharp/piercing weapons are expensive but ineffective.");
						Print.println("When in combat, you will be given 3 (by default, skills and circumstance may change this) random attacks ('opportunities') to use your weapon.");
						Print.println("Pay close attention to hit, warmup/cooldown, and sbp (sharp, blunt, pierce) damage.");
						Print.println("More simply: Higher is better, except in the case of delay (warmup and cooldown).");
						Print.println("Leveling Terms: WELVL is weapon effective level this starts at 10 and goes up from the Crude tier. LHP is Leveled HP. This is 100 at level 0, and goes up by 10 every level.");
						Print.println("Bleed lists a % of LHP. It also caps out at two levels higher than your own, like many other leveled mechanics.");
						Print.println("Well, you made it through bootcamp. Have fun!");
						Print.println("-realDragon");
						return false;
					}});
				list.add(new MenuBack());
				return list;
			}});
	}
	
	private void mapScrollMenu() {
		final List<World> vistedWorlds = new ArrayList<World>();
		WorldGen.plane.worlds().stream().filter(w -> w.hasVisited()).forEach(vistedWorlds::add);
		Input.menuGo(new ScrollMenuGenerator(vistedWorlds.size(),"prior <> worlds","next <> worlds") {
			
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
						if (!visitedTowns.contains(w.getStartTown())) {
							//always have a path to the starting town in your current world
							visitedTowns.add(w.getStartTown());
						}
						Input.menuGo(new ScrollMenuGenerator(visitedTowns.size(),"prior <> towns","next <> towns") {

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
										Input.menuGo(new MenuGenerator() {

											@Override
											public List<MenuItem> gen() {
												List<MenuItem> list = new ArrayList<MenuItem>();
												list.add(new MenuLine() {

													@Override
													public String title() {
														return t.getName()+": tier " +t.getTier();
													}});
												boolean blockTravel = Player.player.atFeature != null;
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return "Compass" + (blockTravel ? " (Blocked)" : "");
													}

													@Override
													public boolean go() {
														if (blockTravel) {
															Print.println("You cannot currently travel with the Compass.");
															return true;
														}
														WorldGen.pathToTown(t);
														Print.println("Travel to " + t.getName()+"?");
														if (Input.yesNo()) {
															WorldGen.travelToTown(t);
															exitMenu = true;
															return true;
														}
														return false;
													}});
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return t.hasLore() ? "Lore" : "Stories";
													}

													@Override
													public boolean go() {
														 if (t.hasLore()) {
															 Print.println(t.getLore());
														 }else {
															 Print.println("There are no stories about " + t.getName()+".");
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
														Island isle = t.getIsland();
														Print.println(t.getName() + " is a tier " +t.getTier() + " "
																+ (t.isFort() ? "fort" : "town") + " located "+isle.getLocDesc()+" the "+isle.getTypeName()+" of " +isle.getName()+", which has "+isle.getTowns().size()+" regions in it."
																);
														float per = t.getAllOccupants().size()/t.occupantGoal();
														String occString = t.getAllOccupants().size()+TrawelColor.PRE_WHITE;
														if (per < 1f) {
															occString = TrawelColor.inlineColor(TrawelColor.colorMix(TrawelColor.colorMix(Color.BLACK,Color.WHITE,.8f),Color.WHITE,per))+occString;
														}else {
															//per > 1 so we don't need to subtract 1 and then add one for the first term of the mix
															occString = TrawelColor.inlineColor(TrawelColor.colorMix(Color.WHITE,Color.RED,(per)/(per+10f)))+occString;
														}
														Print.println("It has around " +t.getFeatures().size() + " things to do and around " + occString + " occupants.");
														if (Player.player.getCheating()) {
															Print.println("Goal Desire: " +t.occupantGoal()+ " Need: " +t.occupantNeed());
															Print.println("Inn Connect Flow: " + (t.hasConnectFlow() ? t.getConnectFlow().getName() + " to " +t.getConnectFlow().otherTown(t).getName() : "none"));
														}
														if (!t.isFort() && t.openSlots() > 0) {
															Print.println("There are " + t.openSlots() + " lots of land free.");
														}
														double[] loc = Calender.lerpLocation(t);
														Print.print("In "+w.getName() +", it is located at "+ Print.F_WHOLE.format(loc[0]) +" latitude and "+Print.F_WHOLE.format(loc[1]) +" longitude.");
														if (w == Player.getPlayerWorld()) {
															Print.println(" It is about " +w.getCalender().stringLocalTime(t) + " there.");
														}else {
															Print.println();//flush
														}
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
															Print.println(tagFluff+".");
														}
														int road = 0;
														int ship = 0;
														int tele = 0;
														for (Connection c: t.getConnects()) {
															switch (c.getType().type) {
															case LAND:
																road++;
																break;
															case SEA:
																ship++;
																break;
															case MAGIC:
																tele++;
																break;
															}
														}
														Docks docks = null;
														for (Feature f: t.getFeatures()) {
															if (f instanceof Docks) {
																docks = (Docks) f;
																break;
															}
														}
														Print.println(TrawelColor.STAT_HEADER+"Connections:");
														if (road > 0) {
															Print.println(" Has " + road + " land routes.");
														}
														if (tele > 0) {
															Print.println(" Has "+tele+" teleport rituals.");
														}
														if (docks != null) {
															int farFlung = docks.farConnectsList().size();
															Print.println(" Has Docks with " + ship + " direct routes" + (farFlung > 0 ? " and "+farFlung + " indirect destination"+(farFlung > 0 ? "s" : "")+"." : "."));
														}else {
															if (ship > 0) {
																Print.println(" Has "+ship+" locations from their port.");
															}
														}
														Print.println(" ");
														return false;
													}});
												if (Player.player.getCheating()) {
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "Debug: Print Terminal Map";
														}

														@Override
														public boolean go() {
															char[][] map = new char[w.getXSize()][w.getYSize()];
															
															for (int i = 0; i < map.length; i++) {
																Arrays.fill(map[i], ' ');
															}
															for(Island i: w.getIslands()) {
																for (Town town: i.getTowns()) {
																	if (t != town) {
																		if (map[town.getLocationX()-1][town.getLocationY()-1] != ' ') {
																			if (map[town.getLocationX()-1][town.getLocationY()-1] == 'x') {
																				map[town.getLocationX()-1][town.getLocationY()-1] = 'y';
																			}else {
																				if (map[town.getLocationX()-1][town.getLocationY()-1] != 'y') {
																					map[town.getLocationX()-1][town.getLocationY()-1] = 'v';
																				}
																			}
																			break;
																		}
																		//radix explanation: https://stackoverflow.com/a/43035605
																		map[town.getLocationX()-1][town.getLocationY()-1] = Character.forDigit(town.getTier()%10, 10);		
																	}else {
																		if (map[town.getLocationX()-1][town.getLocationY()-1] == ' ') {
																			map[town.getLocationX()-1][town.getLocationY()-1] = 'x';
																		}else {
																			map[town.getLocationX()-1][town.getLocationY()-1] = 'y';
																		}
																	}
																}
															}
															for (int j = 0; j < w.getYSize(); j++) {
																Print.print(" ");//print a space to avoid causing issues in the graphical
																for (int i = 0; i < map.length; i++) {
																	Print.print(""+map[i][j]);
																}
																Print.println();
															}
															return false;
														}});
													list.add(new MenuSelect() {

														@Override
														public String title() {
															return "DEBUG: teleport";
														}

														@Override
														public boolean go() {
															Player.player.setLocation(t);
															//catch up time globally so teleporting to another world is less wonky
															TrawelTime.globalTimeCatchUp();
															return false;
														}});
												}
												list.add(new MenuBack());
												return list;
											}});
										if (exitMenu) {
											return true;
										}
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
						if (exitMenu) {
							return true;
						}
						return false;
					}});
			}
			
			@Override
			public List<MenuItem> footer() {
				return Collections.singletonList(new MenuBack("Cancel"));
			}
		});
	}
	public int getForceReward(AltarForce key) {
		return forceRewards.getOrDefault(key,0);
	}
	public void nextForceReward(AltarForce key) {
		forceRewards.put(key,1+getForceReward(key));
	}
	
	public float getForceRelation(AltarForce key) {
		return forceRelations.getOrDefault(key,0f);
	}
	public void addForceRelation(AltarForce key,float value) {
		forceRelations.put(key,value+getForceRelation(key));
	}
	
	@Override
	public void addGold(int delta,World w) {
		super.addGold(delta,w);
		Networking.leaderboard("highest_world_currency", getGold(w));
	}
	
	public boolean askBuyMoney(int money, String toBuy) {
		int current = getGold();
		if (current < money) {
			Print.println("You need " + World.currentMoneyDisplay(money) + " but you only have " + current+".");
			return false;
		}
		Print.println("Pay "+ World.currentMoneyDisplay(money) +" of your " +current +" for " +toBuy+"?");
		if (Input.yesNo()) {
			loseGold(money);
			return true;
		}
		return false;
	}
	
	public int askSlot() {
		Print.println("1 Head ("+Player.bag.getArmorSlot(0).getName()+")");
		Print.println("2 Arms ("+Player.bag.getArmorSlot(1).getName()+")");
		Print.println("3 Chest ("+Player.bag.getArmorSlot(2).getName()+")");
		Print.println("4 Legs ("+Player.bag.getArmorSlot(3).getName()+")");
		Print.println("5 Feet ("+Player.bag.getArmorSlot(4).getName()+")");
		Print.println("6 Weapon ("+Player.bag.getHand().getName()+")");
		Print.println("9 Cancel.");
		return Input.inInt(6,true,true);
	}
	
	public boolean addPunishment(Effect punishment) {
		if (isGameMode_NoPunishments()) {
			return false;
		}
		if (getPerson().hasEffect(punishment) && !punishment.stacks()) {
			return false;
		}
		getPerson().addEffect(punishment);
		return true;
	}
	
	public void checkWipePunishments() {
		if (!isGameMode_NoPunishments()) {
			return;
		}
		getPerson().clearEffects();
	}

	public static boolean isGameMode_NoPunishments() {
		if (player == null) {
			return false;
		}
		return player.gameMode_NoPunishments;
	}

	public void setGameMode_NoPunishments(boolean gameMode_NoPunishments) {
		this.gameMode_NoPunishments = gameMode_NoPunishments;
	}
	
}
