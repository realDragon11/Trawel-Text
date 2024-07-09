package trawel.towns.features.nodes;
import java.util.ArrayList;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.Networking;
import trawel.arc.misc.Deaths;
import trawel.battle.Combat;
import trawel.helper.methods.extra;
import trawel.helper.methods.randomLists;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.CultType;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Perk;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.World;
import trawel.towns.features.nodes.BossNode.BossType;
import trawel.towns.features.services.Oracle;

public class MineNode implements NodeType{
	
	/**
	 * these are 0 indexed, and the actual event nums aren't
	 * <br>
	 * so +1
	 */
	private WeightedTable noneMineRoller, hellMineRoller, entryMineRoller, mineRegrowRoller;
	
	public static final int BOSS_HELLBARON = 12;
	
	private int[] startRolls = new int[] {1,2,6,7,8};
	
	public MineNode() {
		noneMineRoller = new WeightedTable(new float[] {
				//1: duelist
				1f,
				//2: water
				.7f,
				//3: vein
				1.5f,
				//4: rare possible vein
				1f,
				//5: door
				.3f,
				//6: crystals
				.4f,
				//7: minecart
				.3f,
				//8: ladder
				.3f,
				//9: cultists
				0.75f,
				//10: mugger
				1.5f,
				//11: trapped chamber
				0.5f,
				//12: hell baron
				0f
		});
		hellMineRoller = new WeightedTable(new float[] {
				//1: duelist
				1.5f,
				//2: water
				.3f,
				//3: vein
				1f,
				//4: rare possible vein
				1f,
				//5: door
				.5f,
				//6: crystals
				.1f,
				//7: minecart
				.1f,
				//8: ladder
				.4f,
				//9: cultists
				1f,
				//10: mugger
				2.5f,
				//11: trapped chamber
				0.1f,
				//12: hell baron
				0f
		});
		entryMineRoller = new WeightedTable(new float[] {
				//1: duelist
				2f,
				//2: water
				.5f,
				//3: vein
				.3f,
				//4: rare possible vein
				0f,
				//5: door
				.5f,
				//6: crystals
				1f,
				//7: minecart
				.5f,
				//8: ladder
				.5f,
				//9: cultists
				.3f,
				//10: mugger
				.4f,
				//11: trapped chamber
				0.25f,
				//12: hell baron
				0f
		});
		//mostly ends up in veins over time with other things regrowing, but also some other stuff indicative of the earth being tapped dry
		mineRegrowRoller = new WeightedTable(new float[] {
				//1: duelist
				3f,
				//2: water
				.2f,
				//3: vein
				1f,
				//4: rare possible vein
				.2f,
				//5: door
				0f,
				//6: crystals
				0f,
				//7: minecart
				.2f,
				//8: ladder
				.2f,
				//9: cultists
				0f,
				//10: mugger
				3f,
				//11: trapped chamber
				0f,
				//12: hell baron
				0f
		});
	}
	
	@Override
	public int rollRegrow() {
		return 1+mineRegrowRoller.random(extra.getRand());
	}
	
	private int getNodeTypeForParentShape(NodeConnector holder,int guessDepth) {
		switch (guessDepth) {
		case 0://start
			//smaller list of things only the starting node can be
			return startRolls[extra.randRange(0,startRolls.length-1)];
		case 1: case 2://entry
			return 1+entryMineRoller.random(extra.getRand());
		}
		switch (holder.parent.getShape()) {
		case ELEVATOR:
			return 1+hellMineRoller.random(extra.getRand());
		default:
		case NONE:
			return 1+noneMineRoller.random(extra.getRand());
		}
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int idNum = getNodeTypeForParentShape(holder,guessDepth);
		int ret = holder.newNode(NodeType.NodeTypeNum.MINE.ordinal(),idNum,tier);
		holder.setFloor(ret, guessDepth);
		return ret;
	}
	

	@Override
	public int generate(NodeConnector holder, int from, int size, int tier) {
		if (size < 3) {
			return genShaft(holder,from,size,tier,10);
			//shafts will also auto terminate if they run out
		}
		if (extra.chanceIn(1,3)) {//mineshaft splitting
			return genShaft(holder,from,size,tier,extra.randRange(2,5));
		}
		int made = getNode(holder,from,from == 0 ? 0 : holder.getFloor(from)+1,tier);
		size--;
		int sizeLeft;
		int[] split;
		if (size < 5) {
			split = new int[size];
			sizeLeft = 0;
			size = 0;
		}else {
			split = new int[extra.randRange(3,Math.min(size,5))];
			size-=split.length;
			sizeLeft = (size)/(split.length+2);
		}
		for (int i = 0; i < split.length;i++) {
			split[i] = sizeLeft;
		}
		sizeLeft = size-(split.length*split.length);
		while (sizeLeft > 0) {
			split[extra.randRange(0,split.length-1)]+=1;
			sizeLeft--;
		}
		for (int j = 0; j < split.length;j++) {
			int tempLevel = tier;
			if (extra.chanceIn(1,20)) {//less likely to tier up without mineshafts
				tempLevel++;
			}
			int n = generate(holder,made,split[j],tempLevel);
			holder.setMutualConnect(made,n);
		}
		return made;
	}
	
	protected int genShaft(NodeConnector holder, int from, int sizeLeft, int tier,int shaftLeft) {
		if (sizeLeft <= 0) {//ran out of resources
			return getNode(holder,from,from == 0 ? 0 : holder.getFloor(from)+1,tier);
		}
		//sizeleft means that if we don't have enough left we just burrow into a wall, so to speak
		//this makes ends not 'frayed' as much
		if (shaftLeft > 1 || sizeLeft < 3) {
			//shaft always is same level
			int us = getNode(holder,from,from == 0 ? 0 : holder.getFloor(from)+1, tier);
			int next = genShaft(holder,us,sizeLeft-1,tier,shaftLeft-1);
			holder.setMutualConnect(us,next);
			return us;
		}else {//shaft ends normally and we generate a normal node instead
			//always a tier up when ending shaft
			return generate(holder,from,sizeLeft-1,tier+1);
		}
	}

	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector start = new NodeConnector(owner);
		start.parent = owner;
		switch (owner.shape) {
		case NONE: 
			generate(start,0,size, tier);
			return start.complete(owner);
		case ELEVATOR:
			int lastNode = 1;
			int newNode;
			int currentLevel = tier;
			int expectedLevel = tier;
			size++;
			for (int i = 1;i < size;i++) {
				expectedLevel = tier+(i/10);
				//non linear growth, but will be force caught up and can't get too far ahead
				currentLevel = extra.clamp(currentLevel+extra.randRange(0,1),expectedLevel-1,expectedLevel+2);
				newNode = getNode(start,lastNode,i,tier+(i/10));
				start.setFloor(newNode,i);
				if (i != 1) {
					start.setMutualConnect(newNode, lastNode);
				}
				lastNode = newNode;
			}
			if (owner.bossType() != BossType.NONE) {
				currentLevel = Math.max(currentLevel+2, 3+(tier+(size/10)));
				newNode = getNode(start, lastNode,size+2,currentLevel);
				start.setEventNum(newNode,BOSS_HELLBARON);
				start.setMutualConnect(newNode, lastNode);
				start.setFloor(newNode,size+2);
			}
			
			return start.complete(owner);
		}
		throw new RuntimeException("Invalid Mine: "+owner.getName());
	}
	
	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		case 1:
			Person p = RaceFactory.getDueler(holder.getLevel(madeNode));
			String warName = extra.capFirst(randomLists.randomWarrior());
			p.setTitle(randomLists.randomTitleFormat(warName));
			GenericNode.setSimpleDuelPerson(holder,madeNode, p,warName,"Approach " +p.getNameNoTitle() +".","Challenge");
			break;
		case 2: 
			holder.setStorage(madeNode, extra.choose("river","pond","lake","stream"));
			break;
		case 3: //common minerals
			GenericNode.applyGenericVein(holder, madeNode,2);
			break;
		case 4: //rare also allowed
			GenericNode.applyGenericVein(holder, madeNode,3);
			break;
		case 5:
			GenericNode.applyLockDoor(holder, madeNode);
			break;
		case 6://crystals
			String cColor = randomLists.randomPrintableColor();
			holder.setStorage(madeNode, cColor);
			break;
		case 7://minecart
			break;
		case 8://ladder
			holder.setForceGo(madeNode, true);
			break;
		case 9://cultists
			int cultLevel = holder.getLevel(madeNode)+2;
			int mookLevel = Math.max(1,cultLevel-4);
			holder.setLevel(madeNode, cultLevel);
			int cultAmount = extra.randRange(3, 4);
			List<Person> cultPeeps = new ArrayList<Person>();
			for (int i = 0;i < cultAmount;i++) {
				if (i == 0) {
					cultPeeps.add(RaceFactory.makeCultistLeader(cultLevel,CultType.BLOOD));
					continue;
				}
				Person c = RaceFactory.makeCultist(mookLevel,CultType.BLOOD);
				c.setFlag(PersonFlag.IS_MOOK,true);
				cultPeeps.add(c);
			}
			holder.setStorage(madeNode, cultPeeps);
		break;
		case 10:
			Person mugger = RaceFactory.makeMugger(holder.getLevel(madeNode));
			String mugName = randomLists.extractTitleFormat(mugger.getTitle());
			GenericNode.setBasicRagePerson(holder,madeNode, mugger,mugName,"The "+extra.capFirst(mugName) + " attacks you!");
			break;
		case 11://trapped treasure chamber
			GenericNode.applyTrappedChamber(holder,madeNode);
			break;
		case 12://hell baron
			holder.setStorage(madeNode,new Object[] {BossNode.BossType.GENERIC_DEMON_OVERLORD});
			GenericNode.applyBoss(holder, madeNode);
			break;
		}
	}

	@Override
	public boolean interact(NodeConnector holder,int node) {
		switch(holder.getEventNum(node)) {
		case 2:
			extra.println("You wash yourself in the "+holder.getStorageFirstClass(node,String.class)+".");
			Player.player.getPerson().washAll();
			Player.player.getPerson().bathEffects();
			break;
		case 1://duelist
		case 3://vein
		case 4://vein
		case 5://door
		case 10://mugger
		case 11://trapped chamber
			//handled by generic node code
			break;
		case 6: 
			String cColor = holder.getStorageFirstClass(node,String.class);
			extra.println("You examine the " + cColor+ " crystals"+extra.COLOR_RESET+". They are very pretty.");
			holder.findBehind(node,cColor+ " crystals"+extra.COLOR_RESET);
			break;
		case 7: 
			extra.println("You examine the iron minecart. It is on the tracks that travel throughout the mine.");
			holder.findBehind(node,"minecart");
			break;
		case 8: 
			extra.println("You traverse the ladder. This place is like a maze!");
			Networking.sendStrong("PlayDelay|sound_footsteps|1|");
			holder.findBehind(node,"ladder");
			break;
		case 9: return cultists1(holder,node);
		}
		Networking.clearSide(1);
		return false;
	}
	

	/* for Ryou Misaki (nico)*/
	private boolean cultists1(NodeConnector holder,int node) {
		/**
		 * 0 = just met, fresh
		 * 1 = angry (will attack if not part of cult)
		 * 2 = peaceful
		 * 3 = dead
		 * 4 = owned (you made the sacrifice here)
		 * 5 = wary (was angry, now is peaceful)
		 * 6 = force attack (so you can attack wary without them instantly forgiving you)
		 *   turns back into normal attack if you die
		 * 7 = wary but don't want to talk anymore, reverts to 5 if you come back
		 */
		int state = holder.getStateNum(node);
		List<Person> cultists = null;
		Person leader = null;
		boolean partOfCult = Player.player.getPerson().hasPerk(Perk.CULT_CHOSEN_BLOOD);
		if (state != 3) {
			cultists = holder.getStorageFirstClass(node,List.class);
			leader = extra.getNonAddOrFirst(cultists);
			leader.getBag().graphicalDisplay(1, leader);
		}
		if (state == 1 && partOfCult) {
			extra.println("The cultists eye you warily, but they sense a kinship within you...");
			holder.setForceGo(node,false);
			holder.setStateNum(node,5);
			state = 5;
		}
		if (state == 1 || state == 6) {
			Combat c = Player.player.massFightWith(cultists);
			if (c.playerWon() > 0) {
				state = 3;
				holder.setStateNum(node,3);
				holder.setStorage(node,null);
				holder.setForceGo(node,false);
				return false;
			}else {
				extra.println("They desecrate your corpse.");
				state = 1;
				holder.setStateNum(node,1);
				return true;
			}
		}
		if (state == 0) {
			extra.println("The cultists welcome you to their private (friends welcome) altar of blood. It's small, but you sense a power here.");
			state = 2;
			holder.setStateNum(node,2);
		}
		if (state == 7) {
			state = 5;
			holder.setStateNum(node,5);
		}
		
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				int substate = holder.getStateNum(node);//Effectively final!!!! somehow, somewhy, the java gods are smiling
				switch (substate) {
				case 2://peaceful
				case 4://owned MAYBELATER: gifts?
					list.add(new MenuSelect() {
						@Override
						public String title() {
							return "Listen in on Blood Cult matters.";
						}

						@Override
						public boolean go() {
							Oracle.tip("cult");
							return false;
						}});
					list.add(attackCultMenu(holder,node));
					boolean nowOfCult = Player.player.getPerson().hasPerk(Perk.CULT_CHOSEN_BLOOD);
					if (!nowOfCult) {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Listen to Offer.";
							}

							@Override
							public boolean go() {
								List<Person> cultists = holder.getStorageFirstClass(node,List.class);
								Person leader = extra.getNonAddOrFirst(cultists);
								extra.println(leader.getName() +" exclaims: 'Just perform a small blood rite for us, and we can set you up good with our gods!'");
								if (extra.yesNo()) {
									extra.println("You are stabbed to death.");
									Deaths.die("You rise from the altar!");
									extra.println("The cultists praise you as the second coming of flagjaij! You feel sick, but powerful.");
									Player.player.getPerson().addEffect(Effect.CURSE);
									Player.unlockPerk(Perk.CULT_CHOSEN_BLOOD);
									Player.player.hasCult = true;
									Networking.unlockAchievement("cult1");
									holder.setStateNum(node,4);
								}
								return false;
							}});
					}else {
						
					}
					break;
				case 3://dead
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Poke around the Altar.";
						}

						@Override
						public boolean go() {
							if (!holder.findBehind(node,"altar")) {
								extra.println("It's bloody, but not much else.");
							}
							return false;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Examine the dead Cultists.";
						}

						@Override
						public boolean go() {
							if (!holder.findBehind(node,"scarred bodies")) {
								extra.println("Their bodies show signs of scarification and bloodletting.");
							}
							return false;
						}});
					break;
				case 5://wary (you are part of their cult but you attacked them, any order, can repeat)
					list.add(new MenuSelect() {
						@Override
						public String title() {
							return "Listen in on Blood Cult matters.";
						}

						@Override
						public boolean go() {
							if (holder.getStateNum(node) == 7) {
								extra.println("They are silent.");
								return false;
							}
							if (extra.chanceIn(1,3)) {
								extra.println("Their faces are sullen, and they do not speak much. Perhaps it is best for you to leave.");
								holder.setStateNum(node,7);
							}else {
								Oracle.tip("cult");
							}
							return false;
						}});
					list.add(attackCultMenu(holder,node));
					break;
				}
				//we can set force go and just back out, the node's state variable persists across local class lines
				list.add(new MenuBack("Leave the altar area."));
				return list;
			}});
		
		return false;//never kicks out, if attacking sets force go first, which forces you to see the above attack code
	}
	
	protected MenuSelect attackCultMenu(NodeConnector holder,int node) {
		return new MenuSelect() {

			@Override
			public String title() {
				return "Destroy Cult.";
			}

			@Override
			public boolean go() {
				List<Person> cultists = holder.getStorageFirstClass(node,List.class);
				Person leader = extra.getNonAddOrFirst(cultists);
				if (cultists.size() > 1) {
					extra.println(extra.PRE_BATTLE + "Attack " + leader.getName() +" and their "+(cultists.size()-1)+" devout acolytes?");
				}else {
					extra.println(extra.PRE_BATTLE + "Attack " + leader.getName() +"?");
				}
				if (extra.yesNo()) {
					holder.setStateNum(node,6);//angy cultists are very madge
					holder.setForceGo(node, true);
					return true;
				}
				return false;
			}};
	}

	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.LIVING_FLAME,DrawBane.SILVER,DrawBane.GOLD};
	}

	@Override
	public void passTime(NodeConnector holder,int node, double time, TimeContext calling) {
		// empty
	}


	@Override
	public String interactString(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 2:
			return "Wash yourself in the " +holder.getStorageFirstClass(node,String.class)+".";
		case 5:
			return "Look at the " + holder.getStorageFirstClass(node,String.class)+".";
		case 6:
			return "Examine the " + holder.getStorageFirstClass(node,String.class) + " Crystals"+extra.COLOR_RESET+".";
		case 7:
			return "Study the Minecart.";
		case 8:
			return "Climb Ladder!";
		case 9:
			if (hideContents(holder,node)) {//if not visited
				return STR_SHADOW_ROOM_ACT;
			}
			return "Enter Sanctum.";
		}
		return null;
	}

	public static final String 
	STR_SHADOW_ROOM_ACT = "Enter Dark Chamber?",
	STR_SHADOW_ROOM_NAME = "Dark Chamber"
	; 

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 2://cleaning water, locked door
			return holder.getStorageFirstClass(node,String.class);
		case 6:
			return holder.getStorageFirstClass(node,String.class) + " Crystals"+extra.COLOR_RESET;
		case 7:
			return "Minecart";
		case 8:
			return "Ladder";
		case 9:
			if (hideContents(holder,node)) {//if not visited
				return STR_SHADOW_ROOM_NAME;//TODO: more dark chambers
			}
			return "Blood Cult Sanctum";
		}
		return null;
	}
	
	public static boolean hideContents(NodeConnector holder, int node) {
		return (holder.getVisited(node) < 2 && !Player.player.getPerson().hasSkill(Skill.NIGHTVISION));
	}


}
