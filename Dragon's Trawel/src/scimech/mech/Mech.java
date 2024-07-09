package scimech.mech;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuGeneratorPaged;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.MenuSelectNumber;
import scimech.combat.DamageEffect;
import scimech.combat.DamageMods;
import scimech.combat.DamageTypes;
import scimech.combat.Dummy;
import scimech.combat.MechCombat;
import scimech.combat.ResistMap;
import scimech.combat.TakeDamage;
import scimech.combat.Target;
import scimech.handlers.Savable;
import scimech.handlers.SaveHandler;
import scimech.mech.Mount.MenuMount;
import scimech.mech.Mount.MenuMountTarget;
import scimech.mech.Systems.MenuSystem;
import scimech.people.Pilot;
import scimech.people.Trait;
import scimech.people.TraitKeeper;
import trawel.helper.methods.extra;

public abstract class Mech extends MechPart implements TurnSubscriber, Target, Savable{

	public boolean playerControlled = false;
	protected int heat = 0;//for debug
	public int energy = 0;
	protected int hp;
	protected float speed, slow;
	protected int complexityCap;
	protected int weightCap;
	protected float dodgeBonus;
	protected List<Mount> mounts = new ArrayList<Mount>();
	protected List<Systems> systems = new ArrayList<Systems>();
	protected Pilot pilot;
	public String callsign;
	
	protected TraitKeeper keeper = new TraitKeeper();
	
	public abstract int baseHP();
	public abstract int baseSpeed();
	public abstract int baseComplexity();
	public abstract String getName();
	public abstract int baseDodge();
	public abstract int baseHeatCap();
	
	public int heatCap() {
		return baseHeatCap();
	}
	
	@Override
	public int getHP() {
		return hp;
	}
	
	@Override
	public TargetType targetType() {
		return TargetType.MECH;
	}
	public int getSpeed() {
		return baseSpeed()+(int)speed;
	}
	public void refreshForBattle() {
		//speed = extra.randRange(0,10);
		heat = 0;
		//energy = 0;
		slow = 0;
		this.repairLimit(100,this.getTrait(Trait.GREASE_MONKEY)*5);
		for (Mount m: mounts) {
			for (Fixture f: m.fixtures) {
				f.empDamage = 0;
			}
		}
		for (Systems s: systems) {
			s.empDamage = 0;
		}
	}
	
	public void fullRepair() {
		hp = getMaxHP();
	}

	public void takeHeat(int amount) {
		heat += amount;
	}
	
	public static final int SPEED_DIE = 10;
	
	@Override
	public void roundStart() {
		energy = 0;
		dodgeBonus = -slow;
		speed = extra.randRange(0,SPEED_DIE)-slow;
		for (Systems ss: systems) {
			ss.roundStart();
		}
		for (Mount mount: mounts) {
			mount.roundStart();
		}
		slow = 0;
		heat /=2;
		if (this.getTrait(Trait.MOBILE) > 0) {
			dodgeBonus+=2;
			speed +=10;
		}
		if (this.getTrait(Trait.EVASIVE) > 0) {
			dodgeBonus+=6;
		}
		this.repairLimit(this.getTrait(Trait.GREASE_MONKEY),this.getTrait(Trait.GREASE_MONKEY)*5);
	}
	
	@Override
	public void activate(Target t, TurnSubscriber ts) {
		if (this.playerControlled) {
			extra.menuGo(new MenuGenerator() {

				@Override
				public List<MenuItem> gen() {
					List<MenuItem> mList = new ArrayList<MenuItem>();
					int em = averageEMP();
					if (em > 4) {
						mList.add(new MenuLine() {

							@Override
							public String title() {
								return "EMP: " + ((em/5)*5) + "%";
							}});
					}
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return callsign + " ("+getName()+")" + "/"+pilot.getName() +" HP: " + hp + "/" + getMaxHP() +" Energy: " + energy + " Heat: " + heat+ "/" + heatCap();
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
			while (energy > 0 && MechCombat.mc.twoSided()) {
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
		return baseHP()+this.getTrait(Trait.HARDENED)*5+(this.getTrait(Trait.TOUGH) > 0 ? 50 : 0);//TODO
	}
	
	public class MenuMechTarget extends MenuSelect {//can be extended further

		protected Mech owner;
		protected Mount firing;
		@Override
		public String title() {
			return callsign + " ("+getName()+")" + "/"+pilot.getName() + " hp: " + hp + "/" + getMaxHP();
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
				ms.number = MechCombat.averageDamage(menuMechTarget.owner,mount,12);
				mList.add(ms);
				for (Mount m: menuMechTarget.owner.mounts) {
					if (m.checkFire()) {
						continue;
					}
					MenuMountTarget mmt = m.new MenuMountTarget();
					mmt.owner = m;
					mmt.damage = MechCombat.averageDamage(m,mount,8);
					mList.add(mmt);
				}
				return mList;
			}});
		if (MechCombat.mc.t != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean checkFire() {
		return hp <= 0;
	}
	
	@Override
	public int dodgeValue() {
		return (int) (baseDodge()+dodgeBonus);
	}
	public float addDodgeBonus(float bonus) {
		return dodgeBonus+=bonus;
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
				int totalSDam = (int) (value*map.calcMult(type, mods).systemDamageMult*MechCombat.SYSTEM_DAM_MULT);
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
						mech.takeHeat((int)(amount*extra.lerp(resistMap().calcMult(DamageTypes.BURN,DamageMods.EFFECT).effectMult,1f,.75f)));
					}
					break;
				case EMP:
					takeEMPDamage((int)(amount*resistMap().calcMult(DamageTypes.SHOCK,DamageMods.EFFECT).effectMult));
					break;
				case SLOW:
					amount *=resistMap().calcMult(DamageTypes.KINETIC,DamageMods.EFFECT).effectMult;
					if (!damaged.isDummy()) {
						Mech mech = (Mech)damaged;
						mech.slow+=amount;
						mech.dodgeBonus-=amount;
					}else {
						damaged.constructDummy().dodgeValue-=amount;
					}
					break;
				case ACID:
					if (!damaged.isDummy()) {
						amount *=resistMap().calcMult(DamageTypes.KINETIC,DamageMods.EFFECT).effectMult;
						Mech mech = (Mech)damaged;
						mech.takeSystemDamage((int) amount);
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
	
	public void takeEMPDamage(int dam) {
		int mDam = extra.randRange(0,dam);
		int sDam = dam-mDam;
		int[] arr = new int[mounts.size()];
		for (int i = 0; i < arr.length;i++) {
			arr[i] = 0;
		}
		for (int i = 0; i < mDam;i++) {
			int v = extra.randRange(0,arr.length-1);//Mounts MUST have at least one fixture
			arr[v]++;
		}
		for (int i = 0; i < arr.length;i++) {
			mounts.get(i).takeEMPDamage(arr[i]);
		}
		arr = new int[systems.size()];//TODO: should probably include slot size into it so they don't flood their mounts with shitty fixtures
		for (int i = 0; i < arr.length;i++) {
			arr[i] = 0;
		}
		for (int i = 0; i < sDam;i++) {
			int v = extra.randRange(0,arr.length-1);//Mounts MUST have at least one fixture
				arr[v]++;
		}
		for (int i = 0; i < arr.length;i++) {
			systems.get(i).takeEMPDamage(arr[i]);
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
		ResistMap traitMap = new ResistMap();
		float tv = extra.lerp(1f,.7f,Math.min(10, this.getTrait(Trait.THICK_SKULL)/10f));
		traitMap.put(DamageMods.AP,(tv+1)/2f, tv); 
		map.subMaps.add(traitMap);
		return map;
	}
	
	public boolean addMount(Mount m) {
		m.currentMech = this;
		if (m.getComplexity() > hardComplexityCap()-totalComplexity()) {
			extra.println("Hit complexity cap.");
			return false;
		}
		
		this.mounts.add(m);
		return true;
	}
	public boolean addSystem(Systems s) {
		s.currentMech = this;
		if (s.getComplexity() > hardComplexityCap()-totalComplexity()) {
			extra.println("Hit complexity cap.");
			return false;
		}
		
		if (s.getComplexity() > hardComplexityCap()-totalComplexity()) {
			extra.println("Hit complexity cap.");
			return false;
		}
		
		Class<? extends Systems> c = s.getClass();
		int count = 0;
		for (Systems sys: systems) {
			if (sys.getClass().equals(c)) {
				count++;
			}
		}
		
		if (count >= s.installLimit()) {
			extra.println("Hit install cap.");
			return false;	
		}
		this.systems.add(s);
		return true;
	}
	
	public void statistics() {
		extra.println(callsign + " ("+getName()+")" + "/"+pilot.getName() +" HP: " + hp + "/" + getMaxHP() +" Energy: " + energy + " Heat: " + heat+ "/" + heatCap());
		extra.println("Weight: " + this.totalWeight() + "/" + this.weightCap + " Complexity: "+ this.totalComplexity() + "/" +this.complexityCap);
		extra.println("Mounts: " + this.mounts.size() + " Systems: " + this.systems.size() + " Fixtures: " + this.totalFixtures());
		extra.println("Speed: " + this.getSpeed() + " Dodge: " + this.dodgeValue() +" Saved: " + this.getLockedComplexity() + "/"+this.baseComplexity());
		extra.println(keeper.toString());
		extra.println();
		pilot.statistics();
	}
	public int totalFixtures() {
		int total = 0;
		for (Mount m: mounts) {
			total += m.fixtures.size();
		}
		return total;
	}
	public void addSpeed(float i) {
		speed +=i;
		
	}
	public void clearHeat(int heat) {
		this.heat = Math.max(0,this.heat-heat); 
	}
	
	public int averageEMP() {
		int damageSum = 0;
		for (Mount m: mounts) {
			damageSum += m.averageEMP();
		}
		for (Systems s: systems) {
			damageSum += s.empDamage;
		}
		damageSum/=(mounts.size()+systems.size());
		return damageSum;
	}
	
	public void repair(int rep) {
		for (Mount m: mounts) {
			m.repair(rep);
		}
		for (Systems s: systems) {
			s.repair(rep);
		}
	}
	
	public void repairLimit(int rep,int limit) {
		for (Mount m: mounts) {
			m.repairLimit(rep,limit);
		}
		for (Systems s: systems) {
			s.repairLimit(rep,limit);
		}
	}
	
	public int getTrait(Trait t) {
		return keeper.getTrait(t)+pilot.getTrait(t);
	}
	
	@Override
	public String saveString() {
		String output = this.getClass().getName()+"&["+keeper.saveString()+"]{"+callsign +"," + weightCap+","+complexityCap+","+hp+",}{";
		for (Mount m: mounts) {
			output+=m.saveString() +";";
		}
		output+="}{";
		for (Systems s: systems) {
			output+=s.saveString() +";";
		}
		output+="}";
		return output;
	}
	
	public static Mech internalDeserial(String s,Mech add) throws Exception {
		int start = s.indexOf('[')+1;
		int end = s.indexOf(']');
		add.keeper = (TraitKeeper) SaveHandler.deserialize(s.substring(start,end));
		
		start = s.indexOf('{')+1;
		end = s.indexOf('}');
		String sub = s.substring(start, end);
		String[] sSubs = sub.split(",");
		add.callsign = sSubs[0];
		add.weightCap = Integer.parseInt(sSubs[1]);
		add.complexityCap = Integer.parseInt(sSubs[2]);
		add.hp = Integer.parseInt(sSubs[3]);
		
		start = end+2;
		end = s.indexOf('}',start);
		sub = s.substring(start, end);
		sSubs = sub.split(";");
		for (int i = 0; i < sSubs.length;i++) {
			add.mounts.add((Mount) SaveHandler.deserialize(sSubs[i]));
			add.mounts.get(i).currentMech = add;
		}
		start = end+2;
		end = s.indexOf('}',start);
		sub = s.substring(start, end);
		sSubs = sub.split(";");
		for (int i = 0; i < sSubs.length;i++) {
			add.systems.add((Systems) SaveHandler.deserialize(sSubs[i]));
			add.systems.get(i).currentMech = add;
		}
		
		return add;
	}
	public Pilot swapPilot(Pilot pilot2) {
		Pilot pilot3 = pilot;
		pilot = pilot2;
		return pilot3;
	}
	
	public int getLockedComplexity() {
		int total = 0;
		for (Systems ss: systems) {
			if (ss.locked) {
				total +=ss.getBaseComplexity();
			}
		}
		for (Mount mount: mounts) {
			if (mount.locked) {
				total += mount.baseComplexity();
			}
			for (Fixture f: mount.fixtures) {
				if (f.locked) {
					total += f.getBaseComplexity();
				}
			}
		}
		return total;
		
	}

}
