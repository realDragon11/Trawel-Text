package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.battle.Combat.SkillCon;
import trawel.towns.Town;
import trawel.towns.fort.SubSkill;

public class Dungeon extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private byte boss;
	private List<SubSkill> skill_cons;
	private List<Integer> skill_nodes;
	public Dungeon(String name,Town t,Shape s,int bossType) {
		this.name = name;
		town = t;
		tutorialText = "Dungeon.";
		shape = s;
		boss = (byte) bossType;	
		generate(50);
		area_type = Area.DUNGEON;
	}
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		start.start();
	}
	
	@Override
	protected void generate(int size) {
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
