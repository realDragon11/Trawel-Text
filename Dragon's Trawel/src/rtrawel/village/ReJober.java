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
import trawel.extra;

public class ReJober implements Content {

	@Override
	public boolean go() {
		extra.println("Who wants to change their path in life?");
		RPlayer p = (RPlayer)Party.party.getUnit();
		List<String> classes = new ArrayList<String>();
		classes.add("cleric");
		classes.add("priest");
		classes.add("ranger");
		classes.add("warrior");
		classes.add("elementalist");
		while (true) {
			extra.println("1 back");
			for (int i = 0; i < classes.size();i++) {
				extra.println((i+2) + " " + classes.get(i));
			}
			int in = extra.inInt(classes.size()+1);
			if (in == 1) {
				return false;
			}
			in-=2;
			String str = classes.get(in);
			Job j = JobFactory.getJobByName(str);
			if (j.weaponTypes().contains(p.getWeapon().getWeaponType())) {
				
			}else {
				extra.println("To switch jobs, you must have an approriate weapon. Choose a weapon from your inventory: ");
				for (WeaponType t: j.weaponTypes()) {
					extra.print(t.toString().toLowerCase());
				}
				extra.println();
				extra.println();//space
				Party.party.refreshItemKeys();
				Party.party.displayItems();
				int in2 = extra.inInt(Party.party.itemKeys.size());
				Item i = RCore.getItemByName(Party.party.itemKeys.get(in2-1));
				if (i.getItemType().equals(ItemType.WEAPON)) {
					if (j.weaponTypes().contains(((Weapon)i).getWeaponType())){
						extra.println("You swap out your " + p.getWeapon().getName() + " for your " + i.getName() + ".");
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
			if (p.progression.jobLevel(str) == 0) {
			p.progression.addJob(str);
			}
			p.deEquipUnfitting();
		}
	}

	@Override
	public String name() {
		return "career specialist";
	}

}
