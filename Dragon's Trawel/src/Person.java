import java.util.ArrayList;

/**
 * 
 * @author Brian Malone
 * 2/5/2018
 * A collection of a monster and an inventory.
 */
public class Person implements java.io.Serializable{
	//inst vars
	private Inventory bag;
	
	private Attack attackNext;
	private int xp = 0;
	private int level = 1;
	private double speedFill = 0;
	private boolean isAttacking;
	private int hp, intellect, maxHp, tempMaxHp;
	private Taunts brag;
	private String placeOfBirth;
	private int beer;
	private boolean racist;
	
	private String firstName,title;

	private int skillPoints;
	private int fighterLevel= 0,traderLevel = 0,explorerLevel = 0, mageLevel = 0, magePow = 0, defenderLevel = 0;
	private ArrayList<Skill> skills = new ArrayList<Skill>();
	//private boolean isPlayer;
	
	//Constructor
	public Person(int level) {
	maxHp = 40*level;//doesn't get all the hp it would naturally get
	hp = maxHp;
	intellect = level;
	bag = new Inventory(level);
	firstName = randomLists.randomFirstName();
	title = randomLists.randomLastName();
	brag = new Taunts(bag.getRace());
	placeOfBirth = extra.capFirst((String)extra.choose(randomLists.randomElement(),randomLists.randomColor()))+ " " +extra.choose("Kingdom","Kingdom","Colony","Domain","Realm");
	this.level = level;
	skillPoints = level-1;
	if (extra.chanceIn(1,5)) {
		racist = true;
	}
	this.magePow = bag.getRace().magicPower;
	}
	
	//instance methods
	

	
	/**
	 * Returns the references to the inventory associated with this person.
	 * @return the bag (Inventory)
	 */
	public Inventory getBag() {
		return bag;
	}
	
	/**
	 * Quene an attack for usage, which will be completed when the speed fills up
	 * @param newAttack (Attack)
	 */
	public void setAttack(Attack newAttack){
		attackNext = newAttack;
		speedFill = attackNext.getSpeed()/bag.getSpeed();
		isAttacking = true;
	}
	
	/**
	 * Returns whether there is an attack quened or not
	 * @return has an attack already (boolean)
	 */
	public boolean isAttacking() {
		return isAttacking;
	}
	
	/**
	 * Returns what attack the person wants to use next
	 * @return the next attack (Attack)
	 */
	public Attack getNextAttack() {
		if (isAttacking == false) {
			extra.println("This person isn't attacking!");
			throw new NotAttackingException();
		}
		return attackNext;
	}
	
	/**
	 * Get the time to the next attack
	 * @return speedFill (double)
	 */
	public double getTime(){
		return speedFill;
	}
	
	/**
	 * Advances the attack timer by t
	 * @param t (double)
	 */
	public void advanceTime(double t){
		speedFill-=t;
	}
	/**
	 * Clear the person for a new battle.
	 */
	public void battleSetup() {
		hp = (int) (maxHp*bag.getHealth());
		if (takeBeer()) {
			if (Player.player.getPerson() == this) {
				Networking.sendStrong("Achievement|drink_beer|");
			}
			hp+=level*5;
		}
		if (hasSkill(Skill.BEER_BELLY)) {
			if (takeBeer()) {
				hp+=level*5;
			}	
		}
		if (this.hasSkill(Skill.LIFE_MAGE)) {
			hp+=this.getMageLevel();
		}
		hp+=skillPoints;
		tempMaxHp = hp;
		speedFill = -1;
		isAttacking = false;
		int s = this.hasSkill(Skill.ARMOR_MAGE) ? this.getMageLevel(): 0;
		int b = this.hasSkill(Skill.ARMOR_MAGE) ? this.getMageLevel(): 0;
		int p = this.hasSkill(Skill.ARMOR_MAGE) ? this.getMageLevel(): 0;
		if (this.hasSkill(Skill.SHIELD)) {
			s+=7*level;
			b+=7*level;
			p+=7*level;
		}else {
			if (this.hasSkill(Skill.PARRY)) {
				s+=5*level;
			}
		}
		bag.resetArmor(s,b,p);
		Boolean print = extra.getPrint();
		extra.changePrint(true);
		AIClass.checkYoSelf(this);
		extra.changePrint(print);
	}
	
	/**
	 * Take damage. Return true if this caused a death.
	 * @param dam (int)
	 * @return if this caused the person to die. (boolean)
	 */
	public boolean takeDamage(int dam) {
		hp-=dam;
		return (hp <= 0);
	}
	/**
	 * Get the current hp of the person.
	 * Note that this is not the base hp, it is specific to the current battle instead.
	 * @return hp (int)
	 */
	public int getHp() {
		return hp;
	}
	
	/**
	 * Adds Xp. Returns true if this causes a level up.
	 * @param x (int)
	 * @return has leveled up (boolean)
	 */
	public boolean addXp(int x) {
		xp += x;
		if (x > 0) {
		extra.println(this.getName() + " has " + xp + "/" + level*level + " xp toward level " + (level+1) + ". +" + x + "xp.");
		}
		if (xp >= level*level) {
			xp-=level*level;
			level++;
			//extra.println(level + " " + xp + " " + x);
			if (isPlayer() == false) {
			intellect++;}
			maxHp+=50;
			extra.println("\"" + brag.getBoast() + "\" " + getName() + " " + extra.choose("declares","boasts","states firmly")+ ".");
			addXp(0);//recursive level easy trick
			this.setSkillPoints(this.getSkillPoints() + 1);
			if (this.isPlayer()) {
				playerLevelUp();
			}
			return true;
		}
		return false;
	}
	
	public void playerLevelUp() {
		if (Player.getTutorial()) {
			extra.println("This is the skill menu.");
			if (skillPoints == 0) {
				extra.println("You don't have any skillpoints, and should probably exit this menu.");
			}
		}
		extra.println("You have " + skillPoints + " skillpoint"+ (skillPoints == 1 ? "" : "s") +".");
		extra.println("Pick a class to examine:");
		extra.println("1 fighter");
		extra.println("2 trader");
		extra.println("3 explorer");
		extra.println("4 mage");
		extra.println("5 defender");
		extra.println("6 exit");
		ArrayList<Skill> list = new ArrayList<Skill>();
		switch(extra.inInt(6)) {
		case 1: 
			extra.println("Fighter Class Level: " + fighterLevel);
			for (Skill s: Skill.values()) {
			if (s.getLevel() == fighterLevel+1 && s.getType() == Skill.Type.FIGHTER) {
				list.add(s);
			}};break;
		case 2: extra.println("Trader Class Level: " + traderLevel); 
			for (Skill s: Skill.values()) {
			if (s.getLevel() == traderLevel+1 && s.getType() == Skill.Type.TRADER) {
				list.add(s);
			}};break;
		case 3: extra.println("Explorer Class Level: " + explorerLevel); 
		for (Skill s: Skill.values()) {
		if (s.getLevel() == explorerLevel+1 && s.getType() == Skill.Type.EXPLORER) {
			list.add(s);
		}};break;
		
		case 4: extra.println("Mage Class Level: " + mageLevel); 
		for (Skill s: Skill.values()) {
		if (s.getLevel() == mageLevel+1 && s.getType() == Skill.Type.MAGE) {
			list.add(s);
		}};break;
		
		case 5: extra.println("Defender Class Level: " + defenderLevel); 
		for (Skill s: Skill.values()) {
		if (s.getLevel() == defenderLevel+1 && s.getType() == Skill.Type.DEFENDER) {
			list.add(s);
		}};break;
		case 6: return;
		}
		extra.println("Pick a skill to buy:");
		int i = 1;
		for (Skill s: list) {
			extra.println(i+" "+ s.getName() + ": " + s.getDesc());
			i++;
		}
		extra.println(i + " back");
		int in = extra.inInt(i);
		i=1;
		
		for (Skill s: list) {
			
			if (in == i) {
				if (skillPoints > 0) {
				skills.add(s);
				extra.println("You spend a skillpoint to gain the "+s.getName() + " skill!");
				skillPoints--;
				switch (s.getType()) {
				case FIGHTER: fighterLevel++;break;
				case TRADER: traderLevel++;break;
				case EXPLORER: explorerLevel++;break;
				case MAGE: mageLevel++;break;
				case DEFENDER: defenderLevel++;break;
				}
				switch (s) {
				case INHERIT:this.bag.addGold(500);break;
				case EXPANDER:Store.INVENTORY_SIZE++;;break;
				case SKILLPLUS: skillPoints+=2;
				case MAGE_TRAINING: magePow+=3;
				default: break;
				}
				}else {
					extra.println("You don't have any skillpoints!");
				}
			}
			
			i++;
		}
		playerLevelUp();
	}
		
	

	/**
	 * Returns the stance that this person is currently using.
	 * @return Stance (Stance)
	 */
	public Stance getStance() {
		return bag.getStance();
	}
	
	/**
	 * Get the level of the person
	 * @return level (int)
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Get the full name of this person
	 * @return String - full name
	 */
	public String getName() {
		return firstName + " " + title;
	}
	
	/**
	 * Display this person's stats.
	 */
	public void displayStats() {
		extra.println("This is " + this.getName() +". They are a level " + this.getLevel() +" " + this.getBag().getRace().name+".");
		extra.println("They have " + (int) (maxHp*bag.getHealth()) + " hp. Their health modifier is " + extra.format(bag.getHealth()) + "x.");
		extra.println("They have " + extra.format(bag.getAim()) + "x aiming, " +  extra.format(bag.getDam()) + "x damage, and "+extra.format(bag.getSpeed()) + "x speed.");
		extra.println("They have " + extra.format(bag.getDodge()) + "x dodging, " + extra.format(bag.getBluntResist()) + " blunt resistance, " +
		extra.format(bag.getSharpResist()) + " sharp resistance, and "+ extra.format(bag.getPierceResist()) + " pierce resistance.");
		extra.println("They have " + xp + "/" + level*level + " xp toward level " + (level+1) + ".");
		extra.println("Their inventory includes " + bag.nameInventory());
		if (beer > 0 || skills.contains(Skill.BEER_LOVER)) {extra.println("They look drunk.");}
		
		
	}
	
	/**
	 * Returns the reference to the Taunts instance this person uses.
	 * @return (Taunts)
	 */
	public Taunts getTaunts() {
		return brag;
	}
	
	/**
	 * Get's the string of where this person is 'from'
	 * @return (String) - place of birth
	 */
	public String whereFrom() {
		return placeOfBirth;
	}


	/**
	 * @return the intellect (int)
	 */
	public int getIntellect() {
		return intellect;
	}


	/**
	 * @param intellect (int) -  the intellect to set
	 */
	public void setIntellect(int intellect) {
		this.intellect = intellect;
	}

	public boolean isPlayer() {
		return (intellect < 0);
	}

	public void setPlayer() {
			intellect = -2;
	}
	
	public void autoLootPlayer() {
			intellect = -1;
	}

	public void displayStatsShort() {
		extra.println("This is " + this.getName() +". They are a level " + this.getLevel() +" " + this.getBag().getRace().name+".");
		extra.println("Their inventory includes: \n " + bag.nameInventory()); 
		if (beer > 0 || skills.contains(Skill.BEER_LOVER)) {extra.println("They look drunk.");}
	}
	
	public void addBeer() {
		beer++;
	}
	
	public boolean takeBeer() {
		if (beer > 0 || skills.contains(Skill.BEER_LOVER)) {
		beer--;
		return true;
		}else {
			return false;
		}
	}

	public void displayXp() {
		extra.println(this.getName() + " has " + xp + "/" + level*level + " xp toward level " + (level+1) + ".");
	}

	public int getMaxHp() {
		return tempMaxHp;
	}

	public void displayHp() {
		extra.println("Hp: " + hp + "/" + tempMaxHp);
		
	}

	public int getSkillPoints() {
		return skillPoints;
	}

	public void setSkillPoints(int skillPoints) {
		this.skillPoints = skillPoints;
	}
	
	public ArrayList<Skill> getSkills(){
		return skills;
	}
	
	public boolean hasSkill(Skill o) {
		return skills.contains(o);
	}

	public boolean isAlive() {
		return this.getHp() > 0;
	}

	public void addHp(int i) {
		hp+=i;
	}
	public void addFighterLevel() {
		fighterLevel++;
	}

	public boolean isRacist() {
		return racist;
	}
	
	public void setRacism(boolean bool) {
		racist = bool;
	}

	public void displayArmor() {
		int sharp=0, blunt=0, pierce=0, sharpm =0, bluntm = 0, piercem = 0;
		for (Armor a: bag.getArmor()) {
			sharp += a.getSharp();
			blunt += a.getBlunt();
			pierce += a.getPierce();
			
			sharpm += a.getSharpResist()*a.getResist();
			bluntm += a.getBluntResist()*a.getResist();
			piercem += a.getPierceResist()*a.getResist();
		}
		extra.println("Sharp: " + sharp + "/" +sharpm);
		extra.println("Blunt: " + blunt + "/" +bluntm);
		extra.println("Pierce: " + pierce + "/" +piercem);
	}

	public int getMageLevel() {
		return mageLevel+magePow;
	}

	public void addXpSilent(int x) {
		xp += x;
		if (xp >= level*level) {
			xp-=level*level;
			level++;
			if (isPlayer() == false) {
			intellect++;}
			maxHp+=50;
			addXpSilent(0);//recursive level easy trick
			this.setSkillPoints(this.getSkillPoints() + 1);
		}
	}
	
	
}
