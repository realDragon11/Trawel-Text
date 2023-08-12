package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory.CultType;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.World;
import trawel.towns.services.Oracle;

public class MineNode implements NodeType{
	
	private static final int EVENT_NUMBER = 10;
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		//int idNum =-1;
		/*
		if (extra.chanceIn(1, 5)) {
			if (extra.chanceIn(1, 30)) {
				idNum = 1;//vein, generic'd
			}else {
				idNum = 2;//gem vein
			}
			
		}*/
		
		//if (idNum == -1) {
		int idNum = extra.randRange(1,EVENT_NUMBER);
		//}
		
		
		int ret = holder.newNode(NodeType.NodeTypeNum.MINE.ordinal(),idNum,tier);
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
		int made = getNode(holder,from,0,tier);
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
			int n = generate(holder,from,split[j],tempLevel);
			holder.setMutualConnect(made,n);
		}
		return made;
	}
	
	protected int genShaft(NodeConnector holder, int from, int sizeLeft, int tier,int shaftLeft) {
		if (sizeLeft <= 0) {//ran out of resources
			return getNode(holder,from,0,tier);
		}
		//sizeleft means that if we don't have enough left we just burrow into a wall, so to speak
		//this makes ends not 'frayed' as much
		if (shaftLeft > 1 || sizeLeft < 3) {
			//shaft always is same level
			int us = getNode(holder,from,0, tier);
			int next = genShaft(holder,us,sizeLeft-1,tier,shaftLeft-1);
			holder.setMutualConnect(us,next);
			return us;
		}else {//shaft ends normally
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
			size++;
			for (int i = 1;i < size;i++) {
				newNode = getNode(start,lastNode,i,tier+(i/10));
				start.setFloor(newNode,i);
				start.setMutualConnect(newNode, lastNode);
				lastNode = newNode;
			}
			newNode = NodeType.NodeTypeNum.BOSS.singleton.getNode(start, lastNode,size+2, 1+tier+(int)Math.ceil(size/10));
			start.setMutualConnect(newNode, lastNode);
			start.setFloor(newNode,size+2);
			return start.complete(owner);
		}
		throw new RuntimeException("Invalid mine");
	}
	
	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		/*
		case -3: made.name = "sapphire cluster"; made.interactString = "mine sapphires";break;
		case -2: made.name = "ruby cluster"; made.interactString = "mine rubies";break;
		case -1: made.name = "emerald cluster"; made.interactString = "mine emeralds";break;
		case 0: made.name = ""; made.interactString = "";break;*/
		case 1:
			Person p = RaceFactory.getDueler(holder.getLevel(madeNode));
			String warName = extra.capFirst(randomLists.randomWarrior());
			p.setTitle("the "+warName);
			GenericNode.setSimpleDuelPerson(holder,madeNode, p,warName,"Approach " +p.getNameNoTitle() +".","Challenge the "+warName +"?");
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
			/*
			holder.setStorage(madeNode,extra.choose("locked door","barricaded door","padlocked door"));
			holder.setForceGo(madeNode, true);*/
			break;
		case 6:
			String cColor = randomLists.randomPrintableColor();
			holder.setStorage(madeNode, cColor);
			//made.interactString = "examine crystals";
			//made.storage1 = randomLists.randomColor();
			//made.name = "weird " + (String)made.storage1 + " crystals";break;
			break;
		case 7://minecart
			break;
		case 8: 
			holder.setForceGo(madeNode, true);
			//made.name = "ladder"; made.interactString = "traverse ladder"; made.setForceGo(true)
		break;
		case 9: 
			//made.name = "cultists"; made.interactString = "approach cultists";
			//made.storage1 = RaceFactory.getCultist(made.level);
			int cultLevel = holder.getLevel(madeNode);
			int cultAmount = 1;
			if (cultLevel > 3) {
				if (extra.chanceIn(2,3)) {
					cultAmount = 3;
					cultLevel -=2;
				}
			}
			List<Person> cultPeeps = new ArrayList<Person>();
			for (int i = 0;i < cultAmount;i++) {
				if (i == 0) {
					cultPeeps.add(RaceFactory.makeCultistLeader(cultLevel+1,CultType.BLOOD));
					continue;
				}
				Person c = RaceFactory.makeCultist(cultLevel+1,CultType.BLOOD);
				c.setFlag(PersonFlag.IS_MOOK,true);
				cultPeeps.add(c);
			}
			holder.setStorage(madeNode, cultPeeps);
		break;
		case 10:
			Person mugger = RaceFactory.makeMuggerWithTitle(holder.getLevel(madeNode));
			String mugName = mugger.getTitle();
			GenericNode.setBasicRagePerson(holder,madeNode, mugger,mugName,extra.capFirst(mugName) + " attacks you!");
			break;
		}
	}

	@Override
	public boolean interact(NodeConnector holder,int node) {
		switch(holder.getEventNum(node)) {
		//case -3: saph1();break;
		//case -2: rubies1();break;
		//case -1: emeralds1();break;
		//case 1: duelist();break;//generic now, should probably do enums by now tbh
		case 2:
			extra.println("You wash yourself in the "+holder.getStorageFirstClass(node,String.class)+".");
			Player.player.getPerson().washAll();break;
		//case 3: goldVein1();break;
		//case 4: mugger1(); if (node.state == 0) {return true;};break;
		case 5: /*
			if (holder.isForceGo(node)) {
				if (holder.parent.getOwner() == Player.player) {
					extra.println("You find the keyhole and then unlock the "+holder.getStorageFirstClass(node,String.class)+".");
					//holder.setForceGo(node, false);
					holder.setStateNum(node,1);//unlocked once
					//so they can change locks, check to make sure that there isn't an infinite loop on this still
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
			break;*/
		case 6: 
			String cColor = holder.getStorageFirstClass(node,String.class);
			extra.println("You examine the " + cColor+ " crystals. They are very pretty.");
			holder.findBehind(node,cColor+ " crystals");
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
		 * 7 = wary but don't want to talk anymore, reverts to 6 if you come back
		 */
		int state = holder.getStateNum(node);
		List<Person> cultists = null;
		Person leader = null;
		boolean partOfCult = Player.player.getPerson().hasPerk(Perk.CULT_LEADER_BLOOD);
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
			state = 1;
			holder.setStateNum(node,6);
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
							return "Listen in on Blood Cult matters";
						}

						@Override
						public boolean go() {
							Oracle.tip("cult");
							return false;
						}});
					list.add(attackCultMenu(holder,node));
					boolean nowOfCult = Player.player.getPerson().hasPerk(Perk.CULT_LEADER_BLOOD);
					if (!nowOfCult) {
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Listen to Offer";
							}

							@Override
							public boolean go() {
								List<Person> cultists = holder.getStorageFirstClass(node,List.class);
								Person leader = extra.getNonAddOrFirst(cultists);
								extra.println(leader.getName() +" exclaims: 'Just perform a small blood rite for us, and we can set you up good with our gods!'");
								if (extra.yesNo()) {
									extra.println("You are stabbed to death.");
									mainGame.die("You rise from the altar!");
									extra.println("The cultists praise you as the second coming of flagjaij! You feel sick, but powerful.");
									Player.player.getPerson().addEffect(Effect.CURSE);
									Player.player.getPerson().setPerk(Perk.CULT_LEADER_BLOOD);
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
							return "Poke around the Altar";
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
							return "Examine the dead Cultists";
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
							return "Listen in on Blood Cult matters";
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
				list.add(new MenuBack("leave the altar area"));
				return list;
			}});
		
		return false;//never kicks out, if attacking sets force go first, which forces you to see the above attack code
	}
	
	protected MenuSelect attackCultMenu(NodeConnector holder,int node) {
		return new MenuSelect() {

			@Override
			public String title() {
				/*
				List<Person> cultists = holder.getStorageFirstClass(node,List.class);
				Person leader = extra.getNonAddOrFirst(cultists);
				if (cultists.size() > 1) {
					return extra.PRE_RED + "Attack " + leader.getName() +" and their acolytes?";
				}
				return extra.PRE_RED + "Attack " + leader.getName() +"?";
				*/
				return "Destroy Cult";
			}

			@Override
			public boolean go() {
				List<Person> cultists = holder.getStorageFirstClass(node,List.class);
				Person leader = extra.getNonAddOrFirst(cultists);
				if (cultists.size() > 1) {
					extra.println(extra.PRE_RED + "Attack " + leader.getName() +" and their acolytes?");
				}else {
					extra.println(extra.PRE_RED + "Attack " + leader.getName() +"?");
				}
				if (extra.yesNo()) {
					holder.setStateNum(node,6);//angy cultists are very madge
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
			return "Examine the " + holder.getStorageFirstClass(node,String.class) + " Crystals.";
		case 7:
			return "Study the Minecart.";
		case 8:
			return "Climb Ladder!";
		case 9:
			if (holder.getVisited(node) < 2) {//if not visited
				return "Enter?";
			}
			return "Enter Sanctum.";
		}
		return null;
	}


	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 2://cleaning water, locked door
			return holder.getStorageFirstClass(node,String.class);
		case 6:
			return holder.getStorageFirstClass(node,String.class) + " Crystals";
		case 7:
			return "Minecart";
		case 8:
			return "Ladder";
		case 9:
			if (holder.getVisited(node) < 2) {//if not visited
				return "Dark Chamber";
			}
			return "Blood Cult Sanctum";
		}
		return null;
	}


}
