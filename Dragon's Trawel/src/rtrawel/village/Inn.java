package rtrawel.village;

import rtrawel.battle.Party;
import rtrawel.unit.RUnit;
import trawel.extra;

public class Inn implements Content {

	private int cost;
	public Inn(int iCost) {
		cost = iCost;
	}
	
	@Override
	public boolean go() {
		extra.println("Spend " + cost + " gold on a night's rest?");
		if (Party.party.gold >= cost && extra.yesNo()) {
			Party.party.gold -= cost;
			for (RUnit r: Party.party.list) {
				if (r.isAlive()) {
					r.refresh();
				}
			}
		}
		return false;
	}

	@Override
	public String name() {
		return "inn";
	}

}
