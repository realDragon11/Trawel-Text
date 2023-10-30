package trawel.towns;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.personal.RaceFactory;
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

public class TravelingFeature extends Feature{

	private static final long serialVersionUID = 1L;
	
	private double timeLeft = 0;
	private boolean regen = false;
	
	private int useCount = 0;
	private TravelType contents = null;
	
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
	
	protected int curTier;
	
	public TravelingFeature(Town town) {
		this.town = town;
		this.tier = town.getTier();
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
		curTier = extra.zeroOut(tier+extra.randRange(1,5)-4)+1;
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
			break;
		case 3:
			contents = TravelType.STALL;
			area_type = Area.SHOP;
			name = "X Stall";
			break;
		case 4:
			contents = TravelType.CELEBRATION;
			name = extra.choose("Celebration","Festival","Community Event");
			break;
		}
	}

	@Override
	protected void go() {
		int ret = 0;
		//while there is still a feature to go to
		//note: if time passes too much at once could skip the feature not existing, which would be bad
		while (ret != 9 && !regen && contents != null) {
			//all menus must return every time
			switch (contents) {
			case CELEBRATION:
				ret = extra.menuGo(new MenuGenerator() {

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
								Combat c = mainGame.CombatTwo(RaceFactory.getDueler(curTier),RaceFactory.getDueler(curTier),town.getIsland().getWorld());
								town.addOccupant(c.getNonSummonSurvivors().get(0).getMakeAgent(AgentGoal.NONE));
								Player.addTime(3);
								mainGame.globalPassTime();
								return true;
							}});
						list.add(new MenuBack());
						return list;
					}});
				break;
			case FIGHT:
				break;
			case ORACLE:
				break;
			case STALL:
				break;
			}
		}
		
	}

}
