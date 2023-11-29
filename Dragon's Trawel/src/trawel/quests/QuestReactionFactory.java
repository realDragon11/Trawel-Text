package trawel.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.WorldGen;
import trawel.extra;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.towns.Town;
import trawel.towns.World;

public class QuestReactionFactory {

	public enum QKey{//while saves don't update, can rearrange, otherwise no
		FETCH, KILL, CLEANSE, COLLECT,
		GOOD, EVIL,
		LAWFUL, CHAOTIC,
		NORMAL_DEST,
		DEST_MOUNTAIN,DEST_WOODS,DEST_INN,DEST_SLUM, DEST_WHUT,DEST_GUILD,
		GIVE_INN,GIVE_MGUILD,GIVE_SLUM, GIVE_FORT, GIVE_WHUT, GIVE_RGUILD, GIVE_HGUILD,
		FIRE_ALIGN, KNOW_ALIGN//aligns used for collecting
	}
	
	public static List<QuestReaction> reactions = new ArrayList<QuestReaction>();
	
	public QuestReactionFactory() {
		/*//EXAMPLE
		reactions.add(new QuestReaction(new QKey[] {},new QKey[] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				// method stub
				
			}}) );
		*/
		reactions.add(new QuestReaction(new QKey[] {QKey.NORMAL_DEST},new QKey[] {QKey.EVIL},new QKey[][] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				Person p = RaceFactory.getPeace(bumperLocation.getTier());
				extra.println("A traveler greets you and notices your quest ("+q.name() +") and asks if you want directions.");
				p.getBag().graphicalDisplay(1, p);
				extra.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> mList = new ArrayList<MenuItem>();
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return "Ask for directions.";
							}

							@Override
							public boolean go() {
								Town t = q.nextLocation();
								if (t != null) {
									WorldGen.pathToTown(t);
								}else {
									extra.println("They cannot seem to say the instructions.");
								}
								
								return false;
							}});
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return extra.PRE_BATTLE + "Attack them!";
							}

							@Override
							public boolean go() {
								Combat c = Player.player.fightWith(p);
								if (c.playerWon() >= 0) {
									
								}else {
									extra.println(p.getName() +" wanders off, regreting their helpfulness.");
									bumperLocation.addOccupant(p.getMakeAgent(AgentGoal.NONE));
								}
								
								return true;
							}});
						mList.add(new MenuBack("Leave."));
						return mList;
					}});
					Networking.clearSide(1);
			}}) );
		
		reactions.add(new QuestReaction(new QKey[] {QKey.KILL},new QKey[] {},new QKey[][] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				Person p = RaceFactory.getDueler(bumperLocation.getTier());
				extra.println(extra.PRE_BATTLE +p.getName() + " appears, claiming that they were hired to defend " + q.targetName +"!");
				
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() >= 0) {
					
				}else {
					extra.println(p.getName() +" wanders off, job well done.");
					bumperLocation.addOccupant(p.getMakeAgent(AgentGoal.NONE));
				}
			}}) );
		reactions.add(new QuestReaction(new QKey[] {QKey.KILL,QKey.EVIL},new QKey[] {},new QKey[][] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				Person p = RaceFactory.getLawman(bumperLocation.getTier());
				extra.println(extra.PRE_BATTLE +p.getName() + " attacks you for traveling to murder " + q.targetName +"!");
				
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() >= 0) {
					
				}else {
					extra.println(p.getName() +" wanders off, job well done.");
					bumperLocation.addOccupant(p.getMakeAgent(AgentGoal.NONE));
				}
			}}) );
		
		reactions.add(new QuestReaction(new QKey[] {QKey.EVIL},new QKey[] {QKey.LAWFUL},
				new QKey[][] {new QKey[] {QKey.GIVE_INN,QKey.GIVE_RGUILD,QKey.GIVE_SLUM}
						}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				Person p = RaceFactory.makeMuggerWithTitle(bumperLocation.getTier());
				extra.println("A figure approaches you and claims that they'll take the reward for "+q.name+", which they overheard!");
				p.getBag().graphicalDisplay(1,p);
				int payOffCost = Math.round(p.getUnEffectiveLevel()*3);
				extra.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return extra.PRE_BATTLE+"Defend yourself!";
							}

							@Override
							public boolean go() {
								Combat c = Player.player.fightWith(p);
								if (c.playerWon() > 0) {
									
								}else {
									extra.println("They run off laughing about the job.");
									bumperLocation.addOccupant(p.getMakeAgent(AgentGoal.NONE));
								}
								return true;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return extra.PRE_MAYBE_BATTLE+"Attempt to flee...";
							}

							@Override
							public boolean go() {
								Person playp = Player.player.getPerson();
								if (playp.contestedRoll(p,playp.getDexterity(),p.getDexterity()) >= 0) {
									extra.println("You run away!");
									return true;
								}else {
									//adds a small handicap, note that these tend to get cleared AFTER battle so this should work
									playp.addEffect(Effect.EXHAUSTED);
									extra.println(extra.PRE_BATTLE+"They catch up, prepare to defend yourself!");
									Combat c = Player.player.fightWith(p);
									if (c.playerWon() > 0) {
										
									}else {
										extra.println("They run off laughing about the job.");
										bumperLocation.addOccupant(p.getMakeAgent(AgentGoal.NONE));
									}
									return true;
								}
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Try to pay them off. ("+World.currentMoneyDisplay(payOffCost)+")";
							}

							@Override
							public boolean go() {
								if (Player.player.getGold() < payOffCost) {
									extra.println(extra.PRE_BATTLE+"They laugh that you can't afford them!");
									Combat c = Player.player.fightWith(p);
									if (c.playerWon() > 0) {
										
									}else {
										extra.println("They run off laughing about the job.");
										bumperLocation.addOccupant(p.getMakeAgent(AgentGoal.NONE));
									}
									return true;
								}
								Player.player.addGold(-payOffCost);
								extra.println("They count the money and leave.");
								return true;
							}});
						return list;
					}});
				Networking.clearSide(1);
			}}) );
		
		Collections.shuffle(reactions);
	}
	
	public class QuestReaction{
		public final List<QKey> mandates;
		public final List<QKey> forbids;
		public final List<List<QKey>> needsOne;
		
		public QuestTriggerEvent qte;
		
		public QuestReaction(QKey[] mandated,QKey[] forbidden,QKey[][] needOnes,QuestTriggerEvent qet) {
			mandates = Arrays.asList(mandated);
			forbids = Arrays.asList(forbidden);
			needsOne = new ArrayList<List<QKey>>();
			for (QKey[] arr: needOnes) {
				needsOne.add(Arrays.asList(arr));
			}
			qte = qet;
		}
	}
	
	
	public interface QuestTriggerEvent{
		public void trigger(BasicSideQuest q, Town bumperLocation);
	}


	public static boolean runMe(Town t) {
		if (Player.player.sideQuests.size() == 0) {
			return false;
		}
		if (extra.chanceIn(2,3+Player.player.roadGracePeriod)) {
			Player.player.roadGracePeriod--;//can go into negatives but at -1 will be 100% chance to continue and get set to 0
			return false;
		}
		BasicSideQuest side = extra.randList(Player.player.sideQuests).reactionQuest();
		if (side == null) {
			Player.player.roadGracePeriod = 0;//so it doesn't dip into far negatives
			return false;
		}
		conditional: for (QuestReaction qr: reactions) {
			for (QKey forbid: qr.forbids) {
				if (side.qKeywords.contains(forbid)) {
					continue conditional;
				}
			}
			if (qr.mandates.size() > 0) {
				if (!side.qKeywords.containsAll(qr.mandates)) {
					continue conditional;
				}
			}
			if (qr.needsOne.size() > 0) {
				for (int i = qr.needsOne.size()-1;i>=0;i--) {
					if (Collections.disjoint(qr.needsOne.get(i),side.qKeywords)){
						continue conditional;
					}
				}
			}
			side.reactionsLeft--;
			qr.qte.trigger(side, t);
			Collections.shuffle(reactions);
			Player.player.roadGracePeriod = 5;
			return true;
		}
		Player.player.roadGracePeriod = 0;//so it doesn't dip into far negatives
		return false;
	}
}
