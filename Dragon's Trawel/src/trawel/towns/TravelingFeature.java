package trawel.towns;

import java.util.List;

import trawel.extra;
import trawel.randomLists;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.fight.Arena;
import trawel.towns.services.Inn;
import trawel.towns.services.Oracle;
import trawel.towns.services.Store;

public class TravelingFeature extends Feature{

	private static final long serialVersionUID = 1L;
	public boolean hasSomething  = false;
	private Feature feature;
	private double timePassed = 30;
	@Override
	public void enter() {
		this.feature.enter();
	}
	protected int curTier;
	
	public TravelingFeature(Town town) {
		this.town = town;
		this.tier = town.getTier();
		tutorialText = "Traveling features and celebrations can be host to a wide array of things.";
	}
	
	@Override
	public String getColor() {
		return (hasSomething ? feature.getColor() : extra.F_SPECIAL);
	}
	
	@Override
	public boolean canShow() {
		return hasSomething;
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
		case 4: feature = new Store(town, curTier,6);
				feature.setName("traveling "+ feature.getName());
				break;
		case 5: feature = new Inn("celebration",curTier,town,null);break;
		}
		if (hasSomething) {
			name = feature.getName();
			feature.setTownInternal(town);
		}
	}

	@Override
	protected void go() {
		throw new UnsupportedOperationException("Traveling features can only be entered, not go'd");
	}

}
