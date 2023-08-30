package trawel.towns.nodes;
import trawel.Networking;
import trawel.extra;
import trawel.Networking.Area;
import trawel.personal.classless.Perk;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.towns.Town;

public class Mine extends NodeFeature {

	private static final long serialVersionUID = 1L;
	private int veinsLeft = 0;
	
	public Mine(String name,Town t, SuperPerson owner,Shape s) {
		this.name = name;
		town = t;
		tutorialText = "Mine.";
		this.owner = owner;
		shape = s;
		generate(50);
		background_area = "mine";
		area_type = Area.MINE;
	}
	
	@Override
	public String getColor() {
		return extra.F_NODE;
	}
	
	@Override
	public void go() {
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|mine|Mine|");
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
		start = NodeType.NodeTypeNum.MINE.singleton.getStart(this, size, getTown().getTier());//DOLATER: get actual level
	}
	
	@Override
	protected byte bossType() {
		return 2;
	}
	@Override
	public String sizeDesc() {
		return " S: " + start.getSize() + " V: " +veinsLeft;
	}

}
