package trawel.battle.attacks;

import org.nustaq.serialization.annotations.OneOf;

import trawel.Effect;
import trawel.extra;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.item.Inventory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.people.Skill;

/**
 * 
 * @author dragon
 * 2/8/2018
 * 
 * An attack has speed, accuracy, damage values, and a description.
 * It it used to attack other Persons in a certain way. It should be held in a Stance.
 *
 */
public class Attack{

	private static final long serialVersionUID = 1L;
	//instance variables
	private double hitMod, speed;
	private int sharp, blunt, pierce;
	private String desc, name;
	private transient Target target;
	public transient Person defender;//only used for mass battles
	private transient boolean isMagic = false;
	private transient String magicDesc;
	private transient Skill skill;
	private int soundStrength;
	@OneOf({"sharp","blunt","pierce"})
	private String soundType;
	private transient Wound wound;
	private transient Weapon weapon;
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
	public Attack(String name, double hitmod, double speed, double sharp, double blunt, double pierce, String desc,int sstr, String stype) {
		this.hitMod = hitmod;
		this.speed = speed;
		this.sharp = (int)sharp;
		this.blunt = (int)blunt;
		this.pierce = (int)pierce;
		this.desc = desc;
		this.name = name;
		this.soundStrength = sstr;
		this.soundType = stype;
	}
	
	public Attack(String name, double hitmod, double speed, double sharp, double blunt, double pierce, String desc,int sstr,String stype, Target target, Weapon weap) {
		this.hitMod = hitmod;
		this.speed = speed;
		this.sharp = (int)sharp;
		this.blunt = (int)blunt;
		this.pierce = (int)pierce;
		this.desc = desc;
		this.name = name;
		this.soundStrength = sstr;
		this.soundType = stype;
		this.target = target;
		this.weapon = weap;
		//add a wound effect
		//TODO: see if examine needs fixing
		try {
		if (extra.randRange(1,10) == 1 && (weapon == null || !weapon.isKeen())) {
			this.wound = Attack.Wound.GRAZE;
		}else {
		double counter = extra.getRand().nextDouble() * (sharp + blunt + pierce);
		counter-=sharp;
		if (counter<=0) {
			this.wound = extra.randList(target.slashWounds);
		}else {
			counter-=blunt;
			if (counter<=0) {
				this.wound = extra.randList(target.bluntWounds);
			}else {
				this.wound = extra.randList(target.pierceWounds);
			}
		}
		}}catch (RuntimeException e) {
			this.wound = Wound.ERROR;
		}
	}
	
	public Attack(Skill skill, int mageLevel, TargetFactory.TargetType targetType) {
		//name
		//magicDesc;
		hitMod = 1;
		isMagic = true;
		sharp = 0;//fire
		blunt = 0;//shock
		pierce = 0;//ice
		this.skill = skill;
		desc = "ERROR";
		soundStrength = -1;
		double pow = extra.hrandom()*Math.log(mageLevel);
		if (skill == Skill.ELEMENTAL_MAGE) {
			speed = 100+extra.randRange(0,20)-10;
			target = TargetFactory.randTarget(targetType);
			switch (extra.randRange(1,3)) {
			case 1: 
				name = extra.choose("sear","flame blast","searing shot","fireball");
				sharp = (int)((pow*.2)*100);
				magicDesc = sharp +"% fire";
				soundType = "fire";
				this.wound = extra.randList(TargetFactory.fireWounds)
				;break;
			case 2:
				name = extra.choose("shock","zap");
				blunt = (int)(extra.hrandom()*mageLevel*5);
				magicDesc = blunt + " shock";
				soundType = "shock";
				this.wound = extra.randList(TargetFactory.shockWounds);
				;break;
			case 3: 
				name = extra.choose("cone of cold","freeze","permafrost","ray of frost");
				pierce = (int)((pow)*100);
				magicDesc = pierce +"% freeze";
				soundType = "freeze";
				this.wound = extra.randList(TargetFactory.freezeWounds);
				;break;
			}
			desc = "X` casts "+name+" at Y`!";
			
		}
		if (skill == Skill.DEATH_MAGE) {
			speed = 60+extra.randRange(0,20)-10;
			target = TargetFactory.randTarget(targetType);
			soundType = "wither";
			switch (extra.randRange(1,3)) {
			case 1:
			name = extra.choose("wither","halt","impair");
			sharp = (int)((pow*.5)*100);
			magicDesc = sharp + "% impairment";
			
			break;
			case 2:
				name = extra.choose("harm","damage");
				blunt = (int)(extra.hrandom()*mageLevel*3);
				magicDesc = blunt + " damage";
				;break;
			case 3:
				name = extra.choose("harm","damage");
				pierce = (int)(extra.hrandom()*mageLevel);
				magicDesc = pierce + " skill damage";
				;break;
			}
			desc = "X` casts "+name+" at Y`!";
		}
		
		if (skill == Skill.ARMOR_MAGE) {
			speed = 50+extra.randRange(0,20)-10;
			target = TargetFactory.randTarget(targetType);
			name = extra.choose("repair","armorweave");
			desc = "X` casts "+name+"!";
			sharp = (int)((pow*.4)*100);
			magicDesc = sharp + "% repair";
			soundType = "armor";
		}
		if (skill == Skill.ILLUSION_MAGE) {
			speed = 60+extra.randRange(0,20)-10;
			target = TargetFactory.randTarget(targetType);
			name = extra.choose("befuddle","confuse");
			desc = "X` casts "+name+"!";
			magicDesc = "";
			soundType = "confuse";
		}
		if (skill == Skill.EXECUTE_ATTACK) {
			speed = 100+extra.randRange(0,20)-10;
			target = TargetFactory.randTarget(targetType);
			name = "execute";
			desc = "X` attempts to "+name+" Y`!";
			magicDesc = "";
			soundType = "sharp";
			sharp = Math.min(12,4+mageLevel);//% threshold to kill
			blunt = (int)(extra.hrandom()*mageLevel*2);
			wound = Wound.EXE_WOUND;
		}
		if (skill == Skill.DRUNK_DRINK) {
			speed = 120+extra.randRange(0,40)-20;
			target = TargetFactory.randTarget(targetType);
			name = "drink";
			desc = "X` drinks from a flagon!";
			magicDesc = "+" + mageLevel + " hp";
			wound = Wound.DRINK;
		}
		if (skill == Skill.MARK_ATTACK) {
			speed = 80+extra.randRange(0,40)-20;
			target = TargetFactory.randTarget(targetType);
			name = "mark";
			desc = "X` marks Y`!";
			magicDesc = "";
		}
		
		if (skill == Skill.BLOOD_SURGE) {
			speed = 100+extra.randRange(0,40)-20;
			target = TargetFactory.randTarget(targetType);
			name = "blood surge";
			desc = "X` regenerates!";
			magicDesc = "";
		}
		
		if (skill == Skill.BLOOD_HARVEST) {
			speed = 140+extra.randRange(0,40)-20;
			target = TargetFactory.randTarget(targetType);
			name = "blood harvest";
			desc = "X` harvests Y`!";
			magicDesc = "";
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
		tempStr = tempStr.replace("Z`",Z+"[*]");//technically if you were to look upwards you could find the weapon, but I'm gonna put it in this way
		return tempStr;
	}
	/**
	 * @return the name (String) of the attack
	 */
	public String getName() {
		return name;
	}
	
	public void display(int style) {
		display(style,null,null);
	}

	public void display(int style, Person attacker, Person defender) {
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
			extra.specialPrint(in,name ,extra.format(hitMod) , extra.format((speed))  ,""+ (sharp)  ,""+(blunt)  ,  ""+(pierce));
			}else {
				int[] in = new int[3];
				in[0] = 20+7;
				in[1] = 9;
				in[2] = 9+10+10;
				extra.specialPrint(in, name,extra.format(speed),magicDesc);
			}
		}
		if (wound != null) {
		extra.println(" "+this.wound.name + " - " + String.format(this.wound.desc,(Object[])Combat.woundNums(this,attacker,defender,null)));}
	}
	
	public Attack impair(int handLevel, TargetFactory.TargetType targetType,Weapon weap, Person p) {
		Target t = TargetFactory.randTarget(targetType);
		Style s = StyleFactory.randStyle();
		if (!name.contains("examine")) {
			double sMult = 1;
			double bMult = 1;
			double pMult = 1;
			double speedMult = s.speed;
			double speedMod = extra.randRange(-10,10);
			double hitMult = t.hit*s.hit;
			if (weap != null) {
				sMult *= weap.getMat().sharpMult;
				bMult *= weap.getMat().bluntMult;
				pMult *= weap.getMat().pierceMult;
				hitMult *=weap.qualList.contains(Weapon.WeaponQual.ACCURATE) ? 0.1 : 0;
			}
			if (p != null) {
				speedMult *= p.getSpeed();
				if (p.hasEffect(Effect.SLICE)) {
					hitMult*=1.1;//WET
					speedMult*=.9;
				}
				if (p.hasEffect(Effect.DICE)) {
					speedMult*=.9;//WET
				}
			}
		return new Attack(s.name + name + " " + t.name,
				hitMod*hitMult,
				Math.min((speed*speedMult)+speedMod,15),
				handLevel*s.damage*t.sharp*sharp*extra.upDamCurve(.25,.5)*sMult,
				handLevel*s.damage*t.blunt*blunt*extra.upDamCurve(.25,.5)*bMult,
				handLevel*s.damage*t.pierce*pierce*extra.upDamCurve(.25,.5)*pMult,  desc,soundStrength,soundType,t,weap);
		}else {
			return this;
		}
	}
	
	public Attack wither(double percent) {
	percent = 1-percent;
	return new Attack(name,hitMod*percent,speed*percent,sharp*percent,blunt*percent,pierce*percent,desc,soundStrength,soundType,target, weapon);	
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

	public int getSoundIntensity() {
		return soundStrength;
	}

	public String getSoundType() {
		return soundType;
	}

	public Wound getWound() {
		return wound;
	}
	
	//.0f = float/double with no decimal places. Weird that it can't auto convert, but oh well
	//UPDATE welp can't make them look nicer so just made them all ints anyways
	public enum Wound{//FIXME: make sure the reworked wounds are fully in
		HAMSTRUNG("Hamstrung","Delays the next attack by %1$d instants.","Their leg is hamstrung!"), 
		BLINDED("Blinded","The next attack will be %1$d%% less accurate.","Blood falls into their eyes!"),
		CONFUSED("Confused","Forces the opponent to retarget.","They look confused!"), 
		DIZZY("Dizzy","Decreases their next attack's to-hit by %1$d%%","They look dizzy!"),
		SLICE("Slice","Your next attack will happen %1$d%% quicker and be %2$d%% more accurate.","They are sliced!"),//TODO
		DICE("Dice","Your next attack will happen %1$d%% and %2$d instants sooner.","They are diced!"),//TODO
		WINDED("Winded","The next attack will take %1$d instants longer.","The wind is knocked out of them!"),
		BLEED("Bleed","Applies %1$d bleed.","They bleed..."),
		DISARMED("Disarm","Removes one attack choice.","Their attack is put off-kilter!"),
		MAJOR_BLEED("Cut Artery","Applies %1$d bleed plus %2$d bleed.","An artery is cut!"),
		TRIPPED("Tripped","The next attack will take %1$d instants longer.","They are tripped!"),
		GRAZE("Grazed","No effect.","The blow's a graze..."),
		KO("Knockout","Deals %1$d bonus damage, but heals after their next attack.","It's a knockout!"),
		HACK("Hack","Deals %1$d bonus damage.","It's a wicked hack!"),
		TAT("Punctured","Deals up to %1$d bonus damage, based on final pierce damage and to-hit.","The blow goes right through them!"),
		I_BLEED("Internal Bleeding","Applies a stacking %1$d bleed.","Their insides get crushed."),
		CRUSHED("Crushed","Deals %1$d armor piercing damage, repeating the attack's damage once more.","They are crushed!"),
		ERROR("Error","error","ERROR"),
		SCALDED("Scalded","Deals bonus damage through armor.","They are scalded by the flames!"),//TODO: the other wound types
		SCREAMING("Screaming","Removes one attack choice.","They scream!"),
		FROSTED("Frosted","Decreases their next attack's to-hit.","They are frozen over..."),
		FROSTBITE("Frostbite","Deals bonus damage through armor.","Their flesh is frozen!"),
		TEAR("Tear","Decreases dodge by %1$%d%%, stacking.","Their wing is torn!"), //see if need to add a '%'
		EXE_WOUND("Execute","Kills low health targets.",""),
		DRINK("Drink","Heals and provides a random potion effect.",""),
		;
		//done line
		public String name, desc, active;
		Wound(String iName,String iDesc,String activeDesc){
			name = iName;
			desc = iDesc;
			active = activeDesc;
		}
	}

	public void blind(double d) {
		hitMod*=d;
		
	}

	/**
	 * should not be used on impaired/final attacks
	 */
	public double getTotalDam(Weapon weap) {
		double sMult = 1;
		double bMult = 1;
		double pMult = 1;
		if (weap != null) {
			sMult = weap.getMat().sharpMult;
			bMult = weap.getMat().bluntMult;
			pMult = weap.getMat().pierceMult;
		}
		return (this.getSharp()*sMult)+(this.getBlunt()*bMult)+(this.getPierce()*pMult);
	}
	
	/**
	 * note that, for an impaired attack, the weapon substats and person are already applied
	 */
	public double getTotalDam() {
		return getSharp()+getBlunt()+getPierce();
	}
	
	public double getTotalDam(double sMult, double bMult, double pMult) {
		return (this.getSharp()*sMult)+(this.getBlunt()*bMult)+(this.getPierce()*pMult);
	}
	
	public Weapon getWeapon() {
		return weapon;
	}

}
