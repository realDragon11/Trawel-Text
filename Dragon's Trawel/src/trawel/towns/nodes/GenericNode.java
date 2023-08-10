package trawel.towns.nodes;

import trawel.Networking;
import trawel.extra;
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
import trawel.towns.nodes.NodeConnector.NodeFlag;

public class GenericNode implements NodeType {

	public enum Generic{
		DEAD_PERSON, DEAD_STRING_SIMPLE
		,DEAD_STRING_TOTAL
		,DEAD_RACE_INDEX
		,BASIC_RAGE_PERSON
		,BASIC_DUEL_PERSON
		,VEIN_MINERAL
	}
	
	public static void setSimpleDuelPerson(NodeConnector holder,int node,Person p, String nodename, String interact) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.BASIC_DUEL_PERSON.ordinal());
		holder.setStorage(node, new Object[]{p,nodename,interact});
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
		}
		return false;
	}

	@Override
	public DrawBane[] dbFinds() {//never actually set as a type anywhere, uses base nodetype finds
		return null;
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
		// TODO Auto-generated method stub

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
		extra.println(extra.PRE_RED+holder.getStorageAsArray(node)[2]);
		if (extra.yesNo()) {
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
				holder.setForceGo(node,false);
				return false;
			}else {
				return true;
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

}
