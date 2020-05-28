package rtrawel.unit;

import java.util.List;

public class TargetGroup {

	public List<RUnit> targets;
	
	@Override
	public String toString() {
		String str = "";
		for (RUnit r: targets) {
			str+= r.getName()+", ";
		}
		return str;
	}
}
