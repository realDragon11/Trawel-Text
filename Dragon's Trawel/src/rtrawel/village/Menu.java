package rtrawel.village;

import rtrawel.battle.Party;
import rtrawel.items.Armor;
import rtrawel.items.Consumable;
import rtrawel.items.Item;
import rtrawel.items.Weapon;
import rtrawel.jobs.JobFactory;
import rtrawel.unit.RCore;
import rtrawel.unit.RPlayer;
import rtrawel.unit.RUnit;
import rtrawel.unit.TargetGroup;
import trawel.extra;

public class Menu implements Content {

	@Override
	public boolean go() {
		int in = 0;
		while (in != 1) {
			extra.println("1 back");
			extra.println("2 inventory");
			in = extra.inInt(2);
			switch (in) {
			case 2:
				doInv();
				break;
			}
		}
		// TODO
		return false;
	}

	private void doInv() {
		int in2 = 0;
		while (in2 != 1) {
			extra.println("gold: " + Party.party.gold);
			extra.println("1 back");
			extra.println("2 bag");
			extra.println("3 assign items");
			extra.println("4 de-equip");
			in2 = extra.inInt(4);
			switch (in2) {
			case 2:
				doBag();
				break;
			case 3:
				assignItems();
				break;
			case 4: deEquip();break;
			}
		}
	}
	
	
	private void deEquip() {
		extra.println("Who to de-equip?");
		RPlayer p = (RPlayer)Party.party.getUnit();
		while (true) {
			Item i = null;
			extra.println("1 back");
			extra.println("2 helm (" + p.head == null ? "nothing" : p.head.getName()+")" );
			extra.println("3 torso (" + p.torso == null ? "nothing" : p.torso.getName()+")" );
			extra.println("4 arms (" + p.arms == null ? "nothing" : p.arms.getName()+")" );
			extra.println("5 pants (" + p.pants == null ? "nothing" : p.head.getName()+")" );
			extra.println("6 feet (" + p.feet == null ? "nothing" : p.feet.getName()+")" );
			extra.println("7 asc1 (" + p.assec1 == null ? "nothing" : p.assec1.getName()+")" );
			extra.println("8 asc2 (" + p.assec2 == null ? "nothing" : p.assec2.getName()+")" );
			i = p.swapArmor(extra.inInt(8)-1);
			if (i != null) {
				Party.party.addItem(i.getName(),1);
			}
		}
	}

	private void assignItems() {
		extra.println("Assign items for who?");
		RPlayer wielder = (RPlayer)Party.party.getUnit();
		while (wielder.assignItems());
	}

	private void doBag() {
		if (Party.party.itemKeys.size() == 0) {
			extra.println("Your bag is empty!");
			return;
		}
		int in2 = 0;
		while (in2 != (Party.party.itemKeys.size()+1)) {
			Party.party.refreshItemKeys();
			Party.party.displayItems();
			extra.println((Party.party.itemKeys.size()+1) + " back");
			in2 = extra.inInt(Party.party.itemKeys.size()+1);
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
			extra.println("Who wants to equip this armor?");
			RPlayer armer = (RPlayer)Party.party.getUnit();
			if (JobFactory.getJobByName(armer.getJob()).armorClasses().contains(((Armor)item).getArmorClass())) {
				item = armer.swapArmor((Armor)item);
				if (item != null) {
				Party.party.addItem(item.getName(),1);
				}
			}
			break;
		case CONSUMABLE:
			extra.println("Who wants to apply the item?");
			RUnit one = Party.party.getUnit();
			extra.println("Who wants to get the item applied to them?");
			RUnit two = Party.party.getUnit();
			((Consumable)item).getAction().go(one,new TargetGroup(two));
			break;
		case NONE:
			extra.println("This item is material-only.");
			Party.party.addItem(item.getName(),1);
			break;
		case WEAPON:
			extra.println("Who wants to equip this weapon?");
			RPlayer wielder = (RPlayer)Party.party.getUnit();
			if (JobFactory.getJobByName(wielder.getJob()).weaponTypes().contains(((Weapon)item).getWeaponType())) {
				extra.println("You swap out your " + wielder.getWeapon().getName() + " for your " + item.getName() + ".");
				Party.party.addItem(wielder.getWeapon().getName(),1);
				wielder.setWeapon((Weapon)item);
				wielder.cleanAbs();
			}
			break;
		
		}
	}

	@Override
	public String name() {
		return "menu";
	}

}
