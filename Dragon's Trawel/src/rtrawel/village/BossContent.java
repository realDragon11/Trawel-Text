package rtrawel.village;

import rtrawel.battle.Fight;
import trawel.helper.methods.extra;

public class BossContent implements Content{

	public String name;
	public Fight fight;
	public Village village;
	public int wanders;
	public BossContent(String n, Fight f,Village v,int wanderCount) {
		fight = f;
		name = n;
		village = v;
		wanders = wanderCount;
	}
	
	@Override
	public boolean go() {
		if (village.wanderCombo < wanders) {
			extra.println("You haven't traveled to the boss yet.");
			return false;
		}
		if (fight.go()) {
			village.conts.remove(this);
			return true;
		}else {
			return false;
		}
	}

	@Override
	public String name() {
		return name + ((village.wanderCombo < wanders) ?  " " + village.wanderCombo +"/"+ wanders + " explores" : "") ;
	}

}
