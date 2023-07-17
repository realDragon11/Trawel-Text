package trawel;

import java.util.List;

import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class TravelingFeature extends Feature implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean hasSomething  = false;
	private Feature feature;
	private double timePassed = 30;
	@Override
	public void go() {
		this.feature.go();
	}
	protected int tier, curTier;
	
	public TravelingFeature(int tier) {
		this.tier = tier;
		tutorialText = "Traveling features and celebrations can be host to a wide array of things.";
		
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		timePassed += time;
		if (hasSomething) {
			feature.passTime(time, calling);
		}
		if (timePassed > extra.randRange(20,81)) {
			timePassed = 0;
			newFeature();
		}
		return null;
	}
	
	public void newFeature() {
		//switch statement with features that need to be added
		//and also non-features
		curTier = extra.zeroOut(tier+extra.randRange(1,5)-4)+1;
		hasSomething = true;
		switch(extra.randRange(0,8)) {
		default: hasSomething = false;break;
		//case 1: feature = new Gambler("traveling gambler","cups",100);break;
		case 2: feature = new Oracle("traveling oracle",curTier);break;
		case 3: feature = new Arena("traveling "+extra.choose(randomLists.randomColor(),randomLists.randomElement())+" arena",curTier,1,3,12,0);break;
		case 4: feature = new Store(curTier);
				feature.name = "traveling "+ feature.name;
				feature.town = town;
				break;
		case 5: feature = new Inn("celebration",curTier,town,null);break;
		}
		if (hasSomething) {
		name = feature.name;}
	}

}
