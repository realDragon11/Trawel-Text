import java.awt.Color;
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

	public static void go(int level) {
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
		highestB.activate(level);
	}
	
	public double calculate(Inventory i) {
		double total = 0;
		for (Response r: responses) {
			total +=i.calculateDrawBaneFor(r.db)*r.mag;
		}
		return total;
	}
	
	public abstract void activate(int level);
	
	public class BumperFactory {
		public BumperFactory(){
			Bumper b = new Bumper() {
	
				@Override
				public void activate(int level) {
					ArrayList<Person> list = new ArrayList<Person>();
					for (int i = 0;i < extra.randRange(2,4);i++) {
						list.add(RaceFactory.makeWolf(extra.zeroOut(level-3)+1));}
					
					Networking.sendColor(Color.RED);
					extra.println("A pack of wolves descend upon you!");
					ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,5));
			bumperList.add(b);
		}
	}

}
