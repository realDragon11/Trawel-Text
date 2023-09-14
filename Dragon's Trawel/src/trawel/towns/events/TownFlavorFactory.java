package trawel.towns.events;
import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.factions.FBox.FSub;
import trawel.factions.Faction;
import trawel.personal.people.Player;
import trawel.towns.Town;


public class TownFlavorFactory {
	
	public static List<TownFlavor> bumperList = new ArrayList<TownFlavor>();
	
	public class Response{
		TownTag db;
		double mag;
		public Response(TownTag d, double mag) {
			db = d;
			this.mag = mag;
		}
	}
	public TownFlavorFactory(){
		TownFlavor b = new TownFlavor() {
			@Override
			public void activate(int level) {
				switch (extra.randRange(1,4)) {
				case 1:
					extra.println("A group of adventurers is gathered on the fringes of the town, planning their next expedition.");
					break;
				case 2:
					extra.println("Two mercenaries walk through town, looking for a place to unload their goods from their last adventure.");
					break;
				case 3:
					extra.println("A few of the town guard are talking with some adventurers, sharing stories back and forth.");
					break;
				case 4:
					extra.println("Some drunk adventurers are partying in the street.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.ADVENTURE,3));
		b = new TownFlavor() {
			@Override
			public void activate(int level) {
				extra.println("Three small children are playing tag on a rural street inside the town.");
			}
		};
		b.responses.add(new Response(TownTag.SMALL_TOWN,2));
		b = new TownFlavor() {
			@Override
			public void activate(int level) {
				switch (extra.randRange(1,3)) {
				case 1:
					extra.println("A merchant is ordering laborers to move crates in an alleyway.");
					break;
				case 2:
					extra.println("A caravan of merchants has arrived in town, and are unloading their goods.");
					break;
				case 3:
					extra.println("Some town guards are overseeing a trade dispute.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.MERCHANT,3));
		bumperList.add(b);
		b = new TownFlavor() {
			@Override
			public void activate(int level) {
				switch (extra.randRange(1,2)) {
				case 1:
					extra.println("A town guard turns their eye as a thief robs a merchant in blind daylight.");
					break;
				case 2:
					extra.println("The town guards are eyeing you with hungry looks, aimed at your coin purse.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.LAWLESS,3));
		bumperList.add(b);
		b = new TownFlavor() {
			@Override
			public void activate(int level) {
				FSub sub =Player.player.getPerson().facRep.getFacRep(Faction.HEROIC);
				if (sub == null) {
					extra.println("Some adventurers eye you before deciding you're not worth their time.");
					return;
				}
				int total = (int) (sub.forFac-sub.againstFac);
				if (total > level*2) {
					extra.println("Some adventurers ask for your autograph.");
					return;
				}
				if (total > 0) {
					extra.println("Some adventurers chat with you about your heroic deeds.");
					return;
				}
				extra.println("Some adventurers eye you warily.");
			}
		};
		b.responses.add(new Response(TownTag.ADVENTURE,2));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(int level) {
				FSub sub =Player.player.getPerson().facRep.getFacRep(Faction.MERCHANT);
				if (sub == null) {
					extra.println("Some merchants eye you before deciding you're not worth their time.");
					return;
				}
				int total = (int) (sub.forFac-sub.againstFac);
				if (total > level*2) {
					extra.println("Some merchants salute you.");
					return;
				}
				if (total > 0) {
					extra.println("Some merchants look at you before carrying on with their business.");
					return;
				}
				extra.println("Some merchants eye you warily.");
			}
		};
		b.responses.add(new Response(TownTag.MERCHANT,2));
		bumperList.add(b);
		
	}
	
	public static boolean go(double threshold, int level, Town t) {
		double highest = -999;
		double d = 0;
		TownFlavor highestB = null;
		List<TownFlavor> bumps;
		bumps = bumperList;
		for (TownFlavor b: bumps) {
			d = b.calculate(t)*extra.hrandom();
			if (d > highest) {
				highest = d;
				highestB = b;
			}
		}
		if (d > threshold) {
			highestB.activate(level);
			return true;
		}
		return false;
	}
				
	public abstract class TownFlavor {
	
		private List<Response> responses = new ArrayList<Response>();
		
		public double calculate(Town t) {
			double total = 0;
			for (Response r: responses) {
				total +=t.tTags.contains(r.db) ? r.mag : 0;
			}
			return total;
		}
		
		public abstract void activate(int level);
	}
}
