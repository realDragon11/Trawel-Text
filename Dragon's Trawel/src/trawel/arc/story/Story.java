package trawel.arc.story;

import trawel.personal.Person;
import trawel.personal.classless.Perk;
import trawel.towns.features.Feature;

public abstract class Story implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	protected int lastKnownLevel = 0;
	public abstract void storyStart();
	public void onDeath() {}
	public void onDeathPart2(){}
	public void levelUp(int level){}
	public void enterFeature(Feature f) {}
	public void startFight(boolean massFight) {}
	public void winFight(boolean massFight) {}
	public void setPerson(Person p, int index) {}
	/**
	 * does not apply to EVERY perk gained, and might call the same perk multiple times
	 * <br>
	 * keep your own list
	 */
	public void perkTrigger(Perk perk) {}
}
