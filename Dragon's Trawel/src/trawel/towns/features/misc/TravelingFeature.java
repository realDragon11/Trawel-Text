package trawel.towns.features.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Town;
import trawel.towns.data.FeatureData;
import trawel.towns.features.services.Oracle;
import trawel.towns.features.services.Store;

public class TravelingFeature extends Store{
	
	static {
		FeatureData.registerFeature(TravelingFeature.class,new FeatureData() {
			
			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+ " have community services and events hosted by the Townspeople. These services and events vary greatly.");
			}
			
			@Override
			public int priority() {
				return 70;
			}
			
			@Override
			public String name() {
				return "Town Stall";
			}
			
			@Override
			public String color() {
				return TrawelColor.F_VARIES;
			}
			
			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.ADVANCED_SERVICES;
			}
		});
	}

	private static final long serialVersionUID = 1L;
	
	private double timeLeft = 0;
	private boolean regen = false;
	
	private int useCount = 0;
	private TravelType contents = null;
	private List<Person> fighters = new ArrayList<Person>();
	protected int trueTier;
	public static boolean exiting;//false by default
	
	protected enum TravelType{
		CELEBRATION(TrawelColor.F_SERVICE,"Celebration",Area.INN),
		FIGHT(TrawelColor.F_COMBAT,"Fight",Area.ARENA),
		ORACLE(TrawelColor.F_SPECIAL,"Oracle",Area.ORACLE),
		STALL(TrawelColor.F_SERVICE,"Store",Area.SHOP);
		
		public final String color, name;
		public final Area area;
		TravelType(String _color, String _name, Area _area){
			color = _color;
			name = _name;
			area = _area;
		}
	}
	
	public TravelingFeature(Town town) {
		super(town.getTier(),TravelingFeature.class);
		this.town = town;
		trueTier = town.getTier();
		timeLeft = Rand.randFloat()*24f;
	}
	
	//overrides with own method
	@Override
	public String getColor() {
		return (contents != null ? contents.color : TrawelColor.F_SPECIAL);
	}
	
	@Override
	public Networking.Area getArea() {
		return contents != null ? contents.area : Area.MISC_SERVICE;
	}
	
	@Override
	public boolean canShow() {
		return contents != null;
	}
	
	@Override
	public String nameOfType() {
		return "Varies: " + (contents != null ? contents.name : "nothing");
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timeLeft -= time;
		if (timeLeft < 0) {
			if (regen) {
				regenNow();
			}else {
				regenSetup();
			}
		}
		return null;
	}
	
	public void regenNow() {
		newFeature();
		regen = false;
		timeLeft = 22f+Rand.randFloat()*4f;
	}
	
	public void regenSetup() {
		contents = null;
		timeLeft = 12f+Rand.randFloat()*48f;
		regen = true;
	}
	
	public void newFeature() {
		useCount = 0;
		//switch statement with features that need to be added
		//and also non-features
		tier = extra.zeroOut(trueTier+Rand.randRange(1,5)-4)+1;
		fighters.clear();//don't add to world yet, could overflow
		items.clear();//probably should make a better way later
		switch(Rand.randRange(0,8)) {
		default:
			contents = null;
			name = "n/a";
		break;
		case 1:
			contents = TravelType.ORACLE;
			name = "Traveling Oracle";
			break;
		case 2:
			contents = TravelType.FIGHT;
			name = "Event Fight";
			tier = Math.max(2,tier);
			fighters.add(RaceFactory.getDueler(tier+1));
			for (int i = Rand.randRange(2,3);i>=0;i--) {
				fighters.add(RaceFactory.getDueler(tier));
			}
			for (int i = Rand.randRange(2,3);i>=0;i--) {
				fighters.add(RaceFactory.getDueler(tier-1));
			}
			break;
		case 3:
			contents = TravelType.STALL;
			name = "Traveling Stall";
			restock();
			break;
		case 4:
			contents = TravelType.CELEBRATION;
			name = Rand.choose("Celebration","Festival","Community Event");
			break;
		}
	}

	@Override
	public void go() {
		exiting = false;
		//while there is still a feature to go to
		//note: if time passes too much at once could skip the feature not existing, which would be bad
		while (exiting == false && !regen && contents != null) {
			//all menus must return every time
			switch (contents) {
			case CELEBRATION:
				Input.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {
							@Override
							public String title() {
								return TrawelColor.SERVICE_FREE+"Attempt to find Free Beer.";
							}

							@Override
							public boolean go() {
								Player.addTime(.5f+Rand.randFloat()*1f);
								TrawelTime.globalPassTime();
								useCount++;
								if (Rand.randRange(1, useCount) == 1) {
									Print.println(TrawelColor.RESULT_PASS+"You find some beer laying around.");
									Player.player.beer++;
								}else{
									Print.println(TrawelColor.RESULT_FAIL+"Your efforts were in vain, you could not find any beer.");
								}
								return true;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Watch Nameless Duel";
							}

							@Override
							public boolean go() {
								Player.addTime(3);
								TrawelTime.globalPassTime();
								Combat c = Combat.CombatTwo(RaceFactory.getDueler(tier),RaceFactory.getDueler(tier),town.getIsland().getWorld());
								town.addOccupant(c.getNonSummonSurvivors().get(0).getMakeAgent(AgentGoal.NONE));
								Player.addTime(3);
								TrawelTime.globalPassTime();
								regenSetup();
								return true;
							}});
						list.add(new MenuBack(){
							@Override
							public boolean go() {
								exiting = true;
								return true;
							};
						});
						return list;
					}});
				break;
			case FIGHT:
				Input.menuGo(new ScrollMenuGenerator(fighters.size(),"last <>","next <>") {

					@Override
					public List<MenuItem> forSlot(int i) {
						Person p = fighters.get(i);
						return Collections.singletonList(new MenuSelect() {

							@Override
							public String title() {
								return p.getName() + " LvL "+p.getLevel();
							}

							@Override
							public boolean go() {
								if (p.reallyFight("Challenge")) {
									Player.addTime(1);
									TrawelTime.globalPassTime();
									Combat c = Player.player.fightWith(p);
									if (c.playerWon() > 0) {
										fighters.remove(i);
									}else {
										town.addOccupant(fighters.remove(i).getMakeAgent(AgentGoal.NONE));
									}
								}
								return true;//need to regen list in this case if a fighter is removed, but also if time passes
							}});
					}

					@Override
					public List<MenuItem> header() {
						return Collections.singletonList(new MenuLine() {

							@Override
							public String title() {
								return "There are " + fighters.size() + " fighters hanging around.";
							}});
					}

					@Override
					public List<MenuItem> footer() {
						return Collections.singletonList(new MenuBack("Leave.") {
							@Override
							public boolean go() {
								exiting = true;
								return true;
							};
						});
					}});
				break;
			case ORACLE:
				Input.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.SERVICE_FREE+"Listen in on their ramblings.";
							}

							@Override
							public boolean go() {
								Player.addTime(1+Rand.randFloat());
								TrawelTime.globalPassTime();
								if (useCount > 2) {
									if (Rand.chanceIn(useCount,3+useCount)){
										if (useCount > 5) {
											regenSetup();
											Print.println(TrawelColor.RESULT_FAIL+"The oracle packs up and heads off with extreme urgency.");
										}else {
											Print.println("The oracle watches you in silence.");
										}
										return true;
									}
								}
								timeLeft+=1;//extend how long they stay, since they will be removed above by chance
								Networking.unlockAchievement("oracle1");
								Print.println("\""+Oracle.tipRandomOracle(town.getName())+"\"");
								useCount++;
								return true;
							}});
						list.add(new MenuBack(){
							@Override
							public boolean go() {
								exiting = true;
								return true;
							};
						});
						return list;
					}});
				break;
			case STALL:
				Input.menuGo(modernStoreFront());
				exiting = true;
				break;
			}
		}
		
	}

}
