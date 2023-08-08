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
	
	//NOTE
	//can insert skill stuff here since we have a link to the person!
	
	public float getTotalAgiPen(float capOver,float agiPenToDex) {
		return getCapAgiPen(capOver)*multDex(capOver,agiPenToDex*dexterity);
	}
	
	public float getTotalAgiPen(float agiPenToDex) {
		return getTotalAgiPen(getNaiveStrCap(capacity),agiPenToDex);
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
	
	/**
	 * not a pen to dex, how much dex can pen AMP for being negative dex
	 */
	public float getDexPen(float capacityPercent) {
		if (capacityPercent <= 1f) {
			return .75f;
		}
		if (capacityPercent <= 1.5f) {
			return extra.lerp(.75f,.5f,capacityPercent/1.5f);
		}
		if (capacityPercent <= 2f) {
			return extra.lerp(.5f,.25f,capacityPercent/2f);
		}
		if (capacityPercent <= 3f) {
			return extra.lerp(.25f,.1f,capacityPercent/3f);
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
