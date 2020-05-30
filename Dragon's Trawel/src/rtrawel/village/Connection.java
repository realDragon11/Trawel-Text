package rtrawel.village;

import rtrawel.EventFlag;
import rtrawel.battle.Fight;

public class Connection {

	public Village village1, village2;
	public Fight fight;
	public String evF;
	
	public Connection(Village v1, Village v2,Fight f,String eventFlag) {
		if (EventFlag.eventFlag.getEF(eventFlag) == 0) {
			fight = f;
		}else {
			fight = null;
		}
		evF = eventFlag;
		
		village1 = v1;
		village2 = v2;
		v1.addRoad(this);
		v2.addRoad(this);
	}
	
	public Village go(Village curVillage) {
		if (fight == null) {
			return (curVillage == village1 ? village2 : village1);
		}
		if (fight.go()) {
			fight = null;
			EventFlag.eventFlag.setEF(evF,1);
			return (curVillage == village1 ? village2 : village1);
		}
		return curVillage;
	}
	
	public String name(Village curVillage) {
		String bname = (curVillage == village1 ? village2.name : village1.name);
		if (fight != null) {
			bname += " (boss blocked)";
		}
		return bname;
	}
}
