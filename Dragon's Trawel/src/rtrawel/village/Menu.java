package rtrawel.village;

import rtrawel.battle.Party;
import trawel.extra;

public class Menu implements Content {

	@Override
	public boolean go() {
		int in = 0;
		while (in != 1) {
			extra.println("1 back");
			extra.println("2 inventory");
			in = extra.inInt(2);
			switch (in) {
			case 2:
				doInv();
			}
		}
		// TODO
		return false;
	}

	private void doInv() {
		int in2 = 0;
		while (in2 != 1) {
			extra.println("1 back");
			extra.println("2 bag");
			extra.println("3 assign items");
			in2 = extra.inInt(3);
			switch (in2) {
			case 2:
				Party.party.displayItems();
			}
		}
	}

	@Override
	public String name() {
		return "menu";
	}

}
