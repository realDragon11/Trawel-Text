package trawel;
import java.util.ArrayList;
import java.util.List;

import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.quests.Quest.TriggerType;
import trawel.towns.Town;


public class BumperFactory {
	
	public class Response{
		public final DrawBane db;
		public final double mag;
		public Response(DrawBane d, double mag) {
			db = d;
			this.mag = mag;
		}
	}
	public static List<Bumper> bumperList = new ArrayList<Bumper>();
	public static List<Bumper> shipList = new ArrayList<Bumper>();
	public BumperFactory(){
		Bumper b = new Bumper() {

			@Override
			public void activate(int level) {
				//wolf pack now against a larger pack+alpha with variable allies
				/**
				 * allyList does not contain player so we can retainAll easier
				 */
				List<Person> allyList = new ArrayList<Person>();
				List<Person> foeList = new ArrayList<Person>();
				
				List<Person> playerSide = new ArrayList<Person>();
				playerSide.addAll(Player.player.getAllies());
				String allyString;
				String foeString;
				int wolvesPresent;
				switch (extra.randRange(0,2)) {
				default:
				case 0:
					foeString = "A large pack of wolves ";
					for (int i = 0;i < 4;i++) {
						Person p = RaceFactory.makeWolf(level);
						p.setFlag(PersonFlag.IS_MOOK,true);
						foeList.addAll(p.getSelfOrAllies());
					}
					wolvesPresent = 4;
					break;
				case 1:
					foeString = "A small but orderly pack of wolves ";
					foeList.addAll(RaceFactory.makeAlphaWolf(level+4).getSelfOrAllies());
					for (int i = 0;i < 2;i++) {
						Person p = RaceFactory.makeWolf(level);
						p.setFlag(PersonFlag.IS_MOOK,true);
						foeList.addAll(p.getSelfOrAllies());
					}
					wolvesPresent = 3;
					break;
				case 2:
					foeString = "Two alpha wolves ";
					for (int i = 0;i < 2;i++) {
						foeList.addAll(RaceFactory.makeAlphaWolf(level).getSelfOrAllies());
					}
					wolvesPresent = 2;
					break;
				}
				switch (extra.randRange(0,1)) {
				case 0:
					allyString = "ambush you near a crashed merchant caravan, who fight alongside you.";
					allyList.add(RaceFactory.makeQuarterMaster(level+3));
					allyList.add(RaceFactory.makeRich(level-2));
					break;
				default:
				case 1:
					allyString = "attack you near a watchtower, which comes to your aid.";
					allyList.add(RaceFactory.getLawman(level));
					allyList.add(RaceFactory.getLawman(level));
					break;
				}
				extra.println(extra.PRE_BATTLE+foeString+allyString);
				List<List<Person>> listList = new ArrayList<List<Person>>();
				playerSide.addAll(allyList);
				listList.add(playerSide);
				listList.add(foeList);
				Combat combat = mainGame.HugeBattle(Player.getPlayerWorld(),listList);
				if (combat.playerWon() > 0) {
					List<Person> endList = combat.getAllSurvivors();
					allyList.retainAll(endList);
					Town t = Player.player.getLocation();
					for (Person p: allyList) {
						if (p.getFlag(PersonFlag.IS_MOOK) || p.getFlag(PersonFlag.IS_SUMMON)) {
							continue;
						}
						//add added player side helpers to nearby town
						t.addOccupant(p.getMakeAgent(AgentGoal.NONE));
					}
				}else {
					List<Person> endList = combat.getAllSurvivors();
					foeList.retainAll(endList);
					for (Person p: foeList) {
						if (p.getFlag(PersonFlag.IS_SUMMON)) {
							continue;
						}
						wolvesPresent--;
						if (p.getFlag(PersonFlag.IS_MOOK)) {
							continue;
						}
						//spooky wolves
						Player.player.getWorld().addReoccuring(p.getMakeAgent(AgentGoal.SPOOKY));
					}
				}
			}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.NOTHING,2));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			b.minAreaLevel = 3;
			bumperList.add(b);

			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeWolf(level);

					extra.println(extra.PRE_BATTLE+"A wolf attacks you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
					}

				}};
		b.responses.add(new Response(DrawBane.MEAT,3));
		b.responses.add(new Response(DrawBane.NOTHING,3));
		b.responses.add(new Response(DrawBane.REPEL,-10));
		
		
		bumperList.add(b);
		
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeFellReaver(level);
					
					extra.println(extra.PRE_BATTLE+"A fell reaver appears!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
					}else {
					}
				}};
			b.responses.add(new Response(DrawBane.MEAT,2));
			b.responses.add(new Response(DrawBane.CEON_STONE,4));
			b.responses.add(new Response(DrawBane.DAYLIGHT,-0.5f));
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeEnt(level);
					
					extra.println(extra.PRE_BATTLE+"An ent appears!");
					Player.player.fightWith(p);
				}};
			b.responses.add(new Response(DrawBane.ENT_CORE,6));
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeVampire(level);
					
					extra.println(extra.PRE_BATTLE+"A vampire jumps from the shadows!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
					}else {
					}					
				}};
			b.responses.add(new Response(DrawBane.BLOOD,3));
			b.responses.add(new Response(DrawBane.GARLIC,-8));
			b.responses.add(new Response(DrawBane.SILVER,-.5));
			b.responses.add(new Response(DrawBane.DAYLIGHT,-2f));
			bumperList.add(b);
		 b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.getMugger(level);
					
					extra.println(extra.PRE_BATTLE+"A thief charges you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getLocation().addOccupant(p.setOrMakeAgentGoal(AgentGoal.NONE));
					}else {
					}
					
				}};
			b.responses.add(new Response(DrawBane.NOTHING,1));
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			b.responses.add(new Response(DrawBane.MONEY,2));
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBear(level);
					
					extra.println(extra.PRE_BATTLE+"A bear attacks you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
					}
				}

			};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.NOTHING,3));
			b.responses.add(new Response(DrawBane.HONEY,1));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			bumperList.add(b);
			b = new Bumper() {

				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeBat(level);
					
					extra.println(extra.PRE_BATTLE+"A bat attacks you!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
					}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.NOTHING,3));
			b.responses.add(new Response(DrawBane.REPEL,-8));
			b.responses.add(new Response(DrawBane.DAYLIGHT,-1f));
			bumperList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeUnicorn(level);
					
					extra.println(extra.PRE_BATTLE+"A unicorn accosts you for holding the virgin captive!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
					}
					
				}};
			b.responses.add(new Response(DrawBane.VIRGIN,6));
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
						}else {
							list = c.getNonSummonSurvivors();
							if (list.size() < count) {
							}
						}
					}else {
						Person p = RaceFactory.makeHarpy(level);
						extra.println(extra.PRE_BATTLE+"A harpy nestmother attacks!");
						Combat c = Player.player.fightWith(p);
						if (c.playerWon() > 0) {
						}else {
							Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
						}
					}
					
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,1.25));
			b.responses.add(new Response(DrawBane.SILVER,1.4));
			b.responses.add(new Response(DrawBane.GOLD,1.4));
			b.responses.add(new Response(DrawBane.REPEL,-1));
			bumperList.add(b);
			
			//ships
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.getMugger(level);
					
					extra.println(extra.PRE_BATTLE+"A pirate challenges you for your booty!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getLocation().addOccupant(p.setOrMakeAgentGoal(AgentGoal.NONE));
					}else {
					}
					
				}};
			b.responses.add(new Response(DrawBane.NOTHING,1));
			b.responses.add(new Response(DrawBane.SILVER,1));
			b.responses.add(new Response(DrawBane.GOLD,2));
			b.responses.add(new Response(DrawBane.MONEY,2));
			shipList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeDrudgerStock(level);
					
					extra.println(extra.PRE_BATTLE+"A drudger attacks your ship!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
					}else {
					}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,3));
			b.responses.add(new Response(DrawBane.NOTHING,2.5));
			shipList.add(b);
			
			b = new Bumper() {
				
				@Override
				public void activate(int level) {
					Person p = RaceFactory.makeHarpy(level);
					
					extra.println(extra.PRE_BATTLE+"A harpy ambushes your ship from a nearby shipwreck!");
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() < 0) {
						Player.player.getWorld().addReoccuring(new Agent(p,AgentGoal.SPOOKY));
					}else {
					}
					
				}};
			b.responses.add(new Response(DrawBane.MEAT,1.25));
			b.responses.add(new Response(DrawBane.SILVER,1.4));
			b.responses.add(new Response(DrawBane.GOLD,1.4));
			b.responses.add(new Response(DrawBane.REPEL,-1));
			shipList.add(b);
			
		
	}
}
