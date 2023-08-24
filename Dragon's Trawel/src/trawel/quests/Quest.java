package trawel.quests;

import java.io.Serializable;
import java.util.List;

import trawel.quests.QuestReactionFactory.QKey;

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
	
}
