package trawel.personal;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.battle.Combat;
import trawel.battle.attacks.Attack;
import trawel.battle.attacks.ImpairedAttack;
import trawel.battle.attacks.Stance;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.mainGame;
import trawel.core.mainGame.DispAttack;
import trawel.helper.constants.TrawelChar;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.Services;
import trawel.personal.Person.PersonFlag;
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.Item;
import trawel.personal.item.body.Race;
import trawel.personal.item.magic.Enchant;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Player;
import trawel.towns.contexts.World;
import trawel.towns.features.services.Store;

/**
 * @author dragon
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
		return theStance.getAttack(Rand.getRand().nextInt(theStance.getAttackCount()));
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
	 * @param theStance - (Stance) the stance from which to  the attack
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
						, attacker, defender).damage;
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
		
		if (highestValue <=0) {return Rand.randList(attacks);}//if they're all zero, just return a random one
		return attacks.get(i);
	}
	
	/**
	 * Take the inventory out of @param loot and put it, or any money gained by selling it, into @param stash
	 * @param loot (Inventory)
	 * @param stash (Inventory)
	 * @param aetherStuff if the items can be atom-smashed into aether
	 */
	public static void loot(Inventory loot, Inventory stash, boolean aetherStuff, Person p,boolean canEverDisplay) {
		boolean display = canEverDisplay && !Print.getPrint();
		//still do graphical display quickly for the player if connected
		boolean graphicalDisplay = p.isPlayer() && Networking.connected();
		int i = 0;
		boolean normalLoot = loot.getRace().racialType == Race.RaceType.PERSONABLE && p.isPersonable();
		if (normalLoot) {
			while (i < 5) {
				if (compareItem(stash.getArmorSlot(i),loot.getArmorSlot(i),p)) {
					if (display) {
						Print.println(p.getNameNoTitle()+": Took " + loot.getArmorSlot(i).getName() + " over " + stash.getArmorSlot(i).getName()+".");
					}
					if (aetherStuff) {
						Services.aetherifyItem(stash.getArmorSlot(i),stash,display);
						stash.swapArmorSlot(loot.getArmorSlot(i),i);//we lose the ref to the thing we just deleted here
						loot.nullArmorSlot(i);
					}else {
						loot.swapArmorSlot(stash.swapArmorSlot(loot.getArmorSlot(i),i), i);
					}
					p.resetCapacity();
					if (graphicalDisplay) {//take
						Networking.charUpdate();
						String depth = null;
						switch (i) {
						case 0:depth= "head|";break; //head
						case 1:depth= "arms|";break; //arms
						case 2:depth= "chest|";break; //chest
						case 3:depth= "legs|";break; //legs
						case 4:depth= "feet|";break; //feet
						}
						Networking.send("RemoveInv|1|" + depth);
						Networking.waitIfConnected(200L);
					}
				}else {
					if (aetherStuff) {
						Services.aetherifyItem(loot.getArmorSlot(i),stash,display);
						loot.nullArmorSlot(i);
					}
					if (graphicalDisplay) {//reject
						Networking.charUpdate();
						String depth = null;
						switch (i) {
						case 0:depth= "head|";break; //head
						case 1:depth= "arms|";break; //arms
						case 2:depth= "chest|";break; //chest
						case 3:depth= "legs|";break; //legs
						case 4:depth= "feet|";break; //feet
						}
						Networking.send("RemoveInv|1|" + depth);
						//no wait time
					}
				}
				
				i++;
			}
			if (compareItem(stash.getHand(),loot.getHand(),p)) {
				if (graphicalDisplay) {
					Networking.send("RemoveInv|1|hand|");
					Networking.waitIfConnected(200L);
				}
				if (display) {
					Print.println(p.getNameNoTitle()+": Took " + loot.getHand().getName() + " over " + stash.getHand().getName()+".");
				}
				if (aetherStuff) {
					Services.aetherifyItem(stash.getHand(),stash,display);
					stash.swapWeapon(loot.getHand());//we lose the ref to the thing we just deleted here
					loot.setWeapon(null);
				}else {
					loot.swapWeapon(stash.swapWeapon(loot.getHand()));
				}
				p.resetCapacity();
			}else {
				if (graphicalDisplay) {
					Networking.send("RemoveInv|1|hand|");
					//no wait
				}
				if (aetherStuff) {
					Services.aetherifyItem(loot.getHand(), stash,display);
					loot.setWeapon(null);
				}
			}
			
		}else {
			if (aetherStuff) {
				while (i < 5) {
					if (loot.getArmorSlot(i).canAetherLoot()) {
						Services.aetherifyItem(loot.getArmorSlot(i),stash,display);
						loot.nullArmorSlot(i);
					}
					i++;
				}
				if (loot.getHand().canAetherLoot()) {
					Services.aetherifyItem(loot.getHand(), stash,display);
					loot.setWeapon(null);
				}
			}
		}
		if (p.isPlayer()) {//player must still loot drawbanes normally
			Networking.charUpdate();
			for (DrawBane db: loot.getDrawBanes()) {
				stash.addNewDrawBanePlayer(db);
			}
			Input.endBackingSegment();
		}else {
			//MAYBELATER drawbane taking ai
		}
		if (aetherStuff) {
			int aether = loot.getAether();
			stash.addAether(aether);
			if (normalLoot || p.getFlag(PersonFlag.HAS_WEALTH)) {
				int money = loot.getGold();
				stash.addGold(money);
				loot.removeAllCurrency();
				if (display) {
					Print.println(p.getName() + " claims the " + aether + " aether"+(money > 0 ? " and " + World.currentMoneyDisplay(money) + "." : "."));
				}
			}else {
				loot.removeAether();
				if (display) {
					Print.println(p.getName() + " gains " + aether + " aether.");
				}
			}
		}
	}
	/**
	 * stores the current player inventory loot so we can display only the autoloot changes that stick
	 */
	public static void playerStashOldItems() {
		Player.aLootHand = Player.bag.getHand();
		for (int i = 0; i < 5;i++) {
			Player.aLootArmors[i] = Player.bag.getArmorSlot(i);
		}
		Player.aLootAether = Player.bag.getAether();
		Player.aLootLocal = Player.bag.getGold();
	}
	/**
	 * NOTE: does not display non-local world currency changes
	 */
	public static void playerDispLootChanges() {
		if (Player.aLootHand != Player.bag.getHand()) {
			Print.println("AutoLoot: TOOK " + Player.bag.getHand().getName() + " OVER " + Player.aLootHand.getName()+".");
		}
		for (int i = 0; i < 5;i++) {
			if (Player.aLootArmors[i] != Player.bag.getArmorSlot(i)) {
				Print.println("AutoLoot: TOOK " + Player.bag.getArmorSlot(i).getName() + " OVER " + Player.aLootArmors[i].getName()+".");
			}
		}
		if (Player.aLootAether != Player.bag.getAether()) {
			Print.println("Aether: " + (Player.bag.getAether()-Player.aLootAether));
		}
		if (Player.aLootLocal != Player.bag.getGold()) {
			Print.println("Local ("+World.currentMoneyString()+"): " + (Player.bag.getGold()-Player.aLootLocal));
		}
	}
	
	public static void playerLoot(Inventory loot, boolean canAtomSmash) {
		boolean normalLoot = loot.getRace().racialType == Race.RaceType.PERSONABLE;
		Input.endBackingSegment();
		if (Player.player.getPerson().getFlag(PersonFlag.AUTOLOOT)) {
			//we can use canAtomSmash to decide to display the updates here or if they're managed by a greater
			//looting function, as with mass battles
			if (canAtomSmash) {
				playerStashOldItems();
			}
			loot(loot,Player.bag, canAtomSmash,Player.player.getPerson(), false);
			if (canAtomSmash) {
				playerDispLootChanges();
			}
			Networking.leaderboard("highest_aether", Player.bag.getAether());
			return;
		}
		
		if (normalLoot && Player.getTutorial()) {
			Print.println("You are now looting something! The first item presented will be the new item, the second, your current item, and finally, the difference will be shown. Some items may be autosold if all their visible stats are worse.");
		}
		
		if (normalLoot) {
			for (int slot = 0; slot < 5; slot++) {
				Armor a = loot.getArmorSlot(slot);
				Item replaceArmor = playerLootCompareItem(a, canAtomSmash);
				//int slot = a.getSlot();
				if (replaceArmor != a) {
					if (replaceArmor == null) {
						Print.println("You swap for the " + a.getName() + ".");
					}else {
						if (canAtomSmash) {
							Services.aetherifyItem(replaceArmor,Player.bag,true);
							Print.println("You swap for the " + a.getName() + ".");
							loot.nullArmorSlot(a.getSlot());
						}else {
							//loot.swapArmorSlot(Player.bag.swapArmorSlot(a,slot), slot);
						}
					}
				}else {
					if (canAtomSmash) {
						Services.aetherifyItem(a,Player.bag,true);
						loot.nullArmorSlot(slot);
					}
				}
				if (Networking.connected()) {
					Networking.charUpdate();
					String depth = null;
					switch (a.getSlot()) {
					case 0:depth= "head|";break; //head
					case 1:depth= "arms|";break; //arms
					case 2:depth= "chest|";break; //chest
					case 3:depth= "legs|";break; //legs
					case 4:depth= "feet|";break; //feet
					}
					Networking.send("RemoveInv|1|" + depth);
				}
			}
			Weapon weap = loot.getHand();
			Item replaceWeap = playerLootCompareItem(weap, canAtomSmash);
			if (replaceWeap != weap) {
				if (replaceWeap == null) {
					Print.println("You swap for the " + weap.getName() + ".");
				}else {
					if (canAtomSmash) {
						Services.aetherifyItem(replaceWeap,Player.bag,true);
						Print.println("You swap for the " + weap.getName() + ".");
					}
				}
			}else {
				if (canAtomSmash) {
					Services.aetherifyItem(weap,Player.bag,true);
					loot.setWeapon(null);
				}
			}
			
			Networking.send("RemoveInv|1|hand|");
		}else {
			if (canAtomSmash) {
				for (int slot = 0; slot < 5; slot++) {
					Armor a = loot.getArmorSlot(slot);
					if (!a.canAetherLoot()) {
						continue;
					}
					Services.aetherifyItem(a,Player.bag,true);
					loot.nullArmorSlot(slot);
				}
				if (loot.getHand().canAetherLoot()) {
					Services.aetherifyItem(loot.getHand(),Player.bag,true);
					loot.setWeapon(null);
				}
			}
		}
		Networking.charUpdate();
		Input.endBackingSegment();
		for (DrawBane db: loot.getDrawBanes()) {
			Player.bag.addNewDrawBanePlayer(db);
		}
		Input.endBackingSegment();
		loot.clearDrawBanes();
		if (canAtomSmash) {
			int aether = loot.getAether();
			Player.bag.addAether(aether);
			int money = loot.getGold();
			if (loot.owner != null && loot.owner.getSuper() != null) {
				Player.player.takeGold(loot.owner.getSuper());
				Print.println("You claim the " + aether + " aether"+(money > 0 ? " and " + World.currentMoneyDisplay(money) + "." : ".") +" And also any other world currency.");
				loot.removeAllCurrency();
			}else {
				if (money > 0) {
					Player.bag.addGold(money);
					loot.removeAllCurrency();
					Print.println("You claim the " + aether + " aether"+(money > 0 ? " and " + World.currentMoneyDisplay(money) + "." : "."));
				}else {
					loot.removeAether();
					Print.println("You gain " + aether + " aether.");
				}
			}
		}
		Networking.leaderboard("highest_aether", Player.bag.getAether());
	}
	
	/**
	 * Returns true if they want to replace it
	 * <br>
	 * do not call for the player unless the player is autolooting
	 * @param hasItem (Item that they have)
	 * @param toReplace (Item that we're looking at)
	 * @return if you should swap items (boolean)
	 */
	public static boolean compareItem(Item hasItem, Item toReplace, Person p) {
		//p is the person comparing it, and is used to apply skills and feats that modify stats
		//for the player, the base stats should be the same, but the 'diff' should show the actual difference
		assert hasItem.getType() == toReplace.getType();
		switch (hasItem.getType()) {
		case ARMOR:
			if (!p.getFlag(PersonFlag.SMART_COMPARE)) {
				return (toReplace.getAetherValue()>hasItem.getAetherValue());
			}
			if (((Armor)(toReplace)).fitness() > ((Armor)(hasItem)).fitness()){
				return true;	
			}
			return false;
		case RACE:
			return false;
		case WEAPON:
			if (!p.getFlag(PersonFlag.SMART_COMPARE)) {
				return (toReplace.getAetherValue()>hasItem.getAetherValue());
			}
			if (((Weapon)(toReplace)).scoreWeight() > ((Weapon)(hasItem)).scoreWeight()){
				return true;	
			}
			return false;
		}
		throw new RuntimeException("invalid item type to compare");
	}
	
	public static boolean compareItem(Item current, Item next, Person p, Store s) {
		return compareItem(current,next,p);
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
		int playerValue = Player.bag.getAether();
		if (playerValue+delta < 0) {
			//should not occur, this is a failsafe
			throw new RuntimeException("You can't afford this item!");
		}
		if (delta < 0) {
			Player.bag.addAether(delta);
			int aetherDelta = -delta;
			Print.println(TrawelColor.RESULT_PASS+"You complete the trade."
			+ (aetherDelta > 0 ? " Spent " +aetherDelta +" aether." : "")
			);
		}else {
			if (delta > 0) {//we sold something more expensive
				Player.bag.addAether(delta);
				Print.println("You complete the trade, gaining " + delta +" aether.");
			}else {//equal value
				Print.println("You complete the trade.");
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
	 * if they reject the item, it will return 'thinking'.
	 * <br>
	 * allowedNotGiveBack == true means that the function calling this must accept null and also items of different types
	 */
	public static Item askDoSwap(Item thinking, Store store, boolean allowedNotGiveBack) {
		if (thinking instanceof Race) {
			allowedNotGiveBack = false;
		}
		final boolean finalAllowedNotGiveBack = allowedNotGiveBack;
		Item[] ret = new Item[1];
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				Item current = Player.bag.itemCounterpart(thinking);
				//FIXME: this is technically working menugenerator behavior, but it's icky
				boolean canSwap = true;
				if (store != null) {
					int delta = -store.getDelta(current,thinking,Player.player);
					Print.println("Buy the");
					thinking.display(store,true,3);
					Print.println("replacing your");
					current.display(store,false,3);
					displayChange(current,thinking,Player.player.getPerson(),store);
					int buyPower = Player.bag.getAether();//Player.player.getTotalBuyPower(store.aetherPerMoney(Player.player.getPerson()));
					Print.println("Aether Needed: " +delta + "/"+buyPower);
					if (buyPower < delta) {
						canSwap = false;
					}
				}else {
					Print.println("Use the");
					thinking.display(1);
					Print.println("instead of your");
					current.display(1);
					displayChange(current,thinking, Player.player.getPerson());
				}
				Print.println("Current Capacity: " + Player.player.getPerson().capacityDesc());
				
				final boolean fCanSwap = canSwap;
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Take " + thinking.getName()+".";
					}

					@Override
					public boolean go() {
						if (!fCanSwap) {
							Print.println("You cannot afford that trade.");
							return false;
						}
						ret[0] = current;
						Player.bag.swapItem(thinking);
						Player.bag.graphicalDisplay(-1,Player.player.getPerson());
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
							Input.inputContinue();
							return false;
						}
						thinking.display(store,true,5);
						Input.inputContinue();
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
							Input.inputContinue();
							return false;
						}
						current.display(store,false,5);
						Input.inputContinue();
						return false;
					}});
				
				//hijack remaining pouch code if already in pouch
				if (Player.player.isInPouch(thinking)) {
					list.add(new MenuBack("Keep " + thinking.getName() + " in pouch.") {
						@Override
						public boolean go() {
							ret[0] = thinking;
							return true;
						}
					});
					return list;
				}
				//end hijack of if already in pouch
				if (finalAllowedNotGiveBack) {
					if (Player.player.canAddPouch()) {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Put in bag.";
							}

							@Override
							public boolean go() {
								if (!fCanSwap) {
									Print.println("You cannot afford that trade.");
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
									Print.println("You cannot afford that trade.");
									return false;
								}
								ret[0] = null;
								Player.bag.swapItem(thinking);
								return Player.player.addPouch(current);
							}});
					}else {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Manage Bag.";
							}

							@Override
							public boolean go() {
								Player.player.pouchMenu(true);
								return false;
							}});
					}
					list.addAll(Player.player.getPouchesAgainst(thinking));
					//list.addAll(Player.player.getPouchesAll());
				}else {
					//cannot use pouches if not of same type, so don't need to discard
					list.addAll(Player.player.getPouchesAgainst(thinking));
				}
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
	public static boolean compareItem(Inventory bag, Item toReplace, Person p) {
		Item item = null;
		if (Armor.class.isInstance(toReplace)) {
			Armor a = (Armor)toReplace;
			item = bag.getArmorSlot(a.getArmorType());
		}else {
			if (Weapon.class.isInstance(toReplace)) {
				item = bag.getHand();
			}
		}
		return compareItem(item,toReplace, p);
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
		Print.println(" ");
		Print.println(TrawelColor.STAT_HEADER+"Difference"+TrawelColor.PRE_WHITE+": "+TrawelColor.ITEM_DESC_PROP+"L " +TrawelColor.softColorDelta0(toReplace.getLevel(),hasItem.getLevel()));
		int costDiff = 0;
		String costName = "aether";
		/*if (s == null) {
			costName = "aether";
			costDiff = toReplace.getAetherValue() - hasItem.getAetherValue();
		}else {
			costName = s.getTown().getIsland().getWorld().moneyString();
			costDiff = s.getDelta(hasItem,toReplace,p.getSuper());//just crash if doesn't have super, shouldn't be shopping
			//costDiff = (int) (Math.ceil(s.getMarkup()*toReplace.getMoneyValue()) - hasItem.getMoneyValue());//DOLATER match rounding across places
		}*/
		if (s == null) {
			costDiff = toReplace.getAetherValue() - hasItem.getAetherValue();
		}else {
			costDiff = (int) (Math.ceil(s.getMarkup()*toReplace.getAetherValue()) - hasItem.getAetherValue());
		}
		
		if (Armor.class.isInstance(hasItem)) {
			Armor hasArm = (Armor) hasItem;
			Armor toArm = (Armor) toReplace;
			if (Player.getTutorial()) {
				Print.println("SBP = sharp, blunt, pierce");
			}
			Print.println(" "+
			TrawelColor.ITEM_DESC_PROP+TrawelChar.CHAR_SHARP
			+" "+TrawelColor.hardColorDelta1Elide(toArm.getSharpResist(),hasArm.getSharpResist())
			+ TrawelColor.PRE_WHITE+" / "
			+ TrawelColor.ITEM_DESC_PROP+TrawelChar.CHAR_BLUNT
			+" "+TrawelColor.hardColorDelta1Elide(toArm.getBluntResist(),hasArm.getBluntResist())
			+ TrawelColor.PRE_WHITE+" / "
			+ TrawelColor.ITEM_DESC_PROP+TrawelChar.CHAR_PIERCE
			+" "+TrawelColor.hardColorDelta1Elide(toArm.getPierceResist(),hasArm.getPierceResist())
			//weight is an int anyway
			+ (Player.player.caresAboutCapacity() ? TrawelColor.ITEM_DESC_PROP+ " "+TrawelChar.DISP_WEIGHT+": "+TrawelColor.softColorDelta0Reversed(toArm.getWeight(),hasArm.getWeight()) : "")
			//amp is not, but we want it to display hard anyway
			+ (Player.player.caresAboutAMP() ? TrawelColor.ITEM_DESC_PROP+ " "+TrawelChar.DISP_AMP+": "+ TrawelColor.hardColorDelta2(toArm.getAgiPenMult(),hasArm.getAgiPenMult()) : "")
			+ " " + priceDiffDisp(costDiff,costName,s)
			);
			if (hasItem.getEnchant() != null || toReplace.getEnchant() != null) {
				displayEnchantDiff(hasItem.getEnchant(),toReplace.getEnchant());
			}
			hasArm.getQuals().stream().filter(q -> !toArm.getQuals().contains(q)).forEach(q -> Print.println(" -"+q.removeText()));
			toArm.getQuals().stream().filter(q -> !hasArm.getQuals().contains(q)).forEach(q -> Print.println(" +"+q.addText()));
		}else {
			if (Weapon.class.isInstance(hasItem)) {
				Weapon hasWeap = (Weapon)hasItem;
				Weapon toWeap = (Weapon)toReplace;
				if (Player.getTutorial()) {
					Print.println("ic = impact chance, bd = best damage, wa = weighted average damage");
				}
				boolean isQDiff = !toWeap.equalQuals(hasWeap);
				int qualDiff = isQDiff ? toWeap.numQual()-hasWeap.numQual() : 0;
				
				Print.println(" "+TrawelColor.ITEM_DESC_PROP+" ic/bd/wa: " 
				+ (TrawelColor.softColorDelta2Elide(toWeap.scoreImpact(),hasWeap.scoreImpact()))
				+ TrawelColor.PRE_WHITE+"/"
				+ (TrawelColor.hardColorDelta2Elide(toWeap.scoreBest(),hasWeap.scoreBest()))
				+ TrawelColor.PRE_WHITE+"/"
				+ (TrawelColor.hardColorDelta2Elide(toWeap.scoreWeight(),hasWeap.scoreWeight()))
				//if the qualities are the same, 'q=', if neither has any, do not display
				+TrawelColor.ITEM_DESC_PROP
				+ (isQDiff ? " "+TrawelChar.DISP_QUALS+" "
				+ TrawelColor.colorBaseZeroTimid(qualDiff) : (toWeap.numQual() > 0 ? (" "+TrawelChar.DISP_QUALS+" =") : ""))
				+ (Player.player.caresAboutCapacity() ? TrawelColor.ITEM_DESC_PROP+" "+TrawelChar.DISP_WEIGHT+": "+TrawelColor.softColorDelta0Reversed(toWeap.getWeight(),hasWeap.getWeight()) : "")
				+ " " + priceDiffDisp(costDiff,costName,s)
				);
				if (((Weapon)hasItem).getEnchant() != null || ((Weapon)toReplace).getEnchant()!= null) {
					displayEnchantDiff(((Weapon)hasItem).getEnchant(),((Weapon)toReplace).getEnchant());
				}
			}else {
				Print.println(priceDiffDisp(costDiff,costName,s));
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
		if (name == "aether") {
			name = TrawelChar.DISP_AETHER;
		}
		if (s == null) {
			return TrawelColor.ITEM_VALUE+name+": " + (delta != 0 ? TrawelColor.colorBaseZeroTimid(delta) : "=");
		}
		if (delta > 0) {//costs less, might be gaining money
			return TrawelColor.ITEM_VALUE + "requires " +  Math.abs(delta) + " " +name;
		}else {//costs more, losing money
			return TrawelColor.ITEM_VALUE + "will return " +Math.abs(delta) + " " + name;
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
			if (toReplace.isKeen()) {
				Print.println(TrawelColor.PRE_GREEN+" +Keen");
			}
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
				if (hasItem.isKeen()) {
					Print.println(TrawelColor.PRE_RED+" -Keen");
				}
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
				if (hasItem.isKeen()) {
					if (!toReplace.isKeen()) {
						Print.println(TrawelColor.PRE_RED+" -Keen");
					}
				}else {
					if (toReplace.isKeen()) {
						Print.println(TrawelColor.PRE_GREEN+" +Keen");
					}
				}
			}
		}
		
	}
	
	private static void enchantDiff(float has, float get, String name) {
		if (has-get != 0) {
			Print.println(" " +TrawelColor.hardColorDelta2(get,has) + " " + name + " mult");
		}
	}
	
	public static ImpairedAttack chooseAttack(List<ImpairedAttack> attacks,Combat combat, Person attacker, Person defender) {
		if (attacker.isPlayer()) {
			if (attacker.getFlag(PersonFlag.AUTOBATTLE)) {
				ImpairedAttack att = AIClass.attackTest(attacks, 4, combat, attacker, defender);
				Input.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuBack("AI chose "+att.getName()+"."));
						//used so player can use 10 back out code instead of
						//having to mash 1 if they want
						return list;
					}});
				return att;
			}
			
			List<Skill> tactics = Player.player.listOfTactics();
			int numb = 9;
			while (numb == 9 || numb < 1) {
				if (numb == 9) {
					int j = 1;
					switch (mainGame.attackDisplayStyle) {
					case SIMPLIFIED:
						if (combat.turns > 0) {
							Print.println(" ");
							Print.println(attacker.inlineHPColor()+"You are "+attacker.getNameNoTitle()+".");
							Print.println(
									combat.prettyHPIndex(Player.lastAttackStringer)
									);
						}
						Print.println("Attacks on "+combat.prettyHPPerson("[HP]"+defender.getName(),TrawelColor.PRE_WHITE, defender)+": ");
						for(ImpairedAttack a: attacks) {
							Print.print(j + " ");
							a.display(3);
							j++;
						}
						break;
					case CLASSIC:
						if (combat.turns > 0) {
							Print.println(" ");
							Print.println(attacker.inlineHPColor()+"You are "+attacker.getNameNoTitle()+".");
							Print.println(
									combat.prettyHPIndex(Player.lastAttackStringer)
									);
						}
						Print.println("     name                hit    delay    sharp    blunt     pierce");
						for(ImpairedAttack a: attacks) {
							Print.print(j + "    ");
							a.display(1);
							j++;
						}
						break;
					case TWO_LINE1_WITH_KEY:
					case TWO_LINE1:
						if (combat.turns > 0) {
							Print.println(" ");
							Print.println(attacker.inlineHPColor()+"You are "+attacker.getNameNoTitle()+".");
							Print.println(
									combat.prettyHPIndex(Player.lastAttackStringer)
									);
						}
						if (mainGame.attackDisplayStyle == DispAttack.TWO_LINE1_WITH_KEY) {
							Print.println("Attacks on "+combat.prettyHPPerson("[HP]"+defender.getName(),TrawelColor.PRE_WHITE, defender)+": " + TrawelChar.CHAR_HITMULT + " hitmult; " +TrawelChar.CHAR_INSTANTS+" warmup cooldown; "+
									ImpairedAttack.EXPLAIN_DAMAGE_TYPES());
						}else {
							Print.println("Attacks on "+combat.prettyHPPerson("[HP]"+defender.getName(),TrawelColor.PRE_WHITE, defender)+": ");
						}
						for(ImpairedAttack a: attacks) {
							Print.print(j + " ");
							a.display(2);
							j++;
						}
						break;
					}
					if (tactics.size() > 0) {
						Print.println(j++ + " Tactics.");
					}
					Print.println("9 Examine.");
					numb = Input.inInt(j,true,false);
				}else {
					numb = -numb;//restore attack choice
				}
				if (numb == 9) {
					Input.menuGo(new MenuGenerator() {

						@Override
						public List<MenuItem> gen() {
							List<MenuItem> list = new ArrayList<MenuItem>();
							list.add(new MenuLine() {

								@Override
								public String title() {
									return attacker.getName() + " targeting " + defender.getName()+".";
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "Your HP, Armor, and Effects.";
								}

								@Override
								public boolean go() {
									Print.println(TrawelColor.STAT_HEADER+attacker.getName()+": ");
									attacker.displayHp();
									attacker.displayArmor();
									attacker.displayEffects();
									return false;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "Your body Condition.";
								}

								@Override
								public boolean go() {
									Print.println(TrawelColor.STAT_HEADER+attacker.getName()+": ");
									if (mainGame.advancedCombatDisplay) {
										attacker.debug_print_status(0);
									}
									attacker.printBodyStatus(false);
									return false;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "Target HP, Armor, and Effects.";
								}

								@Override
								public boolean go() {
									Print.println(TrawelColor.STAT_HEADER+defender.getName()+": ");
									defender.displayHp();
									defender.displayArmor();
									defender.displayEffects();
									return false;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "Target body Condition.";
								}

								@Override
								public boolean go() {
									Print.println(TrawelColor.STAT_HEADER+defender.getName()+": ");
									if (mainGame.advancedCombatDisplay) {
										defender.debug_print_status(0);
									}
									defender.printBodyStatus(false);
									return false;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return "Target Stats, Attributes, and Skills.";
								}

								@Override
								public boolean go() {
									defender.displayStatOverview(true);
									String[] attributes = defender.attributeDesc();
									for (int i = 0;i < attributes.length;i++) {
										Print.println(attributes[i]);
									}
									defender.displaySkills();
									return false;
								}});
							list.add(new MenuBack("Return to attacking."));
							return list;
						}
					});
					/*
					extra.print("You have ");
					attacker.displayHp();
					attacker.displayEffects();
					
					extra.println("---");
					
					defender.displayStats();
					defender.displayArmor();
					
					defender.displaySkills();
					defender.displayEffects();
					defender.debug_print_status(0);
					
					//new debug examine code
					extra.println("9 to repeat attacks.");
					numb = extra.inInt(attacks.size(),true,false);
					if (numb != 9) {
						numb = -numb;//store attack choice
					}*/
					//repeat attacks
					numb = 9;
				}else {
					if (numb > attacks.size()) {//if past the list, which is already one behind, go to tactics screen
						assert tactics.size() > 0;
						ImpairedAttack[] tacticPick = new ImpairedAttack[1];
						Input.menuGo(new ScrollMenuGenerator(tactics.size(),"Last <> Tactics.","Next <> Tactics.") {

							@Override
							public List<MenuItem> forSlot(int i) {
								List<MenuItem> list = new ArrayList<MenuItem>();
								final Skill skill = tactics.get(i);
								final Attack a = WeaponAttackFactory.getTactic(skill);
								list.add(new MenuSelect() {

									@Override
									public String title() {
										//TODO: perhaps standardize display
										return a.getName() +", delay of "+a.getSpeed()+": " +a.getRider().desc;
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
	 * find a single item, player only
	 */
	public static void findItem(Item found, Person person) {
		//Item current = person.getBag().itemCounterpart(found);
		Item done = askDoSwap(found,null,true);
		if (done != null) {
			Services.aetherifyItem(done,person.getBag(),true);
		}
		Networking.charUpdate();
		Networking.leaderboard("highest_aether", Player.bag.getAether());
	}


}
