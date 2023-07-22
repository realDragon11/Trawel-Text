package trawel;
import java.awt.Color;
import java.util.ArrayList;

import org.nustaq.serialization.annotations.OneOf;

/**
 * 
 * @author Brian Malone
 * 5/30/2018
 */
public class Connection implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Town> towns;
	private double time;
	@OneOf({"road","ship","teleport"})
	private String type;
	private String nameString;
	
	public Connection(Town t1, Town t2,double time, String type) {
		towns = new ArrayList<Town>();
		towns.add(t1);
		towns.add(t2);
		this.time = (time);
		this.type = type;
	}
	
	public ArrayList<Town> getTowns(){
		return towns;
	}

	public double getTime() {
		return time;
	}

	public String getType() {
		return type;
	}

	public void display(int style,Town town1) {
		Town ot = otherTown(town1);
		switch (ot.visited) {
		case 0: extra.print(extra.inlineColor(extra.colorMix(Color.ORANGE,Color.WHITE,.5f))); ot.visited = 1;break;
		case 1: extra.print(extra.inlineColor(extra.colorMix(Color.YELLOW,Color.WHITE,.5f)));break;
		case 2: extra.print(extra.inlineColor(extra.colorMix(Color.BLUE,Color.WHITE,.5f)));break;
		case 3: extra.print(extra.inlineColor(extra.colorMix(Color.GREEN,Color.WHITE,.5f)));;break;
		}
		extra.println(getName() + " to " + ot.getName() + " {Level: "+ot.getTier()+"} ("+dir(town1,ot)+")");
		if (Player.hasSkill(Skill.TOWNSENSE)) {
			extra.println(ot.getName() + " has " + ot.getConnects().size() + " connections.");
		}
		if (Player.hasSkill(Skill.TELESENSE) && ot.hasTeleporters()) {
			extra.println(ot.getName() + " has a teleport shop.");
		}
		if (Player.hasSkill(Skill.SHIPSENSE) && ot.hasPort()) {
			extra.println(ot.getName() + " has a shipyard.");
		}
		if (Player.hasSkill(Skill.TIERSENSE)) {
			extra.println(ot.getName() + " is level "+ ot.getTier() + ".");
		}
		if (Player.hasSkill(Skill.SHOPSENSE)){
			Boolean doIt = false;
			for (Feature f: ot.getFeatures()) {
				if (Store.class.isInstance(f)) {
					doIt = true;
					break;
				}
			}
			if (doIt) {
			extra.println(ot.getName() + " has a shop.");
			}
		}
		
		if (Player.hasSkill(Skill.ARENASENSE)){
			Boolean doIt = false;
			for (Feature f: ot.getFeatures()) {
				if (Arena.class.isInstance(f)) {
					doIt = true;
					break;
				}
			}
			if (doIt) {
			extra.println(ot.getName() + " has a shop.");
			}
		}
	}
	private String dir(Town t1, Town t2) {
		//vectors: int angle = (int) Math.atan2((t1.getLocation().x*t2.getLocation().y)-(t1.getLocation().y*t2.getLocation().x),(t1.getLocation().x*t2.getLocation().x)+(t1.getLocation().y*t2.getLocation().y));
		int angle = (int) Math.toDegrees(Math.atan2(t2.getLocationX()-t1.getLocationX(),t2.getLocationY()-t1.getLocationY()))-90;
		while(angle <0) {
			angle +=360;
		}
		//extra.println(angle +" x:" +(t2.getLocation().x-t1.getLocation().x) + " y:" +(t2.getLocation().y-t1.getLocation().y));
		if (angle < 22 || angle >= (360-22)) {
			return "E";
		}
		if (angle >= 22  && angle < 45+22) {
			return "NE";
		}
		if (angle >= 45+22  && angle < 90+22) {
			return "N";
		}
		if (angle >= 90+22  && angle < 180-22) {
			return "NW";
		}
		if (angle >= 180-22  && angle < 180+22) {
			return "W";
		}
		if (angle >= 180+22  && angle < 270-22) {
			return "SW";
		}
		if (angle >= 270-22  && angle < 270+22) {
			return "S";
		}
		if (angle >= 270+22  && angle < 360-22) {
			return "SE";
		}
		return "error";
	}

	public Town otherTown(Town town1) {
		ArrayList<Town> towns2 = (ArrayList<Town>) towns.clone();
		towns2.remove(town1);
		return towns2.get(0);
	}

	public String getName() {
		return nameString;
	}

	public void setName(String nameString) {
		this.nameString = nameString;
	}
}
