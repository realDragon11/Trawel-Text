package trawel.towns.contexts;
import java.util.ArrayList;
import java.util.List;

import trawel.time.ContextLevel;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

/**
 * 
 * @author dragon
 * 5/30/2018
 */

public class Island extends TContextOwner{
	
	private static final long serialVersionUID = 1L;
	
	private List<Town> towns;
	private String name;
	private World world;
	private IslandType type;
	
	public enum IslandType{
		ISLAND("island","on"),
		POCKET("pocket dimension","in");
		
		public final String name, locDesc;
		IslandType(String _name, String _locDesc){
			name = _name;
			locDesc = _locDesc;
		}
	}
	
	public Island(String _name, World w, IslandType _type) {
		towns = new ArrayList<Town>();
		name = _name;
		w.addIsland(this);
		world = w;
		type = _type;
	}
	public Island(String name, World w) {
		this(name,w,IslandType.ISLAND);
	}
	/**
	 * only for tests!
	 * @param testOnly
	 */
	public Island(boolean testOnly) {
		towns = new ArrayList<Town>();
		this.name = "test";
	}
	
	public void addTown(Town t) {
		towns.add(t);
	}

	public List<Town> getTowns() {
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
	
	@Override
	public List<TimeEvent> consumeEvents(List<TimeEvent> list) {
		return list;//MAYBELATER
	}
	@Override
	public ContextLevel contextLevel() {
		return ContextLevel.ISLAND;
	}
	public String getTypeName() {
		return type.name;
	}
	public String getLocDesc() {
		return type.locDesc;
	}
	public IslandType getIslandType() {
		return type;
	}
	
}
