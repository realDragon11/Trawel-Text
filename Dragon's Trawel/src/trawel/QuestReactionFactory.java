package trawel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestReactionFactory {

	public enum QKey implements java.io.Serializable {
		FETCH, KILL, CLEANSE, 
		GOOD, EVIL,
		LAWFUL, CHAOTIC,
		DEST_MOUNTAIN,DEST_WOODS,DEST_INN,
		GIVE_INN,GIVE_MGUILD,GIVE_SLUM, DEST_SLUM
	}
	
	public static List<QuestReaction> reactions = new ArrayList<QuestReaction>();
	
	public QuestReactionFactory() {
		/*//EXAMPLE
		reactions.add(new QuestReaction(new QKey[] {},new QKey[] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				// TODO Auto-generated method stub
				
			}}) );
		*/
		reactions.add(new QuestReaction(new QKey[] {},new QKey[] {QKey.DEST_WOODS,QKey.DEST_MOUNTAIN,QKey.DEST_INN,QKey.DEST_SLUM}, new QuestTriggerEvent() {

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
								return "ask for directions";
							}

							@Override
							public boolean go() {
								if (q.target.locationT != null) {
									WorldGen.pathToTown(q.target.locationT);
								}else {
									extra.println("They cannot seem to say the instructions.");
								}
								
								return false;
							}});
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return Networking.AGGRO + "attack them";
							}

							@Override
							public boolean go() {
								if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
									
								}else {
									extra.println(p.getName() +" wanders off, regreting their helpfulness.");
								}
								
								return true;
							}});
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return "leave";
							}

							@Override
							public boolean go() {
								return true;
							}});
						return mList;
					}});
			}}) );
		
		reactions.add(new QuestReaction(new QKey[] {QKey.KILL},new QKey[] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				Person p = RaceFactory.getDueler(bumperLocation.getTier());
				extra.println(Networking.AGGRO +p.getName() + " appears, claiming that they were hired to defend " + q.targetName +"!");
				
				if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
					
				}else {
					extra.println(p.getName() +" wanders off, job well done.");
				}
			}}) );
		reactions.add(new QuestReaction(new QKey[] {QKey.KILL,QKey.EVIL},new QKey[] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				Person p = RaceFactory.getLawman(bumperLocation.getTier());
				extra.println(Networking.AGGRO +p.getName() + " attacks you for traveling to murder " + q.targetName +"!");
				
				if (mainGame.CombatTwo(Player.player.getPerson(),p).equals(Player.player.getPerson())) {
					
				}else {
					extra.println(p.getName() +" wanders off, job well done.");
				}
			}}) );
		
		Collections.shuffle(reactions);
	}
	
	public class QuestReaction{
		public QKey[] mandates;
		public QKey[] needsOne;
		
		public QuestTriggerEvent qte;
		
		public QuestReaction(QKey[] man,QKey[] ned,QuestTriggerEvent qet) {
			mandates = man;
			needsOne = ned;
			qte = qet;
		}
	}
	
	
	public interface QuestTriggerEvent{
		public void trigger(BasicSideQuest q, Town bumperLocation);
	}


	public static boolean runMe(Town t) {
		if (Player.player.sideQuests.size() == 0 || extra.chanceIn(1,3)) {
			return false;
		}
		BasicSideQuest side = extra.randList(Player.player.sideQuests).reactionQuest();
		if (side == null) {
			return false;
		}
		boolean evented = false;
		for (QuestReaction qr: reactions) {
			boolean greenStep = true;
			if (qr.mandates.length == 0) {
				//greenStep = true;
			}else {
				for (QKey mandate: qr.mandates) {
					if (!side.qKeywords.contains(mandate)) {
						greenStep = false;
						break;
					}
				}
			}
			if (greenStep == false) {
				continue;
			}
			greenStep = false;
			if (qr.needsOne.length == 0) {
				greenStep = true;
			}else {
				for (QKey req: qr.needsOne) {
					if (side.qKeywords.contains(req)) {
						greenStep = true;
						break;
					}
				}
			}
			if (greenStep == false) {
				continue;
			}
			evented = true;
			side.reactionsLeft--;
			qr.qte.trigger(side, t);
			break;
			
			
		}
		if (evented) {
			Collections.shuffle(reactions);
			return true;
		}
		return false;
	}
}
