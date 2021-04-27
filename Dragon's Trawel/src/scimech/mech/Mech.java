package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import scimech.combat.DamageEffect;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.Dummy;
import scimech.combat.MechCombat;
import scimech.combat.ResistMap;
import scimech.combat.TakeDamage;
import scimech.combat.Target;
import scimech.mech.Fixture.MenuFixture;
import scimech.mech.Mount.MenuMount;
import scimech.mech.Mount.MenuMountTarget;
import scimech.mech.Systems.MenuSystem;
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
			complexityCap, weightCap;//for debug
	protected List<Mount> mounts = new ArrayList<Mount>();
	protected List<Systems> systems = new ArrayList<Systems>();
	protected Pilot pilot;
	public String callsign;
	
	public abstract int baseHP();
	public abstract int baseSpeed();
	public abstract int baseComplexity();
	public abstract String getName();
	public abstract int baseDodge();
	public abstract int baseHeatCap();
	
	public int heatCap() {
		return baseHeatCap();
	}
	
	public int getHP() {
		return hp;
	}
	
	public TargetType targetType() {
		return TargetType.MECH;
	}
	public int getSpeed() {
		return baseSpeed()+speed;
	}
	public void refreshForBattle() {
		speed = extra.randRange(0,10);
		heat = 0;
		energy = 0;
	}
	
	public void fullRepair() {
		hp = baseHP();
	}

	public void takeHeat(int amount) {
		heat += amount;
	}
	
	public void roundStart() {
		energy = 0;
		speed = extra.randRange(0,10);
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
							return callsign + " ("+getName()+") " + "/"+pilot.getName() +" HP: " + hp + "/" + getMaxHP() +" Energy: " + energy + " Heat: " + heat+ "/" + heatCap();
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
							return "statistics";
						}

						@Override
						public boolean go() {
							statistics();
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
			while (energy > 0) {
				boolean activated = false;
				for (int i = 0; i < 5;i++) {
					if (mounts.get(extra.randRange(0,mounts.size()-1)).aiFire()) {
						activated = true;
						break;
					}
				}
				if (activated == false) {
					energy--;
				}
			}
		}
	}
	
	public void mountSelect() {
		extra.menuGoPaged(new MenuGeneratorPaged() {

			@Override
			public List<MenuItem> gen() {
				this.header = new MenuLine() {

					@Override
					public String title() {
						return "Energy: " + energy + " heat: " + heat + "/" + heatCap();
					}
					
				};
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
				for (Systems s: systems) {
					MenuSystem ms = s.new MenuSystem();
					ms.sys =s;
					mList.add(ms);
				}
				return mList;
			}});
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
	
	
	public double weightPenalty() {//TODO
		int total = totalWeight();
		if (total <= weightCap) {
			return 1;
		}
		return (Math.pow(((total-weightCap)/2f),1.5f)+3f)/3f;
	}
	
	public int totalWeight() {
		int total = 0;
		for (Systems ss: systems) {
			total +=ss.getWeight();
		}
		for (Mount mount: mounts) {
			total += mount.getWeight();
		}
		return total;
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
						return "target hull est dam: " + number;
					}

					@Override
					public boolean go() {
						MechCombat.mc.t = menuMechTarget.owner;
						return true;
					}});
				ms.number = MechCombat.averageDamage(menuMechTarget.owner,mount,8);
				mList.add(ms);
				for (Mount m: menuMechTarget.owner.mounts) {
					if (m.checkFire()) {
						continue;
					}
					MenuMountTarget mmt = m.new MenuMountTarget();
					mmt.owner = m;
					mmt.damage = MechCombat.averageDamage(m,mount,6);
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
		return hp <= 0;
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
		d.resistMap = this.resistMap();
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
	
	@Override
	public TakeDamage takeDamage() {
		return new TakeDamage() {

			@Override
			public void take(DamageTypes type, DamageMods mods, int value, Target damaged) {
				ResistMap map = damaged.resistMap();
				int totalDam = (int) (value*map.calcMult(type, mods).hpDamageMult);
				int totalSDam = (int) (value*map.calcMult(type, mods).systemDamageMult);
				if (!damaged.isDummy()) {
					Mech m  = (Mech)damaged;
					m.takeSystemDamage(totalSDam);
				}
				damaged.takeHPDamage(totalDam);
			}

			@Override
			public void suffer(DamageEffect de,double amount, Target damaged) {
				switch (de) {
				case BURN:
					if (!damaged.isDummy()) {
						Mech mech = (Mech)damaged;
						mech.takeHeat((int)amount);
					}
					break;

				}
				
			}};
	}
	public void takeSystemDamage(int dam) {
		int mDam = extra.randRange(0,dam);
		int sDam = dam-mDam;
		int[] arr = new int[mounts.size()];
		for (int i = 0; i < arr.length;i++) {
			arr[i] = 0;
		}
		for (int i = 0; i < mDam;i++) {
			int v = extra.randRange(0,arr.length-1);//Mounts MUST have at least one fixture
			if (extra.chanceIn(2,3) && mounts.get(v).checkFire()) {
				if (extra.chanceIn(1,3)){
					i--;
					}
			}else {
				arr[v]++;
			}
		}
		for (int i = 0; i < arr.length;i++) {
			mounts.get(i).takeSystemDamage(arr[i]);
		}
		arr = new int[systems.size()];//TODO: should probably include slot size into it so they don't flood their mounts with shitty fixtures
		for (int i = 0; i < arr.length;i++) {
			arr[i] = 0;
		}
		for (int i = 0; i < sDam;i++) {
			int v = extra.randRange(0,arr.length-1);//Mounts MUST have at least one fixture
			if (extra.chanceIn(2,3) && systems.get(v).damage == 100) {
				i--;
			}else {
				arr[v]++;
			}
		}
		for (int i = 0; i < arr.length;i++) {
			systems.get(i).takeDamage(arr[i]);
		}
		
	}
	public float rating() {
		int cap = heatCap();
		if (heat < cap) {
			return 1;
		}
		return Math.max(extra.lerp(1,0,(heat-cap)/(cap)),.1f);
	}
	
	
	public abstract ResistMap internalResistMap();
	
	@Override
	public ResistMap resistMap() {
		ResistMap map = internalResistMap();
		for (Systems s: systems) {
			ResistMap sMap = s.resistMap();
			if (sMap != null) {
				map.subMaps.add(sMap);
			}
		}
		return map;
	}
	
	public void addMount(Mount m) {
		this.mounts.add(m);
		m.currentMech = this;
	}
	public void addSystem(Systems s) {
		this.systems.add(s);
		s.currentMech = this;
	}
	
	public void statistics() {
		extra.println(callsign + " ("+getName()+") " + "/"+pilot.getName() +" HP: " + hp + "/" + getMaxHP() +" Energy: " + energy + " Heat: " + heat+ "/" + heatCap());
		extra.println("Weight: " + this.totalWeight() + "/" + this.weightCap + " Complexity: "+ this.totalComplexity() + "/" +this.complexityCap);
		extra.println("Mounts: " + this.mounts.size() + " Systems: " + this.systems.size());
		extra.println("Speed: " + this.getSpeed() + " Dodge: " + this.dodgeValue());
	}
}
