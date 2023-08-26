package trawel.personal.classless;

import trawel.extra;
import trawel.personal.Person;

public class AttributeBox {
	
	public final Person owner;
	
	private int strength, dexterity, clarity;
	
	private int capacity;
	
	public AttributeBox(Person p) {
		owner = p;
		
		reset();
	}

	public void reset() {
		strength = 100;
		dexterity = 100;
		clarity = 100;
		capacity = 0;
	}
	
	public void setCapacity(int i) {
		capacity = i;
	}
	
	public void process(Feat f) {
		strength += f.getStrength();
		dexterity += f.getDexterity();
		clarity += f.getClarity();
	}
	
	public void process(Perk p) {
		strength += p.getStrength();
		dexterity += p.getDexterity();
		clarity += p.getClarity();
	}
	
	public void process(Archetype a) {
		strength += a.getStrength();
		dexterity += a.getDexterity();
		clarity += a.getClarity();
	}

	public int getStrength() {
		return strength;
	}

	public int getDexterity() {
		return dexterity;
	}
	
	public int getClarity() {
		return clarity;
	}
	/**
	 * can average out attributes if you want to use multiple
	 * <br>
	 * returns an effective level >=1, but likely > 10
	 */
	public int getEffectiveAttributeLevel(int attributeAverage) {
		int level = owner.getEffectiveLevel();
		int aLevel = Math.round(attributeAverage/10f)+1;
		int offset = aLevel-level;
		int aOffset = Math.abs(offset);
		int sign;
		if (offset > 0) {
			sign = 1;
		}else {
			if (offset == 0) {
				return level;
			}else {
				sign = -1;
			}
		}
		switch (aOffset) {
		default:
			return Math.max(1,level+(sign*4));//max is 4
		case 3: case 4:
			return Math.max(1, level+(sign*3));//3-4 is 3
		case 2:
			return Math.max(1, level+(sign*2));
		case 1:
			return Math.max(1, level+sign);
		}
		/*
		if (aOffset >= 5) {
			return Math.max(1, sign*4);//max is 4
		}
		if (aOffset >= 3) {
			return Math.max(1, sign*3);//3-4 is 3
		}
		if (aOffset >= 2) {//could be ==
			return Math.max(1, sign*2);//3-4 is 3
		}
		
		if (aOffset >= 1) {//could be ==
			return Math.max(1, sign*2);//3-4 is 3
		}
		return level;*/
	}
	
	//NOTE
	//can insert skill stuff here since we have a link to the person!
	
	public float getAttributeAgiPenWithPen(float capOver,float agiPenToDex) {
		return getCapAgiPen(capOver)*multDex(capOver,agiPenToDex*dexterity);
	}
	
	public float getAttributeAgiPenWithPen(float agiPenToDex) {
		return getAttributeAgiPenWithPen(getNaiveStrCap(capacity),agiPenToDex);
	}
	
	/**
	 * use at lerped strength for aim penalties
	 * <br>
	 * this applies to the agi mult and aim
	 * <br>
	 * indirectly applies to dodge
	 */
	public float multDex(float capOver) {
		float dp = getDexPen(capOver);
		if (dexterity <= 100) {
			return extra.lerp(dp,1f,dexterity/100f);
		}
		return extra.lerp(1f,2f,dexterity/1500f);//reaches 2x at 1500
	}
	
	private float multDex(float capOver,float effective_dex) {
		float dp = getDexPen(capOver);
		if (dexterity <= 100) {
			return extra.lerp(dp,1f,effective_dex/100f);
		}
		return extra.lerp(1f,2f,effective_dex/1500f);//reaches 2x at 1500
	}
	
	public float getCapAgiPen() {
		return getCapAgiPen(getNaiveStrCap(capacity));
	}
	
	public float getCapAgiPen(float capacityPercent) {
		if (capacityPercent <= 1f) {
			return 1f;
		}
		if (capacityPercent <= 1.5f) {
			return extra.lerp(1f,.75f,capacityPercent/1.5f);
		}
		if (capacityPercent <= 2f) {
			return extra.lerp(.75f,.5f,capacityPercent/2f);
		}
		if (capacityPercent <= 3f) {
			return extra.lerp(.5f,.25f,capacityPercent/3f);
		}
		return .25f;
	}
	
	public float getNaiveStrCap(int capacity) {
		return capacity/(float)strength;
	}
	public float getDexPen() {
		return getDexPen(getNaiveStrCap(capacity));
	}
	/**
	 * not a pen to dex, how much dex can pen AMP for being negative dex
	 */
	public float getDexPen(float capacityPercent) {
		if (capacityPercent <= 1f) {
			return .75f;
		}
		if (capacityPercent <= 1.5f) {
			return extra.lerp(.75f,.5f,capacityPercent-1f);
		}
		if (capacityPercent <= 2f) {
			return extra.lerp(.5f,.25f,capacityPercent-1.5f);
		}
		if (capacityPercent <= 3f) {
			return extra.lerp(.25f,.1f,capacityPercent-2f);
		}
		return .1f;
	}
	
	public float multStrength() {
		if (strength <= 100) {
			return extra.lerp(.5f,1f,Math.max(0,strength)/100f);
		}
		return 1f + (strength/1000f);//+.1f per 100
	}
	
	public String getDesc() {
		return "dex: " +getDexterity() + " cap/str: "+capacity+"/"+getStrength();
	}
}
