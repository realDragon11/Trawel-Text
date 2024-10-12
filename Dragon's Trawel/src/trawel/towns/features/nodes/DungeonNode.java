package trawel.towns.features.nodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.battle.Combat;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.LootTables;
import trawel.helper.methods.LootTables.LootTheme;
import trawel.helper.methods.LootTables.LootType;
import trawel.helper.methods.extra;
import trawel.helper.methods.randomLists;
import trawel.personal.AIClass;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.contexts.World;
import trawel.towns.features.fort.elements.SubSkill;

public class DungeonNode implements NodeType{

	private WeightedTable dungeonGuardRoller, dungeonLootRoller, dungeonNoneRoller, dungeonEntryRoller, dungeonRegrowRoller;
	
	/*
	 * FOR MULTIFIGHT DUNGEONS ONLY
	 * 2: single guard
	 * 3: multi guard
	 */
	private static final byte[] GUARD_NUMBERS = new byte[] {2,3};
	
	/*
	 * 5/6: chest/mimic
	 * 7/8: statue/living statue
	 * 9: trapped treasure chamber
	 */
	private static final byte[] LOOT_NUMBERS = new byte[] {5,6,7,8,9};
	
	public static final int BOSS_FATESPINNER = 10, BOSS_OLDQUEEN = 11, BOSS_YORE = 12, BOSS_VARIABLE = 13;
	
	public DungeonNode() {
		dungeonGuardRoller = new WeightedTable(new float[] {1f,4f});
		dungeonLootRoller = new WeightedTable(new float[] {3f,1f,1.5f,.5f,.3f});
		dungeonNoneRoller = new WeightedTable(new float[] {
				0f,//1 ladder
				2f,//2 single guard
				0f,//3 multi guard
				.1f,//4 door
				.5f,//5 chest
				.5f,//6 mimic
				.1f,//7 statue
				.5f,//8 living statue
				.3f,//9 trapped treasure chamber
				0f,//10 BOSS: fatespinner
				0f,//11 BOSS: old queen
				0f,//12 BOSS: yore
				0f,//13 MINIBOSS: variable gate reward
		});
		//unlike other areas, the entrance is more locked down rather than less locked down, to protect the more frequently exposed loot later on
		dungeonEntryRoller = new WeightedTable(new float[] {
				0f,//1 ladder
				4f,//2 single guard
				0f,//3 multi guard
				1f,//4 door
				0f,//5 chest
				0f,//6 mimic
				0f,//7 statue
				0f,//8 living statue
				0f,//9 trapped treasure chamber
				0f,//10 BOSS: fatespinner
				0f,//11 BOSS: old queen
				0f,//12 BOSS: yore
				0f,//13 MINIBOSS: variable gate reward
		});
		dungeonRegrowRoller = new WeightedTable(new float[] {
				0f,//1 ladder
				3f,//2 single guard
				0f,//3 multi guard
				0f,//4 door
				.1f,//5 chest
				.5f,//6 mimic
				.1f,//7 statue
				.5f,//8 living statue
				0f,//9 trapped treasure chamber
				0f,//10 BOSS: fatespinner
				0f,//11 BOSS: old queen
				0f,//12 BOSS: yore
				0f,//13 MINIBOSS: variable gate reward
		});
	}
	
	@Override
	public int rollRegrow() {
		return 1+dungeonRegrowRoller.random(Rand.getRand());
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		byte idNum;
		if (guessDepth == 0) {
			idNum = 4;//starting node can only be a door
		}else {
			if (guessDepth == 1) {//first set of nodes after entrance are more blocking
				idNum = (byte) (dungeonEntryRoller.random(Rand.getRand())+1);//starts at 1, ladder
			}else {
				idNum = (byte) (dungeonNoneRoller.random(Rand.getRand())+1);//starts at 1, ladder
			}
		}
		int ret = holder.newNode(NodeType.NodeTypeNum.DUNGEON.ordinal(),idNum,tier);
		holder.setFloor(ret, guessDepth);
		return ret;
	}

	@Override
	public int generate(NodeConnector holder,int from, int size, int tier) {
		int made = getNode(holder,from,from == 0 ? 0 : holder.getFloor(from)+1,tier);
		if (size < 2) {
			return made;
		}
		int split = Rand.randRange(1,Math.min(size,3));
		int i = 0;
		int sizeLeft = size-1;
		//now more even, as with new groves, but less likely to fill it entirely
		int baseSize = sizeLeft/split;
		sizeLeft-=baseSize*split;
		while (i < split) {
			int sizeRemove = sizeLeft > 2 ? Rand.randRange(1,sizeLeft-1) : 0;
			sizeLeft-=sizeRemove;
			int tempLevel = tier;
			if (Rand.chanceIn(1,10)) {
				tempLevel++;
			}
			int n = generate(holder,made,sizeRemove+baseSize,tempLevel);
			holder.setMutualConnect(made, n);
			i++;
		}
		return made;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector holder = new NodeConnector(owner);
		switch (owner.shape) {
		case NONE: generate(holder,0,size, tier);
			return holder.complete(owner);
		case TOWER:
			int curSize = 1;
			List<List<Integer>> floors = new ArrayList<List<Integer>>();
			List<Integer> curFloor;
			int stair;
			int curStair;
			int start = getNode(holder,0,0,tier);
			holder.setFloor(start,0);
			stair = start;
			int levelUp = 0;
			int floor = 0;
			int lastNode;
			int curNode;
			int stair_level = -10;
			//DOLATER: fix order of nodes if that is still an issue
			while (curSize < size) {
				lastNode = stair;
				stair_level = holder.getLevel(stair);
				if (++levelUp >= 3) {
					levelUp =0;
					++stair_level;
				}
				curStair = getNode(holder,stair,floor,stair_level);//stair as from is tentative
				floor++;
				holder.setStair(curStair);
				holder.setEventNum(curStair, 1);
				
				stair_level = holder.getLevel(curStair);
				
				curFloor = new ArrayList<Integer>();
				for (int j = 0; j < 2;j++) {
					for (int i = 0;i <2; i++) {
						curNode = getNode(holder,0,floor,stair_level);
						//holder.setFloor(curNode, floor);//already set in getNode by the guess
						holder.setMutualConnect(lastNode, curNode);
						//lastNode.reverseConnections();
						lastNode = curNode;
						curFloor.add(lastNode);
						if (i == 1) {
							holder.setMutualConnect(lastNode, curStair);
						}
						if (floor > 1 && floors.size()%2==0) {//every other floor past the first two depths, chance to overwrite what spawned
							//guard floor
							if (Rand.chanceIn(3,4)) {//linking nodes have a 3/4ths chance to become a guard of some sort
								holder.setEventNum(lastNode,GUARD_NUMBERS[dungeonGuardRoller.random(Rand.getRand())]);
							}
						}else {
							if (Rand.chanceIn(1,2)) {//linking nodes have a 50% chance to become a loot of some sort
								//might not be a safe loot
								holder.setEventNum(lastNode,LOOT_NUMBERS[dungeonLootRoller.random(Rand.getRand())]);
							}
						}
						floor++;
					}
					floor-=2;
					//curStair.getConnects().add(lastNode);
					
					lastNode = stair;
				}
				floor +=10;
				curFloor.add(curStair);
				floors.add(curFloor);
				curSize +=curFloor.size();
				//reverse order of stair connects
				//stair.reverseConnections();
				//curStair.setFloor(floor);
				holder.setFloor(curStair, floor);
				
				//move onto next floor
				stair = curStair;
			}
			floor+=10;
			int b = getNode(holder, 0, floor, stair_level+2);
			holder.setMutualConnect(b, stair);
			holder.setFloor(b, floor);
			switch (holder.parent.bossType()) {
			case FATESPINNER:
				holder.setEventNum(b, BOSS_FATESPINNER);
				break;
			case OLD_QUEEN:
				holder.setEventNum(b, BOSS_OLDQUEEN);
				break;
			default:
				throw new RuntimeException("Invalid boss type for tower: "+holder.parent.getName());
			}
			/*
			stair.reverseConnections();
			stair.getConnects().add(b);
			stair.reverseConnections();
			*/
			for (List<Integer> fl: floors) {
				for (Integer f: fl) {
					List<Integer> connects = holder.getConnects(f);
					int isize = connects.size()-1;
					int[] pass = new int[isize+1];
					for (int i = isize; i >=0 ;i--) {
						int lowest = 256;
						int erasespot = -1;
						int low_loc = -1;
						for (int j = isize; j >=0;j--) {
							int cur = connects.get(j);
							if (cur == 256) {
								continue;
							}
							int cur_num = holder.getFloor(cur);
							if (lowest > cur_num) {
								low_loc = cur;
								lowest = cur_num;
								erasespot = j;
							}
						}
						assert low_loc > 0;
						pass[i] = low_loc;
						connects.set(erasespot, 256);
					}
					holder.setConnects(f,pass);
				}
				
			}
			return holder.complete(owner);
		case RIGGED_DUNGEON:
			int max_level = tier+8;
			int start_level = tier;
			int path_length_weak = 10;
			int weak_end_level = tier+3;
			int path_length_tough = 4;
			int tough_end_level = tier+6;
			int fight_room = getNode(holder, 0, 0, max_level);
			switch (holder.parent.bossType()) {
			case YORE:
				holder.setEventNum(fight_room, BOSS_YORE);
				break;
			default:
				throw new RuntimeException("Invalid boss type for rigged dungeon: "+holder.parent.getName());
			}
			Dungeon keeper = (Dungeon) owner;
			keeper.setupBattleCons();
			List<SubSkill> skillcon_list = new ArrayList<SubSkill>();
			
			skillcon_list.add(SubSkill.SCRYING);
			skillcon_list.add(SubSkill.DEATH);
			skillcon_list.add(SubSkill.ELEMENTAL);
			Collections.shuffle(skillcon_list);//random order
			while (!skillcon_list.isEmpty()) {
				int last_node = fight_room;
				boolean tough = Rand.randRange(0, 1) == 0;
				int this_length = tough ? path_length_tough : path_length_weak;
				int this_end_level = tough ? tough_end_level : weak_end_level;
				for (int i = 0; i < this_length;i++) {
					//since i starts at 0, the first node in each will be equal floor to the arena, which is kinda cool
					int cur_node = getNode(holder,fight_room,i,(int)extra.lerp(start_level, this_end_level, ((float)(i))/this_length));
					holder.setMutualConnect(last_node, cur_node);
					if (i == this_length-1) {//battlecon orb
						holder.setEventNum(cur_node, 100);
						keeper.registerBattleConWithNode(skillcon_list.remove(0), cur_node);
					}else {
						if (i == this_length-2) {//guard post
							holder.setLevel(cur_node, holder.getLevel(cur_node)+1);
							holder.setEventNum(cur_node, 2);
						}
					}
					last_node = cur_node;
				}
			}
			return holder.complete(owner);
		case RIGGED_TOWER:
			int t_boss_level = tier+8;
			int t_max_level = tier+6;
			int t_fight_room = getNode(holder, 0, 0, t_boss_level);
			switch (holder.parent.bossType()) {
			case YORE:
				holder.setEventNum(t_fight_room, BOSS_YORE);
				break;
			default:
				throw new RuntimeException("Invalid boss type for rigged tower: "+holder.parent.getName());
			}
			Dungeon t_keeper = (Dungeon) owner;
			t_keeper.setupBattleCons();
			List<SubSkill> t_skillcon_list = new ArrayList<SubSkill>();
			
			t_skillcon_list.add(SubSkill.SCRYING);
			t_skillcon_list.add(SubSkill.DEATH);
			t_skillcon_list.add(SubSkill.ELEMENTAL);
			Collections.shuffle(t_skillcon_list);//random order
			
			int t_areas = t_skillcon_list.size();
			int t_floors = (size/(t_areas*2))+t_areas;//total number of actual 'floors', 2 rooms per floor
			int t_area_size = t_floors/t_areas;//amount of 'floors' in each area, 3 areas * 2 rooms per floor
			int t_cur_floor = 0;
			int t_cur_level = tier;
			
			int t_cur_node1;
			int t_cur_node2;
			
			int t_cur_node1new;
			int t_cur_node2new;
			int last_connector = t_fight_room;
			for (int i = 0;i < t_areas;i++) {
				t_cur_floor++;
				t_cur_level = (int)extra.lerp(tier,t_max_level,((float)t_cur_floor)/t_floors);//set level
				t_cur_node1 = getNode(holder,last_connector,t_cur_floor,t_cur_level);
				t_cur_node2 = getNode(holder,last_connector,t_cur_floor,t_cur_level);
				
				holder.setMutualConnect(t_cur_node1, last_connector);
				holder.setMutualConnect(t_cur_node2, last_connector);
				
				//start at j=1 since we need to make the first rooms work
				for (int j = 1; j < t_area_size; j++) {
					t_cur_floor++;
					t_cur_level = (int)extra.lerp(tier,t_max_level,((float)t_cur_floor)/t_floors);//set level
					t_cur_node1new = getNode(holder,t_cur_node1,t_cur_floor,t_cur_level);
					holder.setMutualConnect(t_cur_node1, t_cur_node1new);
					t_cur_node2new = getNode(holder,t_cur_node2,t_cur_floor,t_cur_level);
					holder.setMutualConnect(t_cur_node2, t_cur_node2new);
					
					t_cur_node1 = t_cur_node1new;
					t_cur_node2 = t_cur_node2new;
				}
				
				//generate skillcon room
				t_cur_floor++;//don't need to set level
				int skillroom = getNode(holder, 0, t_cur_floor, t_cur_level);
				holder.setEventNum(skillroom, 100);
				t_keeper.registerBattleConWithNode(t_skillcon_list.remove(0),skillroom);
				holder.setMutualConnect(t_cur_node2, skillroom);
				holder.setMutualConnect(t_cur_node1, skillroom);
				last_connector = skillroom;
			}
			
			return holder.complete(owner);
		}
		throw new RuntimeException("Invalid Dungeon: "+owner.getName());
	}
	
	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		case 1:
			holder.setForceGo(madeNode,true);
			holder.setStorage(madeNode, Rand.choose("stairs","ladder"));
			break;
		case 5: case 6:
			Person mimic = RaceFactory.makeMimic(holder.getLevel(madeNode));
			holder.setStorage(madeNode, new Object[] {randomLists.randomChestAdjective() + "Chest",mimic});
			break;
		case 2:
			holder.setForceGo(madeNode, true);
			holder.setStorage(madeNode, new Object[] {Rand.choose("Checkpoint","Barricade","Guardpost")
					,RaceFactory.makeDGuard(holder.getLevel(madeNode))});
		break;
		case 3:
			List<Person> list = new ArrayList<Person>();
			int baseLevel = holder.getLevel(madeNode)+1;
			holder.setLevel(madeNode, baseLevel);//now increases node level
			int guardLevel = baseLevel;
			int guardAmount = 4;
			if (Rand.chanceIn(1,4)) {//less, but eliter guards
				guardAmount--;
				for (int i = 0; i < guardAmount;i++) {
					list.add(RaceFactory.makeDGuard(guardLevel+Rand.randRange(1,2)));
				}
			}else {
				int manyCost = 5;
				if (guardLevel > manyCost && Rand.chanceIn(1,6)) {//more weaker guards
					do {
						guardLevel-=manyCost;
						guardAmount++;
					}while (guardLevel > manyCost && guardAmount < 6 && Rand.chanceIn(2,3));
					//populate our list
					for (int i = 0; i < guardAmount;i++) {
						list.add(RaceFactory.makeDGuard(guardLevel));
					}
				}else {
					//no modifiers
					for (int i = 0; i < guardAmount;i++) {
						list.add(RaceFactory.makeDGuard(guardLevel));
					}
				}
			}
			holder.setForceGo(madeNode, true);
			holder.setStorage(madeNode, new Object[] {
					Rand.choose("Large ","Well Lit ","High Security ") +Rand.choose("Checkpoint","Barricade","Guardpost")
					,list});
		break;
		case 4:
			GenericNode.applyLockDoor(holder, madeNode);
			break;
		case 8:
			if (Rand.chanceIn(1, 3)) {
				holder.setForceGo(madeNode, true);
			}
		case 7:
			holder.setStorage(madeNode, RaceFactory.makeStatue(holder.getLevel(madeNode)));
			break;
		case 9://trapped treasure chamber
			GenericNode.applyTrappedChamber(holder,madeNode);
			break;
		case 10://fatespinner
			holder.setStorage(madeNode,new Object[] {BossNode.BossType.FATESPINNER});
			GenericNode.applyBoss(holder, madeNode);
			break;
		case 11://old queen
			holder.setStorage(madeNode,new Object[] {BossNode.BossType.OLD_QUEEN});
			GenericNode.applyBoss(holder, madeNode);
			break;
		case 12://yore
			holder.setStorage(madeNode,new Object[] {BossNode.BossType.YORE});
			GenericNode.applyBoss(holder, madeNode);
			break;
		case 13://variable gate reward
			holder.setStorage(madeNode,new Object[] {BossNode.BossType.VARIABLE_GATE_BOSS,holder.getStateNum(madeNode)});
			GenericNode.applyBoss(holder, madeNode);
			break;
		case 100://skillcon holder
			break;
		}
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1: Networking.sendStrong("PlayDelay|sound_footsteps|1|"); break;
		case 2://single guard
			Person guard = holder.getStorageFirstPerson(node);
			Combat gc = Player.player.fightWith(guard);
			if (gc.playerWon() > 0) {
				GenericNode.setSimpleDeadRaceID(holder, node, guard.getBag().getRaceID());
				return false;
			}else {
				return true;
			}
		case 3://multiguards
			List<Person> guards = holder.getStorageFirstClass(node,List.class);
			List<Person> playerSide = holder.parent.getHelpFighters();
			playerSide.addAll(Player.player.getAllies());
			List<List<Person>> people = new ArrayList<List<Person>>();
			people.add(playerSide);
			people.add(guards);
			Combat bgc = Combat.HugeBattle(holder.getWorld(), people);
			String wasname = holder.getStorageFirstClass(node,String.class);
			if (bgc.playerWon() > 0) {
				GenericNode.setTotalDeadString(holder, node,"Wrecked " +wasname,"Examine Bodies","They are slowly rotting.", "pile of corpses");
				holder.parent.retainAliveFighters(bgc.getNonSummonSurvivors());
				return false;
			}else {
				List<Person> survivors = bgc.getNonSummonSurvivors();
				holder.parent.retainAliveFighters(survivors);//will empty it
				holder.setStorage(node,new Object[] {wasname,survivors});//they don't revive
				return true;
			}
		case 5: return chest(holder, node);
		case 6: return mimic(holder, node);
		case 8: return statue(holder, node);
		case 7: return statueLoot(holder, node);
		
		case 100://skillcon holder
			if (holder.getStateNum(node) == 0) {
				holder.setStateNum(node,1);
				Networking.unlockAchievement("power_orb_smash");
				switch (((Dungeon)holder.parent).requestRemoveBattleCon(node)) {
				default:
					Print.println("You smash the orb of power.");
					break;
				case DEATH:
					Print.println("You smash the orb of power, and the screams of the dead thank you.");
					break;
				case ELEMENTAL:
					Print.println("You smash the orb of power, and the room is briefly brought to a boil.");
					break;
				case SCRYING:
					Print.println("You smash the orb of power, and a vision of an arena flashes in your mind.");
					break;
				}
				BossNode.addRubyPayout(holder, node,.5f);
			}else {
				Print.println("The orb is broken into jagged fragments.");
				holder.findBehind(node,"broken orb");
			}
			return false;
		}
		return false;
	}


	private boolean chest(NodeConnector holder, int node) {
		if (holder.getStateNum(node) != 0) {
			Print.println("The "+holder.getStorageFirstClass(node,String.class)+" has already been opened.");
			holder.findBehind(node,"chest");
			return false;
		}
		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1,p);
		Print.println("[r_warn]Really open the " + holder.getStorageFirstClass(node,String.class) + "?");
		if (Input.yesNo()) {
			holder.setStateNum(node,1);//now unused
			Print.println("You open the chest...");
			LootTables.doLoot(holder.getLevel(node),LootType.UNLOCKED_DUNGEON_CHEST,LootTheme.EXPLORE);
			holder.findBehind(node,"chest");
			String name = holder.getStorageFirstClass(node,String.class);
			GenericNode.setMiscText(holder,node,"Opened "+name,"Examine opened "+name+".","The "+name+" has already been opened.","chest");
			Networking.clearSide(1);
			return false;
		}else {
			Networking.clearSide(1);
			Print.println("You decide not to open it.");
			return false;
		}
	}
	
	private boolean mimic(NodeConnector holder, int node) {

		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1,p);
		Print.println("[r_warn]Really open the " + holder.getStorageFirstClass(node,String.class) + "?");
		if (Input.yesNo()) {
			Print.println("You open the chest...");
			Print.println(TrawelColor.PRE_BATTLE+"The mimic attacks you!");
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				GenericNode.setSimpleDeadRaceID(holder, node,p.getBag().getRaceID());
				return false;
			}else {
				return true;
			}
		}else {
			Print.println("You decide not to open it.");
			return false;
		}
	}


	private boolean statue(NodeConnector holder, int node) {
		int state = holder.getStateNum(node);
		Person p = holder.getStorageFirstPerson(node);
		if (state == 0 && !holder.isForced()) {
			Print.println("Really loot the statue?");
			p.getBag().graphicalDisplay(1, p);
			if (!Input.yesNo() && Rand.chanceIn(1,2)) {//half chance to attack you anyway
				Print.println("You decide not to loot it.");
				Networking.clearSide(1);
				return false;
			}
			Print.println(TrawelColor.PRE_BATTLE+"The statue springs to life and attacks you!");
		}
		if (state == 0) {
			holder.setStateNum(node,1);
			holder.setForceGo(node,true);
		}else {//already attacked
			Print.println("The statue attacks you!");
		}
		
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			GenericNode.setSimpleDeadString(holder, node, "Living Statue");
			return false;
		}else {
			return true;
		}
	}

	private boolean statueLoot(NodeConnector holder, int node) {
		int state = holder.getStateNum(node);
		Person p = holder.getStorageFirstPerson(node);
		if (state == 0) {
			Print.println("Really loot the statue?");
			p.getBag().graphicalDisplay(1, p);
			if (!Input.yesNo()) {//half chance to attack you anyway
				Print.println("You decide not to loot it.");
				Networking.clearSide(1);
				return false;
			}
			Print.println("You loot the statue...");
			AIClass.playerLoot(p.getBag(),true);
			holder.setStateNum(node,1);
			return false;
		}else {
			Print.println("The " + holder.getStorageFirstPerson(node).getBag().getRace().renderName(false) + " statue has already been looted.");
			holder.findBehind(node,"statue");
			return false;
		}
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.VIRGIN,DrawBane.MIMIC_GUTS,DrawBane.CEON_STONE,DrawBane.TELESCOPE,DrawBane.KNOW_FRAG};
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
		// empty
		
	}

	@Override
	public String interactString(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1:
			return "Traverse " +holder.getStorageFirstClass(node,String.class)+".";
		case 5: 
			if (holder.getStateNum(node) != 0) {
				return "Examine Opened " + holder.getStorageFirstClass(node,String.class);
			}
		case 6:
			return "Open the " + holder.getStorageFirstClass(node,String.class);
		case 7: 
			if (holder.getStateNum(node) != 0) {
				return "Looted " + Print.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
			}
		case 8:
			return Print.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
		case 100:
			if (holder.getStateNum(node) != 0) {
				return "Examine broken orb.";
			}else {
				return "Break orb of power.";
			}
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1://ladder etc
			/*if (NodeConnector.currentNode != node) {
				return holder.getStorageFirstClass(node,String.class) + ( 
						holder.getFloor(node) > holder.getFloor(NodeConnector.currentNode)
						? " {up}" : " down");
			}*/
			return holder.getStorageFirstClass(node,String.class);
		case 2://guard
		case 3://guards
			return holder.getStorageFirstClass(node,String.class);
			//door is handled by genericnode
		case 5:
			if (holder.getStateNum(node) != 0) {
				return "Opened " + holder.getStorageFirstClass(node,String.class);
			}
		 case 6:
			return holder.getStorageFirstClass(node,String.class);
		case 7: 
			if (holder.getStateNum(node) != 0) {
				return "Looted " + Print.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
			}
		case 8:
			return Print.capFirst(holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)) + " Statue";
		case 100:
			if (holder.getStateNum(node) != 0) {
				return "Destroyed Orb of Power";
			}else {
				return "Orb of Power";
			}
		}
		return null;
	}
	

}
