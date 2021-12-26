package trawel;

import java.util.ArrayList;
import java.util.List;

public class QuestReactionFactory {

	public enum QKey implements java.io.Serializable {
		FETCH, KILL, CLEANSE, 
		GOOD, EVIL,
		LAWFUL, CHAOTIC,
		DEST_MOUNTAIN,DEST_WOODS,DEST_INN,
		GIVE_INN,GIVE_MGUILD,
	}
	
	public List<QuestReaction> reactions = new ArrayList<QuestReaction>();
	
	public QuestReactionFactory() {
		/*//EXAMPLE
		reactions.add(new QuestReaction(new QKey[] {},new QKey[] {}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q, Town bumperLocation) {
				// TODO Auto-generated method stub
				
			}}) );
		*/
		reactions.add(new QuestReaction(new QKey[] {},new QKey[] {QKey.DEST_WOODS,QKey.DEST_MOUNTAIN,QKey.DEST_WOODS}, new QuestTriggerEvent() {

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
}
