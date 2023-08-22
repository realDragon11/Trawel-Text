package trawel.personal.people;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuLine;
import derg.menus.MenuSelect;
import derg.menus.ScrollMenuGenerator;
import trawel.Effect;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.battle.attacks.WeaponAttackFactory;
import trawel.personal.Person;
import trawel.personal.classless.IHasSkills;
import trawel.personal.classless.Skill;
import trawel.personal.classless.Skill.Type;
import trawel.personal.item.Potion;
import trawel.personal.classless.SkillAttackConf;
import trawel.personal.people.Agent.AgentGoal;
import trawel.time.CanPassTime;
import trawel.towns.Town;
import trawel.towns.World;

public abstract class SuperPerson implements java.io.Serializable, CanPassTime{

	private static final long serialVersionUID = 1L;
	/**
	 * TODO: make not a list of strings, can have titles that are more dynamic
	 * <br>
	 * such as upgradable and things that can generate their names from existing things like features
	 */
	protected List<String> titleList = new ArrayList<String>();
	private Town location;
	protected SkillAttackConf[] attConfs = null;
	
	protected int featPicks = 1;//start with 1 for player
	protected byte sAttCount = 0;
	
	public List<Integer> moneys;
	public List<World> moneymappings;
	
	protected Potion flask;
	protected boolean knowsFlask;
	
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
		if (Player.player == this) {
			Player.updateWorld(location.getIsland().getWorld());
		}
		
		addGold(getPerson().getBag().surrenderRawMoney());
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
	
	
	public boolean isPersonable() {
		return getPerson().isPersonable();
	}

	public SkillAttackConf[] getSpecialAttacks() {
		return attConfs;
	}
	public byte getSAttCount() {
		return sAttCount;
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
				int i = 0;
				for (; i < attConfs.length-1;i++) {
					if (attConfs[i] == null) {
						break;
					}
					list.add(new SkillConfiger(attConfs[i],i));
				}
				if (skills.size() > 0 && i < max) {
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
				Skill source = s.getAliasFor();
				if (source == null) {
					source = s;
				}
				List<IHasSkills> base = WeaponAttackFactory.getSources(source);
				List<IHasSkills> hases  = new ArrayList<IHasSkills>();
				for (IHasSkills has: base) {
					if (getPerson().hasSkillHas(has)) {
						options += has.friendlyName() +" ";
						hases.add(has);
					}
				}
				final String option = options;
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
									sAttCount++;
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
					sAttCount--;
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
	
	public int getGold() {
		World w = getWorld();
		if (w == null) {
			return getGold(Player.getPlayerWorld());
		}
		return getGold(w);
	}
	public String getGoldDisp() {
		World w = getWorld();
		if (w == null) {
			w = Player.getPlayerWorld();
		}
		return w.moneyString(getGold(w));
	}
	
	
	public void addGold(int delta) {
		World w = getWorld();
		if (w == null) {
			throw new RuntimeException("invalid location for adding money in superperson");
		}
		addGold(delta,w);
	}
	
	public int getGold(World w) {
		int index = moneymappings.indexOf(w);
		if (index == -1) {
			moneymappings.add(w);
			moneys.add(0);
			return 0;
		}
		return moneys.get(index);
	}
	
	public void addGold(int delta,World w) {
		int index = moneymappings.indexOf(w);
		if (index == -1) {
			moneymappings.add(w);
			moneys.add(Math.max(0, delta));
			return;
		}
		moneys.set(index, Math.max(0,moneys.get(index)+delta));
	}
	
	/**
	 * subtracts and prints failure for the caller
	 * @param aether
	 * @param money
	 * @return if bought successfully
	 */
	public boolean doCanBuy(int aether, int money) {
		int hasMoney = getGold();
		int hasAether = Player.bag.getAether();
		Person p = getPerson();
		if (hasAether >= aether) {
			if (hasMoney < money) {
				if (!p.isPlayer()) {
					return false;
				}
				extra.println("Not enough " + World.currentMoneyString()+"!");
				return false;
			}else {
				if (money > 0) {//if we're not in a place, gold = 0 so we don't run this
					addGold(-money);
				}
				p.getBag().addAether(-aether);
				return true;
			}
		}else {
			if (hasMoney < money) {
				if (!p.isPlayer()) {
					return false;
				}
				extra.println("Not enough aether or " + World.currentMoneyString()+"!");
				return false;
			}else {
				if (!p.isPlayer()) {
					return false;
				}
				extra.println("Not enough aether!");
				return false;
			}
		}
	}
	
	public boolean getCanBuy(int aether, int money) {
		int hasMoney = getGold();
		Person p = getPerson();
		int hasAether = p.getBag().getAether();
		if (hasAether >= aether) {
			if (hasMoney < money) {
				if (!p.isPlayer()) {
					return false;
				}
				extra.println("Not enough " + World.currentMoneyString()+"!");
				return false;
			}else {
				return true;
			}
		}else {
			if (hasMoney < money) {
				if (!p.isPlayer()) {
					return false;
				}
				extra.println("Not enough aether or " + World.currentMoneyString()+"!");
				return false;
			}else {
				if (!p.isPlayer()) {
					return false;
				}
				extra.println("Not enough aether!");
				return false;
			}
		}
	}
	
	public boolean canBuyMoneyAmount(int money,float aetherRate) {
		int hasMoney = getGold();
		int hasAether = Player.bag.getAether();
		return hasMoney+ (int)(hasAether*aetherRate) >= money;
	}
	
	public boolean canBuyMoneyAmount(int money) {
		return canBuyMoneyAmount(money,Player.NORMAL_AETHER_RATE);
	}
	
	public int getTotalBuyPower(int aetherpermoney) {
		return getGold()+(Player.bag.getAether()/aetherpermoney);
	}
	
	public int getTotalBuyPower(float aetherRate) {
		return getGold()+ (int)(Player.bag.getAether()*aetherRate);
	}
	public int getTotalBuyPower() {
		return getTotalBuyPower(Player.NORMAL_AETHER_RATE);
	}
	
	public void buyMoneyAmountRateInt(int money,int aetherPer) {
		int value = money;
		int gold = getGold();
		if (gold >= value) {
			addGold(-value);//if we're not in a place, gold = 0 so we don't run this
			return;
		}
		value-=gold;
		getPerson().getBag().addAether( -value*aetherPer);
	}
	
	public void buyMoneyAmount(int money,float aetherRate) {
		int value = money;
		int gold = getGold();
		if (gold >= value) {
			addGold(-value);//if we're not in a place, gold = 0 so we don't run this
			return;
		}
		value-=gold;
		getPerson().getBag().addAether( -((int)(value/aetherRate)));
	}
	public void buyMoneyAmount(int money) {
		buyMoneyAmount(money,Player.NORMAL_AETHER_RATE);
	}
	
	/**
	 * superperson will lose up to i gold, and this will return how much they lose
	 * <br>
	 * if they were broke will return -1
	 * <br>
	 * 0 will be returned if i == 0
	 * <BR>
	 * WARNING: will throw if not in a location
	 * @param i
	 */
	public int loseGold(int i) {
		if (i == 0) {
			return 0;
		}
		int has = getGold();
		if (has == 0) {
			return -1;
		}
		int lose = Math.max(has,i);
		addGold(-lose);
		return lose;
	}
	
	/**
	 * includes the person and their current traveling friends
	 */
	public List<Person> getAllies(){
		return Collections.singletonList(getPerson());
	}
	
	public boolean hasAllies() {
		return false;
	}
	
	public Combat fightWith(Person p) {
		if (p.getSuper() != null) {
			return fightWith(p.getSuper());
		}
		if (hasAllies()) {
			List<List<Person>> listlist = new ArrayList<List<Person>>();
			listlist.add(getAllies());
			listlist.add(Collections.singletonList(p));
			return mainGame.HugeBattle(getWorld(), listlist);
		}
		return mainGame.CombatTwo(getPerson(),p,getWorld());
	}
	
	public Combat fightWith(SuperPerson p) {
		if (hasAllies() || p.hasAllies()) {
			List<List<Person>> listlist = new ArrayList<List<Person>>();
			listlist.add(getAllies());
			listlist.add(p.getAllies());
			return mainGame.HugeBattle(getWorld(), listlist);
		}
		return mainGame.CombatTwo(getPerson(),p.getPerson(),getWorld());
	}
	
	public Combat massFightWith(List<Person> others) {
		//right now this person can't have allies
		if (others.size() == 1) {
			return mainGame.CombatTwo(getPerson(),others.get(0),getWorld());
		}
		List<List<Person>> listlist = new ArrayList<List<Person>>();
		listlist.add(Collections.singletonList(getPerson()));
		listlist.add(others);
		return mainGame.HugeBattle(getWorld(), listlist);
	}
	
	public World getWorld() {
		Person p = getPerson();
		if (p.isPlayer()) {
			return Player.getPlayerWorld();
		}
		return location == null ? null : location.getIsland().getWorld();
	}
	
	public Effect doSip() {
		if (flask != null) {
			knowsFlask = true;
			Effect e = flask.effect;
			flask.sip(getPerson());
			if (flask.sips <=0) {
				flask = null;
			}
			return e;
		}
		return null;
	}
	
	public boolean hasFlask() {
		return flask != null;
	}
	
	public Effect peekFlask() {
		return flask.effect;
	}
	public int getFlaskUses() {
		return flask.sips;
	}
	
	public void addFlaskUses(byte b) {
		flask.sips= (byte) Math.min(20,flask.sips+b);
	}
	
	public void spoilPotion() {
		knowsFlask = false;
		flask = new Potion(Effect.CURSE,flask.sips);
	}
	public void muddyPotion() {
		knowsFlask = false;
	}
	
	public boolean knowsPotion() {
		return knowsFlask;
	}
	
	public void setFlask(Potion pot) {
		knowsFlask = false;
		flask = pot;
	}

	public abstract boolean everDeathCheated();

	public void removeGold(World w) {
		int index = moneymappings.indexOf(w);
		if (index != -1) {
			moneys.set(index,0);
		}
	}
	

	public void fillSkillConfigs() {
		if (attConfs == null) {
			attConfs = new SkillAttackConf[6];
		}
		sAttCount = 0;
		for (int i = 0; i < 6;i++) {
			attConfs[i] = null;
		}
		//needs sequential stream
		getPerson().fetchSkills().stream().filter(s -> s.getType() == Type.ATTACK_TYPE).limit(6).forEach(this::addSkillConfig);
	}
	
	protected void addSkillConfig(Skill skill) {
		List<IHasSkills> base = WeaponAttackFactory.getSources(skill.getAliasOrSelf());
		attConfs[sAttCount] = new SkillAttackConf(skill,extra.randList(base),null);
		sAttCount++;
	}
}
