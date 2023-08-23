package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.battle.Combat.SkillCon;
import trawel.towns.Town;

public class Dungeon extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private byte boss;
	private List<SkillCon> skill_cons;
	private List<Integer> skill_nodes;
	public Dungeon(String name,Town t,Shape s,int bossType) {
		this.name = name;
		town = t;
		tutorialText = "Dungeon.";
		shape = s;
		generate(50);
		boss = (byte) bossType;
		
	}
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		Networking.setArea("dungeon");
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
		return skill_cons;
	}
	
	public void setupBattleCons() {
		skill_cons = new ArrayList<SkillCon>();
		skill_nodes = new ArrayList<Integer>();
	}
	
	public void registerBattleConWithNode(SkillCon c, int node) {
		skill_cons.add(c);
		skill_nodes.add(node);
	}
	
	public void requestRemoveBattleCon(int node) {
		int index = skill_nodes.indexOf(node);
		skill_nodes.remove(index);
		skill_cons.remove(index);
	}

}
