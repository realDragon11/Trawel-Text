package trawel.quests;

import java.io.Serializable;

import trawel.towns.Feature;

public class QuestR implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final int QRID;
	public final String name;
	public final Quest overQuest;
	public final Feature locationF;
	
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
		locationF.removeQR(this);
	}
	
	public void enable() {
		locationF.addQR(this);
	}
}
