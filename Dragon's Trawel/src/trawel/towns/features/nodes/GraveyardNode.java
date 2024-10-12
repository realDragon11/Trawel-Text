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
import trawel.helper.methods.extra;
import trawel.personal.AIClass;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.AttributeBox;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.types.CleanseSideQuest.CleanseType;
import trawel.quests.types.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.towns.features.nodes.NodeConnector.NodeFlag;
import trawel.towns.features.services.Oracle;

public class GraveyardNode implements NodeType{
	
	/**
	 * these are 0 indexed, and the actual event nums aren't
	 * <br>
	 * so +1
	 */
	private WeightedTable noneGraveyardRoller, entryGraveyardRoller;
	public GraveyardNode() {
		noneGraveyardRoller = new WeightedTable(new float[] {
				//1: gravedigger
				1f,
				//2: graverobber
				1f,
				//3: bats
				1f,
				//4: vampire with bats
				.5f,
				//5: collector
				.8f,
				//6: attacking statue
				1f,
				//7: non attacking statue
				.5f,
				//8: trapped chamber
				.5f
		});
		entryGraveyardRoller = new WeightedTable(new float[] {
				//1: gravedigger
				1f,
				//2: graverobber
				1f,
				//3: bats
				.3f,
				//4: vampire with bats
				.2f,
				//5: collector
				.5f,
				//6: attacking statue
				1f,//interesting to go by them
				//7: non attacking statue
				.5f,
				//8: trapped chamber
				0f
		});
	}
	
	@Override
	public int rollRegrow() {
		throw new RuntimeException("graveyard node can't be rolled for regrowth");
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int idNum;
		switch (guessDepth) {
		case 0://start
			idNum = 1;//for now, starting node will only be a gravedigger
			break;
		case 1: case 2://entry
			idNum = 1+entryGraveyardRoller.random(Rand.getRand());
			break;
		default:
			idNum = 1+noneGraveyardRoller.random(Rand.getRand());
			break;
		}
		int ret = holder.newNode(NodeType.NodeTypeNum.GRAVEYARD.ordinal(),idNum,tier);
		holder.setFloor(ret, guessDepth);
		return ret;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector start = new NodeConnector(owner);
		generate(start,0,size,tier);
		return start.complete(owner);
	}
	
	@Override
	public int generate(NodeConnector holder,int from, int size, int tier) {
		int made = getNode(holder,from,from == 0 ? 0 : holder.getFloor(from)+1,tier);
		//TODO make the graveyard generator set the idnum of many of it's nodes to make a semi-sane progression into the graveyard
		if (size <= 0) {
			return made;
		}
		int split = Rand.randRange(1,Math.min(size,3));
		int i = 0;
		int sizeLeft = size-(split+1);
		int[] dist = new int[split]; 
		while (sizeLeft > 0)//DOLATER: maybe give a better gen type
		{
			dist[Rand.randRange(0,split-1)]++;
			sizeLeft--;
		}
		while (i < split) {
			int tempLevel = tier;
			if (Rand.chanceIn(1,10)) {
				tempLevel++;
			}
			int n = generate(holder,made,dist[i],tempLevel);
			holder.setMutualConnect(made, n);
			i++;
		}
		return made;
	}
	private static final String 
			STR_SHADOW_FIGURE_ACT = "Approach Shadowy Figure",
			STR_SHADOW_FIGURE_NAME = "Shadowy Figure",
			STR_SHADOW_OBJECT_ACT = "Approach Shadowy Object",
			STR_SHADOW_OBJECT_NAME = "Shadowy Object"
			;
	
	//note that you can't use generic nodes if you want shadowy behavior
	
	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		case 1: 
			holder.setStorage(madeNode, RaceFactory.makeGravedigger(holder.getLevel(madeNode)));
			break;
		case 2:
			holder.setStorage(madeNode, RaceFactory.makeGraverobber(holder.getLevel(madeNode)));
			break;
		case 3: 
			int startBLevel = holder.getLevel(madeNode);
			/*
			int batLevel = startBLevel;
			int batAmount = 2;
			while (batAmount < 7) {
				if (batLevel > 3 && extra.chanceIn(3,4)) {
					batAmount+=2;
					batLevel = (int) (startBLevel*(2f/batAmount));
				}else {
					break;
				}
			}
			batLevel = Math.max(1,batLevel);
			
			List<Person> list = new ArrayList<Person>();
			for (int i = 0;i < batAmount;i++) {
				list.add(RaceFactory.makeSwarmBat(batLevel));
			}*/
			holder.setStorage(madeNode,RaceFactory.makeGroupOrDefault(startBLevel,3, 8,
					RaceFactory.getMakerForID(RaceID.B_SWARMBAT), RaceFactory.getMakerForID(RaceID.B_BAT)));
			holder.setForceGo(madeNode, true);
		;break;
		case 4:
			int level = holder.getLevel(madeNode)+1;
			holder.setLevel(madeNode, level);//now increases node level
			float spare = 0;
			if (level > 3) {
				//FIXME: idk how to use this properly yet, plus it needs to be fixed
				if (Rand.chanceIn(1,3)) {
					//bat master
					spare = level/1.2f;
					level = level-2;
				}else {
					///level is same
					spare = level/2f;
				}
			}else {
				spare = 0;
				//vamp = base level, no bats
			}
			Person vamp = RaceFactory.makeVampire(level);
			//vampire can't be fully killed normally so we handle it getting cleanse progress manually later
			vamp.cleanseType = -1;
			List<Person> vampBats = RaceFactory.wrapMakeGroupForLeader(vamp,
					RaceFactory.getMakerForID(RaceID.B_SWARMBAT),spare, 1, 4);
			holder.setStorage(madeNode, vampBats);
			;break;
		case 5:
			GenericNode.applyCollector(holder, madeNode);
			holder.setFlag(madeNode,NodeFlag.GENERIC_OVERRIDE,false);//we call it ourselves
			holder.setEventNum(madeNode, 5);//set event back from generic's, since we call it ourselves
			break;
		case 6://will set the state to the times it is seen until it will attack +10 after seen once
			//will attack instantly if attempting to loot
			holder.setFlag(madeNode,NodeFlag.SILENT_FORCEGO_POSSIBLE,true);
			holder.setForceGo(madeNode, true);
			//fall through
		case 7: //non hostile statue which will show but never attack
			holder.setStorage(madeNode, RaceFactory.makeStatue(holder.getLevel(madeNode)));
			break;
		case 8://trapped chamber
			GenericNode.applyTrappedChamber(holder, madeNode);
			holder.setFlag(madeNode,NodeFlag.GENERIC_OVERRIDE,false);//we call it ourselves
			holder.setEventNum(madeNode,8);//set event back from generic's, since we call it ourselves
			break;
		}
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		String str = interactStringHide(holder,node);
		if (!holder.isForced() && str != null) {
			Print.println(str+"?");
			if (!Input.yesNo()) {
				//unvisit
				holder.setVisited(node,2);//only been at node
				return false;
			}
			//set we know about
			holder.setFlag(node,NodeFlag.UNIQUE_1,true);
		}
		switch(holder.getEventNum(node)) {
		//case -1: Networking.sendStrong("PlayDelay|sound_footsteps|1|"); break;
		case 1: return graveDigger(holder,node);
		case 2: return graveRobber(holder,node);
		case 3: return packOfBats(holder,node);
		case 4: return vampire1(holder,node);
		case 5:	return collector(holder,node);
		case 6: return statue(holder,node);
		case 7: return statueLoot(holder,node);
		case 8:
			Print.println("You enter the crypt.");
			return GenericNode.trappedChamber(holder, node);
		}
		return false;
	}
	
	@Override
	public String interactString(NodeConnector holder, int node) {
		int state = holder.getStateNum(node);
		String nightString = interactStringHide(holder,node);
		if (nightString != null) {
			return nightString;
		}
		switch(holder.getEventNum(node)) {
		case 1:
			return "Approach Gravedigger";
		case 2:
			return "Approach Graverobber";
			//3 is bats, 4 is vampire, neither have interact strings
		case 4://needs a string for nightvison, since it won't force go by default
			return "Approach Lair";
		case 5://collector
			return "Approach " +holder.getStorageFirstPerson(node).getName();
		case 6://attacking statue
			Person attStatue = holder.getStorageFirstPerson(node);
			String attName = Print.capFirst(attStatue.getBag().getRace().renderName(false));
			if (state == 33) {//destroyed
				return "Examine Destroyed " + attName + " Statue";
			}
			return "Loot " +attName + " Statue";
		case 7://non attacking statue
			Person quietStatue = holder.getStorageFirstPerson(node);
			String quietName = Print.capFirst(quietStatue.getBag().getRace().renderName(false));
			if (state == 13) {//destroyed
				return "Examine Looted " + quietName + " Statue";
			}
			return "Loot " +quietName + " Statue";
		case 8://trapped chamber
			return GenericNode.getTChamberInteract(holder, node);
		}
		return null;
	}
	
	/**
	 * 0 = do not hide
	 * <br>
	 * 1 = hide as person
	 * <br>
	 * 2 = hide as object
	 */
	private int hideContents(NodeConnector holder, int node) {
		if (Player.player.getPerson().hasSkill(Skill.NIGHTVISION)) {
			return 0;
		}
		if (holder.getFlag(node,NodeFlag.UNIQUE_1) == true) {
			return 0;
		}
		int event = holder.getEventNum(node);
		switch (event) {
		case 3://bats
			return 0;
		case 1://gravedigger
		case 2://graverobber
		case 4://vampire
		case 5://collector
		case 6://attacking statue
		case 7://normal statue
		case 10://wandering skeleton
			return 1;
		case 8://trapped chamber
		case 9://locked skele coffin
			return 2;
		}
		return 0;
	}
	
	public String interactStringHide(NodeConnector holder, int node) {
		switch (hideContents(holder,node)) {
		case 0: default:
			return null;
		case 1:
			return STR_SHADOW_FIGURE_ACT;
		case 2:
			return STR_SHADOW_OBJECT_ACT;
		}
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch (hideContents(holder,node)) {
		case 1:
			return STR_SHADOW_FIGURE_NAME;
		case 2:
			return STR_SHADOW_OBJECT_NAME;
		}
		int state = holder.getStateNum(node);
		switch(holder.getEventNum(node)) {
		case 2:
			return "A Graverobber";
		case 5: case 1://collector, gravedigger
			return holder.getStorageFirstPerson(node).getName();
		case 3:
			return "Swarm of Bats";
		case 4:
			return "Vampire's Lair";
		case 6://attacking statue
			Person attStatue = holder.getStorageFirstPerson(node);
			String attName = Print.capFirst(attStatue.getBag().getRace().renderName(false));
			if (state == 33) {//destroyed
				return "Destroyed " + attName + " Statue";
			}
			if (state > 30) {
				return "Hostile " + attName + " Statue";
			}
			return attName + " Statue";
		case 7://non attacking statue
			Person quietStatue = holder.getStorageFirstPerson(node);
			String quietName = Print.capFirst(quietStatue.getBag().getRace().renderName(false));
			if (state == 13) {//destroyed
				return "Looted " + quietName + " Statue";
			}
			return quietName + " Statue";
		case 8://trapped chamber
			return "Crypt with " +GenericNode.getTChamberName(holder, node);
		}
		return null;
	}
	
	private boolean packOfBats(NodeConnector holder,int node) {
			Print.println(TrawelColor.PRE_BATTLE+"The bats descend upon you!");
			List<Person> list = holder.getStorageFirstClass(node, List.class);
			Combat c = Player.player.massFightWith(list);
			if (c.playerWon() > 0) {
				GenericNode.setTotalDeadString(holder,node,"Bat Corpses","Examine dead Bats.","Some zombies seem to have taken a few bites.","Bat Bodies");
				//swarm bats do not count as normal bats for cleansing
			return false;
			}else {
				holder.setStorage(node,c.getNonSummonSurvivors());//our get storage first can read this or an array
				return true;
			}
	}


	private boolean graveDigger(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		if (state == 0) {//first meeting, will play nightvision or not
			holder.setStateNum(node,10);
			state = 10;
			Print.println("You come across a weary gravedigger, warding against undead during a break.");
		}
		Person p = holder.getStorageFirstPerson(node);
		p.graphicalFoe();
		if (state == 10) {//fine with
			
			Input.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuLine() {

						@Override
						public String title() {
							return "This is " + p.getName();
						}});
					list.add(new MenuSelect(){

						@Override
						public String title() {
							return "Chat.";
						}

						@Override
						public boolean go() {
							Print.println(p.getName()+" looks up from the mud and answers your greeting.");
							Input.menuGo(new MenuGenerator() {

								@Override
								public List<MenuItem> gen() {
									List<MenuItem> list = new ArrayList<MenuItem>();
									list.add(new MenuLine() {

										@Override
										public String title() {
											return "What would you like to ask about?";
										}});
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "Tips.";
										}

										@Override
										public boolean go() {
											Print.println("\""+Oracle.tipStringExt("gravedigger","a","Gravedigger","Gravediggers","Gravedigger",holder.parent.getTown().getName(),
													Arrays.asList(new String[] {"Vampires","Graverobbers","Skeletons"}))+"\"");
											return false;
										}});
									list.add(new MenuSelect() {

										@Override
										public String title() {
											return "This Graveyard.";
										}

										@Override
										public boolean go() {
											Print.println("\"We are in " + holder.parent.getName() + ". Beware, danger lurks everywhere.\"");
											//tODO: maybe nodefeature lore eventually
											return false;
										}});
									list.add(new MenuBack("Say goodbye."));
									return list;
								}});
							return false;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.PRE_BATTLE+"Mug Them.";
						}

						@Override
						public boolean go() {
							if (p.reallyAttack()) {
								holder.setStateNum(node, 11);
								return true;
							}
							return false;
						}});
					list.add(new MenuBack("Leave"));
					return list;
				}});
			if (holder.getStateNum(node) == 11) {//angry now, could have been selected in above menu
				if (state != 11) {//if just got made angry
					Print.println("They scream bloody murder about vampire thralls!");
					holder.setForceGo(node,true);
				}else {
					Print.println("The Gravedigger attacks you!");
				}
			}else {
				Networking.clearSide(1);
				return false;
			}
			//Person p = holder.getStorageFirstPerson(node);
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				GenericNode.setSimpleDeadPerson(holder, node,p);
				return false;
			}else {
				return true;
			}
		}
		Networking.clearSide(1);
		return false;
	}
	
	private boolean graveRobber(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		if (state == 0) {//first meeting
			holder.setStateNum(node,10);
			state = 10;
			Print.println("A Graverobber is poking around some headstones.");
		}
		Person p = holder.getStorageFirstPerson(node);
		p.graphicalFoe();
		if (state == 10) {//might be fine with
			int react = p.facRep.getReactionAgainst(p,Player.player.getPerson());
			if (react > 0) {
				Print.println("The Graverobber nods to you, but doesn't want to be disturbed. "+TrawelColor.PRE_BATTLE+"Disturb them?");
				if (!Input.yesNo()) {
					Networking.clearSide(1);
					return false;
				}
			}else {
				if (react == 0) {
					Print.println("The Graverobber tells you to leave. "+TrawelColor.PRE_BATTLE+"Attack them?");
					if (!Input.yesNo()) {
						Networking.clearSide(1);
						return false;
					}
				}
			}
			//we didn't leave while we still could, friend
			holder.setStateNum(node,11);
			Print.println(TrawelColor.PRE_BATTLE+"\"Should have left it alone, friend!\"");
		}else {
			Print.println(TrawelColor.PRE_BATTLE+"The Graverobber attacks you!");
		}
		//if we got here, we're fighting
		//it doesn't forcego, however
		
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			//the graveyard takes their face, unlike the gravedigger which still gets their normal body
			GenericNode.setSimpleDeadRaceID(holder, node,p.getBag().getRaceID());
			return false;
		}else {
			Player.player.stealCurrencyLeveled(p,1f);
			return true;
		}
	}

	private boolean vampire1(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		if (state == 0) {
			holder.setStateNum(node,10);
			Print.println("A vampire eyes you from a perch on a tombstone.");
			holder.setForceGo(node, true);
		}
		Print.println(TrawelColor.PRE_BATTLE+"Shouldn't have come to this graveyard, mortal!");
		Combat c = Player.player.massFightWith(holder.getStorageFirstClass(node,List.class));
		
		if (c.playerWon() > 0) {
			//must kill bats to fully kill vampire, so we handle cleanse trigger manually
			Player.player.questTrigger(TriggerType.CLEANSE,CleanseType.VAMPIRE.trigger, 1);
			GenericNode.setTotalDeadString(holder, node,"Vampire Coffin","Examine motes of Grave Dust","There isn't enough here to gather.","coffin");
			return false;
		}else {
			if (extra.getNonAddOrFirst(c.getNonSummonSurvivors()).getFlag(PersonFlag.IS_MOOK)) {
				//if the vampire is dead, the bats don't revive, otherwise they do
				holder.setStorage(node,c.getNonSummonSurvivors());
				//swarm bats do not count as normal bats for cleansing
			}
			return true;
		}

	}
	
	
	/**
	 * states <10 is normal hasn't visited
	 * <br>
	 * states 11 to 20 are silently stalking
	 * <br>
	 * states 21 to 30 are stalking after having seen
	 * <br>
	 * state 31 is first fighting, state 32 is after visibly fighting, and state 33 is destroyed
	 */
	private boolean statue(NodeConnector holder,int node) {
		
		int state = holder.getStateNum(node);
		//extra.println("statue enter: " +state);
		//can have nightvision and not have interacted yet, in which case we need special dialogue
		//will silently forcego to count the timer
		boolean forced = holder.isForced();
		if (state == 20 || state == 30) {//will attack
			state = 31;
		}
		if (state < 10) {
			if (forced) {
				state = Rand.randRange(11,18);
			}else {
				state = Rand.randRange(21,28);
			}
		}else {
			if (state < 30) {
				if (forced) {
					if (Rand.chanceIn(1,8)) {
						Print.println("...Did that statue just move?");
					}
					state++;//will only add this way if forced, also can add if examined for real
				}
			}else {
				//destroyed or attacking
			}
		}
		//leave if silently forced, so it's actually silent (aside from 'just move?')
		if (forced && state != 31) {
			holder.setStateNum(node,state);
			return false;
		}
		
		Person p = holder.getStorageFirstPerson(node);
		//now we can display them
		
		if (state == 33) {
			//destroyed
			Print.println("The destroyed statue slumps in the mud.");
			holder.findBehind(node,"broken statue");
			return false;
		}
		
		if (state != 31) {
			//the code to look at them will occur from interact, but only if not forced
			
			//seen, convert to the seen track, and mess up the timing to mess with
			//them if they checked it due to the 'just move?'
			if (state < 20) {
				if (state < 18) {//if wasn't close
					state = Rand.randRange(21,26);
				}else {//if was close
					state = Rand.randRange(26, 28);
				}
			}
			
			p.getBag().graphicalDisplay(1, p);
			//they approached it, now we can ask to loot it for real
			Print.println(statueLootAsk(p));
			if (!Input.yesNo() && Rand.chanceIn(3,4)) {//25% chance to attack instantly if ignored
				Networking.clearSide(1);
				holder.setStateNum(node,state);
				return false;
			}
		}
		
		//if we get here, they want to loot it or it's attacking them
		//it might also be displayed, but in that case the combat will clean it up for us
		
		if (state <= 31) {//first fight
			Print.println(TrawelColor.PRE_BATTLE+"The statue springs to life and attacks you!");
		}else {
			Print.println(TrawelColor.PRE_BATTLE+"The statue attacks you!");
		}
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			holder.setStateNum(node,33);
			holder.setForceGo(node,false);
			return false;
		}else {
			holder.setStateNum(node,32);
			return true;
		}
	}
	
	private String statueLootAsk(Person p) {
		return ("The motionless statue of a " +p.getBag().getRace().renderName(false) + " overlooks a coffin. Loot it?");
	}
	
	private boolean statueLoot(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		Person p = holder.getStorageFirstPerson(node);
		if (state == 13) {
			Print.println("The " + p.getBag().getRace().renderName(false) + " statue has already been looted.");
			holder.findBehind(node,"statue");
			return false;
		}
		p.getBag().graphicalDisplay(1,p);
		Print.println(statueLootAsk(p));
		if (!Input.yesNo()) {
			Networking.clearSide(1);
			return false;
		}
		Print.println("You loot the statue...");
		holder.setStateNum(node,13);
		AIClass.playerLoot(p.getBag(),true);
		return false;
	}

	private boolean collector(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		if (state == 0) {
			holder.setStateNum(node,10);
			Print.println("An antiquarian is navigating amongst old urns.");
		}
		return GenericNode.goCollector(holder, node);
	}
	
	//locked coffins
	private String[] lockedCoffinAdjs = new String[]{"Sunbleached","Waterlogged","Barnacled","Ornate"};
	private String[] lockedCoffinNames = new String[]{"Chest","Chest","Chest","Crate","Crate","Cask","Basket","Amphorae","Box","Case","Container","Strongbox","Trunk","Barrel"};
	
	private String getLockedCoffinName(NodeConnector holder,int node) {
		final int modifier = (int)holder.getStorageAsArray(node)[2];
		final String name = lockedCoffinAdjs[(int)holder.getStorageAsArray(node)[0]]+" "+lockedCoffinNames[(int)holder.getStorageAsArray(node)[1]];
		return GenericNode.contestModNameLookup(modifier)+" "+name;
	}
	
	private void lockedCoffinSkele(NodeConnector holder, int node) {
		final String coffinName = getLockedCoffinName(holder,node);
		final String coreName = lockedCoffinNames[(int)holder.getStorageAsArray(node)[1]].toLowerCase();
		final int modifier = (int)holder.getStorageAsArray(node)[2];
		final int level = holder.getLevel(node);
		//only applies burnout on fail to prevent further attempts, no other penalty
		Input.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "There is a locked "+coffinName+" here.";
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
							return TrawelColor.RESULT_WARN+"Pry open the "+coreName+". "+AttributeBox.showPlayerContest(0,smashDifficulty);
						}

						@Override
						public boolean go() {
							if (Player.player.getPerson().contestedRoll(Player.player.getPerson().getStrength(),smashDifficulty)>=0){
								//broke down door
								Print.println(TrawelColor.RESULT_PASS+"You pry open the "+coreName+".");
								lockedCoffinSkeleEncounter(holder, node,"Forced "+coffinName,"Examine the forced open "+coreName+".","This "+coreName+" has already been forced open and looted.",coffinName);
							}else {
								//failed
								Player.player.addPunishment(Effect.BURNOUT);
								Print.println(TrawelColor.RESULT_FAIL+"You fail to pry open the "+coreName+".");
								holder.findBehind(node,coffinName);
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
								lockedCoffinSkeleEncounter(holder, node,"Picked "+coffinName,"Examine the picked "+coreName+".","This "+coreName+" has already been picked and looted.",coffinName);
							}else {
								//failed
								Player.player.addPunishment(Effect.BURNOUT);
								Print.println(TrawelColor.RESULT_FAIL+"You fail to lockpick the "+coreName+".");
								holder.findBehind(node,coffinName);
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
								lockedCoffinSkeleEncounter(holder, node,"Opened "+coffinName,"Examine the opened "+coreName+".","This "+coreName+" has already been opened and looted.",coffinName);
							}else {
								//failed
								Player.player.addPunishment(Effect.BURNOUT);
								Print.println(TrawelColor.RESULT_FAIL+"Your Knock cantrip on the "+coreName+" fizzles.");
								holder.findBehind(node,coffinName);
							}
							return true;
						}});
				}
				list.add(new MenuBack());
				return list;
			}}
		);
	}
	
	private boolean lockedCoffinSkeleEncounter(NodeConnector holder, int node, String nodeName,String interact, String interactDesc, String findName) {
		final boolean hasSkele = holder.getStateNum(node) > 0;
		if (hasSkele) {
			//set skeleton
			GenericNode.morphSetup(holder, node);
			holder.setEventNum(node,10);//multi skeleton
			holder.setStorage(node,new Object[] {RaceFactory.makeSkeletonPirate(holder.getLevel(node)),nodeName,interact,interactDesc,findName});
			//no loot, just had a skeleton
			return wanderingSkeleMulti(holder, node);
		}else {
			//loot
			//TODO
			holder.findBehind(node,findName);
			GenericNode.setMiscText(holder, node,nodeName,interact,interactDesc,findName);
			return false;
		}
	}
	
	private boolean wanderingSkeleMulti(NodeConnector holder, int node) {
		Person skeleton = holder.getStorageFirstPerson(node);
		Object[] stored = holder.getStorageAsArray(node);
		String nodeName = (String) stored[1];
		String interact = (String) stored[2];
		String interactDesc  = (String) stored[3];
		String findName  = (String) stored[3];
		
		Print.println("There is a skeleton wandering around a "+findName+" here.");
		Print.println("[battle]The skeleton attacks you!");
		
		Combat c = Player.player.fightWith(skeleton);
		if (c.playerWon() > 0) {
			Print.println("The skeleton crumbles to dust.");
			GenericNode.setMiscText(holder, node,nodeName,interact,interactDesc,findName);
			return false;
		}else {
			//skeleton wins
			return true;
		}
		
	}
	//end locked coffin related methods
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.GRAVE_DIRT,DrawBane.GRAVE_DUST,DrawBane.GARLIC,DrawBane.TELESCOPE};
	}

	@Override
	public void passTime(NodeConnector holder,int node, double time, TimeContext calling){
		//none yet
	}
	

}
