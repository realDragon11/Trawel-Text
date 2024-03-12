package trawel;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuGeneratorPaged;
import derg.menus.MenuItem;
import derg.menus.MenuLast;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import save.KyroManager;
import scimech.combat.MechCombat;
import scimech.handlers.SaveHandler;
import scimech.mech.Mech;
import scimech.people.Pilot;
import scimech.units.mechs.DebugMech;
import scimech.units.mechs.Dynamo;
import scimech.units.mechs.Hazmat;
import scimech.units.mechs.Musketeer;
import scimech.units.mechs.Packrat;
import scimech.units.mechs.Pirate;
import scimech.units.mechs.Pyro;
import scimech.units.mechs.Swashbuckler;
import trawel.Networking.ConnectType;
import trawel.battle.Combat;
import trawel.battle.Combat.SkillCon;
import trawel.battle.attacks.ImpairedAttack.DamageType;
import trawel.battle.attacks.StyleFactory;
import trawel.battle.attacks.TargetFactory;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.factions.HostileTask;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Archetype;
import trawel.personal.item.DummyInventory;
import trawel.personal.item.Item;
import trawel.personal.item.body.Race;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory;
import trawel.towns.Calender;
import trawel.towns.World;
import trawel.towns.events.TownFlavorFactory;
import trawel.towns.services.Oracle;
/**
 * 
 * @author dragon
 * 2/5/2018
 * Entry point for compiled game.
 */
public class mainGame {

	//b__X is in development, b_X is the actual release of that version
	public static final String VERSION_STRING = "v0.8.b_10h3";
	public static final String VERSION_DATE = " updated Mar 12th 2024";
	public static final String[] changelog = new String[] {
			//add to front, changeviewer cycles to older ones when used
			"b_10 hotfix 3: pouch swap rejection crash, dryad refight crash, dungeon regrow wrong checkpoint type, more perks have attributes, buffed magic attacks and some related archetypes and feats.",
			"b_10 hotfix 2: fixed fort menu crashes, improved timeevent error reporting.",
			"b_10 hotfix 1: increased world currency amount for most humanoid NPCs, attempted to fix dungeon party crash, fixed Inn rent waiting and let you rent more if you have less than 1 month left.",
			
			"b_10: {part 1/9} You can travel through multiple towns at the same time by using the Compass from your Map, in your Inventory. This can result in multiple random encounters.",
			"b_10: {part 2/9} Curse, Tired, Damaged, and Bees are now punishment longer lasting effects. Tired can be cured when resting, Bees can be cured by entering water. Curse is still cured at Doctors, which heals everything but Damaged. Damaged requires Blacksmith repairs. These effects will be used to give a downside to failing Attribute checks, so they're not just free rewards with no consequences.",
			"b_10: {part 3/9} This punishment system can be seen in the 'Trapped Treasure Chambers' in Mines and Dungeons, which have their own small set of traps and mechanics for those that would seek to loot them. Failing checks applies Burnout which prevents further ones until you rest or cure. Rolls that aren't prevented (such as mountain rocks) have their attributes halved.",
			"b_10: {part 4/9} Aether is now used entirely for buying/selling equipment. Drawbanes still use World Currency. Enchanters no longer let you sell aether. This changes are meant to make each currency serve a purpose and let them be more common without dealing with exchanging balancing problems.",
			"b_10: {part 5/9} Added Drudger, Fell, Monster, and Animal Cleanse Quests. Almost all targets will award progress if encountered in Node Explorations or other features as well, with some exceptions for mooks. Some fightable hunters now reward Amber when looted. Witch Huts have more Collect targets.",
			"b_10: {part 6/9} Random encounter frequency was bugged, is now a lot more common. Vampires and some other Bumpers will appear less during the daytime. Bandits will prefer money, not just valuable metals. Removed most bumper level requirements, and updated wolf pack with player allies. There are more Primal Deathcheater types.",
			"b_10: {part 7/9} The first Node in Features will not have forced combat, and the first two layers will have less free loot and less obstacle fights.",
			"b_10: {part 8/9} log.txt has full stack trace. Updated tutorial's feature explanations and list of world perks. Mines now properly weight their contents. Graveyard's Nightvison behavior has been fixed. Connection failure message will only play once, after 10 seconds.",
			"b_10: {part 9/9} Some less prominent bugfixes can be found in the Steam patch notes and github commit history.",
			
			"Prior Changelog notes were from 2023, press again to continue.",
			
			"b_9: {part 1/1} Expanded Bag items can now be melted down into aether from player menu or if Bag is full when looting. 'Really Attack' prompts give the option to examine the Person's inventory.",
			"b_9: {part 2/2} Plant Spots, Drawbanes, and Alchemy have recieved interconnected improvements, most notably a few extra potion combos. Altars now can have Forest blessings. Traveling features have new special behavior, and you can rent rooms at Inns.",
			"b_9: {part 3/3} Added the Enchanter Town Feature, which can enchant or improve enchantments on Weapons and Armor. It can also allow you to sell Aether for World Currency. The Blacksmith can now temper armor to remove negative traits at the cost of Amber Gems.",
			"b_9: {part 4/4} Changed internal Sidequest code to save properly, tweaked quest generation behavior, added quests to the Rogue, Hero, and new Hunter's guild. Added more Quest Reactions.",
			"b_9: {part 5/5} Increased positive reputation gain from combat, decreased negative gain. Added Amber as the Hunter's gem, and updated world currency/gem costs and rewards.",
			"b_9: {part 6/6} Adjusted Wounds, importantly adding more elemental wounds.",
			"b_9: {part 7/7} Added more Classless Skills and Feats: Beer Belly, Deadly Aim, Glutton, Life Mage, Shaman, Potion Chugger.",
			"b_9: {part 8/8} Expanded Greap worldgen map, filling out some more towns and adding many town locations. Route types have been expanded to include caravans and lesser traveled paths, which have different travel speeds and NPC interests.",
			"b_9: {part 9/9} Added Simplified attack display style, moved attack notes from Combat Debug to its own option.",
			
			"b_8: {part 1/5} Added a wait option to Arenas and prevented giant time chunks by accident, made Arenas fight without the player and have the winners move on after enough training. Players always get a new fighter in their first round (so entirely for 1 round titles), but more rounds are done entirely naturally now, potentially drawing from survivors.",
			"b_8: {part 2/5} Updated Lot menu, standardized collecting earned money from built Features. Features can now have intro/outro text, which can be turned off as a display option. Node Features now have autosave enabled while exploring them, and saves once again use a different date format.",
			"b_8: {part 3/5} Created unique stances for Fish Anchor, Fish Spear, and Lance which all used other weapon stances as placeholders since the weapon updates. Two stances still need migration. Grazes have been renamed Negates and Graze is now used to indicate no rolled wound. Armor Quality value and fitness updated, which mostly matters for AI, as well as Weapon Quality price.",
			"b_8: {part 4/5} Cleaned up Wound/Effect/Tactic text and code, added a few new Skills. Archetypes now grant more skills, as well as stats. Some new archetypes were added.",
			"b_8: {part 5/5} More insults added to species, materials with extreme outlier elemental stats reigned in.",
			
			"b_7: {part 1/2} Nodes display inward/outward to indicate going deeper or closer to the entrance (in most cases, ultimately its merely a 'floor' counter).",
			"b_7: {part 2/2} Updated to Kryo serialization, Java 8 to Java 17 migration failed. ",
			
			"b_6: {part 1/5} Updated stat displays and item 'higher/lower is better' displays. Tutorial text separated from 'feature description' text.",
			"b_6: {part 2/5} Added many new towns to the Eoano WorldGen, and fluffed up the lore and flavor of existing towns as well. Species store re-added to Arona. Most bosses are now non-unique in that you don't have only one option if you want their Perk. Ancient Queen boss added to new island.",
			"b_6: {part 3/5} Cleaned up Greap WorldGen, although it is still not a complete world.",
			"b_6: {part 4/5} Some Features got larger improvements as part of the WorldGen updates: Districts now can house sub-stores. Gardens now start filled, and refill over time, and their changes in growth speed are more distinct for worldgen gardens. Several node features had changes to what nodes are likely to generate in them, and also their levels, which now display as a range when viewing from the town. Added Forest Altar, making altars now be dedicated to different primal forces.",
			"b_6: {part 5/5} Some titles were changed, others were added. Steam version gained several new leaderboards and achievements.",
			
			"b_5: {part 1/4} You can now recruit adventurers in certain dungeons. All other dungeons had their mass battles removed. In 'dangerous' dungeons, these adventurers will assist you in the 'high security' checkpoints, and against the boss. Currently this is just the Tower of Fate and the Fatespinner. They will get a share of the loot, and if higher level than you, might get first pick. They will not be interested in joining you if they are 2 or more levels higher. They will not revive on death, and will leave if you do not visit the dungeon for a while. Fighting with them will make them take longer to leave than just visiting will.",
			"b_5: {part 2/4} Passive population migration has been fixed and tweaked, now if you play close attention you might be able to see persons moving from town to town. They have a slight preference to move towards towns with Inns, but also to not overcrowd a town past what its features can support.",
			"b_5: {part 3/4} All Features and Nodes now have updated behavior to update the background in the legacy graphical, and often fall back to different art now, since not all of it was made. (Same as the treatment armor, people, and weapons got). The 'examine closely' for weapons has been improved, and armor qualities also appear in world again, with slightly different functionalities.",
			"b_5: {part 4/4} Necks and Heads best damage mult is 2.5x damage instead of 3x, reducing the 'neck meta' of AI's absurd alpha strikes.",
			
			"b_4: {part 1/4} Added 'back out' option to terminal (graphical will get it when it gets other updates). Added a few new display options, check the reduced indicator menu again, and also various 'realtime waiting' options. Added 'tactics' which are persistent action options in combat that don't deal damage (requires skills), and also some skill stances which apply a tactic effect and also a normal attack.",
			"b_4: {part 2/4} Changed up condition wounds a fair bit, turning them into an injury system to constrast the typically short-lived nature of normal wounds in combat. Getting brained or shattered can hurt quite a bit. A boss, 'Yore' was also added to the Dungeon of Fate, with a new battle condition-based dungeon mechanic. Baron of Hell also got a demon bodytype, and cloth was turned into patchwork, linen, and cotton.",
			"b_4: {part 3/4} Added a 'pouch/bag' feature to store equipment, currently works quite well outside of making room for things you're not currently comparing. You menu reforged into 'Player' menu, which can save in Nodes now. Includes character-exclusive settings like autolevel, autoloot, and autobattle.",
			"b_4: {part 4/4} Enchanced compass into a full map, with directions to any visited town (and always Unun), and also town flavor in that menu, in addition to population and feature counts, timezone, etc. Display current time in hours and minutes in town menu. Society Titles have been expanded.",
			
			"b_3: {part 1/3} Tutorial mostly moved from 'feature text' to 'story'. Added saveable display options. Known issues: combat town Features need another cleanup with the new currency, mass battles have some balancing issues if they have an un-even number of fighters.",
			"b_3: {part 2/3} Subtle changes to battle conditions. Effective level added, so level 2 is only a flat +10% better, not 2x better, which each further level being another +10% (level 3 is 1.3x mult, because level 1 is 1.1x). Stat tweaks on most species, some non-'personable' creatures now have barks and can loot money.",
			"b_3: {part 3/3} Many town Feature mechanics had small tweaks, Brewing being the most visible in regards to how DrawBanes are talked about, and once again Merchant Guild prices, Fort Hall prices, and the more one-off Doctor prices. 'Highest Contribution' battlescore stat got split into the more sensible 'highest and lowest % deviation from average', which can show the extremes in attack balance a weapon has, for example a gold weapon being good at slapping but nothing else.",
			
			"b_2: {part 1/3} Attack backend changes became frontend changes for weapons, which now have different stat displays. This also made armor and dodging actual things again, before their average stats were a bit too low, now the forumlas have been re-tested and remade. 'classless' system (essentially multiclassing but with many multis) has born fruit, you can now use the replacement system, although it still has a long ways to go.",
			"b_2: {part 2/3} If you want to pick your own Archetype to start with, use slowstart, otherwise it will pick a random one. Note that some require additional setup of their magic attacks. Nodes areas and ports also received complete overhauls, and much of the update development time was spent making Trawel run again after breaking nodes to improve them. Various small features, including witch huts, slums, and world generation also got less major updates and fixes.",
			"b_2: {part 3/3} Some changes were made but not enough to have anything to show, for example summons should work, (which is a far cry from Trawel in 2019, where the concept of a 3 person fight was unthinkable) but there are no skills that summon any creatures yet.",
			
			"b_1: base attack code reworked in basically every way. currency divided. threading added (nothreads is an arg), time passing redone. Node exploration mostly same but had entire backend update. Locational damage exists but does little at the moment."
			
			,"End of current beta ingame changelog. Check the Github and Steam for prior updates and more detailed notes."
	};
	public static int changelogViewer = 0;

	public static Scanner scanner = new Scanner(System.in);

	public static boolean debug = false;
	public static boolean inEclipse = true;//for other run configs
	public static boolean autoConnect = false;
	public static boolean legacyConnect = true;
	public static boolean noDisconnect = false;
	public static boolean noThreads = true;
	public static boolean permaNoThreads = false;
	public static boolean noTerminal = false;
	public static boolean headless = false;

	public static boolean GUIInput = true;

	public static Story story = null;

	public static Date lastAutoSave = new Date();

	public static boolean logStreamIsErr = false;


	private static boolean finalSetup1 = false;
	private static boolean basicSetup1 = false;

	public static boolean multiCanRun = false;

	public static DispAttack attackDisplayStyle = DispAttack.TWO_LINE1_WITH_KEY;
	public static boolean advancedCombatDisplay = false;
	public static boolean doTutorial;
	public static boolean displayTravelText;
	public static boolean displayFlavorText;
	public static boolean displayLocationalText;
	public static boolean displayFeatureText;
	public static boolean displayOwnName;
	public static boolean displayOtherCombat;
	public static boolean showLargeTimePassing;
	public static boolean delayWaits;
	public static boolean combatWaits;
	public static boolean displayTargetSummary;
	public static boolean displayNodeDeeper;
	public static boolean displayFeatureFluff;
	public static boolean combatFeedbackNotes;

	private static boolean doAutoSave = true;
	private static PrintStream logStream;

	private static Properties prefs;
	private static File prefFile;

	public enum DispAttack{
		CLASSIC("Classic simple table, delay instead of cooldown and warmup"),
		SIMPLIFIED("Displays only accuracy, delay, damage, and wounds."),
		TWO_LINE1_WITH_KEY("Current version, hybrid table with per-cell labels instead of a header. Includes a key/legend."),
		TWO_LINE1("Current version, hybrid table with per-cell labels instead of a header. Does not include key/legend");

		private String desc;
		DispAttack(String _desc){
			desc = _desc;
		}

		public String desc() {
			return desc;
		}

	}

	public static DispAttack dispAttackLookup(String str) {
		for (DispAttack da: DispAttack.values()) {
			if (da.name().equals(str)) {
				return da;
			}
		}
		return null;
	}

	public static void mainMenu() {
		//log("gen main menu 0");
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				//log("gen main menu 1");
				extra.changePrint(false);
				//log("gen main menu 2");
				Networking.richDesc("Character Select");
				Networking.sendStrong("Visual|MainMenu|");

				//log("gen main menu 3");
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Play Game";
					}

					@Override
					public boolean go() {
						Networking.clearSides();
						gameTypes();
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Load Trawel Save";
					}

					@Override
					public boolean go() {
						forceSetup();
						Networking.clearSides();
						for (int i = 1; i < 9; i++) {
							extra.println(i+" slot: "+WorldGen.checkNameInFile(""+i));
						}
						extra.println("9 autosave: "+WorldGen.checkNameInFile("auto"));
						int in = extra.inInt(9);
						WorldGen.load(in == 9 ? "auto" : in+"");
						boolean runit;
						try {
							Player.player.getPerson();
							runit = true;
						}catch (Exception e) {
							runit = false;
						}
						if (runit) {
							adventureBody();
						}
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Changelog";
					}

					@Override
					public boolean go() {
						do {
							extra.println(changelog[changelogViewer++]);
							changelogViewer%=changelog.length;
							extra.println("1 more");
							extra.println("9 back");
						}while(extra.inInt(1,true, true) == 1);
						
						return false;
					}});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return " ";
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Menu Tutorial";
					}

					@Override
					public boolean go() {
						extra.println("In Trawel, you navigate the game world, and make choices, with numbered menu lists.");
						extra.println("In Trawel proper (some subgames differ), there will never be an option presented that is greater than 9 or less than 1. Some newer menus have scrolling, but they will only ever show a max of 9 options at a time.");
						extra.println("If you're playing from Steam, you can click these options, press a number key (numpad or number row), or even use a controller. If you're using the terminal, you'll have to input the number and then press enter.");
						extra.println("Terminal Only: The 'command prompt' also lets you input 0 to attempt to leave the current menu, and 10 to attempt to leave up to 10 menus. This will only work if there is a 'pinned' back option, and that option isn't considered to lead in circles. Usually you'll end up at the town screen, but older menus might not have a pinned back out, some don't have backing out by design, and others wouldn't make sense to back out of. It will also 'break' the backing out if the state changes, for example after you finish looting, it won't back you out of where you're looting unless you input it again.");
						extra.println("Graphical Only: The graphical client has a few bonus features not immediately evident. You can press the scroll wheel to automatically scroll to the bottom. The 'Jump' option lets you have the game scroll, print new text without scrolling, or jump instantly on new text. 'Fancy' mode disables some rendering that might bug on certain hardware, and is generally less intensive. You can use the arrow keys to scroll and WASD/enter to input on keyboard. On controller, 'dpad' right/A will select the current option and 'dpad' left/B will select the last option. 'dpad' up and down change options.");
						extra.println("Tips: Many modern menus in Trawel are designed so that as many options as possible occupy the same number each time you enter them. In some menus, the options change so much that this isn't possible, or there is an edge case where this rule can't apply. But the Player menu is very stable within each Trawel release, so you can navigate that without needing to read (or listen to) the contents.");
						extra.println("Most older menus do not behave like this, and are slowly being converted.");
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Display Options";
					}

					@Override
					public boolean go() {
						advancedDisplayOptions();
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Credits";
					}

					@Override
					public boolean go() {
						credits();
						return false;
					}});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return " ";
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "GitHub (opens in browser)";
					}

					@Override
					public boolean go() {
						openWebpage("https://github.com/realDragon11/Trawel-Text");
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Advanced Options";
					}

					@Override
					public boolean go() {
						advancedOptions();
						return false;
					}});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return " ";
					}});
				mList.add(new MenuLast() {

					@Override
					public String title() {
						return "Exit";
					}

					@Override
					public boolean go() {
						System.exit(0);
						return false;
					}});

				return mList;
			}
		});
	}

	//MAYBELATER: for now, options are only global and there is no 'per save' override
	public static void advancedDisplayOptions() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Changing these display options will change them for all saves.";
					}}
						);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return doTutorial +" extra Tutorial Text";
					}

					@Override
					public boolean go() {
						doTutorial = !doTutorial;
						prefs.setProperty("tutorial",doTutorial+"");
						return false;
					}});

				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Attack Display Style: " + attackDisplayStyle.name();
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
										return "Currently: " + attackDisplayStyle.name();
									}});
								for (DispAttack da: DispAttack.values()) {
									list.add(new MenuSelect() {
										@Override
										public String title() {
											return da.name() + ": " + da.desc();
										}

										@Override
										public boolean go() {
											attackDisplayStyle = da;
											prefs.setProperty("attack_display",attackDisplayStyle.name());
											return true;
										}});
								}
								list.add(new MenuBack("Cancel."));
								return list;
							}});
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Change Display Chars ("+extra.current_display_style+")";
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
										return "Current Style: " + extra.current_display_style;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Accuracy Hit: " +extra.CHAR_HITCHANCE
												+ " Time Instants: " + extra.CHAR_INSTANTS;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										String dams = "";
										for (DamageType dt: DamageType.values()) {
											dams += dt.getExplain() +"; "; 
										}
										return dams;
										//return "Sharp: " +extra.CHAR_SHARP + " Blunt: " + extra.CHAR_BLUNT + " Pierce: "+extra.CHAR_PIERCE;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Weight: " +extra.DISP_WEIGHT
												+ " Aether: " + extra.DISP_AETHER
												+ " Restiction Agility Multiplier Penalty: " +extra.DISP_AMP
												+ " Qualities: " + extra.DISP_QUALS;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "more than 75% HP: " +extra.HP_I_FULL
												+ "; more than 50% HP: " + extra.HP_I_MOSTLY
												+ "; more than 25% HP: " +extra.HP_I_HALF
												+ "; more than 0% HP: " + extra.HP_I_SOME
												+ "; dead: " + extra.HP_I_DEAD
												;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return ">= 75% damage: " +extra.DAM_I_KILL
												+ "; >= 50% damage: " + extra.DAM_I_HEAVY
												+ "; >= 25% damage: " +extra.DAM_I_SOME
												+ "; <25% damage: " + extra.DAM_I_NONE;

									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Visual (Full ASCII)";
									}

									@Override
									public boolean go() {
										extra.charSwitchVisual();
										prefs.setProperty("char_style","visual");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Narrator (Narrator friendly ASCII only)";
									}

									@Override
									public boolean go() {
										extra.charSwitchNarrator();
										prefs.setProperty("char_style","narrator");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Emote (Unicode)";
									}

									@Override
									public boolean go() {
										extra.charSwitchEmote();
										prefs.setProperty("char_style","emote");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "None (Visual without HP/Damage Indicators)";
									}

									@Override
									public boolean go() {
										extra.charSwitchNone();
										prefs.setProperty("char_style","none");
										return false;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "In the future there will also be an option that reads from a file.";
									}});
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Reduce Flavor and Indicator Text";
					}

					@Override
					public boolean go() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Flavor Text " + displayFlavorText + " (Covers random town flavor, 'first arrival' flavor, and random taunting/boasting)";
							}

							@Override
							public boolean go() {
								displayFlavorText = !displayFlavorText;
								prefs.setProperty("flavor_text", displayFlavorText+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Travel Text " + displayTravelText+ " (covers 'You start to travel to X' and the arrival message.)";
							}

							@Override
							public boolean go() {
								displayTravelText = !displayTravelText;
								prefs.setProperty("travel_text", displayTravelText+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Locational Text " + displayLocationalText + " (covers 'You are in X' and date.)";
							}

							@Override
							public boolean go() {
								displayLocationalText = !displayLocationalText;
								prefs.setProperty("locational_text", displayLocationalText+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Feature Text " + displayFeatureText + " (Diplays type of town Feature after its name.)";
							}

							@Override
							public boolean go() {
								displayFeatureText = !displayFeatureText;
								prefs.setProperty("feature_text", displayFeatureText+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Own Name " + displayOwnName + " (If disabled, 'YOU' will be used whenever your character's name, title, or fullname would be used. This will not make sense, but might help clarity.)";
							}

							@Override
							public boolean go() {
								displayOwnName = !displayOwnName;
								prefs.setProperty("ownname_text", displayOwnName+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Other Combat Text " + displayOtherCombat + " (If disabled, no text will be displayed on Person turns that do not include you as an attacker or defender. Does not disable Combat Conditions text, and does disable death messages.)";
							}

							@Override
							public boolean go() {
								displayOtherCombat = !displayOtherCombat;
								prefs.setProperty("othercombat_text", displayOtherCombat+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Target Summary " + displayTargetSummary + " (If disabled, will not print target's stats in 1v1s or before attacking in mass battles.)";
							}

							@Override
							public boolean go() {
								displayTargetSummary = !displayTargetSummary;
								prefs.setProperty("targetsummary_text", displayTargetSummary+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Node Deeper " + displayNodeDeeper + " (Inward and Outward in Node Features.)";
							}

							@Override
							public boolean go() {
								displayNodeDeeper = !displayNodeDeeper;
								prefs.setProperty("nodedeeper_text", displayNodeDeeper+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Feature Fluff " + displayFeatureFluff + " (Such as entering and exiting stores with sayings.)";
							}

							@Override
							public boolean go() {
								displayFeatureFluff = !displayFeatureFluff;
								prefs.setProperty("featurefluff_text", displayFeatureFluff+"");
								return false;
							}
						});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Combat Notes " + combatFeedbackNotes + " (Displays more detailed breakdowns of special triggers after attacks.)";
							}

							@Override
							public boolean go() {
								combatFeedbackNotes = !combatFeedbackNotes;
								prefs.setProperty("combatnotes_text", combatFeedbackNotes+"");
								return false;
							}
						});
						extra.menuGo(new ScrollMenuGenerator(list.size(), "last <> options", "next <> options") {
							@Override
							public List<MenuItem> forSlot(int i) {
								return Collections.singletonList(list.get(i));
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return advancedCombatDisplay +" Debug Combat Display (HP per attack, attack result notes)";
					}

					@Override
					public boolean go() {
						advancedCombatDisplay = !advancedCombatDisplay;
						prefs.setProperty("debug_attacks",advancedCombatDisplay+"");
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Graphical only Realtime Displays";
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
										return "These options will wait time in real life after time 'passes' in game, before continuing with the displays.";
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Realtime waiting for World time: " +showLargeTimePassing;
									}

									@Override
									public boolean go() {
										showLargeTimePassing = !showLargeTimePassing;
										prefs.setProperty("largetime_wait",showLargeTimePassing+"");
										return false;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "World Time is the time that passes in the game world. If this option is on, the game will attempt to show 6 hours every second as it catches up instead of catching up as fast as it can compute. Note that the game doesn't always catch up right away. If more than 24 hours are passing, the amount of time shown per second will slowly increase until all time is shown.";
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Realtime waiting for Combat Delay: " +delayWaits;
									}

									@Override
									public boolean go() {
										delayWaits = !delayWaits;
										prefs.setProperty("combattime_wait",delayWaits+"");
										return false;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Waiting for Combat Delay will attempt to show 100 instants as 1 second of real world time. It is suggested to have either this or the next option on, but not both.";
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Combat Action Pausing: " +combatWaits;
									}

									@Override
									public boolean go() {
										combatWaits = !combatWaits;
										prefs.setProperty("combataction_wait",combatWaits+"");
										return false;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Combat Action pausing will wait after displaying a combat attack. It will wait half a second normally, but if you're in a mass battle, it will attempt to wait 4/5ths of a second when you're being attacked. Unlike delay based timing, it doesn't consider your own attacks valid things to wait on. It is suggested to have either this or the previous option on, but not both.";
									}});
								list.add(new MenuBack());
								return list;
							}});
						return false;
					}});

				mList.add(new MenuBack());
				return mList;
			}
		});
		try (FileWriter fw = new FileWriter(prefFile)){
			prefs.store(fw, null);
			extra.println("Prefs saved.");
		} catch (Exception e) {
			extra.println("Was not able to save prefs.");
		}

	}

	private static void advancedOptions() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "These options deal with reconnecting to Trawel Graphical, and only apply to the Steam version. They only work on the command line, otherwise you may have to restart or use the command line to finish reconnecting.";
					}}
						);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Quick Reconnect";
					}

					@Override
					public boolean go() {
						if (autoConnect) {
							extra.println(extra.PRE_ORANGE+"This option will break your connection! Really continue?");
							if (!extra.yesNo()) {
								return false;
							}
						}
						Networking.connect(6510,true);
						extra.println("Test Connection!");
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Full Reconnect";
					}

					@Override
					public boolean go() {
						if (autoConnect) {
							extra.println(extra.PRE_ORANGE+"This option will break your connection! Really continue?");
							if (!extra.yesNo()) {
								return false;
							}
						}
						extra.println("Port?");
						Networking.connect(extra.inInt(65535),true);
						extra.println("Test Connection!");
						return true;
					}});
				mList.add(new MenuBack());
				return mList;
			}
		});
	}


	private static void credits() {
		extra.println("Made by 'dragon' of realDragon");
		extra.println();
		
		extra.println("Special thanks to many different communities across different programming languages and engines.");
		extra.println();
		
		extra.println(extra.STAT_HEADER+"Music:");
		extra.println(" manicInsomniac");
		extra.println();
		
		extra.println(extra.STAT_HEADER+"Java Libraries:");
		extra.println(" Esoteric Software's Kryo");
		extra.println(" Yellowstone Games' SquidSquad family");
		extra.println(" All dependencies of prior libraries");
		extra.println();
		
		extra.println(extra.STAT_HEADER+"Graphical Libraries:");
		extra.println(" JujuAdams' Scribble");
		extra.println(" Alynne's Input");
		extra.println(" Pixelated Pope's Retro Palette Swapper");
		extra.println();
		
		extra.println(extra.STAT_HEADER+"Art:");
		extra.println(extra.ITEM_DESC_PROP+" Character, armor, and Weapon Art:");
		extra.println("  Most Character Art: SmashCooper");
		extra.println("  Some Weapons: Duster");
		extra.println("  Scars, Weapon Placeholders: Jacobs");
		extra.println(extra.ITEM_DESC_PROP+" Background Art:");
		extra.println("  Damrok");
		extra.println(extra.ITEM_DESC_PROP+" Concept/Splash Art:");
		extra.println("  TamLinArt");
		extra.println();
		
		extra.println(extra.STAT_HEADER+"Sounds:");
		extra.println(" Stock Media provided by Soundrangers / FxProSound / SoundIdeasCom / PrankAudio / hdaudio / agcnf_media / sounddogs / AbloomAudio / Yurikud / SoundMorph => through Pond5");
		extra.println(" Metal sounds by Bluezone");
		extra.println(" Clicking sound: One of the many CC0 stapler sounds on FreeSound");
		extra.println();
		
		extra.println(extra.STAT_HEADER+"Steam Assets:");
		
		extra.println("Achievement icons can be found on game-icons.net, modified using their website. They are CC BY 3.0");
		extra.println(extra.ITEM_DESC_PROP+"Game-Icons.net list:");
		extra.println(
				"Lorc, http://lorcblog.blogspot.com; "
				+ "Delapouite, https://delapouite.com; "
				+ "John Colburn, http://ninmunanmu.com; "
				+ "Felbrigg, http://blackdogofdoom.blogspot.co.uk; "
				+ "John Redman, http://www.uniquedicetowers.com; "
				+ "Carl Olsen, https://twitter.com/unstoppableCarl; "
				+ "Sbed, http://opengameart.org/content/95-game-icons; "
				+ "PriorBlue; "
				+ "Willdabeast, http://wjbstories.blogspot.com; "
				+ "Viscious Speed, http://viscious-speed.deviantart.com; "
				+ "Lord Berandas, http://berandas.deviantart.com; "
				+ "Irongamer, http://ecesisllc.wix.com/home; "
				+ "HeavenlyDog, http://www.gnomosygoblins.blogspot.com; "
				+ "Lucas; "
				+ "Faithtoken, http://fungustoken.deviantart.com; "
				+ "Skoll; "
				+ "Andy Meneely, http://www.se.rit.edu/~andy/; "
				+ "Cathelineau; "
				+ "Kier Heyl; "
				+ "Aussiesim; "
				+ "Sparker, http://citizenparker.com; "
				+ "Zeromancer; "
				+ "Rihlsul; "
				+ "Quoting; "
				+ "Guard13007, https://guard13007.com; "
				+ "DarkZaitzev, http://darkzaitzev.deviantart.com; "
				+ "SpencerDub; "
				+ "GeneralAce135; "
				+ "Zajkonur; "
				+ "Catsu; "
				+ "Starseeker; "
				+ "Pepijn Poolman; "
				+ "Pierre Leducq; "
				+ "Caro Asercion; "
				+ "");
	}

	@SuppressWarnings("unused")
	private static void baseSetup1() {
		if (!basicSetup1) {
			EnchantConstant.init();
			DrawBane.setup();
			new MaterialFactory();
			new RaceFactory();
			new TargetFactory();
			new StyleFactory();
			ArmorStyle.init();
			new Oracle().load();
			//new TauntsFactory();
			//new BookFactory();
			new BumperFactory();
			new WeaponAttackFactory();
			new TownFlavorFactory();
			new QuestReactionFactory();
			//WorldGen.initDummyInvs();
			story = new StoryNone();
			DummyInventory.dummyAttackInv = new DummyInventory();
			KyroManager.trawelRegister();
			basicSetup1 = true;
		}
	}

	public static void forceSetup() {
		baseSetup1();
		if (!finalSetup1) {
			randomLists.init();
			finalSetup1 = true;
		}
	}

	public static void unitTestSetup() {
		forceSetup();
	}


	private static void gameTypes() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "Choose a Game Mode:";
					}}
						);
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Trawel Quickstart (base game, recommended)";
					}

					@Override
					public boolean go() {
						adventure1(false,false,false,false);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Trawel Slowstart (base game, rerolling character creator)";
					}

					@Override
					public boolean go() {
						adventure1(false,true,true,true);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "rTrawel (experimental jrpg, prototype, nongraphical)";
					}

					@Override
					public boolean go() {
						rtrawel.TestRunner.run();
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "SciMechs (experimental mech game, prototype, nongraphical)";
					}

					@Override
					public boolean go() {
						SaveHandler.clean();
						while (true) {
							extra.println("Choose your mechs");
							List<Mech> mechs = mechsForSide(true);
							extra.println("Save mechs?");
							if (extra.yesNo()) {
								SaveHandler.clean();
								SaveHandler.imprintMechs(mechs);
								SaveHandler.save();
							}
							extra.println("Choose their mechs");
							mechs.addAll(mechsForSide(false));

							MechCombat mc = new MechCombat(mechs);
							mc.go();

							extra.println(mc.activeMechs.get(0).playerControlled == true ? "You win!" : "You lose!"+ "\n Would you like the quit?");
							if (extra.yesNo()) {
								return true;
							}
						}
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "various tests";
					}

					@Override
					public boolean go() {
						extra.menuGo(new MenuGenerator(){

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> mList = new ArrayList<MenuItem>();
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Cheat Trawel";
									}

									@Override
									public boolean go() {
										adventure1(true,false,false,false);
										return true;
									}});
								mList.add(new MenuLine() {

									@Override
									public String title() {
										return "Graphical Tests:";
									}}
										);
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Model Mode";
									}

									@Override
									public boolean go() {
										modelMode();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Time Test";
									}

									@Override
									public boolean go() {
										forceSetup();
										Calender.timeTest();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Scroll Menu Test";
									}

									@Override
									public boolean go() {
										scrollTest();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Stat Query";
									}

									@Override
									public boolean go() {
										weaponStatQuery();
										return true;
									}});
								/*
								mList.add(new MenuLine() {

									@Override
									public String title() {
										return "Backend Tests:";
									}}
								);

								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Power Metrics (creates csv)";
									}

									@Override
									public boolean go() {
										baseSetup1();
										try {
											WeaponAttackFactory.weaponMetrics();
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										}
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Rarity Metrics (creates csv)";
									}

									@Override
									public boolean go() {
										baseSetup1();
										try {
											Weapon.duoRarityMetrics();
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										}
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Save Test";
									}

									@Override
									public boolean go() {
										saveTest();
										return true;
									}});*/
								mList.add(new MenuBack("Back to Main Menu"));
								return mList;
							}
						});
						return true;
					}});
				mList.add(new MenuBack());
				return mList;
			}
		});
	}

	private static List<Mech> curMechs;


	private static List<Mech> mechsForSide(boolean side){
		extra.menuGoPaged(new MenuGeneratorPaged(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "load mechs (??? complexity)";
					}

					@Override
					public boolean go() {
						curMechs = SaveHandler.exportMechs();
						for (Mech m: curMechs) {
							m.playerControlled = side;
							m.swapPilot(new Pilot());
						}
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "debug mechs (70x2 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = debugMechs(side);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "three musketeers (80x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = threeMusketeers(side);
						return true;
					}});

				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "pirate squad (80x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = pirateSquad(side);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "science squad (120x3 complexity)";
					}

					@Override
					public boolean go() {
						curMechs = scienceSquad(side);
						return true;
					}});
				return mList;
			}});

		return curMechs;
	}

	private static List<Mech> debugMechs(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new DebugMech(side));
		mechs.add(new DebugMech(side));
		return mechs;
	}

	private static List<Mech> threeMusketeers(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Musketeer(side));
		mechs.add(new Musketeer(side));
		mechs.add(new Musketeer(side));
		return mechs;
	}

	private static List<Mech> pirateSquad(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Swashbuckler(side));
		mechs.add(new Packrat(side));
		mechs.add(new Pirate(side));
		return mechs;
	}

	private static List<Mech> scienceSquad(boolean side){
		List<Mech> mechs = new ArrayList<Mech>();
		mechs.add(new Dynamo(side));
		mechs.add(new Pyro(side));
		mechs.add(new Hazmat(side));
		return mechs;
	}

	private static void modelMode() {
		forceSetup();
		Person manOne;
		manOne = RaceFactory.makeOld(2);//new Person(starting_level,false,Race.RaceType.HUMANOID,null);
		new Player(manOne);
		Player.bag.swapWeapon(new Weapon(1,WeaponType.AXE));
		//Player.bag.getHand().forceEnchantHit(0);
		/*
			 player.bag.swapArmorSlot(new Armor(1,0,MaterialFactory.getMat("emerald")),0);
			 player.bag.swapArmorSlot(new Armor(1,1,MaterialFactory.getMat("emerald")),1);
			 player.bag.swapArmorSlot(new Armor(1,2,MaterialFactory.getMat("emerald")),2);
			 player.bag.swapArmorSlot(new Armor(1,3,MaterialFactory.getMat("emerald")),3);
			 player.bag.swapArmorSlot(new Armor(1,4,MaterialFactory.getMat("emerald")),4);*/
		manOne.setPlayer();
		Networking.charUpdate();
		extra.yesNo();

	}

	public static String headerText() {
		return ("Dragon's Trawel "+VERSION_STRING+VERSION_DATE)+"\r\n"+
			(
			        " ___________  ___  _    _ _____ _     \r\n" + 
					"|_   _| ___ \\/ _ \\| |  | |  ___| |    \r\n" + 
					"  | | | |_/ / /_\\ \\ |  | | |__ | |    \r\n" + 
					"  | | |    /|  _  | |/\\| |  __|| |    \r\n" + 
					"  | | | |\\ \\| | | \\  /\\  / |___| |____\r\n" + 
			       "  \\_/ \\_| \\_\\_| |_/\\/  \\/\\____/\\_____/"
			);
	}

	public static void log(String str) {
		if (extra.isMainThread()) {
			logStream.println(str);
			logStream.flush();
		}
	}
	
	public static void errLog(String str) {
		str = "ERROR: "+str;
		if (extra.isMainThread()) {
			logStream.println(str);
			logStream.flush();
		}
		if (!logStreamIsErr) {
			System.err.println(str);
		}
	}

	/**
	 * Main method. Calls the main game.
	 * @param args (Strings)
	 */
	public static void main(String[] args) {
		inEclipse = false;
		try {
			System.out.println(headerText());
			extra.setMainThread();
			logStream = new PrintStream("log.txt");
			for (String a: args) {
				if (a.toLowerCase().equals("autoconnect")){
					autoConnect = true;
				}
				if (a.toLowerCase().equals("noguiinput")){
					GUIInput = false;
				}
				if (a.toLowerCase().equals("nowarnings")) {
					logStreamIsErr = true;
					System.setErr(logStream);
				}
				if (a.toLowerCase().equals("ineclipse")) {
					inEclipse = true;
				}
				if (a.toLowerCase().equals("nodisconnect")) {
					noDisconnect = true;
				}
				if (a.toLowerCase().equals("nothreads")) {
					permaNoThreads = true;
				}
				if (a.toLowerCase().equals("gdx")) {
					legacyConnect = false;
				}
				if (a.toLowerCase().equals("quiet")) {
					noTerminal = true;
				}
				if (a.toLowerCase().equals("loud")) {
					headless = true;
				}

			}
			new Networking();

			prefs = new Properties();
			prefFile = new File("trawel_prefs.properties");
			prefFile.createNewFile();
			try (FileReader prefReader = new FileReader(prefFile)){
				prefs.load(prefReader);
				prefReader.close();
			}catch (Exception e) {
				throw e;
			}


			String charStyle = prefs.getProperty("char_style");
			if (charStyle == null) {
				if (autoConnect) {
					charStyle = "none";
				}else {
					charStyle = "narrator";
				}
			}

			switch (charStyle) {
			case "visual":
				extra.charSwitchVisual();
				break;
			case "narrator":
				extra.charSwitchNarrator();
				break;
			case "emote":
				extra.charSwitchEmote();
				break;
			case "none":
				extra.charSwitchNone();
				break;
			}
			advancedCombatDisplay = Boolean.parseBoolean(prefs.getProperty("debug_attacks","FALSE"));
			doTutorial = Boolean.parseBoolean(prefs.getProperty("tutorial","TRUE"));
			attackDisplayStyle = dispAttackLookup(prefs.getProperty("attack_display",DispAttack.TWO_LINE1_WITH_KEY.name()));
			displayTravelText = Boolean.parseBoolean(prefs.getProperty("travel_text","TRUE"));
			displayFlavorText = Boolean.parseBoolean(prefs.getProperty("flavor_text","TRUE"));
			displayLocationalText = Boolean.parseBoolean(prefs.getProperty("locational_text","TRUE"));
			displayFeatureText = Boolean.parseBoolean(prefs.getProperty("feature_text","TRUE"));
			displayFeatureFluff = Boolean.parseBoolean(prefs.getProperty("featurefluff_text","TRUE"));
			displayOwnName = Boolean.parseBoolean(prefs.getProperty("ownname_text","TRUE"));
			displayOtherCombat = Boolean.parseBoolean(prefs.getProperty("othercombat_text","TRUE"));
			showLargeTimePassing= Boolean.parseBoolean(prefs.getProperty("largetime_wait","TRUE"));
			delayWaits = Boolean.parseBoolean(prefs.getProperty("combattime_wait","FALSE"));//probably will perform poorly in mass battles
			combatWaits = Boolean.parseBoolean(prefs.getProperty("combataction_wait","TRUE"));
			displayTargetSummary = Boolean.parseBoolean(prefs.getProperty("targetsummary_text","TRUE"));
			displayNodeDeeper = Boolean.parseBoolean(prefs.getProperty("nodedeeper_text","TRUE"));
			combatFeedbackNotes = Boolean.parseBoolean(prefs.getProperty("combatnotes_text","TRUE"));

			if (autoConnect) {
				System.out.println("Please wait for the graphical to load...");
				Networking.handleAnyConnection(legacyConnect ? ConnectType.LEGACY : ConnectType.GDX);
				extra.println("Trawel, Gameplay Version "+VERSION_STRING+VERSION_DATE);
			}else {
				Networking.handleAnyConnection(ConnectType.NONE);
			}
		}catch(Exception e) {
			System.out.println("There was an error when setting up Trawel.");
			e.printStackTrace();

			errorHandle(e);
			System.out.println("Press enter to quit.");
			extra.inString();
		}
		boolean breakErr = false;
		while (!breakErr) {
			try {
				//mainGame.log("starting main menu");
				mainMenu();
			}catch (Exception e) {
				try {
					errorHandle(e);
				}catch(Exception fatal) {
					System.out.println("Error was fatal. Press enter to close.");
					breakErr = true;
				}
				if (!breakErr) {
					System.out.println("Press enter to attempt to recover from the error.");
				}
				extra.inString();
			}
		}
		//mainGame.log("exiting game");

		logStream.close();
	}
	/*
	 * really dumb how displaying a stack trace works
	 * https://stackoverflow.com/questions/1069066/how-can-i-get-the-current-stack-trace-in-java#comment34163092_1069074
	 */

	private static void errorHandle(Exception e) {
		StackTraceElement[] trace = e.getStackTrace();
		mainGame.log(e.toString());
		for (int i = 0; i < trace.length; i++) {
			mainGame.log("\t"+trace[i].toString());
		}
		System.out.println("Error Stacktrace:");
		e.printStackTrace();
		extra.println(extra.PRE_RED+"Trawel has encountered an exception. Please report to realDragon. More details can be found in log.txt and the terminal.");
		extra.println("Error Preview: "+ e.toString());
	}


	//instance methods


	/**
	 * should only call in the wild if you want a duel with no adds or summons
	 * @return combat
	 */
	public static Combat CombatTwo(Person first_man,Person second_man, World w) {
		Person holdPerson;
		boolean hasPlayer = false;
		assert !second_man.isPlayer();
		if (first_man.isPlayer()) {
			first_man.getBag().graphicalDisplay(-1,first_man);
			second_man.getBag().graphicalDisplay(1,second_man);
			Networking.setBattle(Networking.BattleType.NORMAL);
			story.startFight(false);
			extra.println("You are dueling " +second_man.getName()+".");
			hasPlayer = true;
		}else {
			if (!extra.getPrint()) {
				extra.println(first_man.getName()+" is dueling " +second_man.getName()+".");
			}
		}
		
		Combat c = new Combat(first_man,second_man, w);
		
		if (c.getNonSummonSurvivors().contains(second_man)) {
			holdPerson = second_man;
			second_man = first_man;
			first_man = holdPerson;
		}
		
		if (hasPlayer) {
			//stop combat music
			Networking.setBattle(Networking.BattleType.NONE);			
		}

		first_man.addXp(second_man.getLevel());
		
		if (second_man.isPlayer()) {
			first_man.addPlayerKill();
			Player.addXp(extra.zeroOut((int) (first_man.getLevel()*Math.floor((first_man.getMaxHp()-first_man.getHp())/(first_man.getMaxHp())))));
			dieFight();
			//clean up victor's graphics
			Networking.clearSide(1);
			
			//player cannot be looted
		}else{
			//player didn't lose, if they were in fight
			extra.println(first_man.getName() +" goes to loot " + second_man.getName() +".");
			if (first_man.isPlayer()) {
				AIClass.playerLoot(second_man.getBag(), true);
				//clean up after looting
				Networking.clearSide(1);
			}else {
				AIClass.loot(second_man.getBag(), first_man.getBag(), true,first_man,true);
			}
	
			//micro optimization- the player didn't lose, and this is a duel
			//so if this is true, first_man is the player
			//this gets used a lot in the background so every bit helps
			if (hasPlayer) {
				Player.player.duel_wins++;
				Networking.leaderboard("most_duel_wins", Player.player.duel_wins);
				//only applies in true 1v1's
				if (Player.player.duel_wins == 1) {
					Player.player.addAchieve("dueling", "Dueler");
				}
				if (Player.player.duel_wins == 10) {
					Player.player.addAchieve("dueling", "Duelist");
				}
				if (Player.player.duel_wins == 50) {
					Player.player.addAchieve("dueling", "Veteran Duelist");
				}
				if (Player.player.duel_wins == 100) {
					Player.player.addAchieve("dueling", "Master Duelist");
				}
				if (Player.player.duel_wins == 1000) {
					Player.player.addAchieve("dueling", "Grandmaster Duelist");
				}
				//only player kill deaths are added since it will be assumed others will stick
				second_man.addDeath();
				if (second_man.getBag().getRace().racialType == Race.RaceType.PERSONABLE) {
					//don't cheat death against not-player killers
					int deaths = second_man.getDeaths();
					int pKills = second_man.getPlayerKills();
					//max revive chance decreases the more they die, even if they have infinite kills on you, to reduce annoyance
					if (
							(deaths == 1 && extra.randFloat() > .97)//~3% chance if this is our only death
							|| (pKills > 0 && deaths < 4 && extra.chanceIn(Math.min((4*deaths)+pKills,(pKills*2)+1), pKills+(5*deaths)))
							//killing the player is effective
							//we can at most get a (X-1)/x chance
							//can cheat death a max of 3 times
							) {
						w.addDeathCheater(second_man);//dupes don't happen since in this case the dupe is instantly removed in the wander code
						second_man.hTask = HostileTask.REVENGE;
					}
				}
				story.winFight(false);
			}
		}

		//clean up effects in case they get reused
		first_man.clearBattleEffects();
		second_man.clearBattleEffects();

		return c;
	}

	public static Combat HugeBattle(World w, List<List<Person>> people){
		return HugeBattle(w,null,people,true);
	}

	public static Combat HugeBattle(World w,List<SkillCon> cons, List<List<Person>> people, boolean canLoot){
		Combat battle = new Combat(w,cons,people);
		Comparator<Person> levelSorter = new Comparator<Person>(){//sort in descending order
			@Override
			public int compare(Person arg0, Person arg1) {
				if (arg0.getLevel() == arg1.getLevel()) {
					//there can only be one player, multiplayer would need a bigger re-write
					//player wins ties
					if (arg0.isPlayer()) {
						return 1;
					}
					if (arg1.isPlayer()) {
						return -1;
					}
					return 0;
				}
				if (arg0.getLevel() < arg1.getLevel()) {
					return -1;
				}
				return 1;
			}

		};
		List<Person> lives = battle.getAllSurvivors();
		battle.killed.sort(levelSorter);
		lives.sort(levelSorter);


		int killLevelTotal = 0;
		int winSide = -1;
		int[] xpTotalList = new int[people.size()];
		int[] xpDeadList = new int[people.size()];
		int[] xpAverage = new int[people.size()];
		int xpHighestKill = battle.killed.get(0).getLevel();
		int xpHighestAverage = 0;//excludes winning side
		int xpSideHigh = -1;
		int xpLowestAverage = Integer.MAX_VALUE;//excludes winning side
		int xpSideLow = -1;

		for (int i = people.size()-1;i >=0;i--) {
			xpTotalList[i] = 0;
			xpDeadList[i] = 0;
			if (people.get(i).contains(lives.get(0))) {
				winSide = i;
			}
			for (Person p: people.get(i)) {
				int lvl = p.getLevel();
				if (battle.killed.contains(p)) {
					xpDeadList[i]+=lvl;
				}
				xpTotalList[i]=lvl;
			}
			xpAverage[i] = xpTotalList[i]/people.get(i).size();
			if (i != winSide) {
				if (xpAverage[i] <= xpLowestAverage) {
					xpLowestAverage = xpAverage[i];
					xpSideLow = i;
				}
				if (xpAverage[i] >= xpHighestAverage) {
					xpHighestAverage = xpAverage[i];
					xpSideHigh = i;
				}
				//we use >= and <= so that if they're all the same it will pick the last one for both, so we can compare xpSideHigh == xpSideLow for that
			}
		}

		assert winSide >= 0;
		assert xpSideHigh >= 0;
		assert xpSideLow >= 0;


		int xpReward = xpHighestKill;//by default it's just the highest level of the killed list
		boolean bypassLevelCap = false;
		boolean playerIsLooting = false;
		
		if (people.size() == 2) {//if a XvX battle
			assert xpSideHigh == xpSideLow;
			if (people.get(winSide).size() == 1) {// if it was a 1vX
				xpReward = xpDeadList[xpSideHigh];
				if (xpAverage[winSide]-1 < xpHighestAverage) {//if it was a 1vX where average level of X side was at least winside level minus one
					bypassLevelCap = true;
					//the solo winner gets xp equal to total dead level instead of just the best one
					//because the 1vX was roughly fair
					//otherwise, still capped at their level to avoid kicking a bunch of level 1 wolves being good
				}
			}
		}

		//NOTE: player does not get first pick unless they were highest level, they do win ties
		lives = battle.getNonSummonSurvivors();//summons count for xp but not loot
		for (Person surv: lives){
			surv.clearBattleEffects();
			int subReward = xpReward;
			if (!bypassLevelCap && subReward > surv.getLevel()) {
				subReward = surv.getLevel();
			}
			boolean isPlayer = surv.isPlayer();
			if (isPlayer) {
				Networking.setBattle(Networking.BattleType.NONE);
				AIClass.playerStashOldItems();
				playerIsLooting = true;
			}
			surv.addXp(subReward);
			if (canLoot) {
				for (Person kill: battle.killed) {
					if (kill.isPlayer()) {
						continue;//skip
					}
					if (kill.getFlag(PersonFlag.PLAYER_LOOT_ONLY) && !isPlayer) {
						continue;//skip
					}
					if (isPlayer) {//if the surv examining this kill is the player
						kill.getBag().graphicalDisplay(1,kill);
						AIClass.playerLoot(kill.getBag(), false);
						Networking.clearSide(1);
					}else {
						AIClass.loot(kill.getBag(),surv.getBag(),false,surv,true);
					}
				}
				if (isPlayer && Player.player.getPerson().getFlag(PersonFlag.AUTOLOOT)) {
					AIClass.playerDispLootChanges();
				}
			}

		}

		int gold = 0;
		int aether = 0;
		Item isell = null;
		for (Person kill: battle.killed) {
			kill.clearBattleEffects();
			if (kill.isPlayer()) {
				Networking.setBattle(Networking.BattleType.NONE);
				Networking.clearSide(1);
				dieFight();
				continue;
			}
			if (kill.getFlag(PersonFlag.PLAYER_LOOT_ONLY) && !playerIsLooting) {
				continue;//skip
			}
			gold += kill.getBag().getGold();
			aether += kill.getBag().getAether();
			for (int i = 0;i<5;i++) {
				isell = kill.getBag().getArmorSlot(i);
				if (isell.canAetherLoot()) {
					aether +=isell.getAetherValue();
				}
			}
			isell = kill.getBag().getHand();
			if (isell.canAetherLoot()) {
				aether +=isell.getAetherValue();
			}
			//DOLATER: none of these people have their aether, money or items taken because it is assumed they won't be used again
		}

		if (canLoot){
			if (lives.size() > 1) {
				List<Person> moneyLootList = new ArrayList<Person>();
				for (Person p: lives) {
					if (p.isPersonable() || p.getFlag(PersonFlag.HAS_WEALTH)) {
						moneyLootList.add(p);
					}
				}
				int giveGold = moneyLootList.size() > 0 ? gold/moneyLootList.size() : 0;
				int giveAether = aether/lives.size();
				if (!extra.getPrint()) {
					if (giveGold > 0) {
						extra.println("The remaining " +World.currentMoneyDisplay(gold) +" is divvied up, "+giveGold +" each.");
					}
					if (giveAether > 0) {
						extra.println("The remaining " + aether +" aether is divvied up, "+giveAether +" each.");
					}
				}
				
				for (Person surv: lives){
					if (giveGold > 0 && (surv.isPersonable() || surv.getFlag(PersonFlag.HAS_WEALTH))) {
						surv.getBag().addGold(giveGold);
					}
					if (giveAether > 0) {
						surv.getBag().addAether(giveAether);
					}
					if (surv.isPlayer()) {
						Networking.setBattle(Networking.BattleType.NONE);
						Networking.clearSide(1);
						mainGame.story.winFight(true);
					}
				}
			}else {
				if (lives.size() > 0) {
					Person looter = lives.get(0);
					if (gold > 0 && (looter.isPersonable() || looter.getFlag(PersonFlag.HAS_WEALTH))) {
						extra.println(looter.getName() + " claims the remaining " +World.currentMoneyDisplay(gold) +".");
						looter.getBag().addGold(gold);
					}
					if (aether > 0) {
						extra.println(looter.getName() + " claims the remaining " +aether +" aether.");
						looter.getBag().addAether(aether);
					}

					if (looter.isPlayer()) {
						Networking.setBattle(Networking.BattleType.NONE);
						Networking.clearSide(1);
						mainGame.story.winFight(true);
					}
				}
				//MAYBELATER: all that loot will get atom smashed for now
			}
		}

		battle.endaether = aether + (100 *gold);
		return battle;
	}

	public static void adventureBody() {
		lastAutoSave = new Date();
		Player.isPlaying = true;
		extra.mainThreadDataUpdate();
		while(Player.isPlaying) {
			checkAutosave();
			Player.player.getLocation().atTown();
			globalPassTime();
		}
		multiCanRun = false;
		extra.println("You do not wake up.");
	}
	
	public static void checkAutosave() {
		if (doAutoSave && (new Date().getTime()-lastAutoSave.getTime() > 1000*60*2)) {
			extra.println("Autosaving...");
			WorldGen.plane.prepareSave();
			WorldGen.save("auto");
			lastAutoSave = new Date();
		}
	}

	/**
	 * note that some events, like the player generating gold, ignore normal restrictions
	 */
	public static void globalPassTime() {
		if (showLargeTimePassing && Networking.connected()) {
			boolean largeTimePassing = false;
			if (Player.peekTime() > 24) {
				largeTimePassing = true;
				extra.println("Passing "+extra.F_WHOLE.format(Player.peekTime())+" hours.");
			}
			double passAmount = .5;
			while (Player.peekTime() > 0) {
				globalPassTimeUpTo(passAmount);
				if (largeTimePassing) {
					passAmount+=.005;
				}
				/*
				if (Player.player.atFeature != null) {
					Networking.updateTime();
					Player.player.atFeature.sendBackVariant();
				}else {
					Player.player.getLocation().sendBackVariant();
				}*/
				Networking.updateTime();
				Networking.waitIfConnected(84);//1 second should be around 6 hours
			}
		}else {
			if (Player.peekTime() > 0) {
				globalTimeCatchUp();
				Networking.updateTime();
			}
		}

	}

	public static void globalTimeCatchUp() {
		double time = Player.popTime();
		WorldGen.plane.advanceTime(time);
	}

	public static void globalPassTimeUpTo(double limit) {
		double time = Player.takeTime(limit);
		if (time > 0) {
			WorldGen.plane.advanceTime(time);
		}
	}


	public static void adventure1(boolean cheaty, boolean displayFight, boolean rerolls, boolean advancedDisplay){
		baseSetup1();
		if (!finalSetup1) {
			randomLists.init();
			finalSetup1 = true;
		}
		Networking.richDesc("Character Select");
		World world = null;//WorldGen.eoano();
		if (cheaty || rerolls) {
			if (cheaty) {
				story = new StoryNone();
			}else {
				extra.println("Skip tutorial?");
				if (extra.yesNo()) {
					story = new StoryNone();
				}else {
					story = new StoryTutorial();
				}
			}
		}else {
			story = new StoryTutorial();
		}
		Person manOne = null, manTwo;
		Player player;
		//
		while (manOne == null) {
			if (world == null) {
				extra.println("Generating world...");
				world = WorldGen.eoano();
				WorldGen.finishPlane(WorldGen.plane);
				extra.getThreadData().world = world;//init
			}
			manOne = RaceFactory.makePlayerValid(!rerolls);
			manTwo = RaceFactory.makePlayerValid(!rerolls);
			if (!displayFight) {
				extra.changePrint(true);
			}
			Combat c = CombatTwo(manOne,manTwo,null);
			manOne = c.getNonSummonSurvivors().get(0);
			story.setPerson(c.killed.get(0), 0);
			//story.setPerson(manTwo, 0);
			if (!displayFight) {
				extra.changePrint(false);
			}
			if (rerolls) {
				manOne.getBag().graphicalDisplay(1, manOne);
				if (advancedDisplay) {
					manOne.displayStats(false);
				}
				extra.println("Play as " + manOne.getName() +"?");
				if (extra.yesNo()) {
					break;
				}
				manOne = null;
			}
			Networking.clearSides();
		}
		Networking.clearSides();
		player = new Player(manOne);
		manOne.setPlayer();
		//Networking.send("Visual|Race|" + manOne.getBag().getRace().name+  "|");
		Networking.charUpdate();
		assert player.getPerson().getFeatPoints() > 0;
		if (!rerolls) {
			//if they didn't get assigned one by the racial system, pick them randomly now
			//note that this might pick a feat instead of an archetype, which is fine
			//this will let the player actually get to see the 'second archetype' pick system in action
			while (player.getPerson().getFeatPoints() > 0) {
				player.getPerson().pickFeatRandom();
			}
			player.fillSkillConfigs();//fill any skill configs
		}else {
			Archetype.menuChooseFirstArchetype(manOne);
			//now they get to pick a second one, because slowstart doesn't care about racial sterotypes
			Archetype.menuChooseSecondArchetype(manOne);
		}
		player.setLocation(world.getStartTown());//also sets the player world
		if (cheaty) {
			Player.player.setCheating();
			story = new StoryNone();
			//player.getPerson().addXp(9999);
			Player.player.addGold(1000);
			Player.bag.addAether(100000);
		}
		story.storyStart();
		player.storyHold = story;

		WorldGen.plane.setPlayer(player);

		multiCanRun = true;

		//player.getPerson().playerLevelUp();
		adventureBody();
	}



	public static void openWebpage(String urlString) {
		try {
			Desktop.getDesktop().browse(new URL(urlString).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void die(String deathMessage) {
		Networking.sendStrong("StatUp|deaths|1|");
		Networking.leaderboard("most_deaths",++Player.player.deaths);
		story.onDeath();
		extra.println(deathMessage);
		story.onDeathPart2();
		extra.endBackingSegment();
	}

	public static void dieMisc() {
		die(extra.choose("You rise from death...","You return to life.","You walk again!","You rise from the grave!","Death releases its hold on you."));
	}
	
	public static void dieFight() {
		if (extra.chanceIn(1,3)) {
			dieMisc();
			return;
		}
		die(extra.choose("You revive after the battle.","You right your repaired body.","You stand after your corpse pulls itself together."));
	}

	public static void scrollTest() {
		extra.menuGo(new ScrollMenuGenerator(30, "back <> more","forward (<> left)") {

			@Override
			public List<MenuItem> header() {
				return null;
			}

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return " hi " + i;
					}

					@Override
					public boolean go() {
						return true;
					}});
				return list;
			}

			@Override
			public List<MenuItem> footer() {
				return null;
			}
		});
		extra.menuGo(new ScrollMenuGenerator(16,"a <>", "b<>") {

			@Override
			public List<MenuItem> header() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "dead header";
					}});
				return list;
			}

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return " bye " + i;
					}

					@Override
					public boolean go() {
						return false;
					}});
				return list;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return " live footer";
					}

					@Override
					public boolean go() {
						return true;
					}});
				return list;
			}
		});
	}

	private static List<Material> weaponMatTesterList;

	public static void weaponStatQuery() {
		forceSetup();

		List<Material> standardList = new ArrayList<Material>();
		standardList.add(MaterialFactory.getMat("iron"));
		standardList.add(MaterialFactory.getMat("steel"));
		standardList.add(MaterialFactory.getMat("gold"));
		standardList.add(MaterialFactory.getMat("adamantine"));

		MenuGenerator weapMenu = new ScrollMenuGenerator(WeaponType.values().length,"previous <> weapons", "next <> weapons") {

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list= new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return WeaponType.values()[i].toString();
					}

					@Override
					public boolean go() {
						WeaponAttackFactory.dispTestWeapon(1,WeaponType.values()[i],weaponMatTesterList);
						WeaponAttackFactory.dispTestWeapon(10,WeaponType.values()[i],weaponMatTesterList);
						WeaponAttackFactory.dispTestWeapon(100,WeaponType.values()[i],weaponMatTesterList);
						return false;
					}});
				return list;
			}

			@Override
			public List<MenuItem> header() {
				return null;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuBack("back"));
				return list;
			}};
			List<Material> allWeapMats = new ArrayList<Material>();
			MaterialFactory.matList.stream().filter(M->M.weapon).forEach(allWeapMats::add);

			MenuGenerator matMenu = new ScrollMenuGenerator(allWeapMats.size(),"previous <> materials", "next <> materials") {

				@Override
				public List<MenuItem> forSlot(int i) {
					List<MenuItem> list= new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return allWeapMats.get(i).name;
						}

						@Override
						public boolean go() {
							weaponMatTesterList = Collections.singletonList(allWeapMats.get(i));
							extra.menuGo(weapMenu);
							return false;
						}});
					return list;
				}

				@Override
				public List<MenuItem> header() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "standard";
						}

						@Override
						public boolean go() {
							weaponMatTesterList = standardList;
							extra.menuGo(weapMenu);
							return false;
						}});
					return list;
				}

				@Override
				public List<MenuItem> footer() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuBack("exit"));
					return list;
				}};
		extra.menuGo(matMenu);
	}


}
