package trawel.towns.features.outdated;

import java.util.List;

import trawel.core.Networking.Area;
import trawel.helper.constants.TrawelColor;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.features.Feature;

public class Well extends Feature{

	private static final long serialVersionUID = 1L;

	public Well(String name) {
		this.name = name;
	}
	
	@Override
	public String getColor() {
		return TrawelColor.PRE_TELE;//lol
	}
	
	@Override
	public String nameOfFeature() {
		return "Well";
	}
	
	@Override
	public String nameOfType() {
		return "Well";
	}
	
	@Override
	public Area getArea() {
		return Area.MISC_SERVICE;
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
