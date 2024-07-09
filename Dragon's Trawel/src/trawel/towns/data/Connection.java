package trawel.towns.data;
import trawel.helper.methods.extra;
import trawel.personal.classless.Skill;
import trawel.personal.people.Player;
import trawel.towns.contexts.Town;
import trawel.towns.features.Feature;
import trawel.towns.features.fight.Arena;
import trawel.towns.features.services.Store;

/**
 * 
 * @author dragon
 * 5/30/2018
 */
public class Connection implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private Town townA, townB;
	private final byte type;
	private final String nameString;
	private int dupeNum = -1;
	
	public Connection(String name, Town t1, Town t2, ConnectType connectType) {
		townA = t1;
		townB = t2;
		type = (byte) connectType.ordinal();
		nameString = name;
	}
	
	public Town[] getTowns(){
		return new Town[] {townA,townB};
	}

	public double getTime() {
		return WorldGen.distanceBetweenTowns(townA,townB,getType());
	}
	
	public double getRawTime() {
		return WorldGen.rawConnectTime(townA,townB,getType());
	}
	
	public String getTimeDisp() {
		return displayTime(getTime());
	}
	
	public static String displayTime(double time) {
		if (time < 1) {
			return "<1 hours";
		}
		if (time < 3) {
			return "<"+((int)time+1)+" hours";
		}
		if (time < 30) {
			return "~"+((int)time) + " hours";
		}
		return "~"+((int)Math.round(time/24)) + " days";
	}
	
	public enum ConnectClass {
		LAND, SEA, MAGIC
	}
	
	public enum ConnectType {
		//all times are given in miles per hour, and there are 2 miles per cell accounted for later
		
		//DOLATER: find more reliable travel time sources to ballpark with gameplay
		PATH("path",ConnectClass.LAND,0,0,1.5),//slow walk
		ROAD("road",ConnectClass.LAND,0,0,2.5),//walk
		CARV("caravan",ConnectClass.LAND,0.5,0,4.0),//caravan
		SHIP("ship",ConnectClass.SEA,3.0,1.5,7.0),//normal ship?
		TELE("tele",ConnectClass.MAGIC,4.0,0,40.0);

		public final ConnectClass type;
		private final String desc;
		public final double startTime;
		public final double endTime;
		/**
		 * cells per hour
		 * (one cell is 2 miles)
		 */
		public final double perHourSpeed;
		ConnectType(String name, ConnectClass _type, double _startTime, double _endTime, double _speed){
			desc = name;
			type = _type;
			startTime = _startTime;
			endTime = _endTime;
			perHourSpeed = _speed/WorldGen.distanceScale;
		}
		public String desc() {
			return desc;
		}
	}

	public ConnectType getType() {
		return ConnectType.values()[type];
	}

	/**
	 * check Town's displayLine if seeing indirectly, use this or connection's displayLine otherwise
	 */
	public void display(int style,Town town1) {
		Town ot = otherTown(town1);
		String visitColor = extra.PRE_WHITE;
		switch (ot.visited) {
		case 0: visitColor = extra.VISIT_NEW; ot.visited = 1;break;
		case 1: visitColor = extra.VISIT_SEEN;break;
		case 2: visitColor = extra.VISIT_BEEN;break;
		case 3: visitColor = extra.VISIT_OWN;break;
		}
		extra.println(visitColor + getName() + " to " + ot.getName() + " {Tier: "+ot.getTier()+"} ("+dir(town1,ot)+")");
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
			extra.println(ot.getName() + " has an arena.");
			}
		}
	}
	
	/**
	 * note that this DOES count as directly seeing the town
	 */
	public String displayLine(Town from) {
		Town ot = otherTown(from);
		String visitColor = extra.PRE_WHITE;
		switch (ot.visited) {
		case 0: visitColor = extra.VISIT_NEW; ot.visited = 1;break;
		case 1: visitColor = extra.VISIT_SEEN;break;
		case 2: visitColor = extra.VISIT_BEEN;break;
		case 3: visitColor = extra.VISIT_OWN;break;
		}
		return visitColor +getName() + " to " + ot.getName()
		+ " {Level: "+ot.getTier()+"} ("+dir(from,ot)+", "+getTimeDisp()+")"
		+ (Player.hasSkill(Skill.TOWNSENSE) ? " " +ot.getName() + " has " + ot.getConnects().size() + " connections." : "");
	}
	
	public static String dir(Town t1, Town t2) {
		if (t1.getIsland().getWorld() != t2.getIsland().getWorld()) {
			return "?";
		}
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
	
	public boolean isWorldConnection() {
		return townA.getIsland().getWorld() != townB.getIsland().getWorld();
	}
	
	/**
	 * number of connections that have the same towns as the two links
	 */
	public int dupeNum() {
		return dupeNum;
	}
	
	public void setDupeNum(int i) {
		dupeNum = i;
	}
	
	public double getAIWanderAppeal(Town from) {
		return baseTypeAppeal()*(otherTown(from).occupantNeed()/dupeNum());
	}
	
	public double baseTypeAppeal() {
		switch (getType()) {
		case PATH:
			return .4;
		case ROAD:
			return 1;
		case CARV:
			return 1.05;
		case SHIP:
			return .6;
		case TELE:
			return .2;
		}
		throw new RuntimeException("invalid connect type for appeal");
	}
	
}
