import java.awt.Color;
import java.util.ArrayList;


public class BumperFactory {
	
	public class Response{
		DrawBane db;
		double mag;
		public Response(DrawBane d, double mag) {
			db = d;
			this.mag = mag;
		}
	}
	public static ArrayList<Bumper> bumperList = new ArrayList<Bumper>();
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
		
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeFellReaver(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A fell reaver appears!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,6));
			b.responses.add(new Response(DrawBane.CEON_STONE,1));
			bumperList.add(b);
	}
}
