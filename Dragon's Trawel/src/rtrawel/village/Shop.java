package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import rtrawel.items.Item;
import rtrawel.unit.RCore;
import trawel.core.Input;
import trawel.core.Print;

public class Shop implements Content{
	
	public List<String> items = new ArrayList<String>();
	
	private String name;
	
	public Shop(String s) {
		name = s;
	}

	@Override
	public boolean go() {
		while (true) {
			Print.println("1 back");
			Print.println("2 buy");
			Print.println("3 sell");
			int in = Input.inInt(3);
			switch (in){
				case 1: return false;
				case 2: buyStuff();break;
				case 3: sellStuff();break;
			}
		}
	}

	private void sellStuff() {
		while (true) {
			Print.println(Party.party.gold + " gold");
			Print.println("1 back");
			Party.party.refreshItemKeys();
			for (int i = 1;i < Party.party.itemKeys.size()+1;i++) {
				Item it = RCore.getItemByName(Party.party.itemKeys.get(i-1));
				Print.println(it.display());
				Print.println((i+1) + " cost: "+ it.cost() + " count: " + Party.party.items.get(Party.party.itemKeys.get(i-1)));
			}
			int in = Input.inInt(Party.party.itemKeys.size()+1);
			if (in == 1) {
				return;
			}
			Item it = RCore.getItemByName(Party.party.itemKeys.get(in-2));
			int canSell = Party.party.items.get(Party.party.itemKeys.get(in-2));
			Print.println("Sell how many?");
			in = Input.inInt(canSell);
			Party.party.gold+=it.cost()*in/2;
			for (int i = 0;i< in;i++) {
				Party.party.popItem(it);
			}
			
		}
	}

	private void buyStuff() {
		while (true) {
			Print.println(Party.party.gold + " gold");
			Print.println("1 back");
			for (int i = 1;i < items.size()+1;i++) {
				Item it = RCore.getItemByName(items.get(i-1));
				Print.println(it.display());
				Print.println((i+1) + " cost: "+ it.cost());
			}
			int in = Input.inInt(items.size()+1);
			if (in == 1) {
				return;
			}
			Item it = RCore.getItemByName(items.get(in-2));
			if (it.cost() > Party.party.gold) {
				Print.println("You're to poor!");
				continue;
			}
			int canBuy = Party.party.gold/it.cost();
			Print.println("Buy how many? (" + canBuy+" max)");
			in = Input.inInt(canBuy);
			Party.party.gold-=it.cost()*in;
			Party.party.addItem(it.getName(),in);
			
		}
		
	}

	@Override
	public String name() {
		return name;
	}

}
