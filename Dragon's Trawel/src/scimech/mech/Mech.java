package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.Target;
import trawel.MenuGenerator;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.extra;

public abstract class Mech implements TurnSubscriber, Target{

	public boolean playerControlled = false;
	protected int heat = 0, energy = 0, hp;
	protected List<Mount> mounts = new ArrayList<Mount>();
	protected List<Systems> systems = new ArrayList<Systems>();

	public void takeHeat(int amount) {
		heat += amount;
	}
	
	public void roundStart() {
		for (Systems ss: systems) {
			ss.roundStart();
		}
		for (Mount mount: mounts) {
			mount.roundStart();
		}
		heat /=2;
	}
	
	@Override
	public void activate(Target t, TurnSubscriber ts) {
		if (this.playerControlled) {
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return "HP: " + hp +" Energy: " + energy + " Heat: " + heat;
						}});
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "mounts";
						}

						@Override
						public boolean go() {
							mountSelect();
							return false;
						}});
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "systems";
						}

						@Override
						public boolean go() {
							systemsSelect();
							return false;
						}});
					
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "end turn";
						}

						@Override
						public boolean go() {
							return true;
						}});
					return mList;
				}});
		}else {
			
		}
	}
	
	public void mountSelect() {
		
	}
	
	public void systemsSelect() {
		
	}
}
