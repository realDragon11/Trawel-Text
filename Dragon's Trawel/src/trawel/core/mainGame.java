package trawel.core;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import scimech.handlers.SaveHandler;
import scimech.handlers.SciRunner;
import trawel.arc.misc.Changelog;
import trawel.arc.story.Story;
import trawel.arc.story.StoryNone;
import trawel.arc.story.StoryTutorial;
import trawel.battle.Combat;
import trawel.battle.attacks.ImpairedAttack.DamageType;
import trawel.battle.targets.TargetFactory;
import trawel.core.Networking.ConnectType;
import trawel.battle.attacks.StyleFactory;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.LootTables;
import trawel.helper.methods.randomLists;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Archetype;
import trawel.personal.classless.Feat;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.item.DummyInventory;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Player;
import trawel.quests.events.BumperFactory;
import trawel.quests.events.QuestReactionFactory;
import trawel.threads.ThreadData;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.Calender;
import trawel.towns.data.FeatureData;
import trawel.towns.data.TownFlavorFactory;
import trawel.towns.data.WorldGen;
import trawel.towns.features.services.Oracle;
/**
 * 
 * @author dragon
 * 2/5/2018
 * Entry point for compiled game.
 */
public class mainGame {
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

	public static Date lastAutoSave = new Date();

	public static boolean logStreamIsErr = false;


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
	public static boolean extendedTargetSummary;
	public static boolean displayNodeDeeper;
	public static boolean displayFeatureFluff;
	public static boolean combatFeedbackNotes;
	public static boolean saveText;
	public static boolean lineSep;
	public static boolean displayAutoBattle;
	
	public static boolean cdAutoLoot;
	public static boolean cdAutoLevel;
	public static boolean cdAutoBattle;
	public static boolean cdAutoSip;
	public static boolean cdAutoRecord;
	
	
	public static GraphicStyle graphicStyle;

	private static boolean doAutoSave = true;
	private static PrintStream logStream;

	private static Properties prefs;
	private static File prefFile;

	public enum DispAttack{
		CLASSIC("Classic","Classic small table, delay instead of cooldown and warmup"),
		SIMPLIFIED("Simple","Displays only accuracy, delay, damage, and wounds."),
		TWO_LINE1_WITH_KEY("Doubled-Key","Current version, hybrid table with per-cell labels instead of a header. Includes a key/legend."),
		TWO_LINE1("Doubled","Current version, hybrid table with per-cell labels instead of a header. Does not include key/legend");

		public final String name, desc;
		DispAttack(String _name, String _desc){
			name = _name;
			desc = _desc;
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
	
	public enum GraphicStyle{
		LEGACY("Legacy","Angled perspective 2019-2024."),
		WASDD("Modern A","Flat perspective 2024+.");
		
		public final String name, desc;
		GraphicStyle(String _name, String _desc){
			name = _name;
			desc = _desc;
		}
	}
	public static GraphicStyle graphicStyleLookup(String str) {
		for (GraphicStyle gs: GraphicStyle.values()) {
			if (gs.name().equals(str)) {
				return gs;
			}
		}
		return null;
	}

	public static void mainMenu() {
		//log("gen main menu 0");
		Input.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				//log("gen main menu 1");
				Print.changePrint(false);
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
							Print.println(i+" slot: "+SaveManager.checkNameInFile(""+i));
						}
						Print.println("9 autosave: "+SaveManager.checkNameInFile("auto"));
						int in = Input.inInt(9);
						Print.println("Loading...");
						SaveManager.load(in == 9 ? "auto" : in+"");
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
				mList.add(Changelog.getMenu());
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
						Print.println("In Trawel, you navigate the game world, and make choices, with numbered menu lists.");
						Print.println("In Trawel proper (some subgames differ), there will never be an option presented that is greater than 9 or less than 1. Some newer menus have scrolling, but they will only ever show a max of 9 options at a time.");
						Print.println("If you're playing from Steam, you can click these options, press a number key (numpad or number row), or even use a controller. If you're using the terminal, you'll have to input the number and then press enter.");
						Print.println("Terminal Only: The 'command prompt' also lets you input 0 to attempt to leave the current menu, and 10 to attempt to leave up to 10 menus. This will only work if there is a 'pinned' back option, and that option isn't considered to lead in circles. Usually you'll end up at the town screen, but older menus might not have a pinned back out, some don't have backing out by design, and others wouldn't make sense to back out of. It will also 'break' the backing out if the state changes, for example after you finish looting, it won't back you out of where you're looting unless you input it again.");
						Print.println("Graphical Only: The graphical client has a few bonus features not immediately evident. You can press the scroll wheel to automatically scroll to the bottom. The 'Jump' option lets you have the game scroll, print new text without scrolling, or jump instantly on new text. 'Fancy' mode disables some rendering that might bug on certain hardware, and is generally less intensive. You can use the arrow keys to scroll and WASD/enter to input on keyboard. On controller, 'dpad' right/A will select the current option and 'dpad' left/B will select the last option. 'dpad' up and down change options.");
						Print.println("Tips: Many modern menus in Trawel are designed so that as many options as possible occupy the same number each time you enter them. In some menus, the options change so much that this isn't possible, or there is an edge case where this rule can't apply. But the Player menu is very stable within each Trawel release, so you can navigate that without needing to read (or listen to) the contents.");
						Print.println("Most older menus do not behave like this, and are slowly being converted.");
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
						return "External";
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
				mList.add(new MenuSelect() {

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
		Input.menuGo(new MenuGenerator(){

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
						return "Attack Display Style: " + attackDisplayStyle.name+".";
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
										return "Currently: " + attackDisplayStyle.name+".";
									}});
								for (DispAttack da: DispAttack.values()) {
									list.add(new MenuSelect() {
										@Override
										public String title() {
											return da.name + ": " + da.desc;
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
						return "Change Display Chars ("+TrawelChar.current_display_style+")";
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
										return "Current Style: " + TrawelChar.current_display_style;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "Accuracy Hit: " +TrawelChar.CHAR_HITMULT
												+ " Time Instants: " + TrawelChar.CHAR_INSTANTS;
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
										return "Weight: " +TrawelChar.DISP_WEIGHT
												+ " Aether: " + TrawelChar.DISP_AETHER
												+ " Restiction Agility Multiplier Penalty: " +TrawelChar.DISP_AMP
												+ " Qualities: " + TrawelChar.DISP_QUALS;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return "more than 75% HP: " +TrawelChar.HP_I_FULL
												+ "; more than 50% HP: " + TrawelChar.HP_I_MOSTLY
												+ "; more than 25% HP: " +TrawelChar.HP_I_HALF
												+ "; more than 0% HP: " + TrawelChar.HP_I_SOME
												+ "; dead: " + TrawelChar.HP_I_DEAD
												;
									}});
								list.add(new MenuLine() {

									@Override
									public String title() {
										return ">= 75% damage: " +TrawelChar.DAM_I_KILL
												+ "; >= 50% damage: " + TrawelChar.DAM_I_HEAVY
												+ "; >= 25% damage: " +TrawelChar.DAM_I_SOME
												+ "; <25% damage: " + TrawelChar.DAM_I_NONE;

									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "Visual (Full ASCII)";
									}

									@Override
									public boolean go() {
										TrawelChar.charSwitchVisual();
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
										TrawelChar.charSwitchNarrator();
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
										TrawelChar.charSwitchEmote();
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
										TrawelChar.charSwitchNone();
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
								return "Flavor Text: " + displayFlavorText + " (If disabled, town fluff events, flavor text on first arrival, and random taunting will not be displayed.)";
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
								return "Travel Text: " + displayTravelText+ " (If disabled, text to indicate movement has started and finished will not be displayed.)";
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
								return "Locational Text: " + displayLocationalText + " (If disabled, current town and date will not be displayed.)";
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
								return "Feature Text: " + displayFeatureText + " (If disabled, won't display type of town Feature after its name.)";
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
								return "Own Name: " + displayOwnName + " (If disabled, 'YOU' will be used whenever your character's name, title, or fullname would be used. This will not make sense, but might help clarity.)";
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
								return "Other Combat Text: " + displayOtherCombat + " (If disabled, no text will be displayed on Person turns that do not include you as an attacker or defender. Does not disable Combat Conditions text, and does disable death messages.)";
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
								return "Show Target Summary: " + displayTargetSummary + " (If disabled, will not print target's defenses in 1v1s or before attacking in mass battles.)";
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
								return "Extended Target Summary: " + extendedTargetSummary + " (If enabled, will show full stats whenever displaying target summary.)";
							}

							@Override
							public boolean go() {
								extendedTargetSummary = !extendedTargetSummary;
								prefs.setProperty("targetsummary_extended", extendedTargetSummary+"");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Node Deeper: " + displayNodeDeeper + " (If enabled, Inward and Outward in Node Features.)";
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
								return "Feature Fluff: " + displayFeatureFluff + " (If enabled, print fluff text when entering/exiting features with flavor.)";
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
								return "Combat Notes: " + combatFeedbackNotes + " (If disabled, doesn't display more detailed breakdowns of special triggers after attacks.)";
							}

							@Override
							public boolean go() {
								combatFeedbackNotes = !combatFeedbackNotes;
								prefs.setProperty("combatnotes_text", combatFeedbackNotes+"");
								return false;
							}
						});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Debug Combat Display: "+advancedCombatDisplay +"  (If enabled, prints verbose debug messages in combat.)";
							}

							@Override
							public boolean go() {
								advancedCombatDisplay = !advancedCombatDisplay;
								prefs.setProperty("debug_attacks",advancedCombatDisplay+"");
								return false;
							}
						});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Line Separator: "+lineSep+"  (If disabled, removes line separator after each input and boot ASCII.)";
							}

							@Override
							public boolean go() {
								lineSep = !lineSep;
								prefs.setProperty("linesep_text",lineSep+"");
								return false;
							}
						});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Save Text: "+saveText+"  (If disabled, removes messages indicating a save was finished.)";
							}

							@Override
							public boolean go() {
								saveText = !saveText;
								prefs.setProperty("nosave_text",saveText+"");
								return false;
							}
						});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Display AutoBattle: "+displayAutoBattle + " (If disabled, will not display the AI's choices when using AutoBattle.)";
							}

							@Override
							public boolean go() {
								displayAutoBattle = !displayAutoBattle;
								prefs.setProperty("displayAutoBattle",displayAutoBattle+"");
								return false;
							}});
						Input.menuGo(new ScrollMenuGenerator(list.size(), "last <> options", "next <> options") {
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
						return "Graphic Style: "+graphicStyle.name;
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
										return "Currently: " + graphicStyle.name;
									}});
								for (GraphicStyle gs: GraphicStyle.values()) {
									list.add(new MenuSelect() {
										@Override
										public String title() {
											return gs.name + ": " + gs.desc;
										}

										@Override
										public boolean go() {
											graphicStyle = gs;
											prefs.setProperty("graphic_style",graphicStyle.name());
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
						return "Graphical only Realtime Displays";
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
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Character Option Defaults";
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
										return "These settings set the default Character Options for newly created characters. Descriptions are in the in-run toggle menu. They do not effect existing characters.";
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "AutoLoot: "+cdAutoLoot;
									}

									@Override
									public boolean go() {
										cdAutoLoot = !cdAutoLoot;
										prefs.setProperty("cdAutoLoot",cdAutoLoot+"");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "AutoLevel: "+cdAutoLevel;
									}

									@Override
									public boolean go() {
										cdAutoLevel = !cdAutoLevel;
										prefs.setProperty("cdAutoLevel",cdAutoLevel+"");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "AutoBattle: "+cdAutoBattle;
									}

									@Override
									public boolean go() {
										cdAutoBattle = !cdAutoBattle;
										prefs.setProperty("cdAutoBattle",cdAutoBattle+"");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "AutoSip: "+cdAutoSip;
									}

									@Override
									public boolean go() {
										cdAutoSip = !cdAutoSip;
										prefs.setProperty("cdAutoSip",cdAutoSip+"");
										return false;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "AutoRecord: "+cdAutoRecord;
									}

									@Override
									public boolean go() {
										cdAutoRecord = !cdAutoRecord;
										prefs.setProperty("cdAutoRecord",cdAutoRecord+"");
										return false;
									}});
								list.add(new MenuBack());
								return list;
							}
						});
						return false;
					}});
				mList.add(new MenuBack());
				return mList;
			}
		});
		try (FileWriter fw = new FileWriter(prefFile)){
			prefs.store(fw, null);
			Print.println("Prefs saved.");
		} catch (Exception e) {
			Print.println("Was not able to save prefs.");
		}

	}

	private static void advancedOptions() {
		Input.menuGo(new MenuGenerator(){

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
						return "Quick Reconnect.";
					}

					@Override
					public boolean go() {
						if (autoConnect) {
							Print.println(TrawelColor.PRE_ORANGE+"This option will break your connection! Really continue?");
							if (!Input.yesNo()) {
								return false;
							}
						}
						Networking.connect(6510,true);
						Print.println("Test Connection!");
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Full Reconnect.";
					}

					@Override
					public boolean go() {
						if (autoConnect) {
							Print.println(TrawelColor.PRE_ORANGE+"This option will break your connection! Really continue?");
							if (!Input.yesNo()) {
								return false;
							}
						}
						Print.println("Port?");
						Networking.connect(Input.inInt(65535),true);
						Print.println("Test Connection!");
						return true;
					}});
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "External Links:";
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
						return "Steam Discussions (opens in browser)";
					}

					@Override
					public boolean go() {
						openWebpage("https://steamcommunity.com/app/992240/discussions/");
						return false;
					}});
				mList.add(new MenuBack());
				return mList;
			}
		});
	}


	private static void credits() {
		Print.println("Made by 'dragon' of realDragon.");
		Print.println();
		
		Print.println("Special thanks to many different communities across different programming languages and engines.");
		Print.println();
		
		Print.println(TrawelColor.STAT_HEADER+"Music:");
		Print.println(" manicInsomniac");
		Print.println();
		
		Print.println(TrawelColor.STAT_HEADER+"Java Libraries:");
		Print.println(" Yellowstone Games' SquidSquad family");
		Print.println(" Apache's Fury");
		Print.println(" Esoteric Software's Kryo");
		Print.println(" All dependencies of prior libraries");
		Print.println();
		
		Print.println(TrawelColor.STAT_HEADER+"Graphical Libraries:");
		Print.println(" JujuAdams' Scribble");
		Print.println(" Alynne's Input");
		Print.println(" Pixelated Pope's Retro Palette Swapper");
		Print.println();
		
		Print.println(TrawelColor.STAT_HEADER+"Legacy Art:");
		Print.println("  Most Character and Armor Art: SmashCooper");
		Print.println("  Some Weapons: Duster");
		Print.println("  Scars, Weapon Placeholders: Jacobs");
		Print.println(TrawelColor.STAT_HEADER+"Modern A:");
		Print.println("  Art: Wasdd");
		Print.println(TrawelColor.STAT_HEADER+"Other Art:");
		Print.println(TrawelColor.ITEM_DESC_PROP+" Background Art:");
		Print.println("  Damrok");
		Print.println(TrawelColor.ITEM_DESC_PROP+" Concept/Splash Art:");
		Print.println("  TamLinArt");
		Print.println();
		
		Print.println(TrawelColor.STAT_HEADER+"Sounds:");
		Print.println(" Stock Media provided by Soundrangers / FxProSound / SoundIdeasCom / PrankAudio / hdaudio / agcnf_media / sounddogs / AbloomAudio / Yurikud / SoundMorph / partnersinrhyme => through Pond5");
		Print.println(" Metal sounds by Bluezone");
		Print.println(" Clicking sound: One of the many CC0 stapler sounds on FreeSound");
		Print.println();
		
		Print.println(TrawelColor.STAT_HEADER+"Steam Assets:");
		
		Print.println("Achievement icons can be found on game-icons.net, modified using their website. These assets are also used in the Graphical UI Tray. They are CC BY 3.0 or CC0, depending on the author.");
		Print.println(TrawelColor.ITEM_DESC_PROP+"Game-Icons.net list:");
		Print.println(
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
			new BumperFactory();
			new WeaponAttackFactory();
			new TownFlavorFactory();
			new QuestReactionFactory();
			//WorldGen.initDummyInvs();
			randomLists.init();
			LootTables.initLootTables();
			DummyInventory.dummyAttackInv = new DummyInventory();
			SaveManager.trawelRegisterFury();
			basicSetup1 = true;
		}
	}

	public static void forceSetup() {
		baseSetup1();
	}

	public static void unitTestSetup() {
		forceSetup();
	}


	private static void gameTypes() {
		Input.menuGo(new MenuGenerator(){

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
						return "Trawel Quickstart. (base game, recommended)";
					}

					@Override
					public boolean go() {
						adventure1(false,false,false,false,false);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Trawel Slowstart. (base game, character creator)";
					}

					@Override
					public boolean go() {
						adventure1(false,true,true,true,false);
						return true;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Sidegames.";
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
										return "rTrawel. (experimental JRPG, prototype, nongraphical)";
									}

									@Override
									public boolean go() {
										rtrawel.TestRunner.run();
										return true;
									}});
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return "SciMechs (experimental mech game, prototype, nongraphical)";
									}

									@Override
									public boolean go() {
										SaveHandler.clean();
										while (true) {
											Print.println(SciRunner.playProto() ? "You win!" : "You lose!"+ "\n Would you like the quit?");
											if (Input.yesNo()) {
												return true;
											}
										}
									}});
								list.add(new MenuBack("Back to Main Menu."));
								return list;
							}});
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "Extras.";
					}

					@Override
					public boolean go() {
						Input.menuGo(new MenuGenerator(){

							@Override
							public List<MenuItem> gen() {
								List<MenuItem> mList = new ArrayList<MenuItem>();
								mList.add(new MenuLine() {
									@Override
									public String title() {
										return "Play Modifiers:";
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Cheat Trawel.";
									}

									@Override
									public boolean go() {
										adventure1(true,false,false,false,false);
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Debug Trawel.";
									}

									@Override
									public boolean go() {
										debug = true;
										adventure1(true,false,false,false,true);
										debug = false;
										return true;
									}});
								mList.add(new MenuLine() {
									@Override
									public String title() {
										return "Queries and Tests:";
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Weapon Stats.";
									}

									@Override
									public boolean go() {
										weaponStatQuery();
										return true;
									}});
								mList.add(new MenuLine() {
									@Override
									public String title() {
										return "Graphical Tests:";
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Model Mode.";
									}

									@Override
									public boolean go() {
										modelMode();
										return true;
									}});
								mList.add(new MenuSelect() {

									@Override
									public String title() {
										return "Time Test.";
									}

									@Override
									public boolean go() {
										forceSetup();
										Calender.timeTest();
										return true;
									}});
								mList.add(new MenuBack("Back to Main Menu."));
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

	

	private static void modelMode() {
		forceSetup();
		Person model;
		model = RaceFactory.makeOld(2);//new Person(starting_level,false,Race.RaceType.HUMANOID,null);
		Player player = new Player();
		player.setPerson(model);
		Player.bag.swapWeapon(new Weapon(1,WeaponType.AXE));
		//Player.bag.getHand().forceEnchantHit(0);
		/*
			 player.bag.swapArmorSlot(new Armor(1,0,MaterialFactory.getMat("emerald")),0);
			 player.bag.swapArmorSlot(new Armor(1,1,MaterialFactory.getMat("emerald")),1);
			 player.bag.swapArmorSlot(new Armor(1,2,MaterialFactory.getMat("emerald")),2);
			 player.bag.swapArmorSlot(new Armor(1,3,MaterialFactory.getMat("emerald")),3);
			 player.bag.swapArmorSlot(new Armor(1,4,MaterialFactory.getMat("emerald")),4);*/
		model.setPlayer();
		Networking.charUpdate();
		Input.yesNo();

	}

	public static String headerText() {
		return ("Dragon's Trawel "+Changelog.VERSION_STRING+Changelog.VERSION_DATE)+
			(lineSep ? ("\r\n"+
			        " ___________  ___  _    _ _____ _     \r\n" + 
					"|_   _| ___ \\/ _ \\| |  | |  ___| |    \r\n" + 
					"  | | | |_/ / /_\\ \\ |  | | |__ | |    \r\n" + 
					"  | | |    /|  _  | |/\\| |  __|| |    \r\n" + 
					"  | | | |\\ \\| | | \\  /\\  / |___| |____\r\n" + 
			       "  \\_/ \\_| \\_\\_| |_/\\/  \\/\\____/\\_____/"
			) : "");
	}

	public static void log(String str) {
		if (ThreadData.isMainThread()) {
			logStream.println(str);
			logStream.flush();
		}
	}
	
	public static void errLog(String str) {
		str = "ERROR: "+str;
		if (ThreadData.isMainThread()) {
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
			ThreadData.setMainThread();
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
				TrawelChar.charSwitchVisual();
				break;
			case "narrator":
				TrawelChar.charSwitchNarrator();
				break;
			case "emote":
				TrawelChar.charSwitchEmote();
				break;
			case "none":
				TrawelChar.charSwitchNone();
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
			extendedTargetSummary = Boolean.parseBoolean(prefs.getProperty("targetsummary_extended","FALSE"));
			displayNodeDeeper = Boolean.parseBoolean(prefs.getProperty("nodedeeper_text","TRUE"));
			combatFeedbackNotes = Boolean.parseBoolean(prefs.getProperty("combatnotes_text","TRUE"));
			graphicStyle = graphicStyleLookup(prefs.getProperty("graphic_style",GraphicStyle.WASDD.name()));
			saveText = Boolean.parseBoolean(prefs.getProperty("save_text","TRUE"));
			lineSep = Boolean.parseBoolean(prefs.getProperty("linesep_text","TRUE"));
			displayAutoBattle = Boolean.parseBoolean(prefs.getProperty("displayAutoBattle","TRUE"));
			
			cdAutoLoot = Boolean.parseBoolean(prefs.getProperty("cdAutoLoot","FALSE"));
			cdAutoLevel = Boolean.parseBoolean(prefs.getProperty("cdAutoLevel","FALSE"));
			cdAutoBattle = Boolean.parseBoolean(prefs.getProperty("cdAutoBattle","FALSE"));
			cdAutoSip = Boolean.parseBoolean(prefs.getProperty("cdAutoSip","FALSE"));
			cdAutoRecord = Boolean.parseBoolean(prefs.getProperty("cdAutoRecord","FALSE"));
			

			if (autoConnect) {
				System.out.println("Please wait for the graphical to load...");
				Networking.handleAnyConnection(legacyConnect ? ConnectType.LEGACY : ConnectType.GDX);
				Print.println("Trawel, Gameplay Version "+Changelog.VERSION_STRING+Changelog.VERSION_DATE);
			}
		}catch(Exception e) {
			System.out.println("There was an error when setting up Trawel.");
			e.printStackTrace();

			errorHandle(e);
			System.out.println("Press enter to quit.");
			Input.inString();
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
				Input.inString();
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
		Print.println(TrawelColor.PRE_RED+"Trawel has encountered an exception. Please report to realDragon. More details can be found in log.txt and the terminal.");
		Print.println("Error Preview: "+ e.toString());
	}

	public static void adventureBody() {
		try {
			lastAutoSave = new Date();
			Player.isPlaying = true;
			ThreadData.mainThreadDataUpdate();
			while(Player.isPlaying) {
				checkAutosave();
				Player.player.getLocation().atTown();
				TrawelTime.globalPassTime();
			}
		} finally {
			try {
				Player.player.close();
			} catch (IOException e) {
				Print.println("Error closing Player Object.");
			}
			Player.player = null;
			multiCanRun = false;
			Print.println("You do not wake up.");
		}
	}
	
	public static void checkAutosave() {
		if (doAutoSave && (new Date().getTime()-lastAutoSave.getTime() > 1000*60*2)) {
			if (saveText) {
				Print.println("Autosaving...");
			}else {
				Print.println("Autosaving.");
			}
			
			WorldGen.plane.prepareSave();
			SaveManager.save("auto");
			lastAutoSave = new Date();
		}
	}

	public static void adventure1(boolean cheaty, boolean displayFight, boolean rerolls, boolean advancedDisplay, boolean debug){
		baseSetup1();
		Networking.richDesc("Character Select");
		World world = null;//WorldGen.eoano();
		Player player = new Player();
		if (debug) {
			player.setStory(new StoryNone());
		}else {
			Print.println("Enable tutorial? (Recommended.)");
			if (Input.yesNo()) {
				player.setStory(new StoryTutorial());
			}else {
				player.setStory(new StoryNone());
			}
		}
		Person manOne = null, manTwo;
		while (manOne == null) {
			if (world == null) {
				Print.println("Generating world...");
				world = WorldGen.eoano();
				WorldGen.finishPlane(WorldGen.plane);
				ThreadData.getThreadData().world = world;//init
			}
			manOne = RaceFactory.makePlayerValid(!rerolls);
			manTwo = RaceFactory.makePlayerValid(!rerolls);
			if (!displayFight) {
				Print.changePrint(true);
			}
			Combat c = Combat.CombatTwo(manOne,manTwo,null);
			manOne = c.getNonSummonSurvivors().get(0);
			player.getStory().setPerson(c.killed.get(0), 0);
			//story.setPerson(manTwo, 0);
			if (!displayFight) {
				Print.changePrint(false);
			}
			if (rerolls) {
				manOne.getBag().graphicalDisplay(1, manOne);
				if (advancedDisplay) {
					manOne.displayStats(false);
				}
				Print.println("Play as " + manOne.getName() +"?");
				if (Input.yesNo()) {
					break;
				}
				manOne = null;
			}
			Networking.clearSides();
		}
		Networking.clearSides();
		player.setPerson(manOne);
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
			//now they get to pick a second one, because slowstart doesn't care about racial stereotypes
			Archetype.menuChooseSecondArchetype(manOne);
		}
		player.getPerson().skillTriggers();//apply skill triggers mostly for starting gear
		player.setLocation(world.getStartTown());//also sets the player world
		player.addGold(10);//give 10 gold to start to deal with death punishments mostly
		if (cheaty) {
			Player.player.setCheating();
			if (debug) {
				Player.player.getPerson().setFlag(PersonFlag.AUTOBATTLE,true);
				Player.player.getPerson().setFlag(PersonFlag.AUTOLOOT,true);
				Player.player.getPerson().setFlag(PersonFlag.AUTOLEVEL,true);
				Player.player.getPerson().addXp(99999);
				Weapon w = Player.player.getPerson().getBag().getHand();
				for (int i = 0; i < 50;i++) {
					w.levelUp();
				}
				for (World wo: WorldGen.plane.worlds()) {
					wo.setVisited();
					Player.player.addGold(999999, wo);
				}
				for (Town t: WorldGen.plane.getTowns()) {
					t.visited = Math.max(2,t.visited);
				}
				Player.player.getPerson().getBag().addAether(999999);
			}
		}
		displayPlayerStart();
		player.getStory().storyStart();

		WorldGen.plane.setPlayer(player);

		multiCanRun = true;

		//player.getPerson().playerLevelUp();
		adventureBody();
	}
	
	private static void displayPlayerStart() {
		Print.println("You are now " + Player.player.getPerson().getName() +".");
		for (Archetype a: Player.player.getPerson().getArchSet()) {
			Print.println("Starting Archetype: " +a.getBriefText());
		}
		for (Feat a: Player.player.getPerson().getFeatSet()) {
			Print.println("Starting Feat: " +a.getBriefText());
		}
		for (Perk a: Player.player.getPerson().getPerkSet()) {
			Print.println("Starting Perk: " +a.getBriefText());
		}
	}



	public static void openWebpage(String urlString) {
		try {
			Desktop.getDesktop().browse(new URL(urlString).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void scrollTest() {
		Input.menuGo(new ScrollMenuGenerator(30, "back <> more","forward (<> left)") {

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
		Input.menuGo(new ScrollMenuGenerator(16,"a <>", "b<>") {

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

		final List<Material> standardList = new ArrayList<Material>();
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
						WeaponAttackFactory.dispTestWeapon(1,WeaponType.values()[i],null,weaponMatTesterList);
						WeaponAttackFactory.dispTestWeapon(10,WeaponType.values()[i],null,weaponMatTesterList);
						WeaponAttackFactory.dispTestWeapon(100,WeaponType.values()[i],null,weaponMatTesterList);
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
			}
		};
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
						Input.menuGo(weapMenu);
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
						return "Standard Mats.";
					}

					@Override
					public boolean go() {
						weaponMatTesterList = standardList;
						Input.menuGo(weapMenu);
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Magic.";
					}

					@Override
					public boolean go() {
						final List<Material> magicMatList = new ArrayList<Material>();
						magicMatList.add(MaterialFactory.getMat("test"));
						final List<Skill> overSkillList = new ArrayList<Skill>();
						final Map<Skill,List<IHasSkills>> skillStances = WeaponAttackFactory.getMetricStances();
						for (Skill s: skillStances.keySet()) {
							overSkillList.add(s);
						}
						Input.menuGo(new ScrollMenuGenerator(overSkillList.size(),"previous <> skills", "next <> skills") {

							@Override
							public List<MenuItem> forSlot(int i) {

								List<MenuItem> list= new ArrayList<MenuItem>();
								list.add(new MenuSelect() {

									@Override
									public String title() {
										return overSkillList.get(i).getName();
									}

									@Override
									public boolean go() {
										Skill skillChosen = overSkillList.get(i);
										List<IHasSkills> sourceList = skillStances.get(skillChosen);
										Input.menuGo(new ScrollMenuGenerator(sourceList.size(),"previous <> sources", "next <> sources") {

											@Override
											public List<MenuItem> forSlot(int i) {
												List<MenuItem> list = new ArrayList<MenuItem>();
												list.add(new MenuSelect() {

													@Override
													public String title() {
														return sourceList.get(i).menuName();
													}

													@Override
													public boolean go() {
														WeaponAttackFactory.dispTestWeapon(1,WeaponType.NULL_WAND,sourceList.get(i),magicMatList);
														WeaponAttackFactory.dispTestWeapon(10,WeaponType.NULL_WAND,sourceList.get(i),magicMatList);
														WeaponAttackFactory.dispTestWeapon(100,WeaponType.NULL_WAND,sourceList.get(i),magicMatList);
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
											}
										});
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
							}
						});
						return false;
					}});
				return list;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuBack("exit"));
				return list;
			}
		};
		Input.menuGo(matMenu);
	}

}
