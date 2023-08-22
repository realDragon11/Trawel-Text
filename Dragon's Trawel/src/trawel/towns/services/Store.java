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
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.Item.ItemType;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.DrawBane.DrawList;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class Store extends Feature{

	private static final long serialVersionUID = 1L;
	protected int type;
	protected List<Item> items;
	protected List<DrawBane> dbs;
	protected double time;
	private int buys;
	protected float markup;
	protected float aetherRate;
	protected int invSize;
	
	public static final int INVENTORY_SIZE = 5;
	
	public static final float BASE_MARKUP = 1.5f;

	private Store() {
		time = 0;
		tutorialText = "Store.";
		markup= extra.lerp(BASE_MARKUP,BASE_MARKUP*extra.choose(.9f,.95f,1.2f,1.3f,1.5f),extra.randFloat());
		aetherRate = Player.NORMAL_AETHER_RATE;
		if (extra.chanceIn(3,4)) {
			//3 out of 4 chance to move at least somewhat towards a % deviated rate
			aetherRate= extra.lerp(aetherRate,aetherRate*extra.choose(.8f,.7f,.4f,1.1f,1.2f),extra.randFloat());
		}
		invSize = extra.randRange(4,6);
		if (invSize < 5) {
			markup *= .95;
		}
		markup = Math.max(1.1f,markup);
	}
	
	public Store(Town t, int tier, int type) {
		this();
		town = t;
		this.generate(tier, type);
	}
	
	public Store(Town t, String appendname) {
		this(t.getTier());
		name +=appendname;
	}
	
	public Store(int tier) {
		this();
		//not set stores are now always general stores
		type = 6;//extra.getRand().nextInt(7);//6 = general
		this.generate(tier, type);
	}
	public Store(int tier, int type) {
		this();
		this.generate(tier, type);
	}
	
	public Store(String name, int tier, int type) {
		this();
		this.generate(tier, type);
		this.name = name;
	}
	
	protected Store(Class<? extends Store> subtype) {//for witch hut
		this();
		if (subtype == WitchHut.class) {
			type = 9;
			this.generate(tier, type);
		}
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
		case 0: name = extra.choose("Hat","Headwear","Heads and Hair");break;
		case 1: name = extra.choose("Gloves","Handwear","Hand protection","Mitten");break;
		case 2: name = extra.choose("Chestpiece","Bodywear","Chest Protector");break;
		case 3: name = extra.choose("Pants","Legwear","Leg Protector","Trouser","Pantaloon");break;
		case 4: name = extra.choose("Boot","Footwear","Cobbler","Feet Protection");break;
		case 5: name = extra.choose("Weapon","Arms","Armament","War");break;
		case 6: name = extra.choose("General","Flea","Convenience","Trading","Super");break;
		case 7: name = extra.choose("Race","Species");break;
		case 8: name = extra.choose("Drawbane");break;//misc
		case 9: name = extra.choose("Witch","Potion Material");break;
		case 10: name = extra.choose("Food");break;
		case 11: name = extra.choose("Oddity");break;//collector
		}
		name += " " + extra.choose("Store","Market","Shop","Post","Boutique","Emporium","Outlet","Center","Mart","Stand");
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
					items.add(RaceFactory.randRace(Race.RaceType.PERSONABLE));
				}
			}
		}
		
		if (type >=8 ) {
			dbs = new ArrayList<DrawBane>();
			for (int j = 0;j < 5;j++) {
				addAnItem();
			}
			
		}
	}
	
	//TODO put these in tables elsewhere TODO
	
	
	public static DrawBane randomPI() {
		return extra.choose(DrawBane.MEAT,DrawBane.BAT_WING,DrawBane.APPLE,DrawBane.CEON_STONE,DrawBane.MIMIC_GUTS,DrawBane.BLOOD);
	}
	
	private void serviceItem(Item item) {
		serviceItem(items.indexOf(item));
	}
	/**
	 * > 0 = gaining money from the trade (person selling something worth more)
	 * <br>
	 * < 0 = losing money from the trade (person selling something worth less)
	 * <br>
	 * @param selling
	 * @param buying
	 * @return
	 */
	public int getDelta(Item selling, Item buying, SuperPerson p) {
		//the gold the item you are exchanging it for is worth
		int sellGold = selling != null ? extra.zeroOut(selling.getMoneyValue()) : 0;
		double buyGold = Math.ceil(buying.getMoneyValue()*getScaledMarkup(p,buying));
		double raw_delta = sellGold-buyGold;// > 0 = earning money, < 0 = spending money
		return (int) (raw_delta > 0 ? Math.floor(raw_delta) : Math.ceil(raw_delta));
	}
	
	private void serviceItem(int index) {
		Person p = Player.player.getPerson();
		Inventory bag = p.getBag();
		if (type == 8 || type == 9) {
			if (index == -1) {
				DrawBane sellItem = bag.playerOfferDrawBane("sell");
				if (sellItem != null) {
					Services.sellItem(sellItem, bag);
				}
				
				return;
			}
			DrawBane db = dbs.get(index);
			int buyGold = (int) Math.ceil(db.getValue() * markup);
			if (Player.player.getTotalBuyPower(aetherPerMoney(Player.player.getPerson())) >= buyGold) {
				extra.println("Buy the "+ db.getName() + "? (" + buyGold + " "+World.currentMoneyString()+")");//TODO: explain aether conversion
				if (extra.yesNo()) {
					DrawBane sellItem = bag.addNewDrawBanePlayer(db);
					if (sellItem != null) {
						Player.player.buyMoneyAmountRateInt(sellItem.getValue()-buyGold,aetherPerMoney(Player.player.getPerson()));
					}
					dbs.remove(index);
				}
			}else {
				extra.println("You cannot afford this item.");
			}
			return;
		}
		Item buyItem = items.get(index);
		if (canSee(buyItem) <= 0) {
			return;
		}
		ItemType itemType = buyItem.getType();
		Item sellItem = null;
		int slot = -1;
		switch (itemType) {
		case ARMOR:
			slot = ((Armor)buyItem).getSlot();
			sellItem = bag.getArmorSlot(slot);
			break;
		case RACE:
			sellItem = bag.getRace();
			break;
		case WEAPON:
			sellItem = bag.getHand();
			break;
		default:
			throw new RuntimeException("invalid store item type");
		}
		int delta = getDelta(sellItem,buyItem,Player.player);
		if (Player.player.getTotalBuyPower()+delta < 0) {
			extra.println("You can't afford this item!");
			return;
		}
		if (!AIClass.compareItem(sellItem,buyItem,p,this)) {
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
		if (delta < 0) {
			int beforeMoney = Player.player.getGold();
			int beforeAether = Player.bag.getAether();
			Player.player.buyMoneyAmountRateInt(-delta,aetherPerMoney(Player.player.getPerson()));
			int moneyDelta = beforeMoney-Player.player.getGold();
			int aetherDelta = beforeAether-Player.bag.getAether();
			extra.println("You complete the trade."
			+ (moneyDelta > 0 ? " Spent " +World.currentMoneyDisplay(moneyDelta) : "")
			+ (moneyDelta > 0 && aetherDelta > 0 ? " and" : (aetherDelta > 0 ? " Spent" : ""))
			+ (aetherDelta > 0 ? " " +aetherDelta +" aether" : "")
			+ "."
					);
		}else {
			if (delta > 0) {//we sold something more expensive
				Player.player.addGold(delta);
				extra.println("You complete the trade, gaining " + World.currentMoneyDisplay(delta) +".");
			}else {//equal value
				extra.println("You complete the trade.");
			}
		}
		
		
	}
	
	private void arraySwap(Item i,Item i2) {
		items.remove(i2);
		items.add(i);
	}
	
	/**
	 * 2 = can see, normal markup
	 * <br>
	 * 1 = can see but is marked up more than normal
	 * <br>
	 * 0 = cannot see
	 */
	public int canSee(Item i) {
		double scaled = getScaledMarkup(Player.player,i);
		if (scaled  < 4d) {
			if (scaled == markup) {
				return 2;
			}
			return 1;
		}else {
			return 0;
		}
	}
	
	public int aetherPerMoney(Person shopper) {
		Float effectiveRate = aetherRate;
		if (shopper.hasSkill(Skill.MAGE_FRUGAL)) {
			effectiveRate *= 1.1f+ Math.max(1,shopper.getClarity()/100f);
		}
		return ((int)(1/effectiveRate));
	}
	
	public static int rateGuess(float base, float actual, boolean higherBetter) {
		if (actual == base) {
			return 0;
		}
		//float delta = Math.abs(actual-base);
		float perOfLarger = Math.min(actual,base)/Math.max(actual,base);
		boolean worse = higherBetter ? actual < base : actual > base;
		if (worse) {
			if (perOfLarger < .75) {
				return -3;
			}else {
				if (perOfLarger < .9) {
					return -2;
				}
				return -1;
			}
		}//better rate than normal
		if (perOfLarger < .75) {
			return 3;
		}else {
			if (perOfLarger < .9) {
				return 2;
			}
			return 1;
		}
	}
	
	private String rateString(int i) {
		switch (i) {
		case 0:
			return "a normal";
		case -3:
			return "a shockingly bad";
		case -2:
			return "a bad";
		case -1:
			return "a poor";
		case 1:
			return "a good";
		case 2:
			return "a great";
		case 3:
			return "an amazing";
		}
		throw new RuntimeException("bad rate guess");
	}
	
	private String priceRateString(int i) {
		switch (i) {
		case 0:
			return "normal";
		case -3:
			return "extremely high";
		case -2:
			return "high";
		case -1:
			return "above average";
		case 1:
			return "below average";
		case 2:
			return "cheap";
		case 3:
			return "like they are basically charity";
		}
		throw new RuntimeException("bad rate guess");
	}
	
	public MenuGenerator modernStoreFront() {
		return new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "You have " + World.currentMoneyDisplay(Player.player.getGold()) + " and "+Player.bag.getAether() +" aether.";
					}});
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "They will exchange "+aetherPerMoney(Player.player.getPerson()) + " aether for "
					+ World.currentMoneyDisplay(1)
					+" which is " +rateString(rateGuess(Player.NORMAL_AETHER_RATE,aetherRate,true)) + " rate."
					+" Their prices seem " + priceRateString(rateGuess(BASE_MARKUP,markup,false)) +".";
					}});
				
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "You have a total of "+(Player.player.getGold() + (Player.bag.getAether()/aetherPerMoney(Player.player.getPerson()))) +" buying power.";
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
			return item.storeString(getScaledMarkup(Player.player,item),canSee(item));
		}

		@Override
		public boolean go() {
			serviceItem(item);
			return false;
		}

		@Override
		public boolean canClick() {
			return canSee(item) > 0;
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
		extra.menuGo(modernStoreFront());
	}
	
	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		time += addtime;
		if (time > 12+(extra.getRand().nextInt(30))) {
			extra.offPrintStack();
			goShopping();
			extra.popPrintStack();
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
			Player.player.addTitle(town.getName() + " "+ getStoreName() + " shopper");
		}
		if (buys == 10) {
			Player.player.addTitle(town.getName() + " "+getStoreName() + " frequent shopper");
		}
	}
	
	/**
	 * substores override this
	 */
	protected String getStoreName() {
		return name;
	}
	
	public void addAnItem() {
		if (type >= 8) {
			if (dbs.size() >= invSize) {
				dbs.remove(extra.randRange(0,dbs.size()-1));
			}
			switch (type) {
			case 8:
				dbs.add(DrawBane.draw(DrawList.GENERIC_STORE));
				return;
			case 9:
				dbs.add(DrawBane.draw(DrawList.WITCH_STORE));
				return;
			case 10:
				dbs.add(DrawBane.draw(DrawList.FOOD));
				return;
			case 11:
				dbs.add(DrawBane.draw(DrawList.COLLECTOR));
				return;
			}
			
			return;
		}
		if (items.size() >= invSize) {
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
		if (type == 9) {//potion shops apply some, and also refills
			WitchHut.randomRefillsAtTown(town,tier);
			return;
		}
		if (type >= 7) {//7 is speices store, maybe someday
			return;
		}
		//needs to be in order to avoid changing the item list multiple times at once
		town.getPersonableOccupants().sequential().forEach(a-> doShop(a));
	}
	
	private void doShop(Agent a) {
		Person p = a.getPerson();
		Inventory bag = p.getBag();
		
		assert a.isPersonable();
		
		for (int j = items.size()-1;j>=0;j--) {
			Item i = items.get(j);
			if (i.getLevel() > p.getLevel()) {
				continue;//ai hard capped to not be able to buy better items than its level
			}
			Item counter = bag.itemCounterpart(i);
			//for some reason counter can be null, which shouldn't be allowed under current circumstances
			//however future behavior will be fine with it, so allowing that now
			int delta = getDelta(counter,i,a);
			if (a.getTotalBuyPower()+delta < 0 && AIClass.compareItem(bag,i,false,p)) {
				a.buyMoneyAmountRateInt(-delta,aetherPerMoney(p));
				items.remove(i);
				items.add(bag.swapItem(i));
			}
		}
	}

	public float getMarkup() {
		return markup;
	}
	
	public double getScaledMarkup(SuperPerson buyer, Item i) {
		int itemLevel = i.getLevel();
		int ePLevel = buyer.getPerson().getLevel();
		if (buyer.getPerson().isPlayer()) {
			ePLevel = Math.max(Player.player.merchantLevel, ePLevel);
		}
		if (ePLevel >= itemLevel) {
			return markup;
		}
		assert markup > 1f;//must be a real markup
		return Math.pow(markup,itemLevel-ePLevel);//every overleveled adds the markup again
	}
}
