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
		extra.println(i++ + " back");
		extra.println(i++ + " wander around");
		for (Connection c: connects) {
			extra.println(i + " " + c.name(Party.party.curVillage));
			i++;
		}
		
		
		int in = extra.inInt(i);
		i = 1;
		if (in == i) {
			return true;
		}
		i++;
		if (in == i) {
			Party.party.curVillage.doRandomBattle();
			return false;
		}
		
		i++;
		for (Connection c: connects) {
			if (in == i) {
				Party.party.curVillage = c.go(Party.party.curVillage);
				return true;
			}
			i++;
		}

		
		return false;
	}

	@Override
	public String name() {
		return "roads";
	}

}
