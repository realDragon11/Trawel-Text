package trawel.personal.classless;

import trawel.core.Print;
import trawel.helper.methods.extra;
import trawel.personal.Effect;
import trawel.personal.Person;
import trawel.personal.people.Player;

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
	 * <br>
	 * min is also capped at half owner's effective level and max at owner's effective level +3, scaling by 15 past level
	 */
	public int getEffectiveAttributeLevel(int attributeAverage) {
		int levelAttrib = owner.getEffectiveLevel()*10;//110 at level 1
		//attributeAverage 110+ at level 1 they already have 1 archetype
		if (attributeAverage >= levelAttrib) {//attributes are higher or equal
			//every 15 higher, +1 level on base, up to 3
			return owner.getEffectiveLevel()+Math.min(3,(attributeAverage-levelAttrib)/15);
		}
		//what level the attributes should be, down to half 
		return Math.max(owner.getEffectiveLevel()/2,Math.round(attributeAverage/10f));
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
	
	/*
	public float multStrength() {
		if (strength <= 100) {
			return extra.lerp(.5f,1f,Math.max(0,strength)/100f);
		}
		return 1f + (strength/1000f);//+.1f per 100
	}*/
	
	public String getDesc() {
		return "dex: " +getDexterity() + " cap/str: "+capacity+"/"+getStrength();
	}
	
	public static final String getStatHintByIndex(int index) {
		switch (index) {
		case 0:
			return "{Strength}";
		case 1:
			return "{Dexterity}";
		case 2:
			return "{Clarity}";
		}
		throw new RuntimeException("invalid stat index: " + index);
	}
	
	public static final String getStatNameByIndex(int index) {
		switch (index) {
		case 0:
			return "Strength";
		case 1:
			return "Dexterity";
		case 2:
			return "Clarity";
		}
		throw new RuntimeException("invalid stat index: " + index);
	}
	
	public static final String showPlayerContest(int index,int difficulty) {
		int playerAttribute = Player.player.getPerson().getStatByIndex(index);
		if (Player.player.getPerson().hasEffect(Effect.BURNOUT)) {
			playerAttribute/=2;
		}
		//probability math figured out by maris
		int chance;
		if (playerAttribute >= difficulty) {
			chance = (int) (100 *(1d - (difficulty/(2d*(1+playerAttribute)))));
		}else {
			chance = (int) (100 *(1d-(1d - (playerAttribute/(2d*(1+difficulty))))));
		}
		//int chance = (100*playerAttribute)/(playerAttribute+difficulty);
		return chance+"% ("+playerAttribute+" "+getStatNameByIndex(index)+" vs "+difficulty+") ";
	}
}
