package trawel.towns.features.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.LootTables;
import trawel.helper.methods.LootTables.LootTheme;
import trawel.helper.methods.LootTables.LootType;
import trawel.helper.methods.randomLists;
import trawel.personal.AIClass;
import trawel.personal.Effect;
import trawel.personal.NPCMutator;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Item;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Gem;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.behaviors.GateNodeBehavior;
import trawel.time.TimeContext;
import trawel.towns.contexts.World;
import trawel.towns.features.services.Oracle;

public class BeachNode implements NodeType {
	
	/**
	 * EntryRoller is used in the first layer (and entrance) of beaches to avoid blocking off the entrance with a fight and low hanging fruit rewards
	 */
	private WeightedTable beachBasicRoller, beachEntryRoller, beachRegrowRoller;
	
	public BeachNode() {
		beachBasicRoller = new WeightedTable(new float[] {
				//1: skeleton pirate blocker
				0f,
				//2: pirate rager
				1f,
				//3: ashore locked chest
				.8f,
				//4: variable fluff landmark, static state
				.2f,
				//5: message in a bottle
				1f,
				//6: beachcomber (generic casual with bonus drawbane)
				1f,
				//7: accumulating harpy nest
				.8f
		});
		beachEntryRoller = new WeightedTable(new float[] {
				//1: skeleton pirate blocker
				0f,
				//2: pirate rager
				0f,
				//3: ashore locked chest
				.4f,
				//4: variable fluff landmark, static state
				1.5f,
				//5: message in a bottle
				1f,
				//6: beachcomber (generic casual with bonus drawbane)
				1f,
				//7: accumulating harpy nest
				.5f
		});
		//beach regrowth avoids placing things that can't regrow
		beachRegrowRoller = new WeightedTable(new float[] {
				//1: skeleton pirate blocker
				0f,
				//2: pirate rager
				1f,
				//3: ashore locked chest
				.5f,
				//4: variable fluff landmark, static state
				0f,
				//5: message in a bottle
				1f,
				//6: beachcomber (generic casual with bonus drawbane)
				1f,
				//7: accumulating harpy nest
				.5f
		});
	}
	
	@Override
	public int rollRegrow() {
		return 1+beachRegrowRoller.random(Rand.getRand());
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int id = 1;//since first content node is 1 id, and we add from there, including adding 0
		switch (guessDepth) {
		case 0://start
		case 1://entry
			id+=beachEntryRoller.random(Rand.getRand());
			break;
		default:
			id+=beachBasicRoller.random(Rand.getRand());
			break;
		}
		int ret = holder.newNode(NodeType.NodeTypeNum.BEACH.ordinal(),id,tier);
		holder.setFloor(ret, guessDepth);
		return ret;
	}
	
	@Override
	public void apply(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 1://beach dungeon blocker
			int gateType = Rand.randRange(0,2);
			Person skelePerson = RaceFactory.makeGateNodeBlocker(holder.getLevel(node),gateType);
			Agent skeleAgent = skelePerson.getMakeAgent(AgentGoal.WORLD_ENCOUNTER);
			holder.parent.getTown().getIsland().getWorld().addReoccuring(skeleAgent);
			GateNodeBehavior behavior = new GateNodeBehavior(skeleAgent,holder.parent,gateType);
			skeleAgent.enqueueBehavior(behavior);
			holder.setForceGo(node,true);
			holder.setStorage(node,behavior);
			break;
		case 2://pirate rager
			Person pirate = RaceFactory.makePirate(holder.getLevel(node));
			String mugName = randomLists.extractTitleFormat(pirate.getTitle());
			GenericNode.setBasicRagePerson(holder,node,pirate,mugName,"The "+Print.capFirst(mugName) + " attacks you!");
			break;
		case 3://ashore locked chest
			holder.setStorage(node,new Object[] {Rand.randRange(0,lockedChestAdjs.length-1),Rand.randRange(0,lockedChestNames.length-1),GenericNode.contestModRoll()});
			break;
		case 4://variable fluff landmark, static state
			int fluffType = Rand.randRange(0,2);
			holder.setStateNum(node,fluffType);
			switch (fluffType) {
				case 0://sea glass
					//https://en.wikipedia.org/wiki/Sea_glass
					//sea glass tends to certain kinds of colors, but we just use our printable list so that can be updated easier
					holder.setStorage(node,randomLists.randomPrintableColor());
					break;
				case 1://beach rock
					//https://en.wikipedia.org/wiki/Beachrock
					holder.setStorage(node,Rand.choose("Shelled ","Petrified ","Smoothed ","Tall "+"Wide ")+Rand.choose("Beachrock","Sea Stack","Crag","Ridge","Bluff"));
					break;
				case 2://patch of rocky sand/gravel
					//https://en.wikipedia.org/wiki/Shingle_beach
					holder.setStorage(node,Rand.choose("Gravel ","Pebble ","Shingle ","Rocky ")+Rand.choose("Patch","Strip"));
					break;
			}
			break;
		case 5://bottle with variable results (statenum determines reward, if any)
			holder.setStateNum(node,Rand.choose(0,1,2,2,2));
			break;
		case 6://beachcomber
			//https://en.wikipedia.org/wiki/Beachcombing
			GenericNode.setBasicCasual(holder,node,RaceFactory.makeBeachcomber(holder.getLevel(node)));
			break;
		case 7://accumulating harpy nest
			//waits 12h to 2d12h for first accumulation chance, starts with 0 times replenished, and the harpy is made
			holder.setStorage(node,new Object[] {12d+Rand.randRange(0f,48f),0,RaceFactory.makeHarpy(holder.getLevel(node))});
			holder.setStateNum(node,1);//occupied but no bonus loot
			break;
		}
	}
	
	@Override
	public int generate(NodeConnector holder, int from, int sizeLeft, int tier) {
		int floor = from == 0 ? 0 : holder.getFloor(from)+1;
		int node = getNode(holder,from,floor,tier);
		sizeLeft--;
		if (sizeLeft < 2) {//if we don't have much left to do anything, return
			return node;
		}
		int nextTier;
		int guessLevel = holder.parent.getLevel() + (floor/2);//expected level up every 2 since beaches are meant to be broad over being deep
		if (tier < (guessLevel-2)) {
			//if more than 2 behind, always tier up
			nextTier = tier+1;
		}else {
			if (tier > guessLevel +1) {
				//if more than 1 ahead, never tier up
				nextTier = tier;
			}else {
				if (tier < guessLevel) {
					//if behind, 66% chance to tier up
					nextTier = tier + (Rand.chanceIn(2,3) ? 1 : 0);
				}else {
					//if on par or ahead, 20% chance to tier up
					nextTier = tier + (Rand.chanceIn(1,5) ? 1 : 0);
				}
			}
		}
		
		int routeA = generate(holder,node,(sizeLeft/2) + (Rand.chanceIn(1,3) ? 1 : 0),nextTier);
		int routeB = generate(holder,node,(sizeLeft/2) + (Rand.chanceIn(1,3) ? 1 : 0),nextTier);
		holder.setMutualConnect(node, routeA);
		holder.setMutualConnect(node, routeB);
		return node;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector start = new NodeConnector(owner);
		start.parent = owner;
		switch (owner.getShape()) {
			case TREASURE_BEACH://minimum rec'd size of 32, is probably more volatile than usual nodefeatures
				//split into 3-4 main quadrants, min 3 each
				int branchAmount = Rand.randRange(3,4);
				int branchSize = Math.max(8,size/branchAmount);
				int dungeonsLeft = 2;//minimum two dungeons
				int cavesLeft = 3;//minimum three caves
				int entry = getNode(start,0,0,tier);
				for (int branch = 0; branch < branchAmount;branch++) {
					int headNode = getNode(start,entry,1,tier);
					int subSizeLeft = branchSize;
					//used to help with trying to force the quota, can only force once per branch
					boolean dungeonYet = false;
					boolean caveYet = false;
					for (int subNum = 0; subNum < 5; subNum++) {
						//max subnum is 5 but will terminate early if running out of space
						int subSize = Rand.randRange(3,branchSize/2);
						//subbranch types: 0 = none, goto beach normal generate; 1 = cave, make linear cave as with grove; 2 = dungeon, make a small dungeon
						int subType = 0;
						//first sub-branch will always be a beach branch
						if (subNum > 0) {
							//if we roll a dungeon, or if we won't meet quota without one forced per branch remaining
							//first branch is immune to minimum if 3 or less, since 3-0 = 3, but can still roll naturally
							if (Rand.chanceIn(1,20) || (dungeonYet == false && dungeonsLeft >= 3-branch)) {
								dungeonYet = true;
								dungeonsLeft--;
								subType = 2;
							}else {
								//dungeon takes priority over cave
								//if we roll a cave, or haven't met quota yet
								if (Rand.chanceIn(1,10) || (caveYet == false && cavesLeft >= 3-branch)) {
									caveYet = true;
									cavesLeft--;
									subType = 1;
								}
							}
						}
						int subHead;
						switch (subType) {
						case 0://normal none beach
							//uses base tier instead of going up by one
							subHead = generate(start,headNode, subSize, tier);
							start.setMutualConnect(headNode,subHead);
							break;
						case 1://linear cave
							int tempTier = tier+1;
							int caveFloor = start.getFloor(headNode)+1;
							subHead = NodeType.NodeTypeNum.CAVE.singleton.getNode(start, headNode, caveFloor, tempTier);
							//set to cave entrance
							start.setEventNum(subHead,1);
							start.setMutualConnect(headNode,subHead);
							int caveLast = subHead;
							for (int i = 1; i < subSize;i++) {
								int caveNext = NodeType.NodeTypeNum.CAVE.singleton.getNode(start,caveLast,++caveFloor, tempTier);
								start.setMutualConnect(caveLast, caveNext);
								start.reverseConnections(caveLast);
								caveLast = caveNext;
								//tier levels up much more often in caves
								if (Rand.chanceIn(1,3)) {
									tempTier++;
								}
							}
							break;
						case 2://small dungeon
							int dTier = tier+1;
							subHead = getNode(start, headNode, start.getFloor(headNode)+1,dTier);
							//set to gate blocker on dungeon
							start.setEventNum(subHead,1);
							//connect subbranch entry node with branch entry node
							start.setMutualConnect(headNode,subHead);
							int nodeLast = subHead;
							for (int i = 0; i < subSize;i++) {
								if (i%2==0) {
									dTier++;//go up a tier every other node
								}
								int newDNode = NodeType.NodeTypeNum.DUNGEON.singleton.getNode(start,nodeLast,start.getFloor(nodeLast)+1, dTier);
								start.setMutualConnect(nodeLast,newDNode);
								start.reverseConnections(nodeLast);
								nodeLast = newDNode;
							}
							//add miniboss at end of each dungeon
							dTier++;
							int bossDNode = NodeType.NodeTypeNum.DUNGEON.singleton.getNode(start,nodeLast,start.getFloor(nodeLast)+1, dTier);
							start.setMutualConnect(nodeLast,bossDNode);
							start.reverseConnections(nodeLast);
							//state is set to the node that contains the blocker, which is read by the boss generator to try to determine the boss type
							start.setStateNum(bossDNode,subHead);
							start.setEventNum(bossDNode,DungeonNode.BOSS_VARIABLE);
							
							break;
						}
						//we decided what branch we want to use fully, subtract out budget
						//will not be exact due to using two generates and not recording how much they make
						subSizeLeft-=subSize;
						//if we're almost out of budget, cancel. Can go slightly over and under budget
						//this can lead to quotas not being met if budget rolls poorly but is unlikely
						if (subSizeLeft <= 2) {
							break;
						}
					}
					
					//shuffle subbranches to avoid consistently lopsided chances
					start.shuffleConnects(headNode);
					start.setMutualConnect(entry,headNode);
				}
				//shuffle branches after since each of them will get different chances of stuff
				start.shuffleConnects(entry);
				
				return start.complete(owner);
			case NONE:
				int noneNode = getNode(start, 0, 0, tier);
				int fourthSize = Math.max(3,size/4);
				for (int i = 0; i < 4;i++) {
					start.setMutualConnect(noneNode, generate(start, noneNode, fourthSize, tier));
				}
				return start.complete(owner);
		}
		throw new RuntimeException("Invalid Beach: "+owner.getName());
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 1://gatenode blocker
			return holder.getStorageFirstClass(node,GateNodeBehavior.class).getNodeName();
		//2 pirate rager, generic
		case 3:
			return getChestName(holder,node);
		case 4:
			switch (holder.getStateNum(node)) {
				default: case 0://sea glass
					return holder.getStorage(node) +TrawelColor.COLOR_RESET+" Sea Glass";
				case 1://beach rock
				case 2://gravel patch
					return ""+holder.getStorage(node);
			}
		case 5:
			return "Bottle";
		case 7://accumulating harpy nest
			if (holder.getStateNum(node) == 0) {
				return "Empty Harpy Nest";
			}
			return Item.getModiferNameColoredCapital(holder.getStateNum(node)) + " Harpy Nest";
		}
		return null;
	}
	
	@Override
	public String interactString(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 1://gatenode blocker
			return holder.getStorageFirstClass(node,GateNodeBehavior.class).getInteractText();
		//2 pirate rager, generic
		case 3://ashore chest
			return (holder.getStateNum(node) == 0 ? "Open " : "Examine ") + getChestName(holder,node);
		case 4://variable fluff landmark, static state
			switch (holder.getStateNum(node)) {
			default: case 0://sea glass
				return "Examine sea glass.";
			case 1://beach rock
				return "Examine rocks.";
			case 2://gravel patch
				return "Examine sand.";
			}
		case 5:
			return "Uncork bottled message.";
		case 7://accumulating harpy nest
			return "Approach nest.";
		}
		return null;
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 1://gatenode blocker
			GateNodeBehavior gnb = holder.getStorageFirstClass(node,GateNodeBehavior.class);
			if (gnb.opened) {//already been opened
				Print.println(gnb.getOpenedText());
				return false;
			}else {
				if (gnb.checkOpened()) {
					//opening!
					Print.println(gnb.getOpeningText());
					return false;
				}else {
					//still locked
					Print.println(gnb.getLockedText());
					NodeConnector.setKickGate();//gate kick back
					return false;
				}
			}
			//all paths return by this point so return would be unreachable
		//2 rager pirate, generic
		case 3://ashore locked chest
			ashoreLockedChest(holder, node);
			return false;
		case 4://variable fluff landmark, static state
			switch (holder.getStateNum(node)) {
			default: case 0://sea glass
				Print.println("The glass here has been weathered by the ocean waves.");
				break;
			case 1://beach rock
				Print.println("There is a large rock sticking out of the sand here.");
				break;
			case 2://gravel patch
				Print.println("The ground here is more rocky than the softer sand around it.");
				break;
			}
			return false;
		case 5://consumable message in a bottle
			int messageReward = 0;
			switch (holder.getStateNum(node)) {
			case 0:
				Print.println("The bottle is empty.");
				break;
			case 1://whaler loot
				messageReward = IEffectiveLevel.cleanRangeReward(holder.getLevel(node),RaceFactory.WEALTH_WORKER,.5f);
				Print.println("Inside the bottle there is a message and "+World.currentMoneyDisplay(messageReward)+": \""
						+Oracle.tipStringExt("lootWhaler","a","Whaler","Whalers","Whaler",holder.parent.getTown().getName(),
								Arrays.asList(new String[] {"Whales","Sea Serpents","Ghost Ships"}))+"\"");
				Player.bag.addNewDrawBanePlayer(Rand.choose(DrawBane.LIVING_FLAME,DrawBane.TELESCOPE,DrawBane.GOLD));
				break;
			case 2://civ letter loot
				messageReward = IEffectiveLevel.cleanRangeReward(holder.getLevel(node),RaceFactory.WEALTH_STANDARD,.5f);
				DrawBane loot = Rand.choose(DrawBane.CLOTH,DrawBane.BLOOD,DrawBane.REPEL);
				String lootName;
				switch (loot){
					case CLOTH:
						lootName = "Cloth";
						break;
					case BLOOD:
						lootName = "Blood";
						break;
					case REPEL:
						lootName = "Repel";
						break;
					default:
						throw new RuntimeException("invalid letter loot for message in a bottle");
				}
				Print.println("Inside the bottle there is a message and "+World.currentMoneyDisplay(messageReward)+": \""
						+Oracle.tipStringExt("lootLetter"+lootName,"a","Lover","Lovers","Lover",holder.parent.getTown().getName(),
								Arrays.asList(new String[] {"Rival","Fraud","Poser"}))+"\"");
				Player.bag.addNewDrawBanePlayer(loot);
				break;
			}
			GenericNode.setMiscText(holder, node,"Broken Bottle","Examine broken glass.","There was a message in a bottle here, but not anymore.","broken glass");
			return false;
		case 7://accumulating harpy nest
			if (holder.getStateNum(node) == 0) {//no harpy present
				Print.println("[r_same]The nest is abandoned.");
				Print.println("[r_warn]Destroy the nest to drive off the harpies?");
				if (Input.yesNo()) {
					GenericNode.setMiscText(holder,node,"Trashed Harpy Nest","Examine destroyed nest.","You smashed this nest to drive off the harpies roosting here.","destroyed nest");
					return false;
				}
			}else {//harpy present
				Print.println("A harpy is tending to their things in the nest.");
				Person harpy = holder.getStorageFirstPerson(node);
				harpy.graphicalFoe();
				if (harpy.reallyAttack()) {
					Combat c = Player.player.fightWith(harpy);
					if (c.playerWon() > 0) {
						holder.setStateNum(node,0);//no harpy present
						//increase amount of times destroyed by 1
						int destructions = (int) holder.getStorageAsArray(node)[1]+1;
						if (destructions > 3 || Rand.chanceIn(destructions,3)) {//hard cap of three harpies per nest
							Print.println("[r_change]The nest was wrecked during the battle.");
							GenericNode.setMiscText(holder,node,"Wrecked Harpy Nest","Examine destroyed nest.","The nest was destroyed by battle.","destroyed nest");
							return false;//exit
						}
						//delete harpy and set next regen for 12h+2d randomly
						holder.setStorage(node,new Object[] {12d+Rand.randRange(0f,48f),destructions,null});
						//player can choose to destroy the nest if not automatically destroyed
						Print.println("[r_warn]Destroy the nest to drive off the harpies?");
						if (Input.yesNo()) {
							GenericNode.setMiscText(holder,node,"Trashed Harpy Nest","Examine destroyed nest.","You smashed this nest to drive off the harpies roosting here.","destroyed nest");
							return false;
						}
						//otherwise can fall through and leave
					}
				}
			}//end harpy present
			//harpy cannot kick player out of node exploration, so always returns false
			return false;
		}
		return false;
	}

	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.WOOD,DrawBane.TELESCOPE,DrawBane.KNOW_FRAG};
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
		switch (holder.getEventNum(node)) {
			case 7://accumulating harpy nest
				Object[] harpyStorage = holder.getStorageAsArray(node);
				if (harpyStorage[0] == null) {
					//can set the double to null to stop checking
					break;
				}
				//subtract the current time from the timer
				double harpyTime = ((double)harpyStorage[0])-time;
				if (harpyTime <= 0) {
					int harpyState = holder.getStateNum(node);
					if (harpyState == 0) {
						//empty, harpy was killed, so just make a new one with a new timer
						//waits 12h to 2d12h for first accumulation chance
						//add the harpy time to account for overshooting the goal time
						//keep the number of times the harpy was killed
						holder.setStorage(node,new Object[] {harpyTime+12d+Rand.randRange(0f,48f),harpyStorage[1],RaceFactory.makeHarpy(holder.getLevel(node))});
						holder.setStateNum(node,1);
						break;
					}else {
						//harpy is present, add to them instead
						Person harpy = holder.getStorageFirstPerson(node);
						boolean added = NPCMutator.mutateAddFindHarpy(harpy);
						if (added) {
							//successfully added, increment harpy counter
							holder.setStateNum(node,harpyState+1);
							//roll a random chance to be the peak of this harpy, otherwise they keep finding loot up to a cap of 11 which is a 'legendary' harpy
							if (Rand.chanceIn(harpyState,11)) {//uses harpy state before adding, so last chance is a 10 in 11 chance to not become legendary
								harpyStorage[0] = null;//cannot find more drawbanes
								break;
							}else {
								//otherwise we can just set the new time stored, we have a reference to the underlying array so use that
								//and the harpy had the drawbane added directly to it
								//add the harpy time to account for overshooting the goal time
								harpyStorage[0] = harpyTime+12d+Rand.randRange(0f,48f);
							}
						}else {
							harpyStorage[0] = null;//cannot find more drawbanes
						}
					}
				}else {
					harpyStorage[0] = harpyTime;//update the stored time with the time passing
				}
				break;
		}
	}
	
	private String[] lockedChestAdjs = new String[]{"Sunbleached","Waterlogged","Barnacled","Ornate"};
	private String[] lockedChestNames = new String[]{"Chest","Chest","Chest","Crate","Crate","Cask","Basket","Amphorae","Box","Case","Container","Strongbox","Trunk","Barrel"};
	
	private String getChestName(NodeConnector holder,int node) {
		final int modifier = (int)holder.getStorageAsArray(node)[2];
		final String name = lockedChestAdjs[(int)holder.getStorageAsArray(node)[0]]+" "+lockedChestNames[(int)holder.getStorageAsArray(node)[1]];
		//broke/picked/open are just fallbacks- it will be converted to misc text anyways
		switch (holder.getStateNum(node)) {
			default:
			case 0://no interaction
				return GenericNode.contestModNameLookup(modifier)+" "+name;
			case 1://broke open
				return "Broken "+GenericNode.contestModNameLookup(modifier)+" "+name;
			case 2://lockpicked open
				return "Picked "+GenericNode.contestModNameLookup(modifier)+" "+name;
			case 3://magicked open
				return "Opened "+GenericNode.contestModNameLookup(modifier)+" "+name;
		}
	}
	
	private void ashoreLockedChest(NodeConnector holder, int node) {
		final String chestname = getChestName(holder,node);
		final String coreName = lockedChestNames[(int)holder.getStorageAsArray(node)[1]].toLowerCase();
		final int modifier = (int)holder.getStorageAsArray(node)[2];
		final int level = holder.getLevel(node);
		switch (holder.getStateNum(node)) {
			default: case 0:
				//only applies burnout on fail to prevent further attempts, no other penalty
				Input.menuGo(new MenuGenerator() {

					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "There is a locked "+chestname+" here.";
							}});
						if (Player.player.getPerson().hasEffect(Effect.BURNOUT)) {
							list.add(new MenuLine() {

								@Override
								public String title() {
									return TrawelColor.RESULT_ERROR+"You are too burnt out to find a way to open the "+coreName+".";
								}});
						}else {
							//base medium strength contest
							final int smashDifficulty = GenericNode.contestModDifficultyLookup(modifier,0,2,level);
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return TrawelColor.RESULT_WARN+"Smash open the "+coreName+". "+AttributeBox.showPlayerContest(0,smashDifficulty);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getStrength(),smashDifficulty)>=0){
										//broke down door
										Print.println(TrawelColor.RESULT_PASS+"You smash open the "+coreName+".");
										holder.setStateNum(node,1);//broken open, unused now
										beachChestLoot(holder.getLevel(node));
										holder.findBehind(node,chestname);
										GenericNode.setMiscText(holder, node,"Smashed "+chestname,"Examine the smashed "+coreName+".","This "+coreName+" has already been smashed and looted.",chestname);
									}else {
										//failed
										Print.println(TrawelColor.RESULT_FAIL+"You fail to bash open the "+coreName+".");
										Player.player.addPunishment(Effect.BURNOUT);
										holder.findBehind(node,chestname);
									}
									return true;
								}});
							//base medium dexterity contest
							final int pickDifficulty = GenericNode.contestModDifficultyLookup(modifier,1,2,level);
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return TrawelColor.RESULT_WARN+"Lockpick the "+coreName+". "+AttributeBox.showPlayerContest(1,pickDifficulty);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getDexterity(),pickDifficulty)>=0){
										//lockpicked door
										Print.println(TrawelColor.RESULT_PASS+"You pick open the "+coreName+".");
										holder.setStateNum(node,2);//picked open, unused now
										beachChestLoot(holder.getLevel(node));
										holder.findBehind(node,chestname);
										GenericNode.setMiscText(holder, node,"Picked "+chestname,"Examine the picked "+coreName+".","This "+coreName+" has already been picked and looted.",chestname);
									}else {
										//failed
										Print.println(TrawelColor.RESULT_FAIL+"You fail to lockpick the "+coreName+".");
										Player.player.addPunishment(Effect.BURNOUT);
										holder.findBehind(node,chestname);
									}
									return true;
								}});
							//base medium clarity contest
							final int knockDifficulty = GenericNode.contestModDifficultyLookup(modifier,2,2,level);
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return TrawelColor.RESULT_WARN+"Cast Knock on the "+coreName+". "+AttributeBox.showPlayerContest(2,knockDifficulty);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getClarity(),knockDifficulty)>=0){
										//lockpicked door
										Print.println(TrawelColor.RESULT_PASS+"You open the "+coreName+" with a Knock cantrip.");
										holder.setStateNum(node,3);//opened, unused now
										beachChestLoot(holder.getLevel(node));
										holder.findBehind(node,chestname);
										GenericNode.setMiscText(holder, node,"Opened "+chestname,"Examine the opened "+coreName+".","This "+coreName+" has already been opened and looted.",chestname);
									}else {
										//failed
										Print.println(TrawelColor.RESULT_FAIL+"Your Knock cantrip on the "+coreName+" fizzles.");
										Player.player.addPunishment(Effect.BURNOUT);
										holder.findBehind(node,chestname);
									}
									return true;
								}});
						}
						list.add(new MenuBack());
						return list;
					}});
				return;
			case 1:
				Print.println("This "+coreName+" has already been smashed and looted.");
				holder.findBehind(node,chestname);
				return;
			case 2:
				Print.println("This "+coreName+" has already been picked and looted.");
				holder.findBehind(node,chestname);
				return;
			case 3:
				Print.println();
				Print.println("This "+coreName+" has already been opened and looted.");
				holder.findBehind(node,chestname);
				return;
		}
	}
	
	private void beachChestLoot(int level) {
		LootTables.doLoot(level,LootType.BEACH_CHEST,LootTheme.SKILLED);
	}

}
