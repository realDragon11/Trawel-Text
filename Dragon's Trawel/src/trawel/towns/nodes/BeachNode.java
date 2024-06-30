package trawel.towns.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.AIClass;
import trawel.Effect;
import trawel.extra;
import trawel.randomLists;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.IEffectiveLevel;
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
import trawel.towns.World;
import trawel.towns.services.Oracle;

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
				1f,
				//4: variable fluff landmark, static state
				.2f,
				//5: message in a bottle
				1f,
				//6: beachcomber (generic casual with bonus drawbane)
				1f
		});
		beachEntryRoller = new WeightedTable(new float[] {
				//1: skeleton pirate blocker
				0f,
				//2: pirate rager
				0f,
				//3: ashore locked chest
				.5f,
				//4: variable fluff landmark, static state
				1.5f,
				//5: message in a bottle
				1f,
				//6: beachcomber (generic casual with bonus drawbane)
				1f
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
				1f
		});
	}
	
	@Override
	public int rollRegrow() {
		return 1+beachRegrowRoller.random(extra.getRand());
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int id = 1;//since first content node is 1 id, and we add from there, including adding 0
		switch (guessDepth) {
		case 0://start
		case 1://entry
			id+=beachEntryRoller.random(extra.getRand());
			break;
		default:
			id+=beachBasicRoller.random(extra.getRand());
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
			int gateType = extra.randRange(0,2);
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
			GenericNode.setBasicRagePerson(holder,node,pirate,mugName,"The "+extra.capFirst(mugName) + " attacks you!");
			break;
		case 3://ashore locked chest
			holder.setStorage(node,randomLists.randomChestAdjective() + "Chest");
			break;
		case 4://variable fluff landmark, static state
			int fluffType = extra.randRange(0,2);
			holder.setStateNum(node,fluffType);
			switch (fluffType) {
				case 0://sea glass
					//https://en.wikipedia.org/wiki/Sea_glass
					//sea glass tends to certain kinds of colors, but we just use our printable list so that can be updated easier
					holder.setStorage(node,randomLists.randomPrintableColor());
					break;
				case 1://beach rock
					//https://en.wikipedia.org/wiki/Beachrock
					holder.setStorage(node,extra.choose("Shelled ","Petrified ","Smoothed ","Tall "+"Wide ")+extra.choose("Beachrock","Sea Stack","Crag","Ridge","Bluff"));
					break;
				case 2://patch of rocky sand/gravel
					//https://en.wikipedia.org/wiki/Shingle_beach
					holder.setStorage(node,extra.choose("Gravel ","Pebble ","Shingle ","Rocky ")+extra.choose("Patch","Strip"));
					break;
			}
			break;
		case 5://bottle with variable results (statenum determines reward, if any)
			holder.setStateNum(node,extra.choose(0,1,1));
			break;
		case 6://beachcomber
			//https://en.wikipedia.org/wiki/Beachcombing
			GenericNode.setBasicCasual(holder,node,RaceFactory.makeBeachcomber(holder.getLevel(node)));
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
		int routeA = generate(holder,node,(sizeLeft/2) + (extra.chanceIn(1,3) ? 1 : 0),tier + (extra.chanceIn(1,10) ? 1 : 0));
		int routeB = generate(holder,node,(sizeLeft/2) + (extra.chanceIn(1,3) ? 1 : 0),tier + (extra.chanceIn(1,10) ? 1 : 0));
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
				int branchAmount = extra.randRange(3,4);
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
						int subSize = extra.randRange(3,branchSize/2);
						//subbranch types: 0 = none, goto beach normal generate; 1 = cave, make linear cave as with grove; 2 = dungeon, make a small dungeon
						int subType = 0;
						//first sub-branch will always be a beach branch
						if (subNum > 0) {
							//if we roll a dungeon, or if we won't meet quota without one forced per branch remaining
							//first branch is immune to minimum if 3 or less, since 3-0 = 3, but can still roll naturally
							if (extra.chanceIn(1,20) || (dungeonYet == false && dungeonsLeft >= 3-branch)) {
								dungeonYet = true;
								dungeonsLeft--;
								subType = 2;
							}else {
								//dungeon takes priority over cave
								//if we roll a cave, or haven't met quota yet
								if (extra.chanceIn(1,10) || (caveYet == false && cavesLeft >= 3-branch)) {
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
								if (extra.chanceIn(1,3)) {
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
							int bossDNode = NodeType.NodeTypeNum.BOSS.singleton.getNode(start,nodeLast,start.getFloor(nodeLast)+1, dTier);
							start.setMutualConnect(nodeLast,bossDNode);
							start.reverseConnections(nodeLast);
							//state is set to the node that contains the blocker, which is read by the boss generator to try to determine the boss type
							start.setStateNum(bossDNode,subHead);
							
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
			switch (holder.getStateNum(node)) {
				default: case 0:
					return "Locked " + holder.getStorageFirstClass(node, String.class);
				case 1:
					return "Smashed " + holder.getStorageFirstClass(node, String.class);
				case 2:
					return "Picked " + holder.getStorageFirstClass(node, String.class);
				case 3:
					return "Opened " + holder.getStorageFirstClass(node, String.class);
			}
		case 4:
			switch (holder.getStateNum(node)) {
				default: case 0://sea glass
					return holder.getStorage(node) +extra.COLOR_RESET+" Sea Glass";
				case 1://beach rock
				case 2://gravel patch
					return ""+holder.getStorage(node);
			}
		case 5:
			return "Bottle";
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
			return (holder.getStateNum(node) == 0 ? "Open " : "Examine ") + holder.getStorageFirstClass(node, String.class);
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
		}
		return null;
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch (holder.getEventNum(node)) {
		case 1://gatenode blocker
			GateNodeBehavior gnb = holder.getStorageFirstClass(node,GateNodeBehavior.class);
			if (gnb.opened) {//already been opened
				extra.println(gnb.getOpenedText());
				return false;
			}else {
				if (gnb.checkOpened()) {
					//opening!
					extra.println(gnb.getOpeningText());
					return false;
				}else {
					//still locked
					extra.println(gnb.getLockedText());
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
				extra.println("The glass here has been weathered by the ocean waves.");
				break;
			case 1://beach rock
				extra.println("There is a large rock sticking out of the sand here.");
				break;
			case 2://gravel patch
				extra.println("The ground here is more rocky than the softer sand around it.");
				break;
			}
			return false;
		case 5://consumable message in a bottle
			int messageReward = 0;
			switch (holder.getStateNum(node)) {
			case 0:
				extra.println("The bottle is empty.");
				break;
			case 1:
				messageReward = IEffectiveLevel.cleanRangeReward(holder.getLevel(node),RaceFactory.WEALTH_WORKER,.5f);
				extra.println("Inside the bottle there is a message and "+World.currentMoneyDisplay(messageReward)+": \""
						+Oracle.tipStringExt("lootWhaler","a","Whaler","Whalers","Whaler",holder.parent.getTown().getName(),
								Arrays.asList(new String[] {"Whales","Sea Serpents","Ghost Ships"}))+"\"");
				Player.bag.addNewDrawBanePlayer(extra.choose(DrawBane.LIVING_FLAME,DrawBane.TELESCOPE,DrawBane.GOLD));
				break;
			}
			GenericNode.setMiscText(holder, node,"Broken Bottle","Examine broken glass.","There was a message in a bottle here, but not anymore.","broken glass");
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
		// TODO Auto-generated method stub
	}
	
	
	private void ashoreLockedChest(NodeConnector holder, int node) {
		String chestname = holder.getStorageFirstClass(node, String.class);
		switch (holder.getStateNum(node)) {
			default: case 0:
				//only applies burnout on fail to prevent further attempts, no other penalty
				extra.menuGo(new MenuGenerator() {

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
									return extra.RESULT_ERROR+"You are too burnt out to find a way to open the "+chestname+".";
								}});
						}else {
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return extra.RESULT_WARN+"Smash open the "+chestname+". "+AttributeBox.getStatHintByIndex(0);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(
										Player.player.getPerson().getStrength(), IEffectiveLevel.attributeChallengeMedium(holder.getLevel(node)))
										>=0){
										//broke down door
										extra.println(extra.RESULT_PASS+"You smash open the "+chestname+".");
										holder.setStateNum(node,1);//broken open
										beachChestLoot(holder.getLevel(node));
										holder.findBehind(node,chestname);
									}else {
										//failed
										Player.player.getPerson().addEffect(Effect.BURNOUT);
										extra.println(extra.RESULT_FAIL+"You fail to bash open the "+chestname+".");
										holder.findBehind(node,chestname);
									}
									return true;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return extra.RESULT_WARN+"Lockpick the "+chestname+". "+AttributeBox.getStatHintByIndex(1);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(
										Player.player.getPerson().getDexterity(), IEffectiveLevel.attributeChallengeMedium(holder.getLevel(node)))
										>=0){
										//lockpicked door
										extra.println(extra.RESULT_PASS+"You pick open the "+chestname+".");
										holder.setStateNum(node,2);//picked open
										beachChestLoot(holder.getLevel(node));
										holder.findBehind(node,chestname);
									}else {
										//failed
										Player.player.getPerson().addEffect(Effect.BURNOUT);
										extra.println(extra.RESULT_FAIL+"You fail to lockpick the "+chestname+".");
										holder.findBehind(node,chestname);
									}
									return true;
								}});
							list.add(new MenuSelect() {

								@Override
								public String title() {
									return extra.RESULT_WARN+"Cast Knock on the "+chestname+". "+AttributeBox.getStatHintByIndex(2);
								}

								@Override
								public boolean go() {
									if (Player.player.getPerson().contestedRoll(
										Player.player.getPerson().getClarity(), IEffectiveLevel.attributeChallengeMedium(holder.getLevel(node)))
										>=0){
										//lockpicked door
										extra.println(extra.RESULT_PASS+"You open the "+chestname+" with a Knock cantrip.");
										holder.setStateNum(node,3);//opened
										beachChestLoot(holder.getLevel(node));
										holder.findBehind(node,chestname);
									}else {
										//failed
										Player.player.getPerson().addEffect(Effect.BURNOUT);
										extra.println(extra.RESULT_FAIL+"Your Knock cantrip on the "+chestname+" fizzles.");
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
				extra.println("This chest has already been smashed and looted.");
				holder.findBehind(node,chestname);
				return;
			case 2:
				extra.println("This chest has already been picked and looted.");
				holder.findBehind(node,chestname);
				return;
			case 3:
				extra.println("This chest has already been opened and looted.");
				holder.findBehind(node,chestname);
				return;
		}
	}
	
	private void beachChestLoot(int level) {
		switch (extra.randRange(0,3)) {
			default: case 0: case 1://basic loot
				int moneyReward = IEffectiveLevel.cleanRangeReward(level,RaceFactory.WEALTH_WELL_OFF,.7f);
				int aetherReward = IEffectiveLevel.cleanRangeReward(level,500,.5f);
				Player.bag.addAether(aetherReward);
				Player.player.addGold(moneyReward);
				extra.println(extra.RESULT_GOOD+"Inside the chest you find "+World.currentMoneyDisplay(moneyReward) + " and "+aetherReward + " Aether!");
				return;
			case 2://hunter stash
				//silver weapon
				Weapon silvered = new Weapon(level,MaterialFactory.getMat("silver"),extra.choose(WeaponType.MACE,WeaponType.LONGSWORD,WeaponType.BROADSWORD,WeaponType.SPEAR));
				//amber
				int amberAmount = IEffectiveLevel.cleanRangeReward(level,Gem.AMBER.unitSize*2.2f, .5f);
				Gem.AMBER.changeGem(amberAmount);
				extra.println("You find a Hunter's cache with " + amberAmount + " Amber and a "+silvered.getName()+"!");
				AIClass.findItem(silvered, Player.player.getPerson());
				return;
			case 3://misc gem stash
				int emeraldAmount = IEffectiveLevel.cleanRangeReward(level,Gem.EMERALD.unitSize*1.2f, .5f);
				int rubyAmount = IEffectiveLevel.cleanRangeReward(level,Gem.RUBY.unitSize*1.2f, .5f);
				//higher amount because skill based action
				int sapphireAmount = IEffectiveLevel.cleanRangeReward(level,Gem.SAPPHIRE.unitSize*1.8f, .5f);
				Gem.EMERALD.changeGem(emeraldAmount);
				Gem.RUBY.changeGem(rubyAmount);
				Gem.SAPPHIRE.changeGem(sapphireAmount);
				extra.println("You find a Gem cache with " + emeraldAmount + " Emeralds, "+rubyAmount + " Rubies, and " + sapphireAmount + " Sapphires!");
				return;
		}
	}

}
