package trawel.towns.features.services;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.core.Input;
import trawel.core.Networking.Area;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.Services;
import trawel.helper.methods.extra;
import trawel.personal.AIClass;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.Item.ItemType;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.DrawBane.DrawList;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.FeatureData;
import trawel.towns.features.Feature;
import trawel.towns.features.multi.Slum;

public class Store extends Feature{
	
	static {
		FeatureData.registerFeature(Store.class,new FeatureData() {

			@Override
			public void tutorial() {
				Print.println(fancyNamePlural()+" sell various items. Equipment must be bought with "+TrawelChar.DISP_AETHER+". Other items require World Currency. "+fancyNamePlural()+" will hide their better wares to those who haven't proven themselves- either in battle or as a Merchant Guild partner.");
			}

			@Override
			public String name() {
				return "Store";
			}

			@Override
			public String color() {
				return TrawelColor.F_SERVICE;
			}

			@Override
			public FeatureTutorialCategory category() {
				return FeatureTutorialCategory.ADVANCED_SERVICES;
			}

			@Override
			public int priority() {
				return 1;
			}});
	}

	private static final long serialVersionUID = 1L;
	protected int type;
	protected List<Item> items;
	protected List<RaceID> races;
	protected List<DrawBane> dbs;
	protected double time;
	private int buys;
	protected float markup;
	protected float aetherRate;
	protected int invSize;
	protected int itemMinLevel = -1, itemMaxLevel = -1;
	
	public static final int INVENTORY_SIZE = 5;
	
	public static final float BASE_MARKUP = 2.5f;

	private Store() {
		time = 0;
		markup= extra.lerp(BASE_MARKUP,BASE_MARKUP*Rand.choose(.9f,.95f,1.2f,1.3f,1.5f),Rand.randFloat());
		aetherRate = Player.NORMAL_AETHER_RATE;
		/*if (extra.chanceIn(3,4)) {
			//3 out of 4 chance to move at least somewhat towards a % deviated rate
			aetherRate = extra.lerp(aetherRate,aetherRate*extra.choose(.8f,.7f,.4f,1.1f,1.2f),extra.randFloat());
			//can't be better than pure rate
			aetherRate = Math.min(aetherRate,Player.PURE_AETHER_RATE);
		}*/
		invSize = Rand.randRange(4,6);
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
	
	public Store(Town t, String _name, int tier, int type,int _invSize) {
		this();
		town = t;
		_invSize = invSize;
		generate(tier, type);
		name = _name;
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
	
	/**
	 * used by witch hut and slums
	 * @param subtype
	 */
	protected Store(int tier,Class<? extends Store> subtype) {
		this();
		if (subtype == WitchHut.class) {
			type = 9;
			this.generate(tier, type);
		}
		if (subtype == Slum.class) {
			itemMinLevel = Math.max(1,tier-6);
			itemMaxLevel = tier;
			type = 6;
			//lower size
			invSize = Rand.randRange(2,4);
			//move towards a bad aether rate
			//to simulate lack of ability to deal with aether
			float max_pen = Player.NORMAL_AETHER_RATE*.3f;
			if (aetherRate > max_pen) {
				aetherRate = extra.lerp(aetherRate,max_pen,.4f);
			}
			generate(tier, type);
		}
		if (subtype == trawel.towns.features.misc.TravelingFeature.class) {
			type = 6;
			this.generate(tier, type);
		}
	}
	
	@Override
	public String nameOfType() {
		switch (getType()) {
			default:
				return "Store";
			case 0:
				return "Helmet Store";
			case 1:
				return "Armbands Store";
			case 2:
				return "Chestpiece Store";
			case 3:
				return "Pants Store";
			case 4:
				return "Boots Store";
			case 5:
				return "Weapon Store";
			case 6:
				return "Equipment Store";
			case 7:
				return "Species Store";
			case 8:
				return "Drawbane Store";
			case 9:
				return "Reagent Store";
			case 10:
				return "Food Store";
			case 11:
				return "Oddity Store";
		}
	}
	
	@Override
	public Area getArea() {
		return Area.SHOP;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public void generate(int tier,int newType) {
		if (this.tier <= 0) {
			this.tier = tier;//might be overwritten later in case of subclasses
		}
		if (itemMinLevel <= 0) {
			itemMinLevel = Math.max(1,tier-2);
		}
		if (itemMaxLevel <= 0) {
			itemMaxLevel = tier+2;
		}
		assert itemMinLevel <= itemMaxLevel;
		type = newType;
		switch (type) {
		case 0: name = Rand.choose("Hat","Headwear","Heads and Hair");
		break;
		case 1: name = Rand.choose("Gloves","Handwear","Hand Protection","Mitten");
		break;
		case 2: name = Rand.choose("Chestpiece","Bodywear","Chest Protector");
		break;
		case 3: name = Rand.choose("Pants","Legwear","Leg Protector","Trouser","Pantaloon");
		break;
		case 4: name = Rand.choose("Boot","Footwear","Cobbler","Feet Protection");
		break;
		case 5: name = Rand.choose("Weapon","Arms","Armament","War");
		break;
		case 6: name = Rand.choose("General","Flea","Convenience","Trading","Super","Tag","Jumble","Clearance","Retail");
		break;
		case 7: name = Rand.choose("Race","Species","Body","New You");
		break;
		case 8: name = Rand.choose("Drawbane");
		break;//misc
		case 9: name = Rand.choose("Witchery","Potion Material","Reagent","Catalyst","Reactant","Ingredient");
		break;
		case 10: name = Rand.choose("Food","Provision","Comestible","Edible","Commissariat");
		break;
		case 11: name = Rand.choose("Oddity","Bizarre","Peculiar","Curiosity","Souvenir","Trinket","Trophy");
		break;//collector
		}
		name += " " + Rand.choose("Store","Market","Shop","Post","Boutique","Emporium","Outlet","Center","Mart","Stand","Sale","Fair","Bazaar","Stall","Booth");

		if (type >=8) {
			dbs = new ArrayList<DrawBane>();
		}else {
			if (type == 7) {
				races = new ArrayList<RaceID>();
			}else {
				items = new ArrayList<Item>();
			}
		}
		for (int j = invSize-1;j >=0;j--) {
			addAnItem();
		}
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
		int sellGold = selling != null ? extra.zeroOut(selling.getAetherValue()) : 0;
		double buyGold = Math.ceil(buying.getAetherValue()*getScaledMarkup(p,buying));
		double raw_delta = (sellGold-buyGold);// > 0 = earning money, < 0 = spending money
		return (int) (raw_delta > 0 ? Math.floor(raw_delta) : Math.ceil(raw_delta));
	}
	
	private void serviceItem(int index) {
		Person p = Player.player.getPerson();
		Inventory bag = p.getBag();
		if (type >= 8) {
			if (index == -1) {
				DrawBane sellItem = bag.playerOfferDrawBane("sell");
				if (sellItem != null) {
					Services.sellItem(sellItem, bag);
				}
				return;
			}
			DrawBane db = dbs.get(index);
			int buyGold = (int) Math.ceil(db.getValue() * markup);
			if (Player.bag.getGold() >= buyGold) {
				Print.println("Buy the "+ db.getName() + "? (" + World.currentMoneyDisplay(buyGold) + ")");//TODO: explain aether conversion
				if (Input.yesNo()) {
					DrawBane sellItem = bag.buyNewDrawBanePlayer(db);
					if (sellItem != null) {
						Services.sellItem(sellItem, bag);
					}
					Player.bag.addGold(-buyGold);
					dbs.remove(index);
				}
			}else {
				Print.println(TrawelColor.RESULT_ERROR+"You cannot afford this item.");
			}
			return;
		}
		Item buyItem = items.get(index);
		if (canSee(buyItem) <= 0) {
			return;
		}
		ItemType itemType = buyItem.getType();
		Item result = AIClass.storeBuyCompareItem(buyItem, this);
		if (result == buyItem) {
			Print.println("You decide not to buy the item.");
			return;
		}
		if (result != null) {
			items.set(items.indexOf(buyItem),result);
		}else {
			items.remove(buyItem);
		}
		
		this.addBuy();
		switch (itemType) {
		case ARMOR:
			Print.println("They "+Rand.choose("take","pick up","claim","swap for")+" the " + buyItem.getName() + ".");
			//arraySwap(bag.swapArmorSlot((Armor)buyItem, slot),buyItem);
			break;
		case RACE:
			//arraySwap(bag.swapRace((Race)buyItem),buyItem);
			break;
		case WEAPON:
			Print.println("They "+Rand.choose("take","pick up","claim","swap for")+" the " + buyItem.getName() + ".");
			//arraySwap(bag.swapWeapon((Weapon)buyItem),buyItem);
			break;	
		}
		/*
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
		*/
		
		
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
		if (type == 7) {//generate item list on the fly, then delete in menu back
			items = new ArrayList<Item>();
			for (int i = 0; i < races.size();i++) {
				items.add(RaceFactory.getRace(races.get(i)));
			}
		}
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
						return 
					/*"They will exchange "+aetherPerMoney(Player.player.getPerson()) + " aether for "
					+ World.currentMoneyDisplay(1)
					+" which is " +rateString(rateGuess(Player.NORMAL_AETHER_RATE,aetherRate,true)) + " rate."*/
					"Their prices seem " + priceRateString(rateGuess(BASE_MARKUP,markup,false)) +".";
					}});
				/*
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "You have a total of "+(Player.player.getGold() + (Player.bag.getAether()/aetherPerMoney(Player.player.getPerson()))) +" buying power.";
					}});*/
				
				if (type < 8) {//normal items
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
				list.add(new MenuBack() {
					
					@Override
					public boolean go() {
						if (type == 7) {
							//clean up temp item list
							items = null;
						}
						return true;
					}
				});
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
			return item.getName() + " cost: " + Print.F_WHOLE.format(Math.ceil(item.getValue()*markup));
		}

		@Override
		public boolean go() {
			serviceItem(dbs.indexOf(item));
			return false;
		}
		
	}

	protected void restock() {
		if (this.type == 8 || type == 9) {
			dbs.clear();
			for (int i = invSize;i >= 0;i--) {
				addAnItem();
			}
			return;
		}
		items.clear();
		for (int i = invSize;i >= 0;i--) {
			addAnItem();
		}
		
	}
	
	@Override
	public void go() {
		Input.menuGo(modernStoreFront());
	}
	
	@Override
	public List<TimeEvent> passTime(double addtime, TimeContext calling) {
		time += addtime;
		if (time > 12+(Rand.getRand().nextInt(30))) {
			Print.offPrintStack();
			goShopping();
			Print.popPrintStack();
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
			Player.player.addAchieve(this, getStoreName() + " customer");
		}
		if (buys == 10) {
			Player.player.addAchieve(this, "Valued " + getStoreName() + " customer");
		}
	}
	
	/**
	 * substores override this
	 */
	protected String getStoreName() {
		return name;
	}
	
	/**
	 * will return null if added a drawbane
	 * <br>
	 * note that if the item is a race, those get saved differently with ids
	 */
	public Item addAnItem() {
		if (type >= 8) {
			if (dbs.size() >= invSize) {
				dbs.remove(Rand.randRange(0,dbs.size()-1));
			}
			switch (type) {
			case 8:
				dbs.add(DrawBane.draw(DrawList.GENERIC_STORE));
				return null;
			case 9:
				dbs.add(DrawBane.draw(DrawList.WITCH_STORE));
				return null;
			case 10:
				dbs.add(DrawBane.draw(DrawList.FOOD));
				return null;
			case 11:
				dbs.add(DrawBane.draw(DrawList.COLLECTOR));
				return null;
			}
			return null;
		}
		if (type == 7) {
			if (races.size() >= invSize) {
				races.remove(Rand.randRange(0,races.size()-1));
			}
			Race addRace = RaceFactory.randRace(Race.RaceType.PERSONABLE);
			races.add(addRace.raceID());
			return addRace;
		}
		if (items.size() >= invSize) {
			items.remove(Rand.randRange(0,items.size()-1));
		}
		if (type < 5) {
			Armor a = new Armor(Rand.randRange(itemMinLevel, itemMaxLevel),type);
			items.add(a);
			return a;
		}
		if (type == 5) {
			Weapon w = Weapon.genMidWeapon(Rand.randRange(itemMinLevel, itemMaxLevel));
			items.add(w);
			return w;
		}
		if (type == 6) {
			if (Rand.randFloat() > .5f) {
				Weapon w = Weapon.genMidWeapon(Rand.randRange(itemMinLevel, itemMaxLevel));
				items.add(w);
				return w;
			}else {
				Armor a = new Armor(Rand.randRange(itemMinLevel, itemMaxLevel));
				items.add(a);
				return a;
			}
		}
		throw new RuntimeException("Cannot add item to store " + getName() + " with type " + type);
	}
	private void goShopping() {
		if (type == 9) {//potion shops apply some, and also refills
			WitchHut.randomRefillsAtTown(town,tier);
			return;
		}
		if (type >= 7) {//7 is species store, doesn't currently work for npcs due to needing to serialize differently
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
			if (a.getPerson().getBag().getAether()+delta >= 0 && AIClass.compareItem(bag,i,p)) {
				a.getPerson().getBag().addAether(delta);
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
