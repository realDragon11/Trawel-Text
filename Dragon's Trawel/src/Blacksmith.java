
public class Blacksmith extends Feature {
	
	private int tier;
	private double time = 0;
	private Store store;
	
	public Blacksmith(String name,int tier, Store s){
		this.name = name;
		this.tier = tier;
		this.store = s;
		tutorialText = "Blacksmith's add new items to stores.";
	}
	
	public Blacksmith(int tier, Store s){
		this.tier = tier;
		this.store = s;
		name = store.getName() +" " + extra.choose("smith","blacksmith","smithy","forge");
		tutorialText = "Blacksmith's add new items to stores.";
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|icon|Blacksmith|");
		extra.println("You have " + Player.bag.getGold() + " gold.");
		extra.println("1 forge item for store (" + tier*100 + " gold)");
		extra.println("2 exit");
		switch (extra.inInt(2)) {
		case 1: 
			if (Player.bag.getGold() >= tier*100) {
				Player.bag.addGold(-tier*100);
				store.addAnItem();
				extra.println("An item has been forged and sent to " + store.getName() + "!");
			}else {
				extra.println("You can't afford that!");
			}break;
		case 2: return;
		}
		go();
	}

	@Override
	public void passTime(double addtime) {
		this.time += addtime;
		if (time > 12+(Math.random()*30)) {
			store.addAnItem();
			time = 0;
		}

	}

}
