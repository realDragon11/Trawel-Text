package trawel.towns.features.fight;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.factions.HostileTask;
import trawel.helper.constants.FeatureData;
import trawel.helper.constants.TrawelColor;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Potion;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.quests.locations.QBMenuItem;
import trawel.quests.locations.QRMenuItem;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.quests.types.CleanseSideQuest;
import trawel.quests.types.FetchSideQuest;
import trawel.quests.types.KillSideQuest;
import trawel.quests.types.Quest;
import trawel.quests.types.CleanseSideQuest.CleanseType;
import trawel.quests.types.FetchSideQuest.FetchType;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.features.Feature;
import trawel.towns.features.services.Doctor;
import trawel.towns.features.services.Store;
import trawel.towns.features.services.WitchHut;

public class Slum extends Store implements QuestBoardLocation{
	
	static {
		FeatureData.registerFeature(Slum.class,
				new FeatureData() {

					@Override
					public void tutorial() {
						Print.println(fancyNamePlural()+" hold sidequests, backalley vendors, and are often controlled by a Crime Lord. If the Crime Lord is removed from the "+fancyName()+", it is possible to pay for reform programs to enfranchise the people there. The cost of such programs will increase with the danger still present in the "+fancyName()+".");
					}

					@Override
					public String name() {
						return "District";
					}

					@Override
					public FeatureTutorialCategory category() {
						return FeatureTutorialCategory.ENCOUNTERS;
					}

					@Override
					public int priority() {
						return 15;
					}

					@Override
					public String color() {
						return TrawelColor.F_MULTI;
					}
				});
	}
	

	private boolean removable;
	public Agent crimeLord;
	private double timePassed = 0;
	private float crimeRating = 10;
	private int wins = 0;
	private ReplaceFeatureInterface replacer;
	private String storename;
	
	private boolean canQuest = true;
	
	public List<Quest> sideQuests = new ArrayList<Quest>();
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public QRType getQRType() {
		return QRType.SLUM;
	}
	
	@Override
	public Area getArea() {
		return Area.SLUM;
	}
	
	@Override
	public String nameOfType() {
		return "District";
	}
	
	/**
	 * the general contract of this interface is that it will generate a Feature to replace the Slum
	 * <br>
	 * and that it is allowed to hold state between the generate call and the printReplaceText call,
	 * but be reusable given that it is called again, if and only if there is no chance of 
	 * generate race condition calling before printReplaceText does
	 */
	public static interface ReplaceFeatureInterface extends Serializable{
		public Feature generate(Slum from);
		/**
		 * will be called right after generate, so you can store the Slum 'from' if you want to put special text there for it
		 * <br>
		 * must be provided, but for comparison, the non-custom version will print "You pay for the reform programs."
		 */
		public void printReplaceText();
	}
	
	public Slum(int _tier) {
		super(_tier,Slum.class);
		storename = name;
		tier = _tier;
	}

	public Slum(Town t, String name,boolean removable) {
		this(t.getTier());
		town = t;
		this.name = name;
		this.removable = removable;
	}
	public Slum(Town t, String _name,int _tier,ReplaceFeatureInterface _replacer) {
		this(_tier);
		name = _name;
		town = t;
		replacer = _replacer;
		removable = true;
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
		switch (Rand.randRange(1,5)) {
		case 1:
			if (Rand.randFloat() > .90f) {//10% for merchant
				sideQuests.add(FetchSideQuest.generate(this,FetchType.MERCHANT));
				break;
			}
			sideQuests.add(FetchSideQuest.generate(this,FetchType.CRIME));
			break;
		case 2:
			if (Rand.randFloat() > .95f) {//5% for heroism
				sideQuests.add(FetchSideQuest.generate(this,FetchType.HERO));
				break;
			}
			sideQuests.add(FetchSideQuest.generate(this,FetchType.COMMUNITY));
			break;
		case 3:
			sideQuests.add(CleanseSideQuest.generate(this,Rand.choose(CleanseType.WOLF,CleanseType.BEAR,CleanseType.BANDIT)));
			break;
		case 4: case 5:
			sideQuests.add(KillSideQuest.generate(this,Rand.randFloat() > .4f));//60% chance to be a murder quest
			break;
		}
	}
	
	@Override
	public void go() {
		Slum sl = this;
		int removecost = (int) (((crimeRating*5)+(tier*10))/2f);
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"Shop at '"+storename+"'.";
					}

					@Override
					public boolean go() {
						Input.menuGo(modernStoreFront());
						return false;
					}});
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return TrawelColor.FSERVICE_QUEST+"Speak to Fixer (Sidequests).";
					}
	
					@Override
					public boolean go() {
						backroom();
						return false;
					}
				});
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return TrawelColor.SERVICE_FREE+"Enter Backalleys (Crime).";
					}
	
					@Override
					public boolean go() {
						crime();
						return false;
					}
				});
				if (crimeLord != null) { 
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return TrawelColor.PRE_BATTLE +"Attack Crime Lord!";
						}
		
						@Override
						public boolean go() {
							killCrime();
							return false;
						}
					});
				}
				
				if (crimeLord == null && removable) {
					
					mList.add(new MenuSelect() {
						
						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Pay to reform district ("+World.currentMoneyDisplay(removecost)+")";
						}
		
						@Override
						public boolean go() {
							if (Player.player.getGold() > removecost) {
								Player.player.addGold(-removecost);
								Networking.unlockAchievement("reform");
								town.helpCommunity(20);
								if (replacer != null) {
									town.laterReplace(Slum.this,replacer.generate(Slum.this));
									return true;
								}
								Print.println("You pay for the reform programs.");
								String formerly = " (formerly '"+sl.getName()+"')";
								switch (Rand.randRange(0,3)) {
								case 0: case 1:
									town.laterReplace(sl,new WitchHut("Chemist Business"+formerly,sl.town));
									break;
								case 2:
									town.laterReplace(sl,new Store(sl.town,formerly));
									break;
								case 3:
									town.laterReplace(sl,new Doctor("Medical Clinic"+formerly,sl.town));
									break;
								}
								return true;
							}else {
								Print.println("You can't afford to uplift the slum out of poverty, and no one else who could seems to care.");
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
		timePassed-=time;
		if (timePassed < 0) {
			addAnItem();
			if (canQuest) {this.generateSideQuest();}
			if (crimeLord == null){//replacing crime lord does not let them do much
				timePassed = 48;
				if (town.getPersonableOccupants().count() == 0) {
					return null;
				}
				crimeLord = town.getRandPersonableOccupant();
				town.removeOccupant(crimeLord);
				crimeLord.onlyGoal(AgentGoal.OWN_SOMETHING);
			}else {
				//if has crime lord
				timePassed = 12+(Rand.randFloat()*24);
				if (town.getPersonableOccupants().count() == 0) {
					return null;
				}
				Agent sp = town.getRandPersonableOccupant();
				if (sp.getPerson().hTask == HostileTask.MUG && sp.getPerson().getLevel() > crimeLord.getPerson().getLevel() && Rand.chanceIn(1,3)) {
					//replace crime lord
					crimeLord.onlyGoal(AgentGoal.NONE);
					town.addOccupant(crimeLord);
					crimeLord = sp;
					town.removeOccupant(sp);
				}else {
					//if not replaced, make money
					crimeLord.getPerson().getBag().addGold(tier);
				}
				//increase crime, replaced or not
				capCrimeToLord();
				//add after floorcapping to new crime level
				crimeRating+=IEffectiveLevel.unEffective(crimeLord.getPerson().getEffectiveLevel());
			}
			
		}
		return null;//TODO: might need events
	}
	
	private void backroom() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				
				for (Quest q: sideQuests) {
					mList.add(new QBMenuItem(q,Slum.this));
				}
				for (QuestR qr: Player.player.QRFor(Slum.this)) {
					mList.add(new QRMenuItem(qr));
				}
				mList.add(new MenuBack());
				return mList;
			}});
	}
	
	private void crime() {
		//Slum sl = this;
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "buy experimental potions";
					}

					@Override
					public boolean go() {
						int potionCost = tier;
						Print.println(Player.player.getFlask() != null ? "You already have a potion, buying one will replace it." : "Quality not assured.");
						Print.println("You have "+Player.showGold()+".");
						switch (Rand.randRange(1, 3)) {
						case 1:
							Print.println("Buy a low-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (Input.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									Print.println("You cannot afford this. (You have "+Player.showGold()+".)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (Rand.chanceIn(1, 3)) {
									Player.player.setFlask(new Potion(Effect.CURSE,Rand.randRange(2, 3)));
								}else {
									Player.player.setFlask(new Potion(Rand.randList(Arrays.asList(Effect.estimEffects)),Rand.randRange(2, 3)));	
								}
								Print.println("You buy the potion.");
							}
							break;
						case 2:
							potionCost *=2;
							Print.println("Buy a medium-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (Input.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									Print.println("You cannot afford this. (You have "+Player.showGold()+".)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (Rand.chanceIn(1, 4)) {
									Player.player.setFlask(new Potion(Effect.CURSE,Rand.randRange(3, 4)));
								}else {
									Player.player.setFlask(new Potion(Rand.randList(Arrays.asList(Effect.estimEffects)),Rand.randRange(3, 4)));	
								}
								Print.println("You buy the potion.");
							}
							break;
						case 3:
							potionCost *=4;
							Print.println("Buy a high-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (Input.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									Print.println("You cannot afford this. (You have "+Player.showGold()+" gold.)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (Rand.chanceIn(1, 6)) {
									Player.player.setFlask(new Potion(Effect.CURSE,Rand.randRange(3, 5)));
								}else {
									Player.player.setFlask(new Potion(Rand.randList(Arrays.asList(Effect.estimEffects)),Rand.randRange(3, 5)));	
								}
								Print.println("You buy the potion.");
							}
							break;
						}
						return false;
					}
				});
				
				if (crimeRating > 0) {
					mList.add(new MenuSelect() {
	
						@Override
						public String title() {
							return TrawelColor.PRE_BATTLE +"Go vigilante.";
						}
	
						@Override
						public boolean go() {
							Print.println("You wait around and find a mugger.");
							Player.addTime(2);
							TrawelTime.globalPassTime();
							Combat c = Player.player.fightWith(RaceFactory.makeMugger(tier));
							if (c.playerWon() > 0) {
								//crime rating go down
								crimeRating-= Player.player.getPerson().getUnEffectiveLevel();
								//cap reduction on crime lord if they're still alive
								capCrimeToLord();
							}
							return false;
						}
					});
				}
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return TrawelColor.PRE_BATTLE +"Mug someone.";
					}

					@Override
					public boolean go() {
						Print.println("You wait around and find someone to rob.");
						Player.addTime(1);
						TrawelTime.globalPassTime();
						Combat c = Player.player.fightWith(RaceFactory.makePeace(tier));
						if (c.playerWon() > 0) {
							//crime rating go up
							crimeRating+=1;
						}
						return false;
					}
				});
				
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	private void killCrime() {
		Person p = crimeLord.getPerson();
		Print.println(TrawelColor.PRE_BATTLE +"Attack " + p.getName() + "?");
		if (Input.yesNo()) {
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				Print.println("You kill the crime lord!");
				wins++;
				//reduce crime by 3x crimelord eLevel and 1x player eLevel, compared to only 1x player eLevel of mugger
				crimeRating -= 3*crimeLord.getPerson().getUnEffectiveLevel();
				crimeRating -= Player.player.getPerson().getUnEffectiveLevel();
				crimeLord = null;
			}else {
				Print.println("The crime lord kills you.");
			}
		}
		
	}
	
	public void capCrimeToLord() {
		if (crimeLord == null) {
			return;
		}
		crimeRating = Math.max(crimeRating,IEffectiveLevel.unEffective(crimeLord.getPerson().getEffectiveLevel()));
	}

	@Override
	public void removeSideQuest(Quest q) {
		sideQuests.remove(q);
	}
	
	@Override
	public float occupantDesire() {
		return 5f;
	}

}
