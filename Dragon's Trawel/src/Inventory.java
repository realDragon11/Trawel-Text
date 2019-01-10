/**
 * 
 * @author Brian Malone
 * 2/8/2018
 * 
 * An inventory, which holds 5 armors, and a weapon, as wells as some gold.
 *
 * Must have all it's slot's filled, or else it will cause errors.
 */
public class Inventory implements java.io.Serializable{
	
	//instance vars
	private int gold = 0;
	private Armor[] armorSlots = new Armor[5];
	private Weapon hand;
	private Race race;
	private String raceMap;
	
	//constructors
	/**
	 * Create a new inventory, populated with items of level level.
	 * @param level (int)
	 */
	public Inventory(int level) {
		armorSlots[0] = new Armor(level,0);
		armorSlots[1] = new Armor(level,1);
		armorSlots[2] = new Armor(level,2);
		armorSlots[3] = new Armor(level,3);
		armorSlots[4] = new Armor(level,4);
		hand = new Weapon(level);
		race = RaceFactory.randRace();
		raceMap = race.randomRaceMap();
	}
	
	//instance
	/**
	 * Get a pointer to an armor in slot slot.
	 * @return  armorSlots (Armor)
	 */
	public Armor getArmorSlot(int slot) {
		return armorSlots[slot];
	}


	/**
	 * Swaps out an armor for the one in the slot.
	 * @param newArmor (Armor)
	 * @param slot (int)
	 * @return old armor (Armor)
	 */
	public Armor swapArmorSlot(Armor newArmor, int slot) {
		extra.println("They "+extra.choose("take","pick up","claim","swap for")+" the " + newArmor.getName() + ".");
		Armor tempArm = armorSlots[slot];
		armorSlots[slot] = newArmor;
		return tempArm;
	}
	
	/**
	 * Get the sum of the sharp resistance of the inventory.
	 * @return (double) - sharp resistance
	 */
	public double getSharpResist() {
		int i = 0;
		double retResist = 0;
		while (i < 5) {
			retResist += armorSlots[i].getSharpResist()*armorSlots[i].getResist();
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	/**
	 * Get the sum of the blunt resistance of the inventory.
	 * @return (double) - blunt resistance
	 */
	public double getBluntResist() {
		int i = 0;
		double retResist = 0;
		while (i < 5) {
			retResist += armorSlots[i].getBluntResist()*armorSlots[i].getResist();
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	/**
	 * Get the sum of the piercing resistance of the inventory.
	 * @return (double) - piercing resistance
	 */
	public double getPierceResist() {
		int i = 0;
		double retResist = 0;
		while (i < 5) {
			retResist += armorSlots[i].getPierceResist()*armorSlots[i].getResist();
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	/**
	 * Get the total dodge modifier of the inventory
	 * @return (double) - dodge
	 */
	public double getDodge() {
		int i = 0;
		double retMod = 1;
		while (i < 5) {
			if (armorSlots[i].isEnchanted()) {
			retMod *= armorSlots[i].getEnchant().getDodgeMod();}
		retMod *= armorSlots[i].getDexMod();
		i++;
		}
		if (hand.IsEnchantedConstant()) {
			retMod *= hand.getEnchant().getDodgeMod();
		}
		retMod*=race.dodgeMod;
		return extra.zeroOut(retMod);
	}
	
	/**
	 * Get the total aiming modifier of the inventory
	 * @return (double) - aim
	 */
	public double getAim() {
		int i = 0;
		double retMod = 1;
		while (i < 5) {
			if (armorSlots[i].isEnchanted()) {
			retMod *= armorSlots[i].getEnchant().getAimMod();}
			i++;
		}
		if (hand.IsEnchantedConstant()) {
			retMod *= hand.getEnchant().getAimMod();
		}
		retMod*=race.aimMod;
		return extra.zeroOut(retMod);
	}
	
	/**
	 * Get the total damage modifier of the inventory
	 * @return (double) - damage mod
	 */
	public double getDam() {
		int i = 0;
		double retMod = 1;
		while (i < 5) {
			if (armorSlots[i].isEnchanted()) {
			retMod *= armorSlots[i].getEnchant().getDamMod();}
			i++;
		}
		if (hand.IsEnchantedConstant()) {
			retMod *= hand.getEnchant().getDamMod();
		}
		retMod*=race.damMod;
		return extra.zeroOut(retMod);
	}
	
	
	/**
	 * Get the total health modifier of the inventory
	 * @return (double) - health mod
	 */
	public double getHealth() {
		int i = 0;
		double retMod = 1;
		while (i < 5) {
			if (armorSlots[i].isEnchanted()) {
			retMod *= armorSlots[i].getEnchant().getHealthMod();}
			i++;
		}
		if (hand.IsEnchantedConstant()) {
			retMod *= hand.getEnchant().getHealthMod();
		}
		retMod*=race.hpMod;
		return extra.zeroOut(retMod);
	}
	
	/**
	 * Get the total speed modifier of the inventory
	 * @return (double) - dodge mod
	 */
	public double getSpeed() {
		int i = 0;
		double retMod = 1;
		while (i < 5) {
			if (armorSlots[i].isEnchanted()) {
			retMod *= armorSlots[i].getEnchant().getSpeedMod();}
			i++;
		}
		if (hand.IsEnchantedConstant()) {
			retMod *= hand.getEnchant().getSpeedMod();
		}
		retMod*=race.speedMod;
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
			tempStr += " "+ armorSlots[i].getName() + ",\n";
			i++;
		}
		return tempStr + " and a " + hand.getName() + "\n as well as " + gold + " " + extra.choose("gold","gold pieces","pieces of gold") +".";//There are way to many plurals to account for
	}
	
	/*
	 public String nameInventory() {
		String tempStr = "a";
		String sendStr = "Visual2|Inventory|";
		int i = 0;
		while (i < 5) {
			tempStr += " "+ armorSlots[i].getName() + ",\n";
			sendStr+=armorSlots[i].getBaseName()+"|";
			sendStr+=armorSlots[i].getMaterial()+"|";
			i++;
		}
		sendStr+=hand.getBaseName()+"|";
		sendStr+=hand.getMaterial()+"|";
		Networking.send(sendStr);
		return tempStr + " and a " + hand.getName() + "\n as well as " + gold + " " + extra.choose("gold","gold pieces","pieces of gold") +".";//There are way to many plurals to account for
	}
	 */
	
	/**
	 * Swaps out a new weapon.
	 * @param newWeap (Weapon)
	 * @return old weapon (Weapon)
	 */
	public Weapon swapWeapon(Weapon newWeap) {
		extra.println("They "+extra.choose("take","pick up","claim","swap for")+" the " + newWeap.getName() + ".");
		Weapon tempWeap = hand;
		hand = newWeap;
		return tempWeap;
	}


	/**
	 * @return the gold in the inventory (int)
	 */
	public int getGold() {
		return gold;
	}


	/**
	 * @param gold the gold to set (int)
	 */
	public void setGold(int gold) {
		this.gold = gold;
	}
	
	/**
	 * Add or subtract from the inventories current gold
	 * @param net gold change (int)
	 */
	public void modGold(int gold) {
		this.gold += gold;
	}

	/*
	public void display(int i) {
		String sendStr = "Visual|Inventory|";
		for (Armor a: armorSlots) {
			a.display(1);
			sendStr+=a.getBaseName()+"|";
			sendStr+=a.getMaterial()+"|";
		}
		hand.display(1);
		sendStr+=hand.getBaseName()+"|";
		sendStr+=hand.getMaterial()+"|";
		hand.getMartialStance().display(1);
	
	extra.println( gold + " " + extra.choose("gold","gold pieces","pieces of gold") +".");
	Networking.sendStrong(sendStr);
	}*/
	
	public void display(int i) {
		
		for (Armor a: armorSlots) {
			a.display(1);
		}
		hand.display(1);
		hand.getMartialStance().display(1);
		
	
	extra.println( gold + " " + extra.choose("gold","gold pieces","pieces of gold") +".");
	
	}
	
	public void graphicalDisplay(int side) {//TODO: add them in the right layer order, or set their depth 
		Networking.sendStrong("ClearInv|"+side+"|");
		Networking.sendStrong("AddInv|"+side+"|" +race.name +"|"+race.baseMap+"|"+raceMap+"|1|");
		for (Armor a: armorSlots) {
			String str = "AddInv|"+side+"|" +a.getBaseName().replace(' ','_') +"|"+a.getBaseMap()+"|"+a.getMat().palIndex+"|";
			switch (a.getArmorType()) {
			case 0:str+= "-6|";break; //head
			case 1:str+= "-5|";break; //arms
			case 2:str+= "-3|";break; //chest
			case 3:str+= "-2|";break; //legs
			case 4:str+= "-1|";break; //feet
			}
			
			Networking.sendStrong(str);
		}
		
		Networking.sendStrong("AddInv|"+side+"|" +hand.getBaseName().replace(' ','_') +"|iron|"+hand.getMat().palIndex+"|-4|");
	}
	

	public void addGold(int add) {
		gold += add;
		gold = Math.max(gold,0);
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Race swapRace(Race newRace) {
		Race r = race;
		race = newRace;
		return r;
	}

	public double getSharpResist(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getSharpResist()*armorSlots[i].getResist())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	public double getBluntResist(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getBluntResist()*armorSlots[i].getResist())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	public double getPierceResist(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getPierceResist()*armorSlots[i].getResist())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	//TO BE USED IN COMBAT
	public double getSharp(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getSharp())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	public double getBlunt(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getBlunt())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	public double getPierce(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getPierce())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	public double getFire(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getFireMod())*mult;
			i++;
		}
		return extra.zeroOut(retResist/5);
	}
	
	public double getShock(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getShockMod())*mult;
			i++;
		}
		return extra.zeroOut(retResist/5);
	}
	
	public double getFreeze(int slot) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			retResist += (armorSlots[i].getFreezeMod())*mult;
			i++;
		}
		return extra.zeroOut(retResist/5);
	}
	
	
	public void resetArmor() {
		for (Armor a: armorSlots) {
			a.resetArmor();
		}
	}

	public void burn(double percent, int slot) {
		int i = 0;
		double mult;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
			}else {
				mult = .75;
			}
			armorSlots[i].burn(mult * percent);
			i++;
		}
	}

	public Armor[] getArmor() {
		return armorSlots;
	}

	public Item swapItem(Item i) {
		if (Armor.class.isInstance(i)) {
			Armor a = (Armor)i;
			return this.swapArmorSlot(a,a.getArmorType());
		}
		if (Weapon.class.isInstance(i)) {
			Weapon w = (Weapon)i;
			return this.swapWeapon(w);
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
		return null;
	}

	public void restoreArmor(double d) {
		for (Armor a: armorSlots) {
			a.restoreArmor(d);
		}
		
	}

}
