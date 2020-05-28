package rtrawel.unit;

import java.util.ArrayList;
import java.util.List;

public class TargetGroup {

	public List<RUnit> targets = new ArrayList<RUnit>();
	
	@Override
	public String toString() {
		String str = "";
		for (RUnit r: targets) {
			str+= r.getName()+", ";
		}
		return str;
	}
	
	public TargetGroup() {
		
	}
	
	public TargetGroup(RUnit i) {
		targets.add(i);
	}
}
