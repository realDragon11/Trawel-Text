package trawel;
import java.awt.Color;
import java.util.ArrayList;

import trawel.earts.ASpell;

/**
 * @author Brian Malone
 * 2/8/2018
 * The Class that makes the decisions for the Persons
 * entirely static
 */

public class AIClass {
	
	//should account for running out of weight, and gold/weight when selling sometimes

	//static methods
	
	/**
	 * Get a random attack from the stance.
	 * @param theStance - (Stance)
	 * @return a random attack (Attack)
	 */
	public static Attack randomAttack(Stance theStance){
		return theStance.getAttack((int)(theStance.getAttackCount()*Math.random()));
	}
	
	/**
	 * Choose an attack. No intellect level provided, so it defaults to 0.
	 * @param theStance - (Stance) the stance from which to take the attack 
	 * @return (Attack) an attack chosen by the AI 
	 */
	public static Attack chooseAttack(Stance theStance) {
		return chooseAttack(theStance, 0, null, null, null);
	}
	
	public static Attack eArtASpell(ASpell a, Person d) {
		switch (a) {
		case ELEMENTAL_BURST:
			return (new Attack(Skill.ELEMENTAL_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		case DEATH_BURST:
			return (new Attack(Skill.DEATH_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		case ARMOR_UP:
			return (new Attack(Skill.ARMOR_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		case BEFUDDLE:
			return (new Attack(Skill.ILLUSION_MAGE,(int)Math.round(Player.player.eaBox.aSpellPower), d.getBag().getRace().targetType));
		}
		throw new RuntimeException("ASpell not defined.");
	}
	
	/**
	 * Choose which attack in a stance to use. Supply with an intellect level for varying levels of 
	 * smarts
	 * @param theStance - (Stance) the stance from which to take the attack
	 * @param smarts - (int) intellect, how smart the attacker is 
	 * @return an attack (Attack)
	 */
	public static Attack chooseAttack(Stance theStance, int smarts, Combat com, Person attacker, Person defender) {
		
			int j = 1;
			ArrayList<Attack> attacks = theStance.giveList();
			
			if (!attacker.isPlayer()) {
			int times = 1;
			if (attacker.hasSkill(Skill.MAGE_POWER)) {
				times++;
				attacks.remove(0);
			}
			while (times > 0) {
			if (attacker.hasSkill(Skill.ELEMENTAL_MAGE)) {
				attacks.add(new Attack(Skill.ELEMENTAL_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			if (attacker.hasSkill(Skill.DEATH_MAGE)) {
				attacks.add(new Attack(Skill.DEATH_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			if (attacker.hasSkill(Skill.ARMOR_MAGE)) {
				attacks.add(new Attack(Skill.ARMOR_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			if (attacker.hasSkill(Skill.ILLUSION_MAGE)) {
				attacks.add(new Attack(Skill.ILLUSION_MAGE,attacker.getMageLevel(), defender.getBag().getRace().targetType));
			}
			times--;}
			if (attacker.hasSkill(Skill.GOOFFENSIVE)) {
				Material mat = attacker.getBag().getHand().getMat();
				if (attacker.hasSkill(Skill.SHIELD)){
					switch (extra.randRange(1, 2)) {
					case 1: attacks.add(new Attack("bash",1,100.0,0*mat.sharpMult,10*mat.bluntMult,0,"X` bashes Y` with the their shield!",1,"blunt").impair(attacker.getDefenderLevel(), defender.getBag().getRace().targetType,null));break;
					case 2: attacks.add(new Attack("smash",.9,90.0,0*mat.sharpMult,12*mat.bluntMult,0,"X` smashes Y` with the their shield!",1,"blunt").impair(attacker.getDefenderLevel(), defender.getBag().getRace().targetType,null));break;
					}
				}else {
					if (attacker.hasSkill(Skill.PARRY)){
						switch (extra.randRange(1, 3)) {
						case 1: attacks.add(new Attack("slice",1,90.0,10*mat.sharpMult,0*mat.bluntMult,0*mat.pierceMult,"X` slices Y` with the their parrying dagger!",0,"sharp").impair(attacker.getDefenderLevel(), defender.getBag().getRace().targetType,null));break;
						case 2: attacks.add(new Attack("dice",.8,70.0,8*mat.sharpMult,0*mat.bluntMult,0*mat.pierceMult,"X` dices Y` with the their parrying dagger!",0,"sharp").impair(attacker.getDefenderLevel(), defender.getBag().getRace().targetType,null));break;
						case 3: attacks.add(new Attack("stab",1.1,90.0,0*mat.sharpMult,0*mat.bluntMult,8*mat.pierceMult,"X` stabs at Y` with the their parrying dagger!",0,"pierce").impair(attacker.getDefenderLevel(), defender.getBag().getRace().targetType,null));break;
						}
					}
				}
			}
			if (attacker.hasSkill(Skill.KUNG_FU)) {
			switch (extra.randRange(1,3)) {
			case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(attacker.getFighterLevel(), defender.getBag().getRace().targetType,null));break;
			case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(attacker.getFighterLevel(), defender.getBag().getRace().targetType,null));break;
			case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(attacker.getFighterLevel(), defender.getBag().getRace().targetType,null));break;
			}
			}
			if (attacker.hasSkill(Skill.WAIT)) {
				attacks.add(new Attack("wait",0,20.0,0,0,0,"X` waits for a better chance!",-1,"wait"));
			}
			}else {
				if (Player.player.eaBox.aSpell1 != null) {
					attacks.add(eArtASpell(Player.player.eaBox.aSpell1,defender));
				}
				if (Player.player.eaBox.aSpell2 != null) {
					attacks.add(eArtASpell(Player.player.eaBox.aSpell2,defender));
				}
				if (Player.player.eaBox.exeTrainLevel > 0) {
					attacks.add(new Attack(Skill.EXECUTE_ATTACK,Player.player.eaBox.getExeExe(), defender.getBag().getRace().targetType));
				}
				if (Player.player.eaBox.berTrainLevel > 0) {
					switch (extra.randRange(1,3)) {
					case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(Player.player.eaBox.berTrainLevel, defender.getBag().getRace().targetType,null));break;
					case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(Player.player.eaBox.berTrainLevel, defender.getBag().getRace().targetType,null));break;
					case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(Player.player.eaBox.berTrainLevel, defender.getBag().getRace().targetType,null));break;
					}
				}
				if (Player.player.eaBox.drunkTrainLevel > 0) {
					switch (extra.randRange(1,3)) {
					case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(Player.player.eaBox.drunkTrainLevel, defender.getBag().getRace().targetType,null));break;
					case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(Player.player.eaBox.drunkTrainLevel, defender.getBag().getRace().targetType,null));break;
					case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(Player.player.eaBox.drunkTrainLevel, defender.getBag().getRace().targetType,null));break;
					}
					attacks.add(new Attack(Skill.DRUNK_DRINK,Player.player.eaBox.drunkTrainLevel, defender.getBag().getRace().targetType));
				}
				
			}
			
			
			
			if (attacker.isPlayer()) {
			extra.println("     name                hit    delay    sharp    blunt     pierce");
			for(Attack a: attacks) {
				extra.print(j + "    ");
				a.display(1);
				j++;
			}
			int numb = extra.inInt(attacks.size())-1;
			return attacks.get(numb);
		}
		return attackTest(attacks,smarts,com, attacker, defender);
	}
	
	/**
	 * Has the ai simulate a couple attacks, and choose which one is best.
	 * Basically simulates choosing each of the different attacks, and deciding which will deal the most damage.
	 * It accounts for enemy armor and weapon material, but not the potential for the enemy to kill them before they do a slower attack.
	 * @param attacks (Stance)
	 * @param rounds (int)
	 * @param com (Combat) - the combat this is taking place in (so we can access the handleattack method)
	 * @param attacker (Person) - the person who is doing the attack
	 * @param defender (Person) - the person who is defending from the attack
	 * @return the chosen attack (Attack)
	 */
	public static Attack attackTest(ArrayList<Attack> attacks,int rounds, Combat com, Person attacker, Person defender) {
		int size = attacks.size();
		int i = size-1;
		int j = 0;
		double[] damray = new double[size];
		
		extra.disablePrintSubtle();
		while (i >= 0) {
			j = 0;
			do {
				if (!attacks.get(i).isMagic()) {
				damray[i]+=100*extra.zeroOut((double)com.handleAttack(attacks.get(i),defender.getBag(),attacker.getBag(),0.05,attacker,defender));}else {
					if (attacks.get(i).getSkill() == Skill.DEATH_MAGE) {
						damray[i]+=100*extra.zeroOut(attacks.get(i).getBlunt());
						if (defender.hasSkill(Skill.LIFE_MAGE)) {
							damray[i] = 0;
						}
					}
				}
				j++;
			}while (j < rounds);
			damray[i]/= ((double)rounds*attacks.get(i).getSpeed());
			i--;
		}
		extra.enablePrintSubtle();
		j=0;
		i=0;//will now hold position of the highest one
		double highestValue = -1;
		while (j < size) {
			//extra.println(theStance.getAttack(j).getName() + " " + damray[j]);//debug
			if (damray[j] > highestValue) {
				highestValue = damray[j];
				i = j;
			}
			j++;
		}
		
		
		if (highestValue <=0) {return extra.randList(attacks);}//if they're all zero, just return a random one
		//extra.println("Chose: " + theStance.getAttack(i).getName() + " " + damray[i]);//debug
		return attacks.get(i);
	}
	
	/**
	 * Checks to see if there's any items in the inventory that cost zero gold.
	 * Any that do probably make it (nigh) impossible to win, so
	 * it discards them in favor of a level 1 item, randomly generated.
	 * Also checks to see if the total modifier for the item is very low.
	 * Returns true if an item was replaced this way.
	 * @param inv (Inventory)
	 */
	public static boolean checkCheap(Inventory inv) {
		//local vars
		int i=0;
		boolean soldSomething = false; //did we sell something yet?
		Item hold; //the item we're currently looking at
		EnchantConstant holdEnchant;
		while (i < 5) {//check to see if an armor causes any zero's or is overwhelmingly negative
			hold = inv.getArmorSlot(i);
			if (hold.getEnchant() != null) {
				holdEnchant = hold.getEnchant();
			
			if (hold.getCost() < 2 || (holdEnchant.getAimMod()*holdEnchant.getDamMod()*holdEnchant.getDodgeMod()*holdEnchant.getHealthMod()*holdEnchant.getSpeedMod()) < .5) {
			Services.sellItem((Armor)hold,inv,true);
			soldSomething = true;
			}}
			i++;
		}//end while
		hold = inv.getHand();
		if (hold.getEnchant() != null) {//if there's an enchant, check to see if the weapon causes any zero's or is overwhelmingly negative
			holdEnchant = hold.getEnchant();
		
		if (hold.getCost() < 2 || (holdEnchant.getAimMod()*holdEnchant.getDamMod()*holdEnchant.getHealthMod()*holdEnchant.getSpeedMod()) < .5) {//*holdEnchant.getDodgeMod() //not being able to dodge isn't an instant loss
		Services.sellItem((Weapon)hold,inv,true);
		soldSomething = true;
		}}
		return soldSomething;
	}
	
	/**
	 * Look over this person's equipment to make sure it doesn't render them impossible to win.
	 * @param man (Person)
	 */
	public static void checkYoSelf(Person man) {
		//extra.println(man.getName() + " starts looking over their "+bpmFunctions.choose("equipment","gear","inventory","belongings")+".");
		while (checkCheap(man.getBag()));
		//extra.println(man.getName() + " has taken stock of their "+bpmFunctions.choose("equipment","gear","inventory","belongings")+".");
		//man.displayStatsShort();
	}
	
	/**
	 * Take the inventory out of @param loot and put it, or any money gained by selling it, into @param stash
	 * @param loot (Inventory)
	 * @param stash (Inventory)
	 * @param smarts (int)
	 */
	public static void loot(Inventory loot, Inventory stash, int smarts, boolean sellStuff) {
		int i = 0;
		while (i < 5) {
			if (compareItem((Item)stash.getArmorSlot(i),(Item)loot.getArmorSlot(i),smarts,true)) {
				if (sellStuff) {
				Services.sellItem(stash.swapArmorSlot(loot.getArmorSlot(i),i),stash,false);}else {
					loot.swapArmorSlot(stash.swapArmorSlot(loot.getArmorSlot(i),i), i);
				}
			}else {
				if (sellStuff) {
				Services.sellItem(loot.getArmorSlot(i),loot,stash,false);}
			}
			
			
			if (smarts < 0) {
			Networking.charUpdate();
			String depth = null;
			switch (i) {
			case 0:depth= "-6|";break; //head
			case 1:depth= "-3|";break; //arms
			case 2:depth= "-5|";break; //chest
			case 3:depth= "-1|";break; //legs
			case 4:depth= "-2|";break; //feet
			}
			Networking.send("RemoveInv|1|" + depth);
			}
			i++;
		}
		if (compareItem((Item)stash.getHand(),(Item)loot.getHand(),smarts,true)) {
			if (sellStuff) {
			Services.sellItem(stash.swapWeapon(loot.getHand()),stash,false);}else {
				loot.swapWeapon(stash.swapWeapon(loot.getHand()));
			}
		}else {
			if (sellStuff) {
			Services.sellItem(loot.getHand(),loot,stash,false);
			}
		}
		if (smarts < 0) {
			Networking.charUpdate();
			if (Player.hasSkill(Skill.LOOTER)) {
				stash.addGold(10);
				extra.println("You take the extra coins they had stored away in their " + extra.choose("spleen","appendix","imagination","lower left thigh","no-no place","closed eyes") + ". +10 gold");
			}
			for (DrawBane db: loot.getDrawBanes()) {
				stash.addNewDrawBane(db);
			}
		}else {
			//TODO drawbane taking ai
		}
		stash.setGold(stash.getGold()+loot.getGold());
		loot.setGold(0);
	}
	
	/**
	 * Returns true if @param hasItem costs more than @param toReplace
	 * @param hasItem (Item)
	 * @param toReplace (Item)
	 * @param smarts (int)
	 * @return if you should swap items (boolean)
	 */
	public static boolean compareItem(Item hasItem, Item toReplace,int smarts, boolean autosellOn) {
		if (Armor.class.isInstance(hasItem)) {
			if (autosellOn && worseArmor((Armor)hasItem,(Armor)toReplace)) {
				if (smarts < 0) {
					extra.print("Autosold the ");
					toReplace.display(1);
				}
				return false;
			}
		}
		if (smarts < 0){
			extra.println("Use the");
			toReplace.display(1);
			extra.println("instead of your");
			hasItem.display(1);
			displayChange(hasItem,toReplace);
			return extra.yesNo();
		}
		if (!Weapon.class.isInstance(hasItem)){
		return (toReplace.getCost()>hasItem.getCost());
	}else {
		if (smarts < 2) {
			return (toReplace.getCost()>hasItem.getCost());
			}
		if (((Weapon) hasItem).highestDamage().average > ((Weapon) toReplace).highestDamage().average){
			return true;	
			}
		return false;
		}
	}
	
	public static boolean compareItem(Inventory bag, Item toReplace,int smarts, boolean autosellOn) {
		Item item = null;
		if (Armor.class.isInstance(toReplace)) {
			Armor a = (Armor)toReplace;
			item = bag.getArmorSlot(a.getArmorType());
		}else {
			if (Weapon.class.isInstance(toReplace)) {
				item = bag.getHand();
			}
		}
		return compareItem(item,toReplace,smarts,autosellOn);
	}

	private static boolean worseArmor(Armor hasItem, Armor toReplace) {
		if (toReplace.getBluntResist()*toReplace.getResist() > hasItem.getBluntResist()*hasItem.getResist() || 
				toReplace.getSharpResist()*toReplace.getResist() > hasItem.getSharpResist()*hasItem.getResist() || 
				toReplace.getPierceResist()*toReplace.getResist() > hasItem.getPierceResist()*hasItem.getResist() //|| toReplace.getDexMod() >hasItem.getDexMod()
				) 
		{
		return false;}
		//enchant compare
		if (hasItem.isEnchanted()) {
			if (toReplace.isEnchanted()) {
				EnchantConstant e = hasItem.getEnchant();
				EnchantConstant e2 = toReplace.getEnchant();
				if (e.getAimMod() < e2.getAimMod() || e.getDamMod() < e2.getDamMod() || e.getDodgeMod() < e2.getDodgeMod() || e.getHealthMod() < e2.getHealthMod() || e.getSpeedMod() < e2.getSpeedMod()) {
					return false;
				}
			}else {
				EnchantConstant e = hasItem.getEnchant();
				if (e.getAimMod() < 1 || e.getDamMod() < 1 || e.getDodgeMod() < 1 || e.getHealthMod() < 1 || e.getSpeedMod() < 1) {
					return false;
				}
			}
		}else {
			if (toReplace.isEnchanted()) {
				EnchantConstant e = toReplace.getEnchant();
				if (e.getAimMod() > 1 || e.getDamMod() > 1 || e.getDodgeMod() > 1 || e.getHealthMod() > 1 || e.getSpeedMod() > 1) {
					return false;
				}
			}//no else
		}
		
		return true;
	}

	public static void displayChange(Item hasItem, Item toReplace) {
		extra.println();
		if (Armor.class.isInstance(hasItem)) {
			Armor hasArm = (Armor) hasItem;
			Armor toArm = (Armor) toReplace;
			Networking.sendColor(Color.PINK);
			extra.println("Difference: sbp: " + extra.format2(toArm.getSharpResist()*toArm.getResist()-hasArm.getSharpResist()*hasArm.getResist()) + " " +  extra.format2(toArm.getBluntResist()*toArm.getResist()-hasArm.getBluntResist()*hasArm.getResist()) + " " + extra.format2((toArm.getPierceResist()*toArm.getResist())- hasArm.getPierceResist()*hasArm.getResist())+  " cost: " + extra.format2(toReplace.getCost() - hasItem.getCost()));//" dex: " + extra.format2(toArm.getDexMod()-hasArm.getDexMod()) +
		}
		if (Weapon.class.isInstance(hasItem)) {
			Weapon hasWeap = (Weapon)hasItem;
			Weapon toWeap = (Weapon)toReplace;
			Networking.sendColor(Color.PINK);
			extra.println("Difference: hd/ad: " + extra.format2((toWeap.highestDamage().highest-hasWeap.highestDamage().highest)) + "/" + extra.format2(toWeap.highestDamage().average-hasWeap.highestDamage().average) + " cost: " + extra.format2(toReplace.getCost() - hasItem.getCost()));
			if (((Weapon)hasItem).getEnchantHit() != null || ((Weapon)toReplace).getEnchantHit()!= null) {
				displayEnchantDiff(((Weapon)hasItem).getEnchantHit(),((Weapon)toReplace).getEnchantHit());
			}
		}
		if (hasItem.getEnchant() != null || toReplace.getEnchant()!= null) {
			displayEnchantDiff(hasItem.getEnchant(),toReplace.getEnchant());
		}
	}
	
	private static void displayEnchantDiff(EnchantConstant hasItem, EnchantConstant toReplace) {
		if (hasItem == null) {
			enchantDiff(1,toReplace.getAimMod(),"aim");
			enchantDiff(1,toReplace.getDamMod(),"damage");
			enchantDiff(1,toReplace.getDodgeMod(),"dodge");
			enchantDiff(1,toReplace.getHealthMod(),"health");
			enchantDiff(1,toReplace.getSpeedMod(),"speed");
		}else {
			if (toReplace == null) {
				enchantDiff(hasItem.getAimMod(),1,"aim");
				enchantDiff(hasItem.getDamMod(),1,"damage");
				enchantDiff(hasItem.getDodgeMod(),1,"dodge");
				enchantDiff(hasItem.getHealthMod(),1,"health");
				enchantDiff(hasItem.getSpeedMod(),1,"speed");
			}else {
				enchantDiff(hasItem.getAimMod(),toReplace.getAimMod(),"aim");
				enchantDiff(hasItem.getDamMod(),toReplace.getDamMod(),"damage");
				enchantDiff(hasItem.getDodgeMod(),toReplace.getDodgeMod(),"dodge");
				enchantDiff(hasItem.getHealthMod(),toReplace.getHealthMod(),"health");
				enchantDiff(hasItem.getSpeedMod(),toReplace.getSpeedMod(),"speed");
				//enchantDiff(hasItem,toReplace,"aim");
			}
		}
		
	}
	
	private static void displayEnchantDiff(EnchantHit hasItem, EnchantHit toReplace) {
		if (hasItem == null) {
			enchantDiff(0,toReplace.getFireMod(),"fire");
			enchantDiff(0,toReplace.getShockMod(),"shock");
			enchantDiff(0,toReplace.getFreezeMod(),"frost");
		}else {
			if (toReplace == null) {
				enchantDiff(hasItem.getFireMod(),0,"fire");
				enchantDiff(hasItem.getShockMod(),0,"shock");
				enchantDiff(hasItem.getFreezeMod(),0,"frost");
			}else {
				enchantDiff(hasItem.getFireMod(),toReplace.getFireMod(),"fire");
				enchantDiff(hasItem.getShockMod(),toReplace.getShockMod(),"shock");
				enchantDiff(hasItem.getFreezeMod(),toReplace.getFreezeMod(),"frost");
			}
		}
	}
	
	private static void enchantDiff(double has, double get, String name) {
		if (has-get != 0) {
			Networking.sendColor(Color.PINK);
			extra.println(" " +extra.format2(get-has) + " " + name);
		}
	}
	
	/*
	public static void displayDifference(Item hasItem, Item toReplace) {
		extra.println("Difference: " +(hasItem));
	}*/


}
