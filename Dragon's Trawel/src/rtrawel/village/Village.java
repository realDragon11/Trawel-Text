package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import trawel.extra;

public class Village {

	public List<Content> conts = new ArrayList<Content>();
	public String name = "";
	public static Menu menu = new Menu();
	
	/**
	 * adds the non-unique menu
	 */
	public Village() {
		conts.add(menu);
	}

	public void doRandomBattle() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean go() {
		int i = 1;
		for (Content c: conts) {
			extra.println(i + " " + c.name());
			i++;
		}
		int in = extra.inInt(i-1);
		i = 1;
		for (Content c: conts) {
			if (in == i) {
				return c.go();
			}
			i++;
		}
		return false;
	}
}
