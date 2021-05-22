package trawel;
import java.util.ArrayList;
import java.util.List;

public class Plane implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<World> worlds = new ArrayList<World>();
	private Player player;
	
	public void addWorld(World w) {
		worlds.add(w);	
	}
	
	public ArrayList<World> worlds() {
		return worlds;
	}
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player p) {
		player = p;
	}

	public void passTime(double time) {
		for (World w: worlds) {
		
		w.passTime(time);	
		}
	}

	public List<Town> getTowns() {
		List<Town> list = new ArrayList<Town>();
		for(World w: worlds) {
			for(Island i: w.getislands()) {
				list.addAll(i.getTowns());
			}
		}
		
		return list;
	}
}
