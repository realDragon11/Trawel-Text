package trawel;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import trawel.personal.item.Inventory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.towns.Town;

public abstract class Bumper {
	
	public List<BumperFactory.Response> responses = new ArrayList<BumperFactory.Response>();
	public int minAreaLevel = 0;
	
	public static EnumMap<DrawBane,Double> amounts = new EnumMap<DrawBane,Double>(DrawBane.class);

	/**
	 * types:<br>
	 * default: bumperList
	 * <br>
	 * 1: shipList
	 */
	public static boolean go(double threshold, int level, int type,Town t) {
		//calculate which enemy to fight
		double highest = -999;
		double d = 0;
		Bumper highestB = null;
		List<Bumper> bumps;
		switch (type) {
		default: bumps = BumperFactory.bumperList;break;
		case 1: bumps= BumperFactory.shipList;break;
		}
		amounts.clear();
		DrawBane[] dbs = DrawBane.values();
		Inventory inv = Player.player.getPerson().getBag();
		for (int i = dbs.length-1; i >=0;i--) {
			amounts.put(dbs[i], inv.calculateDrawBaneFor(dbs[i]));
		}
		for (Bumper b: bumps) {
			if (level < b.minAreaLevel) {
				continue;
			}
			d = b.calculate()*extra.hrandom();
			if (d > highest) {
				highest = d;
				highestB = b;
			}
		}
		if (d > threshold) {
			assert highestB != null;
			highestB.activate(level);
			return true;
		}
		return false;
	}
	
	public double calculate() {
		double total = 0;
		for (BumperFactory.Response r: responses) {
			total +=amounts.getOrDefault(r.db,0d)*r.mag;
		}
		return total;
	}
	
	public abstract void activate(int level);
	

}
