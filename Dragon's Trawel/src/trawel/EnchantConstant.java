package trawel;

import java.util.List;
import java.util.Random;

import com.github.tommyettinger.random.WhiskerRandom;
import com.github.yellowstonegames.core.WeightedTable;

/**
 * A constant enchantment is one that is always active- it applies to the character's base stats.
 * @author Brian Malone
 * 2/8/2018
 */
public class EnchantConstant extends Enchant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	//instance variables
	private String beforeName = "";
	private String afterName = "";// beforeName armor afterName
	private float goldMult = 1;
	private int goldMod = 0;

	//powers
	private float speedMod = 1;
	private float healthMod = 1;
	private float damMod = 1;
	private float aimMod = 1;
	private float dodgeMod = 1;//would add armor, but keywords would be too close to dodging
	private float magnitudeOne,magnitudeTwo;
	
	private static WeightedTable enchantChances;
	private static float[][] floatMultList;
	private static Random juneRand;
	
	public static void init() {
		juneRand = new WhiskerRandom();
		
		floatMultList = new float[][] {
			{1,1f,9,0f,8},//first mult normal, no second mult
			{0,1f,9,0f,4},//bad first, no second
			{9,0f,1,1f,8},//no first, same second
			{9,0f,0,1f,4},//no first, bad second
			{1,.8f,1,.8f,10},//weak both
			{0,.6f,0,.6f,5},//bad weak both
			{1,1f,1,1f,8},//same both
			{0,.8f,0,.8f,8},//bad both
			
			{1,1.3f,9,0f,10},//strong first, no second
			{0,1.2f,9,0f,6},//strong bad first, no second
			{9,0f,1,1.2f,10},//no first, strong second
			{9,0f,0,1.1f,6},//no first, strong bad second
			{1,1.1f,1,1.1f,12},//semistrong both
			{0,1.1f,0,1.1f,4},//bad semistrong both
			{1,1.2f,1,1.2f,10},//strong both
			
			{1,1.5f,0,1f,10},//strong first, bad second
			{0,1.3f,1,1f,5},//strong bad first, same second
			{0,1f,1,1.5f,10},//bad first, strong second
			{1,1f,0,1.3f,5},//same first, strong bad second
			{1,1.3f,0,1.2f,12},//both strong, good/bad
			{0,1.2f,1,1.3f,12},//both strong, bad/good
			
			{1,1.35f,0,.6f,12},//strong first, less bad second
			{1,1f,0,.4f,8},//same first, less bad second
			{0,.4f,1,1f,8},//less bad first, same second
			{0,.6f,1,1.35f,12},//less bad first, strong second
			
			//make sure no trailing comma
			};
			float[] chances = new float[floatMultList.length];
			for (int i = floatMultList.length-1;i>=0;i--) {
				chances[i] = floatMultList[i][4];
			}
			enchantChances = new WeightedTable(chances);
	}
	
	//constructors
	
	public static EnchantConstant makeEnchant(float mod) {
		for (int i = 0; i < 4;i++) {
			EnchantConstant enchant = new EnchantConstant(mod);
			if (cutOff(enchant,.6f,.7f)) {
				continue;
			}
			return enchant;
		}
		return null;//give up
	}
	
	/**
	 * 
	 * @param enchant
	 * @param overall mult of properties min(.6f suggested)
	 * @param min allowed property (.7f suggested)
	 * @return if the enchantment sucks too much
	 */
	public static boolean cutOff(EnchantConstant enchant, float overall, float min) {
		if (enchant.getAimMod() < min || enchant.getDamMod() < min || enchant.getDodgeMod() < min || enchant.getHealthMod() < min || enchant.getSpeedMod() < min){
			return true;
		}
		if (enchant.getAimMod()*enchant.getDamMod()*enchant.getDodgeMod()*enchant.getHealthMod()*enchant.getSpeedMod() < overall){
			return true;
		}
		return false;
	}
	
	/**
	 * An constant enchantment which effects the user's stats constantly.
	 * @param powMod (double)
	 */
	public EnchantConstant(float powMod){
		if (powMod <= 0){
			//should be handled top level to prevent the ai from endlessly trying to enchant a steel weapon and failing
			throw new RuntimeException("Not enough base enchant? Did the ai try to enchant a steel weapon?");
		}
		//first component of enchantment
		
		
		
		int off1 = 0;//0 = benefit, 1 = downside
		int off2 = 0;
		magnitudeOne = (extra.hrandomFloat()*powMod);
		magnitudeTwo = (extra.hrandomFloat()*powMod);
		/*
		int rng = extra.randRange(0,14);
		float badfloat = extra.randFloat();
		switch (rng) {
		case 0: case 6://yn weak enchant
			magnitudeOne = (extra.hrandomFloat()*powMod);
			magnitudeTwo = 0;
			if (badfloat < .2f) {
				off1 = 1;
			}
			break;
		case 1: case 7://ny weak enchant
			magnitudeOne = 0;
			magnitudeTwo = (extra.hrandomFloat()*powMod);
			break;
		case 2: case 8: case 11://yy weak enchant
			magnitudeOne = (extra.hrandomFloat()*powMod);
			magnitudeTwo = (extra.hrandomFloat()*powMod);
			break;
		case 3: case 9: case 12://yn strong enchant
			magnitudeOne = 1.5f*(extra.hrandomFloat()*powMod);
			magnitudeTwo = 0;
			break;
		case 4: case 10: case 13://ny strong enchant
			magnitudeOne = 0;
			magnitudeTwo = 1.5f*(extra.hrandomFloat()*powMod);
			break;
		case 5: case 14://yy strong enchant
			magnitudeOne = 1.4f*(extra.hrandomFloat()*powMod);
			magnitudeTwo = 1.4f*(extra.hrandomFloat()*powMod);
			break;
		default://def
			magnitudeOne = (extra.hrandomFloat()*powMod);
			magnitudeTwo = (extra.hrandomFloat()*powMod);
			break;
		}*/
		
		float[] mults = floatMultList[enchantChances.random(juneRand)];
		off1 = mults[0] == 0 ? 1 : 0;//0 = bad, but not in our offset scheme
		magnitudeOne *= mults[1];
		off2 = mults[2] == 0 ? 1 : 0;
		magnitudeOne *= mults[3];
		
		if (magnitudeOne > 0) {
			magnitudeOne = extra.clamp(magnitudeOne,0.1f,3f);
		}
		if (magnitudeTwo > 0) {
			magnitudeTwo = extra.clamp(magnitudeTwo,0.1f,3f);
		}
		if (magnitudeOne > 0){
			switch (off1+(extra.randRange(0, 4)*2)) {
			case 0: beforeName = (String)extra.choose("speedy","quick","fast","hasty","brisk");
			speedMod+=.1*magnitudeOne;
			goldMult +=.1*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 1: beforeName = (String)extra.choose("slow","sluggish","lackadaisical","lethargic","reluctant");
			speedMod-=.1*magnitudeOne;
			goldMult -=.1*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 2: beforeName = (String)extra.choose("healthy","bolstering","hearty","tough","robust","stalwart");
			healthMod+=.1*magnitudeOne;
			goldMult +=.1*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 3: beforeName = (String)extra.choose("sickly","ailing","delicate");
			healthMod-=.1*magnitudeOne;
			goldMult -=.1*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 4: beforeName = (String)extra.choose("powerful","offensive","angry","mighty","furious");
			damMod+=.1*magnitudeOne;
			goldMult +=.1*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 5: beforeName = (String)extra.choose("weak","pitiful","merciful","timid");
			damMod-=.1*magnitudeOne;
			goldMult -=.1*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 6: beforeName = (String)extra.choose("accurate","exact","eagle-eyed");
			aimMod+=.1*magnitudeOne;
			goldMult +=.1*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 7: beforeName = (String)extra.choose("clumsy","unwieldy","bungling","graceless","clunky");
			aimMod-=.1*magnitudeOne;
			goldMult -=.1*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 8: beforeName = (String)extra.choose("dodgy","displacing","evasive");
			dodgeMod+=.1*magnitudeOne;
			goldMult +=.1*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 9: beforeName = (String)extra.choose("restraining","exposing","constraining","restrictive");
			dodgeMod-=.1*magnitudeOne;
			goldMult -=.1*magnitudeOne;
			goldMod -=magnitudeOne*10;
			break;
		
		}
		beforeName += " ";
		}
		//second component of enchantment
		if (magnitudeTwo > 0){
			afterName = " of "+randomLists.powerAdjective()+ " ";
		switch (off1+(extra.randRange(0, 4)*2)) {
			case 0: afterName += (String)extra.choose("speed","quickness","haste","alacrity","briskness","fleetness");
			speedMod+=.1*magnitudeTwo;
			goldMult +=.1*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 1: afterName += (String)extra.choose("slowness","sluggishness","reluctance","lethargy");
			speedMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 2: afterName += (String)extra.choose("health","heart","resistance","toughness","robustness");
			healthMod+=.1*magnitudeTwo;
			goldMult +=.1*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 3: afterName += (String)extra.choose("sickness","illness","infirmity");
			healthMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 4: afterName += (String)extra.choose("power","offense","anger","might","fury");
			damMod+=.1*magnitudeTwo;
			goldMult +=.1*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 5: afterName += (String)extra.choose("weakness","pitifulness","mercy");
			damMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 6: afterName += (String)extra.choose("accuracy","aiming","exactness");
			aimMod+=.1*magnitudeTwo;
			goldMult +=.1*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 7: afterName += (String)extra.choose("missing","bungling","floundering");
			aimMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 8: afterName += (String)extra.choose("dodging","displacement","evasion","avoidance");
			dodgeMod+=.1*magnitudeTwo;
			goldMult +=.1*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 9: afterName += (String)extra.choose("restrainment","openness","restriction","stumbling");
			dodgeMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*10;
			break;
		
		}
		}
	}
	
	//instance methods
	
	/**
	 * @return the goldMod (int)
	 */
	public int getGoldMod() {
		return goldMod;
	}


	/**
	 * @return the goldMult (double)
	 */
	public float getGoldMult() {
		return goldMult;
	}


	/**
	 * @return the beforeName (String)
	 */
	public String getBeforeName() {
		return beforeName;
	}


	/**
	 * @return the afterName (String)
	 */
	public String getAfterName() {
		return afterName;
	}

	/**
	 * @return the speedMod (double)
	 */
	@Override
	public float getSpeedMod() {
		return speedMod;
	}

	/**
	 * @return the healthMod (double)
	 */
	@Override
	public float getHealthMod() {
		return healthMod;
	}

	/**
	 * @return the damMod (double)
	 */
	@Override
	public float getDamMod() {
		return damMod;
	}

	/**
	 * @return the aimMod (double)
	 */
	@Override
	public float getAimMod() {
		return aimMod;
	}

	/**
	 * @return the dodgeMod (double)
	 */
	@Override
	public float getDodgeMod() {
		return dodgeMod;
	}

	@Override
	public void display(int i) {
		double d = 2;
		String str = null;
		for (int j = 0; j < 5;j++) {
			switch (j) {
			case 0:	d = getSpeedMod(); str = "speed";break;
			case 1:	d = getHealthMod(); str = "health";break;
			case 2:	d = getDamMod(); str = "dam";break;
			case 3:	d = getAimMod(); str = "aim";break;
			case 4:	d = getDodgeMod(); str = "dodge";break;
			}
			if (d != 1) {
				extra.println("  " +extra.colorBasedAtOne(d,extra.TIMID_GREEN,extra.TIMID_RED,extra.PRE_WHITE) + "x " + str);
			}
		
		}
		
	}

	@Override
	public Enchant.Type getEnchantType() {
		return Enchant.Type.CONSTANT;
	}



}
