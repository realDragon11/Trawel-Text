package trawel.towns.misc;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class Garden extends Feature {

	private static final long serialVersionUID = 1L;
	private ArrayList<PlantSpot> plants = new ArrayList<PlantSpot>();
	public Garden(Town town) {
		this.town = town;
		this.tier = town.getTier();
		tier = town.getTier();
		name = "garden";
		tutorialText = "Garden.";
		for (int i = 0; i < 4;i++) {
		plants.add(new PlantSpot(tier));}
		background_area = "forest";
		background_variant = 1;
		area_type = Area.GARDEN;
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}

	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|garden|Garden|");
		extra.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				for (PlantSpot ps: plants) {
					list.add(ps.getMenuForGarden());
				}
				list.add(new MenuBack("leave"));
				return list;
			}
		});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		time *= (1.0+(tier/100.0));
		for (PlantSpot p: plants) {
			p.passTime(time,calling);
		}
		return null;
	}

}
