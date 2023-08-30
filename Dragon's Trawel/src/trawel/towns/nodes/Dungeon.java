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
import trawel.battle.Combat.SkillCon;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.Town;
import trawel.towns.fort.SubSkill;

public class Dungeon extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private byte boss;
	private List<SubSkill> skill_cons;
	private List<Integer> skill_nodes;
	/**
	 * will be null if not a dungeon shape that has helpers
	 */
	private List<SuperPerson> delve_helpers;
	private int helper_cap = 3;
	private boolean escapePartyMenu;
	
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
	public void go() {
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		if (hasHelpers()) {
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> list = new ArrayList<MenuItem>();
					if (Player.getTutorial()) {
						list.add(new MenuLine() {

							@Override
							public String title() {
								return "This dungeon is very dangerous and other adventurers are willing to group up and share the loot.";
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
								extra.menuGo(new ScrollMenuGenerator(size,"next <>","prior <>") {
		
									@Override
									public List<MenuItem> forSlot(int i) {
										if (i < delve_helpers.size()) {
											//existing member
											SuperPerson per = delve_helpers.get(i);
											return Collections.singletonList(new MenuSelect() {

												@Override
												public String title() {
													return "Manage " +per.getPerson().getName() + " LvL: "+per.getPerson().getLevel();
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
																	return per.getPerson().getName() + " LvL: "+per.getPerson().getLevel();
																}});
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
													// TODO Auto-generated method stub
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
					return list;
				}});
		}else {
			start.start();
		}
	}
	
	@Override
	protected void generate(int size) {
		switch (shape) {
		case RIGGED_DUNGEON: case TOWER:
			delve_helpers = new ArrayList<SuperPerson>();
			break;
		default:
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

}
