package trawel.towns.misc;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class Garden extends Feature {

	private static final long serialVersionUID = 1L;
	private int tier;
	private ArrayList<PlantSpot> plants = new ArrayList<PlantSpot>();
	public Garden(Town town) {
		this.town = town;
		this.tier = town.getTier();
		tier = town.getTier();
		name = "garden";
		tutorialText = "Gardens can grow plants.";
		for (int i = 0; i < 4;i++) {
		plants.add(new PlantSpot(tier));}
		background_area = "forest";
		background_variant = 1;
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}

	@Override
	public void go() {
		Networking.setArea("shop");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|garden|Garden|");
		while (true) {
			for (int i = 0; i < plants.size();i++) {
				extra.println((i+1) + " garden section containing " + plants.get(i).contains);
			}
			extra.println(plants.size()+1 + " exit");
			int in = extra.inInt(plants.size()+1);
			if (in <= plants.size()) {
				plants.get(in-1).go();
			}else {
				break;
			}
		}
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
