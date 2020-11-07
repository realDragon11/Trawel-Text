package trawel.townevents;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import trawel.Bumper;
import trawel.BumperFactory;
import trawel.Inventory;
import trawel.Player;
import trawel.Town;
import trawel.extra;


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
				extra.println("A group of adventurers is gathered on the fringes of the town, planning their next expedition.");
				
			}
			
		};
		b.responses.add(new Response(TownTag.ADVENTURE,3));
		bumperList.add(b);
		
	}
	
	public static boolean go(double threshold, int level, Town t) {
		//TODO: calculate which enemy to fight
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
		return true;}
		return false;
	}
	public abstract class TownFlavor {
		
		public ArrayList<Response> responses = new ArrayList<Response>();
		
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
