package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.battle.Party;
import rtrawel.items.Item;
import rtrawel.items.Item.ItemType;
import rtrawel.items.Weapon;
import rtrawel.items.Weapon.WeaponType;
import rtrawel.jobs.Job;
import rtrawel.jobs.JobFactory;
import rtrawel.unit.RCore;
import rtrawel.unit.RPlayer;
import trawel.core.Input;
import trawel.core.Print;

public class ReJober implements Content {

	@Override
	public boolean go() {
		Print.println("Who wants to change their path in life?");
		RPlayer p = (RPlayer)Party.party.getUnit();
		List<String> classes = new ArrayList<String>();
		classes.add("cleric");
		classes.add("priest");
		classes.add("ranger");
		classes.add("warrior");
		classes.add("elementalist");
		while (true) {
			Print.println("1 back");
			for (int i = 0; i < classes.size();i++) {
				Print.println((i+2) + " " + classes.get(i));
			}
			int in = Input.inInt(classes.size()+1);
			if (in == 1) {
				return false;
			}
			in-=2;
			String str = classes.get(in);
			Job j = JobFactory.getJobByName(str);
			if (j.weaponTypes().contains(p.getWeapon().getWeaponType())) {
				
			}else {
				Print.println("To switch jobs, you must have an approriate weapon. Choose a weapon from your inventory: ");
				for (WeaponType t: j.weaponTypes()) {
					Print.print(t.toString().toLowerCase() + ", ");
				}
				Print.println();
				Print.println();//space
				Party.party.refreshItemKeys();
				Party.party.displayItems();
				if (Party.party.itemKeys.size() == 0){
					continue;
				}
				int in2 = Input.inInt(Party.party.itemKeys.size());
				Item i = RCore.getItemByName(Party.party.itemKeys.get(in2-1));
				if (i.getItemType().equals(ItemType.WEAPON)) {
					if (j.weaponTypes().contains(((Weapon)i).getWeaponType())){
						Print.println("You swap out your " + p.getWeapon().getName() + " for your " + i.getName() + ".");
						Party.party.addItem(p.getWeapon().getName(),1);
						p.setWeapon((Weapon)i);
					}else {
						continue;
					}
				}else {
					continue;
				}
			}
			p.currentJob = str;
			if (p.progression.jobLevel(str) < 1) {
			p.progression.addJob(str);
			}
			p.deEquipUnfitting();
			Print.println(p.getName() + " is now a " + str + "!");
			p.cleanAbs();
		}
	}

	@Override
	public String name() {
		return "career specialist";
	}

}
