package trawel;
import java.util.ArrayList;

/**
 * 
 * @author Brian Malone
 * 5/30/2018
 */
public class Connection implements java.io.Serializable{
	private ArrayList<Town> towns;
	private double time;
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
		extra.println(getName() + " to " + ot.getName());
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
