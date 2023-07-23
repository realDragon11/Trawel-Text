package trawel.towns;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.nustaq.serialization.annotations.OneOf;

import trawel.WorldGen;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.personal.people.Skill;
import trawel.towns.fight.Arena;
import trawel.towns.services.Store;

/**
 * 
 * @author dragon
 * 5/30/2018
 */
public class Connection implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private Town townA, townB;
	private double time;
	@OneOf({"road","ship","teleport"})
	private final byte type;
	private final String nameString;
	
	public Connection(String name, Town t1, Town t2,double time, String typeS) {
		townA = t1;
		townB = t2;
		this.time = (time);
		switch (typeS) {
		case "road":
			type = 0;
			break;
		case "ship":
			type = 1;
			break;
		case "teleport":
			type = 2;
			break;
		default:
			throw new RuntimeException("invalid connect type");
		}
		nameString = name;
	}
	public Connection(String name, Town t1, Town t2, ConnectType connectType) {
		townA = t1;
		townB = t2;
		time = 0;
		type = (byte) connectType.ordinal();
		nameString = name;
	}
	
	public Town[] getTowns(){
		return new Town[] {townA,townB};
	}

	public double getTime() {
		return WorldGen.distanceBetweenTowns(townA,townB,getType());
	}
	
	public enum ConnectType {
		ROAD("road"),SHIP("ship"),TELE("tele");

		private final String desc;
		ConnectType(String name){
			desc = name;
		}
		public String desc() {
			return desc;
		}
	}

	public ConnectType getType() {
		return ConnectType.values()[type];
	}

	public void display(int style,Town town1) {
		Town ot = otherTown(town1);
		String visitColor = extra.PRE_WHITE;
		switch (ot.visited) {
		case 0: visitColor = extra.COLOR_NEW; ot.visited = 1;break;
		case 1: visitColor = extra.COLOR_SEEN;break;
		case 2: visitColor = extra.COLOR_BEEN;break;
		case 3: visitColor = extra.COLOR_OWN;break;
		}
		extra.println(visitColor + getName() + " to " + ot.getName() + " {Level: "+ot.getTier()+"} ("+dir(town1,ot)+")");
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
	private static String dir(Town t1, Town t2) {
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
		if (town1.equals(townA)) {
			return townB;
		}
		if (town1.equals(townB)) {
			return townA;
		}
		throw new RuntimeException("neither town was in the connect: " +town1.getName() +" . " + this.getName());
	}

	public String getName() {
		return nameString;
	}
}
