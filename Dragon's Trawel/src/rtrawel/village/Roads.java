package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import trawel.core.Input;
import trawel.core.Print;

public class Roads implements Content {

	public List<Connection> connects = new ArrayList<Connection>(); 
	
	@Override
	public boolean go() {
		int i = 1;
		Print.println(i++ + " back");
		Print.println(i++ + " wander around");
		for (Connection c: connects) {
			Print.println(i + " " + c.name(Party.party.curVillage));
			i++;
		}
		
		
		int in = Input.inInt(i);
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
