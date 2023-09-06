package trawel.towns.nodes;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.Town;

public class Mine extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private int veinsLeft = 0;
	private int size;
	
	public Mine(String name,Town t, SuperPerson _owner,Shape s) {
		this(name,t,50,t.getTier(),s);
		owner = _owner;
	}
	public Mine(String _name,Town t,int _size, int _tier, Shape s) {
		background_area = "mine";
		tutorialText = "Mine.";
		area_type = Area.MINE;
		name = _name;
		town = t;
		tier = _tier;
		size = _size;
		shape = s;
		generate(size);
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|mine|Mine|");
		//FIXME: lights will persist
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
		if (veinsLeft == 0 && shape.equals(Shape.NONE)) {
			Networking.unlockAchievement("mine1");
			Player.unlockPerk(Perk.MINE_ALL_VEINS);
		}
	}
	
	@Override
	protected void generate(int size) {
		start = NodeType.NodeTypeNum.MINE.singleton.getStart(this, size, tier);//DOLATER: get actual level
	}
	
	@Override
	protected byte bossType() {
		return 2;
	}
	@Override
	public String sizeDesc() {
		return super.sizeDesc() + " V: " +veinsLeft;
	}

}
