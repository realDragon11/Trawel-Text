package trawel;
import java.util.ArrayList;
import java.util.List;

import trawel.time.ContextType;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public class Plane extends TContextOwner{
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

	public List<Town> getTowns() {
		List<Town> list = new ArrayList<Town>();
		for(World w: worlds) {
			for(Island i: w.getislands()) {
				list.addAll(i.getTowns());
			}
		}
		
		return list;
	}
	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		for (World t: worlds) {
			if (t == Player.world) {
				timeScope.localEvents(t.contextTime(time, calling,true));
			}else {
				timeScope.localEvents(t.contextTime(time, calling));
			}
			
		}
		return null;
	}
	
	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.UNBOUNDED,this);
		timeSetup();
		for(World w: worlds) {
			w.reload();
		}
	}
}
