import java.awt.Color;
import java.util.ArrayList;

public abstract class Bumper {
	
	public ArrayList<BumperFactory.Response> responses = new ArrayList<BumperFactory.Response>();
	
	

	public static boolean go(double threshold, int level) {
		//TODO: calculate which enemy to fight
		double highest = -999;
		double d = 0;
		Bumper highestB = null;
		for (Bumper b: BumperFactory.bumperList) {
			d = b.calculate(Player.player.getPerson().getBag())*extra.hrandom();
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
	
	public double calculate(Inventory i) {
		double total = 0;
		for (BumperFactory.Response r: responses) {
			total +=i.calculateDrawBaneFor(r.db)*r.mag;
		}
		return total;
	}
	
	public abstract void activate(int level);
	

}
