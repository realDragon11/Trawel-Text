package rtrawel.village;

import rtrawel.battle.Fight;

public class Connection {

	public Village village1, village2;
	public Fight fight;
	
	public Connection(Village v1, Village v2,Fight f) {
		fight = f;
		village1 = v1;
		village2 = v2;
	}
	
	public Village go(Village curVillage) {
		if (fight == null) {
			return (curVillage == village1 ? village2 : village1);
		}
		if (fight.go()) {
			fight = null;
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
