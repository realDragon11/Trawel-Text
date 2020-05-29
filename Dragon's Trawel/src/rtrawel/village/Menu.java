package rtrawel.village;

import rtrawel.battle.Party;
import rtrawel.items.Item;
import rtrawel.unit.RCore;
import rtrawel.unit.RUnit;
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
				doBag();
			}
		}
	}
	
	
	private void doBag() {
		if (Party.party.itemKeys.size() == 0) {
			extra.println("Your bag is empty!");
			return;
		}
		int in2 = 0;
		while (in2 != (Party.party.itemKeys.size()+1)) {
			Party.party.refreshItemKeys();
			Party.party.displayItems();
			extra.println((Party.party.itemKeys.size()+1) + " back");
			in2 = extra.inInt(Party.party.itemKeys.size()+1);
			if (in2 <= Party.party.itemKeys.size()) {
				goItem(in2);
			}
		}
	}
	private void goItem(int in2) {
		String str = Party.party.popItem(in2-1);
		Item item = RCore.getItemByName(str);
		switch (item.getItemType()) {
		case ARMOR:
			//TODO
			break;
		case CONSUMABLE:
			extra.println("Who wants to apply the item?");
			RUnit one = Party.party.getUnit();
			extra.println("Who wants to get the item applied to them?");
			RUnit two = Party.party.getUnit();
			
			break;
		case NONE:
			extra.println("This item is material-only.");
			break;
		case WEAPON:
			break;
		
		}
	}

	@Override
	public String name() {
		return "menu";
	}

}
