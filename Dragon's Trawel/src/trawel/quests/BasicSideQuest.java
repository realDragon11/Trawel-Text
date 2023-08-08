package trawel.quests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.factions.Faction;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory.QKey;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.fight.Slum;
import trawel.towns.fort.FortHall;
import trawel.towns.services.Inn;
import trawel.towns.services.MerchantGuild;

public class BasicSideQuest implements Quest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QuestR giver, target;
	
	public String giverName;
	public String targetName;
	public Person targetPerson;
	public int tier;
	public int count;
	public String trigger;
	
	public String name, desc;
	
	public List<QKey> qKeywords = new ArrayList<QKey>();
	
	public int reactionsLeft = 2;
	
	public boolean completed = false;
	
	public static String triggerText(TriggerType type) {
		if (type == TriggerType.CLEANSE) {
			return "db:";
		}
		return "";
	}
	
	
	public void cleanup() {
		giver.cleanup();
		if (target != null) {
		target.cleanup();}
	}
	
	@Override
	public void fail() {
		cleanup();
		Player.player.sideQuests.remove(this);
	}
	
	public void announceUpdate() {
		extra.println(desc);
	}
	
	@Override
	public void complete() {
		cleanup();
		Player.player.sideQuests.remove(this);
	}
	
	public static BasicSideQuest getRandomSideQuest(Town loc,Inn inn) {
		BasicSideQuest q = new BasicSideQuest();
		q.qKeywords.add(QKey.GIVE_INN);
		int i;
		switch (extra.randRange(1,3)) {
		case 1: //fetch quest
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
			q.targetName = extra.choose("totem","heirloom","keepsake","letter","key");
			q.qKeywords.add(QKey.FETCH);
			q.giver = new QuestR() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.player.addGold(10);
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,.1f, 0);
					q.complete();
					return false;
				}};
				q.giver.locationF = inn;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
			q.target = new QuestR() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
			i = 3; 
			while (q.target.locationF == null) {
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(i));
			i++;
			if (i > 10) {
				return null;
			}
			}
			q.resolveDest(q.target.locationF);
			q.target.locationT = q.target.locationF.getTown();
			if (q.target.locationT == null) {
				return null;
			}
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			q.name = q.giverName + "'s " + q.targetName;
			q.desc = "Fetch " + q.targetName + " from " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			break;
		case 2: //kill quest (murder/hero variants)
			boolean murder = extra.choose(false,true);
			q.qKeywords.add(QKey.KILL);
			if (murder) {
				q.qKeywords.add(QKey.EVIL);
			}else {
				q.qKeywords.add(QKey.LAWFUL);
			}
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
			q.giver = new QuestR() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.player.addGold(30);
					q.complete();
					return false;
				}};
				q.giver.locationF = inn;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
			q.target = new QuestR() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
										//Player.player.eaBox.exeKillLevel += 1;
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
			i = 3; 
			while (q.target.locationF == null) {
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(i));
			i++;
			if (i > 10) {
				return null;
			}
			}
			q.resolveDest(q.target.locationF);
			q.target.locationT = q.target.locationF.getTown();
			if (q.target.locationT == null) {
				return null;
			}
			if (murder == true) {
				q.targetPerson = RaceFactory.getPeace(q.target.locationT.getTier());
			}else {
				q.targetPerson = RaceFactory.getMugger(q.target.locationT.getTier());
			}
			q.targetName = q.targetPerson.getName();
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			if (murder == true) {
				q.name = "Murder " + q.targetName + " for " + q.giverName;
				q.desc = "Murder " + q.targetName + " at " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			}else {
				q.name = "Execute " + q.targetName + " for " + q.giverName;
				q.desc = "Execute " + q.targetName + " at " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			}
			break;
		case 3: //cleanse quest
			q.qKeywords.add(QKey.CLEANSE);
			q.qKeywords.add(QKey.LAWFUL);
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
			q.giver = new QuestR() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.player.addGold(50);
					Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,2,0);
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1,0);
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
					q.qKeywords.add(QKey.GOOD);
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
	
	public static BasicSideQuest getRandomMerchantQuest(Town loc,MerchantGuild mguild) {
		BasicSideQuest q = new BasicSideQuest();
		q.qKeywords.add(QKey.GIVE_MGUILD);
		int i;
		switch (extra.randRange(1,2)) {
		case 1: //fetch quest
			q.qKeywords.add(QKey.FETCH);
			q.giverName = mguild.getQuarterMaster().getName();
			q.targetName = "crate of "+ extra.choose("supplies","goods","trade goods","documents");
			q.giver = new QuestR() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.player.addGold(20);
					Player.player.getPerson().facRep.addFactionRep(Faction.MERCHANT,.1f, 0);
					Player.player.addMPoints(.2f);
					q.complete();
					return false;
				}};
				q.giver.locationF = mguild;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
			q.target = new QuestR() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
			i = 3; 
			while (q.target.locationF == null) {
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(i));
			i++;
			if (i > 10) {
				return null;
			}
			}
			q.resolveDest(q.target.locationF);
			q.target.locationT = q.target.locationF.getTown();
			if (q.target.locationT == null) {
				return null;
			}
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			q.name = q.giverName + "'s " + q.targetName;
			q.desc = "Fetch " + q.targetName + " from " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			break;
		case 2: //cleanse quest
			q.qKeywords.add(QKey.CLEANSE);
			q.giverName = mguild.getQuarterMaster().getName();
			q.giver = new QuestR() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.player.addGold(50);
					Player.player.getPerson().facRep.addFactionRep(Faction.MERCHANT,1,0);
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1,0);
					Player.player.addMPoints(.2f);
					q.complete();
					return false;
				}};
				q.giver.locationF = mguild;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
				q.qKeywords.add(QKey.LAWFUL);
				q.targetName = "bandits";
				q.trigger = "bandit";
				q.count = 3;
			//q.target.locationF.addQR(q.target);
				q.name = "Kill " + q.targetName + " for " + q.giverName ;
				q.desc = "Kill " + q.count + " more " + q.targetName + " on the roads for " + q.giverName;
			break;
		
		}
		
		return q;
	}
	
	public static BasicSideQuest getRandomSideQuest(Town loc,Slum slum) {
		BasicSideQuest q = new BasicSideQuest();
		q.qKeywords.add(QKey.GIVE_SLUM);
		int i;
		switch (extra.randRange(1,2)) {
		case 1: //fetch quest
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
			q.targetName = extra.choose("'taxes'","spice","letter","sealed letter","key");
			q.qKeywords.add(QKey.FETCH);
			q.qKeywords.add(QKey.EVIL);
			q.giver = new QuestR() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.player.addGold(100);
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,0, .05f);
					q.complete();
					return false;
				}};
				q.giver.locationF = slum;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
			q.target = new QuestR() {

				private static final long serialVersionUID = 1L;

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
			i = 2; 
			while (q.target.locationF == null) {
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(i));
			i++;
			if (i > 6) {
				return null;
			}
			}
			q.resolveDest(q.target.locationF);
			q.target.locationT = q.target.locationF.getTown();
			if (q.target.locationT == null) {
				return null;
			}
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			q.name = q.giverName + "'s " + q.targetName;
			q.desc = "Obtain " + q.targetName + " from " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName + " using any means";
			break;
		case 2: //kill quest (murder/hero variants)
			boolean murder = extra.choose(false,true,true);
			q.qKeywords.add(QKey.KILL);
			if (murder) {
				q.qKeywords.add(QKey.EVIL);
			}else {
				q.qKeywords.add(QKey.LAWFUL);
			}
			q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
			q.giver = new QuestR() {

				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					Player.player.addGold(50);
					q.complete();
					return false;
				}};
				q.giver.locationF = slum;
				q.giver.locationT = loc;
				q.giver.overQuest = q;
			q.target = new QuestR() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
										//Player.player.eaBox.exeKillLevel += 1;
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
			i = 2; 
			while (q.target.locationF == null) {
			q.target.locationF = extra.randList(loc.getQuestLocationsInRange(i));
			i++;
			if (i > 8) {
				return null;
			}
			}
			q.resolveDest(q.target.locationF);
			q.target.locationT = q.target.locationF.getTown();
			if (q.target.locationT == null) {
				return null;
			}
			if (murder == true) {
				q.targetPerson = RaceFactory.getPeace(q.target.locationT.getTier());
			}else {
				q.targetPerson = RaceFactory.getMugger(q.target.locationT.getTier());
			}
			q.targetName = q.targetPerson.getName();
			q.target.overQuest = q;
			//q.target.locationF.addQR(q.target);
			if (murder == true) {
				q.name = "Murder " + q.targetName + " for " + q.giverName;
				q.desc = "Murder " + q.targetName + " at " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			}else {
				q.name = "Execute " + q.targetName + " for " + q.giverName;
				q.desc = "Execute " + q.targetName + " at " + q.target.locationF.getName() + " in " + q.target.locationT.getName() + " for " + q.giverName;
			}
			break;
		
		}
		return q;
	}
	
	public static BasicSideQuest getFortCollectQuest(Town fort,FortHall hall,DrawBane db) {
		BasicSideQuest q = new BasicSideQuest();
		q.qKeywords.add(QKey.GIVE_FORT);
			q.giverName = fort.getName() + " Command";
			q.targetName = db.getName();
			q.qKeywords.add(QKey.COLLECT);
			q.giver = new QuestR() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.giverName;
				}

				@Override
				public boolean go() {
					Player.player.getPerson().addXp(1);
					q.complete();
					//TODO: let construct
					return false;
				}};
				q.giver.locationF = hall;
				q.giver.locationT = fort;
				q.giver.overQuest = q;
			q.target = new QuestR() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getName() {
					return q.targetName;
				}

				@Override
				public boolean go() {
					extra.println("You assemble a whole " + q.targetName);
					q.giver.locationF.addQR(q.giver);
					q.desc = "Return the completed " + q.targetName + " to " + q.giverName;
					this.cleanup();
					q.announceUpdate();
					return false;
				}
				
			};
			q.trigger = "db:"+db.name();//could also use ordinal
			switch (db) {
			case LIVING_FLAME:
				q.count = 12;
				q.qKeywords.add(QKey.FIRE_ALIGN);
				break;
			case TELESCOPE:
				q.count = 8;
				q.qKeywords.add(QKey.KNOW_ALIGN);
				break;
			}

			q.name = q.giverName + "'s " + q.targetName;
			q.desc = "Collect " + q.count + " more " + q.targetName + " pieces for " + q.giverName;
		return q;
	}
	
	private void resolveDest(Feature locationF) {
		switch (locationF.getQRType()) {
		case FOREST:
			this.qKeywords.add(QKey.DEST_WOODS);
			break;
		case INN:
			this.qKeywords.add(QKey.DEST_INN);
			break;
		case MOUNTAIN:
			this.qKeywords.add(QKey.DEST_MOUNTAIN);
			break;
		case SLUM:
			this.qKeywords.add(QKey.DEST_SLUM);
			break;
		}
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
	public void questTrigger(TriggerType type, String trigger, int num) {
		if (this.trigger != null) {
			if (this.trigger.equals(trigger)) {
				count-=num;
				if (count <=0) {
					desc = "Return to " + giverName + " at " + giver.locationF.getName() + " in " + giver.locationT.getName();
					
					if (completed == false) {
						giver.locationF.addQR(giver);
						this.announceUpdate();
						completed = true;
					}
				}else {
					switch (type) {//should replace with the string key
					case CLEANSE:
						desc = "Kill " + count + " more " + targetName + " on the roads for " + giverName;
						break;
					case COLLECT:
						desc = "Collect " + count + " more " + targetName + " pieces for " + giverName;
						break;
					}
				}
			}
		}
		
	}

	@Override
	public BasicSideQuest reactionQuest() {
		if (reactionsLeft <= 0) {
			return null;
		}
		return this;
	}


	@Override
	public Collection<? extends String> triggers() {
		return Collections.singletonList(trigger);
	}
}

