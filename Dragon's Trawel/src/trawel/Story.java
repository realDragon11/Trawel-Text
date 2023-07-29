package trawel;

import trawel.personal.Person;
import trawel.towns.Feature;

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
}
