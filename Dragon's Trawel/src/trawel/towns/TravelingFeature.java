package trawel.towns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.Item;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.Networking.Area;
import trawel.battle.Combat;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.fight.Arena;
import trawel.towns.services.Inn;
import trawel.towns.services.Oracle;
import trawel.towns.services.Store;

public class TravelingFeature extends Store{

	private static final long serialVersionUID = 1L;
	
	private double timeLeft = 0;
	private boolean regen = false;
	
	private int useCount = 0;
	private TravelType contents = null;
	private List<Person> fighters = new ArrayList<Person>();
	protected int trueTier;
	public static boolean exiting;//false by default
	
	protected enum TravelType{
		CELEBRATION(extra.F_SERVICE,"Celebration"),
		FIGHT(extra.F_COMBAT,"Fight"),
		ORACLE(extra.F_SPECIAL,"Oracle"),
		STALL(extra.F_SERVICE,"Store");
		
		public final String color, name;
		TravelType(String _color, String _name){
			color = _color;
			name = _name;
		}
	}
	
	public TravelingFeature(Town town) {
		super(town.getTier(),TravelingFeature.class);
		this.town = town;
		trueTier = town.getTier();
		timeLeft = extra.randFloat()*24f;
	}
	
	@Override
	public String getColor() {
		return (contents != null ? contents.color : extra.F_SPECIAL);
	}
	
	@Override
	public boolean canShow() {
		return contents != null;
	}
	
	@Override
	public String getTutorialText() {
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
		timeLeft = 22f+extra.randFloat()*4f;
	}
	
	public void regenSetup() {
		contents = null;
		timeLeft = 12f+extra.randFloat()*48f;
		regen = true;
	}
	
	public void newFeature() {
		useCount = 0;
		//switch statement with features that need to be added
		//and also non-features
		tier = extra.zeroOut(trueTier+extra.randRange(1,5)-4)+1;
		fighters.clear();//don't add to world yet, could overflow
		items.clear();//probably should make a better way later
		switch(extra.randRange(0,8)) {
		default:
			contents = null;
			name = "n/a";
		break;
		case 1:
			contents = TravelType.ORACLE;
			name = "Traveling Oracle";
			area_type = Area.ORACLE;
			break;
		case 2:
			contents = TravelType.FIGHT;
			name = "Event Fight";
			area_type = Area.ARENA;
			tier = Math.max(2,tier);
			fighters.add(RaceFactory.getDueler(tier+1));
			for (int i = extra.randRange(2,3);i>=0;i--) {
				fighters.add(RaceFactory.getDueler(tier));
			}
			for (int i = extra.randRange(2,3);i>=0;i--) {
				fighters.add(RaceFactory.getDueler(tier-1));
			}
			break;
		case 3:
			contents = TravelType.STALL;
			area_type = Area.SHOP;
			name = "Traveling Stall";
			restock();
			break;
		case 4:
			contents = TravelType.CELEBRATION;
			name = extra.choose("Celebration","Festival","Community Event");
			break;
		}
	}

	@Override
	public void go() {
		//while there is still a feature to go to
		//note: if time passes too much at once could skip the feature not existing, which would be bad
		while (exiting == false && !regen && contents != null) {
			//all menus must return every time
			switch (contents) {
			case CELEBRATION:
				extra.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {
							@Override
							public String title() {
								return "Attempt to find Free Beer";
							}

							@Override
							public boolean go() {
								Player.addTime(.5f+extra.randFloat()*1f);
								mainGame.globalPassTime();
								if (extra.randRange(1, useCount+1) == 1) {
									extra.println("You find some beer laying around.");
									Player.player.beer++;
								}else{
									extra.println("Your efforts were in vain, you could not find any beer.");
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
								mainGame.globalPassTime();
								Combat c = mainGame.CombatTwo(RaceFactory.getDueler(tier),RaceFactory.getDueler(tier),town.getIsland().getWorld());
								town.addOccupant(c.getNonSummonSurvivors().get(0).getMakeAgent(AgentGoal.NONE));
								Player.addTime(3);
								mainGame.globalPassTime();
								regen = true;
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
				extra.menuGo(new ScrollMenuGenerator(fighters.size(),"last <>","next <>") {

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
									Player.addTime(.5);
									mainGame.globalPassTime();
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
				extra.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Listen in on their ramblings.";
							}

							@Override
							public boolean go() {
								Player.addTime(1+extra.randFloat());
								mainGame.globalPassTime();
								Oracle.tip("");
								return false;
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
				extra.menuGo(modernStoreFront());
				exiting = true;
				break;
			}
		}
		exiting = false;
		
	}

}
