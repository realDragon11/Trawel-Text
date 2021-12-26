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
	
	public QuestReactionFactory() {
		
	}
	
	public class QuestReaction{
		public List<QKey> mandates = new ArrayList<QKey>();
		public List<QKey> needsOne = new ArrayList<QKey>();
		
		public QuestTriggerEvent qte;
	}
	
	
	public interface QuestTriggerEvent{
		public void trigger(Quest q);
	}
}
