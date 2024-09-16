package trawel.towns.features.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.arc.misc.Deaths;
import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Gem;
import trawel.personal.item.solid.Material;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TrawelTime;
import trawel.towns.contexts.World;
import trawel.towns.features.elements.PlantSpot;
import trawel.towns.features.nodes.NodeConnector.NodeFlag;
import trawel.towns.features.services.Oracle;

public class GenericNode implements NodeType {

	public enum Generic{
		DEAD_PERSON
		,DEAD_STRING_SIMPLE
		,DEAD_STRING_TOTAL
		,DEAD_RACE_INDEX
		,BASIC_RAGE_PERSON
		,BASIC_DUEL_PERSON
		,VEIN_MINERAL
		,COLLECTOR
		,LOCKDOOR
		,PLANT_SPOT
		,CASUAL_PERSON
		,RICH_GUARD
		,EMPTY_FOR_REGROW
		,MISC_TEXT_WITH_REGEN
		,TRAPPED_CHAMBER
		,BOSS
	}
	
	public static void setSimpleDuelPerson(NodeConnector holder,int node,Person p, String nodename, String interactstring,String wantDuel) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.BASIC_DUEL_PERSON.ordinal());
		holder.setStorage(node, new Object[]{p,nodename,interactstring,wantDuel});
	}
	
	public static void setSimpleDeadString(NodeConnector holder,int node, String bodyname) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.DEAD_STRING_SIMPLE.ordinal());
		holder.setStorage(node, bodyname);
	}
	public static void setSimpleDeadPerson(NodeConnector holder,int node, Person body) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.DEAD_PERSON.ordinal());
		holder.setStorage(node, body);
	}
	
	public static void setSimpleDeadRaceID(NodeConnector holder,int node, RaceID raceid) {
		morphSetup(holder,node);
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
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.DEAD_STRING_TOTAL.ordinal());
		holder.setStorage(node, new Object[] {nodename,interactstring,interactresult,findbody});
	}
	
	public static void setMiscText(NodeConnector holder,int node,String nodename,String interactstring,String interactresult,String findText) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.MISC_TEXT_WITH_REGEN.ordinal());
		holder.setStorage(node, new Object[] {nodename,interactstring,interactresult,findText});
	}
	
	/**
	 * pass null as attackstring to get 'The <p racename> attacks you!'
	 * <br>
	 * pass null as cleanseID to not trigger any quests
	 */
	public static void setBasicRagePerson(NodeConnector holder,int node, Person p, String nodename,String attackString) {
		morphSetup(holder,node);
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
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.CASUAL_PERSON.ordinal());
		holder.setStorage(node, p);
	}
	
	public static void setBasicRichAndGuard(NodeConnector holder,int node) {
		morphSetup(holder,node);
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
			Print.println("There is nothing here.");
			return false;
		case TRAPPED_CHAMBER:
			return trappedChamber(holder, node);
		case BOSS:
			return BossNode.interact(holder, node);
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
					if (Rand.chanceIn(2, 5)) {//add something
						pspot.timer = 0;//reset timer
						switch (NodeType.getTypeEnum(holder.getTypeNum(node))) {
						case CAVE:
							pspot.contains = Seed.SEED_TRUFFLE;
							break;
						case DUNGEON:
							pspot.contains = Seed.SEED_FUNGUS;
							break;
						case GRAVEYARD:
							pspot.contains = Seed.SEED_GARLIC;
							break;
						case GROVE:
							if (Rand.chanceIn(1,15)) {
								pspot.contains = Seed.SEED_ENT;
								break;
							}
							if (Rand.chanceIn(1,6)) {//remove plantspot instead to keep the grove in flux
								emptyANode(holder,node);
							}
							pspot.contains = Rand.choose(Seed.SEED_BEE,Seed.SEED_APPLE,Seed.SEED_APPLE,Seed.SEED_PUMPKIN);
							break;
						case MINE:
							pspot.contains = Seed.SEED_TRUFFLE;
							break;
						case BEACH:
							pspot.contains = Seed.SEED_EGGCORN;
							break;
						}
					}else {
						if (Rand.chanceIn(1, 5)) {
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
			if (holder.globalTimer > 36 && Player.player.atFeature != holder.parent) {
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
			if (force || Rand.chanceIn(1,3)) {
				resetNode(holder,node,NodeType.NodeTypeNum.CAVE.singleton.rollRegrow());
				holder.globalTimer-=24;
				return true;
			}
			break;
		case DUNGEON:
			if (force || Rand.chanceIn(1,3)) {
				resetNode(holder,node,NodeType.NodeTypeNum.DUNGEON.singleton.rollRegrow());
				holder.globalTimer-=24;
				return true;
			}
			break;
		case GRAVEYARD://can't easily, lifeless + shadowy stuff
			return false;
		case GROVE:
			if (force || Rand.chanceIn(2,3)) {
				//groves are meant to be living, so this only rolls stuff that can likely regrow (or turn into a plant spot)
				resetNode(holder,node,NodeType.NodeTypeNum.GROVE.singleton.rollRegrow());
				holder.globalTimer-=16;
				return true;
			}
			break;
		case MINE:
			if (force || Rand.chanceIn(1,3)) {
				//veins can grow, but not regrow
				resetNode(holder,node,NodeType.NodeTypeNum.MINE.singleton.rollRegrow());
				holder.globalTimer-=48;
				return true;
			}
			break;
		case BEACH:
			if (force || Rand.chanceIn(1,3)) {
				//avoids rolling things that can't regrow
				resetNode(holder,node,NodeType.NodeTypeNum.BEACH.singleton.rollRegrow());
				holder.globalTimer-=16;
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
	
	/**
	 * Node has no content, can be used for regrowth
	 * @param holder
	 * @param node
	 */
	protected static void emptyANode(NodeConnector holder, int node) {
		holder.setEventNum(node,Generic.EMPTY_FOR_REGROW.ordinal());
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setFlag(node,NodeFlag.SILENT_FORCEGO_POSSIBLE,false);
		holder.setFlag(node,NodeFlag.FORCEGO,false);
		holder.setStateNum(node,0);
		holder.setVisited(node,0);//flush
		holder.setFlag(node, NodeFlag.REGROWN,true);
	}
	
	/**
	 * cleans up remaining state/flags before morphing a node. Isn't a valid end state, call one of the other functions afterwards to apply the correct node
	 * @param holder
	 * @param node
	 */
	protected static void morphSetup(NodeConnector holder, int node) {
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,false);//will probably be changed after
		holder.setFlag(node,NodeFlag.SILENT_FORCEGO_POSSIBLE,false);
		holder.setFlag(node,NodeFlag.FORCEGO,false);
		holder.setStateNum(node,0);
		//keep visited the same
		//holder.setFlag(node, NodeFlag.REGROWN,false);//don't set since it needs to display it was regrown
		//holder.setFlag(node, NodeFlag.UNIQUE_1,false);//unsure how to handle this
		
		//clear storage to free space
		holder.setStorage(node,null);
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
				return "Mine the " + vmat.color +vmatName+TrawelColor.COLOR_RESET+".";
			}
			//displays color even after mined
			return "Examine the "+vmat.color+"vein"+TrawelColor.COLOR_RESET+".";
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
		case TRAPPED_CHAMBER:
			if (NodeType.getTypeEnum(holder.getTypeNum(node)) == NodeTypeNum.MINE) {
				//mine hides this type of node
				//graveyard also hides it, but it handles displaying it itself
				if (MineNode.hideContents(holder, node)) {
					return MineNode.STR_SHADOW_ROOM_ACT;
				}
			}
			return getTChamberInteract(holder, node);
		case BOSS:
			return BossNode.interactString(holder, node);
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
				return "Vein of " + vmat.color +vmatName+TrawelColor.COLOR_RESET;//needs to have the vein first so it can have the color applied to it
			}
			//displays color even after mined
			return "Mined "+vmat.color+"Vein"+TrawelColor.COLOR_RESET;
		case COLLECTOR:
			return holder.getStorageFirstPerson(node).getName();
		case LOCKDOOR:
			return holder.getStorageFirstClass(node,String.class);
		case PLANT_SPOT:
			Seed contains = ((PlantSpot)holder.getStorage(node)).contains;
			return contains == Seed.EMPTY ? "Plant Spot" : contains.toString();
		case CASUAL_PERSON:
			return (holder.getStateNum(node) >=10 ? "An angry " :"")+holder.getStorageFirstPerson(node).getBag().getRace().renderName(false);
		case RICH_GUARD:
			List<Person> rplist = holder.getStorageFirstClass(node,List.class);
			Person rich = extra.getNonAddOrFirst(rplist);
			return rich.getBag().getRace().renderName(false);
		case EMPTY_FOR_REGROW:
			return "Empty Space";
		case TRAPPED_CHAMBER:
			if (NodeType.getTypeEnum(holder.getTypeNum(node)) == NodeTypeNum.MINE) {
				//mine hides this type of node
				if (MineNode.hideContents(holder, node)) {
					return MineNode.STR_SHADOW_ROOM_NAME;
				}
			}
			return getTChamberName(holder, node);
		case BOSS:
			return BossNode.nodeName(holder, node);
		}
		return null;
	}
	
	private boolean basicRager(NodeConnector holder,int node) {
		Print.println(TrawelColor.PRE_BATTLE+holder.getStorageAsArray(node)[2]);
		Person p = holder.getStorageFirstPerson(node);
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
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
				return false;
			}else {
				return false;//duelist doesn't kick you out
			}
		}
		return false;
	}
	
	private boolean simpleDeadString(NodeConnector holder,int node) {
		String str = holder.getStorageFirstClass(node, String.class);
		Print.println(Rand.choose("The " + str + " is dead.","The "+str+" lies here, dead."));//TODO use inserts in a global dead body fluffer
		holder.findBehind(node,"body parts");
		return false;
	}
	
	private boolean simpleDeadPerson(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		if (p.getSuper() != null && p.getSuper().everDeathCheated()) {
			Print.println("There was a body here, but now it's gone!");
			return false;
		}
		
		Print.println(p.getName() + " is dead.");
		holder.findBehind(node,p.getName() +"'s corpse");
		return false;
	}
	/**
	 * also used for dead body total
	 */
	private boolean miscStringTotal(NodeConnector holder,int node) {
		Object[] objs = holder.getStorageAsArray(node);
		String str = objs[2].toString();//third
		Print.println(str);
		if (objs[3] != null) {
			holder.findBehind(node,objs[3].toString());
		}
		return false;
	}
	
	private boolean simpleDeadRaceID(NodeConnector holder,int node) {
		Integer i = holder.getStorageFirstClass(node, Integer.class);
		String str = RaceID.values()[i].name;
		Print.println(Rand.choose("The " + str + " is dead.","The "+str+" lies here, dead."));//TODO use inserts in a global dead body fluffer
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
	 * 3 tier is from tin to gold with a chance of silver to adamantine
	 */
	protected static void applyGenericVein(NodeConnector holder,int node, int maxValueTier) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.VEIN_MINERAL.ordinal());
		holder.addVein();//gem veins count now
		String mineral = null;
		if (maxValueTier > 0) {
			if (Rand.chanceIn(1, 6)) {
				mineral = Rand.choose("emerald","emerald","ruby","sapphire");//more likely to apply emerald
			}else {
				switch (maxValueTier) {
				case 1:
					if (Rand.chanceIn(1,3)) {
						mineral = Rand.choose("tin","copper","iron","silver","gold","platinum");
					}else {
						mineral = Rand.choose("tin","copper","iron");
					}
					break;
				case 2:
					if (Rand.chanceIn(1,3)) {
						mineral = Rand.choose("iron","silver","gold","platinum","moonsilver");
					}else {
						mineral = Rand.choose("tin","copper","iron","silver");
					}
					break;
				case 3:
					if (Rand.chanceIn(1,3)) {
						mineral = Rand.choose("silver","gold","platinum","moonsilver","mythril","adamantine");
					}else {
						mineral = Rand.choose("tin","copper","iron","silver","gold");
					}
					break;
				}
			}
		}else {
			if (Rand.chanceIn(1,3)) {
				mineral = Rand.choose("tin","copper","iron","silver","gold");
			}else {
				mineral = Rand.choose("tin","copper","iron");
			}
		}
		holder.setStorage(node, new Object[]{mineral});
	}
	
	protected static void applyGenericGemVein(NodeConnector holder,int node) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.VEIN_MINERAL.ordinal());
		holder.addVein();
		holder.setStorage(node, new Object[]{Rand.choose("emerald","emerald","ruby","sapphire")});//more likely to apply emerald
	}
	
	private boolean genericVein(NodeConnector holder,int node) {
		String matName = holder.getStorageFirstClass(node,String.class);
		Material m = MaterialFactory.getMat(matName);
		if (holder.getStateNum(node) == 0) {
			Networking.unlockAchievement("ore1");
			
			Gem gem = null;
			boolean themed = false;
			switch (matName) {
			case "emerald":
				gem = Gem.EMERALD;
				themed = true;
				break;
			case "ruby":
				gem = Gem.RUBY;
				break;
			case "sapphire":
				gem = Gem.SAPPHIRE;
				break;
			default:
				int reward = IEffectiveLevel.cleanRangeReward(holder.getLevel(node),2*m.veinReward, .6f);
				Player.player.addGold(reward);
				Print.println("You mine the vein of "+ World.currentMoneyDisplay(reward)+ " worth of "+m.color+matName+TrawelColor.COLOR_RESET+".");
				break;
			}
			if (gem != null) {
				int gemAmount = IEffectiveLevel.cleanRangeReward(holder.getLevel(node),gem.reward(1.5f, themed), .5f);
				gem.changeGem(gemAmount);
				//TODO: color is overwritten immediately now
				Print.println("You mine the vein and claim "+gemAmount+" "+m.color+gem.fancyName(gemAmount)+TrawelColor.COLOR_RESET+"!");
			}
			holder.setStateNum(node,1);
			holder.removeVein();
			
			holder.findBehind(node,"empty "+m.color+matName+"vein"+TrawelColor.COLOR_RESET);//instant chance so they want to mine more
		}else {
			Print.println("The "+m.color+matName+TrawelColor.COLOR_RESET+" has already been mined.");
			holder.findBehind(node,"empty "+m.color+matName+"vein"+TrawelColor.COLOR_RESET);
		}
		return false;
	}
	
	public static void applyCollector(NodeConnector holder,int node) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.COLLECTOR.ordinal());
		holder.setStorage(node, RaceFactory.makeCollector(holder.getLevel(node)));
	}
	
	public static boolean goCollector(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1, p);
		Input.menuGo(new MenuGenerator() {
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
						if (Rand.chanceIn(1,30)) {
							Print.println(TrawelColor.PRE_BATTLE +"\"Enough of your games!\"");
							setBasicRagePerson(holder, node, p, "A very angry "+TrawelColor.PRE_BATTLE+p.getName(),TrawelColor.PRE_BATTLE+p.getName() + " attacks you!");
							return true;//out of menu
						}
						if (Rand.chanceIn(1,3)) {
							Oracle.tip("old");
						}
						Print.println("They ignore you.");
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.PRE_BATTLE+"Attack.";
					}

					@Override
					public boolean go() {
						if (p.reallyAttack()) {
							setBasicRagePerson(holder, node, p, "An angry "+TrawelColor.PRE_BATTLE+p.getName(),TrawelColor.PRE_BATTLE+p.getName() + " attacks you!");
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
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.LOCKDOOR.ordinal());
		holder.setStorage(node,Rand.choose("Locked Door","Barricaded Door","Padlocked Door"));
		holder.setForceGo(node, true);
	}
	
	private boolean goLockedDoor(NodeConnector holder,int node) {
		if (holder.isForceGo(node)) {
			if (holder.parent.getOwner() == Player.player) {
				Print.println("You find the keyhole and then unlock the "+holder.getStorageFirstClass(node,String.class)+".");
				holder.setStateNum(node,1);//unlocked once
				holder.findBehind(node,"unlocked door");
			}else {
				final String name = holder.getStorageFirstClass(node,String.class);
				if (holder.getStateNum(node) == 1){//if you owned this prior
					Print.println("Looks like they changed the locks!");
					holder.setStateNum(node,0);//set back to locked so it doesn't say this again on this node
				}
				//only applies burnout on fail to prevent further attempts, no other penalty
				Input.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "Your way is blocked by the "+name+".";
							}});
						if (Player.player.getPerson().hasEffect(Effect.BURNOUT)) {
							list.add(new MenuLine() {

								@Override
								public String title() {
									return TrawelColor.RESULT_ERROR+"You are too burnt out to find a way to open the "+name+".";
								}});
						}else {
							final int difficulty = IEffectiveLevel.attributeChallengeEasy(holder.getLevel(node));
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return TrawelColor.RESULT_WARN+"Break down the "+name+". "+AttributeBox.showPlayerContest(0,difficulty);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(
										Player.player.getPerson().getStrength(),difficulty)
										>=0){
										//broke down door
										Print.println(TrawelColor.RESULT_PASS+"You bash open the "+name+".");
										holder.setStateNum(node,2);//broken open
										holder.setForceGo(node, false);
										String newName = "Broken " +name;
										holder.findBehind(node,newName);
										holder.setStorage(node,newName);
									}else {
										//failed
										Player.player.addPunishment(Effect.BURNOUT);
										Print.println(TrawelColor.RESULT_FAIL+"You fail to bash open the "+name+".");
									}
									return true;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return TrawelColor.RESULT_WARN+"Lockpick the "+name+". "+AttributeBox.showPlayerContest(1,difficulty);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(
										Player.player.getPerson().getDexterity(),difficulty)
										>=0){
										//lockpicked door
										Print.println(TrawelColor.RESULT_PASS+"You pick open the "+name+".");
										holder.setStateNum(node,3);//picked
										holder.setForceGo(node, false);
										String newName = "Picked " +name;
										holder.findBehind(node,newName);
										holder.setStorage(node,newName);
									}else {
										//failed
										Player.player.addPunishment(Effect.BURNOUT);
										Print.println(TrawelColor.RESULT_FAIL+"You fail to lockpick the "+name+".");
									}
									return true;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return TrawelColor.RESULT_WARN+"Cast Knock on the "+name+". "+AttributeBox.showPlayerContest(2,difficulty);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(
										Player.player.getPerson().getClarity(),difficulty)
										>=0){
										//lockpicked door
										Print.println(TrawelColor.RESULT_PASS+"You open the "+name+" with a Knock cantrip.");
										holder.setStateNum(node,4);//opened
										holder.setForceGo(node, false);
										String newName = "Opened " +name;
										holder.findBehind(node,newName);
										holder.setStorage(node,newName);
									}else {
										//failed
										Player.player.addPunishment(Effect.BURNOUT);
										Print.println(TrawelColor.RESULT_FAIL+"Your Knock cantrip on the "+name+" fizzles.");
									}
									return true;
								}});
						}
						list.add(new MenuBack());
						return list;
					}});
				if (holder.getStateNum(node) <= 1) {//if still locked
					Print.println(TrawelColor.RESULT_ERROR+"The door forces you to turn around.");
					NodeConnector.setKickGate();
					return false;
				}
				//door has been resolved but handling code is above
				return false;
			}
		}else {
			if (holder.parent.getOwner() == Player.player) {
				Print.println("You relock the door every time you go by it, but you know where the hole is now so that's easy.");
			}else {
				switch (holder.getStateNum(node)) {
				case 2://broken
					Print.println(
							Rand.choose(
							"The door is broken."
							,"The door is smashed to bits."
							,"The metal on the door is hanging off the splinters."
							,"The lock is intact. The rest of the door isn't."
							)
							);
					break;
				case 3://lockpicked
					Print.println(
							Rand.choose(
							"The lock has been picked."
							,"The lock here has been tampered with."
							,"The tumblers have been stuck together, rendering the lock useless."
							,"The lock is intact- but only externally."
							)
							);
					break;
				case 4://opened with knock
					Print.println(
							Rand.choose(
							"The lock has been opened with a cantrip."
							,"The lock here has been enchanted open."
							,"The tumblers magically slide back to open, rendering the lock useless."
							,"The lock is intact- but not functional."
							)
							);
					break;
				}
				holder.findBehind(node,holder.getStorageFirstClass(node,String.class));
			}
		};
		return false;
	}
	
	/**
	 * use null for random starting, `""` for nothing
	 */
	public static void setPlantSpot(NodeConnector holder,int node, Seed starting) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.PLANT_SPOT.ordinal());
		if (starting == null) {
			starting = Rand.choose(Seed.GROWN_APPLE,Seed.GROWN_BEE,Seed.GROWN_EGGCORN);
		}
		holder.setStorage(node,new PlantSpot(holder.getLevel(node),starting));
	}
	
	public static boolean goCasualPerson(NodeConnector holder,int node) {
		final Boolean[] returnHolder = new Boolean[] {false};
		final String fluff;
		switch (NodeType.getTypeEnum(holder.getTypeNum(node))) {
			default:
				fluff = " is wandering around.";
				break;
			case BEACH:
				fluff = " is combing the beach.";
				break;
		}
		final Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1, p);
		boolean racist = p.isRacist();
		final RaceID r = p.getBag().getRaceID();
		final String name = Print.capFirst(r.name);
		if (racist && holder.getStateNum(node) >= 10) {//pissed off but can recover
			if (r == Player.bag.getRaceID()) {
				//racist won't attack person of same race first
				//calm down a tad, stop force go-ing
				holder.setStateNum(node,9);
				holder.setForceGo(node,false);
				//this 'shady figure' was you before species store
				Print.println("The "+name+" pulls you aside and tells you to watch for shady figures around here.");
			}else {
				//racist is committing hate crimes, continue to basic rager code
				//do rant
				Print.println("\"" +Print.capFirst(
						Oracle.tipStringExt("racistAttack", "a",r.name,r.namePlural,r.adjective,holder.parent.getTown().getName(),Player.bag.getRace().badNameList())
						+"\""));
				Print.println(TrawelColor.PRE_BATTLE+"The racist "+name+" attacks you!");
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() > 0) {
					GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
					return false;
				}else {
					return true;
				}
			}
		}
		Input.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {
					@Override
					public String title() {
						return p.getName() + fluff;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Attempt to talk to the "+name+".";
					}

					@Override
					public boolean go() {
						int anger = holder.getStateNum(node);
						if (racist) {
							if (r == Player.bag.getRaceID()) {
								//meeting a player to be racist with calms them down
								holder.setStateNum(node,Math.max(0,--anger));
								Print.println("\"" +Print.capFirst(
										Oracle.tipStringExt("racistPraise", "a",r.name,r.namePlural,r.adjective, holder.parent.getTown().getName(),Player.bag.getRace().badNameList())
										)+"\"");
							}else {
								//if not same species and angry is at least 2, X in 6 chance to try to assault/murder the player
								//do before processing other triggers so that we can play the attack line instead of the other ones
								if (anger >= 2 && Rand.chanceIn(holder.getStateNum(node),6)) {
									//attack line is handled by code after forcego 
									/*
									extra.println("\"" +extra.capFirst(
											Oracle.tipStringExt("racistAttack", "a",r.name,r.namePlural,r.adjective,holder.parent.getTown().getName(),Player.bag.getRace().badNameList())
											+"\""));
									*/
									//setBasicRagePerson(holder, node, p, "An angry "+extra.PRE_BATTLE +name,extra.PRE_BATTLE+"The racist "+name + " attacks you!");
									holder.setForceGo(node,true);//set force go
									holder.setStateNum(node,20);//set full anger
									return true;//out of menu, not area
								}
								if (Rand.chanceIn(4,5)) {//40% chance to do generic Tips
									if (Rand.chanceIn(1,3)) {//33% chance to play up self and calm down a bit
										holder.setStateNum(node,Math.max(0,--anger));
										Print.println("\"" +Print.capFirst(
												Oracle.tipStringExt("racistPraise", "a",r.name,r.namePlural,r.adjective, holder.parent.getTown().getName(),Player.bag.getRace().badNameList())
												)+"\"");
									}else {//66% chance to do wide insult and get angrier
										holder.setStateNum(node,++anger);
										Print.println("\"" +Print.capFirst(
												Oracle.tipStringExt("racistShun", "a",r.name,r.namePlural,r.adjective,holder.parent.getTown().getName(),Player.bag.getRace().badNameList())
												+"\""));
									}
								}else {
									//20% chance to do racially charged insult and get angrier
									holder.setStateNum(node,++anger);
									Print.println("\"" + Player.bag.getRace().randomInsult() +"\"");
								}
							}
						}else {
							if (p.isAngry()) {
								Print.println("They seem really mad about everything.");
								//always gets angry
								holder.setStateNum(node,++anger);
								//doesn't have initial gate so could happen right away, but lower starting chance
								if (Rand.chanceIn(anger,8)) {
									//set into fighter forever
									setBasicRagePerson(holder, node, p, "An angry "+TrawelColor.PRE_BATTLE +name,TrawelColor.PRE_BATTLE+"The "+name + " attacks you!");
									return true;//out of menu, not area
								}
							}else {
								Print.println("\""+Oracle.tipString("equality")+"\"");
							}
						}
						return false;
					}}
				);
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.PRE_BATTLE+"Attack";
					}

					@Override
					public boolean go() {
						if (p.reallyAttack()) {
							RaceID r = p.getBag().getRaceID();
							if (racist) {
								holder.setForceGo(node,true);//set force go
								holder.setStateNum(node,20);//set full anger
								//setBasicRagePerson(holder, node, p, "An angry "+extra.PRE_BATTLE +name,extra.PRE_BATTLE+"The "+name + " attacks you!");
								//set immediate fight in case they're same race and it would fail the normal way
								//also means that it doesn't play the racistAttack tip, which makes contextual sense since the player is striking first
								Combat c = Player.player.fightWith(p);
								if (c.playerWon() > 0) {
									GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
									returnHolder[0] = false;
								}else {
									returnHolder[0] = true;//indicate to kick out of area
								}
								return true;//out of menu, not area
							}
							if (p.isAngry()) {
								setBasicRagePerson(holder, node, p, "An angry "+TrawelColor.PRE_BATTLE +name,TrawelColor.PRE_BATTLE+"The "+name + " attacks you!");
								return true;//out of menu, not area
							}else {//will not hold it against you
								Combat c = Player.player.fightWith(p);
								if (c.playerWon() > 0) {
									setSimpleDeadRaceID(holder, node, r);
								}
								return true;//out of menu, not area
							}
						}
						return false;
					}});
				list.add(new MenuBack("leave"));
				return list;
			}});
		Networking.clearSide(1);
		return returnHolder[0];
	}
	
	public static boolean goRichGuard(NodeConnector holder,int node) {
		List<Person> peeps = holder.getStorageFirstClass(node, List.class);
		int state = holder.getStateNum(node);
		final Person leader = extra.getNonAddOrFirst(peeps);
		if (state > 1) {//if attacking by default
			if (leader.getFlag(PersonFlag.IS_MOOK)) {
				Print.println(TrawelColor.PRE_BATTLE+"The bodyguard attacks you!");
			}else {
				if (peeps.size() > 1) {
					Print.println(TrawelColor.PRE_BATTLE+"Their bodyguard swoops in to assist!");
				}else {
					Print.println(TrawelColor.PRE_BATTLE+"They attack without backup!");
				}
				
			}
			Combat c = Player.player.massFightWith(peeps);
			if (c.playerWon() > 0) {
				//leader might be the guard now, we don't care
				GenericNode.setSimpleDeadRaceID(holder, node, leader.getBag().getRaceID());
				return false;
			}else {
				Person aleader = extra.getNonAddOrFirst(c.getNonSummonSurvivors());//need to keep leader effectively final sadly
				if (aleader.getFlag(PersonFlag.IS_MOOK) && Rand.chanceIn(1,3)) {
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
		Input.menuGo(new MenuGenerator() {
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
						List<String> poorNames = Arrays.asList(new String[] {"serf","peasant","peon","menial","slave","servant","barbarian","pleb","commoner","poor"});
						if (Player.player.getGold() > leader.getBag().getGold()*10) {//you're so much richer than them that they see you as part of their bigotry
							Print.println("\"" +Print.capFirst(
									Oracle.tipStringExt("racistPraise", "a","merchant","merchants","merchant", holder.parent.getTown().getName(),poorNames)
									)+"\"");
						}else {
							Print.println("\"" +Print.capFirst(
									Oracle.tipStringExt(Rand.choose("racistShun","racistPraise"), "a","merchant","merchants","merchant",holder.parent.getTown().getName(),poorNames)
									)+"\"");
						};
						return false;
					}
					});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return TrawelColor.PRE_BATTLE+"Attack";
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
	
	public static void applyTrappedChamber(NodeConnector holder,int node) {
		morphSetup(holder,node);
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE,true);
		holder.setEventNum(node,Generic.TRAPPED_CHAMBER.ordinal());
		
		Object[] tchamberArray = new Object[2];
		byte[] lootData = new byte[4];
		//0 = str, 1 = dex, 3 = cla
		lootData[0] = (byte) Rand.randRange(0,2);//attrib value of what type of chamber it is, used to scan for traps early
		//if you fail to scan it throws you into a random trap and you discover that trap but must endure it
		//if you then fail to endure it you suffer the burnout + penalty
		//either way the trap gets revealed which makes it easier in the future
		//if you pass you learn an unlearnt trap in order
		lootData[1] = (byte) Rand.randRange(0,2);//reward type
		//lootData[2] = 0;//reward subtype
		switch (lootData[1]) {
			default: case 0://money
				lootData[2] = 0;
				break;
			case 1://guild cache
				lootData[2] = (byte) Rand.randRange(0,3);//merchant, hero, rogue, hunter
				break;
			case 2://aether well with side reward
				lootData[2] = (byte) Rand.randRange(0,6);//0 to 6 flavors and drawbanes
				break;
		}
		lootData[3] = (byte) Rand.randRange(0,trapChamberType[lootData[0]].length-1);//chamber type fluff offset
		//reward amount scale is determined by number of traps, 2/3/4
		int trapNumber = Rand.randRange(2,4);
		byte[][] trapArray = new byte[trapNumber][];
		//each trap needs a byte attribute for stat type
		//each trap uses a random offset value and modulos fluff from that
		//each trap stores if it's revealed or not
		for (int i = 0; i < trapNumber;i++) {
			trapArray[i] = new byte[3];
			trapArray[i][0] = (byte) Rand.randRange(0,2);//0 = str, 1 = dex, 3 = cla
			trapArray[i][1] = (byte) Rand.randRange(0,trapList[trapArray[i][0]].length-1);
			trapArray[i][2] = 0;
		}
		tchamberArray[0] = lootData;
		tchamberArray[1] = trapArray;
		holder.setStorage(node, tchamberArray);
	}

	public static boolean trappedChamber(NodeConnector holder,int node) {
		Object[] totalArray = holder.getStorageAsArray(node);
		byte[] lootArray = (byte[]) totalArray[0];
		byte[][] trapArray = (byte[][]) totalArray[1];
		/**
		 * 0 = not yet interacted
		 * 1 = n/a
		 * 2 = all traps revealed
		 * 3 = looted
		 */
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				int state = holder.getStateNum(node);
				final int level = holder.getLevel(node);
				final int knownTrapDifficulty = IEffectiveLevel.attributeChallengeEasy(level);
				if (state == 3) {
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "You have looted this "+tChamberLookup(lootArray[0],lootArray[3])[0]+".";
						}});
				}else {
					if (Player.player.getPerson().hasEffect(Effect.BURNOUT)) {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return TrawelColor.RESULT_ERROR+"You are too burnt out to take on this "+tChamberLookup(lootArray[0],lootArray[3])[0]+".";
							}});
					}else {
						list.add(new MenuSelect() {
	
							@Override
							public String title() {
								return TrawelColor.PRE_BATTLE+"Attempt to loot the "+tChamberLookup(lootArray[0],lootArray[3])[0]+".";
							}
	
							@Override
							public boolean go() {
								for (int i = 0; i < trapArray.length;i++) {
									if (!handleTrap(holder,node,trapArray[i])){
										Print.println("You failed at looting the "+tChamberLookup(lootArray[0],lootArray[3])[0]+".");
										return false;
									}
								}
								holder.setStateNum(node,3);//set state which will get refreshed above
								//TODO: do loot
								//multiplier based on level and trap amount
								float lootMult = (trapArray.length/3)*IEffectiveLevel.unclean(level);
								//at least some gold is awarded even for different vault types
								int gold = IEffectiveLevel.cleanRangeReward(level,4*trapArray.length,.5f);
								//xp scales differently than most things
								Player.player.getPerson().addXp((int)Math.ceil((trapArray.length/3)*level));
								
								switch (lootArray[1]) {
								case 0: default://gold heavy vault
									Print.println(TrawelColor.RESULT_GOOD+"The vault is filled with money!");
									//8-16 above and 20-40 here, so 28-56 before level scaling and drop off
									gold += IEffectiveLevel.cleanRangeReward(level,10*trapArray.length,.8f);
									break;
								case 1://guild cache
									int gemCount = 0;
									String gType;
									Gem gem;
									boolean themed = false;
									List<DrawBane> bonusDrawBanes = null;
									switch (lootArray[2]) {
									default: case 0://merchant guild
										gType = "Merchant";
										//decent amount of money with low variability
										gold += IEffectiveLevel.cleanRangeReward(level,3*trapArray.length,.8f);
										gem = Gem.EMERALD;
										break;
									case 1://hero
										gType = "Hero";
										//okay amount of money with high variability
										gold += IEffectiveLevel.cleanRangeReward(level,1.5f*trapArray.length,.3f);
										gem = Gem.RUBY;
										bonusDrawBanes = new ArrayList<DrawBane>();
										//knowledge fragment
										bonusDrawBanes.add(DrawBane.KNOW_FRAG);
										break;
									case 2://rogue
										gType = "Rogue";
										//large amount of money with very high variability
										gold += IEffectiveLevel.cleanRangeReward(level,5*trapArray.length,.1f);
										gem = Gem.SAPPHIRE;
										bonusDrawBanes = new ArrayList<DrawBane>();
										//'wealthy' drawbanes
										bonusDrawBanes.add(Rand.choose(DrawBane.GOLD,DrawBane.SILVER));
										bonusDrawBanes.add(Rand.choose(DrawBane.GOLD,DrawBane.SILVER));
										themed = true;
										break;
									case 3://hunter
										gType = "Hunter";
										//low amount of money
										gold += IEffectiveLevel.cleanRangeReward(level,trapArray.length/1.5f,.3f);
										gem = Gem.AMBER;
										//boosted gems
										gemCount += IEffectiveLevel.cleanRangeReward(level,gem.reward(trapArray.length/2.0f, themed),.8f);
										break;
									}
									//+= since above can give more gems for some
									gemCount += IEffectiveLevel.cleanRangeReward(level,gem.reward(trapArray.length/2.0f, themed),.8f);
									Print.println(TrawelColor.RESULT_GOOD+"The vault contains a "+gType + " guild cache!");
									Print.println(TrawelColor.RESULT_PASS+"You find " + gemCount + " " + gem.fancyName(gemCount)+"!");
									gem.changeGem(gemCount);
									if (bonusDrawBanes != null) {
										for (int i = 0; i < bonusDrawBanes.size(); i++) {
											Player.bag.addNewDrawBanePlayer(bonusDrawBanes.get(i));
										}
									}
									break;
								case 2://aether well with side reward
									//base amount of aether, scale up to x2
									int aetherAdd = IEffectiveLevel.cleanRangeReward(level,1000*trapArray.length,.8f);
									String aetherFluff;
									DrawBane drawAdd;
									switch (lootArray[2]) {
										default: case 0:
											aetherFluff = "You find a massive pool of aether with a heart floating in it!";
											drawAdd = DrawBane.BEATING_HEART;
											break;
										case 1:
											aetherAdd*=2;//more aether looted
											aetherFluff = "You find a telescope pointing up into a cloud of aether!";
											drawAdd = DrawBane.TELESCOPE;
											break;
										case 2:
											aetherAdd*=2;//more aether looted
											aetherFluff = "You find a unicorn horn sealing a fountain of aether!";
											drawAdd = DrawBane.UNICORN_HORN;
											break;
										case 3:
											aetherAdd*=2;//more aether looted
											aetherFluff = "You find an abandoned living flame forge fueled by aether!";
											drawAdd = DrawBane.LIVING_FLAME;
											break;
										case 4:
											aetherAdd*=2;//more aether looted
											aetherFluff = "You find a ward which has collected a cloud of aether!";
											drawAdd = DrawBane.PROTECTIVE_WARD;
											break;
										case 5:
											aetherAdd*=1.5;//more aether looted
											aetherFluff = "You find a decaying tree imbued with aether!";
											drawAdd = DrawBane.ENT_CORE;
											break;
										case 6:
											aetherAdd*=2;//more aether looted
											aetherFluff = "You find a stone bursting with aether!";
											drawAdd = DrawBane.CEON_STONE;
											break;
									}
									Print.println(TrawelColor.RESULT_GOOD+aetherFluff+" +"+aetherAdd+ " Aether.");
									Player.bag.addAether(aetherAdd);
									Player.bag.addNewDrawBanePlayer(drawAdd);
									break;
								}
								Player.player.addGold(gold);
								Print.println(TrawelColor.RESULT_PASS+"You loot " + World.currentMoneyDisplay(gold)+"!");
								return false;
							}});
						if (state < 2) {
							
							final int discoverDifficulty = IEffectiveLevel.attributeChallengeMedium(level);
							list.add(new MenuSelect() {
	
								@Override
								public String title() {
									return TrawelColor.PRE_MAYBE_BATTLE+"Attempt to discover traps by "+tChamberLookup(lootArray[0],lootArray[3])[1]+ ". "+ AttributeBox.showPlayerContest(lootArray[0],discoverDifficulty);
								}
	
								@Override
								public boolean go() {
									int playerRoll = Player.player.getPerson().getStatByIndex(lootArray[0]);
									Player.addTime(.3);//examining time
									TrawelTime.globalPassTime();
									if (Player.player.getPerson().contestedRoll(playerRoll,discoverDifficulty) >=0) {
										//passed check, learns traps
										for (int i = 0; i < trapArray.length;i++) {
											if (trapArray[i][2] == 0) {//if trap is not revealed
												trapArray[i][2] = 1;//reveal it
												Print.println(TrawelColor.RESULT_PASS+tChamberLookup(lootArray[0],lootArray[3])[2] +": "+ trapLookup(trapArray[i][0],trapArray[i][1])[4] + " " + AttributeBox.showPlayerContest(trapArray[i][0],knownTrapDifficulty));//print reveal fluff
												break;//stop revealing
											}
											if (i == trapArray.length-1) {//if the last trap is already revealed
												holder.setStateNum(node,2);//set state which will get refreshed above
												//this ensures they know there's no more traps, but to do so they have to successfully pass
												//while already having all traps.
												Print.println("You can find no more traps here!");
											}
										}
									}else {
										//failed, thrown into entirely random trap
										boolean survived = handleTrap(holder,node,trapArray[Rand.randRange(0,trapArray.length-1)]);
									}
									return false;
								}});
						}
					}
					for (int i = 0; i < trapArray.length;i++) {
						if (trapArray[i][2] != 0) {//if trap is revealed
							final int index = i;
							list.add(new MenuLine() {
								@Override
								public String title() {
									return "Known Trap: "+trapLookup(trapArray[index][0],trapArray[index][1])[0] + " " + AttributeBox.showPlayerContest(trapArray[index][0],knownTrapDifficulty);//print name fluff
								}});
						}
					}
				}//end if still lootable else
				list.add(new MenuBack());
				return list;
			}});
		return false;
	}
	
	/**
	 * will edit trapData array as a side effect
	 * <br>
	 * return true if survived
	 * <br>
	 * if failed inflicts punishment
	 */
	private static boolean handleTrap(NodeConnector holder,int node, byte[] trapData) {
		int level = holder.getLevel(node);
		int playerRoll = Player.player.getPerson().getStatByIndex(trapData[0]);
		int againstRoll = 0;
		if (trapData[2] != 0) {//if the player knows the trap already
			againstRoll = IEffectiveLevel.attributeChallengeEasy(level);
		}else {
			//does not know about trap
			againstRoll = IEffectiveLevel.attributeChallengeHard(level);
		}
		//after we encounter a trap, it is revealed either way
		trapData[2] = 1;
		
		String[] trapFluff = trapLookup(trapData[0],trapData[1]);//get the fluff with type and offset
		if (Player.player.getPerson().contestedRoll(playerRoll,againstRoll) >=0) {
			Player.addTime(.5);//half an hour of passing time
			TrawelTime.globalPassTime();
			//passed check
			Print.println(TrawelColor.RESULT_NO_CHANGE_GOOD+trapFluff[3] + " " + AttributeBox.showPlayerContest(trapData[0],againstRoll));
			return true;
		}else {
			Player.addTime(1);//full hour of failing time
			TrawelTime.globalPassTime();
			//failed check, suffer burnout
			Player.player.addPunishment(Effect.BURNOUT);
			Print.println(TrawelColor.RESULT_FAIL+trapFluff[2] + " " + AttributeBox.showPlayerContest(trapData[0],againstRoll));
			TrapPunishment punishment = trapFluff[1] == null ? null : TrapPunishment.valueOf(trapFluff[1]);
			switch (punishment) {//type of trap punishment
			default:
				Print.println("Unknown trap punishment type!");
				break;
			case DAMAGE_KILL:
				Deaths.die("You revive outside the trapped chamber.");
				Print.println(TrawelColor.RESULT_FAIL+"Your equipment is damaged!");
				Player.player.getPerson().addEffect(Effect.DAMAGED);
				break;
			case WOUND_KILL:
				Deaths.die("You revive outside the trapped chamber.");
				Print.println(TrawelColor.RESULT_FAIL+"Your body is broken!");
				Player.player.getPerson().addEffect(Effect.WOUNDED);
				break;
			case FATIGUE:
				Print.println(TrawelColor.RESULT_FAIL+"You are overcome with fatigue!");
				Player.player.getPerson().addEffect(Effect.TIRED);
				break;
			case BEES:
				Print.println(TrawelColor.RESULT_FAIL+"Bees pursue you!");
				Player.player.getPerson().addEffect(Effect.BEES);
				break;
			case CURSE:
				Print.println(TrawelColor.RESULT_FAIL+"You are cursed!");
				Player.player.getPerson().addEffect(Effect.CURSE);
				break;
			case CURSE_KILL:
				Deaths.die("You revive outside the trapped chamber.");
				Print.println(TrawelColor.RESULT_FAIL+"You are cursed!");
				Player.player.getPerson().addEffect(Effect.CURSE);
				break;
			}
			return false;
		}
	}
	
	private static String[] tChamberLookup(byte stat, byte offset) {
		return trapChamberType[stat][(offset)%trapChamberType[stat].length];
	}
	
	//goes to 126 max types since uses only signed byte
	private static final String[][][] trapChamberType = new String[][][] {
		//name, reveal description
		
		//strength chamber types
		new String[][] {
			new String[] {"Submerged Chamber","swimming around","You spot a trap underwater"}
			,new String[] {"Collapsed Room","overturning rubble","You uncover clear evidence of a trap"}
		},
		new String[][] {
			new String[] {"Treasure Vault","opening control panels","You find trap controls under a panel"}
		},
		new String[][] {
			new String[] {"Magical Maze","studying the magic","You realize a rule the maze must follow"}
			,new String[] {"Dimensional Lockbox","stretching the walls","Your shifting reveals a trap through the gaps"}
		}
	};
	
	private static String[] trapLookup(byte stat, byte offset) {
		return trapList[stat][(offset)%trapList[stat].length];
	}
	
	private enum TrapPunishment {
		DAMAGE_KILL, FATIGUE, BEES, CURSE, CURSE_KILL, WOUND_KILL
	}
	//goes to 126 max types since uses only signed byte
	private static final String[][][] trapList = new String[][][] {
		//top level is attribute, then list of traps, then name, failureeffect, killfluff, survivefluff, revealfluff
		//strength traps
		new String[][] {
				new String[] {"Falling Rocks",TrapPunishment.DAMAGE_KILL.name(),"Rocks crush you from above!","You dodge falling rocks!","An overhead vent drops rocks down..."}
				,new String[] {"Closing Walls",TrapPunishment.FATIGUE.name(),"The walls close in and force you to crawl out!","You force the closing walls open!","Hidden pistons force the wall to close on looters..."}
				,new String[] {"Phantom Hands",TrapPunishment.FATIGUE.name(),"Hands erupt from the floor, grab you, then slowly drag you out!","You break free of hands erupting from the floor!","Solid ghost hands burst from the floor to ensnare looters..."}
				,new String[] {"Weighted Door",TrapPunishment.FATIGUE.name(),"You fail to open a very heavy door.","You lift a very heavy door open!","A weighted door has an assist mechanism..."}
		},
		//dexterity traps
		new String[][] {
			new String[] {"Grabbing Lock",TrapPunishment.FATIGUE.name(),"A lock clamps onto you and you struggle to escape!","You quickly pick a lock that clamped down over your body!","An ornate lock bars progress, enchanted to grab looters..."}
			,new String[] {"Springshot Saws",TrapPunishment.WOUND_KILL.name(),"Saws fly out of nowhere and slice you up!","You weave between flying saws!","Resetting springs launch sawblades at looters..."}
			,new String[] {"Honeyed Darts",TrapPunishment.BEES.name(),"Countless small darts sink into your flesh and soon bees appear!","You pluck an onslaught of small darts from your body before the poison can take hold!","A wall of darts inject bee attracting poison..."}
			,new String[] {"Fireball Spit",TrapPunishment.DAMAGE_KILL.name(),"A sudden fireball knocks you over!","You dodge a statue's spit fireball!","A statue shoots a fireball from its mouth..."}
		},
		//clarity traps
		new String[][] {
			new String[] {"Draining Sigil",TrapPunishment.FATIGUE.name(),"A burning sigil saps your strength!","You resist a burning sigil!","A flame sigil steals the strength of looters to power itself..."}
			,new String[] {"Phantom Bees",TrapPunishment.BEES.name(),"Bees invade your mind and force you out!","You disbelieve the phantom bees before they become real!","Illusions trick the target into becoming a beacon for bees..."}
			,new String[] {"Deadly Banshee",TrapPunishment.CURSE_KILL.name(),"A waifishly wail pierces your soul and cuts through your mind!","You endure the wail of a banshee!","A captive banshee is used for their shrieks..."}
			,new String[] {"Shuffling Floor",TrapPunishment.WOUND_KILL.name(),"The floor gives way and you are impaled on spikes!","You dance around the shifting trap floors unto stable ground!","Trap doors shift around and lock in a pattern..."}
			,new String[] {"Maddening Mirror",TrapPunishment.CURSE.name(),"A mirror reflects your worst fears over your body and you faint!","You stare down a magic mirror!","A mirror is enchanted with fear magic..."}
		},
	};
	
	public static final String getTChamberName(NodeConnector holder, int node) {
		Object[] trapChamberArray = holder.getStorageAsArray(node);
		byte[] lootChamberArray = (byte[]) trapChamberArray[0];
		return tChamberLookup(lootChamberArray[0],lootChamberArray[3])[0]+".";
	}
	
	public static final String getTChamberInteract(NodeConnector holder, int node) {
		Object[] trapChamberArray = holder.getStorageAsArray(node);
		byte[] lootChamberArray = (byte[]) trapChamberArray[0];
		return "Enter the " +tChamberLookup(lootChamberArray[0],lootChamberArray[3])[0];
	}
	
	/**
	 * unlike other generic applies, this doesn't wipe all data, since some bosses use StateNum to determine setup
	 * <br>
	 * needs data in storage to determine which boss to apply
	 */
	public static final void applyBoss(NodeConnector holder, int node) {
		holder.setForceGo(node,false);//safety measure
		holder.setFlag(node,NodeFlag.SILENT_FORCEGO_POSSIBLE,false);//safety measure
		holder.setFlag(node,NodeFlag.GENERIC_OVERRIDE, true);
		holder.setEventNum(node,Generic.BOSS.ordinal());
		BossNode.applyBoss(holder, node);
		
	}

	@Override
	public int rollRegrow() {
		throw new RuntimeException("generic node can't be rolled for regrowth");
	}

}
