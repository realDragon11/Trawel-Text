package trawel.towns.fight;

import java.util.ArrayList;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.people.Player;
import trawel.quests.QRMenuItem;
import trawel.quests.QuestR;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public abstract class ExploreFeature extends Feature {

	protected int explores = 0;
	protected int exhaust = 0;
	protected int exhaustTimer = 0;
	protected double regenRate = 24.0;
	protected boolean exhausted = false;
	
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		
		if (exhaust > 0) {
			exhaustTimer+=time;
			if (exhaustTimer > regenRate) {
				exhaustTimer-=regenRate;
				exhaust--;
				if (exhaust <= 0) {
					exhausted = false;
					exhaustTimer = 0;
				}
			}
		}
		return null;
	}

	@Override
	protected void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Explore!";
					}

					@Override
					public boolean go() {
						explore();
						return false;
					}
				});
				for (QuestR qr: qrList) {
					list.add(new QRMenuItem(qr));
				}
				List<MenuItem> mList = extraMenu();
				if (mList != null) {
					list.addAll(mList);
				}
				list.add(new MenuBack("Exit."));
				return list;
			}
			});
	}
	
	@Override
	public String getTitle() {
		return getName() + (exhausted ? " (Empty)" :"");
	}

	@Override
	public String getColor() {
		return extra.F_COMBAT;
	}
	
	public void explore(){
		if (exhausted) {
			if (mainGame.doTutorial) {
				extra.println("Exploration features have a limited amount of interesting things, which replenishes over time. You should pass in game time by doing other things before returning here.");
			}
			onNoGo();
			return;
		}
		explores++;
		exhaust++;
		if (exhaust > 5) {
			if (extra.randFloat() > exhaust/30f) {
				exahust();
				return;
			}
		}
		Player.addTime(.1 + (extra.randFloat()*.5));
		mainGame.globalPassTime();
		subExplore(roll());
	}
	
	public void exahust() {
		exhausted = true;
		onExhaust();
	}
	
	public abstract void onExhaust();
	public abstract void onNoGo();
	public abstract void subExplore(int id);
	protected abstract WeightedTable roller();
	protected int roll() {
		return roller().random(extra.getRand());
	}
	public List<MenuItem> extraMenu(){
		return null;
	}

}
