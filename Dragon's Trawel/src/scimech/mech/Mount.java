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
import scimech.combat.Target.TargetType;
import scimech.mech.Fixture.MenuFixture;
import scimech.mech.Mech.MenuMechTarget;
import scimech.units.fixtures.LightAutocannon;
import trawel.MenuGenerator;
import trawel.MenuGeneratorPaged;
import trawel.MenuItem;
import trawel.MenuLine;
import trawel.MenuSelect;
import trawel.extra;

public abstract class Mount implements TurnSubscriber, Target{

	protected int heat = 0;
	protected List<Fixture> fixtures = new ArrayList<Fixture>();
	public Mech currentMech;
	public boolean fired = false;
	
	public TargetType targetType() {
		return TargetType.MOUNT;
	}
	
	@Override
	public int getHP() {
		return currentMech.getHP();
	}
	
	@Override
	public void activate(Target t, TurnSubscriber ts) {
		int before = t.getHP();
		for (Fixture f: fixtures) {
			if (f.powered) {
				f.activate(t,this);
			}
			if (!t.isDummy()) {
				Mech m = null;
				switch (t.targetType()) {
				case MECH:
					m = (Mech)t;
					break;
				case MOUNT:
					m = ((Mount)t).currentMech;
					break;
				}
				if (m.checkFire()) {
					
					if (MechCombat.mc.activeMechs.contains(t)) {
						extra.print(m.callsign + " is taken out! ");
						MechCombat.mc.activeMechs.remove(t);
					}
				}
			}

		}
		if (!t.isDummy()) {
			extra.println();
			extra.println(t.targetName() + " takes " + (before-t.getHP())  + " damage!");
			this.bonusEffect(t,before-t.getHP());
			currentMech.energy-=this.getEnergyDraw();//TODO: did I forget to add this?
		}
	}
	
	public void bonusEffect(Target t, int damage) {
		//abstract but not required
	}

	public void takeHeat(int amount) {
		amount *=currentMech.complexityHeatPenalty();
		heat+=amount;
		currentMech.takeHeat(amount);
	}
	
	public void roundStart() {
		fired = false;
		for (Fixture f: fixtures) {
			if (f.overclocked) {
				f.heatCheck(heat*2);
			}else {
				f.heatCheck(heat);
			}
			f.roundStart();
			f.empDecay();
		}
		heat /=2;
	}
	
	public void togglePowerAll(boolean state) {
		for (Fixture f: fixtures) {
			f.powered = state;
		}
	}
	
	public int getEnergyDraw() {
		int total = 0;
		for (Fixture f: fixtures) {
			if (f.powered == true) {
				total += f.getEnergyDraw() *(f.overclocked ? 1.4f : 1);
			}
		}
		return total;
	}
	
	public void examine() {
		extra.menuGo(new MenuGenerator(){

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add( new MenuLine() {
					@Override
					public String title() {
						return "heat: "  + heat + " draw: " + getEnergyDraw();
					}});
				
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "back";
					}

					@Override
					public boolean go() {
						return true;
					}});
				if (fired == false && getEnergyDraw() <= currentMech.energy) {
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "activate";
					}

					@Override
					public boolean go() {
						return targeting();
					}});
				}
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "manage fixtures";
					}

					@Override
					public boolean go() {
						manageFixtures();
						return true;
					}});
				
				return mList;
			}});
		
	}
	public abstract String getName();
	public boolean targeting() {
		List<Mech> enemies = MechCombat.enemies(this.currentMech);
		//fired =  false;
		Mount fixed = this;
		extra.menuGoPaged(new MenuGeneratorPaged() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuSelect() {

					@Override
					public String title() {
						return "cancel";
					}

					@Override
					public boolean go() {
						return true;
					}});
				for (Mech e: enemies) {
					MenuMechTarget mm = e.new MenuMechTarget();
					mm.owner = e;
					mm.firing = fixed;
					mList.add(mm);
				}
				return mList;
			}});
		
		
		if (MechCombat.mc.t != null) {
			fired = true;
			this.activate(MechCombat.mc.t,null);
		}
		MechCombat.mc.t = null;
		return fired;
	}
	
	public void manageFixtures() {
		extra.menuGoPaged(new MenuGeneratorPaged(){

			@Override
			public List<MenuItem> gen() {
				this.header = new MenuLine() {

					@Override
					public String title() {
						return "heat: "  + heat + " draw: " + getEnergyDraw();
					}};
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
				for (Fixture f: fixtures) {
					MenuFixture mf = f.new MenuFixture();
					mf.fix = f;
					mList.add(mf);
				}
				return mList;
			}});
	}
	
	public class MenuMount extends MenuSelect {//can be extended further

		@Override
		public String title() {
			int damageSum = 0;
			for (Fixture f: fixtures) {
				damageSum += f.damage;
			}
			damageSum/=fixtures.size();
			String damString = null;
			if (damageSum > 30) {
				if (damageSum > 60) {
					if (damageSum > 90) {
						damString = "destroyed";
					}else {
						damString = "damaged";
					}
				}else {
					damString = "scratched";
				}
			}
			return getName() + " heat: "  + heat + " draw: " + getEnergyDraw() + (damString == null ? "" : " " + damString) + " " + (fired ? "used" :(getEnergyDraw()>currentMech.energy ?"":"ready"));
		}
		

		@Override
		public boolean go() {
			examine();
			return false;
		}
	}
	
	public class MenuMountTarget extends MenuSelect{
		
		protected Mount owner;
		protected int damage;
		
		@Override
		public String title() {
			int damageSum = 0;
			for (Fixture f: fixtures) {
				damageSum += f.damage;
			}
			damageSum/=fixtures.size();
			String damString = null;
			if (damageSum > 30) {
				if (damageSum > 60) {
					if (damageSum > 90) {
						damString = "destroyed";
					}else {
						damString = "damaged";
					}
				}else {
					damString = "scratched";
				}
			}
			return getName() + " heat: "  + heat + " draw: " + getEnergyDraw() + (damString == null ? "" : " " + damString) + " est dam:" + damage;
		}
		
		@Override
		public boolean go() {
			MechCombat.mc.t = owner;
			return true;
		}
	}
	
	public int averageEMP() {
		int damageSum = 0;
		for (Fixture f: fixtures) {
			damageSum += f.empDamage;
		}
		damageSum/=fixtures.size();
		return damageSum;
	}
	
	public abstract int baseWeight();
	
	public int getWeight() {
		int total = baseWeight();
		for (Fixture f: fixtures) {
			total += f.getWeight();
		}
		return total;
	}
	
	public abstract int baseComplexity();
	
	public int getComplexity() {
		int total = baseComplexity();
		for (Fixture f: fixtures) {
			total += f.getComplexity();
		}
		return total;
	}
	
	public abstract int baseSlots();
	
	public int usedSlots() {
		int total = 0;
		for (Fixture f: fixtures) {
			total += f.getSlots();
		}
		return total;
	}
	
	public boolean checkFire() {
		int damageSum = 0;
		for (Fixture f: fixtures) {
			damageSum += f.damage;
		}
		damageSum/=fixtures.size();
		return damageSum > 98;
	}
	
	public abstract float dodgeMult();
	
	@Override
	public int dodgeValue() {
		return (int) (currentMech.dodgeValue()*dodgeMult());
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
		currentMech.takeHPDamage(i);
	}
	
	@Override
	public String targetName() {
		return currentMech.callsign + " " + this.getName();
	}
	
	@Override
	public TakeDamage takeDamage() {
		return new TakeDamage() {

			@Override
			public void take(DamageTypes type, DamageMods mods, int value, Target damaged) {
				ResistMap map = damaged.resistMap();
				int totalDam = (int) (value*map.calcMult(type, mods).hpDamageMult);
				int totalSDam = (int) (value*map.calcMult(type, mods).systemDamageMult*MechCombat.SYSTEM_DAM_MULT);
				if (!damaged.isDummy()) {
					Mount m  = (Mount)damaged;
					m.takeSystemDamage(totalSDam);
				}
				damaged.takeHPDamage(totalDam);
			}

			@Override
			public void suffer(DamageEffect de,double amount, Target damaged) {
				switch (de) {
				case BURN:
					if (!damaged.isDummy()) {
						Mount mech = (Mount)damaged;
						mech.takeHeat((int)amount);
					}
					break;
				case EMP:
					takeEMPDamage((int)amount);
					break;

				}
			}};
	}

	public void takeSystemDamage(int dam) {
		int[] arr = new int[fixtures.size()];//TODO: should probably include slot size into it so they don't flood their mounts with shitty fixtures
		for (int i = 0; i < arr.length;i++) {
			arr[i] = 0;
		}
		for (int i = 0; i < dam;i++) {
			int v = extra.randRange(0,arr.length-1);//Mounts MUST have at least one fixture
			if (extra.chanceIn(2,3) && fixtures.get(v).damage == 100) {
				i--;
			}else {
				arr[v]++;
			}
		}
		for (int i = 0; i < arr.length;i++) {
			fixtures.get(i).takeDamage(arr[i]);
		}
		
	}
	
	public void takeEMPDamage(int dam) {
		int[] arr = new int[fixtures.size()];//TODO: should probably include slot size into it so they don't flood their mounts with shitty fixtures
		for (int i = 0; i < arr.length;i++) {
			arr[i] = 0;
		}
		for (int i = 0; i < dam;i++) {
			int v = extra.randRange(0,arr.length-1);//Mounts MUST have at least one fixture
			arr[v]++;
		}
		for (int i = 0; i < arr.length;i++) {
			fixtures.get(i).takeEMPDamage(arr[i]);
		}
		
	}

	public boolean addFixture(Fixture f) {
		if (f.getSlots() > (baseSlots()-usedSlots())) {
			return false;
		}
		if (f.getComplexity() > currentMech.hardComplexityCap()-currentMech.totalComplexity()) {
			return false;
		}
		fixtures.add(f);
		f.currentMount = this;
		return true;
	}

	public boolean aiFire() {
		if (this.fired == true) {
			return false;
		}
		if (currentMech.energy < this.getEnergyDraw()) {
			return false;//TODO: make the ai willing to turn off systems
		}
		
		List<Mech> targets = MechCombat.enemies(this.currentMech);
		this.activate(extra.randList(targets), currentMech);
		fired = true;
		return true;
	}

	public void clearHeat(int heat) {
		this.heat = Math.max(0,this.heat-heat); 
	}

}
