package trawel.towns.misc;

import java.util.List;

import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Well extends Feature implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Well(String name) {
		this.name = name;
		tutorialText = "This is a well.";
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
