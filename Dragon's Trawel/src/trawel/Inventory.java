package trawel;
import java.util.ArrayList;
import java.util.List;

import trawel.Weapon.WeaponQual;
import trawel.quests.Quest.TriggerType;

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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//instance vars
	private int gold = 0;
	private Armor[] armorSlots = new Armor[5];
	private Weapon hand;
	private Race race;
	private String raceMap;
	private ArrayList<DrawBane> dbs = new ArrayList<DrawBane>();
	private ArrayList<Seed> seeds = new ArrayList<Seed>();
	public int dbMax = 3;
	public Person owner;
	
	//constructors
	/**
	 * Create a new inventory, populated with items of level level.
	 * @param level (int)
	 * @param matType 
	 */
	public Inventory(int level, Race.RaceType type, Material matType,Person.AIJob job) {
		boolean isDummy = false;
		if (level == -1) {
			isDummy = true;
			level = 10;
		}
		if (type == Race.RaceType.HUMANOID) {
			if (job != null) {
				String matType2 = job.amatType[extra.randRange(0,job.amatType.length-1)];
				armorSlots[0] = new Armor(level,0,MaterialFactory.randMatByType(matType2),matType2);
				matType2 = job.amatType[extra.randRange(0,job.amatType.length-1)];
				armorSlots[1] = new Armor(level,1,MaterialFactory.randMatByType(matType2),matType2);
				matType2 = job.amatType[extra.randRange(0,job.amatType.length-1)];
				armorSlots[2] = new Armor(level,2,MaterialFactory.randMatByType(matType2),matType2);
				matType2 = job.amatType[extra.randRange(0,job.amatType.length-1)];
				armorSlots[3] = new Armor(level,3,MaterialFactory.randMatByType(matType2),matType2);
				matType2 = job.amatType[extra.randRange(0,job.amatType.length-1)];
				armorSlots[4] = new Armor(level,4,MaterialFactory.randMatByType(matType2),matType2);
				hand = Weapon.genMidWeapon(level,job.weapType[extra.randRange(0,job.weapType.length-1)]);
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
					hand = new Weapon(level);
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
			hand = new Weapon(level);}
		race = RaceFactory.randRace(type);
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
		if (armorSlots[i].getDexMod() < 1.0) {
			//heavy armor
			if ((owner != null ? owner.heavyArmorLevel : 0 ) > 20.0) {
				//do nothing, negate heavy armor penalty
			}else {
				retMod *= extra.lerp((float)armorSlots[i].getDexMod(),1,(owner != null ? owner.heavyArmorLevel : 0 )/20.0f);
			}
		}else {
			//light armor
			retMod*= Math.pow(armorSlots[i].getDexMod(),Math.log10(10+((owner != null ? owner.lightArmorLevel : 0 )*2.5f)));
		}
		
		i++;
		}
		if (hand.IsEnchantedConstant()) {
			retMod *= hand.getEnchant().getDodgeMod();
		}
		retMod*=race.dodgeMod;
		
		if ((owner != null ? owner.hasEffect(Effect.BEE_SHROUD) : false )) {
			retMod*=1.1;
		}
		
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

	
	public void display(int i) {
		
		for (Armor a: armorSlots) {
			a.display(1);
		}
		hand.display(1);
		hand.getMartialStance().display(1);
		
	
	extra.println( gold + " " + extra.choose("gold","gold pieces","pieces of gold") +".");
	
	}
	
	public void graphicalDisplay(int side, Person p) { 
		Networking.sendStrong("RaceFlag|"+side+"|"+p.getRaceFlag().name()+"|");
		Networking.sendStrong("RaceInv|"+side+"|" +race.name.replace("-","_") +"|"+race.baseMap+"|"+raceMap+"|"+p.getRaceFlag().name()+ "|"+p.bloodSeed + "|" + p.getBloodCount() + "|1|");
		if (!p.getScar().equals("")) {
			Networking.sendStrong("AddInv|"+side+"|" + p.getScar() +"|iron|0|" + p.bloodSeed + "|" + p.getBloodCount()+"|0|0|");
		}
		if (race.racialType == Race.RaceType.HUMANOID) {
		for (Armor a: armorSlots) {
			String str = "AddInv|"+side+"|" +a.getBaseName().replace(' ','_') +"|"+a.getBaseMap()+"|"+a.getMat().palIndex+"|"+a.bloodSeed + "|" + a.getBloodCount() + "|" +(a.getEnchant() != null ? a.getEnchant().enchantstyle :0 )+"|";
			switch (a.getArmorType()) {
			case 0:str+= "-6|";break; //head
			case 1:str+= "-3|";break; //arms
			case 2:str+= "-5|";break; //chest
			case 3:str+= "-1|";break; //legs
			case 4:str+= "-2|";break; //feet
			}
			
			Networking.sendStrong(str);
		}
		if (p.hasSkill(Skill.SHIELD)) {
			Networking.sendStrong("AddInv|"+side+"|shield|iron|"+hand.getMat().palIndex+"|" + hand.bloodSeed + "|" + hand.getBloodCount() +"|" +(hand.getAnyEnchant() != null ? hand.getAnyEnchant().enchantstyle :0 )+"|-7|");
		}else {
		if (p.hasSkill(Skill.PARRY)) {
			Networking.sendStrong("AddInv|"+side+"|parry|iron|"+hand.getMat().palIndex+ "|" + hand.bloodSeed + "|" + hand.getBloodCount()+ "|" +(hand.getAnyEnchant() != null ? hand.getAnyEnchant().enchantstyle :0 )+ "|-4|");
		}}
		
		Networking.sendStrong("AddInv|"+side+"|" +hand.getBaseName().replace(' ','_') +"|iron|"+hand.getMat().palIndex+ "|" + hand.bloodSeed + "|" + hand.getBloodCount() +"|" +(hand.getAnyEnchant() != null ? hand.getAnyEnchant().enchantstyle :0 )+"|2|");
		}else {
			if (p.getBag().getRace().name.equals("wolf")) {
				Networking.sendStrong("AddInv|"+side+"|" +"wolf_teeth" +"|iron|"+hand.getMat().palIndex+ "|" + hand.bloodSeed + "|" + hand.getBloodCount() +"|" +(hand.getEnchant() != null ? hand.getEnchant().enchantstyle :0 )+"|-9|");
				
			}
		}
		Networking.sendStrong("ClearInv|"+side+"|");
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
	public double getSharp(int slot, List<WeaponQual> qualList) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
				if (qualList.contains(Weapon.WeaponQual.PENETRATIVE)) {
					mult-=0.25;
				}
			}else {
				mult = .75;
				if (qualList.contains(Weapon.WeaponQual.PINPOINT)) {
					mult-=0.25;
				}
			}
			retResist += (armorSlots[i].getSharp())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	
	public double getBlunt(int slot, List<WeaponQual> qualList) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
				if (qualList.contains(Weapon.WeaponQual.PENETRATIVE)) {
					mult-=0.25;
				}
			}else {
				mult = .75;
				if (qualList.contains(Weapon.WeaponQual.PINPOINT)) {
					mult-=0.25;
				}
			}
			retResist += (armorSlots[i].getBlunt())*mult;
			i++;
		}
		return extra.zeroOut(retResist);
	}
	public double getPierce(int slot, List<WeaponQual> qualList) {
		int i = 0;
		double mult;
		double retResist = 0;
		while (i < 5) {
			if (slot == i) {
				mult = 2;
				if (qualList.contains(Weapon.WeaponQual.PENETRATIVE)) {
					mult-=0.25;
				}
			}else {
				mult = .75;
				if (qualList.contains(Weapon.WeaponQual.PINPOINT)) {
					mult-=0.25;
				}
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
	
	
	public void resetArmor(int s, int b, int p) {
		for (Armor a: armorSlots) {
			a.resetArmor(s, b, p);
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

	public String getSoundType(int slot) {
		if (armorSlots[slot].getMatType() != "chainmail") {
		return armorSlots[slot].getSoundType();
		}else {
			return "mail";
		}
	}
	
	public ArrayList<DrawBane> getDrawBanes() {
		return dbs;
	}
	
	public DrawBane addNewDrawBane(DrawBane d) {
		extra.println("You found - " + d.getName() + ": " + d.getFlavor());
		if (Player.player.hasTrigger("db:"+d.name())) {
			extra.println("You have a quest for this DrawBane, discarding it will grant progress.");
		}
		this.displayDrawBanes();
		extra.println((dbMax+ 2) +" discard.");
		int in = extra.inInt((dbMax+ 2));
		if (in == (dbMax+ 2)) {
			Player.player.questTrigger(TriggerType.CLEANSE,"db:"+d.name(),15);
			return null;
		}
		dbs.add(d);
		DrawBane b = dbs.remove(in-1);
		if (Player.player.hasTrigger("db:"+b.name())) {
			Player.player.questTrigger(TriggerType.CLEANSE,"db:"+b.name(),15);
			return null;
		}
		return b;
	}
	
	/**
	 * used only for when a player presents a drawbane, and it isn't wanted
	 * @param db
	 * @paremt rejection text, use %s to replace. null permitted
	 */
	public void giveBackDrawBane(DrawBane d,String rejectText) {
		if (rejectText != null) {
			extra.println(String.format(rejectText,d.getName()));
		}
		dbs.add(d);
	}
	
	public void displayDrawBanes() {
		while (dbs.size() < dbMax) {
			dbs.add(DrawBane.NOTHING);
		}
		int i = 1;
		for (DrawBane b: dbs) {
			extra.println(i + " " + b.getName() + ": " + b.getFlavor());
			i++;
		}
		
	}
	/**
	 * selling is when it's not getting thrown away, but used
	 * @param selling
	 * @return
	 */
	public DrawBane discardDrawBanes(boolean selling) {
		this.displayDrawBanes();
		extra.println((dbMax+ 2)+" keep");
		int in = extra.inInt((dbMax+ 2) );
		if (in == (dbMax+ 2) ) {
			return null;
		}
		DrawBane b = dbs.get(in-1);
		if (!selling) {
			if (Player.player.hasTrigger("db:"+b.name())) {
				Player.player.questTrigger(TriggerType.CLEANSE,"db:"+b.name(),15);
				return null;
			}
			
			if (b == DrawBane.CLEANER) {
				this.washAll();
			}
			return null;
		}
		return dbs.remove(in-1);
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


	public int calculateDrawBaneFor(DrawBane d) {
		int i = 0;
		switch (d) {
		case SILVER:
			for (Armor a: armorSlots) {
				if (a.getMaterial().equals("silver")) {
					i++;
				}
			}
			if (hand.getMaterial().equals("silver")) {
				i++;
			}
			break;
		case GOLD:
			for (Armor a: armorSlots) {
				if (a.getMaterial().equals("gold")) {
					i++;
				}
			}
			if (hand.getMaterial().equals("gold")) {
				i++;
			}
			break;
		case BLOOD:
			int sub = 0;
			for (Armor a: armorSlots) {
				sub+= a.getBloodCount();
			}
			sub+= hand.getBloodCount();
			i+=sub/20;
			break;
		case NOTHING:
			return 3;
		case UNDERLEVELED:
			if (owner.getLevel() < 5) {
				return 5-owner.getLevel();
			}
			return 0;
		}
		
		
		while (dbs.size() < (dbMax+ 1) ) {
			dbs.add(DrawBane.NOTHING);
		}
		for (DrawBane db: dbs) {
			if (db.equals(d)) {
				i++;
			}
		}
		//TODO: make ai nothings different
		return i;
		
	}


	public Seed getSeed() {
		if (seeds.size() == 0) {
			return null;
		}
		this.displaySeeds();
		extra.println(seeds.size()+1 + " keep");
		int in = extra.inInt(seeds.size()+1);
		if (in == seeds.size()+1) {
			return null;
		}
		return seeds.remove(in-1);
		
	}


	public void displaySeeds() {
		for (int i = 0; i < seeds.size(); i++) {
			extra.println((i+1) + " " + seeds.get(i).toString().toLowerCase());
		}
		
	}


	public void addSeed(Seed e) {
		seeds.add(e);
		extra.println("You got the " + e.toString().toLowerCase() + "!");
		while (seeds.size() > 6) {
			extra.println("You have too many seeds. Choose one to remove!");
			getSeed();
		}
		
	}


	public void removeDrawBanes() {
		this.dbs.clear();
		
	}
	
	public void deEnchant() {
		for (Armor a: armorSlots) {
			a.deEnchant();
		}
		hand.deEnchant();
	}


	public int getWorth() {
		return armorSlots[0].getCost()+armorSlots[1].getCost()+armorSlots[2].getCost()+armorSlots[3].getCost()+armorSlots[4].getCost()+hand.getCost()+this.getGold();
	}
	
	public int countArmorQuality(Armor.ArmorQuality qual) {
		int count = 0;
		for (Armor a: armorSlots) {
			if (a.getQuals().contains(qual)) {
				count++;
			}
		}
		return count;
	}
	
	public void armorQualDam(int dam) {
		for (Armor a: armorSlots) {
			a.armorQualDam(dam);
		}
	}

}
