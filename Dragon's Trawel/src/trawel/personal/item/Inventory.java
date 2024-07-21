package trawel.personal.item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import derg.menus.MenuBack;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.Stance;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.core.mainGame.GraphicStyle;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.Skill;
import trawel.personal.item.body.Race;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Armor.ArmorQuality;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.variants.ArmorStyle;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.types.Quest.TriggerType;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.data.Calender;

/**
 * 
 * @author dragon
 * 2/8/2018
 * 
 * An inventory, which holds 5 armors, and a weapon, as wells as some money and aether.
 *
 * Must have all it's slots filled when in personal use, or else it will cause errors.
 */
public class Inventory implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	//instance vars
	private int money = 0, aether = 0;
	/**
	 * only dummy inventory should use protected status
	 */
	protected Armor[] armorSlots = new Armor[5];
	private Weapon hand;
	private RaceID race;
	private int raceMap;
	/**
	 * this list includes both drops (body flesh, parts) and held things, so only player can really use it
	 */
	private List<DrawBane> dbs = new ArrayList<DrawBane>();
	private List<Seed> seeds = null;
	public Person owner;
	
	private transient EnumMap<ArmorQuality,Integer> qualityCount = null;
	
	//constructors
	/**
	 * Create a new inventory, populated with items of level level.
	 * @param level (int)
	 * @param matType 
	 */
	public Inventory(int level, Race.RaceType type, Material matType,Person.AIJob job, Race ra) {
		boolean isDummy = false;
		if (level == -1) {
			isDummy = true;
			level = 10;
		}
		if (type == Race.RaceType.PERSONABLE) {
			if (job != null) {
				ArmorStyle matType2 = job.amatType[Rand.randRange(0,job.amatType.length-1)];
				armorSlots[0] = new Armor(level,(byte) 0,null,matType2);
				matType2 = job.amatType[Rand.randRange(0,job.amatType.length-1)];
				armorSlots[1] = new Armor(level,(byte) 1,null,matType2);
				matType2 = job.amatType[Rand.randRange(0,job.amatType.length-1)];
				armorSlots[2] = new Armor(level,(byte) 2,null,matType2);
				matType2 = job.amatType[Rand.randRange(0,job.amatType.length-1)];
				armorSlots[3] = new Armor(level,(byte) 3,null,matType2);
				matType2 = job.amatType[Rand.randRange(0,job.amatType.length-1)];
				armorSlots[4] = new Armor(level,(byte) 4,null,matType2);
				hand = new Weapon(level,job.randWeap());
			}else {
				
				if (isDummy && matType != null) {
					armorSlots[0] = new Armor(level,0,matType);
					armorSlots[1] = new Armor(level,1,matType);
					armorSlots[2] = new Armor(level,2,matType);
					armorSlots[3] = new Armor(level,3,matType);
					armorSlots[4] = new Armor(level,4,matType);
				} else {
					armorSlots[0] = new Armor(level,0);
					armorSlots[1] = new Armor(level,1);
					armorSlots[2] = new Armor(level,2);
					armorSlots[3] = new Armor(level,3);
					armorSlots[4] = new Armor(level,4);
				}
				if (isDummy) {
					hand = null;
				}else {
					hand = Weapon.genMidWeapon(level);
				}
			}
		}
		if (type == Race.RaceType.BEAST) {
			armorSlots[0] = new Armor(level,0,matType);
			armorSlots[1] = new Armor(level,1,matType);
			armorSlots[2] = new Armor(level,2,matType);
			armorSlots[3] = new Armor(level,3,matType);
			armorSlots[4] = new Armor(level,4,matType);
			hand = null;//new Weapon(level); //beasts always swap out the weapon, thus making it is pointless
		}
		if (ra == null) {
			ra = RaceFactory.randRace(type);
			//might even want to swap to just humanoid
		}
		race = ra.raceID();
		
		raceMap = ra.randomRaceMap();
	}
	

	//instance
	/**
	 * Get a ref to an armor in slot slot.
	 * @return  armorSlots (Armor)
	 */
	public Armor getArmorSlot(int slot) {
		return armorSlots[slot];
	}
	
	public Stream<Item> getSolids(){
		Stream<Item> nullCheck = Stream.concat(Arrays.stream(armorSlots),Stream.of(hand));
		//for some inane reason it refuses to cast to just a stream and tries to make it a combo of IEffectiveLevel, but ONLY
		//if I try to return it after filtering WITHOUT storing it in a variable first
		return nullCheck.filter(a -> a != null);
	}
	
	public Iterable<Armor> getArmors(){
		return new ArmorIterable();
	}
	
	public class ArmorIterable implements Iterable<Armor>{

		@Override
		public Iterator<Armor> iterator() {
			return new ArmorIts();
		}
		
	}
	
	public class ArmorIts implements Iterator<Armor>{

		private Armor current;
		
		public ArmorIts() {
			current = scanFrom(0);
		}
		
		private Armor scanFrom(int i) {
			Armor[] slots = Inventory.this.armorSlots;
			for (;i < slots.length;i++) {
				if (slots[i] != null) {
					return slots[i];
				}
			}
			return null;
		}
		
		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public Armor next() {
			Armor ret = current;
			current = scanFrom(current.getSlot()+1);
			return ret;
		}
		
	}


	/**
	 * Swaps out an armor for the one in the slot.
	 * <br>
	 * if you call this directly, you are responsible for updating capacity in attribute boxes
	 * <br>
	 * cannot accept nulls, but may return nulls
	 * @param newArmor (Armor)
	 * @param slot (int)
	 * @return old armor (Armor)
	 */
	public Armor swapArmorSlot(Armor newArmor, int slot) {
		Armor tempArm = armorSlots[slot];
		armorSlots[slot] = newArmor;
		return tempArm;
	}
	/**
	 * @param slot
	 */
	public void nullArmorSlot(int slot) {
		armorSlots[slot] = null;
		qualityCount = null;
	}
	
	/**
	 * Get the average of the sharp resistance of the inventory.
	 * @return (double) - sharp resistance
	 */
	public double getSharpResist() {
		double retResist = 0;
		for (Armor a: getArmor()) {
			retResist += a.getSharpResist();
		}
		return extra.zeroOut(retResist/5f);
	}
	
	/**
	 * Get the average of the blunt resistance of the inventory.
	 * @return (double) - blunt resistance
	 */
	public double getBluntResist() {
		double retResist = 0;
		for (Armor a: getArmor()) {
			retResist += a.getBluntResist();
		}
		return extra.zeroOut(retResist/5f);
	}
	
	/**
	 * Get the average of the piercing resistance of the inventory.
	 * @return (double) - piercing resistance
	 */
	public double getPierceResist() {
		double retResist = 0;
		for (Armor a: getArmor()) {
			retResist += a.getPierceResist();
		}
		return extra.zeroOut(retResist/5f);
	}
	
	/**
	 * Get the total dodge modifier of the inventory
	 * @return (double) - dodge
	 */
	public double getDodge() {
		int i = 0;
		double retMod = 1;
		while (i < 5) {
			if (armorSlots[i] == null) {
				i++;
				continue;
			}
			if (armorSlots[i].isEnchanted()) {
				retMod *= armorSlots[i].getEnchant().getDodgeMod();
			}
			//retMod *= armorSlots[i].getAgiPenMult();
			//now applied elsewhere
		i++;
		}
		if (hand != null && hand.isEnchantedConstant()) {
			retMod *= hand.getEnchant().getDodgeMod();
		}
		retMod*=getRace().dodgeMod;
		if (owner != null) {
			retMod *= owner.getTotalAgiPen();
		}else {
			retMod *= getAgiPen();
		}
		if (owner != null) {
			if (owner.hasEffect(Effect.BEE_SHROUD)) {
				retMod*=1.1;
			}
		}
		
		
		return extra.zeroOut(retMod);
	}
	
	/**
	 * Get the total aiming modifier of the inventory
	 * @return (double) - aim
	 */
	public double getAim() {
		double retMod = 1;
		for (Armor a: getArmors()) {
			if (a.isEnchanted()) {
				retMod *= a.getEnchant().getAimMod();
			}
		}
		if (hand != null && hand.isEnchantedConstant()) {
			retMod *= hand.getEnchant().getAimMod();
		}
		retMod*=getRace().aimMod;
		return extra.zeroOut(retMod);
	}
	
	/**
	 * Get the total damage modifier of the inventory
	 * @return (double) - damage mod
	 */
	public double getDam() {
		double retMod = 1;
		for (Armor a: getArmors()) {
			if (a.isEnchanted()) {
				retMod *= a.getEnchant().getDamMod();
			}
		}
		if (hand != null && hand.isEnchantedConstant()) {
			retMod *= hand.getEnchant().getDamMod();
		}
		retMod*=getRace().damMod;
		return extra.zeroOut(retMod);
	}
	
	
	/**
	 * Get the total health modifier of the inventory
	 * @return (double) - health mod
	 */
	public double getHealth() {
		double retMod = 1;
		for (Armor a: getArmors()) {
			if (a.isEnchanted()) {
				retMod *= a.getEnchant().getHealthMod();
			}
		}
		if (hand != null && hand.isEnchantedConstant()) {
			retMod *= hand.getEnchant().getHealthMod();
		}
		retMod*=getRace().hpMod;
		return extra.zeroOut(retMod);
	}
	
	/**
	 * Get the total speed modifier of the inventory
	 * @return (double) - dodge mod
	 */
	public double getSpeed() {
		double retMod = 1;
		for (Armor a: getArmors()) {
			if (a.isEnchanted()) {
				retMod *= a.getEnchant().getSpeedMod();
			}
		}
		if (hand != null && hand.isEnchantedConstant()) {
			retMod *= hand.getEnchant().getSpeedMod();
		}
		retMod*=getRace().speedMod;
		return extra.zeroOut(retMod);
	}
	
	/**
	 * Get the stance of the current weapon.
	 * @return (Stance)
	 */
	public Stance getStance() {
		return hand.getMartialStance();
	}

	/**
	 * Get the pointer to the weapon of the inventory.
	 * @return (Weapon) in hand
	 */
	public Weapon getHand() {
		return hand;
	}
	
	/**
	 * Get printable string of the inventory.
	 * Contains plurality issues that would be far to difficult to correct easily.
	 * @return (String)
	 */
	public String nameInventory() {
		String tempStr = "a";
		int i = 0;
		while (i < 5) {
			tempStr += " "+ armorSlots[i].getName() + ",";
			i++;
		}
		if (owner.getSuper()!= null) {
				return tempStr + ( hand.canDisplay() ? " and a " + hand.getName()+"," : "") + " as well as " 
						+ 
						owner.getSuper().getGoldDisp()
						+".";
		}
		return tempStr + " and a " + hand.getName() + " as well as " 
		+ 
		World.currentMoneyDisplay(money)
		+".";
		//There are way to many plurals to account for
	}//gold + " " + extra.choose("gold","gold pieces","pieces of gold")
	public String quickInventory() {
		String tempStr = "";
		for (Armor a: armorSlots) {
			String name = null;
			if (a.canAetherLoot()) {//use this to determine to display
				name = a.getName();
			}
			if (name != null) {
				if (tempStr != "") {
					tempStr+=", "+name;
				}else {
					tempStr += name;
				}
			}
		}
		if (hand.canAetherLoot()) {
			if (tempStr != "") {
				tempStr+=", ";
			}
			tempStr+= hand.getName()+".";
		}else {
			tempStr +=".";
		}
		return tempStr;
	}
	
	/**
	 * Swaps out a new weapon.
	 * <br>
	 * cannot accept nulls, but may return nulls
	 * @param newWeap (Weapon)
	 * @return old weapon (Weapon)
	 */
	public Weapon swapWeapon(Weapon newWeap) {
		assert newWeap != null;
		Weapon tempWeap = hand;
		hand = newWeap;
		return tempWeap;
	}
	
	/**
	 * does not print anything
	 * <br>
	 * can accept nulls
	 * @param newWeap
	 */
	public void setWeapon(Weapon newWeap) {
		hand = newWeap;
	}


	/**
	 * @return the gold in the inventory (int)
	 */
	public int getGold() {
		if (owner.getSuper() != null) {
			World w = owner.getSuper().getWorld();
			if (w == null) {
				if (money > 0) {
					owner.getSuper().addGold(money, Player.getPlayerWorld());
					money = 0;
				}
				return owner.getSuper().getGold(Player.getPlayerWorld());
			}
			if (money > 0) {
				owner.getSuper().addGold(money,w);
				money = 0;
			}
			return owner.getSuper().getGold(w);
		}
		return money;
	}
	
	public int surrenderRawMoney() {
		int temp = money;
		money = 0;
		return temp;
	}


	/**
	 * @param gold the gold to set (int)
	 */
	public void setLocalGold(int gold) {
		this.money = gold;
	}
	
	public int getLocalGold() {
		return money;
	}
	
	public void deepDisplay() {
		for (Armor a: getArmors()) {
			a.display(2);
		}
		hand.display(2);
		//hand.getMartialStance().display(1);
	
		if (owner.getSuper() != null) {
			Print.println("Local Currency: "+ owner.getSuper().getGoldDisp() +". All: " + owner.getSuper().allGoldDisp()+".");
			if (owner.getSuper().hasFlask()) {
				if (owner.getSuper().knowsPotion()) {
					Print.println(owner.getSuper().peekFlask().getName()+TrawelColor.PRE_WHITE+" potion with " +TrawelColor.ITEM_WANT_HIGHER+ owner.getSuper().getFlaskUses() +TrawelColor.PRE_WHITE+ " uses left.");
				}else {
					Print.println("Potion with " +TrawelColor.ITEM_WANT_HIGHER+ owner.getSuper().getFlaskUses() +TrawelColor.PRE_WHITE+ " uses left.");
				}
			}
		}else {
			Print.println("Local Currency: "+ World.currentMoneyDisplay(money) +".");
		}
	
	}

	
	public void graphicalDisplay(int side, Person p) {
		Networking.setSideAs(side,p);
		Race r_race = getRace();
		switch (mainGame.graphicStyle) {
		case LEGACY:
			Networking.sendStrong("RaceFlag|"+side+"|"+p.getRaceFlag().name()+"|");
			Networking.sendStrong("RaceInv|"+side+"|" +r_race.getLegacySprite()+"|"+r_race.getLegacyMap()+"|"+r_race.getLegacyNumber(raceMap)+"|"+p.getRaceFlag().name()+ "|"+p.bloodSeed + "|" + p.getBloodCount() + "|1|body|");
			if (p.getScar() != null) {
				Networking.sendStrong("AddInv|"+side+"|" + p.getScar() +"|iron|0|" + p.bloodSeed + "|" + p.getBloodCount()+"|0|0|body|");
			}
			if (r_race.racialType == Race.RaceType.PERSONABLE) {
				for (Armor a: armorSlots) {
					if (a == null || a.getStyle() == ArmorStyle.BODY) {
						continue;
					}
					int slot = a.getArmorType();
					String str = "AddInv|"+side+"|" +a.getStyle().legacyName[slot] +"|"+a.getBaseMap(GraphicStyle.LEGACY)+"|"+a.getMat().palIndex+"|"+a.bloodSeed + "|" + a.getBloodCount() + "|" +(a.getEnchant() != null ? a.getEnchant().enchantstyle :0 )+"|";
					switch (slot) {
					case 0:str+= "-6|head|";break; //head
					case 1:str+= "-3|arms|";break; //arms
					case 2:str+= "-5|chest|";break; //chest
					case 3:str+= "-1|legs|";break; //legs
					case 4:str+= "-2|feet|";break; //feet
					}

					Networking.sendStrong(str);
				}
				if (hand != null && hand.getWeaponType().getLegacy() != null) {
					Networking.sendStrong("AddInv|"+side+"|" +hand.getWeaponType().getLegacy() +"|iron|"+hand.getMat().palIndex+ "|" + hand.bloodSeed + "|" + hand.getBloodCount() +"|" +(hand.getEnchant() != null ? hand.getEnchant().enchantstyle :0 )+"|2|hand|");
				}
			}else {
				if (p.getBag().getRace().raceID() == RaceID.B_WOLF) {
					Networking.sendStrong("AddInv|"+side+"|" +"wolf_teeth" +"|iron|"+hand.getMat().palIndex+ "|" + hand.bloodSeed + "|" + hand.getBloodCount() +"|" +(hand.getEnchant() != null ? hand.getEnchant().enchantstyle :0 )+"|-9|body|");
				}
			}
			Networking.sendStrong("ClearInv|"+side+"|");
			break;
		case WASDD:
			Networking.sendStrong("RaceFlag|"+side+"|"+p.getRaceFlag().name()+"|");
			Networking.addGraphicalRace(side,r_race,r_race.getWasddNumber(raceMap),p.getRaceFlag().name(), p.bloodSeed, p.getBloodCount(),"body");
			if (r_race.racialType == Race.RaceType.PERSONABLE) {
				for (Armor a: armorSlots) {
					if (a == null || a.getStyle() == ArmorStyle.BODY) {
						continue;
					}
					int slot = a.getArmorType();
					switch (slot) {
					case 0://head
							Networking.addGraphicalInv(side,
									"armor_"+a.getStyle().wasddName+"_helm",
									a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
									-6, "head");
							break;
					case 1://arms
					Networking.addGraphicalInv(side,
							"armor_"+a.getStyle().wasddName+"_rightarm",
							a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
							-3, "arms");
					Networking.addGraphicalInv(side,
							"armor_"+a.getStyle().wasddName+"_leftarm",
							a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
							-5, "arms");
						//gloves
						Networking.addGraphicalInv(side,
								"armor_"+a.getStyle().wasddName+"_rightglove",
								a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
								-9, "arms");
						Networking.addGraphicalInv(side,
								"armor_"+a.getStyle().wasddName+"_leftglove",
								a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
								-9, "arms");
						break;
					case 2://chest
						Networking.addGraphicalInv(side,
								"armor_"+a.getStyle().wasddName+"_chest",
								a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
								-4, "chest");
						break;
					case 3://legs
						Networking.addGraphicalInv(side,
								"armor_"+a.getStyle().wasddName+"_legs",
								a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
								-1, "legs");
						break;
					case 4://feet
						Networking.addGraphicalInv(side,
								"armor_"+a.getStyle().wasddName+"_boots",
								a.getBaseMap(GraphicStyle.WASDD),a.getMat().palIndex, a.bloodSeed, a.getBloodCount(), (a.getEnchant() != null ? a.getEnchant().enchantstyle :0 ),
								-2, "feet");
					}
				}
				if (hand != null && hand.getWeaponType().getWasdd() != null) {
					Networking.addGraphicalInv(side,
							"weapon_"+hand.getWeaponType().getWasdd(),
							"wasdd",hand.getMat().palIndex,hand.bloodSeed,hand.getBloodCount(),
							(hand.getEnchant() != null ? hand.getEnchant().enchantstyle :0 ),-7,"hand");
				}
			}
			//commit changes
			Networking.sendStrong("ClearInv|"+side+"|");
			break;
		}

	}
	

	public void addGold(int add) {
		/*SuperPerson sp = owner.getSuper();
		if (sp != null) {
			sp.addGold(add);
			return;
		}
		money += add;
		money = Math.max(money,0);*/
		addLocalGoldIf(add);
	}
	
	/**
	 * will add local gold if world gold wouldn't be added due to no super person or no world set
	 */
	public void addLocalGoldIf(int add) {
		SuperPerson sp = owner.getSuper();
		if (sp != null && sp.getWorld() != null) {
			sp.addGold(add);
			return;
		}
		money += add;
		money = Math.max(money,0);
	}
	
	public RaceID getRaceID() {
		return race;
	}

	public Race getRace() {
		return RaceFactory.getRace(race);
	}
	
	public Race swapRace(Race newRace) {
		Race r = RaceFactory.getRace(race);
		setRace(newRace.raceID());
		return r;
	}
	
	public void setRace(RaceID race) {
		/**
		 * personable races don't quite have 'features' like beast-ish things might
		 */
		if (RaceFactory.getRace(race).racialType != Race.RaceType.PERSONABLE) {
			if (RaceFactory.getRace(this.race).archetype != RaceFactory.getRace(race).archetype) {
				owner.updateRaceArch();
			}
			owner.updateRaceWeapon();
		}
		this.race = race;
	}

	public void setRace(Race race) {
		setRace(race.raceID());
	}

	

	public double getSharpResistOOB(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getSharpResist())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	public double getBluntResistOOB(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getBluntResist())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	public double getPierceResistOOB(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getPierceResist())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	//TO BE USED IN COMBAT
	public double slotMult(ImpairedAttack att, Armor arm) {
		double mult;
		if (att.getSlot() == arm.getSlot()) {
			mult = 2;
			if (att.hasWeaponQual(Weapon.WeaponQual.PENETRATIVE) && !arm.hasArmorQual(ArmorQuality.DEFLECTING)) {
				mult-=0.25;
			}
		}else {
			mult = .75;
			if (att.hasWeaponQual(Weapon.WeaponQual.PINPOINT) && !arm.hasArmorQual(ArmorQuality.DEFLECTING)) {
				mult-=0.25;
			}
		}
		return mult;
	}
	
	public double getSharp(ImpairedAttack att) {
		int i = 0;
		double retResist = 0;
		while (i < 5) {//should never be null
			retResist += (armorSlots[i].getSharp())*slotMult(att,armorSlots[i]);
			i++;
		}
		return extra.zeroOut(retResist/5f);//slots?
	}
	
	public double getBlunt(ImpairedAttack att) {
		int i = 0;
		double retResist = 0;
		while (i < 5) {//should never be null
			retResist += (armorSlots[i].getBlunt())*slotMult(att,armorSlots[i]);
			i++;
		}
		return extra.zeroOut(retResist/5f);//slots?
	}
	public double getPierce(ImpairedAttack att) {
		int i = 0;
		double retResist = 0;
		while (i < 5) {//should never be null
			retResist += (armorSlots[i].getPierce())*slotMult(att,armorSlots[i]);
			i++;
		}
		return extra.zeroOut(retResist/5f);//slots?
	}
	
	public double getIgniteMult(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getFireMod())*mult;
			i++;
		}
		return extra.zeroOut(retResist/5f);//slots?
	}
	
	public double getElecMult(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getShockMod())*mult;
			i++;
		}
		return extra.zeroOut(retResist/5f);//slots?
	}
	
	public double getFrostMult(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getFreezeMod())*mult;
			i++;
		}
		return extra.zeroOut(retResist/5f);//slots?
	}
	
	/**
	 * now not used for true battle setup, use each armor's reset manually in that case
	 */
	public void resetArmor(int s, int b, int p) {
		for (Armor a: armorSlots) {//should never be null
			a.resetArmor(s, b, p);
		}
	}
	
	public void burnArmor(double percent) {
		for (Armor a: armorSlots) {
			a.burn(percent);
		}
	}

	public void burnArmor(double percent, int slot) {
		int i = 0;
		double mult;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			armorSlots[i].burn(mult * percent);
			i++;
		}
	}
	
	public void damageArmor(double percent, int slot) {
		int i = 0;
		double mult;
		while (i < 5) {//should never be null
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			armorSlots[i].damage(mult * percent);
			i++;
		}
	}
	
	public void buffArmor(double f) {
		for (int i = 0; i < 5; i++) {
			armorSlots[i].buff(f);
		}
	}
	
	public void buffArmorAdd(double f) {
		for (int i = 0; i < 5; i++) {
			armorSlots[i].buffAdd(f);
		}
	}
	
	public void turnTick() {
		for (int i = 0; i < 5; i++) {
			armorSlots[i].buffDecay();
		}
	}

	public Armor[] getArmor() {
		return armorSlots;
	}

	public Item swapItem(Item i) {
		if (Armor.class.isInstance(i)) {
			Armor a = (Armor)i;
			a = swapArmorSlot(a,a.getArmorType());
			if (owner != null) {
				owner.resetCapacity();
			}
			return a;
		}
		if (Weapon.class.isInstance(i)) {
			Weapon w = (Weapon)i;
			w = swapWeapon(w);
			if (owner != null) {
				owner.resetCapacity();
			}
			return w;
		}
		if (Race.class.isInstance(i)) {
			return swapRace((Race)i);
		}
		return null;
	}
	
	public Item itemCounterpart(Item i) {
		if (Armor.class.isInstance(i)) {
			Armor a = (Armor)i;
			return this.getArmorSlot(a.getArmorType());
		}
		if (Weapon.class.isInstance(i)) {
			return this.getHand();
		}
		if (Race.class.isInstance(i)) {
			return this.getRace();
		}
		return null;
	}

	public void restoreArmor(double d) {
		for (Armor a: armorSlots) {
			a.restoreArmor(d);
		}
		
	}

	public String getSoundType(int slot) {
		return armorSlots[slot].getSoundType();//should never be null
	}
	
	public List<DrawBane> getDrawBanes() {
		return dbs;
	}
	
	public boolean hasDrawBane(DrawBane d) {
		return dbs.contains(d);
	}
	
	public int drawBaneCap() {
		if (owner.hasSkill(Skill.BIG_BAG)) {
			return 8;
		}
		return 4;
	}
	
	public void addDrawBaneSilently(DrawBane d) {
		dbs.add(d);
	}
	
	public DrawBane addNewDrawBanePlayer(DrawBane d) {
		if (triggerDrawBane(d)) {
			return null;
		}
		return handleDrawBane(d,false,"replace");
	}
	
	public DrawBane buyNewDrawBanePlayer(DrawBane d) {
		if (triggerDrawBane(d)) {
			return null;
		}
		return handleDrawBane(d,true,"sell");
	}
	
	/**
	 * 
	 * @return true if consumed
	 */
	public boolean triggerDrawBane(DrawBane d) {
		if (d != null) {
			//process obtain triggers
			switch (d) {
			case KNOW_FRAG:
				Print.println("You found a Feat Fragment!");
				Player.player.currentKFrags++;
				if (Player.player.currentKFrags >= Player.player.fragmentReq) {
					Print.println("Bring your Feat Fragments to a library to gain a feat point!");
				}
				//consume fragment
				return true;
			}
		}
		return false;
	}
	
	public DrawBane handleDrawBane(DrawBane d, boolean offering, String text) {
		int oldSize = dbs.size();
		if (d != null) {
			Print.println("You found - " + d.getName() + ": " + d.getFlavor());
			if (Player.player.hasTrigger("db:"+d.name())) {
				Print.println("You have a quest for this DrawBane, discarding it will grant progress.");
			}
			dbs.add(d);//add immediately
		}
		final DrawBane[] ret = new DrawBane[] {null};
		Input.menuGo(new ScrollMenuGenerator(oldSize, "previous <> drawbanes", "next <> drawbanes") {

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				DrawBane b = dbs.get(i);
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return Print.capFirst(text)+" " +b.getName() + " ("+TrawelColor.ITEM_VALUE+b.getValue()+TrawelColor.COLOR_RESET+"): " + b.getFlavor();
					}

					@Override
					public boolean go() {
						ret[0] = dbs.remove(i);
						return true;
					}});
				return list;
			}

			@Override
			public List<MenuItem> header() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				if (d != null && oldSize < drawBaneCap()) {//we have the new element already
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Take "+d.getName()+".";
						}

						@Override
						public boolean go() {
							//already added
							return true;
						}});
				}
				return list;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				if (d == null) {
					if (oldSize == 0) {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "You have no DrawBanes to "+text+".";
							}});
					}
					
					list.add(new MenuBack(Print.capFirst(text)+" nothing."));
				}else {
					//clear the back queue if what the player is looking at changes
					list.add(new MenuBack() {
	
						@Override
						public String title() {
							return (offering ? Print.capFirst(text) : "Discard")
									+" new " + d.getName()+" ("+d.getValue()+").";
						}
	
						@Override
						public boolean go() {
							ret[0] = dbs.remove(dbs.size()-1);
							return true;
						}});
				}
				return list;
			}});
		DrawBane b = ret[0];
		if (b != null && !offering) {
			playerDiscardDrawBaneCleanup(b);
			return null;
		}
		return b;
	}
	
	private void playerDiscardDrawBaneCleanup(DrawBane d) {
		if (Player.player.hasTrigger("db:"+d.name())) {
			Player.player.questTrigger(TriggerType.COLLECT,"db:"+d.name(),15);
		}
		if (d == DrawBane.CLOTH) {
			washAll();
		}
	}
	
	/**
	 * used only for when a player presents a drawbane, and it isn't wanted
	 * @param d
	 * @paremt rejection text, use %s to replace. null permitted
	 */
	public void giveBackDrawBane(DrawBane d,String rejectText) {
		if (d == null) {
			return;
		}
		if (rejectText != null) {
			Print.println(rejectText.replaceAll(Pattern.quote("%"),d.getName()));
		}
		dbs.add(d);
	}
	
	/**
	 * use when not offering to something else
	 */
	public DrawBane playerDiscardDrawBane() {
		return handleDrawBane(null,false,"discard");
	}
	
	public DrawBane playerOfferDrawBane(String offerText) {
		return handleDrawBane(null,true,offerText);
	}


	public void washAll() {
		for (Armor a: this.getArmor()) {
			a.wash();
		}
		this.getHand().wash();
		if (Player.bag == this) {
			this.graphicalDisplay(-1,Player.player.getPerson());
		}
	}


	public double calculateDrawBaneFor(DrawBane d) {
		float i = 0;
		switch (d) {
		case SILVER:
			for (Armor a: getArmors()) {
				if (a.getMaterialName().equals("silver")) {
					i++;
				}
			}
			if (hand.getMaterialName().equals("silver")) {
				i++;
			}
			break;
		case GOLD:
			for (Armor a: getArmors()) {
				if (a.getMaterialName().equals("gold")) {
					i++;
				}
			}
			if (hand.getMaterialName().equals("gold")) {
				i++;
			}
			break;
		case EV_BLOOD:
			float sub = 0;
			//bloodcount is max 16 per, 5 + 1 + 1; 0 to 96
			for (Armor a: getArmors()) {
				sub += a.getBloodCount();//max 16 per armor
			}
			sub += owner.getBloodCount();
			sub += hand.getBloodCount();
			i += sub/16;//max is 5, so will be maxed out with 1 less item fully bloody
			for (DrawBane db: dbs) {
				if (db.equals(DrawBane.BLOOD)) {//blood also counts
					i++;
				}
			}
			break;
		case EV_NOTHING:
			return 1;
		case EV_DAYLIGHT:
			if (!owner.isPlayer()) {
				return 0;//MAYBELATER: expensive calculation
			}
			Town town = Player.player.getLocation();
			double[] p = Calender.lerpLocation(town);
			return town.getIsland().getWorld().getCalender().sunlightAmount(p[0],p[1]);
		case EV_WEALTH:
			i += getGold()/(15*owner.getUnEffectiveLevel());
			for (DrawBane db: dbs) {
				if (db.equals(DrawBane.CLOTH)) {
					i--;//cloth hides wealth
				}
			}
			break;
		}
		
		for (DrawBane db: dbs) {
			if (db.equals(d)) {
				i++;
			}
		}
		return extra.clamp(i,0,5);
		
	}


	public Seed getSeed() {
		if (seeds == null || seeds.size() == 0) {
			Print.println("You don't have any seeds!");
			return null;
		}
		displaySeeds();
		int in;
		if (seeds.size() <= 6) {
			Print.println("9 keep");
			in = Input.inInt(seeds.size(),true,true);
		}else {
			in = Input.inInt(seeds.size());
		}
		if (in >= seeds.size()+1) {
			return null;
		}
		return seeds.remove(in-1);
		
	}


	public void displaySeeds() {
		if (seeds == null) {
			return;
		}
		for (int i = 0; i < seeds.size(); i++) {
			Print.println((i+1) + " " + seeds.get(i).toString().toLowerCase());
		}
		
	}


	public void addSeed(Seed e) {
		if (seeds == null) {
			seeds = new ArrayList<Seed>();
		}
		seeds.add(e);
		if (!owner.isPlayer()) {
			if (seeds.size() > 6) {
				seeds.remove(0);
			}
			return;
		}
		Print.println("You got the " + e.toString().toLowerCase() + "!");
		while (seeds.size() > 6) {
			Print.println("You have too many seeds. Choose one to remove!");
			getSeed();
		}
		
	}


	public void clearDrawBanes() {
		this.dbs.clear();
		
	}
	
	public void deEnchant() {
		for (Armor a: getArmors()) {
			a.deEnchant();
		}
		hand.deEnchant();
	}

	//MAYBELATER: naive aether money
	private int getWorth() {
		int value = this.getAether();
		for (Armor a: getArmors()) {
			value += a.getAetherValue();
		}
		if (hand != null) {
			value += hand.getAetherValue();
		}
		return value;
	}
	
	public int countArmorQuality(Armor.ArmorQuality qual) {
		int count = 0;
		for (Armor a: getArmors()) {
			if (a.getQuals().contains(qual)) {
				count++;
			}
		}
		return count;
	}
	
	/*
	public void armorQualDam(float hpPercent) {
		for (Armor a: getArmors()) {
			a.armorQualDam(hpPercent);
		}
	}*/


	public void addAether(int cost) {
		aether = Math.max(0,aether+cost);
	}
	
	public int getAether() {
		return aether;
	}


	/**
	 * used to regenerate sold items on death cheaters
	 * @param level
	 */
	public void regenNullEquips(int level) {
		if (hand == null) {
			hand = new Weapon(level);
		}
		for (int i = 0; i < 5; i++) {
			armorSlots[i] = armorSlots[i] == null ? new Armor(level,i) : armorSlots[i];
		}
	}

	public void removeAllCurrency() {
		money = 0;
		aether = 0;
		if (owner.getSuper() != null) {
			World w = owner.getSuper().getWorld();
			if (w == null) {
				owner.getSuper().removeGold(Player.getPlayerWorld());
			}else {
				owner.getSuper().removeGold(w);
			}
		}
	}
	
	public void removeAether() {
		aether = 0;
	}
	
	public static final float TEMP_WEIGHT_MULT = .06f;
	public static final float LEVEL_CAP_PEN = 2f;
	public static final float LEVEL_CAP_PEN_EXTREME = 3f;
	
	public int getCapacity() {
		float weight = 0;
		if (hand != null) {
			weight += getCapacityLevelMult(hand.getLevel(),owner.getLevel())*hand.getWeight();
		}
		for (Armor a: getArmors()) {
			weight += getCapacityLevelMult(a.getLevel(),owner.getLevel())*a.getWeight();
		}
		return (int) weight;
	}
	
	public static final float getCapacityLevelMult(int itemLevel, int personLevel) {
		if (personLevel >= itemLevel-1) {
			return 1f;
		}
		if (personLevel >= itemLevel-3) {
			return LEVEL_CAP_PEN;
		}
		return LEVEL_CAP_PEN_EXTREME;
	}


	public float getAgiPen() {
		float mult = 0;
		for (int i = 0; i < 5; i++) {
			if (armorSlots[i] == null) {
				continue;
			}
			mult += armorSlots[i].getAgiPenMult();
		}
		assert mult <= 5.1f;
		return mult/5f;
	}


	public boolean forceDownGradeIf(int level) {
		boolean downgraded = false;
		for (Armor a: armorSlots) {
			downgraded |= a.forceDownGradeIf(level);
		}
		downgraded |= hand.forceDownGradeIf(level);
		return downgraded;
	}
	
	public int qualityCount(ArmorQuality qual) {
		if (!qualityCount.containsKey(qual)) {
			return 0;
		}
		return qualityCount.get(qual);
	}
	
	/*
	public void setQualsIfNot() {
		if (qualityCount == null) {
			resetQuals();
		}
	}*/
	
	public void resetQuals() {
		if (qualityCount == null) {
			qualityCount = new EnumMap<ArmorQuality,Integer>(ArmorQuality.class);
		}else {
			qualityCount.clear();
		}
		for (Armor a: armorSlots) {
			for (ArmorQuality q: a.getQuals()) {
				qualityCount.put(q, 1+qualityCount.getOrDefault(q, 0));
			}
		}
	}

}
