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
			public void trigger(BasicSideQuest q) {
				// TODO Auto-generated method stub
				
			}}) );
		*/
		reactions.add(new QuestReaction(new QKey[] {},new QKey[] {QKey.DEST_WOODS,QKey.DEST_MOUNTAIN}, new QuestTriggerEvent() {

			@Override
			public void trigger(BasicSideQuest q) {
				// TODO Auto-generated method stub
				
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
		public void trigger(BasicSideQuest q);
	}
}
