package trawel;
import java.awt.Color;

public class Graveyard extends Feature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Town town;
	private int size;
	private GraveyardNode start;
	public Graveyard(String name,Town t) {
		this.name = name;
		town = t;
		size = 40;
		tutorialText = "Graveyards are teeming with undead.";
		generate();
		color = Color.RED;
	}
	@Override
	public void go() {
		Networking.setArea("dungeon");
		super.goHeader();
		Networking.sendStrong("Discord|imagesmall|dungeon|Dungeon|");
		start.go();
	}

	@Override
	public void passTime(double time) {
		start.passTime(time);
		start.timeFinish();
	}
	
	public void generate() {
		start = new GraveyardNode(size,town.getTier(),town,this);
	}
	public Shape getShape() {
		return Shape.STANDARD;
	}
	
	public enum Shape{
		STANDARD;
	}

}
