package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import trawel.extra;

public class Roads implements Content {

	public List<Connection> connects = new ArrayList<Connection>(); 
	
	@Override
	public boolean go() {
		int i = 1;
		for (Connection c: connects) {
			extra.println(i + " " + c.name(Party.party.curVillage));
			i++;
		}
		extra.println(i++ + " wander around");
		extra.println(i + " back");
		int in = extra.inInt(i);
		i = 1;
		for (Connection c: connects) {
			if (in == i) {
				Party.party.curVillage = c.go(Party.party.curVillage);
				return true;
			}
			i++;
		}
		if (in == i) {
			Party.party.curVillage.doRandomBattle();
			return false;
		}
		i++;
		if (in == i) {
			return true;
		}
		return false;
	}

	@Override
	public String name() {
		return "roads";
	}

}
