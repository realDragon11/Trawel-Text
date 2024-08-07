package trawel.towns.features.fort.features;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.battle.Combat;
import trawel.battle.Combat.SkillCon;
import trawel.core.Input;
import trawel.core.Networking;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.core.Networking.Area;
import trawel.helper.constants.TrawelColor;
import trawel.personal.AIClass;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.time.TrawelTime;
import trawel.towns.contexts.Town;
import trawel.towns.contexts.World;
import trawel.towns.features.Feature;
import trawel.towns.features.fort.FortFeature;
import trawel.towns.features.fort.FortFoundation;
import trawel.towns.features.fort.elements.LSkill;
import trawel.towns.features.fort.elements.LaborType;
import trawel.towns.features.fort.elements.Laborer;
import trawel.towns.features.fort.elements.SubSkill;

/**
 * 
 * @author dragon
 *
 * The most important fort feature. Holds extra data and the build menu.
 */
public class FortHall extends FortFeature {

	private static final long serialVersionUID = 1L;
	public List<Person> allies = new ArrayList<Person>();
	
	public double forgeTimer = 24.0*7;
	public double fightTimer = 24.0*14;
	public double enchantTimer = 24.0*7;
	
	public int aetherBank = 0;
	public double offenseTimer = 24.0;
	
	public FortHall(int tier, Town town) {
		this.name = "Fort Hall";
		this.town = town;
		this.tier = tier;
		laborer = new Laborer(LaborType.CHIEF);
	}
	
	@Override
	public String getColor() {
		return TrawelColor.F_SERVICE;
	}
	
	@Override
	public String nameOfType() {
		return "Fort Hall";
	}
	
	@Override
	public String nameOfFeature() {
		return "Fort Hall";
	}
	
	@Override
	public Area getArea() {
		return Area.MISC_SERVICE;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public int getDefenceRating() {
		return 0;
	}

	@Override
	public int getOffenseRating() {
		return 0;
	}
	
	public int getTotalDefenceRating() {
		int sum = 0;
		for (Feature f: town.getFeatures()) {
			sum += ((FortFeature)f).getDefenceRating();
		}
		return sum;
	}

	public int getTotalOffenseRating() {
		int sum = 0;
		for (Feature f: town.getFeatures()) {
			sum += ((FortFeature)f).getOffenseRating();
		}
		return sum;
	}

	@Override
	public void go() {
		if (this.getOwner() != Player.player) {
			final float levelMult = getUnEffectiveLevel();
			int cost = (int) ((1000*levelMult)+(aetherBank*Player.NORMAL_AETHER_RATE));
			Print.println(TrawelColor.SERVICE_CURRENCY+"Buy this for fort for "+cost+" "+World.currentMoneyString()+"? (You have " + Player.player.getGold()+")");
			if (Input.yesNo()) {
				if (Player.player.getGold() < cost) {
					Print.println("You can't afford to buy this fort.");
				}else {
					Networking.unlockAchievement("fort1");
					Player.player.addGold(-cost);
					this.setOwner(Player.player);//now cascades
					/*for (Feature f: this.town.getFeatures()) {
						f.setOwner(Player.player);
					}*/
					this.town.visited=3;
					while (allies.size() < 5) {
						allies.add(RaceFactory.getDueler(tier));
					}
				}
			}
		}
		if (this.getOwner() == Player.player) {
			
			Input.menuGo(new MenuGenerator() {
				@Override
				public List<MenuItem> gen() {
					List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return "You have " + allies.size() + " soldiers here and "+Player.showGold()+".";
						}});
					if (allies.size() < 10) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return TrawelColor.SERVICE_CURRENCY+"Buy a soldier ("+World.currentMoneyDisplay(getSoldierCost())+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= getSoldierCost()) {
								Player.player.addGold(-getSoldierCost());
								allies.add(RaceFactory.getDueler(tier));
							}else {
								Print.println("You can't afford another soldier.");
							}
							return false;
						}
					});
					}
					if (town.fortSizeLeft() > 0) {
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.SERVICE_CURRENCY+"Construction Menu";
							}

							@Override
							public boolean go() {
								constructionFoundations();
								return false;
							}
						});
						}
					if (aetherBank > 0) {
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return TrawelColor.SERVICE_FREE+"Collect Aether ("+aetherBank+")";
							}

							@Override
							public boolean go() {
								Player.bag.addAether(aetherBank);
								aetherBank = 0;
								return false;
							}
						});
						}
					mList.add(new MenuBack("leave"));
					return mList;
				}
			});
		}
	}
	
	public void constructionFoundations() {
		final float levelMult = getUnEffectiveLevel();
		final int expensive = (int) (levelMult * 50);
		final int medium = (int) (levelMult * 30);
		final int cheap = (int) (levelMult * 20);
		Input.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> mList = new ArrayList<MenuItem>();
				mList.add(new MenuLine() {

					@Override
					public String title() {
						return "You have " + town.fortSizeLeft() + " more space and " + Player.showGold() + ".";
					}});
				if (town.fortSizeLeft() > 2) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Large Foundation ("+expensive+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= expensive) {
								Player.player.addGold(-expensive);
								town.laterAdd(new FortFoundation(3));
								//town needs to catch up to added feature
								Player.addTime(.5f);
								TrawelTime.globalPassTime();
							}else {
								Print.println("You can't afford a new large foundation.");
							}
							return false;
						}
					});
					}
				if (town.fortSizeLeft() > 1) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Medium Foundation ("+medium+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= medium) {
								Player.player.addGold(-medium);
								town.laterAdd(new FortFoundation(2));
								//town needs to catch up to added feature
								Player.addTime(.5f);
								TrawelTime.globalPassTime();
							}else {
								Print.println("You can't afford a new medium foundation.");
							}
							return false;
						}
					});
					}
				if (town.fortSizeLeft() > 2) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Small Foundation ("+cheap+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= cheap) {
								Player.player.addGold(-cheap);
								town.laterAdd(new FortFoundation(1));
								//town needs to catch up to added feature
								Player.addTime(.5f);
								TrawelTime.globalPassTime();
							}else {
								Print.println("You can't afford a new small foundation.");
							}
							return false;
						}
					});
					}
				mList.add(new MenuBack());
				return mList;
			}
		});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		forgeTimer -= (time);
		while (offenseTimer <= 0) {
			offenseTimer += 12.0;
			aetherBank+=getTotalOffenseRating()*getUnEffectiveLevel();//why is it not making me cast?
		}
		
		forgeTimer -= (time*getSkillCount(SubSkill.SMITHING))/10.0;
		if (forgeTimer <=0) {
			Print.offPrintStack();
			forgeTimer = 24.0*7;
			Inventory inv = new Inventory(tier, Race.RaceType.PERSONABLE, null, null,null);//TODO probably make custom inv type
			inv.deEnchant();
			for (Person p: allies) {
				AIClass.loot(p.getBag(), inv, false,p,false);
			}
			Print.popPrintStack();
		}
		enchantTimer -= (time*getSkillCount(SubSkill.ENCHANTING))/3.0;
		if (enchantTimer <=0 && allies.size() > 0) {
			enchantTimer = 24.0*7;
			allies.get(Rand.randRange(0,allies.size()-1)).getBag().getArmorSlot(Rand.randRange(0,4)).improveEnchantChance(tier);
		}
		if (owner != Player.player) {
			while (allies.size() < 5) {
				allies.add(RaceFactory.getDueler(tier));
			}
		}
		fightTimer -= (time);
		if (fightTimer <=0) {
			fightTimer = 24.0*7;
			List<Person> people = new ArrayList<Person>();
			
			while (people.size() < Math.max(4, allies.size())) {
				people.add(RaceFactory.makeMugger(tier));
			}
			defenseFight(people);
		}
		return null;
	}
	
	public int getSkillCount(SubSkill s) {
		int i = 0;
		for (Feature f: town.getFeatures()) {
			FortFeature ff = (FortFeature)f;
			i+=ff.laborer.getSkillCount(s);
		}
		return i;
	}
	
	public EnumMap<SubSkill,Float> compileSkills(){
		EnumMap<SubSkill,Float> totalMap = new EnumMap<SubSkill, Float>(SubSkill.class);
		/*List<LSkill> list = new ArrayList<LSkill>();
		Set<SubSkill> hasSet = EnumSet.noneOf(SubSkill.class);*/
		for (Feature f: town.getFeatures()) {
			FortFeature ff = (FortFeature)f;
			for (LSkill labor: ff.laborer.lSkills) {
				/*if (!hasSet.contains(labor.skill)) {
					
				}*/
				Float cur = totalMap.get(labor.skill);
				if (cur == null) {
					totalMap.put(labor.skill, new Float(labor.value));
				}else {
					totalMap.put(labor.skill,cur+labor.value);
				}
			}
		}
		return totalMap;
	}
	
	public int getWatchScore() {
		int i = 0;
		i+=getSkillCount(SubSkill.SCRYING)*3;
		i+=getSkillCount(SubSkill.WATCH);
		return i;
	}
	
	public void defenseFight(List<Person> attackers) {
		Print.offPrintStack();
		List<List<Person>> listlist = new ArrayList<List<Person>>();
		List<Person> allies = getAllies();
		if (allies.size() == 0) {
			aetherBank = 0;
			Print.popPrintStack();
			return;
		}
		listlist.add(allies);
		List<Person> fullattackers = new ArrayList<Person>();
		attackers.stream().flatMap(p -> p.getSelfOrAllies().stream()).distinct().forEach(fullattackers::add);
		listlist.add(fullattackers);
		
		List<SkillCon> cons = new ArrayList<SkillCon>();
		EnumMap<SubSkill,Float> ssmap = compileSkills();
		for (SubSkill ss: ssmap.keySet()) {
			SkillCon madeCon = new SkillCon(ss,ssmap.get(ss),0);
			if (madeCon.base != null) {
				cons.add(madeCon);
			}
		}
		
		Combat c = Combat.HugeBattle(this.town.getIsland().getWorld(), cons,listlist,false);
		allies.clear();
		if (c.getVictorySide() == 0) {
			c.getNonSummonSurvivors().stream().filter(p -> !p.isPlayer()).forEach(allies::add);
			aetherBank+=c.endaether;
			/*for (Person p: c.killed) {
				this.aetherBank +=p.getBag().getWorth();
			}*/
		}else {
			this.aetherBank = 0;
		}
		Print.popPrintStack();
	}

	private List<Person> getAllies() {
		return allies;
	}
	
	public int getSoldierCost() {
		return (int) (5*getUnEffectiveLevel()*Math.max(allies.size(),5));
	}

}
