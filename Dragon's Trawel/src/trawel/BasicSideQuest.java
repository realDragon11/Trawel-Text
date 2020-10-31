package trawel;

import java.util.ArrayList;
import java.util.List;

public class BasicSideQuest implements Quest{

	public QuestR giver, target;
	
	public String giverName;
	public String targetName;
	public Person targetPerson;
	public int tier;
	public int count;
	public String trigger;
	
	public String name, desc;
	
	public void cleanup() {
		giver.cleanup();
		if (target != null) {
		target.cleanup();}
	}
	
	public void fail() {
		cleanup();
		Player.player.sideQuests.remove(this);
	}
	
	public void announceUpdate() {
		extra.println(desc);
	}
	
	public void complete() {
		cleanup();
		Player.player.sideQuests.remove(this);
	}
	
	public static BasicSideQuest getRandomSideQuest(Town loc,Inn inn) {
		BasicSideQuest q = new BasicSideQuest();
		switch (extra.randRange(1,3)) {
		case 1: //fetch quest
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
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
					q.desc = "Return the " + q.targetName + " to " + q.giverName + " at " + q.giver.locationF.getName() + " in " + q.giver.locationT.getName();
					this.cleanup();
					q.announceUpdate();
					return false;
				}
				
			};
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(3));
			q.target.locationT = q.target.locationF.town;
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			q.name = q.giverName + "'s " + q.targetName;
			q.desc = "Fetch " + q.targetName + " from " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			break;
		case 2: //kill quest
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
			q.giver = new QuestR() {

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.bag.addGold(40);
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
					extra.menuGo(new MenuGenerator() {

						@Override
						public List<MenuItem> gen() {
							List<MenuItem> mList = new ArrayList<MenuItem>();
							mList.add(new MenuSelect() {

								@Override
								public String title() {
									return "Attack " + q.targetName;
								}

								@Override
								public boolean go() {
									if (mainGame.CombatTwo(Player.player.getPerson(), q.targetPerson).equals(Player.player.getPerson())) {
										Player.player.eaBox.exeKillLevel += 1;
										q.giver.locationF.addQR(q.giver);
										q.desc = "Return to " + q.giverName + " at " + q.giver.locationF.getName() + " in " + q.giver.locationT.getName();
										cleanup();
										q.announceUpdate();
									}
									return true;
								}});
							return mList;
						}});
					
					return false;
				}
				
			};
			int i = 3; 
			while (q.target.locationF == null) {
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(i));
			i++;
			}
			q.target.locationT = q.target.locationF.town;
			q.targetPerson = new Person(q.target.locationT.getTier());
			q.targetName = q.targetPerson.getName();
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			q.name = "Kill " + q.targetName + " for " + q.giverName ;
			q.desc = "Kill " + q.targetName + " at " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			break;
		case 3: //cleanse quest
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
			q.giver = new QuestR() {

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.bag.addGold(40);
					q.complete();
					return false;
				}};
				q.giver.locationF = inn;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
				switch (extra.randRange(1,4)) {
				case 1:
					q.targetName = "bears";
					q.trigger = "bear";
					q.count = 3;
					break;
				case 2:
					q.targetName = "vampires";
					q.trigger = "vampire";
					q.count = 2;
					break;
				case 3:
					q.targetName = "wolves";
					q.trigger = "wolf";
					q.count = 6;
					break;
				case 4:
					q.targetName = "harpies";
					q.trigger = "harpy";
					q.count = 4;
					break;
				}
			//q.target.locationF.addQR(q.target);
				q.name = "Kill " + q.targetName + " for " + q.giverName ;
				q.desc = "Kill " + q.count + " more " + q.targetName + " on the roads for " + q.giverName;
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
		if (target != null) {
		target.locationF.addQR(target);}
		announceUpdate();
	}

	@Override
	public void questTrigger(String trigger, int num) {
		if (this.trigger != null) {
			if (this.trigger.equals(trigger)) {
				count-=num;
				if (count <=0) {
					giver.locationF.addQR(giver);
					desc = "Return to " + giverName + " at " + giver.locationF.getName() + " in " + giver.locationT.getName();
					this.announceUpdate();
				}else {
					desc = "Kill " + count + " more " + targetName + " on the roads for " + giverName;
				}
			}
		}
		
	}
}

