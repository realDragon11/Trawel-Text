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
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
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
		,PLANT_SPOT
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
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch (Generic.values()[holder.getTypeNum(node)]) {
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
			return deadStringTotal(holder, node);
		case VEIN_MINERAL:
			return genericVein(holder, node);
		case LOCKDOOR:
			return goLockedDoor(holder, node);
		case PLANT_SPOT:
			((PlantSpot)holder.getStorage(node)).go();
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
		switch (Generic.values()[holder.getTypeNum(node)]) {
		case PLANT_SPOT:
			PlantSpot pspot = ((PlantSpot)holder.getStorage(node));
			if (pspot.timer > 40f) {
				if (pspot.contains == "") {//if we have nothing
					if (extra.chanceIn(1, 5)) {//add something
						pspot.timer = 0;//reset timer
						switch (NodeType.getTypeEnum(holder.getTypeNum(node))) {
						case BOSS:
							pspot.contains = "ent sapling";//virgin later?
							break;
						case CAVE:
							pspot.contains = "truffle spores";
							break;
						case DUNGEON:
							pspot.contains = "eggcorn seed";
							break;
						case GRAVEYARD:
							pspot.contains = "garlic seed";
							break;
						case GROVE:
							if (extra.chanceIn(1,15)) {
								pspot.contains = "ent sapling";
								break;
							}
							pspot.contains = extra.choose("bee larva","apple seed","apple seed","pumpkin seed");
							break;
						case MINE:
							pspot.contains = "truffle spores";
							break;
						}
					}else {
						pspot.timer-=24;//don't check again for a day
					}
				}
			}
			calling.localEvents(pspot.passTime(time,calling));
			break;
		}
		
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
		// TODO Auto-generated method stub
		switch (Generic.values()[holder.getTypeNum(node)]) {
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
			return holder.getStorageAsArray(node)[1].toString();
		case COLLECTOR:
			return "Approach " +holder.getStorageFirstPerson(node).getName();
		case LOCKDOOR:
			return "Look at the " + holder.getStorageFirstClass(node,String.class)+".";
		case PLANT_SPOT:
			return "Examine (" + ((PlantSpot)holder.getStorage(node)).contains+")";
			
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch (Generic.values()[holder.getTypeNum(node)]) {
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
			holder.getStorageFirstClass(node,String.class);
		case PLANT_SPOT:
			String contains = ((PlantSpot)holder.getStorage(node)).contains;
			return contains == "" ? "Plant Spot" : contains ;
		}
		return null;
	}
	
	private boolean basicRager(NodeConnector holder,int node) {
		//if (holder.getStateNum(node) == 0) {
			extra.println(extra.PRE_RED+holder.getStorageAsArray(node)[2]);
			Person p = holder.getStorageFirstPerson(node);
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() > 0) {
					GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
					holder.setForceGo(node,false);
					return false;
				}else {
					return true;
				}
		//}
		//return false;
	}
	
	private boolean basicDueler(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1, p);
		extra.println(extra.PRE_RED+holder.getStorageAsArray(node)[3]);
		if (extra.yesNo()) {
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
				holder.setForceGo(node,false);
				return false;
			}else {
				return false;//duelist doesn't kick you out
			}
		}
		Networking.clearSide(1);//clear the display
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
		extra.println(p.getName() + " is dead.");
		holder.findBehind(node,p.getName() +"'s corpse");
		return false;
	}
	private boolean deadStringTotal(NodeConnector holder,int node) {
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
							extra.println(extra.PRE_RED +"\"Enough of your games!\"");
							setBasicRagePerson(holder, node, p, "A very angry "+extra.PRE_RED+p.getName(),extra.PRE_RED+p.getName() + " attacks you!");
							return false;
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
						return extra.PRE_RED+"Attack";
					}

					@Override
					public boolean go() {
						extra.println("Really attack " +p.getName()+"?");
						if (extra.yesNo()) {
							setBasicRagePerson(holder, node, p, "An angry "+extra.PRE_RED+p.getName(),extra.PRE_RED+p.getName() + " attacks you!");
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
				if (holder.getStateNum(node) == 1) {
					extra.println("You bash open the "+holder.getStorageFirstClass(node,String.class)+".");
				}else {
					extra.println("Looks like they changed the locks! You bash open the door.");
				}
				
				holder.setStateNum(node,2);//broken open
				holder.setForceGo(node, false);
				holder.findBehind(node,"broken door");
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
	public static void setPlantSpot(NodeConnector holder,int node, String starting) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.PLANT_SPOT.ordinal());
		if (starting == null) {
			starting = extra.choose("apple tree","bee hive","eggcorn");
		}
		holder.setStorage(node,new PlantSpot(holder.getLevel(node),starting));
	}

}
