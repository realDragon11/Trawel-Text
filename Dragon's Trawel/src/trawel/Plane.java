package trawel;
import java.util.ArrayList;

public class Plane implements java.io.Serializable{
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
}
