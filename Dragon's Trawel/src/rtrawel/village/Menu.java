package rtrawel.village;

import java.util.List;

import rtrawel.battle.Party;
import rtrawel.items.Armor;
import rtrawel.items.Consumable;
import rtrawel.items.Item;
import rtrawel.items.Weapon;
import rtrawel.items.Weapon.WeaponType;
import rtrawel.jobs.JobFactory;
import rtrawel.unit.Action;
import rtrawel.unit.MonsterData;
import rtrawel.unit.MonsterFactory;
import rtrawel.unit.RCore;
import rtrawel.unit.RMonster;
import rtrawel.unit.RPlayer;
import rtrawel.unit.RUnit;
import rtrawel.unit.RUnit.RaceType;
import trawel.core.Input;
import trawel.core.Print;
import rtrawel.unit.TargetGroup;

public class Menu implements Content {

	@Override
	public boolean go() {
		int in = 0;
		while (in != 1) {
			Party.party.displayQuick();
			Print.println("1 back");
			Print.println("2 inventory");
			Print.println("3 beast-iary");
			Print.println("4 use abilities and spells");
			in = Input.inInt(4);
			switch (in) {
			case 2:
				doInv();
				break;
			case 3: beast();break;
			case 4: spells();break;
			}
		}
		// TODO
		return false;
	}

	private void spells() {
		Print.println("Who wants to do an action?");
		RPlayer p = (RPlayer)Party.party.getUnit();
		while (true) {
			Party.party.displayQuick();
			Print.println("1 back");
			List<Action> doable = p.getOOCAbs();
			for (int i = 0; i < doable.size();i++) {
				Print.println((i+2) + " " + doable.get(i).getName() + ": " + doable.get(i).getDesc() + (doable.get(i).canCast(p) ? "" : " (locked)" ));
			}
			int in = Input.inInt( doable.size()+1);
			if (in == 1) {
				return;
			}
			doable.get(in-2).go(p,p.decideOOCTargets(doable.get(in-2)));;
			
		}
		
	}

	private void beast() {
		while (true) {
			Print.println("Type in the name of the monster you want to examine, or 'back' to leave.");
			String str = Input.inString();
			if (str.equals("back")) {
				return;
			}
			try {
				MonsterData r = MonsterFactory.getMonsterByName(str);
				if (r == null) {
					Print.println("Beast not found.");
					continue;
				}
				if (Party.party.getKillCount(str) > 0) {
					Print.println(str + " Hp: " + r.getMaxHp());
					if (Party.party.getKillCount(str) >= r.getKillsTilKnown()) {
						String sub = "";
						for (RaceType rt: new RMonster(str,0).getRaceTypes()) {
							sub+= rt.toString().toLowerCase() + ", ";
						}
						Print.println(r.getDesc() + " types: " + sub);
						if (Party.party.getKillCount(str) >= r.getKillsTilVeryKnown()) {
							Print.println("Common Drop: " + r.getDrop() + " Rare Drop: " + r.getRareDrop());
							}
					}
				}else {
					Print.println("You don't know anything about this beast yet.");
				}
			}catch (Exception e) {
				Print.println("Beast not found.");
			}
		}
		
	}

	private void doInv() {
		int in2 = 0;
		while (in2 != 1) {
			Print.println("gold: " + Party.party.gold);
			Print.println("1 back");
			Print.println("2 bag");
			Print.println("3 assign items");
			Print.println("4 de-equip");
			Print.println("5 view all");
			in2 = Input.inInt(5);
			switch (in2) {
			case 2:
				doBag();
				break;
			case 3:
				assignItems();
				break;
			case 4: deEquip();break;
			case 5: viewAll();break;
			}
		}
	}
	
	
	private void viewAll() {
		for (RUnit r: Party.party.list) {
			((RPlayer)r).display();
		}
	}

	private void deEquip() {
		Print.println("Who to de-equip?");
		RPlayer p = (RPlayer)Party.party.getUnit();
		while (true) {
			Item i = null;
			Print.println("1 back");
			Print.println("2 helm (" + (p.head == null ? "nothing" : p.head.getName())+")" );
			Print.println("3 torso (" + (p.torso == null ? "nothing" : p.torso.getName())+")" );
			Print.println("4 arms (" + (p.arms == null ? "nothing" : p.arms.getName())+")" );
			Print.println("5 pants (" + (p.pants == null ? "nothing" : p.head.getName())+")" );
			Print.println("6 feet (" + (p.feet == null ? "nothing" : p.feet.getName())+")" );
			Print.println("7 asc1 (" + (p.assec1 == null ? "nothing" : p.assec1.getName())+")" );
			Print.println("8 asc2 (" + (p.assec2 == null ? "nothing" : p.assec2.getName())+")" );
			Print.println("9 shield (" + (p.shield == null ? "nothing" : p.shield.getName())+")" );
			int in = Input.inInt(8)-1;
			if (in==0) {
				return;
			}
			if (in == 9) {
				i = p.shield;
				p.shield = null;
			}else {
			i = p.swapArmor(in);
			}
			p.cleanAbs();
			if (i != null) {
				Party.party.addItem(i.getName(),1);
			}
		}
	}

	private void assignItems() {
		Print.println("Assign items for who?");
		RPlayer wielder = (RPlayer)Party.party.getUnit();
		while (wielder.assignItems());
	}

	private void doBag() {
		Party.party.refreshItemKeys();
		if (Party.party.itemKeys.size() == 0) {
			Print.println("Your bag is empty!");
			return;
		}
		int in2 = 0;
		while (in2 != (Party.party.itemKeys.size()+1)) {
			Party.party.refreshItemKeys();
			Party.party.displayItems();
			Print.println((Party.party.itemKeys.size()+1) + " back");
			in2 = Input.inInt(Party.party.itemKeys.size()+1);
			if (in2 <= Party.party.itemKeys.size()) {
				goItem(in2);
			}
		}
	}
	private void goItem(int in2) {
		String str = Party.party.popItem(in2-1);
		Item item = RCore.getItemByName(str);
		switch (item.getItemType()) {
		case ARMOR:
			Print.println("Who wants to equip this armor?");
			RPlayer armer = (RPlayer)Party.party.getUnit();
			if (JobFactory.getJobByName(armer.getJob()).armorClasses().contains(((Armor)item).getArmorClass())) {
				item = armer.swapArmor((Armor)item);
				if (item != null) {
				Party.party.addItem(item.getName(),1);
				}
			}else {
				Party.party.addItem(item.getName(),1);
			}
			break;
		case CONSUMABLE:
			Print.println("Who wants to apply the item?");
			RUnit one = Party.party.getUnit();
			Print.println("Who wants to get the item applied to them?");
			RUnit two = Party.party.getUnit();
			((Consumable)item).getAction().go(one,new TargetGroup(two));
			break;
		case NONE:
			Print.println("This item is material-only.");
			Party.party.addItem(item.getName(),1);
			break;
		case WEAPON:
			Print.println("Who wants to equip this weapon?");
			RPlayer wielder = (RPlayer)Party.party.getUnit();
			if (JobFactory.getJobByName(wielder.getJob()).weaponTypes().contains(((Weapon)item).getWeaponType())) {
				if (((Weapon)item).getWeaponType().equals(WeaponType.SHIELD)) {
					if (wielder.shield == null) {
						Print.println("You wield the " + item.getName() + ".");
					}else {
					Print.println("You swap out your " + wielder.getWeapon().getName() + " for your " + item.getName() + ".");
					Party.party.addItem(wielder.shield.getName(),1);}
					wielder.shield = (Weapon)item;
				}else {
				Print.println("You swap out your " + wielder.getWeapon().getName() + " for your " + item.getName() + ".");
				Party.party.addItem(wielder.getWeapon().getName(),1);
				wielder.setWeapon((Weapon)item);
				}
				wielder.cleanAbs();
			}else {
				Party.party.addItem(item.getName(),1);
			}
			break;
		
		}
	}

	@Override
	public String name() {
		return "menu";
	}

}
