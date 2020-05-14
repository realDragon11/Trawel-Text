import java.util.ArrayList;

public abstract class Bumper {
	
	public static ArrayList<Bumper> bumperList = new ArrayList<Bumper>();
	
	public ArrayList<Response> responses = new ArrayList<Response>();
	
	public class Response{
		DrawBane db;
		double mag;
		public Response(DrawBane d, double mag) {
			db = d;
			this.mag = mag;
		}
	}

	public static void go() {
		//TODO: calculate which enemy to fight
		double highest = -999;
		double d;
		Bumper highestB = null;
		for (Bumper b: bumperList) {
			d = b.calculate(Player.player.getPerson().getBag())*extra.hrandom();
			if (d > highest) {
				highest = d;
				highestB = b;
			}
		}
		highestB.activate();
	}
	
	public double calculate(Inventory i) {
		double total = 0;
		for (Response r: responses) {
			total +=i.calculateDrawBaneFor(r.db)*r.mag;
		}
		return total;
	}
	
	public abstract void activate();
	
	public class BumperFactory {
		
	}

}
