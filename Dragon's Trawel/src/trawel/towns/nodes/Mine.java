package trawel.towns.nodes;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.Town;
import trawel.towns.nodes.BossNode.BossType;

public class Mine extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private int veinsLeft = 0;
	private int size;
	private BossType bossType;
	private int totalMined = 0;
	
	public Mine(String name,Town t, SuperPerson _owner,Shape s) {
		this(name,t,50,t.getTier(),s,BossType.NONE);
		owner = _owner;
	}
	public Mine(String _name,Town t,int _size, int _tier, Shape s, BossType _bossType) {
		background_area = "mine";
		tutorialText = "Mine";
		area_type = Area.MINE;
		name = _name;
		town = t;
		tier = _tier;
		size = _size;
		shape = s;
		bossType = _bossType;
		generate(size);
	}
	
	@Override
	public String getTutorialText() {
		switch (shape) {
		case ELEVATOR:
			return "Hellevator Mine";
		case NONE:
			return "Mine";
		}
		return "Mine?";
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		//node will handle re-applying them if background changes within it, still need to clear in case
		Networking.addMultiLight(80,471);
		Networking.addMultiLight(486,360);
		Networking.addMultiLight(1012,353);
		start.start();
		Networking.clearLights();
	}
	@Override
	public void sendBackVariant() {
		Networking.sendStrong("Backvariant|"+background_area+background_variant+"|1|0|");
	}
	
	public void addVein() {
		veinsLeft++;
	}
	public void removeVein() {
		veinsLeft--;
		totalMined++;
		if (veinsLeft == 0 && totalMined > 10 && shape.equals(Shape.NONE)) {
			Networking.unlockAchievement("mine1");
			Player.unlockPerk(Perk.MINE_ALL_VEINS);
		}
	}
	
	@Override
	protected void generate(int size) {
		start = NodeType.NodeTypeNum.MINE.singleton.getStart(this, size, tier);
	}
	
	@Override
	protected BossType bossType() {
		return bossType;
	}
	@Override
	public String sizeDesc() {
		return super.sizeDesc() + " V: " +veinsLeft;
	}

}
