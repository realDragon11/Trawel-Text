package trawel.towns.misc;

import java.util.List;

import trawel.extra;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Well extends Feature{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Well(String name) {
		this.name = name;
		tutorialText = "This is a well.";
	}
	
	@Override
	public String getColor() {
		return extra.PRE_TELE;//lol
	}

	@Override
	public void go() {
		return;

	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		return null;
	}
	
	

}
