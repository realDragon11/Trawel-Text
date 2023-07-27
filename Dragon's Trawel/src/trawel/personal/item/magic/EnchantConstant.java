package trawel.personal.item.magic;

import java.util.List;
import java.util.Random;

import com.github.tommyettinger.random.WhiskerRandom;
import com.github.yellowstonegames.core.WeightedTable;

import derg.SRFrontBackedRandom;
import trawel.extra;
import trawel.randomLists;

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
	/**
	 * actually two bytes, the first is used to indicate the name 'might' fluff ordinal
	 * the second is the actual fluff ordinal
	 */
	private int beforeName = 0;
	/**
	 * actually two bytes, the first is used to indicate the name 'might' fluff ordinal
	 * the second is the actual fluff ordinal
	 */
	private int afterName = 0;
	// beforeName armor afterName
	private float goldMult = 1;
	private int goldMod = 0;

	//powers
	private float speedMod = 1;
	private float healthMod = 1;
	private float damMod = 1;
	private float aimMod = 1;
	private float dodgeMod = 1;//would add armor, but keywords would be too close to dodging
	private float magnitudeOne,magnitudeTwo;
	
	/**
	 * two 4 bit registers, left register (<<4) is magone, the right is magtwo
	 * magone is 'before', magtwo is 'after'
	 * bit 0 = positive (1), negative (0)
	 * bits 1-3 = turned into int determining type of enchant (max 8 possible with this enchant type)
	 * if all bits are 0 (more importantly, 1-3), then this slot isn't used.
	 */
	private byte enchantTypes = 0;
	
	private static WeightedTable enchantChances;
	private static float[][] floatMultList;
	private static String[][][][] stringFluffArr;
	private static SRFrontBackedRandom backFronter;
	
	public static void init() {
		
		assert Integer.bitCount(0) == 0;//otherwise our math doesn't work.
		
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
				float chance = floatMultList[i][4];
				if (!(floatMultList[i][1] != 0 && floatMultList[i][3] != 0)) {
					//if we're not a multistat one
					chance *= 1.6f;
				}
				if ((floatMultList[i][1] != 0 && floatMultList[i][3] != 0)) {
					//if we don't have a downside
					chance *= 1.2f;
				}
				chances[i] = chance;
			}
			enchantChances = new WeightedTable(chances);

			//string fluff enchant
			stringFluffArr = new String[][][][]{
				new String[][][]{//before
				{//speed
					new String[]{"speedy","quick","fast","hasty","brisk"},//pos
					new String[]{"slow","sluggish","lackadaisical","lethargic","reluctant"}//neg
				},
				{//health
					new String[]{"healthy","bolstering","hearty","tough","robust","stalwart"},//pos
					new String[]{"sickly","ailing","delicate"}//neg
				},
				{//damage
					new String[]{"powerful","offensive","angry","mighty","furious"},//pos
					new String[]{"weak","pitiful","merciful","timid"}//neg
				},
				{//aim
					new String[]{"accurate","exact","eagle-eyed"},//pos
					new String[]{"clumsy","unwieldy","bungling","graceless","clunky"}//neg
				},
				{//dodging
					new String[]{"dodgy","displacing","evasive"},//pos
					new String[]{"restraining","exposing","constraining","restrictive"}//neg
				}/*,
				{//x
					new String[]{},//pos
					new String[]{}//neg
				}*/
				},
				new String[][][]{//after
					{//speed
						new String[]{"speed","quickness","haste","alacrity","briskness","fleetness"},//pos
						new String[]{"slowness","sluggishness","reluctance","lethargy"}//neg
					},
					{//health
						new String[]{"health","heart","resistance","toughness","robustness"},//pos
						new String[]{"sickness","illness","infirmity"}//neg
					},
					{//damage
						new String[]{"power","offense","anger","might","fury"},//pos
						new String[]{"weakness","pitifulness","mercy"}//neg
					},
					{//aim
						new String[]{"accuracy","aiming","exactness"},//pos
						new String[]{"missing","bungling","floundering"}//neg
					},
					{//dodging
						new String[]{"dodging","displacement","evasion","avoidance"},//pos
						new String[]{"restrainment","openness","restriction","stumbling"}//neg
					}
				}
			};
			backFronter = new SRFrontBackedRandom();
	}
	/**
	 * {//x
					new String[]{},//pos
					new String[]{}//neg
				}
	 */
	
	//constructors
	
	public static EnchantConstant makeEnchant(float mod, int cost) {
		for (int i = 0; i < 4;i++) {
			EnchantConstant enchant = new EnchantConstant(mod);
			if (cutOff(enchant,.6f,.7f)) {
				continue;
			}
			int newcost = (int) (cost*enchant.getGoldMult()+enchant.getGoldMod());
			if (newcost < 10 || newcost < cost*0.04f) {
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
		
		float[] mults = floatMultList[enchantChances.random(extra.getRand())];
		off1 = mults[0] == 0 ? 1 : 0;//0 = bad, but not in our offset scheme
		magnitudeOne *= mults[1];
		off2 = mults[2] == 0 ? 1 : 0;
		magnitudeOne *= mults[3];
		
		if (magnitudeOne > 0) {
			magnitudeOne = extra.clamp(magnitudeOne,0.1f,2f);
		}
		if (magnitudeTwo > 0) {
			magnitudeTwo = extra.clamp(magnitudeTwo,0.1f,2f);
		}
		float positiveGoldMult = .05f;
		float negativeGoldMult = .07f;
		
		int positiveGoldMod = 8;
		int negativeGoldMod = 4;
		
		byte might = 0;
		String[] fluff = null;
		int subType;
		
		
		if (magnitudeOne > 0){
			if (extra.randFloat() < magnitudeOne*3) {
				//TODO: afterName = randomLists.powerAdjective()+ " ";
				might = 1<<1;//FIXME
			}
			subType = extra.randRange(0, 4);
			enchantTypes = (byte) (internalByteConverter(off1,subType) << 4);
			switch (off1+(subType*2)) {
			case 0: fluff = stringFluffArr[0][0][0];
			speedMod+=.2*magnitudeOne;
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 1: fluff = stringFluffArr[0][0][0];
			speedMod-=.2*magnitudeOne;
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 2: fluff = stringFluffArr[0][1][0];
			healthMod+=.2*magnitudeOne;
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 3: fluff = stringFluffArr[0][1][1];
			healthMod-=.2*magnitudeOne;
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 4: fluff = stringFluffArr[0][2][0];
			damMod+=.2*magnitudeOne;
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 5: fluff = stringFluffArr[0][2][1];
			damMod-=.2*magnitudeOne;
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 6: fluff = stringFluffArr[0][3][0];
			aimMod+=.2*magnitudeOne;
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 7: fluff = stringFluffArr[0][3][1];
			aimMod-=.2*magnitudeOne;
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 8: fluff = stringFluffArr[0][4][0];
			dodgeMod+=.2*magnitudeOne;
			goldMult +=positiveGoldMult*magnitudeOne*.9;
			goldMod +=magnitudeOne*positiveGoldMod*.3;
			break;
			case 9: fluff = stringFluffArr[0][4][1];
			dodgeMod-=.2*magnitudeOne;
			goldMult -=negativeGoldMult*magnitudeOne*.8;
			goldMod -=magnitudeOne*negativeGoldMod*.2;
			break;
		
		}
			backFronter.setBack(fluff);
			beforeName = (might << 4) | (backFronter.getNumByte());
		}
		//second component of enchantment
		if (magnitudeTwo > 0){
			if (extra.randFloat() < magnitudeTwo*3) {
				//afterName = " of "+randomLists.powerAdjective()+ " ";
				//FIXME:
				might = 1<<1;
			}
			subType = extra.randRange(0, 4);
			enchantTypes = (byte) (enchantTypes | internalByteConverter(off2, subType));//FIXME
		switch (off2+(subType*2)) {
			case 0: fluff = stringFluffArr[1][0][0];
			speedMod+=.1*magnitudeTwo;
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 1: fluff = stringFluffArr[1][0][1];
			speedMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 2: fluff = stringFluffArr[1][1][0];
			healthMod+=.1*magnitudeTwo;
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 3: fluff = stringFluffArr[1][1][1];
			healthMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 4: fluff = stringFluffArr[1][2][0];
			damMod+=.1*magnitudeTwo;
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 5: fluff = stringFluffArr[1][2][1];
			damMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 6: fluff = stringFluffArr[1][3][0];
			aimMod+=.1*magnitudeTwo;
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 7: fluff = stringFluffArr[1][3][1];
			aimMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 8: fluff = stringFluffArr[1][4][0];
			dodgeMod+=.1*magnitudeTwo;
			goldMult +=positiveGoldMult*magnitudeTwo*.9;
			goldMod +=magnitudeTwo*positiveGoldMod*.3;
			break;
			case 9: fluff = stringFluffArr[1][4][1];
			dodgeMod-=.1*magnitudeTwo;
			goldMult -=.1*magnitudeTwo*.8;
			goldMod -=magnitudeTwo*negativeGoldMod*.2;
			break;
		
		}
		backFronter.setBack(fluff);
		afterName = (might << 4) | (backFronter.getNumByte());
		}
	}
	
	protected static final byte internalByteConverter(int positive,int subtype) {
		return (byte) (((byte)positive << 4) | ((byte)subtype));
	}
	
	protected static int subTypeNum(boolean first, byte b) {
		if (first) {
			return b & 0b01110000;
		}else {
			return b & 0b00000111;
		}
	}
	protected static int isPositive(boolean first, byte b) {
		if (first) {
			return b & 0b10000000;
		}else {
			return b & 0b00001000;
		}
	}
	
	protected static boolean hasMighty(byte b) {
		return (b & 0b10000000) > 0;
	}
	
	//instance methods
	
	/**
	 * @return the goldMod (int)
	 */
	@Override
	public int getGoldMod() {
		return goldMod;
	}


	/**
	 * @return the goldMult (double)
	 */
	@Override
	public float getGoldMult() {
		return goldMult;
	}
	
	protected String inameResolver(boolean first, int in) {
		String mighty = "";
		if (hasMighty((byte) (in >>> 4))) {
			mighty = "later ";
		}
		return mighty + backFronter.setBack(stringFluffArr[first ? 0 : 1][subTypeNum(first,enchantTypes)][isPositive(first,enchantTypes)])
		.getWithNum(in & 0b0000000011111111);
	}


	/**
	 * @return the beforeName (String)
	 */
	public String getBeforeName() {
		String mighty = "";
		if (hasMighty((byte) (beforeName >>> 4))) {
			mighty = "later ";
		}
		return mighty + backFronter.setBack(stringFluffArr[0][subTypeNum(true,enchantTypes)][isPositive(true,enchantTypes)])
		.getWithNum(beforeName & 0b0000000011111111);
	}


	/**
	 * @return the afterName (String)
	 */
	public String getAfterName() {
		return inameResolver(false,afterName);
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
	public static void testAsserts() {
		EnchantConstant test = new EnchantConstant(20f);
		assert isPositive(true, (byte) 0b11111111) == 1;
		assert isPositive(false, (byte) 0b11111111) == 1;
		assert isPositive(true, (byte) 0) == 0;
		assert isPositive(false, (byte) 0) == 0;
		
	}



}
