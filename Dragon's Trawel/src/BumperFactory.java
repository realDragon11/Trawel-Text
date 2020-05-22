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
				for (int i = 0;i < extra.randRange(1,3);i++) {
					list.add(RaceFactory.makeWolf(extra.zeroOut(level-3)+1));}
				
				Networking.sendColor(Color.RED);
				extra.println("A pack of wolves descend upon you!");
				ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
				
			}};
		b.responses.add(new Response(DrawBane.MEAT,5));
		b.responses.add(new Response(DrawBane.NOTHING,.5));
		b.responses.add(new Response(DrawBane.REPEL,-8));
		bumperList.add(b);
		
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeFellReaver(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A fell reaver appears!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.CEON_STONE,3));
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeEnt(level);
					
					Networking.sendColor(Color.RED);
					extra.println("An ent appears!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.ENT_CORE,5));
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeVampire(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A vampire jumps from the shadows!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.BLOOD,4));
			b.responses.add(new Response(DrawBane.GARLIC,-8));
			b.responses.add(new Response(DrawBane.SILVER,-.5));
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = new Person(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A thief charges you!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBear(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A bear attacks you!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,4));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.HONEY,.7));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBat(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A bat attacks you!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			bumperList.add(b);
			
		
	}
}
