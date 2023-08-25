package trawel;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.mainGame.DispAttack;
import trawel.battle.Combat;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.body.Race;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.magic.EnchantConstant;
import trawel.personal.item.magic.EnchantHit;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Player;
import trawel.towns.World;
import trawel.towns.services.Store;

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
		return theStance.getAttack(extra.getRand().nextInt(theStance.getAttackCount()));
	}
	/*
	@Deprecated
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
	}*/
	/*
	/**
	 * Choose which attack in a stance to use. Supply with an intellect level for varying levels of 
	 * smarts
	 * @param theStance - (Stance) the stance from which to take the attack
	 * @param smarts - (int) intellect, how smart the attacker is 
	 * @return an attack (Attack)
	 /
	@Deprecated
	public static Attack chooseAttack(Stance theStance, int smarts, Combat com, Person attacker, Person defender) {
		
			int j = 1;
			List<Attack> attacks = theStance.giveList();
			
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
					case 1: attacks.add(new Attack("bash",1,100.0,0*mat.sharpMult,10*mat.bluntMult,0,"X` bashes Y` with the their shield!",1,"blunt").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
					case 2: attacks.add(new Attack("smash",.9,90.0,0*mat.sharpMult,12*mat.bluntMult,0,"X` smashes Y` with the their shield!",1,"blunt").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
					}
				}else {
					if (attacker.hasSkill(Skill.PARRY)){
						switch (extra.randRange(1, 3)) {
						case 1: attacks.add(new Attack("slice",1,90.0,10*mat.sharpMult,0*mat.bluntMult,0*mat.pierceMult,"X` slices Y` with the their parrying dagger!",0,"sharp").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
						case 2: attacks.add(new Attack("dice",.8,70.0,8*mat.sharpMult,0*mat.bluntMult,0*mat.pierceMult,"X` dices Y` with the their parrying dagger!",0,"sharp").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
						case 3: attacks.add(new Attack("stab",1.1,90.0,0*mat.sharpMult,0*mat.bluntMult,8*mat.pierceMult,"X` stabs at Y` with the their parrying dagger!",0,"pierce").impair(attacker.getDefenderLevel(), defender,null,attacker));break;
						}
					}
				}
			}
			if (attacker.hasSkill(Skill.KUNG_FU)) {
			switch (extra.randRange(1,3)) {
			case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(attacker.getFighterLevel(), defender,null,attacker));break;
			case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(attacker.getFighterLevel(), defender,null,attacker));break;
			case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(attacker.getFighterLevel(), defender,null,attacker));break;
			}
			}
			if (attacker.hasSkill(Skill.WAIT)) {
				attacks.add(new Attack("wait",0,20.0,0,0,0,"X` waits for a better chance!",-1,"wait"));
			}
			}else {
				if (Player.player.eaBox.berTrainLevel > 0) {
					attacks = theStance.giveList();
					switch (extra.randRange(1,3)) {
					case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(Player.player.eaBox.berTrainLevel, defender,null,attacker));break;
					case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(Player.player.eaBox.berTrainLevel, defender,null,attacker));break;
					case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(Player.player.eaBox.berTrainLevel, defender,null,attacker));break;
					}
				}
				if (Player.player.eaBox.aSpell1 != null) {
					attacks.add(eArtASpell(Player.player.eaBox.aSpell1,defender));
				}
				if (Player.player.eaBox.aSpell2 != null) {
					attacks.add(eArtASpell(Player.player.eaBox.aSpell2,defender));
				}
				if (Player.player.eaBox.exeTrainLevel > 0) {
					attacks.add(new Attack(Skill.EXECUTE_ATTACK,Player.player.eaBox.getExeExe(), defender.getBag().getRace().targetType));
				}
				
				if (Player.player.eaBox.drunkTrainLevel > 0) {
					switch (extra.randRange(1,3)) {
					case 1: attacks.add(new Attack("kick",1,100.0,0,10,0,"X` kicks Y` with the their feet!",1,"blunt").impair(Player.player.eaBox.drunkTrainLevel, defender,null,attacker));break;
					case 2: attacks.add(new Attack("punch",.9,90.0,0,12,0,"X` punches Y` with the their fist!",0,"blunt").impair(Player.player.eaBox.drunkTrainLevel, defender,null,attacker));break;
					case 3: attacks.add(new Attack("bite",.8,120.0,1,4,5,"X` bites Y` with the their teeth!",1,"bite").impair(Player.player.eaBox.drunkTrainLevel, defender,null,attacker));break;
					}
					attacks.add(new Attack(Skill.DRUNK_DRINK,Player.player.eaBox.drunkTrainLevel, defender.getBag().getRace().targetType));
				}
				
				if (Player.player.eaBox.huntTrainLevel > 0) {
					attacks.add(new Attack("wait",0,20.0,0,0,0,"X` waits for a better chance!",-1,"wait"));
					attacks.add(new Attack(Skill.MARK_ATTACK,Player.player.eaBox.huntTrainLevel, defender.getBag().getRace().targetType));
				}
				if (Player.player.eaBox.bloodTrainLevel > 0) {
					attacks.add(new Attack(Skill.BLOOD_SURGE,Player.player.eaBox.bloodTrainLevel, defender.getBag().getRace().targetType));
					attacks.add(new Attack(Skill.BLOOD_HARVEST,Player.player.eaBox.bloodTrainLevel, defender.getBag().getRace().targetType));
				}
				

			}


			if (attacker.isPlayer()) {
				int numb = 9;
				while (numb == 9 || numb < 1) {
					if (numb == 9) {
						switch (mainGame.attackDisplayStyle) {
						case CLASSIC:
							extra.println("     name                hit    delay    sharp    blunt     pierce");
							for(Attack a: attacks) {
								extra.print(j + "    ");
								a.display(1,attacker,defender);
								j++;
							}
						case TWO_LINE1:
							extra.println("Attacks:");
							for(Attack a: attacks) {
								extra.print(j + " ");
								a.display(2,attacker,defender);
								j++;
							}
							break;
						}
						extra.println("9 debug examine");
						numb = extra.inInt(attacks.size(),true);
					}else {
						numb = -numb;//restore attack choice
					}
					if (numb == 9) {
						extra.print("You have ");
						attacker.displayHp();
						defender.displayStats();
						
						defender.displaySkills();
						defender.debug_print_status(0);
						
						defender.displayArmor();
						defender.displayHp();
						//new debug examine code
						extra.println("Press 9 to repeat attacks.");
						numb = extra.inInt(attacks.size(),true);
						if (numb != 9) {
							numb = -numb;//store attack choice
						}
					}
				}
				return attacks.get(numb-1);
			}
			return attackTest(attacks,smarts,com, attacker, defender);
	}*/
	
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
	public static ImpairedAttack attackTest(List<ImpairedAttack> attacks,int rounds, Combat com, Person attacker, Person defender) {
		int size = attacks.size();
		int i = size-1;
		int j = 0;
		double[] damray = new double[size];
		
		while (i >= 0) {
			j = 0;
			do {
				damray[i]+= com.handleAttack(false, attacks.get(i), defender.getBag(), attacker.getBag()
						,Armor.armorEffectiveness, attacker, defender).damage;
				j++;
			}while (j < rounds);
			//we don't need to divide by rounds because
			//all the attacks get the same rounds
			//so they're on even footing in that regard
			//and we only care about magnitude
			damray[i]/= (attacks.get(i).getTime());
			i--;
		}
		j=0;//used to iterate over attacks now
		i=0;//will now hold position of the highest one
		double highestValue = -1;
		while (j < size) {
			if (damray[j] > highestValue) {
				highestValue = damray[j];
				i = j;
			}
			j++;
		}
		
		if (highestValue <=0) {return extra.randList(attacks);}//if they're all zero, just return a random one
		return attacks.get(i);
	}
	
	
	
	/**
	 * Checks to see if there's any items in the inventory that cost zero gold.
	 * Any that do probably make it (nigh) impossible to win, so
	 * it discards them in favor of a level 1 item, randomly generated.
	 * Also checks to see if the total modifier for the item is very low.
	 * Returns true if an item was replaced this way.
	 * 
	 * NOTE: this function should be replaced with not handing out such items in the first place and relegated to a backup
	 * 
	 * @param inv (Inventory)
	 */
	@Deprecated
	public static boolean checkCheap(Inventory inv) {
		return false;
		//local vars
		/*
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
		if (hold != null) {
			if (hold.getEnchant() != null) {//if there's an enchant, check to see if the weapon causes any zero's or is overwhelmingly negative
				holdEnchant = hold.getEnchant();
			
			if (hold.getCost() < 2 || (holdEnchant.getAimMod()*holdEnchant.getDamMod()*holdEnchant.getHealthMod()*holdEnchant.getSpeedMod()) < .5) {//*holdEnchant.getDodgeMod() //not being able to dodge isn't an instant loss
				Services.sellItem((Weapon)hold,inv,true);
				soldSomething = true;
			}
			}
		}
		return soldSomething;
		*/
		
		//for now, this is done elsewhere again
	}
	
	/**
	 * Look over this person's equipment to make sure it doesn't render them impossible to win.
	 * @param man (Person)
	 */
	@Deprecated
	public static void checkYoSelf(Person man) {
		//extra.println(man.getName() + " starts looking over their "+bpmFunctions.choose("equipment","gear","inventory","belongings")+".");
		if (!man.isPlayer()) {
			while (checkCheap(man.getBag()));
		}
		//extra.println(man.getName() + " has taken stock of their "+bpmFunctions.choose("equipment","gear","inventory","belongings")+".");
		//man.displayStatsShort();
	}
	
	/**
	 * Take the inventory out of @param loot and put it, or any money gained by selling it, into @param stash
	 * @param loot (Inventory)
	 * @param stash (Inventory)
	 * @param aetherStuff if the items can be atom-smashed into aether
	 */
	public static void loot(Inventory loot, Inventory stash, boolean aetherStuff, Person p) {
		//FIXME: convert to ai-only version since players must use the other one now
		int i = 0;
		boolean normalLoot = loot.getRace().racialType == Race.RaceType.PERSONABLE;
		if (normalLoot && p.isPlayer() && Player.getTutorial()) {
			extra.println("You are now looting something! The first item presented will be the new item, the second, your current item, and finally, the difference will be shown. Some items may be autosold if all their visible stats are worse.");
		}
		if (normalLoot) {
			while (i < 5) {
				if (compareItem(stash.getArmorSlot(i),loot.getArmorSlot(i),true,p)) {
					if (aetherStuff) {
							//Services.sellItem(stash.swapArmorSlot(loot.getArmorSlot(i),i),stash,false);
							Services.aetherifyItem(stash.getArmorSlot(i),stash);
							extra.println("They "+extra.choose("take","pick up","claim","swap for")+" the " + loot.getArmorSlot(i).getName() + ".");
							stash.swapArmorSlot(loot.getArmorSlot(i),i);//we lose the ref to the thing we just deleted here
							loot.setArmorSlot(null,i);
						}else {
							loot.swapArmorSlot(stash.swapArmorSlot(loot.getArmorSlot(i),i), i);
						}
				}else {
					if (aetherStuff) {
						//Services.sellItem(loot.getArmorSlot(i),loot,stash,false);}
						Services.aetherifyItem(loot.getArmorSlot(i),stash);
						loot.setArmorSlot(null,i);
					}
				}


				if (p.isPlayer() && Networking.connected()) {
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
			if (compareItem(stash.getHand(),loot.getHand(),true,p)) {
				if (aetherStuff) {
						//Services.sellItem(stash.swapWeapon(loot.getHand()),stash,false);
						Services.aetherifyItem(stash.getHand(),stash);
						extra.println("They "+extra.choose("take","pick up","claim","swap for")+" the " + loot.getHand().getName() + ".");
						stash.swapWeapon(loot.getHand());//we lose the ref to the thing we just deleted here
						loot.setWeapon(null);
					}else {
						loot.swapWeapon(stash.swapWeapon(loot.getHand()));
					}
			}else {
				if (aetherStuff) {
					Services.aetherifyItem(loot.getHand(), stash);
					loot.setWeapon(null);
				}
			}
			Networking.send("RemoveInv|1|2|");
		}else {
			if (aetherStuff) {
				while (i < 5) {
					if (loot.getArmorSlot(i).canAetherLoot()) {
						Services.aetherifyItem(loot.getArmorSlot(i),stash);
						loot.setArmorSlot(null,i);
					}
					i++;
				}
				if (loot.getHand().canAetherLoot()) {
					Services.aetherifyItem(loot.getHand(), stash);
					loot.setWeapon(null);
				}
			}
		}
		if (p.isPlayer()) {
			Networking.charUpdate();
			/*if (Player.hasSkill(Skill.LOOTER) && normalLoot) {
				stash.addGold(10);
				extra.println("You take the extra coins they had stored away in their " + extra.choose("spleen","appendix","imagination","lower left thigh","no-no place","closed eyes") + ". +10 gold");
			}*/
			for (DrawBane db: loot.getDrawBanes()) {
				stash.addNewDrawBanePlayer(db);
			}
		}else {
			//TODO drawbane taking ai
		}
		if (aetherStuff) {
			int aether = loot.getAether();
			stash.addAether(aether);
			if (normalLoot || p.getFlag(PersonFlag.HAS_WEALTH)) {
				int money = loot.getGold();
				stash.addGold(money);
				loot.removeAllCurrency();
				if (!extra.getPrint()) {
					extra.println(p.getName() + " claims the " + aether + " aether"+(money > 0 ? " and " + World.currentMoneyDisplay(money) + "." : "."));
				}
			}else {
				loot.removeAether();
				if (!extra.getPrint()) {
					extra.println(p.getName() + " gains " + aether + " aether.");
				}
			}
		}
	}
	
	public static void playerLoot(Inventory loot, boolean canAtomSmash) {
		boolean normalLoot = loot.getRace().racialType == Race.RaceType.PERSONABLE;
		if (normalLoot && Player.getTutorial()) {
			extra.println("You are now looting something! The first item presented will be the new item, the second, your current item, and finally, the difference will be shown. Some items may be autosold if all their visible stats are worse.");
		}
		
		if (normalLoot) {
			for (int slot = 0; slot < 5; slot++) {
				Armor a = loot.getArmorSlot(slot);
				Item replaceArmor = playerLootCompareItem(a, canAtomSmash);
				//int slot = a.getSlot();
				if (replaceArmor != a) {
					if (replaceArmor == null) {
						extra.println("You swap for the " + a.getName() + ".");
					}else {
						if (canAtomSmash) {
							Services.aetherifyItem(replaceArmor,Player.bag);
							extra.println("You swap for the " + a.getName() + ".");
							loot.setArmorSlot(null,a.getSlot());
						}else {
							//loot.swapArmorSlot(Player.bag.swapArmorSlot(a,slot), slot);
						}
					}
				}else {
					if (canAtomSmash) {
						Services.aetherifyItem(a,Player.bag);
						loot.setArmorSlot(null,slot);
					}
				}
				if (Networking.connected()) {
					Networking.charUpdate();
					String depth = null;
					switch (a.getSlot()) {
					case 0:depth= "-6|";break; //head
					case 1:depth= "-3|";break; //arms
					case 2:depth= "-5|";break; //chest
					case 3:depth= "-1|";break; //legs
					case 4:depth= "-2|";break; //feet
					}
					Networking.send("RemoveInv|1|" + depth);
				}
			}
			Weapon weap = loot.getHand();
			Item replaceWeap = playerLootCompareItem(weap, canAtomSmash);
			if (replaceWeap == null) {
				extra.println("You swap for the " + weap.getName() + ".");
			}else {
				if (canAtomSmash) {
					Services.aetherifyItem(replaceWeap,Player.bag);
					extra.println("You swap for the " + weap.getName() + ".");
				}
			}
			Networking.send("RemoveInv|1|2|");
		}else {
			if (canAtomSmash) {
				for (int slot = 0; slot < 5; slot++) {
					Armor a = loot.getArmorSlot(slot);
					if (!a.canAetherLoot()) {
						continue;
					}
					Services.aetherifyItem(a,Player.bag);
					loot.setArmorSlot(null,slot);
				}
				if (loot.getHand().canAetherLoot()) {
					Services.aetherifyItem(loot.getHand(),Player.bag);
					loot.setWeapon(null);
				}
			}
		}
		Networking.charUpdate();
		for (DrawBane db: loot.getDrawBanes()) {
			Player.bag.addNewDrawBanePlayer(db);
		}
		loot.clearDrawBanes();
		if (canAtomSmash) {
			int aether = loot.getAether();
			Player.bag.addAether(aether);
			int money = loot.getGold();
			if (loot.owner != null && loot.owner.getSuper() != null) {
				Player.player.takeGold(loot.owner.getSuper());
				extra.println("You claim the " + aether + " aether"+(money > 0 ? " and " + World.currentMoneyDisplay(money) + "." : ".") +" And also any other world currency.");
				loot.removeAllCurrency();
			}else {
				if (money > 0) {
					Player.bag.addGold(money);
					loot.removeAllCurrency();
					extra.println("You claim the " + aether + " aether"+(money > 0 ? " and " + World.currentMoneyDisplay(money) + "." : "."));
				}else {
					loot.removeAether();
					extra.println("You gain " + aether + " aether.");
				}
			}
		}
	}
	
	/**
	 * Returns true if they want to replace it
	 * @param hasItem (Item)
	 * @param toReplace (Item)
	 * @return if you should swap items (boolean)
	 */
	public static boolean compareItem(Item hasItem, Item toReplace, boolean autosell, Person p) {
		//p is the person comparing it, and is used to apply skills and feats that modify stats
		//for the player, the base stats should be the same, but the 'diff' should show the actual difference
		//when swapped
		autosell = p.getFlag(PersonFlag.AUTOLOOT) && autosell;
		if (Armor.class.isInstance(hasItem)) {
			if (autosell && worseArmor((Armor)hasItem,(Armor)toReplace)) {
				if (p.isPlayer()) {
					extra.print(extra.PRE_YELLOW+"Automelted the ");
					toReplace.display(1);
					Networking.waitIfConnected(100L);
				}
				return false;
			}
		}
		if (p.isPlayer()){
			extra.println("Use the");
			toReplace.display(1);
			extra.println("instead of your");
			hasItem.display(1);
			displayChange(hasItem,toReplace, p);
			return extra.yesNo();
		}
		if (!Weapon.class.isInstance(hasItem)){
		return (toReplace.getAetherValue()>hasItem.getAetherValue());
	}else {
		if (!p.getFlag(PersonFlag.SMART_COMPARE)) {
			return (toReplace.getAetherValue()>hasItem.getAetherValue());
			}
		if (((Weapon)(toReplace)).scoreWeight() > ((Weapon)(hasItem)).scoreWeight()){
			return true;	
			}
		return false;
		}
	}
	
	public static boolean compareItem(Item current, Item next, Person p, Store s) {
		assert !p.isPlayer();
		return compareItem(current,next,false,p);
	}
	public static Item playerLootCompareItem(Item next,boolean canPouch) {
		return askDoSwap(next,null,canPouch);
	}
	
	public static Item storeBuyCompareItem(Item next, Store store) {
		Item swap = askDoSwap(next,store,true);
		if (swap == next) {
			return next;
		}
		int delta = store.getDelta(swap,next,Player.player);
		if (Player.player.getTotalBuyPower(store.aetherPerMoney(Player.player.getPerson()))+delta < 0) {
			//should not occur, this is a failsafe
			throw new RuntimeException("You can't afford this item!");
		}
		if (delta < 0) {
			int beforeMoney = Player.player.getGold();
			int beforeAether = Player.bag.getAether();
			Player.player.buyMoneyAmountRateInt(-delta,store.aetherPerMoney(Player.player.getPerson()));
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
		
		return swap;
	}
	/**
	 * allows player to move their pouch items around. will return what the item is being replaced with
	 * <br>
	 * NOTE: stores are allowed to 'unpouch' the item if the replacement doesn't let the player afford it
	 * <br>
	 * <br>
	 * if they reject the item, it will return 'next'.
	 */
	public static Item askDoSwap(Item thinking, Store store, boolean allowedNotGiveBack) {
		Item[] ret = new Item[1];
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				Item current = Player.bag.itemCounterpart(thinking);
				//FIXME: this is technically working menugenerator behavior, but it's icky
				boolean canSwap = true;
				if (store != null) {
					int delta = store.getDelta(current,thinking,Player.player);
					extra.println("Buy the");
					thinking.display(store,true,3);
					extra.println("replacing your");
					current.display(store,false,3);
					displayChange(current,thinking,Player.player.getPerson(),store);
					int buyPower = Player.player.getTotalBuyPower(store.aetherPerMoney(Player.player.getPerson()));
					extra.println("Buy Value Needed: " +-delta + "/"+buyPower);
					if (buyPower < delta) {
						canSwap = false;
					}
				}else {
					extra.println("Use the");
					thinking.display(1);
					extra.println("instead of your");
					current.display(1);
					displayChange(current,thinking, Player.player.getPerson());
				}
				
				final boolean fCanSwap = canSwap;
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Take " + thinking.getName()+".";
					}

					@Override
					public boolean go() {
						if (!fCanSwap) {
							extra.println("You cannot afford that trade.");
							return false;
						}
						ret[0] = current;
						Player.bag.swapItem(thinking);
						return true;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Examine " + thinking.getName() +".";
					}

					@Override
					public boolean go() {
						if (store == null) {
							thinking.display(4);
							extra.inputContinue();
							return false;
						}
						thinking.display(store,true,5);
						extra.inputContinue();
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Examine " + current.getName() +".";
					}

					@Override
					public boolean go() {
						if (store == null) {
							current.display(4);
							extra.inputContinue();
							return false;
						}
						current.display(store,false,5);
						extra.inputContinue();
						return false;
					}});
				if (allowedNotGiveBack) {
					if (Player.player.canAddPouch()) {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Put in bag.";
							}

							@Override
							public boolean go() {
								if (!fCanSwap) {
									extra.println("You cannot afford that trade.");
									return false;
								}
								ret[0] = null;
								return Player.player.addPouch(thinking);
							}});
						list.add(new MenuSelect() {
							//1+2+3+2+1 limit so should be exact
							@Override
							public String title() {
								return "Take and Put "+current.getName()+" in Bag.";
							}

							@Override
							public boolean go() {
								if (!fCanSwap) {
									extra.println("You cannot afford that trade.");
									return false;
								}
								ret[0] = null;
								Player.bag.swapItem(thinking);
								return Player.player.addPouch(current);
							}});
					}else {
						if (Player.player.getPouchesAgainst(thinking).size() == 0) {
							//FIXME: allow discarding other items if can't fit
						}
					}
					
				}
				//MAYBELATER: with shops, let you swap out any item, not just 'against' items
				list.addAll(Player.player.getPouchesAgainst(thinking));
				list.add(new MenuBack() {

					@Override
					public String title() {
						return "Reject " + thinking.getName() +".";
					}

					@Override
					public boolean go() {
						ret[0] = thinking;
						return true;
					}});
				return list;
			}});
		return ret[0];
	}
	public static boolean compareItem(Inventory bag, Item toReplace, boolean autosellOn, Person p) {
		Item item = null;
		if (Armor.class.isInstance(toReplace)) {
			Armor a = (Armor)toReplace;
			item = bag.getArmorSlot(a.getArmorType());
		}else {
			if (Weapon.class.isInstance(toReplace)) {
				item = bag.getHand();
			}
		}
		return compareItem(item,toReplace,autosellOn, p);
	}

	private static boolean worseArmor(Armor hasItem, Armor toReplace) {
		if (toReplace.getBluntResist() > hasItem.getBluntResist() || 
				toReplace.getSharpResist() > hasItem.getSharpResist() || 
				toReplace.getPierceResist() > hasItem.getPierceResist() //|| toReplace.getDexMod() >hasItem.getDexMod()
				) 
		{
		return false;}
		//enchant compare
		if (hasItem.isEnchanted()) {
			if (toReplace.isEnchanted()) {
				Enchant e = hasItem.getEnchant();
				Enchant e2 = toReplace.getEnchant();
				if (e.getAimMod() < e2.getAimMod() || e.getDamMod() < e2.getDamMod() || e.getDodgeMod() < e2.getDodgeMod() || e.getHealthMod() < e2.getHealthMod() || e.getSpeedMod() < e2.getSpeedMod()) {
					return false;
				}
			}else {
				Enchant e = hasItem.getEnchant();
				if (e.getAimMod() < 1 || e.getDamMod() < 1 || e.getDodgeMod() < 1 || e.getHealthMod() < 1 || e.getSpeedMod() < 1) {
					return false;
				}
			}
		}else {
			if (toReplace.isEnchanted()) {
				Enchant e = toReplace.getEnchant();
				if (e.getAimMod() > 1 || e.getDamMod() > 1 || e.getDodgeMod() > 1 || e.getHealthMod() > 1 || e.getSpeedMod() > 1) {
					return false;
				}
			}//no else
		}
		
		return true;
	}

	public static void displayChange(Item hasItem, Item toReplace, Person p) {
		displayChange(hasItem,toReplace,p,null);
	}
	
	public static void displayChange(Item hasItem, Item toReplace, Person p, Store s) {
		//p is used to display absolute stat changes instead of just raw stats like the non-diff
		extra.println();
		int costDiff = 0;
		String costName = null;
		if (s == null) {
			costName = "aether";
			costDiff = toReplace.getAetherValue() - hasItem.getAetherValue();
		}else {
			costName = s.getTown().getIsland().getWorld().moneyString();
			costDiff = s.getDelta(hasItem,toReplace,p.getSuper());//just crash if doesn't have super, shouldn't be shopping
			//costDiff = (int) (Math.ceil(s.getMarkup()*toReplace.getMoneyValue()) - hasItem.getMoneyValue());//DOLATER match rounding across places
		}
		
		if (Armor.class.isInstance(hasItem)) {
			Armor hasArm = (Armor) hasItem;
			Armor toArm = (Armor) toReplace;
			if (Player.getTutorial()) {
				extra.println("SBP = sharp, blunt, pierce");
			}
			extra.println(extra.PRE_MAGENTA+"Difference: "
			+ extra.TIMID_BLUE+extra.CHAR_SHARP
			+" "+extra.hardColorDelta1Elide(toArm.getSharpResist(),hasArm.getSharpResist())
			+ extra.PRE_WHITE+" / "
			+ extra.TIMID_BLUE+extra.CHAR_BLUNT
			+" "+extra.hardColorDelta1Elide(toArm.getBluntResist(),hasArm.getBluntResist())
			+ extra.PRE_WHITE+" / "
			+ extra.TIMID_BLUE+extra.CHAR_PIERCE
			+" "+extra.hardColorDelta1Elide(toArm.getPierceResist(),hasArm.getPierceResist())
			//weight is an int anyway
			+ (Player.player.caresAboutCapacity() ? extra.TIMID_BLUE+ " "+extra.DISP_WEIGHT+": "+extra.softColorDelta0Reversed(toArm.getWeight(),hasArm.getWeight()) : "")
			//amp is not, but we want it to display hard anyway
			+ (Player.player.caresAboutAMP() ? extra.TIMID_BLUE+ " "+extra.DISP_AMP+": "+ extra.hardColorDelta2(toArm.getAgiPenMult(),hasArm.getAgiPenMult()) : "")
			+ " " + priceDiffDisp(costDiff,costName,s)
			);
			if (hasItem.getEnchant() != null || toReplace.getEnchant() != null) {
				displayEnchantDiff(hasItem.getEnchant(),toReplace.getEnchant());
			}
		}else {
			if (Weapon.class.isInstance(hasItem)) {
				Weapon hasWeap = (Weapon)hasItem;
				Weapon toWeap = (Weapon)toReplace;
				if (Player.getTutorial()) {
					extra.println("ic = impact chance, ad = average damage, wa = weighted average damage");
				}
				boolean isQDiff = !toWeap.equalQuals(hasWeap);
				int qualDiff = isQDiff ? toWeap.numQual()-hasWeap.numQual() : 0;
				
				extra.println(extra.PRE_MAGENTA+"Difference: ic/ad/wa: " 
				+ (extra.softColorDelta2Elide(toWeap.scoreImpact(),hasWeap.scoreImpact()))
				+ extra.PRE_WHITE+"/"
				+ (extra.hardColorDelta2Elide(toWeap.scoreAverage(),hasWeap.scoreAverage()))
				+ extra.PRE_WHITE+"/"
				+ (extra.hardColorDelta2Elide(toWeap.scoreWeight(),hasWeap.scoreWeight()))
				//if the qualities are the same, 'q=', if neither has any, do not display
				+extra.TIMID_MAGENTA
				+ (isQDiff ? " "+extra.DISP_QUALS+" "
				+ extra.colorBaseZeroTimid(qualDiff) : (toWeap.numQual() > 0 ? (" "+extra.DISP_QUALS+" =") : ""))
				+ (Player.player.caresAboutCapacity() ? " "+extra.DISP_WEIGHT+": "+extra.softColorDelta0Reversed(toWeap.getWeight(),hasWeap.getWeight()) : "")
				+ " " + priceDiffDisp(costDiff,costName,s)
				);
				if (((Weapon)hasItem).getEnchant() != null || ((Weapon)toReplace).getEnchant()!= null) {
					displayEnchantDiff(((Weapon)hasItem).getEnchant(),((Weapon)toReplace).getEnchant());
				}
			}else {
				extra.println(priceDiffDisp(costDiff,costName,s));
			}
		}
		
	}
	
	/**
	 * this method works differently if given a Store or not.
	 * <br>
	 * if given a store, the delta should be provided from its delta function
	 * <br>
	 * otherwise, do (item wanting to take) - (item that they have)
	 * @param delta
	 * @param name
	 * @param s
	 * @return
	 */
	public static String priceDiffDisp(int delta,String name, Store s) {
		if (s == null) {
			if (name == "aether") {
				name = extra.DISP_AETHER;
			}
			return extra.TIMID_MAGENTA+name+": " + (delta != 0 ? extra.colorBaseZeroTimid(delta) : "=");
		}
		if (delta < 0) {//costs less, might be gaining money
			return extra.TIMID_BLUE + "requires " +  Math.abs(delta) + " buy value";
		}else {//costs more, losing money
			return extra.TIMID_GREY + "will return " +delta + " " + name;
		}
	}
	
	
	private static void displayEnchantDiff(Enchant hasItem, Enchant toReplace) {
		if (hasItem == null) {
			enchantDiff(1,toReplace.getAimMod(),"aim");
			enchantDiff(1,toReplace.getDamMod(),"damage");
			enchantDiff(1,toReplace.getDodgeMod(),"dodge");
			enchantDiff(1,toReplace.getHealthMod(),"health");
			enchantDiff(1,toReplace.getSpeedMod(),"speed");
			enchantDiff(0,toReplace.getFireMod(),"fire");
			enchantDiff(0,toReplace.getShockMod(),"shock");
			enchantDiff(0,toReplace.getFreezeMod(),"frost");
		}else {
			if (toReplace == null) {
				enchantDiff(hasItem.getAimMod(),1,"aim");
				enchantDiff(hasItem.getDamMod(),1,"damage");
				enchantDiff(hasItem.getDodgeMod(),1,"dodge");
				enchantDiff(hasItem.getHealthMod(),1,"health");
				enchantDiff(hasItem.getSpeedMod(),1,"speed");
				enchantDiff(hasItem.getFireMod(),0,"fire");
				enchantDiff(hasItem.getShockMod(),0,"shock");
				enchantDiff(hasItem.getFreezeMod(),0,"frost");
			}else {
				enchantDiff(hasItem.getAimMod(),toReplace.getAimMod(),"aim");
				enchantDiff(hasItem.getDamMod(),toReplace.getDamMod(),"damage");
				enchantDiff(hasItem.getDodgeMod(),toReplace.getDodgeMod(),"dodge");
				enchantDiff(hasItem.getHealthMod(),toReplace.getHealthMod(),"health");
				enchantDiff(hasItem.getSpeedMod(),toReplace.getSpeedMod(),"speed");
				enchantDiff(hasItem.getFireMod(),toReplace.getFireMod(),"fire");
				enchantDiff(hasItem.getShockMod(),toReplace.getShockMod(),"shock");
				enchantDiff(hasItem.getFreezeMod(),toReplace.getFreezeMod(),"frost");
				//enchantDiff(hasItem,toReplace,"aim");
			}
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
	
	private static void enchantDiff(float has, float get, String name) {
		if (has-get != 0) {
			extra.println(" " +extra.hardColorDelta2(get,has) + " " + name + " mult");
		}
	}
	
	public static ImpairedAttack chooseAttack(List<ImpairedAttack> attacks,Combat combat, Person attacker, Person defender) {
		if (attacker.isPlayer()) {
			List<Skill> tactics = Player.player.listOfTactics();
			int numb = 9;
			while (numb == 9 || numb < 1) {
				if (numb == 9) {
					int j = 1;
					switch (mainGame.attackDisplayStyle) {
					case CLASSIC:
						if (combat.turns > 0) {
							extra.println(" ");
							extra.println(attacker.inlineHPColor()+"You are "+attacker.getNameNoTitle()+".");
							extra.println(
									combat.prettyHPIndex(Player.lastAttackStringer)
									);
						}
						extra.println("     name                hit    delay    sharp    blunt     pierce");
						for(ImpairedAttack a: attacks) {
							extra.print(j + "    ");
							a.display(1);
							j++;
						}
						break;
					case TWO_LINE1_WITH_KEY:
					case TWO_LINE1:
						if (combat.turns > 0) {
							extra.println(" ");
							extra.println(attacker.inlineHPColor()+"You are "+attacker.getNameNoTitle()+".");
							extra.println(
									combat.prettyHPIndex(Player.lastAttackStringer)
									);
						}
						if (mainGame.attackDisplayStyle == DispAttack.TWO_LINE1_WITH_KEY) {
							extra.println("Attacks: " + extra.CHAR_HITCHANCE + " hitmult; " +extra.CHAR_INSTANTS+" warmup -cooldown; "+
									ImpairedAttack.EXPLAIN_DAMAGE_TYPES());
						}else {
							extra.println("Attacks:");
						}
						for(ImpairedAttack a: attacks) {
							extra.print(j + " ");
							a.display(2);
							j++;
						}
						break;
					}
					if (tactics.size() > 0) {
						extra.println(j++ + " tactics");
					}
					extra.println("9 full examine");
					numb = extra.inInt(attacks.size(),true);
				}else {
					numb = -numb;//restore attack choice
				}
				if (numb == 9) {
					extra.print("You have ");
					attacker.displayHp();
					defender.displayStats();
					
					defender.displaySkills();
					defender.debug_print_status(0);
					
					defender.displayArmor();
					defender.displayHp();
					//new debug examine code
					extra.println("9 to repeat attacks.");
					numb = extra.inInt(attacks.size(),true);
					if (numb != 9) {
						numb = -numb;//store attack choice
					}
				}else {
					if (numb > attacks.size()) {//if past the list, which is already one behind, go to tactics screen
						assert tactics.size() > 0;
						ImpairedAttack[] tacticPick = new ImpairedAttack[1];
						extra.menuGo(new ScrollMenuGenerator(tactics.size(),"last <> tactics","next <> tactics") {

							@Override
							public List<MenuItem> forSlot(int i) {
								List<MenuItem> list = new ArrayList<MenuItem>();
								final Skill skill = tactics.get(i);
								final Attack a = WeaponAttackFactory.getTactic(skill);
								list.add(new MenuSelect() {

									@Override
									public String title() {
										//TODO: perhaps standardize display
										return a.getName() +", delay of "+a.getSpeed()+": " +a.getDesc();
									}

									@Override
									public boolean go() {
										tacticPick[0] = a.impairTactic(attacker, defender);
										return true;
									}});
								return list;
							}

							@Override
							public List<MenuItem> header() {
								return null;
							}

							@Override
							public List<MenuItem> footer() {
								List<MenuItem> list = new ArrayList<MenuItem>();
								list.add(new MenuBack("back"));
								return list;
							}});
						if (tacticPick[0] == null) {
							numb = 9;//redisplay
						}else {
							return tacticPick[0];
						}
					}
				}
			}
			
			return attacks.get(numb-1);
		}
		return AIClass.attackTest(attacks, 4, combat, attacker, defender);
	}

	/**
	 * find an item you can't sell
	 */
	public static void findItem(Item found, boolean autosell, Person person) {
		Item current = person.getBag().itemCounterpart(found);
		if (AIClass.compareItem(current,found,autosell,person)) {
			Item ret = person.getBag().swapItem(found);
			if (ret != null) {
				Services.aetherifyItem(ret,person.getBag());
			}
			if (person.isPlayer()) {
				Networking.charUpdate();
			}
		}else {
			Services.aetherifyItem(found,person.getBag());
		}
	}


}
