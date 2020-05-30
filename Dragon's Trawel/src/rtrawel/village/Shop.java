package rtrawel.village;

import rtrawel.battle.Party;
import rtrawel.items.Item;
import rtrawel.unit.RCore;
import trawel.extra;

public class Shop implements Content{

	@Override
	public boolean go() {
		while (true) {
			extra.println("1 back");
			extra.println("2 buy");
			extra.println("3 sell");
			int in = extra.inInt(3);
			switch (in){
				case 1: return false;
				case 2: buyStuff();break;
				case 3: sellStuff();break;
			}
		}
	}

	private void sellStuff() {
		while (true) {
			extra.println("1 back");
			Party.party.refreshItemKeys();
			for (int i = 1;i < Party.party.itemKeys.size();i++) {
				Item it = RCore.getItemByName(Party.party.itemKeys.get(i-1));
				it.display();
				extra.println((i+1) + " cost: "+ it.cost() + " count: " + Party.party.items.get(Party.party.itemKeys.get(i-1)));
			}
			int in = extra.inInt(Party.party.itemKeys.size()+1);
			if (in == 1) {
				return;
			}
			Item it = RCore.getItemByName(Party.party.itemKeys.get(in-2));
			if (it.cost() > Party.party.gold) {
				extra.println("You're to poor!");
				continue;
			}
			
			
		}
	}

	private void buyStuff() {
		while (true) {
			
		}
		
	}

	@Override
	public String name() {
		return "shop";
	}

}
