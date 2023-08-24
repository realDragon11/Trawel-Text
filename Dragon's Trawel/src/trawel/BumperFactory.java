package trawel;
import java.util.ArrayList;
import java.util.List;

import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.quests.Quest.TriggerType;


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
				int canLevelMax = RaceFactory.addAdjustLevel(level, 3);
				int canLevel = RaceFactory.addAdjustLevel(level, 1);
				if (level > 2 && canLevelMax >= 1 && extra.chanceIn(2,5)) {
					List<Person> list = new ArrayList<Person>();
					list.add(RaceFactory.makeAlphaWolf(level-1));
					for (int i = 0;i < 3;i++) {
						list.add(RaceFactory.makeWolf(canLevelMax));
					}

					extra.println(extra.PRE_BATTLE+"A large pack of wolves ambush you!");
					Combat c = Player.player.massFightWith(list);
					if (c.playerWon() > 0) {
						Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 4);
					}
				}else {
					if (canLevel >= 1) {
						List<Person> list = new ArrayList<Person>();
						for (int i = 0;i < 2;i++) {
							list.add(RaceFactory.makeWolf(canLevel));
						}
	
						extra.println(extra.PRE_BATTLE+"A pack of wolves descend upon you!");
						Combat c = Player.player.massFightWith(list);
						if (c.playerWon() > 0) {
							Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 3);
						}
					}else {
						Person p = RaceFactory.makeWolf(level);
						extra.println(extra.PRE_BATTLE+"A wolf attacks you!");
						Combat c = Player.player.fightWith(p);
						if (c.playerWon() > 0) {
							Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 1);
						}
					}
				}
			}};
			b.responses.add(new Response(DrawBane.MEAT,5));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			//b.minAreaLevel = 3;//replaced with solowoofs
			bumperList.add(b);

			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeWolf(level);

					extra.println(extra.PRE_RED+"A wolf attacks you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
						Player.player.questTrigger(TriggerType.CLEANSE,"wolf", 1);
					}

				}};
		b.responses.add(new Response(DrawBane.MEAT,2));
		b.responses.add(new Response(DrawBane.NOTHING,.3));
		b.responses.add(new Response(DrawBane.REPEL,-10));
		
		
		bumperList.add(b);
		
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeFellReaver(level);
					
					extra.println(extra.PRE_RED+"A fell reaver appears!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
					}
				}};
			b.responses.add(new Response(DrawBane.MEAT,2));
			b.responses.add(new Response(DrawBane.CEON_STONE,4));
			b.minAreaLevel = 4;
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeEnt(level);
					
					extra.println(extra.PRE_RED+"An ent appears!");
					Player.player.fightWith(p);
					
				}};
			b.responses.add(new Response(DrawBane.ENT_CORE,5));
			b.minAreaLevel = 3;
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeVampire(level);
					
					extra.println(extra.PRE_RED+"A vampire jumps from the shadows!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
					}else {
						Player.player.questTrigger(TriggerType.CLEANSE,"vampire", 1);
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
					Person p = RaceFactory.getMugger(level);
					
					extra.println(extra.PRE_RED+"A thief charges you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getLocation().addOccupant(new Agent(p));
					}else {
						Player.player.questTrigger(TriggerType.CLEANSE,"bandit", 1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBear(level);
					
					extra.println(extra.PRE_RED+"A bear attacks you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
						Player.player.questTrigger(TriggerType.CLEANSE,"bear", 1);
					}
				}

			};
			b.responses.add(new Response(DrawBane.MEAT,4));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.HONEY,.7));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			b.minAreaLevel = 2;
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBat(level);
					
					extra.println(extra.PRE_RED+"A bat attacks you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
						Player.player.questTrigger(TriggerType.CLEANSE,"bat", 1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.NOTHING,.5));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			bumperList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeUnicorn(level);
					
					extra.println(extra.PRE_RED+"A unicorn accosts you for holding the virgin captive!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
						Player.player.questTrigger(TriggerType.CLEANSE,"unicorn", 1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.VIRGIN,10));
			b.minAreaLevel = 8;
			bumperList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					List<Person> list = new ArrayList<Person>();
					int count = extra.randRange(2, 4);
					int addLevel = RaceFactory.addAdjustLevel(level, count-1);
					if (addLevel > 0) {
						for (int i = 0;i < count;i++) {
							list.add(RaceFactory.makeHarpy(addLevel));
						}
						extra.println(extra.PRE_BATTLE+"A flock of harpies attack!");
						Combat c = Player.player.massFightWith(list);
						if (c.playerWon() > 0) {
							Player.player.questTrigger(TriggerType.CLEANSE,"harpy", count);
						}
					}else {
						Person p = RaceFactory.makeHarpy(level);
						extra.println(extra.PRE_BATTLE+"A harpy nestmother attacks!");
						Combat c = Player.player.fightWith(p);
						if (c.playerWon() > 0) {
							Player.player.questTrigger(TriggerType.CLEANSE,"harpy", 1);
						}else {
							Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
						}
					}
					
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,1.25));
			b.responses.add(new Response(DrawBane.SILVER,1.4));
			b.responses.add(new Response(DrawBane.GOLD,1.4));
			b.responses.add(new Response(DrawBane.REPEL,-1));
			b.minAreaLevel = 6;
			bumperList.add(b);
			
			//ships
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.getMugger(level);
					
					extra.println(extra.PRE_RED+"A pirate challenges you for your booty!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getLocation().addOccupant(new Agent(p));
					}else {
						Player.player.questTrigger(TriggerType.CLEANSE,"bandit", 1);
					}
					
				}};
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			shipList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeDrudgerStock(level);
					
					extra.println(extra.PRE_RED+"A drudger attacks your ship!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
					}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			shipList.add(b);
			
		
	}
}
