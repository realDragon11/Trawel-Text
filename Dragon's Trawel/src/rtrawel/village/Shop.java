package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import rtrawel.items.Item;
import rtrawel.unit.RCore;
import trawel.helper.methods.extra;

public class Shop implements Content{
	
	public List<String> items = new ArrayList<String>();
	
	private String name;
	
	public Shop(String s) {
		name = s;
	}

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
			extra.println(Party.party.gold + " gold");
			extra.println("1 back");
			Party.party.refreshItemKeys();
			for (int i = 1;i < Party.party.itemKeys.size()+1;i++) {
				Item it = RCore.getItemByName(Party.party.itemKeys.get(i-1));
				extra.println(it.display());
				extra.println((i+1) + " cost: "+ it.cost() + " count: " + Party.party.items.get(Party.party.itemKeys.get(i-1)));
			}
			int in = extra.inInt(Party.party.itemKeys.size()+1);
			if (in == 1) {
				return;
			}
			Item it = RCore.getItemByName(Party.party.itemKeys.get(in-2));
			int canSell = Party.party.items.get(Party.party.itemKeys.get(in-2));
			extra.println("Sell how many?");
			in = extra.inInt(canSell);
			Party.party.gold+=it.cost()*in/2;
			for (int i = 0;i< in;i++) {
				Party.party.popItem(it);
			}
			
		}
	}

	private void buyStuff() {
		while (true) {
			extra.println(Party.party.gold + " gold");
			extra.println("1 back");
			for (int i = 1;i < items.size()+1;i++) {
				Item it = RCore.getItemByName(items.get(i-1));
				extra.println(it.display());
				extra.println((i+1) + " cost: "+ it.cost());
			}
			int in = extra.inInt(items.size()+1);
			if (in == 1) {
				return;
			}
			Item it = RCore.getItemByName(items.get(in-2));
			if (it.cost() > Party.party.gold) {
				extra.println("You're to poor!");
				continue;
			}
			int canBuy = Party.party.gold/it.cost();
			extra.println("Buy how many? (" + canBuy+" max)");
			in = extra.inInt(canBuy);
			Party.party.gold-=it.cost()*in;
			Party.party.addItem(it.getName(),in);
			
		}
		
	}

	@Override
	public String name() {
		return name;
	}

}
