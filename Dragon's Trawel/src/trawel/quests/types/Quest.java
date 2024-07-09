package trawel.quests.types;

import java.io.Serializable;
import java.util.List;

import trawel.quests.events.QuestReactionFactory;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.locations.QuestR;
import trawel.towns.Town;

public interface Quest extends Serializable {

	public String name();
	
	public String desc();
	
	public void fail();
	
	public void complete();
	
	public void take();
	
	public void questTrigger(TriggerType type, String trigger, int num);
	
	public BasicSideQuest reactionQuest();
	
	public enum TriggerType{
		CLEANSE,COLLECT
	}

	public List<String> triggers();

	public List<QKey> getKeys();

	public void questReaction(int QRID);

	public Town nextLocation();

	public List<QuestR> getActiveQRs();
	
}
