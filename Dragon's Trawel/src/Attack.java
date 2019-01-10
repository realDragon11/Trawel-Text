/**
 * 
 * @author Brian Malone
 * 2/8/2018
 * 
 * An attack has speed, accuracy, damage values, and a description.
 * It it used to attack other creates in a certain way. It should be held in a Stance.
 *
 */
public class Attack implements java.io.Serializable{
	//instance variables
	private double hitMod, speed;
	private int sharp, blunt, pierce;
	private String desc, name;
	private Target target;
	public Person defender;//only used for mass battles
	private boolean isMagic = false;
	private String magicDesc;
	private Skill skill;
	//constructor
	/**
	 * Creates an attack with the following attributes:
	 * @param name - (String) the name of the attack
	 * @param hitmod - (double) the accuracy of the attack
	 * @param speed - (double) how long the attack takes to complete
	 * @param sharp - (double) the sharp damage [converted to int for you]
	 * @param blunt - (double) the blunt damage [converted to int for you]
	 * @param pierce - (double) the piercing damage [converted to int for you]
	 * @param desc - (String) what is printed when it is used (use X = attacker, Y = defender, Z = weapon name)
	 */
	public Attack(String name, double hitmod, double speed, double sharp, double blunt, double pierce, String desc) {
		this.hitMod = hitmod;
		this.speed = speed;
		this.sharp = (int)sharp;
		this.blunt = (int)blunt;
		this.pierce = (int)pierce;
		this.desc = desc;
		this.name = name;
	}
	
	public Attack(String name, double hitmod, double speed, double sharp, double blunt, double pierce, String desc, Target target) {
		this.hitMod = hitmod;
		this.speed = speed;
		this.sharp = (int)sharp;
		this.blunt = (int)blunt;
		this.pierce = (int)pierce;
		this.desc = desc;
		this.name = name;
		this.target = target;
	}
	
	public Attack(Skill skill, int mageLevel) {
		//name
		//magicDesc;
		hitMod = 1;
		isMagic = true;
		sharp = 0;//fire
		blunt = 0;//shock
		pierce = 0;//ice
		this.skill = skill;
		double pow = extra.hrandom()*Math.log(mageLevel);
		if (skill == Skill.ELEMENTAL_MAGE) {
			speed = 100+extra.randRange(0,20)-10;
			target = TargetFactory.randTarget();
			switch (extra.randRange(1,3)) {
			case 1: 
				name = extra.choose("sear","flame blast","searing shot","fireball");
				sharp = (int)((pow*.2)*100);
				magicDesc = sharp +"% fire";
				;break;
			case 2:
				name = extra.choose("shock","zap");
				blunt = (int)(extra.hrandom()*mageLevel*5);
				magicDesc = blunt + " shock";
				;break;
			case 3: 
				name = extra.choose("cone of cold","freeze","permafrost","ray of frost");
				pierce = (int)((pow)*100);
				magicDesc = pierce +"% freeze";
				;break;
			}
			desc = "X` casts "+name+" at Y`!";
			
		}
		if (skill == Skill.DEATH_MAGE) {
			speed = 60+extra.randRange(0,20)-10;
			target = TargetFactory.randTarget();
			switch (extra.randRange(1, 2)) {
			case 1:
			name = extra.choose("wither","halt","impair");
			sharp = (int)((pow*.5)*100);
			magicDesc = sharp + "% impairment";
			desc = "X` casts "+name+" at Y`!";
			break;
			case 2:
				name = extra.choose("harm","damage");
				blunt = (int)(extra.hrandom()*mageLevel*3);
				magicDesc = blunt + " damage";
				;break;
			}
		}
	}
	
	//instance methods
	
	/**
	 * @return the hitmod (double)
	 */
	public double getHitmod() {
		return hitMod;
	}
	/**
	 * @return the speed(double)
	 */
	public double getSpeed() {
		return speed;
	}
	
	/**
	 * @return the sharp damage (int)
	 */
	public int getSharp() {
		return sharp;
	}

	/**
	 * @return the blunt damage (int)
	 */
	public int getBlunt() {
		return blunt;
	}

	/**
	 * @return the pierce damage (int)
	 */
	public int getPierce() {
		return pierce;
	}
	/**
	 * @return the desc (String)
	 */
	public String getDesc() {
		return desc;
	}
/**
 * Returns the string of an attack, taking the format
 * "X <does something to> Y with their Z"
 * @param X - (String) Attacker name
 * @param Y - (String) Defender name
 * @param Z - (String) Weapon name
 */
	public String attackStringer(String X, String Y, String Z) {
		String tempStr = desc;
		tempStr = tempStr.replace("X`",X);
		
		if (target == null) {
			target = TargetFactory.noTarget;
			//System.err.println("error1");
			// ~~figure out why this is happening and fix it, for now, a simple fix~~
			//probably happens because of HD and such
		}
		tempStr = tempStr.replace("Y`",Y + "'s " + target.name);
		tempStr = tempStr.replace("Z`",Z);//technically if you were to look upwards you could find the weapon, but I'm gonna put it in this way
		return tempStr;
	}
	/**
	 * @return the name (String) of the attack
	 */
	public String getName() {
		return name;
	}

	public void display(int style) {
		if (style == 0) {
			extra.println(name + "\t" + extra.format(hitMod) + "\t" + speed + "\t" + sharp + "\t" + blunt + "\t" + pierce);
		}
		if (style == 1) {
			if (!isMagic) {
			int[] in = new int[6];
			in[0] = 8+12;
			in[1] = 7;
			in[2] = 9;
			in[3] = 9;
			in[4] = 10;
			in[5] = 6+4;
			extra.specialPrint(in,name ,extra.format(hitMod) , extra.format((speed))  , extra.format(sharp)  , extra.format(blunt)  ,  extra.format(pierce));
			}else {
				int[] in = new int[2];
				in[0] = 20;
				in[1] = 7+9+9+10+10;
				extra.specialPrint(in, name,magicDesc);
			}
		}
	}
	
	public Attack impair() {
		Target t = TargetFactory.randTarget();
		Style s = StyleFactory.randStyle();
		if (name != "examine") {
		return new Attack(s.name + name + " " + t.name, hitMod*t.hit*s.hit,  (s.speed*speed)+extra.randRange(0,20)-10,  s.damage*t.sharp*sharp*extra.hrandom(),  s.damage*t.blunt*blunt*extra.hrandom(),  s.damage*t.pierce*pierce*extra.hrandom(),  desc,t);
		}else {
			return this;
		}
	}
	
	public Attack wither(double percent) {
	percent = 1-percent;
	return new Attack(name,hitMod*percent,speed*percent,sharp*percent,blunt*percent,pierce*percent,desc,target);	
	}
	
	public int getSlot() {
		return target.slot;
	}

	public boolean isMagic() {
		return isMagic;
	}
	
	public Skill getSkill() {
		return skill;
	}

}
