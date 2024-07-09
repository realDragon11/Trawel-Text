package trawel.towns.data;
import java.util.ArrayList;
import java.util.List;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.factions.FBox.FSub;
import trawel.factions.Faction;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Player;
import trawel.towns.contexts.Town;


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
			public void activate(Town t) {
				switch (Rand.randRange(1,4)) {
				case 1:
					Print.println("A group of adventurers is gathered on the fringes of the town, planning their next expedition.");
					break;
				case 2:
					Print.println("Two mercenaries walk through town, looking for a place to unload their goods from their last adventure.");
					break;
				case 3:
					Print.println("A few of the town guard are talking with some adventurers, sharing stories back and forth.");
					break;
				case 4:
					Print.println("Some drunk adventurers are partying in the street.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.ADVENTURE,3));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				Print.println("Three small children are playing tag on a small street inside the town.");
			}
		};
		b.responses.add(new Response(TownTag.SMALL_TOWN,2));
		b.responses.add(new Response(TownTag.LAWLESS,-1));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				switch (Rand.randRange(1,3)) {
				case 1:
					Print.println("A merchant is ordering laborers to move crates in an alleyway.");
					break;
				case 2:
					Print.println("A caravan of merchants has arrived in town, and are unloading their goods.");
					break;
				case 3:
					Print.println("Some town guards are overseeing a trade dispute.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.MERCHANT,3));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				switch (Rand.randRange(1,2)) {
				case 1:
					Print.println("A town guard turns their eye as a thief robs a merchant in blind daylight.");
					break;
				case 2:
					Print.println("The town guards are eyeing you with hungry looks, aimed at your coin purse.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.LAWLESS,3));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				FSub sub =Player.player.getPerson().facRep.getFacRep(Faction.HEROIC);
				if (sub == null) {
					Print.println("Some adventurers eye you before deciding you're not worth their time.");
					return;
				}
				int total = (int) (sub.forFac-sub.againstFac);
				if (total > IEffectiveLevel.unclean(t.getTier())*2f) {
					Print.println("Some adventurers ask for your autograph.");
					return;
				}
				if (total > 0) {
					Print.println("Some adventurers chat with you about your heroic deeds.");
					return;
				}
				Print.println("Some adventurers eye you warily.");
			}
		};
		b.responses.add(new Response(TownTag.ADVENTURE,2));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				FSub sub =Player.player.getPerson().facRep.getFacRep(Faction.MERCHANT);
				if (sub == null) {
					Print.println("Some merchants eye you before deciding you're not worth their time.");
					return;
				}
				int total = (int) (sub.forFac-sub.againstFac);
				if (total > IEffectiveLevel.unclean(t.getTier())*2f) {
					Print.println("Some merchants salute you.");
					return;
				}
				if (total > 0) {
					Print.println("Some merchants look at you before carrying on with their business.");
					return;
				}
				Print.println("Some merchants eye you warily.");
			}
		};
		b.responses.add(new Response(TownTag.MERCHANT,2));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				Print.println("Some tourists are asking the town guards the best spots to sightsee at.");
			}
		};
		b.responses.add(new Response(TownTag.TRAVEL,.5));
		b.responses.add(new Response(TownTag.VISTAS,3));
		b.responses.add(new Response(TownTag.LAWLESS,-2));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				Print.println("You hear wild howls in the distance.");
			}
		};
		b.responses.add(new Response(TownTag.DRUIDIC,1));
		b.responses.add(new Response(TownTag.UNSETTLING,1));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				Print.println("A merchant is attempting to haggle with a local farmer for better prices.");
			}
		};
		b.responses.add(new Response(TownTag.FARMS,2));
		b.responses.add(new Response(TownTag.LIVESTOCK,2));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				Print.println("A scribe is examining the area and jotting down some notes.");
			}
		};
		b.responses.add(new Response(TownTag.HISTORY,2));
		b.responses.add(new Response(TownTag.MYSTIC,1));
		b.responses.add(new Response(TownTag.ARCANE,1));
		bumperList.add(b);
		
		b = new TownFlavor() {
			@Override
			public void activate(Town t) {
				Print.println("A beggar signals a hidden thief on a profitable mark.");
			}
		};
		b.responses.add(new Response(TownTag.DISPARITY,1));
		b.responses.add(new Response(TownTag.LAWLESS,2));
		bumperList.add(b);
		
		b = new TownFlavor() {//high travel density
			@Override
			public void activate(Town t) {
				switch (Rand.randRange(1,3)) {
				case 1:
					Print.println("A group of travelers is trying to find a place to stay.");
					break;
				case 2:
					Print.println("A lone wanderer walks inside the town, giving everyone else a wide berth.");
					break;
				case 3:
					Print.println("A caravan is unloading the travelers it contains near the town border.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.TRAVEL,3));
		b.responses.add(new Response(TownTag.VISTAS,.5));
		b.responses.add(new Response(TownTag.CITY,.5));
		b.responses.add(new Response(TownTag.MERCHANT,.5));
		b.responses.add(new Response(TownTag.ADVENTURE,.5));
		bumperList.add(b);
		
		b = new TownFlavor() {//law + travel
			@Override
			public void activate(Town t) {
				Print.println("Some town guards are checking a traveler's papers.");
			}
		};
		b.responses.add(new Response(TownTag.TRAVEL,1));
		b.responses.add(new Response(TownTag.LAW,1));
		b.responses.add(new Response(TownTag.LAWLESS,-2));
		bumperList.add(b);
		
		b = new TownFlavor() {//brewing
			@Override
			public void activate(Town t) {
				Print.println("A brewer is testing out a potion in the street.");
			}
		};
		b.responses.add(new Response(TownTag.ALCHEMY,1));
		bumperList.add(b);
		
		b = new TownFlavor() {//mining
			@Override
			public void activate(Town t) {
				switch (Rand.randRange(1,3)) {
				case 1:
					Print.println("Miners coated in dust trudge back to their homes.");
					break;
				case 2:
					Print.println("A group of miners is playing cards on break.");
					break;
				case 3:
					Print.println("A foreman oversees the training of some miners in picking out profitable ores.");
					break;
				}
			}
		};
		b.responses.add(new Response(TownTag.MINERALS,2));
		bumperList.add(b);
		
		/*
		b = new TownFlavor() {//X
			@Override
			public void activate(Town t) {
				// TODO Auto-generated method stub
			}
		};
		b.responses.add(new Response(TownTag.ADVENTURE,1));
		bumperList.add(b);
		*/
	}
	
	public static boolean go(double threshold, Town t) {
		double highest = -999;
		double d = 0;
		TownFlavor highestB = null;
		List<TownFlavor> bumps;
		bumps = bumperList;
		for (TownFlavor b: bumps) {
			d = b.calculate(t)*Rand.randFloat();//fully random to avoid constantly showing the same ones
			if (d > highest) {
				highest = d;
				highestB = b;
			}
		}
		if (highest > threshold) {
			highestB.activate(t);
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
		
		public abstract void activate(Town t);
	}
}
