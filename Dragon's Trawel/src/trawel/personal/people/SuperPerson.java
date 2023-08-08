package trawel.personal.people;
import java.util.ArrayList;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.extra;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.Person;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Skill;
import trawel.personal.classless.Skill.Type;
import trawel.personal.classless.SkillAttackConf;
import trawel.personal.people.Agent.AgentGoal;
import trawel.time.CanPassTime;
import trawel.towns.Town;

public abstract class SuperPerson implements java.io.Serializable, CanPassTime{

	private static final long serialVersionUID = 1L;
	protected List<String> titleList = new ArrayList<String>();
	private Town location;
	protected SkillAttackConf[] attConfs = null;
	
	protected int featPicks = 0;
	
	/**
	 * used for the player swapping out configs
	 */
	private static int currentEditing = -1;
	
	public void addTitle(String title) {
		titleList.add(title);
	}
	
	public void displayTitles() {
		if (titleList.isEmpty()) {extra.println("They have no titles.");}else {
		extra.println("They have the following titles:");
		for (String str: titleList) {
			extra.print(str+",");
		}
		extra.println();
		}
	}
	
	public Town getLocation() {
		return location;
	}
	public void setLocation(Town location) {
		this.location = location;
	}
	
	public abstract Person getPerson();

	public abstract void setGoal(AgentGoal goal);
	public abstract void onlyGoal(AgentGoal goal);
	public abstract boolean removeGoal(AgentGoal goal);
	public abstract boolean hasGoal(AgentGoal goal);
	
	public void addFeatPick(int amount) {
		featPicks+=amount;
	}
	
	public int getFeatPicks() {
		return featPicks;
	}
	
	
	public boolean isHumanoid() {
		return getPerson().isHumanoid();
	}

	public SkillAttackConf[] getSpecialAttacks() {
		return attConfs;
	}
	
	/**
	 * each special attack skill can only be used for one, and there is a hard cap of 6 (which cuts into one weapon attack in average case)
	 * <br>
	 * should only be used to help set up attConfs outside of battle
	 */
	public int maxSpecialAttacks() {
		return Math.min(6,(int)getPerson().fetchSkills().stream().filter(s -> s.getType() == Type.ATTACK_TYPE).count());
	}
	
	/**
	 * should only be used to help set up attConfs outside of battle
	 */
	public List<Skill> specialAttackSkills(){
		List<Skill> list = new ArrayList<Skill>();
		getPerson().fetchSkills().stream().filter(s -> s.getType() == Type.ATTACK_TYPE).forEach(list::add);
		return list;
	}

	public boolean configAttacks() {//is in a menu
		if (attConfs == null) {
			attConfs = new SkillAttackConf[6];
		}
		int max = maxSpecialAttacks();
		extra.menuGo(new MenuGenerator() {

			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				List<Skill> skills = specialAttackSkills();
				list.add(new MenuLine() {

					@Override
					public String title() {
						if (skills.size() == 0) {
							return "You have no skills that have attacks.";
						}
						return "You have skills that grant attacks. You can have up to " + max + " configs here.";
					}});
				for (int i = 0; i < attConfs.length-1;i++) {
					if (attConfs[i] == null) {
						break;
					}
					list.add(new SkillConfiger(attConfs[i],i));
				}
				if (skills.size() > 0 && attConfs.length < max) {
					list.add(new MenuSelect() {

						@Override
						public String title() {
							return "Create new attack config";
						}

						@Override
						public boolean go() {
							currentEditing = -1;
							createConfig(skills);
							return false;
						}});
				}
				list.add(new MenuBack("back"));
				return list;
			}});
		
		
		return false;//return false for menu
	}
	
	protected void createConfig(List<Skill> skills) {
		extra.menuGo(new ScrollMenuGenerator(skills.size(),"previous <> skills","next <> skills") {

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				Skill s = skills.get(i);
				//list.add(new SkillConfiger(s));
				String options = "";
				List<IHasSkills> hases = WeaponAttackFactory.getSources(s);
				for (IHasSkills has: hases) {
					if (getPerson().hasSkillHas(has)) {
						options += has.friendlyName() +" ";
					}else {
						hases.remove(has);
					}
				}
				final String option = options;
				/*list.add(new MenuLine() {

					@Override
					public String title() {
						
						return s.getName() + ": "+ option;
					}});*/
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return "set up " + s.getName() + ": " + option;
					}

					@Override
					public boolean go() {
						setupConfig(s,hases);
						return true;
					}});
				return list;
			}

			@Override
			public List<MenuItem> header() {
				return null;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuBack("cancel"));
				return list;
			}});
	}
	
	//TODO: only one source allowed for now
	protected void setupConfig(Skill s, List<IHasSkills> hases) {
		extra.menuGo(new ScrollMenuGenerator(hases.size(),"previous <> sources","next <> sources") {

			@Override
			public List<MenuItem> forSlot(int i) {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuSelect() {

					@Override
					public String title() {
						return hases.get(i).getOwnText();
					}

					@Override
					public boolean go() {
						if (SuperPerson.currentEditing == -1) {
							for (int j = 0; j < attConfs.length;j++) {
								if (attConfs[j] == null) {
									attConfs[j] = new SkillAttackConf(s,hases.get(i),null);
									break;
								}
							}
						}else {
							attConfs[SuperPerson.currentEditing].update(s, hases.get(i), null);
						}
						return true;
					}});
				return list;
			}

			@Override
			public List<MenuItem> header() {
				return null;
			}

			@Override
			public List<MenuItem> footer() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				list.add(new MenuBack("cancel"));
				return list;
			}});
	}
	
	
	protected class SkillConfiger extends MenuSelect {
		
		private final SkillAttackConf config;
		private final int index;
		public SkillConfiger(SkillAttackConf _config,int i) {
			config = _config;
			index = i;
		}
		@Override
		public String title() {
			return config.getText() + " configuration";
		}
		@Override
		public boolean go() {
			currentEditing = index;
			extra.println("Replace the " + config.getText() + " config?");
			if (extra.yesNo()) {
				List<Skill> skills = specialAttackSkills();
				createConfig(skills);
				return false;
			}else {//so many nested menus, lets just yes/no
				extra.println("Delete the " + config.getText() + " config?");
				if (extra.yesNo()) {
					for (int i = index;i < attConfs.length-1;i++) {
						SkillAttackConf up = attConfs[i+1];
						if (up != null) {
							attConfs[i] = up;
							continue;
						}
						break;
					}
					attConfs[attConfs.length-1] = null;
				}
			}
			return false;
		}
	}
}
