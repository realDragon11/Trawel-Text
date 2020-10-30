package trawel;
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
	public static ArrayList<Bumper> shipList = new ArrayList<Bumper>();
	public BumperFactory(){
		Bumper b = new Bumper() {

			@Override
			public void activate(int level) {
				ArrayList<Person> list = new ArrayList<Person>();
				int count = extra.randRange(1,3);
				for (int i = 0;i < count;i++) {
					list.add(RaceFactory.makeWolf(extra.zeroOut(level-3)+1));}
				
				Networking.sendColor(Color.RED);
				extra.println("A pack of wolves descend upon you!");
				ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
				if (survivors.contains(Player.player.getPerson())) {
					Player.player.questTrigger("wolf",count);
				}
			}};
		b.responses.add(new Response(DrawBane.MEAT,5));
		b.responses.add(new Response(DrawBane.NOTHING,.5));
		b.responses.add(new Response(DrawBane.REPEL,-8));
		b.minAreaLevel = 3;
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
			b.minAreaLevel = 5;
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
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
							Player.player.questTrigger("vampire",1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.BLOOD,3));
			b.responses.add(new Response(DrawBane.GARLIC,-8));
			b.responses.add(new Response(DrawBane.SILVER,-.5));
			b.minAreaLevel = 4;
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
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
						Player.player.questTrigger("bear",1);
				}
					
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
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeUnicorn(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A unicorn accosts you for holding the virgin captive!");
					if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
							Player.player.questTrigger("unicorn",1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.VIRGIN,10));
			b.minAreaLevel = 7;
			bumperList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					ArrayList<Person> list = new ArrayList<Person>();
					int count = extra.randRange(1,3);
					for (int i = 0;i < count;i++) {
						list.add(RaceFactory.makeHarpy(extra.zeroOut(level-3)+1));}
					Networking.sendColor(Color.RED);
					extra.println("A flock of harpies attack!");
					ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
					if (survivors.contains(Player.player.getPerson())) {
						Player.player.questTrigger("harpy",count);
					}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,1.25));
			b.responses.add(new Response(DrawBane.SILVER,1.25));
			b.responses.add(new Response(DrawBane.GOLD,1.25));
			b.responses.add(new Response(DrawBane.REPEL,-1));
			bumperList.add(b);
			
			//ships
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = new Person(level);
					
					Networking.sendColor(Color.RED);
					extra.println("A pirate challenges you for your booty!");
					mainGame.CombatTwo(Player.player.getPerson(),p);
					
				}};
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			shipList.add(b);
			
		
	}
}
