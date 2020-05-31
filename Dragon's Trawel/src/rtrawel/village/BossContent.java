package rtrawel.village;

import rtrawel.battle.Fight;

public class BossContent implements Content{

	public String name;
	public Fight fight;
	public Village village;
	public BossContent(String n, Fight f,Village v) {
		fight = f;
		name = n;
		village = v;
	}
	
	@Override
	public boolean go() {
		if (fight.go()) {
			village.conts.remove(this);
			return true;
		}else {
			return false;
		}
	}

	@Override
	public String name() {
		return name;
	}

}
