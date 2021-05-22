package trawel;
import java.util.ArrayList;

/**
 * 
 * @author Brian Malone
 * 5/30/2018
 */

public class Island implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Town> towns;
	private String name;
	private World world;
	
	public Island(String name, World w) {
		towns = new ArrayList<Town>();
		this.name = name;
		w.addIsland(this);
		world = w;
	}
	
	public void addTown(Town t) {
		towns.add(t);
	}

	public ArrayList<Town> getTowns() {
		return towns;
	}

	public void passTime(double time) {
		for (Town t: towns) {
			t.passTime(time);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public World getWorld() {
		return world;
	}

	
}
