package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat.SkillCon;
import trawel.personal.Person;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.personal.people.behaviors.AbandonPostBehavior;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;
import trawel.towns.fort.SubSkill;

public class Dungeon extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private byte boss;
	private List<SubSkill> skill_cons;
	private List<Integer> skill_nodes;
	/**
	 * will be null if not a dungeon shape that has helpers
	 */
	private List<Agent> delve_helpers;
	private List<String> left_helpers;
	private int helper_cap = 3;
	private transient boolean escapePartyMenu;
	
	public Dungeon(String name,Town t,Shape s,int bossType) {
		this.name = name;
		town = t;
		tutorialText = "Dungeon.";
		shape = s;
		boss = (byte) bossType;	
		generate(50);
		area_type = Area.DUNGEON;
	}
	
	public boolean hasHelpers() {
		return delve_helpers != null;
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public RemoveAgentFromFeatureEvent laterRemoveAgent(Agent a) {
		if (hasHelpers()) {
			if (delve_helpers.contains(a)) {
				return new RemoveAgentFromFeatureEvent(a,this,true);
			}
		}
		return null;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		if (hasHelpers()) {
			if (left_helpers.size() > 0) {
				extra.println("While you were away, the following party members left:");
				while (!left_helpers.isEmpty()) {
					extra.println(left_helpers.remove(0));
				}
			}
			for (Agent a: delve_helpers) {
				a.setActionTimeMin(48);//if you check they will wait at least two more days
			}
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					if (Player.getTutorial()) {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "This dungeon is very dangerous and other adventurers are willing to group up and share the loot. They will only help in mass fights.";
							}});
					}
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Delve " + getName() + (delve_helpers.size() == 0 ? " Alone" : " With "+delve_helpers.size()+" Allies");
						}

						@Override
						public boolean go() {
							start.start();
							return false;
						}});
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Manage Party";
						}

						@Override
						public boolean go() {
							escapePartyMenu = false;
							while (!escapePartyMenu) {
								int size;
								if (delve_helpers.size() < helper_cap) {
									size = delve_helpers.size()+1;
								}else {
									size = delve_helpers.size();
								}
								extra.menuGo(new ScrollMenuGenerator(size,"last <>","next <>") {
		
									@Override
									public List<MenuItem> forSlot(int i) {
										if (i < delve_helpers.size()) {
											//existing member
											Agent sper = delve_helpers.get(i);
											Person per = sper.getPerson();
											return Collections.singletonList(new MenuSelect() {

												@Override
												public String title() {
													return "Manage " +per.getName() + " LvL: "+per.getLevel();
												}

												@Override
												public boolean go() {
													extra.menuGo(new MenuGenerator() {

														@Override
														public List<MenuItem> gen() {
															List<MenuItem> list = new ArrayList<MenuItem>();
															list.add(new MenuLine() {

																@Override
																public String title() {
																	return per.getName() + " LvL: "+per.getLevel();
																}});
															list.add(new MenuSelect() {

																@Override
																public String title() {
																	return "View Stats";
																}

																@Override
																public boolean go() {
																	per.displayStats(false);
																	return false;
																}});
															list.add(new MenuSelect() {

																@Override
																public String title() {
																	return "Dismiss";
																}

																@Override
																public boolean go() {
																	extra.println(extra.PRE_RED+"Really dismiss " + per.getName() + " ("+per.getLevel()+")?");
																	if (extra.yesNo()) {
																		sper.onlyGoal(AgentGoal.NONE);
																		town.addOccupant(sper);
																		delve_helpers.remove(sper);
																		return true;
																	}
																	return false;
																}});
															list.add(new MenuBack());
															return list;
														}});
													return true;
												}});
										}else {
											//new member
											return Collections.singletonList(new MenuSelect() {

												@Override
												public String title() {
													return "Recruit";
												}

												@Override
												public boolean go() {
													List<Agent> people = new ArrayList<Agent>();
													town.getPersonableOccupants().forEach(people::add);
													extra.println("Spend a few hours attempting to recruit from the around "+people.size()+" townsfolk?");
													if (extra.yesNo()) {
														Player.addTime(2f+(extra.randFloat()*3f));
														mainGame.globalPassTime();
														people.removeIf(p -> !p.hasGoal(AgentGoal.NONE) || p.getPerson().getLevel()-1 > Player.player.getPerson().getLevel());
														if (people.size() == 0) {
															extra.println("You could not find anyone willing to help you.");
															return true;
														}
														Collections.shuffle(people);
														extra.menuGo(new ScrollMenuGenerator(Math.min(4,people.size()),"prior <> recruits","next <> recruits") {
															
															@Override
															public List<MenuItem> header() {
																return null;
															}
															
															@Override
															public List<MenuItem> forSlot(int i) {
																Person p = people.get(i).getPerson();
																return Collections.singletonList(new MenuSelect() {

																	@Override
																	public String title() {
																		return p.getName() + " LvL "+p.getLevel();
																	}

																	@Override
																	public boolean go() {
																		extra.menuGo(new MenuGenerator() {

																			@Override
																			public List<MenuItem> gen() {
																				List<MenuItem> list = new ArrayList<MenuItem>();
																				list.add(new MenuLine() {

																					@Override
																					public String title() {
																						return p.getName() + " LvL: "+p.getLevel();
																					}});
																				list.add(new MenuSelect() {

																					@Override
																					public String title() {
																						return "View Stats";
																					}

																					@Override
																					public boolean go() {
																						p.displayStats(false);
																						return false;
																					}});
																				final int cost = p.getLevel();
																				list.add(new MenuSelect() {

																					@Override
																					public String title() {
																						return "Recruit for "+ World.currentMoneyDisplay(cost)+ " of your " +Player.player.getGoldDisp();
																					}

																					@Override
																					public boolean go() {
																						if (p.getLevel() > Player.player.getGold()) {
																							extra.println("You can't afford them!");
																							return false;
																						}
																						extra.println("Really recruit?");
																						if (extra.yesNo()) {
																							Player.player.addGold(-cost);
																							Agent a = people.get(i);
																							a.addGold(cost);//hehe
																							a.onlyGoal(AgentGoal.DELVE_HELP);
																							town.removeOccupant(a);
																							delve_helpers.add(a);
																							escapePartyMenu = true;
																							return true;
																						}
																						return false;
																					}});
																				list.add(new MenuBack());
																				return list;
																			}});
																		if (escapePartyMenu) {//another escape
																			escapePartyMenu = false;
																			return true;
																		}
																		return false;
																	}});
															}
															
															@Override
															public List<MenuItem> footer() {
																return Collections.singletonList(new MenuBack("Cancel"));
															}
														});
													}
													return true;
												}});
										}
									}
		
									@Override
									public List<MenuItem> header() {
										List<MenuItem> list = new ArrayList<MenuItem>();
										list.add(new MenuLine() {
		
											@Override
											public String title() {
												return "Size: " + delve_helpers.size() +"/"+helper_cap;
											}});
										list.add(new MenuLine() {
		
											@Override
											public String title() {
												String str = null;
												for (SuperPerson p: delve_helpers) {
													if (str == null) {
														str = "Party: " +p.getPerson().getName();
													}else {
														str+=", " +p.getPerson().getName();
													}
												}
												if (str == null) {
													str = "You have no party.";
												}
												return str;
											}});
										return list;
									}
		
									@Override
									public List<MenuItem> footer() {
										return Collections.singletonList(new MenuBack() {
											@Override
											public boolean go() {
												escapePartyMenu = true;
												return super.go();
											}
										});
									}
								});
							}
							return false;
						}});
					list.add(new MenuBack("leave"));
					return list;
				}});
		}else {
			start.start();
		}
	}
	
	@Override
	protected void generate(int size) {
		switch (shape) {
		case TOWER:
			delve_helpers = new ArrayList<Agent>();
			left_helpers = new ArrayList<String>();
			break;
		default:case RIGGED_DUNGEON: //unsure on rigged
			break;
		}
		start = NodeType.NodeTypeNum.DUNGEON.singleton.getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}
	
	@Override
	protected byte bossType() {
		return boss;
	}
	
	public List<SkillCon> getBattleCons(){
		List<SkillCon> list = new ArrayList<SkillCon>();
		for (SubSkill s: skill_cons) {
			list.add(new SkillCon(s,tier,1));//side 1 should be the not-player side
		}
		return list;
	}
	
	public void setupBattleCons() {
		skill_cons = new ArrayList<SubSkill>();
		skill_nodes = new ArrayList<Integer>();
	}
	
	public void registerBattleConWithNode(SubSkill c, int node) {
		skill_cons.add(c);
		skill_nodes.add(node);
	}
	
	public SubSkill requestRemoveBattleCon(int node) {
		int index = skill_nodes.indexOf(node);
		skill_nodes.remove(index);
		return skill_cons.remove(index);
	}
	
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		List<TimeEvent> list = super.passTime(time, calling);
		if (hasHelpers()) {
			for (Agent a: delve_helpers) {//I could go through this backwards, but it should be in time events anyways
				List<TimeEvent> sublist = a.passTime(time, calling);
				if (sublist != null) {
					if (list == null) {
						list = new ArrayList<TimeEvent>();
					}
					list.addAll(sublist);
				}
			}
			if (list != null) {
				for (int i = list.size()-1; i >=0;i--) {
					TimeEvent e = list.get(i);
					if (e instanceof Feature.RemoveAgentFromFeatureEvent) {
						RemoveAgentFromFeatureEvent rem = (RemoveAgentFromFeatureEvent)e;
						if (rem.feature == this) {
							if (delve_helpers.remove(rem.agent)) {
								//removed properly
								list.remove(i);
								left_helpers.add(rem.agent.getPerson().getName() + " ("+rem.agent.getPerson().getLevel()+")");
								if (rem.putInTown) {
									town.addOccupant(rem.agent);//can be instant
								}
							}else {
								throw new RuntimeException("Trying to remove " + rem.agent + " from " + rem.feature +" in dungeon party, but they weren't there!");
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public List<Person> getHelpFighters(){
		List<Person> people = new ArrayList<Person>();
		delve_helpers.stream().forEach(p -> people.addAll(p.getAllies()));
		return people;
	}
	
	@Override
	public void retainAliveFighters(List<Person> retain){
		List<Agent> agents = new ArrayList<Agent>();
		for (Person p: retain) {
			SuperPerson s = p.getSuper();
			if (s != null && s instanceof Agent) {
				Agent a = (Agent) s;
				if (a.isCurrentBehaviorClass(AbandonPostBehavior.class)) {
					a.setActionTimeMin(2*24*7);//2 weeks on use
				}else {
					System.err.println(p.getName() + "'s current behavior isn't abandoning post for dungeon " + getName());
				}
				agents.add(a);
			}
		}
		delve_helpers.retainAll(agents);
	}
	
	@Override
	public float occupantDesire() {
		if (hasHelpers()) {
			return 5;
		}
		return .5f;
	}

}
