package trawel;
import java.util.ArrayList;
import java.util.List;

import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

/**
 * 
 * @author Brian Malone
 * 5/30/2018
 */

public class Island extends TContextOwner{
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public World getWorld() {
		return world;
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		for (Town t: towns) {
			timeScope.localEvents(t.contextTime(time, calling));
		}
		return null;
	}
	
	@Override
	public void reload() {
		super.reload();
		for(Town t: towns) {
			t.reload();
		}
	}
	
	@Override
	public void prepareSave() {
		super.prepareSave();
		for(Town t: towns) {
			t.prepareSave();
		}
	}

	
}
