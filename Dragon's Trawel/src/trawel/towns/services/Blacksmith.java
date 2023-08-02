package trawel.towns.services;

import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.personal.item.Item;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;

public class Blacksmith extends Feature {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int tier;
	private double time = 0;
	private Store store;
	
	public Blacksmith(String name,int tier, Store s){
		this.name = name;
		this.tier = tier;
		this.store = s;
		tutorialText = "Blacksmith's add new items to stores, and can improve items.";
	}
	
	public Blacksmith(int tier, Store s){
		this.tier = tier;
		this.store = s;
		name = store.getName() +" " + extra.choose("smith","blacksmith","smithy","forge");
		tutorialText = "Blacksmith's add new items to stores, and can improve items.";
	}
	
	@Override
	public String getColor() {
		return extra.F_AUX_SERVICE;
	}
	
	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|blacksmith|Blacksmith|");
		extra.println("You have " + Player.bag.getGold() + " gold.");
		extra.println("1 forge item for store (" + tier*100 + " gold)");
		extra.println("2 improve item up to " + Item.getModiferNameColored(tier) +" quality");
		extra.println("3 exit");
		switch (extra.inInt(3)) {
		case 1: 
			if (Player.bag.getGold() >= tier*100) {
				Player.bag.addGold(-tier*100);
				store.addAnItem();
				extra.println("An item has been forged and sent to " + store.getName() + "!");
			}else {
				extra.println("You can't afford that!");
			}break;
		case 2:
			int in = askSlot();
			Item item;
			if (in <=5) {
				item = Player.bag.getArmorSlot(in-1);
			}else {
				item = Player.bag.getHand();
			}
			if (item.getLevel() >= tier) {
				extra.println("This item is too high in quality to improve here!");
				break;
			}
			int cost = (int) (Math.pow(tier-item.getLevel(),2)*item.getAetherValue()+(tier*100));//want to encourage gradual leveling rather than drastic jumps in power
			if (Player.bag.getGold() < cost) {
				extra.println("You can't afford this! (" + cost + " gold)");
				break;
			}
			extra.println("Improve your item to " + Item.getModiferNameColored(tier) + " quality for " +cost +" gold?");
			if (extra.yesNo()) {
				Player.bag.addGold(-cost);
				while (item.getLevel() < tier) {
					item.levelUp();
				}
			}
			;break;
		case 3: return;
		}
		go();
	}

	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		this.time += addtime;
		if (time > 12+(extra.getRand().nextInt(30))) {
			store.addAnItem();//TODO: should probably add in event?
			time = 0;
		}
		return null;
	}
	
	private static int askSlot() {
		extra.println("1 head");
		extra.println("2 arms");
		extra.println("3 chest");
		extra.println("4 legs");
		extra.println("5 feet");
		extra.println("6 weapon");
		return extra.inInt(6);
	}

}
