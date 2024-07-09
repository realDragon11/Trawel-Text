package rtrawel.village;

import java.util.List;

import rtrawel.battle.Party;
import rtrawel.unit.RCore;
import rtrawel.unit.RUnit;
import trawel.core.Input;
import trawel.core.Print;

public class Church implements Content {

	@Override
	public boolean go() {
		while (true) {
			Print.println("1 res");
			Print.println("2 save");
			Print.println("3 back");
			switch (Input.inInt(3)) {
			case 1:
				List<RUnit> rs = Party.party.getDead();
				int size = rs.size();
				if (size > 0) {
					Print.println("Res who?");
					for (int i = 0;i < size;i++) {
						Print.println((i+1) +  " " + rs.get(i).getName());
					}
					RUnit r = rs.get(Input.inInt(size)-1);
					r.heal(1,0);
					r.alive = true;
				}
				break;
			case 2:
				RCore.save();
			break;
			case 3:
				return false;
			}
		}
	}

	@Override
	public String name() {
		return "church";
	}

}
