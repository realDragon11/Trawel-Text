package trawel.towns.fort;

import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import trawel.AIClass;
import trawel.extra;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Skill;
import trawel.personal.item.Inventory;
import trawel.personal.item.body.Race;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

/**
 * 
 * @author dragon
 *
 * The most important fort feature. Holds extra data and the build menu.
 */
public class FortHall extends FortFeature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int level;
	public ArrayList<Person> allies = new ArrayList<Person>();
	
	public double forgeTimer = 24.0*7;
	public double fightTimer = 24.0*14;
	public double enchantTimer = 24.0*7;
	
	public int aetherBank = 0;
	public double offenseTimer = 24.0;
	
	public FortHall(int tier, Town town) {
		this.name = "Fort Hall";
		this.town = town;
		this.level = tier;
		laborer = new Laborer(LaborType.CHIEF);
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
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
			int cost = (this.level*2500)+(this.aetherBank*3);
			extra.println("Buy this for fort for "+cost+" "+World.currentMoneyString()+"? (You have " + Player.player.getGold()+")");
			if (extra.yesNo()) {
				if (Player.player.getGold() < cost) {
					extra.println("You can't afford to buy this fort.");
				}else {
					Player.player.addGold(-cost);
					for (Feature f: this.town.getFeatures()) {
						f.setOwner(Player.player);
					}
					this.town.visited=3;
					while (allies.size() < 5) {
						allies.add(RaceFactory.getDueler(level));
					}
				}
			}
		}
		if (this.getOwner() == Player.player) {
			
			extra.menuGo(new MenuGenerator() {
				@Override
				public List<MenuItem> gen() {
					List<MenuItem> mList = new ArrayList<MenuItem>();
					mList.add(new MenuLine() {

						@Override
						public String title() {
							return "You have " + allies.size() + " soldiers here and "+Player.player.getGold()+".";
						}});
					if (allies.size() < 10) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Buy a soldier ("+getSoldierCost()+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= getSoldierCost()) {
								Player.player.addGold(-getSoldierCost());
								allies.add(RaceFactory.getDueler(level));
							}else {
								extra.println("You can't afford another soldier.");
							}
							return false;
						}
					});
					}
					if (town.fortSizeLeft() > 0) {
						mList.add(new MenuSelect() {

							@Override
							public String title() {
								return "Construction Menu";
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
								return "Collect Aether ("+aetherBank+")";
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
		extra.menuGo(new MenuGenerator() {
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
							return "Build a Large Foundation ("+(level*1000)+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= (level*1000)) {
								Player.player.addGold(-(level*1000));
								town.enqueneAdd(new FortFoundation(3));
							}else {
								extra.println("You can't afford a new large foundation.");
							}
							return false;
						}
					});
					}
				if (town.fortSizeLeft() > 1) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Medium Foundation ("+(level*500)+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= (level*500)) {
								Player.player.addGold(-(level*500));
								town.enqueneAdd(new FortFoundation(2));
							}else {
								extra.println("You can't afford a new medium foundation.");
							}
							return false;
						}
					});
					}
				if (town.fortSizeLeft() > 2) {
					mList.add(new MenuSelect() {

						@Override
						public String title() {
							return "Build a Small Foundation ("+(level*250)+")";
						}

						@Override
						public boolean go() {
							if (Player.player.getGold() >= (level*250)) {
								Player.player.addGold(-(level*250));
								town.enqueneAdd(new FortFoundation(1));
							}else {
								extra.println("You can't afford a new small foundation.");
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
			aetherBank+=this.getTotalOffenseRating()*this.level;
		}
		
		forgeTimer -= (time*getSkillCount(SubSkill.SMITHING))/10.0;
		if (forgeTimer <=0) {
			extra.offPrintStack();
			forgeTimer = 24.0*7;
			Inventory inv = new Inventory(level, Race.RaceType.HUMANOID, null, null,null);//TODO probably make custom inv type
			inv.deEnchant();
			for (Person p: allies) {
				AIClass.loot(p.getBag(), inv,level, false,p);
			}
			extra.popPrintStack();
		}
		enchantTimer -= (time*getSkillCount(SubSkill.ENCHANTING))/2.0;
		if (enchantTimer <=0 && allies.size() > 0) {
			enchantTimer = 24.0*7;
			allies.get(extra.randRange(0,allies.size()-1)).getBag().getArmorSlot(extra.randRange(0,4)).improveEnchantChance(this.level);
		}
		if (owner != Player.player) {
			while (allies.size() < 5) {
				allies.add(RaceFactory.getDueler(level));
			}
		}
		fightTimer -= (time);
		if (fightTimer <=0) {
			fightTimer = 24.0*7;
			List<Person> people = new ArrayList<Person>();
			
			while (people.size() < 5) {
				people.add(RaceFactory.getMugger(level));
			}
			Fight(this.getAllies(),people);
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
	
	public int getWatchScore() {
		int i = 0;
		i+=getSkillCount(SubSkill.SCRYING)*3;
		i+=getSkillCount(SubSkill.WATCH);
		return i;
	}
	
	public void Fight(List<Person>... people) {
		extra.offPrintStack();
		Combat c = new Combat(this.town.getIsland().getWorld(),this,people);
		allies.clear();
		if (c.survivors.get(0).hasSkill(Skill.PLAYERSIDE)) {
			c.survivors.remove(Player.player.getPerson());
			allies.addAll(c.survivors);
			for (Person p: c.killed) {
				this.aetherBank +=p.getBag().getWorth();
			}
		}else {
			this.aetherBank = 0;
		}
		extra.popPrintStack();
	}

	private List<Person> getAllies() {
		/*while (allies.size() < 10) {
			allies.add(new Person(level));
		}*/
		for (Person a: allies) {
			if (!a.hasSkill(Skill.PLAYERSIDE)) {
				a.addSkill(Skill.PLAYERSIDE);
			}
		}
		return allies;
	}
	
	public int getSoldierCost() {
		return 50*level*Math.max(allies.size(),5);
	}

}
