package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Networking.Area;
import trawel.Effect;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class Doctor extends Feature {

	private static final long serialVersionUID = 1L;

	private double timecounter;
	public Doctor(String name,Town t) {
		timecounter = extra.randRange(5,10);
		this.name = name;
		town = t;
		tier = t.getTier();
		area_type = Area.MISC_SERVICE;
	}
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}
	
	@Override
	public String getTutorialText() {
		return "Doctor";
	}
	
	@Override
	public void go() {
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuMoney());
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_FREE+"Get Diagnosis.";
					}

					@Override
					public boolean go() {
						Player.player.getPerson().displayEffects();
						return false;
					}});
				//TODO: includes effects which wore off which is bad now
				int effectGuess = extra.clamp(Player.player.getPerson().effectsSize(), 3, 6);
				int cost = Math.round(getUnEffectiveLevel()*(effectGuess/1.5f));
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.SERVICE_CURRENCY+"Cure ("+World.currentMoneyDisplay(cost)+")";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() < cost) {
							extra.println("Not enough "+World.currentMoneyString()+"!");
							return false;
						}
						extra.println("Pay for a check up?");
						if (extra.yesNo()) {
							Player.player.addGold(-cost);
							Player.player.getPerson().cureEffects();
						}
						return false;
					}});
				list.add(new MenuBack("Leave"));
				return list;
			}});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timecounter-=time;
		if (timecounter <= 0) {
			int price = (int) (2*getUnEffectiveLevel());
			//must have been afflicted by at least one effect since last doctor visit/creation
			town.getPersonableOccupants().filter(a -> a.getPerson().effectsSize() > 0 && a.canBuyMoneyAmount(price)).limit(3)
			.forEach(a -> a.getPerson().clearEffects());//uses clear instead of cure because NPCs don't go to blacksmiths and it's better to reduce the size of effect maps on npcs
			timecounter += extra.randRange(20,40);
		}
		return null;
	}

}
