package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.Dummy;
import scimech.combat.MechCombat;
import scimech.combat.Target;
import scimech.mech.Fixture.MenuFixture;
import scimech.mech.Mount.MenuMount;
import scimech.mech.Mount.MenuMountTarget;
import scimech.people.Pilot;
import trawel.MenuGenerator;
import trawel.MenuGeneratorPaged;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.MenuSelectNumber;
import trawel.extra;

public abstract class Mech implements TurnSubscriber, Target{

	public boolean playerControlled = false;
	protected int heat = 0, energy = 0, hp, speed, 
			complexityCap = 30;//for debug
	protected List<Mount> mounts = new ArrayList<Mount>();
	protected List<Systems> systems = new ArrayList<Systems>();
	protected Pilot pilot;
	public String callsign;
	
	public abstract int baseHP();
	public abstract int baseSpeed();
	public abstract int baseComplexity();
	public abstract String getName();
	public abstract int baseDodge();
	
	
	public TargetType targetType() {
		return TargetType.MECH;
	}
	public int getSpeed() {
		return baseSpeed()+speed;
	}
	public void refreshForBattle() {
		speed = extra.randRange(0,10);
	}
	
	public void fullRepair() {
		hp = baseHP();
	}

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
		extra.menuGoPaged(new MenuGeneratorPaged() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}});
				for (Mount m: mounts) {
					MenuMount mm = m.new MenuMount();
					mList.add(mm);
				}
				return mList;
			}});
	}
	
	public void systemsSelect() {
		
	}
	
	public int totalComplexity() {
		int total = this.baseComplexity();
		for (Systems ss: systems) {
			total +=ss.getComplexity();
		}
		for (Mount mount: mounts) {
			total += mount.getComplexity();
		}
		return total;
	}
	public double complexityHeatPenalty() {
		int total = totalComplexity();
		if (total <= complexityCap) {
			return 1;
		}
		return (Math.pow(((total-complexityCap)/2f),1.5f)+3f)/3f;
	}
	
	public int hardComplexityCap() {
		return complexityCap+20;
	}
	
	public int getMaxHP() {
		return baseHP();//TODO
	}
	
	public class MenuMechTarget extends MenuSelect {//can be extended further

		protected Mech owner;
		protected Mount firing;
		@Override
		public String title() {
			return callsign + " ("+getName()+") " + "/"+pilot.getName() + " hp: " + hp + "/" + getMaxHP();
		}
		

		@Override
		public boolean go() {
			return targetMenu(this,firing);
		}}
	
	public boolean targetMenu(MenuMechTarget menuMechTarget, Mount mount) {
		extra.menuGoPaged(new MenuGeneratorPaged() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "targeting: " +menuMechTarget.title();
					}});
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "cancel";
					}

					@Override
					public boolean go() {
						return true;
					}});
				MenuSelectNumber ms = (new MenuSelectNumber() {

					@Override
					public String title() {
						return "target mech " + number;
					}

					@Override
					public boolean go() {
						MechCombat.mc.t = menuMechTarget.owner;
						return true;
					}});
				ms.number = MechCombat.averageDamage(menuMechTarget.owner,mount,4);
				mList.add(ms);
				for (Mount m: menuMechTarget.owner.mounts) {
					if (m.checkFire()) {
						continue;
					}
					MenuMountTarget mmt = m.new MenuMountTarget();
					mmt.owner = m;
					mmt.damage = MechCombat.averageDamage(m,mount,3);
					mList.add(mmt);
				}
				return mList;
			}});
		if (MechCombat.mc.t != null) {
			return true;
		}
		return false;
	}
	
	public boolean checkFire() {
		return hp > 0;
	}
	
	@Override
	public int dodgeValue() {
		return baseDodge();
	} 
	
	@Override
	public boolean isDummy() {
		return false;
	}
	
	@Override
	public Dummy constructDummy() {
		Dummy d = new Dummy();
		d.dodgeValue = this.dodgeValue();
		d.takeDamage = this.takeDamage();
		d.targetType = this.targetType();
		return d;
	}
	
	@Override
	public void takeHPDamage(int i) {
		hp-=i;
	}
	
	@Override
	public String targetName() {
		return callsign + " hull";
	}
}
