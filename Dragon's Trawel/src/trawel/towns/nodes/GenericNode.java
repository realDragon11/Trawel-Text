package trawel.towns.nodes;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.extra;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.World;
import trawel.towns.misc.PlantSpot;
import trawel.towns.nodes.NodeConnector.NodeFlag;
import trawel.towns.services.Oracle;

public class GenericNode implements NodeType {

	public enum Generic{
		DEAD_PERSON, DEAD_STRING_SIMPLE
		,DEAD_STRING_TOTAL
		,DEAD_RACE_INDEX
		,BASIC_RAGE_PERSON
		,BASIC_DUEL_PERSON
		,VEIN_MINERAL
		,COLLECTOR
		,LOCKDOOR
		,PLANT_SPOT,
		CASUAL_PERSON
		,RICH_GUARD
		,EMPTY_FOR_REGROW
		,MISC_TEXT_WITH_REGEN
	}
	
	public static void setSimpleDuelPerson(NodeConnector holder,int node,Person p, String nodename, String interactstring,String wantDuel) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.BASIC_DUEL_PERSON.ordinal());
		holder.setStorage(node, new Object[]{p,nodename,interactstring,wantDuel});
	}
	
	public static void setSimpleDeadString(NodeConnector holder,int node, String bodyname) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.DEAD_STRING_SIMPLE.ordinal());
		holder.setStorage(node, bodyname);
	}
	public static void setSimpleDeadPerson(NodeConnector holder,int node, Person body) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.DEAD_PERSON.ordinal());
		holder.setStorage(node, body);
	}
	
	public static void setSimpleDeadRaceID(NodeConnector holder,int node, RaceID raceid) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.DEAD_RACE_INDEX.ordinal());
		holder.setStorage(node, new Integer(raceid.ordinal()));
	}
	
	/**
	 * 
	 * @param holder
	 * @param node
	 * @param nodename
	 * @param interactstring
	 * @param interactresult
	 * @param findbody (can be null for no finding)
	 */
	public static void setTotalDeadString(NodeConnector holder,int node,String nodename,String interactstring,String interactresult,String findbody) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.DEAD_STRING_TOTAL.ordinal());
		holder.setStorage(node, new Object[] {nodename,interactstring,interactresult,findbody});
	}
	
	public static void setMiscText(NodeConnector holder,int node,String nodename,String interactstring,String interactresult,String findText) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.MISC_TEXT_WITH_REGEN.ordinal());
		holder.setStorage(node, new Object[] {nodename,interactstring,interactresult,findText});
	}
	
	/**
	 * pass null as attackstring to get 'The <p racename> attacks you!'
	 */
	public static void setBasicRagePerson(NodeConnector holder,int node, Person p, String nodename,String attackString) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.BASIC_RAGE_PERSON.ordinal());
		holder.setForceGo(node,true);
		//should never see interactstring
		holder.setStorage(node,new Object[] {p,nodename,attackString});
	}
	
	/**
	 * used for modern racism, autodetects if the person is racist
	 * <br>
	 * if not racist they're really nice
	 */
	public static void setBasicCasual(NodeConnector holder,int node, Person p) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.CASUAL_PERSON.ordinal());
		holder.setStorage(node, p);
	}
	
	public static void setBasicRichAndGuard(NodeConnector holder,int node) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.RICH_GUARD.ordinal());
		List<Person> list = new ArrayList<Person>();
		int level = holder.getLevel(node);
		if (level == 1) {
			Person p = RaceFactory.makeRich(level);//no guard
			list.add(p);
		}else {
			Person p = RaceFactory.makeRich(level-1);
			list.add(p);
			p = RaceFactory.makeQuarterMaster(level+2);
			p.setFlag(PersonFlag.IS_MOOK,true);
			//used so the bodyguard gets converted into a world person if the rich is killed
			//create an 'avenger' type which is deathcheater without dying
			list.add(p);
		}
		
		
		holder.setStorage(node, list);
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch (Generic.values()[holder.getEventNum(node)]) {
		case BASIC_RAGE_PERSON:
			return basicRager(holder, node);
		case BASIC_DUEL_PERSON:
			return basicDueler(holder, node);
		case DEAD_PERSON:
			return simpleDeadPerson(holder, node);
		case DEAD_RACE_INDEX:
			return simpleDeadRaceID(holder, node);
		case DEAD_STRING_SIMPLE:
			return simpleDeadString(holder, node);
		case DEAD_STRING_TOTAL:
		case MISC_TEXT_WITH_REGEN:
			return miscStringTotal(holder, node);
		case VEIN_MINERAL:
			return genericVein(holder, node);
		case LOCKDOOR:
			return goLockedDoor(holder, node);
		case PLANT_SPOT:
			((PlantSpot)holder.getStorage(node)).go();
			return false;
		case CASUAL_PERSON:
			return goCasualPerson(holder, node);
		case COLLECTOR:
			return goCollector(holder, node);
		case RICH_GUARD:
			return goRichGuard(holder,node);
		case EMPTY_FOR_REGROW:
			extra.println("There is nothing here.");
			return false;
		}
		return false;
	}

	@Override
	public DrawBane[] dbFinds() {//never actually set as a type anywhere, uses base nodetype finds
		return null;
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
		switch (Generic.values()[holder.getEventNum(node)]) {
		case PLANT_SPOT:
			PlantSpot pspot = ((PlantSpot)holder.getStorage(node));
			if (pspot.timer > 40f) {
				if (pspot.contains == Seed.EMPTY) {//if we have nothing
					if (extra.chanceIn(2, 5)) {//add something
						pspot.timer = 0;//reset timer
						switch (NodeType.getTypeEnum(holder.getTypeNum(node))) {
						case BOSS:
							pspot.contains = Seed.SEED_ENT;//virgin later?
							break;
						case CAVE:
							pspot.contains = Seed.SEED_TRUFFLE;
							break;
						case DUNGEON:
							pspot.contains = Seed.SEED_EGGCORN;
							break;
						case GRAVEYARD:
							pspot.contains = Seed.SEED_GARLIC;
							break;
						case GROVE:
							if (extra.chanceIn(1,15)) {
								pspot.contains = Seed.SEED_ENT;
								break;
							}
							if (extra.chanceIn(1,6)) {//remove plantspot instead to keep the grove in flux
								emptyANode(holder,node);
							}
							pspot.contains = extra.choose(Seed.SEED_BEE,Seed.SEED_APPLE,Seed.SEED_APPLE,Seed.SEED_PUMPKIN);
							break;
						case MINE:
							pspot.contains = Seed.SEED_TRUFFLE;
							break;
						}
					}else {
						if (extra.chanceIn(1, 5)) {
							//small chance to reroll into another node so plant spots aren't perma
							//note: only happens if empty, and they have a larger chance to refill
							//than to be removed
							//so a player will likely have to prune them
							pspot.timer = -300;
							//setting timer to negative makes it cost less to have to keep
							//rechecking while still allowing retries
							//force because we already did the randomness check
							regenNode(holder,node,true);
						}else {
							pspot.timer-=24;//don't check again for a day
						}
					}
				}
			}
			calling.localEvents(pspot.passTime(time,calling));
			break;
		case EMPTY_FOR_REGROW:
		case DEAD_PERSON:
		case DEAD_STRING_SIMPLE:
		case DEAD_RACE_INDEX:
		case MISC_TEXT_WITH_REGEN:
		case DEAD_STRING_TOTAL:
			if (holder.globalTimer > 12) {
				regenNode(holder,node,false);
			}
			break;
		}
	}
	
	/**
	 * may or may not successfully regenerate node
	 * based on the node type
	 * @param force if shouldn't roll for chance and always attempt to reset 
	 * @return false if did not regenerate
	 */
	protected static boolean regenNode(NodeConnector holder, int node, boolean force) {
		switch (NodeType.getTypeEnum(holder.getTypeNum(node))) {
		case CAVE:
			if (force || extra.chanceIn(1,3)) {
				resetNode(holder,node,extra.choose(2,3,4));
				holder.globalTimer-=10;
				return true;
			}
			break;
		case DUNGEON:
			if (force || extra.chanceIn(1,3)) {
				resetNode(holder,node,extra.choose(2,3));
				holder.globalTimer-=10;
				return true;
			}
			break;
		case GRAVEYARD:
			//can't easily, lifeless + shadowy stuff
			return false;
		case GROVE:
			if (force || extra.chanceIn(2,3)) {
				//groves are meant to be living, so this only rolls stuff that can likely regrow (or turn into a plant spot)
				resetNode(holder,node,extra.choose(1,3,4,6,7,9,10,11,12,13,16));
				holder.globalTimer-=8;
				return true;
			}
			break;
		case MINE:
			if (force || extra.chanceIn(1,3)) {
				//veins can grow, but not regrow
				resetNode(holder,node,extra.choose(1,1,3,3,3,4,6,7,8,9));
				holder.globalTimer-=20;
				return true;
			}
			break;
		}
		return false;
	}
	
	protected static void resetNode(NodeConnector holder, int node,int eventNum) {
		holder.setEventNum(node, eventNum);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,false);
		holder.setFlag(node,NodeFlag.SILENT_FORCEGO_POSSIBLE,false);
		holder.setFlag(node,NodeFlag.FORCEGO,false);
		holder.setStateNum(node,0);
		holder.setVisited(node,0);//flush
		holder.setFlag(node, NodeFlag.REGROWN,true);
		NodeType.getTypeEnum(holder.getTypeNum(node)).singleton.apply(holder, node);
	}
	
	protected static void emptyANode(NodeConnector holder, int node) {
		holder.setEventNum(node,Generic.EMPTY_FOR_REGROW.ordinal());
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setFlag(node,NodeFlag.SILENT_FORCEGO_POSSIBLE,false);
		holder.setFlag(node,NodeFlag.FORCEGO,false);
		holder.setStateNum(node,0);
		holder.setVisited(node,0);//flush
		holder.setFlag(node, NodeFlag.REGROWN,true);
	}

	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		// should never run
		return -1;
	}

	@Override
	public int generate(NodeConnector holder, int from, int sizeLeft, int tier) {
		// should never run
		return -1;
	}

	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		return null;
	}

	@Override
	public void apply(NodeConnector holder, int madeNode) {
		// cannot run, instead apply directly through an override method which will set everything
	}

	@Override
	public String interactString(NodeConnector holder, int node) {
		switch (Generic.values()[holder.getEventNum(node)]) {
		case DEAD_PERSON:
		case DEAD_RACE_INDEX:
		case DEAD_STRING_SIMPLE:
			return "Examine body.";
		case BASIC_DUEL_PERSON:
			return holder.getStorageAsArray(node)[2].toString();
		case VEIN_MINERAL:
			int mstate = holder.getStateNum(node);
			String vmatName = holder.getStorageFirstClass(node,String.class);
			Material vmat = MaterialFactory.getMat(vmatName);
			if (mstate == 0) {
				return "Mine the " + vmat.color +vmatName+".";
			}
			return "Examine the vein.";
		case DEAD_STRING_TOTAL:
		case MISC_TEXT_WITH_REGEN:
			return holder.getStorageAsArray(node)[1].toString();
		case COLLECTOR:
			return "Approach " +holder.getStorageFirstPerson(node).getName()+".";
		case LOCKDOOR:
			return "Look at the " + holder.getStorageFirstClass(node,String.class)+".";
		case PLANT_SPOT:
			return "Examine (" + ((PlantSpot)holder.getStorage(node)).contains+")";
		case CASUAL_PERSON:
			return "Approach the " + holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)+".";
		case RICH_GUARD:
			List<Person> rplist = holder.getStorageFirstClass(node,List.class);
			Person rich = extra.getNonAddOrFirst(rplist);
			return "Approach the " + rich.getBag().getRace().renderName(false)+".";
		case EMPTY_FOR_REGROW:
			return "Examine empty space.";
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch (Generic.values()[holder.getEventNum(node)]) {
		case BASIC_RAGE_PERSON: case BASIC_DUEL_PERSON:
			return holder.getStorageAsArray(node)[1].toString();
		case DEAD_PERSON:
			Person p = holder.getStorageFirstPerson(node);
			return p.getName() +"'s corpse";
		case DEAD_RACE_INDEX:
			return "Dead "+RaceID.values()[holder.getStorageFirstClass(node, Integer.class)].name;
		case DEAD_STRING_SIMPLE:
			return "Dead " +holder.getStorageFirstClass(node, String.class);
		case DEAD_STRING_TOTAL:
		case MISC_TEXT_WITH_REGEN:
			return holder.getStorageAsArray(node)[0].toString();
		case VEIN_MINERAL:
			int mstate = holder.getStateNum(node);
			String vmatName = holder.getStorageFirstClass(node,String.class);
			Material vmat = MaterialFactory.getMat(vmatName);
			if (mstate == 0) {
				return "Vein of " + vmat.color +vmatName;//needs to have the vein first so it can have the color applied to it
			}
			return "Mined Vein";
		case COLLECTOR:
			return holder.getStorageFirstPerson(node).getName();
		case LOCKDOOR:
			return holder.getStorageFirstClass(node,String.class);
		case PLANT_SPOT:
			Seed contains = ((PlantSpot)holder.getStorage(node)).contains;
			return contains == Seed.EMPTY ? "Plant Spot" : contains.toString();
		case CASUAL_PERSON:
			return holder.getStorageFirstPerson(node).getBag().getRace().renderName(false);
		case RICH_GUARD:
			List<Person> rplist = holder.getStorageFirstClass(node,List.class);
			Person rich = extra.getNonAddOrFirst(rplist);
			return rich.getBag().getRace().renderName(false);
		case EMPTY_FOR_REGROW:
			return "Empty Space";
		}
		return null;
	}
	
	private boolean basicRager(NodeConnector holder,int node) {
		extra.println(extra.PRE_BATTLE+holder.getStorageAsArray(node)[2]);
		Person p = holder.getStorageFirstPerson(node);
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
			holder.setForceGo(node,false);
			return false;
		}else {
			return true;
		}
	}
	
	private boolean basicDueler(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		if (p.reallyFight(holder.getStorageAsArray(node)[3].toString())) {
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
				holder.setForceGo(node,false);
				return false;
			}else {
				return false;//duelist doesn't kick you out
			}
		}
		return false;
	}
	
	private boolean simpleDeadString(NodeConnector holder,int node) {
		String str = holder.getStorageFirstClass(node, String.class);
		extra.println(extra.choose("The " + str + " is dead.","The "+str+" lies here, dead."));//TODO use inserts in a global dead body fluffer
		holder.findBehind(node,"body parts");
		return false;
	}
	
	private boolean simpleDeadPerson(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		if (p.getSuper() != null && p.getSuper().everDeathCheated()) {
			extra.println("There was a body here, but now it's gone!");
			return false;
		}
		
		extra.println(p.getName() + " is dead.");
		holder.findBehind(node,p.getName() +"'s corpse");
		return false;
	}
	/**
	 * also used for dead body total
	 */
	private boolean miscStringTotal(NodeConnector holder,int node) {
		Object[] objs = holder.getStorageAsArray(node);
		String str = objs[2].toString();//third
		extra.println(str);
		if (objs[3] != null) {
			holder.findBehind(node,objs[3].toString());
		}
		return false;
	}
	
	private boolean simpleDeadRaceID(NodeConnector holder,int node) {
		Integer i = holder.getStorageFirstClass(node, Integer.class);
		String str = RaceID.values()[i].name;
		extra.println(extra.choose("The " + str + " is dead.","The "+str+" lies here, dead."));//TODO use inserts in a global dead body fluffer
		holder.findBehind(node,"body parts");
		return false;
	}
	
	/**
	 * maxValueTier is 0 to 3
	 * <br>
	 * 0 tier is from tin to iron with a chance of tin to gold; it is the only tier that can't turn into gems
	 * <br>
	 * 1 tier is from tin to iron with a chance of tin to platinum
	 * <br>
	 * 2 tier is from tin to silver with a chance of iron to moonsilver
	 * <br>
	 * 3 tier is from tin to gold with a chance of tin to adamantine
	 */
	protected static void applyGenericVein(NodeConnector holder,int node, int maxValueTier) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.VEIN_MINERAL.ordinal());
		holder.addVein();//gem veins count now
		String mineral = null;
		if (maxValueTier > 0) {
			if (extra.chanceIn(1, 6)) {
				mineral = extra.choose("emerald","ruby","sapphire");
			}else {
				switch (maxValueTier) {
				case 1:
					if (extra.chanceIn(1,3)) {
						mineral = extra.choose("tin","copper","iron","silver","gold","platinum");
					}else {
						mineral = extra.choose("tin","copper","iron");
					}
					break;
				case 2:
					if (extra.chanceIn(1,3)) {
						mineral = extra.choose("iron","silver","gold","platinum","moonsilver");
					}else {
						mineral = extra.choose("tin","copper","iron","silver");
					}
					break;
				case 3:
					if (extra.chanceIn(1,3)) {
						mineral = extra.choose("silver","gold","platinum","moonsilver","mythril","adamantine");
					}else {
						mineral = extra.choose("tin","copper","iron","silver","gold");
					}
					break;
				}
			}
		}else {
			if (extra.chanceIn(1,3)) {
				mineral = extra.choose("tin","copper","iron","silver","gold");
			}else {
				mineral = extra.choose("tin","copper","iron");
			}
		}
		holder.setStorage(node, new Object[]{mineral});
	}
	
	protected static void applyGenericGemVein(NodeConnector holder,int node) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.VEIN_MINERAL.ordinal());
		holder.setStorage(node, new Object[]{extra.choose("emerald","ruby","sapphire")});
	}
	
	private boolean genericVein(NodeConnector holder,int node) {
		String matName = holder.getStorageFirstClass(node,String.class);
		Material m = MaterialFactory.getMat(matName);
		if (holder.getStateNum(node) == 0) {
			Networking.unlockAchievement("ore1");
			int subType = 0;
			switch (matName) {
			case "emerald":
				subType = 1;
				Player.player.emeralds++;
				extra.println("You mine the vein and claim an "+m.color+"emerald!");
				break;
			case "ruby":
				Player.player.rubies++;
				extra.println("You mine the vein and claim a "+m.color+"ruby!");
				subType = 2;
				break;
			case "sapphire":
				Player.player.sapphires++;
				extra.println("You mine the vein and claim a "+m.color+"sapphire!");
				subType = 3;
				break;
			default:
				int reward = extra.randRange(0,1)+m.veinReward;
				Player.player.addGold(reward);
				extra.println("You mine the vein of "+ World.currentMoneyDisplay(reward)+ " worth of "+m.color+matName+".");
				break;
			}
			holder.setStateNum(node,1);
			/*
			node.name = "empty "+node.storage1+" vein";
			node.interactString = "examine empty "+node.storage1+" vein";*/
			((Mine)holder.parent).removeVein();
			holder.findBehind(node,"empty "+m.color+matName+"vein");//instant chance so they want to mine more
		}else {
			extra.println("The "+m.color+matName+" has already been mined.");
			holder.findBehind(node,"empty "+m.color+matName+"vein");
		}
		return false;
	}
	
	public static void applyCollector(NodeConnector holder,int node) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.COLLECTOR.ordinal());
		holder.setStorage(node, RaceFactory.makeCollector(holder.getLevel(node)));
	}
	
	public static boolean goCollector(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1, p);
		extra.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {
					@Override
					public String title() {
						return p.getName() + " is sifting through the area, looking for collectables.";
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Attempt to talk to them.";
					}

					@Override
					public boolean go() {
						if (extra.chanceIn(1,30)) {
							extra.println(extra.PRE_BATTLE +"\"Enough of your games!\"");
							setBasicRagePerson(holder, node, p, "A very angry "+extra.PRE_BATTLE+p.getName(),extra.PRE_BATTLE+p.getName() + " attacks you!");
							return true;//out of menu
						}
						if (extra.chanceIn(1,3)) {
							Oracle.tip("old");
						}
						extra.println("They ignore you.");
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Attack.";
					}

					@Override
					public boolean go() {
						if (p.reallyAttack()) {
							setBasicRagePerson(holder, node, p, "An angry "+extra.PRE_BATTLE+p.getName(),extra.PRE_BATTLE+p.getName() + " attacks you!");
							return true;//out of menu
						}
						return false;
					}});
				list.add(new MenuBack("leave"));
				return list;
			}});
		Networking.clearSide(1);
		return false;
	}
	
	public static void applyLockDoor(NodeConnector holder,int node) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.LOCKDOOR.ordinal());
		holder.setStorage(node,extra.choose("locked door","barricaded door","padlocked door"));
		holder.setForceGo(node, true);
	}
	
	private boolean goLockedDoor(NodeConnector holder,int node) {
		if (holder.isForceGo(node)) {
			if (holder.parent.getOwner() == Player.player) {
				extra.println("You find the keyhole and then unlock the "+holder.getStorageFirstClass(node,String.class)+".");
				holder.setStateNum(node,1);//unlocked once
				holder.findBehind(node,"unlocked door");
			}else {
				String name = holder.getStorageFirstClass(node,String.class);
				if (holder.getStateNum(node) == 0){
					extra.println("The "+name+" is locked.");
				}else {
					extra.println("Looks like they changed the locks!");
				}
				extra.println("Break down the "+name+"?");
				if (!extra.yesNo()) {
					NodeConnector.setKickGate();
					return false;
				}
				extra.println("You bash open the "+name+".");
				
				holder.setStateNum(node,2);//broken open
				holder.setForceGo(node, false);
				name = "broken " +name;
				holder.findBehind(node,name);
				holder.setStorage(node,name);
			}
		}else {
			if (holder.parent.getOwner() == Player.player) {
				extra.println("You relock the door every time you go by it, but you know where the hole is now so that's easy.");
			}else {
				extra.println(
						extra.choose(
						"The door is broken."
						,"The door is smashed to bits."
						,"The metal on the door is hanging off the splinters."
						,"The lock is intact. The rest of the door isn't."
						)
						);
				holder.findBehind(node,"broken door");
			}
		};
		return false;
	}
	
	/**
	 * use null for random starting, `""` for nothing
	 */
	public static void setPlantSpot(NodeConnector holder,int node, Seed starting) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.PLANT_SPOT.ordinal());
		if (starting == null) {
			starting = extra.choose(Seed.GROWN_APPLE,Seed.GROWN_BEE,Seed.GROWN_EGGCORN);
		}
		holder.setStorage(node,new PlantSpot(holder.getLevel(node),starting));
	}
	
	public static boolean goCasualPerson(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1, p);
		boolean racist = p.isRacist();
		extra.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {
					@Override
					public String title() {
						return p.getName() + " is wandering around.";
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Attempt to talk to them.";
					}

					@Override
					public boolean go() {
						if (racist) {
							RaceID r = p.getBag().getRaceID();
							if (r == Player.bag.getRaceID()) {
								String str = Oracle.tipString("racistPraise");
								str = str.replaceAll("oracles",r.namePlural);
								str = str.replaceAll("oracle",r.namePlural);
								extra.println("\"" +extra.capFirst(str)+"\"");
							}else {
								if (extra.chanceIn(4,5)) {
									String str = Oracle.tipString(extra.choose("racistShun","racistPraise"));
									str = str.replaceAll("not-oracle",Player.bag.getRace().randomSwear());
									str = str.replaceAll("oracles",r.namePlural);
									str = str.replaceAll("oracle",r.namePlural);
									extra.println("\"" +extra.capFirst(str)+"\"");	
								}else {
									extra.println("\"" + Player.bag.getRace().randomInsult() +"\"");
								}
							}
						}else {
							if (p.isAngry()) {
								extra.println("They seem really mad about everything.");
							}else {
								extra.println("They seem nice, but have nothing of substance to talk about.");
							}
						}
						return false;
					}}
				);
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Attack";
					}

					@Override
					public boolean go() {
						if (p.reallyAttack()) {
							RaceID r = p.getBag().getRaceID();
							boolean racistToYou = (racist && r == Player.bag.getRaceID());
							String name = extra.capFirst(r.name);
							if (racistToYou) {
								setBasicRagePerson(holder, node, p, "An angry "+extra.PRE_BATTLE +name,extra.PRE_BATTLE+"The racist "+name + " attacks you!");
								return true;//out of menu, not area
							}else {
								if (p.isAngry()) {
									setBasicRagePerson(holder, node, p, "An angry "+extra.PRE_BATTLE +name,extra.PRE_BATTLE+"The "+name + " attacks you!");
									return true;//out of menu, not area
								}else {//will not hold it against you
									Combat c = Player.player.fightWith(p);
									if (c.playerWon() > 0) {
										setSimpleDeadRaceID(holder, node, r);
									}
									return true;//out of menu, not area
								}
							}
							
						}
						return false;
					}});
				list.add(new MenuBack("leave"));
				return list;
			}});
		Networking.clearSide(1);
		return false;
	}
	
	public static boolean goRichGuard(NodeConnector holder,int node) {
		List<Person> peeps = holder.getStorageFirstClass(node, List.class);
		int state = holder.getStateNum(node);
		final Person leader = extra.getNonAddOrFirst(peeps);
		if (state > 1) {//if attacking by default
			if (leader.getFlag(PersonFlag.IS_MOOK)) {
				extra.println(extra.PRE_BATTLE+"The bodyguard attacks you!");
			}else {
				if (peeps.size() > 1) {
					extra.println(extra.PRE_BATTLE+"Their bodyguard swoops in to assist!");
				}else {
					extra.println(extra.PRE_BATTLE+"They attack without backup!");
				}
				
			}
			Combat c = Player.player.massFightWith(peeps);
			if (c.playerWon() > 0) {
				holder.setForceGo(node,false);//clean up any force go
				//leader might be the guard now, we don't care
				GenericNode.setSimpleDeadRaceID(holder, node, leader.getBag().getRaceID());
				return false;
			}else {
				Person aleader = extra.getNonAddOrFirst(c.getNonSummonSurvivors());//need to keep leader effectively final sadly
				if (aleader.getFlag(PersonFlag.IS_MOOK) && extra.chanceIn(1,3)) {
					//if rich is dead, 33% chance to leave
					GenericNode.setSimpleDeadString(holder, node,"body");
				}else {
					holder.setStorage(node,c.getNonSummonSurvivors());
				}
				return true;//kick out
			}
		}
		//if we can talk
		leader.getBag().graphicalDisplay(1,leader);
		assert !leader.getFlag(PersonFlag.IS_MOOK);
		//boolean is_add = leader.getFlag(PersonFlag.IS_ADD);//can't be an add at this point
		extra.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {
					@Override
					public String title() {
						return leader.getName() + " is wandering around.";
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Attempt to talk to them.";
					}

					@Override
					public boolean go() {
						if (Player.player.getGold() > leader.getBag().getGold()*4) {//you're so much richer than them that they see you as part of their bigotry
							String str = Oracle.tipString("racistPraise");
							str = str.replaceAll(" an "," a ");
							str = str.replaceAll("oracles","rich person");
							str = str.replaceAll("oracle","rich people");
							extra.println("\"" +extra.capFirst(str)+"\"");
						}else {
							String str = Oracle.tipString(extra.choose("racistShun","racistPraise"));
							str = str.replaceAll(" an "," a ");
							str = str.replaceAll("not-oracle","poor person");
							str = str.replaceAll("oracles","rich people");
							str = str.replaceAll("oracle","rich person");
							extra.println("\"" +extra.capFirst(str)+"\"");	
						};
						return false;
					}
					});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Attack";
					}

					@Override
					public boolean go() {
						if (leader.reallyAttack()) {
							holder.setStateNum(node,2);
							holder.setForceGo(node,true);
							return true;//out of menu
						}
						return false;
					}
				});
				list.add(new MenuBack("leave"));
				return list;
			}
		});
		return false;//forcego or not
	}

}
