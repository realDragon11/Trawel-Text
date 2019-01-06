import java.awt.Color;
import java.util.ArrayList;

public class Store extends Feature implements java.io.Serializable{

	private int type;
	private ArrayList<Item> items;
	private int time;
	private int tier;
	private int buys;
	
	public static int INVENTORY_SIZE = 5;

	public Store(int tier) {
		items = new ArrayList<Item>();
		type = (int)(Math.random()*7);//6 = general
		time = 0;
		this.generate(tier, type);
		tutorialText = "This is a store. You can buy stuff here.";
		color = Color.BLUE;
	}
	public Store(int tier, int newType) {
		items = new ArrayList<Item>();
		type =  newType;
		time = 0;
		this.generate(tier, type);
		tutorialText = "This is a store. You can buy stuff here.";
		color = Color.BLUE;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void generate(int tier,int newType) {
		this.tier = tier;
		type = newType;
		switch (type) {
		case 0: name = extra.choose("hat","headwear","heads and hair");break;
		case 1: name = extra.choose("gloves","handwear","hand protection","mitten");break;
		case 2: name = extra.choose("chestpiece","bodywear","chest protector");break;
		case 3: name = extra.choose("pants","legwear","leg protector","trouser","pantaloon");break;
		case 4: name = extra.choose("boot","footwear","cobbler","feet protection");break;
		case 5: name = extra.choose("weapon","arms","armament","war");break;
		case 6: name = extra.choose("general","flea","convenience","trading","super");break;
		case 7: name = extra.choose("race","species");
		}
		name += " " + extra.choose("store","market","shop","post","boutique","emporium","outlet","center","mart","stand");
		if (type < 5) {
			for (int j = 0;j < 5;j++) {
				items.add(new Armor(Math.max(tier+(int)(Math.random()*5)-2,1),type));
			}
		}
		if (type == 5) {
			for (int j = 0;j < 5;j++) {
				items.add(new Weapon(Math.max(tier+(int)(Math.random()*5)-2,1)));
			}
		}
		if (type == 6) {
			for (int j = 0;j < 5;j++) {
				if (Math.random() > .5) {
				items.add(new Weapon(Math.max(tier+(int)(Math.random()*5)-2,1)));}else {
					items.add(new Armor(Math.max(tier+(int)(Math.random()*5)-2,1),(int)(Math.random()*5)));
				}
			}
		}
		if (type == 7) {
			for (int j = 0;j < 5;j++) {
				items.add(RaceFactory.randRace());
			}
		}
	}
	
	private void serviceItem(int index) {
		Item buyItem = items.get(index);
		String itemType = buyItem.getType();
		Inventory bag = Player.player.getPerson().getBag();
		Item sellItem = null;
		int slot = -1;
		if (itemType.contains("armor")) {
			slot = Integer.parseInt(itemType.replaceAll("armor",""));
			sellItem = bag.getArmorSlot(slot);
		}
		if (itemType.contains("weapon")) {
			sellItem = bag.getHand();
		}
		if (itemType.contains("race")) {
			sellItem = bag.getRace();
		}
		int sellGold = extra.zeroOut(sellItem.getCost());//the gold the item you are exchanging it for is worth
		
		if (buyItem.getCost() > (bag.getGold()+sellGold)) {
			extra.println("You can't afford this item!");
			return;
		}
		if (!AIClass.compareItem(sellItem,buyItem,-2,false)) {
			extra.println("You decide not to buy the item.");
			return;
		}
		this.addBuy();
		if (itemType.contains("armor")) {
			arraySwap(bag.swapArmorSlot((Armor)buyItem, slot),buyItem);
		}
		if (itemType.contains("weapon")) {
			arraySwap(bag.swapWeapon((Weapon)buyItem),buyItem);
		}
		if (itemType.contains("race")) {
			arraySwap(bag.swapRace((Race)buyItem),buyItem);
		}
		bag.setGold(bag.getGold()+sellGold-buyItem.getCost());
		extra.println("You complete the trade. "+ (sellGold-buyItem.getCost()) + " gold.");
	}
	
	private void arraySwap(Item i,Item i2) {
		items.remove(i2);
		items.add(i);
	}
	
	public void storeFront() {
		extra.println("You have " + Player.bag.getGold() + " gold.");
		int j = 1;
		extra.println(j + " examine all");j++;
		for (Item i: items) {
			extra.print(j + " ");
			i.display(1);
			j++;
		}
		if (Player.hasSkill(Skill.RESTOCK)) {
		extra.println(j + " restock (" + tier*100 +" gold)" );
		j++;
		}
		extra.println(j + " Exit");
		int i = extra.inInt(j);
		j = 1;
		if (i == j) {//examine all
			for (int k = 0;k < items.size();k++) {
				serviceItem(k);
			}
			storeFront();
			return;
		}j++;
		for (Item it: items) {
			if (i == j) {
				serviceItem(i-2);
				storeFront();//bad way of staying in it, but easy to code
				return;
			}
			
			j++;
		}
		if (Player.hasSkill(Skill.RESTOCK)) {
		if (i ==j) {
			this.restock();
		}j++;}
		if (i ==j) {
			return;
		}j++;
		storeFront();
	}
	

	private void restock() {
		for (int i = items.size()-1;i >= 0;i--) {
			items.remove(i);
		}
		for (int i = INVENTORY_SIZE;i > 0;i-- ) {
			addAnItem();
		}
		
	}
	
	public void go() {
		Networking.sendStrong("Discord|imagesmall|icon|Store|");
		this.storeFront();
	}
	
	@Override
	public void passTime(double addtime) {
		this.time += addtime;
		if (time > 12+(Math.random()*30)) {
			if (type != 7) {
			extra.disablePrintSubtle();
			goShopping();
			extra.enablePrintSubtle();}
			addAnItem();
			time = 0;
		}
		
		
	}
	
	@Override
	public String getName() {
		return name;
	}
	public int getBuys() {
		return buys;
	}
	
	private void addBuy() {
		this.buys +=1;
		if (buys == 3) {
			Player.player.addTitle(this.getName() + " shopper");
		}
		if (buys == 10) {
			Player.player.addTitle(this.getName() + " frequent shopper");
		}
	}
	
	public void addAnItem() {
		if (items.size() >= INVENTORY_SIZE) {
			items.remove((int)(Math.random()*items.size()));}
			if (type < 5) {
					items.add(new Armor(Math.max(tier+(int)(Math.random()*5)-2,1),type));
			}
			if (type == 5) {
					items.add(new Weapon(Math.max(tier+(int)(Math.random()*5)-2,1)));
			}
			if (type == 6) {
					if (Math.random() > .5) {
					items.add(new Weapon(Math.max(tier+(int)(Math.random()*5)-2,1)));}else {
						items.add(new Armor(Math.max(tier+(int)(Math.random()*5)-2,1),(int)(Math.random()*5)));
					}
				}
	}
	private void goShopping() {
		
		for (SuperPerson peep: town.getOccupants()) {
			Agent a = (Agent)peep;
			Inventory bag = a.getPerson().getBag();
			ArrayList<Item> add = new ArrayList<Item>();
			ArrayList<Item> remove = new ArrayList<Item>();
			for (Item i: items) {
				if (AIClass.compareItem(bag,i,a.getPerson().getIntellect(),false)) {
					int goldDiff = i.getCost()-bag.itemCounterpart(i).getCost();
					if (goldDiff <= bag.getGold()){
					bag.addGold(-goldDiff);
					remove.add(i);
					add.add(bag.swapItem(i));
					}
				}
			}
			for (Item i: add) {
				items.add(i);
			}
			for (Item i: remove) {
				items.remove(i);
			}
		}
	}
}
