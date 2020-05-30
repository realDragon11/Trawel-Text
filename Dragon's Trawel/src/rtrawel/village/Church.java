package rtrawel.village;

import java.util.List;

import rtrawel.battle.Party;
import rtrawel.unit.RCore;
import rtrawel.unit.RUnit;
import trawel.extra;

public class Church implements Content {

	@Override
	public boolean go() {
		while (true) {
			extra.println("1 res");
			extra.println("2 save");
			extra.println("3 back");
			switch (extra.inInt(3)) {
			case 1:
				List<RUnit> rs = Party.party.getDead();
				int size = rs.size();
				if (size > 0) {
					extra.println("Res who?");
					for (int i = 0;i < size;i++) {
						extra.println(rs.get(i).getName());
					}
					RUnit r = rs.get(extra.inInt(size)-1);
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
