package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.AIClass;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.factions.Faction;
import trawel.factions.HostileTask;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.Person.PersonType;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.Seed;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Player;
import trawel.quests.Quest.TriggerType;
import trawel.time.TimeContext;
import trawel.towns.World;
import trawel.towns.misc.PlantSpot;
import trawel.towns.services.Oracle;

public class GroveNode implements NodeType{

	private static final int EVENT_NUMBER = 17;

	private WeightedTable groveBasicRoller;
	
	public GroveNode() {
		groveBasicRoller = new WeightedTable(new float[] {
				2f,//dueler
				1f,//river
				2f,//mugger
				1f,//free loot body
				1f,//fairy circle
				//slot 6 next
				1f,//old person
				1f,//collector
				2f,//fallen tree TODO: refalls on things?
				1.5f,//dryad
				//10 next
				2f,//casual
				1f,//mushroom
				1f,//moss
				1.5f,//rich and bodyguard
				.5f,//weapon stone
				//15
				2f,//wolves
				1f,//shaman
				1f//bee hive
				});
	}
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int ret = holder.newNode(NodeType.NodeTypeNum.GROVE.ordinal(),1+groveBasicRoller.random(extra.getRand()),tier);
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
		if (size <= 1) {
			return made;
		}
		int split;
		if (size > 5) {
			split = extra.randRange(3,5);
		}else {
			split = extra.randRange(1,Math.min(size, 3));
		}
		
		int i = 0;
		int sizeLeft = size-1;
		//now more even, for groves, but less likely to fill it entirely
		int baseSize = sizeLeft/(split+1);
		sizeLeft-=baseSize*split;
		while (i < split) {
			int sizeRemove = extra.zeroOut(extra.randRange(sizeLeft/2,sizeLeft)-1);
			sizeLeft-=sizeRemove;
			int tempLevel = tier;
			int n;
			int sizeFor = baseSize+sizeRemove;
			if (sizeFor < 4 && holder.getFloor(made) > 3 && extra.chanceIn(1,5)) {
				//caves always level up
				n = NodeType.NodeTypeNum.CAVE.singleton.generate(holder, made,sizeFor, tempLevel+1);
				if (sizeFor >= 2 && extra.chanceIn(2,3)) {//cannot generate entrance if not enough space
					holder.setEventNum(n, 1);//entrance
				}
			}else {
				if (extra.chanceIn(1,10)) {
					tempLevel++;
				}
				n = generate(holder,made,sizeFor,tempLevel);
			}
			holder.setMutualConnect(made,n);
			i++;
		}
		return made;
	}

	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		case 1: 
			Person pd = RaceFactory.getDueler(holder.getLevel(madeNode));
			String warName = extra.capFirst(randomLists.randomWarrior());
			pd.setTitle("the "+warName);
			GenericNode.setSimpleDuelPerson(holder,madeNode, pd,warName,"Approach " +pd.getNameNoTitle() +".","Challenge");
		break;
		case 2: 
			//made.name = extra.choose("river","pond","lake","stream","brook"); made.interactString = "wash yourself"
			holder.setStorage(madeNode, extra.choose("river","pond","lake","stream","brook"));
			;break;
		case 3:
			String mugname = randomLists.randomMuggerName();
			Person mugger = RaceFactory.getMugger(holder.getLevel(madeNode));
			GenericNode.setBasicRagePerson(holder, madeNode, mugger, mugname, "The " + mugname + " mugs you!");
		break;
		case 4:
			int blevel = holder.getLevel(madeNode)-2;
			if (blevel < 1) {
				GenericNode.setSimpleDeadString(holder, madeNode, "defaced corpse");
			}else {
				Person body = RaceFactory.makeLootBody(blevel);
				holder.setStorage(madeNode, new Object[]{
						extra.choose("Rotting","Decaying","Long Dead","Festering")
						+ " " +body.getBag().getRace().renderName(false) + " " +
						extra.choose("Corpse","Body")
						,
						body
				});
			}
			break;
		case 5:
			PlantSpot[] big_circle = new PlantSpot[extra.randRange(12,24)];
			for (int i = Math.min(big_circle.length/2,holder.getLevel(madeNode)+2); i >=0;i--) {
				int place = extra.randRange(0,big_circle.length-1);
				PlantSpot check = big_circle[place];
				while (check != null) {//fancy way of making them appear in 'stretches' slightly more often
					place++;
					check = big_circle[place%big_circle.length];
				}
				big_circle[place%big_circle.length] = new PlantSpot(1,Seed.EMPTY);
			}
			holder.setStorage(madeNode, big_circle);
			break;
		case 6:
			Person old = RaceFactory.makeOld(holder.getLevel(madeNode)+2);
			if (extra.chanceIn(1,3)) {
				old.getBag().addDrawBaneSilently(DrawBane.TELESCOPE);
			}
			holder.setStorage(madeNode, old);
			break;
		case 7://collector
			GenericNode.applyCollector(holder,madeNode);
			break;
		case 8://tree of many things
			holder.setStorage(madeNode,false);//needs to be 'refilled' by time passing
			;break;
		case 9:
			int dlevel = holder.getLevel(madeNode);
			List<Person> entslist = new ArrayList<Person>();
			int testLevel = RaceFactory.addAdjustLevel(dlevel,3);
			switch(extra.randRange(0,3)) {
			case 3:
				//many ents
				if (dlevel > 3) {
					entslist.add(RaceFactory.getDryad(dlevel-1));
					entslist.add(RaceFactory.makeEnt(testLevel));
					entslist.add(RaceFactory.makeEnt(testLevel));
					entslist.add(RaceFactory.makeEnt(testLevel));
					break;
				}//if we can't afford to make tons of ents, fall through to only one
			case 2:
				testLevel = RaceFactory.addAdjustLevel(dlevel,1);
				if (dlevel > 1) {
					entslist.add(RaceFactory.getDryad(testLevel));
					entslist.add(RaceFactory.makeEnt(testLevel));
					break;
				}//if we can't afford to make any ents, only add dryad
			case 0:
			case 1://only dryad
				entslist.add(RaceFactory.getDryad(dlevel));
				break;
			}
			holder.setStorage(madeNode, entslist);
		break;
		case 10://casual people, racist, angry, or not
			GenericNode.setBasicCasual(holder,madeNode,RaceFactory.makeMaybeRacist(holder.getLevel(madeNode)));
			break;
		case 11://mushroom
			holder.setStorage(madeNode, randomLists.randomPrintableColor());
			break;
		 case 12://moss
			holder.setStorage(madeNode, randomLists.randomPrintableColor());
			holder.setStateNum(madeNode, extra.choose(0,1));//50% chance of having something under it
			break;
		case 13://rich and bodyguard
			GenericNode.setBasicRichAndGuard(holder, madeNode);
			break;
		case 14: 
			holder.setStorage(madeNode, new Weapon(holder.getLevel(madeNode)));
			break;
		case 15:
			List<Person> wolves = new ArrayList<Person>();
			int wolflevel = holder.getLevel(madeNode);
			for (int i = Math.min(wolflevel, extra.randRange(3,4));i > 0 ;i--) {
				wolves.add(RaceFactory.makeWolf(extra.zeroOut(wolflevel-3)+1));
			}
			holder.setForceGo(madeNode,true);
			holder.setStorage(madeNode,wolves);
		;break;
		case 16:
			holder.setStorage(madeNode, RaceFactory.getShaman(holder.getLevel(madeNode)));
			break;
		case 17://bee hive, turns into plant spot
			break;
		}
		//TODO: add lumberjacks and tending to tree
		
	}
	
	@Override
	public boolean interact(NodeConnector holder,int node) {
		switch (holder.getEventNum(node)) {
		case 2: 
			extra.println("You wash yourself in the "+holder.getStorageFirstClass(node, String.class)+".");
			Player.player.getPerson().washAll();
			Player.player.getPerson().bathEffects();
			return false;
		case 4:
			extra.println("You find a "+holder.getStorageFirstClass(node,String.class)+"... With their equipment intact!");
			Person p = holder.getStorageFirstPerson(node);
			p.getBag().forceDownGradeIf(Player.player.getPerson().getLevel());
			AIClass.playerLoot(p.getBag(),true);
			holder.findBehind(node, "body");
			GenericNode.setPlantSpot(holder, node,Seed.SEED_FUNGUS);//make corpse fungus
			return false;
		case 5: //fairy circle
			extra.println("You enter the fairy circle.");
			PlantSpot[] spots = (PlantSpot[])holder.getStorage(node);
			extra.menuGo(new ScrollMenuGenerator(spots.length,"previous <> mushrooms", "next <> mushrooms") {

				@Override
				public List<MenuItem> forSlot(int i) {
					List<MenuItem> list = new ArrayList<MenuItem>();
					PlantSpot p = spots[i];
					if (p == null) {
						list.add(new MenuSelect(){

							@Override
							public String title() {
								return "Mushroom?"+extra.choose("?","??","???");
							}

							@Override
							public boolean go() {
								extra.println("As you reach for this mushroom, it vanishes.");
								return false;
							}});
					}else {
						list.add(new MenuSelect(){

							@Override
							public String title() {
								return "Mushroom?"+extra.choose("!","?!","??!");//this is so dumb and I love it
							}

							@Override
							public boolean go() {
								extra.println("As you reach for this mushroom, a planter appears in the ground!");
								p.go();
								return false;
							}});
					}
					return list;
				}

				@Override
				public List<MenuItem> header() {
					return null;
				}

				@Override
				public List<MenuItem> footer() {
					return Collections.singletonList(new MenuBack("Leave this terrible place."));
				}});
			return false;
		case 6: 
			Person old = holder.getStorageFirstPerson(node);
			int oldstate = holder.getStateNum(node);
			if (oldstate == 0) {
				extra.menuGo(new MenuGenerator() {
					
					@Override
					public List<MenuItem> gen() {
						List<MenuItem> list = new ArrayList<MenuItem>();
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return "Chat";
							}

							@Override
							public boolean go() {
								extra.println("\"We are in " + holder.parent.getName()
								+ ". It is a Grove on the island of "+holder.parent.getTown().getIsland().getName()
										+". Beware, danger lurks under these trees. If you wish to survive, heed my advice:\"");
								Oracle.tip("old");
								return false;
							}});
						list.add(new MenuSelect() {

							@Override
							public String title() {
								return extra.PRE_BATTLE+"End a Long Career";
							}

							@Override
							public boolean go() {
								extra.println("Before you can move, they speak up. "
								+extra.TIMID_RED+"\"Hm, yes. Maybe you would be a fitting end to my story... or perhaps merely another part of it. But be warned, there is no going back.\"");
								if (old.reallyAttack()) {
									holder.setForceGo(node,true);
									holder.setStateNum(node,1);
									return true;
								}
								return false;
							}});
						list.add(new MenuBack("Let bygones be bygones."));//something something ready to move on
						return list;
					}
				});
			}else {//can combat forcego
				extra.println(extra.TIMID_RED+old.getNameNoTitle()+" has had enough. One way or the other.");
				Combat oldc = Player.player.fightWith(old);
				if (oldc.playerWon() > 0) {
					GenericNode.setSimpleDeadPerson(holder, node, old);//gets their own corpse
				}else {
					return true;
				}
			}
			return false;
		case 8: return treeOfManyThings(holder,node);
		case 9: return dryad(holder,node);
		case 11: return funkyMushroom(holder,node);
		case 12: return funkyMoss(holder,node);
		case 14: return weapStone(holder,node);
		case 15: return packOfWolves(holder,node);
		case 16: return shaman(holder,node);
		case 17: return beeHive(holder,node);
		}
		return false;
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.MEAT,DrawBane.WOOD,DrawBane.TRUFFLE};
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
		// empty
		switch (holder.getEventNum(node)) {
		case 12://moss
			if (holder.globalTimer > 6 && holder.getStateNum(node) == 2) {
				holder.globalTimer-=2;
				GenericNode.emptyANode(holder,node);
			}
			break;
		case 8:
			if (holder.globalTimer > 1f) {
				if (holder.getStorageFirstClass(node,Boolean.class)) {
					holder.globalTimer-=.5f;
					holder.setStorage(node, false);//refill
				}
			}
			break;
		case 5:
			PlantSpot[] spots = (PlantSpot[])holder.getStorage(node);
			//should probably find some way to not pass time as often
			for (int i = 0; i < spots.length;i++) {
				if (spots[i] != null) {
					PlantSpot pspot = spots[i];
					if (pspot.timer > 40f) {
						if (pspot.contains == Seed.EMPTY) {//if we have nothing
							if (extra.chanceIn(1, 5)) {//add something
								pspot.contains = extra.choose(Seed.SEED_FAE,Seed.SEED_TRUFFLE);
								pspot.timer = 0;
							}else {
								//delay
								pspot.timer -= 30;
							}
						}
					}
					calling.localEvents(spots[i].passTime(time, calling));
				}
			}
			break;
		}
	}

	
	/*//DOLATER: make the 'I'm lost' mechanic again
	private boolean fairyCircle2() {
		extra.println("You find a fairy circle of mushrooms. Step in it?");
		if (extra.yesNo()) {
			extra.println("Some fairies appear, and ask you what fate you desire.");
			extra.println("1 a power fantasy");
			extra.println("2 a challenge");
			extra.println("3 your normal fate");
			switch (extra.inInt(3)) {
			case 1:
				extra.println("You find yourself jerked nowhere. Your surroundings change...");
				Networking.sendStrong("PlayDelay|sound_teleport|1|");
				Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()-2));
				break;
			case 2:
				extra.println("You find yourself jerked nowhere. Your surroundings change...");
				Networking.sendStrong("PlayDelay|sound_teleport|1|");
				Player.player.setLocation(Player.world.getRandom(Player.player.getPerson().getLevel()));
				break;
			case 3:
				extra.println("The fairies frown and disappear.");
				return false;
			}
			return true;
		}else {
			extra.println("You stay away from the circle.");
			return false;
		}
	}*/

	private boolean treeOfManyThings(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		if (holder.getStorageFirstClass(node,Boolean.class)) {
			extra.println("The tree looks... hollow?");
			holder.findBehind(node, "oddly hollow tree");
			return false;
		}
		//TODO: many things! add a thing, and make it not happen if it was the last thing
		switch (state) {
		case 0://always starts in state 0
			extra.println("You examine the fallen tree. It is very pretty... but something feels off.");
			holder.findBehind(node, "fallen tree");
			break;
		case 1:
			Person p = RaceFactory.makeGeneric(holder.getLevel(node));
			p.getBag().graphicalDisplay(1, p);
			extra.println("There's a person stuck under the fallen tree! Help them?");
			if (extra.yesNo()) {
				extra.println("You move the tree off of them.");
				holder.setStateNum(node,1);
				if (extra.randFloat() > .9f) {
					extra.println(extra.PRE_BATTLE+"Suddenly, they attack you!");
					p.hTask = HostileTask.MUG;
					Combat c = Player.player.fightWith(p);
					if (c.playerWon() > 0) {
					}else {
						extra.println(Player.loseGold(
								IEffectiveLevel.cleanRangeReward(holder.getLevel(node),2f,.2f)
								, true));
						Player.placeAsOccupant(p);
					}
				}else {
					if (extra.randFloat() < .3f) {
						p.hTask = HostileTask.PEACE;
						p.setPersonType(PersonType.COWARDLY);
						extra.println("They scamper off... "+extra.PRE_BATTLE+"Attack them?");
						if (extra.yesNo()) {
							Combat c = Player.player.fightWith(p);
							if (c.playerWon() > 0) {
							}else {
								Player.placeAsOccupant(p);
							}
						}else {
							extra.println("Well, that wasn't very rewarding...");
							Networking.clearSide(1);
							holder.findBehind(node,"tree");
						}
					}else {
						int gold = IEffectiveLevel.cleanRangeReward(holder.getLevel(node), 2f,.7f);
						extra.println("They offer a reward of " + World.currentMoneyDisplay(gold) + " in thanks for saving them. "+extra.PRE_BATTLE+"...But it looks like they might have more. Mug them?");
						if (extra.yesNo()) {
							p.getBag().addGold(gold);
							if (extra.randFloat() > .3f) {//70% chance for more money
								p.getBag().addGold(IEffectiveLevel.cleanRangeReward(holder.getLevel(node),1.5f,.8f));
							}
							p.hTask = HostileTask.PEACE;
							Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,0,p.getUnEffectiveLevel());
							Combat c = Player.player.fightWith(p);
							if (c.playerWon() > 0) {
							}else {
								Player.placeAsOccupant(p);
							}
						}else {
							Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1,0);
							Player.player.addGold(gold);
							extra.println("They walk off, leaving you with your reward.");
						}
					}
				}
			}else {
				extra.println("You leave them alone to rot...");
			}
			
			break;
		}
		if (state != 0 && extra.chanceIn(1,3)) {
			extra.println("What an odd tree.");
		}
		holder.setStateNum(node, treeRandNot(state));
		holder.setStorage(node,true);//needs to be 'refilled' by time passing
		
		return false;
	}
	
	public int treeRandNot(int not) {
		int cap = 1;
		int potential = extra.randRange(0,cap);
		if (potential == not) {
			potential = (potential+extra.randRange(1,Math.round(cap/2f)))%cap;
		}
		return potential;
	}

	private boolean dryad(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		//don't need to add code if empty since would run genericnode interact instead
		List<Person > peeps = holder.getStorageFirstClass(node,List.class);
		Person p = extra.getNonAddOrFirst(peeps);//the ent is an add if present
		if (state >=0 && state < 3) {//3 is angry, immediately killed force updates it with a generic
			extra.println("You come across a dryad tending to a tree.");
			p.getBag().graphicalDisplay(1, p);
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Ask about their Tree";
						}

						@Override
						public boolean go() {
							int state = holder.getStateNum(node);
							if (state != 2) {
								if (state == 1 || extra.chanceIn(2,3)) {
									extra.println("\"Would like your own tree?\"");
									if (extra.yesNo()) {
										extra.println("The dryad says to find a spot where a lumberjack has chopped down a tree and plant one there.");
										Player.bag.addSeed(Seed.SEED_ENT);
										holder.setStateNum(node,2);//can only get once, but can offer multiple times
									}else {
										holder.setStateNum(node,1);//set that they have it
									}
								}else {
									extra.println("The dryad goes into great depth about their tree's history, then says it's a shame that they don't have anything to offer such an intelligent "+Player.bag.getRace().renderName(false)+".");
									holder.setStateNum(node,2);//can only check once
								}
								
							}else {
								extra.println("The dryad goes into great depth about their tree's history.");
							}
							return false;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Ask about this place";
						}

						@Override
						public boolean go() {
							extra.println("\"We are in " + holder.parent.getName() + ". I don't venture away from my tree. It is a dangerous place, and this tree needs protection.\"");
							if (holder.globalTimer >= 6d) {//can offer seeds if the node hasn't refreshed stuff lately
								extra.println("\"Would you like a seed to help grow the forest?\"");
								if (extra.yesNo()) {
									Player.bag.addSeed(Seed.randSeed());
									holder.globalTimer-=6;
									holder.globalTimer/=2;
									//minus 6 (the time needed) and then div by 2 to avoid a large timer making it happen instantly forever
								}
							}
							return false;
						}
					});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_BATTLE+"Attack them.";
						}

						@Override
						public boolean go() {
							if (p.reallyAttack()) {
								if (peeps.size() == 1) {
									Combat c = Player.player.fightWith(p);
									if (c.playerWon() > 0) {
										GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
										return true;
									}else {
										holder.setStateNum(node,3);//angry
										holder.setForceGo(node, true);
										return true;
									}
								}else {
									extra.println(extra.PRE_BATTLE+ "It looks like their tree has other friends!");
									Combat c = Player.player.massFightWith(peeps);
									if (c.playerWon() > 0) {
										GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
										//only one grave
										return true;
									}else {
										holder.setStorage(node, c.getNonSummonSurvivors());//set survivors
										holder.setStateNum(node,3);//angry
										holder.setForceGo(node, true);
										return true;
									}
								}
								
							}
							return false;
						}});
					list.add(new MenuBack("Leave them alone."));
					return list;
				}}
			);
		}
		if (holder.getStateNum(node) == 3) {
			//if we're currently angry
			if (state != 3) {//if we weren't angry, so the player died in a fight
				Networking.clearSide(1);
				return true;//kick out
			}
			//force go fighting usually
			boolean hasdryad = p.getFlag(PersonFlag.IS_MOOK);
			
			if (peeps.size() == 1) {
				extra.println(extra.PRE_BATTLE+ (hasdryad ? p.getName() + " protects their tree!" : "The trees move to avenge their caretaker!"));
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() > 0) {
					holder.setForceGo(node,false);//clean up our force go
					GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
				}else {
					return true;//kick out
				}
			}else {
				extra.println(extra.PRE_BATTLE+ (hasdryad ? p.getName() + " protects their tree, and their tree has friends!" : "The trees move to avenge their caretaker!"));
				Combat c = Player.player.massFightWith(peeps);
				if (c.playerWon() > 0) {
					holder.setForceGo(node,false);//clean up our force go
					GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
					//only one grave, which could be an ent at this point
				}else {
					holder.setStorage(node, c.getNonSummonSurvivors());//set survivors
					return true;//kick out
				}
			}
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0) {
				
				GenericNode.setSimpleDeadRaceID(holder, node, p.getBag().getRaceID());
				return false;
			}
			return true;//kick out
		}else {
			Networking.clearSide(1);
			return false;
		}
	}

	private boolean funkyMushroom(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		int startstate = state;
		if (state == 0) {
			extra.println("You spot a glowing "+holder.getStorageFirstClass(node, String.class)+" mushroom on the forest floor.");
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Eat it whole!";
						}

						@Override
						public boolean go() {
							holder.setStateNum(node,1);
							return true;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Melt into Aether.";
						}

						@Override
						public boolean go() {
							holder.setStateNum(node,2);
							return true;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return extra.PRE_BATTLE+"Crush.";
						}

						@Override
						public boolean go() {
							holder.setStateNum(node,3);
							extra.println("You crush the mushroom under your heel.");
							return true;
						}});
					list.add(new MenuBack("Perhaps not."));
					return list;
				}});
		}
		state = holder.getStateNum(node);
		if (state == 0) {//didn't interact
			extra.println("You decide to leave it alone.");
			holder.findBehind(node, "nearby rock");
			return false;
		}
		switch (state) {
		case 1://eat
			if (extra.chanceIn(1, 4)) {//25% chance of anger
				if (extra.chanceIn(1,5)) {
					state = 22;//dryad takes offense
				}else {
					state = 32;//thief takes offense
				}
			}
			break;
		case 2://sell
			if (extra.chanceIn(3, 4)) {//75% chance of anger
				if (extra.chanceIn(1,3)) {
					state = 22;//dryad takes offense
				}else {
					state = 32;//thief takes offense
				}
			}
			break;
		case 3://crush
			if (extra.chanceIn(3,4)) {
				state = 23;//dryad takes offense
			}else {
				state = 33;//thief takes offense
			}
			break;
		case 4://extract
			//not in yet
			break;
		}
		holder.setStateNum(node, state);
		//<10 = no issues
		//10-19 = reserved for if "we had an issue but resolved it" becomes a valid case
		//20-29 = dryad
		//30-39 = thief
		//use least significant digit to determine outcome after
		if (state > 20 && state < 30) {//dryad
			Person p;
			if (startstate == 0) {//we chose an action this interaction, set stuff up
				//make sure not to overwrite our color name!
				p = RaceFactory.getDryad(holder.getLevel(node));
				String str = holder.getStorageFirstClass(node,String.class);
				holder.setStorage(node, new Object[] {str,p});
				extra.println(extra.PRE_BATTLE+randomLists.randomViolateForestQuote());
			}else {
				p = holder.getStorageFirstPerson(node);
			}
			//act on dryad
			Combat c = Player.player.fightWith(p);
			if (c.playerWon() > 0){
				//deded
				holder.setStorage(node, holder.getStorageAsArray(node)[0]);//clean up the person now instead of later
			}else {
				return true;//kick out
			}
			
			//return true if player dead, otherwise cleanup then fall through
			
		}else {
			Person p;
			if (state > 30 && state < 40) {//thief
				if (startstate == 0) {//we chose an action this interaction, set stuff up
					//make sure not to overwrite our color name!
					p = RaceFactory.makeMuggerWithTitle(holder.getLevel(node));
					String str = holder.getStorageFirstClass(node,String.class);
					holder.setStorage(node, new Object[] {str,p});
					extra.println( extra.PRE_BATTLE+"\"That looked expensive!\"");
				}else {
					p = holder.getStorageFirstPerson(node);
				}
				//act on thief
				Combat c = Player.player.fightWith(p);
				if (c.playerWon() > 0){
					//deded
					holder.setStorage(node, holder.getStorageAsArray(node)[0]);//clean up the person now instead of later
				}else {
					return true;//kick out
				}
				//return true if player dead, otherwise cleanup then fall through
			}
		}
		Seed plantstart = null;//do not add
		switch (state%10) {//now just the starting digit
		case 1://eat
			if (state < 10) {//if not interrupted
				extra.print("You eat the mushroom... ");
			}else {
				extra.println("Resume eating the mushroom?");
				if (!extra.yesNo()) {
					return false;
				}
				extra.print("You return to your meal, and ");
			}
			//all paths replace with new plant spot
			if (extra.chanceIn(1,3)) {
				extra.println("it tastes delicious!");
				plantstart = Seed.SEED_TRUFFLE;
			}else {
				if (extra.chanceIn(1,3)) {
					extra.println("you start to feel lightheaded.... you pass out!");
					extra.println("When you wake up, you notice someone went through your bags!");
					extra.println(
							Player.loseGold(
									IEffectiveLevel.cleanRangeReward(holder.getLevel(node), 3f, .2f)
							,true)
						);
					plantstart = Seed.EMPTY;
				}else {
					extra.println("getting it down is very difficult... but you manage.");
					int xpadd = Math.min(Player.player.getPerson().getLevel(),holder.getLevel(node));
					Player.player.getPerson().addXp(xpadd);
					plantstart = Seed.EMPTY;//add with nothing
				}
			}
			break;
		case 2://sell
			int worth;
			if (state < 10) {//if not interrupted is worth less
				worth = 3;
			}else {
				worth = 2;
			}
			worth = IEffectiveLevel.cleanRangeReward(holder.getLevel(node),worth,.2f);
			extra.println("You sell the mushroom for " +World.currentMoneyDisplay(worth) + ".");
			Player.player.addGold(worth);
			plantstart = Seed.EMPTY;
			break;
		case 3://crush
			//already did the fight
			plantstart = Seed.EMPTY;
			break;
		}
		
		if (plantstart != null) {
			//we use null to indicate we don't want to plant
			//replace current contents
			//this will clean up color data and any people that were still there
			GenericNode.setPlantSpot(holder, node,plantstart);
		}	
		return false;
	}


	/*for  Albin Grahn */
	private boolean funkyMoss(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		//int startstate = state;
		if (state == 0) {//under
			extra.println("You see something shining under the moss! Get a closer look?");
			if (!extra.yesNo()) {
				extra.println("You leave it alone.");
				return false;
			}
			//shiny stuff time
			DrawBane db = holder.attemptCollectAll(node,.7f, 3);
			if (db != null) {
				extra.println("Wow, there were a ton of "+ db.getName() +" pieces under the moss!");
				holder.setStateNum(node,1);
				state = 1;
			}else {
				int worth = 1;
				boolean will_attack = extra.chanceIn(1,3);
				if (will_attack) {
					worth*=2;
				}
				worth = IEffectiveLevel.cleanRangeReward(holder.getLevel(node),worth,.3f);;
				extra.println("You find " +World.currentMoneyDisplay(worth) + " under the moss!");
				Player.player.addGold(worth);
				if (will_attack) {
					GenericNode.setBasicRagePerson(holder, node,
							RaceFactory.makeMuggerWithTitle(holder.getLevel(node))
							,"Moss Thief","They still wanted what was under that moss!");
					return false;//reset for forcego
				}
				//continue
				holder.setStateNum(node,1);
				state = 1;
			}
		}
		if (state == 1 || state == 2) {
			extra.println("There is some moss here. It looks poisonous.");
			holder.setStateNum(node,2);
			return false;//TODO maybe make this have more behavior again
			
		}
		return false;
	}
	
	private boolean weapStone(NodeConnector holder,int node) {
		if (holder.getStateNum(node) == 0) {
			Weapon w = holder.getStorageFirstClass(node, Weapon.class);
			extra.println("There is a " + w.getBaseName() + " embeded in the stone here. Try to take it?");
			if (extra.yesNo()) {
				int lvl = Player.player.getPerson().getLevel();
				int w_level = w.getLevel();
				if (lvl < w_level-1) {
					extra.println("Try as you might, you can't pry lose the " + w.getBaseName());
					return false;
				}
				if (lvl > w_level + 1) {
					extra.println("As you pull on it, the stone crumbles to pieces!");
				}else {
					extra.println("As you pull on it, the "+w.getBaseName()+" slowly slips free, and the rock crumbles!");
				}
				holder.setStateNum(node,1);
				AIClass.findItem(w,Player.player.getPerson());
				GenericNode.setMiscText(holder, node,"Rock Fragments", "Look at rock fragments.", "Crumbled rock lies on the forest floor.", "rock fragments");
			}else {
				extra.println("You leave it alone.");
			}
		}else {
			extra.println("Crumbled rock lies on the forest floor.");
			holder.findBehind(node,"rock fragments");
		}
		return false;
	}

	private boolean shaman(NodeConnector holder,int node) {
		Person p = holder.getStorageFirstPerson(node);
		p.getBag().graphicalDisplay(1,p);
		int cost = (int) (IEffectiveLevel.unclean(holder.getLevel(node))*4);
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuLine() {

					@Override
					public String title() {
						return "\""+Oracle.tipString("shaman")+"\"";
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "Buy Cleansing (" + World.currentMoneyDisplay(cost) +" of your "+Player.player.getGoldDisp()+ ")";
					}

					@Override
					public boolean go() {
						if (cost > Player.player.getGold()) {
							extra.println("You can't afford that!");
							return false;
						}
						extra.println("Really pay?");
						if (extra.yesNo()) {
							Player.player.addGold(-cost);
							Player.player.getPerson().removeEffectAll(Effect.CURSE);
							Player.player.getPerson().cureEffects();
							extra.println("You feel better.");
							return false;
						}
						extra.println("You decide to not.");
						return false;
					}});
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return extra.PRE_BATTLE+"Attack";
					}

					@Override
					public boolean go() {
						if (p.reallyAttack()) {
							GenericNode.setBasicRagePerson(holder, node, p,"Wary Shaman","The Shaman is ready for you!");
							return true;
						}
						return false;
					}});
				list.add(new MenuBack());
				return list;
			}});
		return false;
	}
	
	private boolean packOfWolves(NodeConnector holder,int node) {
		extra.println(extra.PRE_BATTLE+"The pack descends upon you!");
		List<Person> list = holder.getStorageFirstClass(node,List.class);
		Combat c = Player.player.massFightWith(list);
		if (c.playerWon() > 0) {
			holder.setForceGo(node,false);
			GenericNode.setSimpleDeadRaceID(holder, node, list.get(0).getBag().getRaceID());
			return false;
		}else {
			list = c.getNonSummonSurvivors();
			holder.setStorage(node,list);
			return true;
		}

	}
	private boolean beeHive(NodeConnector holder,int node) {
		extra.print("You destroy the natural hive... ");
		Player.player.getPerson().addEffect(Effect.BEES);//BEEEEEEEEEEEEEEEEEEEEEES
		Networking.unlockAchievement("bees_hive");
		extra.println("The BEEEEEES!!!! They're angry!");
		GenericNode.setPlantSpot(holder, node,Seed.SEED_BEE);
		
		Player.bag.addNewDrawBanePlayer(DrawBane.HONEY);
		Player.bag.addNewDrawBanePlayer(DrawBane.WAX);
		Player.bag.addSeed(Seed.SEED_BEE);
		return false;//could have them drive you out but that would be mean
	}

	@Override
	public String interactString(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 2://river
			return "Wash yourself in the " +holder.getStorageFirstClass(node,String.class)+".";
		case 4://looting body, turns into plantspot after
			return "Loot " +holder.getStorageAsArray(node)[0].toString() +".";
		case 5:
			return "Enter Fairy Circle...";
		case 6://old person, uses causal text
			return "Approach the " + holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)+".";
		case 8://tree of many things
			return "Examine fallen tree.";
		case 9://dryad
			return "Approach the Dryad's tree.";
			
			//thankfully the things below will auto take out of arrays
			
		case 11://mushroom, colored
			return "Examine the " + holder.getStorageFirstClass(node, String.class) + " mushroom"+extra.COLOR_RESET+".";
		case 12://moss, colored
			return "Examine the " + holder.getStorageFirstClass(node, String.class) + " moss"+extra.COLOR_RESET+".";
		case 14: //weapon stone
			if (holder.getStateNum(node) == 0) {
				Weapon w = holder.getStorageFirstClass(node, Weapon.class);
				return "Attempt to take " + w.getBaseName();
			}
			return "ERROR";
		case 15://pack of wolves
			return null;
		case 16://shaman, uses causal text
			return "Approach the " + holder.getStorageFirstPerson(node).getBag().getRace().renderName(false)+".";
		case 17://bee hive (to plant spot)
			return "Gather honey?";
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 2://river
			return holder.getStorageFirstClass(node,String.class);
		case 4://looting body, turns into plantspot after
			return holder.getStorageAsArray(node)[0].toString();
		case 5:
			return "Fairy Circle";
		case 6://old person, uses causal text
			return holder.getStorageFirstPerson(node).getBag().getRace().renderName(false);
		case 8://tree of many things
			return "Fallen Tree";
		case 9://dryad
			return "Dryad's Tree";
		case 11://mushroom, colored
			return "A single " +holder.getStorageFirstClass(node, String.class)+" mushroom"+extra.COLOR_RESET;
		case 12://moss, colored
			return "A single piece of " +holder.getStorageFirstClass(node, String.class)+" moss"+extra.COLOR_RESET;
		case 14: //weapon stone
			if (holder.getStateNum(node) == 0) {
				Weapon w = holder.getStorageFirstClass(node, Weapon.class);
				return w.getBaseName() +", stuck in a rock";
			}
			return "ERROR";
		case 15:
			return "Pack of Wolves";
		case 16://shaman, uses causal text
			return holder.getStorageFirstPerson(node).getBag().getRace().renderName(false);
		case 17:
			return "Bee Hive";
		}
		return null;
	}

}
