package trawel.quests.locations;

import java.io.Serializable;

import trawel.quests.types.Quest;
import trawel.towns.features.Feature;

public class QuestR implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final int QRID;
	public final String name;
	public final Quest overQuest;
	public final Feature locationF;
	public boolean enabled;
	
	public QuestR(int _QRID, String _name, Quest _overQuest, Feature _feature) {
		QRID = _QRID;
		name = _name;
		overQuest = _overQuest;
		locationF = _feature;
	}
	
	public String getName() {
		return name;
	}
	
	public void go() {
		overQuest.questReaction(QRID);
	}
	
	public void cleanup() {
		enabled = false;
	}
	
	public void enable() {
		enabled = true;
	}
}
