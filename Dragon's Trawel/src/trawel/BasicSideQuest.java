package trawel;

import java.util.ArrayList;

public class BasicSideQuest implements Quest{

	public QuestR giver, target;
	
	public String giverName;
	public String targetName;
	public Person targetPerson;
	public int tier;
	
	public String name, desc;
	
	public void cleanup() {
		giver.cleanup();
		target.cleanup();
	}
	
	public void fail() {
		cleanup();
	}
	
	public void complete() {
		cleanup();
	}
	
	public static BasicSideQuest getRandomSideQuest(Town loc,Inn inn) {
		BasicSideQuest q = new BasicSideQuest();
		switch (extra.randRange(1,1)) {
		case 1: //fetch quest
			q.giverName = randomLists.randomFirstName() + randomLists.randomLastName();
			q.targetName = extra.choose("totem","heirloom","keepsake","letter","key");
			q.giver = new QuestR() {

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.bag.addGold(20);
					q.complete();
					return false;
				}};
				q.giver.locationF = inn;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
			q.target = new QuestR() {

				@Override
				public String getName() {
					return q.targetName;
				}

				@Override
				public boolean go() {
					extra.println("You claim the " + q.targetName);
					q.giver.locationF.addQR(q.giver);
					q.desc = "Return the " + q.targetName;
					this.cleanup();
					return false;
				}
				
			};
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(3));
			q.target.locationT = loc;
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			q.name = q.giverName + "'s " + q.targetName;
			q.desc = "Fetch " + q.targetName + " from " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			break;
		case 2: //kill quest
			
			
			break;
		
		}
		return q;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String desc() {
		return desc;
	}

	@Override
	public void take() {
		target.locationF.addQR(target);
		
	}
}

