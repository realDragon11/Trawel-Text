package trawel.towns.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.AIClass;
import trawel.Networking;
import trawel.Services;
import trawel.extra;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.Item.ItemType;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.Skill;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class Store extends Feature{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int type;
	private List<Item> items;
	private List<DrawBane> dbs;
	private double time;
	private int tier;
	private int buys;
	private float markup;
	private float aetherRate;
	
	public static int INVENTORY_SIZE = 5;

	private Store() {
		time = 0;
		tutorialText = "This is a store. You can buy stuff here.";
		markup = 1.5f;
		aetherRate = Player.NORMAL_AETHER_RATE;
		if (extra.chanceIn(3,4)) {
			//3 out of 4 chance to move at least somewhat towards a % deviated rate
			aetherRate= extra.lerp(aetherRate,extra.choose(.8f,.7f,.4f,1.1f,1.2f),extra.randFloat());
		}
	}
	
	public Store(Town t, int tier, int type) {
		this();
		town = t;
		this.generate(tier, type);
	}
	
	public Store(int tier) {
		this();
		type = extra.getRand().nextInt(7);//6 = general
		this.generate(tier, type);
	}
	public Store(int tier, int type) {
		this();
		this.generate(tier, type);
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
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
		case 7: name = extra.choose("race","species");break;
		case 8: name = extra.choose("drawbane","lure");break;
		case 9: name = extra.choose("witch","potion");break;
		}
		name += " " + extra.choose("store","market","shop","post","boutique","emporium","outlet","center","mart","stand");
		if (type < 8) {
			items = new ArrayList<Item>();
			if (type < 5) {
				for (int j = 0;j < 5;j++) {
					items.add(new Armor(Math.max(tier+extra.getRand().nextInt(6)-2,1),type));
				}
			}
			if (type == 5) {
				for (int j = 0;j < 5;j++) {
					items.add(Weapon.genMidWeapon(Math.max(tier+extra.getRand().nextInt(6)-2,1)));
				}
			}
			if (type == 6) {
				for (int j = 0;j < 5;j++) {
					Random rand = extra.getRand();
					if (rand.nextFloat() > .5f) {
						items.add(Weapon.genMidWeapon(Math.max(tier+rand.nextInt(6)-2,1)));
					}else {
						items.add(new Armor(Math.max(tier+rand.nextInt(6)-2,1),rand.nextInt(5)));
					}
				}
			}
			if (type == 7) {
				for (int j = 0;j < 5;j++) {
					items.add(RaceFactory.randRace(Race.RaceType.HUMANOID));
				}
			}
		}
		
		if (type == 8) {
			dbs = new ArrayList<DrawBane>();
			for (int j = 0;j < 5;j++) {
				dbs.add(randomDB());
			}
		}
		if (type == 9) {
			dbs = new ArrayList<DrawBane>();
			for (int j = 0;j < 5;j++) {
				dbs.add(randomPI());
			}
		}
	}
	
	//TODO put these in tables elsewhere TODO
	
	public static DrawBane randomDB() {
		return extra.choose(DrawBane.MEAT,DrawBane.GARLIC,DrawBane.BLOOD,DrawBane.REPEL,DrawBane.CLEANER,extra.choose(DrawBane.PROTECTIVE_WARD,DrawBane.SILVER,extra.choose(DrawBane.SILVER,DrawBane.GOLD,DrawBane.VIRGIN)));
	}
	
	public static DrawBane randomPI() {
		return extra.choose(DrawBane.MEAT,DrawBane.BAT_WING,DrawBane.APPLE,DrawBane.CEON_STONE,DrawBane.MIMIC_GUTS,DrawBane.BLOOD);
	}
	
	private void serviceItem(Item item) {
		serviceItem(items.indexOf(item));
	}
	
	private void serviceItem(int index) {
		Person p = Player.player.getPerson();
		Inventory bag = p.getBag();
		if (type == 8 || type == 9) {
			if (index == -1) {
				DrawBane sellItem = bag.discardDrawBanes(true);
				if (sellItem != null) {
					Services.sellItem(sellItem, bag);
				}
				
				return;
			}
			DrawBane db = dbs.get(index);
			int buyGold = (int) Math.ceil(db.getValue() * markup);
			if (Player.getTotalBuyPower() >= buyGold) {
				extra.println("Buy the "+ db.getName() + "? (" + buyGold + " "+World.currentMoneyString()+")");//TODO: explain aether conversion
				if (extra.yesNo()) {
					DrawBane sellItem = bag.addNewDrawBane(db);
					if (sellItem != null) {
						Player.buyMoneyAmount(sellItem.getValue()-buyGold);
					}
					dbs.remove(index);
				}
			}else {
				extra.println("You cannot afford this item.");
			}
			return;
		}
		Item buyItem = items.get(index);
		if (!canSee(buyItem)) {
			return;
		}
		ItemType itemType = buyItem.getType();
		Item sellItem = null;
		int slot = -1;
		switch (itemType) {
		case ARMOR:
			sellItem = bag.getArmorSlot(((Armor)buyItem).getSlot());
			break;
		case RACE:
			sellItem = bag.getRace();
			break;
		case WEAPON:
			sellItem = bag.getHand();
			break;		
		}
		//the gold the item you are exchanging it for is worth
		int sellGold = extra.zeroOut(sellItem.getMoneyValue());
		int buyGold = (int)Math.ceil(buyItem.getMoneyValue()*markup);
		int delta = buyGold-sellGold;
		if (Player.getTotalBuyPower() < delta) {
			extra.println("You can't afford this item!");
			return;
		}
		if (!AIClass.compareItem(sellItem,buyItem,-2,false,p)) {
			extra.println("You decide not to buy the item.");
			return;
		}
		this.addBuy();
		switch (itemType) {
		case ARMOR:
			arraySwap(bag.swapArmorSlot((Armor)buyItem, slot),buyItem);
			break;
		case RACE:
			arraySwap(bag.swapRace((Race)buyItem),buyItem);
			break;
		case WEAPON:
			arraySwap(bag.swapWeapon((Weapon)buyItem),buyItem);
			break;	
		}
		Player.buyMoneyAmount(-delta);
		extra.println("You complete the trade. "+ (delta) + " value.");
	}
	
	private void arraySwap(Item i,Item i2) {
		items.remove(i2);
		items.add(i);
	}
	
	public static boolean canSee(Item i) {
		if (Player.player.merchantLevel >= i.getLevel() || Player.player.getPerson().getLevel() > i.getLevel()) {
			return true;}else {
				return false;
			}
	}
	
	private int aetherPerMoney() {
		return ((int)(1/aetherRate));
	}
	
	private int aetherRateGuess() {
		if (aetherRate == Player.NORMAL_AETHER_RATE) {
			return 0;
		}
		float delta = Math.abs(aetherRate-Player.NORMAL_AETHER_RATE);
		float perOfLarger = delta/Math.max(aetherRate,Player.NORMAL_AETHER_RATE);
		if (aetherRate < Player.NORMAL_AETHER_RATE) {
			if (perOfLarger < .5) {//less than half rate
				return -3;
			}else {
				if (perOfLarger < .8) {
					return -2;
				}
				return -1;
			}
		}//better rate than normal
		if (perOfLarger < .5) {//double rate or more
			return 3;
		}else {
			if (perOfLarger < .8) {
				return 2;
			}
			return 1;
		}
	}
	
	private String aetherRateString() {
		switch (aetherRateGuess()) {
		case 0:
			return "normal";
		case -3:
			return "shockingly bad";
		case -2:
			return "bad";
		case -1:
			return "poor";
		case 1:
			return "good";
		case 2:
			return "great";
		case 3:
			return "amazing";
		}
		throw new RuntimeException("bad aether rate guess");
	}
	
	public MenuGenerator modernStoreFront() {
		return new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "You have " + World.currentMoneyDisplay(Player.getGold()) + " and "+Player.bag.getAether() +" aether.";
					}});
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "They will exchange "+aetherPerMoney() + " aether for " + World.currentMoneyDisplay(1) + " a " +aetherRateString() + " rate.";
					}});
				
				if (type != 8 && type != 9) {//normal items
					for (Item i: items) {
						list.add(new StoreMenuItem(i));
					}
				}else {
					for (DrawBane db: dbs) {
						list.add(new StoreMenuDraw(db));
					}
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "sell drawbane";
						}

						@Override
						public boolean go() {
							serviceItem(-1);
							return false;
						}});
				}
				list.add(new MenuBack());
				return list;
			}
			
		};
	}
	
	private class StoreMenuItem implements MenuItem{
		private Item item;
		
		private StoreMenuItem(Item it) {
			item = it;
		}

		@Override
		public String title() {
			return item.storeString(markup,canSee(item));
		}

		@Override
		public boolean go() {
			serviceItem(item);
			return false;
		}

		@Override
		public boolean canClick() {
			return canSee(item);
		}

		@Override
		public boolean forceLast() {
			return false;
		}
	}
	
	private class StoreMenuDraw extends MenuSelect{
		
		private DrawBane item;
		
		private StoreMenuDraw(DrawBane it) {
			item = it;
		}

		@Override
		public String title() {
			return item.getName() + " cost: " + extra.F_WHOLE.format(Math.ceil(item.getValue()*markup));
		}

		@Override
		public boolean go() {
			serviceItem(dbs.indexOf(item));
			return false;
		}
		
	}
	
	@Deprecated
	public void storeFront() {//FIXME: make modern menu
		Networking.charUpdate();
		extra.println("You have " + World.currentMoneyDisplay(Player.getGold()) + " and "+Player.bag.getAether() +" aether. Current aether rate is " + extra.F_TWO_TRAILING.format(Player.NORMAL_AETHER_RATE));
		int j = 1;
		extra.println(j + " examine all");j++;
		if (type == 8 || type == 9) {
			for (DrawBane i: dbs) {
				extra.println(j + " " + i.getName() + " - " + i.getFlavor() + " cost: " + (i.getValue()*tier));
				j++;
			}
		}else {
		for (Item i: items) {
			extra.print(j + " ");
			if (canSee(i)) {
				i.display(1,markup);
			}else {
				extra.println("They refuse to show you this item.");//DOLATER: have a hint of what they're not showing you with modern menu type
			}
			j++;
		}}
		if (Player.hasSkill(Skill.RESTOCK)) {
		extra.println(j + " restock (" + tier*100 +" gold)" );//DOLATER
		j++;
		}
		if (type == 8) {
			extra.println(j+ " sell drawbane");
			j++;
		}
		extra.println("9 Exit");
		int i = extra.inInt(j,true);
		j = 1;
		if (i == j) {//examine all
			if (type == 8 || type == 9) {
				for (int k = 0;k < dbs.size();k++) {
					serviceItem(k);
				}
			}else {
			for (int k = 0;k < items.size();k++) {
				serviceItem(k);
			}}
			storeFront();
			return;
		}
		
		j++;
		if (type == 8 || type == 9) {
			for (DrawBane it: dbs) {
				if (i == j) {
					serviceItem(i-2);
					storeFront();//bad way of staying in it, but easy to code
					return;
				}
				j++;
			}
				
					
		}else {
		for (Item it: items) {
			if (i == j) {
				serviceItem(i-2);
				storeFront();//bad way of staying in it, but easy to code
				return;
			}
			
			j++;
		}}
		if (Player.hasSkill(Skill.RESTOCK)) {
		if (i ==j) {
			this.restock();
		}j++;}
		if (type == 8) {
			if (i ==j) {
				serviceItem(-1);
			}j++;}
		if (i ==j) {
			return;
		}j++;
		storeFront();
	}
	

	private void restock() {
		if (this.type == 8 || type == 9) {
			for (int i = items.size()-1;i >= 0;i--) {
				dbs.remove(i);
			}
			for (int i = INVENTORY_SIZE;i > 0;i-- ) {
				addAnItem();
			}
			return;
		}
		for (int i = items.size()-1;i >= 0;i--) {
			items.remove(i);
		}
		for (int i = INVENTORY_SIZE;i > 0;i-- ) {
			addAnItem();
		}
		
	}
	
	@Override
	public void go() {
		Networking.setArea("shop");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|store|Store|");
		this.storeFront();
	}
	
	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		time += addtime;
		if (time > 12+(extra.getRand().nextInt(30))) {
			if (type != 7) {
			extra.offPrintStack();
			goShopping();
			extra.popPrintStack();}
			addAnItem();
			time = 0;
		}
		return null;//TODO: should probably make these events or something instead? idk, shouldn't have much issues
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
		if (type == 8) {
			if (dbs.size() >= INVENTORY_SIZE) {
				dbs.remove(extra.randList(dbs));}
			dbs.add(randomDB());
			return;
		}
		if (type == 9) {
			if (dbs.size() >= INVENTORY_SIZE) {
				dbs.remove(extra.randList(dbs));}
			dbs.add(randomPI());
			return;
		}
		if (items.size() >= INVENTORY_SIZE) {
			items.remove(extra.randList(items));}
			if (type < 5) {
					items.add(new Armor(Math.max(tier+extra.getRand().nextInt(6)-2,1),type));
			}
			if (type == 5) {
					items.add(Weapon.genMidWeapon(Math.max(tier+extra.getRand().nextInt(6)-2,1)));
			}
			if (type == 6) {
				Random rand = extra.getRand();
				if (rand.nextFloat() > .5f) {
					items.add(Weapon.genMidWeapon(Math.max(tier+rand.nextInt(6)-2,1)));
				}else {
					items.add(new Armor(Math.max(tier+rand.nextInt(6)-2,1),rand.nextInt(5)));
				}
			}
	}
	private void goShopping() {
		if (type == 8 || type == 9) {
			return;
		}
		for (SuperPerson peep: town.getOccupants()) {
			Agent a = (Agent)peep;
			Inventory bag = a.getPerson().getBag();
			ArrayList<Item> add = new ArrayList<Item>();
			ArrayList<Item> remove = new ArrayList<Item>();
			for (Item i: items) {
				if (AIClass.compareItem(bag,i,a.getPerson().getIntellect(),false,a.getPerson())) {
					int goldDiff = i.getMoneyValue()-bag.itemCounterpart(i).getMoneyValue();
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
