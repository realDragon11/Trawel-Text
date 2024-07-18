package trawel.towns.features.multi;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.factions.HostileTask;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
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
import trawel.towns.data.FeatureData;
import trawel.towns.features.Feature;
import trawel.towns.features.elements.MenuMoney;
import trawel.towns.features.services.Doctor;
import trawel.towns.features.services.Store;
import trawel.towns.features.services.WitchHut;

public class Slum extends Store implements QuestBoardLocation{
	
	static {
		FeatureData.registerFeature(Slum.class,
				new FeatureData() {

					@Override
					public void tutorial() {
						//TODO
						Print.println(fancyNamePlural()+" hold sidequests, backalley vendors, and are often controlled by a Crime Lord. If the Crime Lord is removed from the "+fancyName()+", it is possible to pay for reform programs to enfranchise the people there. The cost of such programs will increase with the danger still present in the "+fancyName()+". If there isn't too much heat, black-market doctors in "+fancyNamePlural()+" cure "+Effect.WOUNDED.getName()+" and "+Effect.BURNOUT.getName()+".");
					}

					@Override
					public String name() {
						return "District";
					}

					@Override
					public FeatureTutorialCategory category() {
						return FeatureTutorialCategory.VITAL_SERVICES;
					}

					@Override
					public int priority() {
						return 25;
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
	private float crimeRating;
	private double heat = 0f;
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
		crimeLord = newCrimeLord();
		//near cap
		crimeRating = 8f*getUnEffectiveLevel();
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
		newCrimeLord();
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
							return TrawelColor.PRE_BATTLE+"Attack Crime Lord!";
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
								Print.println(TrawelColor.RESULT_GOOD+"You pay for the reform programs.");
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
								Print.println(TrawelColor.RESULT_ERROR+"You can't afford to uplift the slum out of poverty, and no one else who could seems to care.");
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
		if (heat > 0) {
			heat -=time;
		}
		timePassed-=time;
		if (timePassed < 0) {
			addAnItem();
			if (canQuest) {this.generateSideQuest();}
			if (crimeLord == null){//replacing crime lord does not let them do much
				timePassed = 48;
				//if there is no crime lord, crime starts falling to an amount based on the tier
				//lerp 50% towards 5 x utier. This will only tick once
				crimeRating = extra.lerp(this.getUnEffectiveLevel()*5f,crimeRating,.5f);
				//hard cap to 10 x utier afterwards
				crimeRating = Math.min(this.getUnEffectiveLevel()*10f,crimeRating);
				//make a new crime lord from scratch to avoid leveling issues
				crimeLord = newCrimeLord();
			}else {
				//if has crime lord
				timePassed = 12f+(Rand.randFloat()*24f);
				Agent sp = town.getRandPersonableOccupant();
				if (sp != null && //if we even got anyone
						sp.getPerson().hTask == HostileTask.MUG//if the random person we choose is a mugger task
						&& sp.getPerson().getLevel() > crimeLord.getPerson().getLevel()//if they are higher level than the crime lord
						&& sp.getPerson().getLevel() < tier+3//max 3 levels higher, crime lords are already always level+1
						) {
					//replace crime lord
					crimeLord.onlyGoal(AgentGoal.NONE);
					town.addOccupant(crimeLord);
					crimeLord = sp;
					town.removeOccupant(sp);
				}else {
					//if not replaced, make money
					crimeLord.getPerson().getBag().addGold(IEffectiveLevel.clean(tier));
				}
				//increase crime, replaced or not
				capCrimeToLord();
				//add after floorcapping to new crime level
				if (crimeRating > this.getUnEffectiveLevel()*10f) {
					//over cap, start lerping downwards
					//20% lerp towards per tick
					crimeRating = extra.lerp(crimeRating,this.getUnEffectiveLevel()*10f,.2f);
				}else {
					crimeRating+=crimeLord.getPerson().getUnEffectiveLevel();
				}
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
	
	private static final double HEAT_CRIME_ALLOW = 12d;//12 hours of heat leeway to challenge crime lord
	private static final double HEAT_DOCTOR_ALLOW = 5d;//5 hours of heat leeway to get doctor treatment
	
	private void crime() {
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuMoney());
				mList.add(new MenuLine() {

					@Override
					public String title() {
						if (heat > 24f) {//if more than one day of heat
							return TrawelColor.INFORM_BAD_STRONG+"There's a lot of heat on you here.";
						}
						if (heat > HEAT_CRIME_ALLOW) {
							return TrawelColor.INFORM_BAD_MID+"You have a fair bit of heat on you here.";
						}
						if (heat > HEAT_DOCTOR_ALLOW) {
							return TrawelColor.INFORM_BAD_WEAK+"You have some heat on you here.";
						}
						if (heat > 0) {
							return TrawelColor.INFORM_GOOD_WEAK+"You have a small amount of heat left here.";
						}
						return TrawelColor.INFORM_GOOD_STRONG+"You have no heat on you here.";
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"Seek black-market treatment.";
					}

					@Override
					public boolean go() {
						Player.addTime(1);//search time
						TrawelTime.globalPassTime();
						if (heat > HEAT_DOCTOR_ALLOW) {//5 hours of heat leeway
							Print.println(TrawelColor.RESULT_ERROR+"You fail to find a doctor- there is too much heat on you here.");
							return false;
						}
						//costs for all punishments, even the ones they can't cure- it's harder work
						//doctor cost is 1f, this is .5f to 2f
						int cost = IEffectiveLevel.cleanRangeReward(getLevel(),2f*(1+Player.player.getPerson().punishmentSize()),.25f);
						Print.println(TrawelColor.SERVICE_CURRENCY+"Spend "+World.currentMoneyDisplay(cost) + " on a black-market treatment?");
						if (Input.yesNo()) {
							if (Player.player.getGold() < cost) {
								Print.println(TrawelColor.RESULT_ERROR+"Not enough "+World.currentMoneyString()+"! The doctor is angry.");
								//add 4 days of heat
								heat += 24d*4;
								return false;
							}else {
								Player.addTime(1);//treatment time
								TrawelTime.globalPassTime();
								//perform curing service
								Player.player.addGold(-cost);
								Print.println(TrawelColor.RESULT_PASS+"You pay and receive treatment.");
								Player.player.getPerson().cureEffects();
								//MAYBELATER: could possibly do random side effects, but the money and heat is probably enough to make it unreliable
								//and also not telling you exactly what you have, but the player menu does that so it's less a factor
								//add 2 days of heat to prevent re-treatment
								heat += 48d;
							}
						}else {
							Print.println(TrawelColor.RESULT_WARN+"The doctor mutters something about attention before going back into hiding.");
							//add a half day of heat so they can't repeatedly attempt for a better deal without going somewhere else
							heat += 12d;
						}
						return false;
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.SERVICE_CURRENCY+"Buy experimental potions.";
					}

					@Override
					public boolean go() {
						int potionCost = tier;
						Print.println(Player.player.getFlask() != null ? "You already have a potion, buying one will replace it." : "Quality not assured.");
						Print.println("You have "+Player.showGold()+".");
						switch (Rand.randRange(1, 3)) {
						case 1:
							Print.println(TrawelColor.SERVICE_CURRENCY+"Buy a low-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (Input.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									Print.println(TrawelColor.RESULT_ERROR+"You cannot afford this. (You have "+Player.showGold()+".)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (Rand.chanceIn(1, 3)) {
									Player.player.setFlask(new Potion(Effect.CURSE,Rand.randRange(2, 3)));
								}else {
									Player.player.setFlask(new Potion(Rand.randList(Arrays.asList(Effect.estimEffects)),Rand.randRange(2, 3)));	
								}
								Print.println(TrawelColor.RESULT_PASS+"You buy the potion.");
							}
							break;
						case 2:
							potionCost *=2;
							Print.println(TrawelColor.SERVICE_CURRENCY+"Buy a medium-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (Input.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									Print.println(TrawelColor.RESULT_ERROR+"You cannot afford this. (You have "+Player.showGold()+".)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (Rand.chanceIn(1, 4)) {
									Player.player.setFlask(new Potion(Effect.CURSE,Rand.randRange(3, 4)));
								}else {
									Player.player.setFlask(new Potion(Rand.randList(Arrays.asList(Effect.estimEffects)),Rand.randRange(3, 4)));	
								}
								Print.println(TrawelColor.RESULT_PASS+"You buy the potion.");
							}
							break;
						case 3:
							potionCost *=4;
							Print.println(TrawelColor.SERVICE_CURRENCY+"Buy a high-quality potion? ("+World.currentMoneyDisplay(potionCost)+")");
							if (Input.yesNo()) {
								if (Player.player.getGold() < potionCost) {
									Print.println(TrawelColor.RESULT_ERROR+"You cannot afford this. (You have "+Player.showGold()+".)");
									return false;
								}
								Player.player.addGold(-potionCost);
								if (Rand.chanceIn(1, 6)) {
									Player.player.setFlask(new Potion(Effect.CURSE,Rand.randRange(3, 5)));
								}else {
									Player.player.setFlask(new Potion(Rand.randList(Arrays.asList(Effect.estimEffects)),Rand.randRange(3, 5)));	
								}
								Print.println(TrawelColor.RESULT_PASS+"You buy the potion.");
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
							return TrawelColor.PRE_MAYBE_BATTLE+"Go vigilante.";
						}
	
						@Override
						public boolean go() {
							Print.println("You wait around to find crime to stop...");
							Player.addTime(1);//search time
							TrawelTime.globalPassTime();
							if (crimeRating > 0) {
								//1/(1+x) chance to fail
								if (Rand.chanceIn(1,1+(int)Math.ceil(crimeRating))) {
									Print.println(TrawelColor.RESULT_FAIL+"You were unable to spot any crimes to stop.");
								}else {
									//pass, so find mugger to fight
									Person mugger = RaceFactory.makeMugger(tier);
									switch (mugger.getJob()) {
										case null:
										default: 
											Print.println(TrawelColor.RESULT_PASS+"A snatchpurse is on the run!");
											if (!mugger.reallyFight("Really corner")) {
												return false;
											}
											break;
										case ROGUE:
											Print.println(TrawelColor.RESULT_PASS+"A thief is breaking open a window!");
											if (!mugger.reallyFight("Really confront")) {
												return false;
											}
											break;
										case THUG:
											Print.println(TrawelColor.RESULT_PASS+"A thug is mugging a citizen!");
											if (!mugger.reallyFight("Really confront")) {
												return false;
											}
											break;
									}
									Combat c = Player.player.fightWith(mugger);
									if (c.playerWon() > 0) {
										//crime rating go down
										crimeRating -= mugger.getUnEffectiveLevel();
										//cap reduction on crime lord if they're still alive
										capCrimeToLord();
									}else {
										//failure increases crime, but less so than winning reduces it
										crimeRating += mugger.getUnEffectiveLevel()/3f;
									}
									Player.addTime(1);//time for battle
									TrawelTime.globalPassTime();
									heat += 3d;//add a small amount of heat, note that it will be -2 for the time passing
								}
							}else {
								Print.println(TrawelColor.RESULT_ERROR+"You could find no crimes to stop- this area is safe, for now.");
							}
							return false;
						}
					});
				}
				mList.add(new MenuSelect() {
					
					@Override
					public String title() {
						return TrawelColor.PRE_BATTLE +"Rob someone.";
					}

					@Override
					public boolean go() {
						Print.println("You wait around and find someone to rob.");
						Player.addTime(1);//time for search
						TrawelTime.globalPassTime();
						float playerFactor = Player.player.getPerson().getUnEffectiveLevel();
						//random chance based on how much crime there is- more crime makes it harder for lower level players to find stuff
						if (Rand.chanceIn(1,3)//flat 33% chance to just pass
								//c/(3p+c) chance to fail
								|| Rand.randFloat() < (crimeRating)/((3f*playerFactor)+crimeRating)) {
							Print.println(TrawelColor.RESULT_PASS+"You find a mark.");
							Person victim = RaceFactory.makePeace(tier);
							if (victim.reallyFight("Really mug")) {
								Combat c = Player.player.fightWith(victim);
								if (c.playerWon() > 0) {
									//crime rating go up
									crimeRating+=Player.player.getPerson().getUnEffectiveLevel();
								}
								Player.addTime(1);//time for battle
								TrawelTime.globalPassTime();
								heat += 3d;//add a small amount of heat, note that it will be -2 for the time passing
							}else {
								Print.println("You decide to leave "+victim.getName() + " alone.");
							}
						}else {
							Print.println(TrawelColor.RESULT_FAIL+"You fail to find any marks.");
						}
						return false;
					}
				});
				mList.add(new MenuBack());
				return mList;
			}});
		
	}
	
	private Agent newCrimeLord() {
		return RaceFactory.makeMugger(tier+1).setOrMakeAgentGoal(AgentGoal.OWN_SOMETHING);
	}
	
	private void killCrime() {
		if (heat > HEAT_CRIME_ALLOW) {
			Print.println(TrawelColor.RESULT_ERROR+"There's too much heat to raid the crime lord right now.");
			return;
		}
		Person p = crimeLord.getPerson();
		if (p.reallyAttack()) {
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				Print.println(TrawelColor.RESULT_PASS+"You kill the crime lord!");
				wins++;
				//reduce crime by 3x crimelord eLevel and 1x player eLevel, compared to only 1x player eLevel of mugger
				crimeRating -= 3*crimeLord.getPerson().getUnEffectiveLevel();
				crimeRating -= Player.player.getPerson().getUnEffectiveLevel();
				crimeLord = null;
				heat = 24d*6;//6 days of heat
			}else {
				Print.println(TrawelColor.RESULT_FAIL+"The crime lord kills you.");
				heat = 24d*2;//2 days of heat
			}
		}
	}
	
	public void capCrimeToLord() {
		if (crimeLord == null) {
			return;
		}
		crimeRating = Math.max(crimeRating,crimeLord.getPerson().getUnEffectiveLevel());
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
