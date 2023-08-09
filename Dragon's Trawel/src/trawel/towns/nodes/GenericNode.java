package trawel.towns.nodes;

import trawel.extra;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.nodes.NodeConnector.NodeFlag;

public class GenericNode implements NodeType {

	public enum Generic{
		DEAD_PERSON, DEAD_STRING_SIMPLE
		,DEAD_STRING_TOTAL
		,DEAD_RACE_INDEX
		,BASIC_RAGE_PERSON
		
		,VEIN_MINERAL
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
		case DEAD_PERSON:
			return simpleDeadPerson(holder, node);
		case DEAD_RACE_INDEX:
			return simpleDeadRaceID(holder, node);
		case DEAD_STRING_SIMPLE:
			return simpleDeadString(holder, node);
		case DEAD_STRING_TOTAL:
			break;
		case VEIN_MINERAL:
			break;
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
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch (Generic.values()[holder.getTypeNum(node)]) {
		case BASIC_RAGE_PERSON:
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
			//TODO
			break;
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
		String str = holder.getStorageAsArray(node)[1].toString();//second
		extra.println(str);
		holder.findBehind(node,"body parts");
		return false;
	}
	
	private boolean simpleDeadRaceID(NodeConnector holder,int node) {
		Integer i = holder.getStorageFirstClass(node, Integer.class);
		String str = RaceID.values()[i].name;
		extra.println(extra.choose("The " + str + " is dead.","The "+str+" lies here, dead."));//TODO use inserts in a global dead body fluffer
		holder.findBehind(node,"body parts");
		return false;
	}

}
