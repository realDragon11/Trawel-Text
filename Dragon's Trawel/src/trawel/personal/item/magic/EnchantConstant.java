package trawel.personal.item.magic;

import com.github.yellowstonegames.core.WeightedTable;

import derg.ds.Chomp;
import derg.strings.random.SRFrontBackedRandom;
import trawel.core.mainGame;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.helper.methods.randomLists;

/**
 * A constant enchantment is one that is always active- it applies to the character's base stats.
 * @author dragon
 * 2/8/2018
 */
public class EnchantConstant extends Enchant {
	private static final long serialVersionUID = 2L;
	//instance variables
	/**
	 * actually two bytes, the first is used to indicate the name 'might' fluff ordinal
	 * the second is the actual fluff ordinal
	 */
	private int beforeName = Chomp.emptyInt;
	/**
	 * actually two bytes, the first is used to indicate the name 'might' fluff ordinal
	 * the second is the actual fluff ordinal
	 */
	private int afterName = Chomp.emptyInt;
	// beforeName armor afterName
	private float goldMult = 1;
	private int goldMod = 0;

	private float magnitudeOne,magnitudeTwo;
	
	/**
	 * two 4 bit registers, left register (<<4) is magone, the right is magtwo
	 * magone is 'before', magtwo is 'after'
	 * bit 0 = positive (1), negative (0)
	 * bits 1-3 = turned into int determining type of enchant (max 8 possible with this enchant type)
	 * if all bits are 0 (more importantly, 1-3), then this slot isn't used.
	 */
	private byte enchantTypes = Chomp.emptyByte;
	
	private static WeightedTable enchantChances;
	private static float[][] floatMultList;
	private static String[][][][] stringFluffArr;
	private static SRFrontBackedRandom backFronter;

	public enum EnchantType{
		SPEED, HEALTH, DAMAGE, AIM, DODGE;
		
		public int getNum() {
			return this.ordinal();
		}
	}
	
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
					new String[]{"powerful","offensive","angry","furious"},//pos
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
	
	/**
	 * can be null
	 * @param mod
	 * @param cost
	 * @return the first valid enchant in 4 tries, or null
	 */
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
	
	private static final float positiveGoldMult = .05f;
	private static final float negativeGoldMult = .07f;
	
	private static final int positiveGoldMod = 8;
	private static final int negativeGoldMod = 4;
	
	/**
	 * An constant enchantment which effects the user's stats constantly.
	 * @param powMod (double)
	 */
	public EnchantConstant(float powMod){
		if (powMod <= 0){
			//should be handled top level to prevent the ai from endlessly trying to enchant a steel weapon and failing
			throw new RuntimeException("Not enough base enchant? Did the ai try to enchant a steel weapon?");
		}

		int off1 = 0;//0 = benefit, 1 = downside
		int off2 = 0;
		magnitudeOne = (extra.hrandomFloat()*powMod);
		magnitudeTwo = (extra.hrandomFloat()*powMod);
		
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
		
		
		int might;
		String[] fluff = null;
		byte subType;
		
		//first component of enchantment
		if (magnitudeOne > 0f){
			//two rand floats, one scaled off of mag, one off of 3
			//thus slightly worse than even chances when mag == 3
			//worse chances than the after mighty, to make it rarer
			if (extra.randFloat()*magnitudeTwo > extra.randFloat()*3f ) {
				might = randomLists.powerMightyAdj(true).getNumByte();
				might |= 1 << 7;//sets that we're mighty, note that mighty is limited to 2^7 options due to this
			}else {
				might = Chomp.emptyInt;
			}
			subType = (byte) extra.randRange(0, 4);
			enchantTypes = (byte) (internalNibbleConverter(off1,subType) << 4);
			//System.err.println(off1 + "/" + subType + " 1: " + Integer.toBinaryString(Byte.toUnsignedInt(enchantTypes)));
			switch (off1+(subType*2)) {
			case 0: fluff = stringFluffArr[0][0][0];
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 1: fluff = stringFluffArr[0][0][0];
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 2: fluff = stringFluffArr[0][1][0];
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 3: fluff = stringFluffArr[0][1][1];
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 4: fluff = stringFluffArr[0][2][0];
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 5: fluff = stringFluffArr[0][2][1];
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 6: fluff = stringFluffArr[0][3][0];
			goldMult +=positiveGoldMult*magnitudeOne;
			goldMod +=magnitudeOne*positiveGoldMod;
			break;
			case 7: fluff = stringFluffArr[0][3][1];
			goldMult -=negativeGoldMult*magnitudeOne;
			goldMod -=magnitudeOne*negativeGoldMod;
			break;
			case 8: fluff = stringFluffArr[0][4][0];
			goldMult +=positiveGoldMult*magnitudeOne*.9;
			goldMod +=magnitudeOne*positiveGoldMod*.3;
			break;
			case 9: fluff = stringFluffArr[0][4][1];
			goldMult -=negativeGoldMult*magnitudeOne*.8;
			goldMod -=magnitudeOne*negativeGoldMod*.2;
			break;
		
		}
			backFronter.setBack(fluff);
			beforeName = (might << 8) | (backFronter.getNumByte());
		}
		//second component of enchantment
		if (magnitudeTwo > 0f){
			//two rand floats, one scaled off of mag, one off of 2
			//thus slightly worse than even chances when mag == 2
			if (extra.randFloat()*magnitudeTwo > extra.randFloat()*2f ) {
				might = randomLists.powerMightyAdj(false).getNumByte();
				might |= 1 << 7;//sets that we're mighty, note that mighty is limited to 2^7 options due to this
			}else {
				might = Chomp.emptyInt;
			}
			subType = (byte) extra.randRange(0, 4);
			enchantTypes = (byte) (enchantTypes | internalNibbleConverter(off2, subType));//FIXME
			//System.err.println(off2 + "/" + subType + " 2: " + Integer.toBinaryString(Byte.toUnsignedInt(enchantTypes)));
		switch (off2+(subType*2)) {
			case 0: fluff = stringFluffArr[1][0][0];
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 1: fluff = stringFluffArr[1][0][1];
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 2: fluff = stringFluffArr[1][1][0];
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 3: fluff = stringFluffArr[1][1][1];
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 4: fluff = stringFluffArr[1][2][0];
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 5: fluff = stringFluffArr[1][2][1];
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 6: fluff = stringFluffArr[1][3][0];
			goldMult +=positiveGoldMult*magnitudeTwo;
			goldMod +=magnitudeTwo*positiveGoldMod;
			break;
			case 7: fluff = stringFluffArr[1][3][1];
			goldMult -=.1*magnitudeTwo;
			goldMod -=magnitudeTwo*negativeGoldMod;
			break;
			case 8: fluff = stringFluffArr[1][4][0];
			goldMult +=positiveGoldMult*magnitudeTwo*.9;
			goldMod +=magnitudeTwo*positiveGoldMod*.3;
			break;
			case 9: fluff = stringFluffArr[1][4][1];
			goldMult -=.1*magnitudeTwo*.8;
			goldMod -=magnitudeTwo*negativeGoldMod*.2;
			break;
		
		}
		backFronter.setBack(fluff);
		afterName = ((might << 8) | backFronter.getNumByte());
		}
		//System.err.println("type bits: " + Integer.toBinaryString(Byte.toUnsignedInt(enchantTypes)));
		//System.err.println("before bits: " + Integer.toBinaryString(beforeName));
		//System.err.println("after bits: " + Integer.toBinaryString(afterName));
	}
	
	protected static final int internalNibbleConverter(int negative,int subtype) {
		assert subtype < 8;
		assert subtype >= 0;
		assert negative >= 0;
		assert negative <= 1;
		return ((negative << 3) | subtype);
	}
	/**
	 * this method will fail if you attempt to pass it a byte
	 * but it needs a number of unsigned byte size
	 */
	protected static int subTypeNum(boolean first, int b) {
		/**
		 * needed because java is REALLLLLY dumb about bitwise on bytes and automatically unboxes them as signed byte to int
		 */
		if (first) {
			return ((b & 0b01110000) >>> 4);
		}else {
			return (b & 0b00000111);
		}
	}
	/**
	 * this method will fail if you attempt to pass it a byte
	 * but it needs a number of unsigned byte size
	 */
	protected static boolean isPositive(boolean first, int b) {
		if (first) {
			return (b & 0b10000000) < 1;
		}else {
			return (b & 0b00001000) < 1;
		}
	}
	
	/**
	 * this method will fail if you attempt to pass it an int
	 * but it needs a number of unsigned byte size
	 */
	protected static boolean hasMighty(int b) {
		return (b & 0b10000000) > 0;
	}
	//maybe cache this in something trasient the first time it gets loaded
	//don't need to worry about threading or anything like that since it will always be the same
	
	/**
	 * 
	 * @param num EnchantType.X.getNum()
	 * @param posMag should be positive
	 * @param negMag should be negative
	 * @return
	 */
	protected float getMultFor(int num,float posMag, float negMag) {
		float mult = 1f;
		if (magnitudeOne > 0f) {
			if (subTypeNum(true,enchantTypes) == num) {
				if (isPositive(true,enchantTypes)) {
					mult += magnitudeOne*posMag;
				}else {
					mult += magnitudeOne*negMag;
				}
				
			}
		}
		if (magnitudeTwo > 0f) {
			if (subTypeNum(false,enchantTypes) == num) {
				if (isPositive(false,enchantTypes)) {
					mult += magnitudeTwo*posMag;
				}else {
					mult += magnitudeTwo*negMag;
				}
			}
		}
		return mult;
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
		//int subject;
		if (first) {
			if (magnitudeOne <= 0f) {
				return "";
			}
			//subject = (byte) (in & 0b1111111100000000);
			//subject = (in >>> 8);
		}else {
			if (magnitudeTwo <= 0f) {
				return "";
			}
			//subject = (in & 0b0000000011111111);
		}
		String mighty = null;
		int mbyte = (in >>> 8);
		//System.out.println(Integer.toBinaryString(mbyte));
		if (hasMighty(mbyte)) {
			mighty = randomLists.powerMightyAdj(first).getWithNum(mbyte & 0b01111111);//remove the flag bit (which is probably the sign bit tbh)
		}
		//System.out.println(Integer.toBinaryString(subTypeNum(first,enchantTypes)));
		if (first) {
			return (mighty != null ? mighty + " " : "") + backFronter.setBack(stringFluffArr[0][subTypeNum(first,enchantTypes)][isPositive(first,enchantTypes)? 0 : 1])
			.getWithNum(in & 0b0000000011111111) + " ";
		}else {
			return " of " +(mighty != null ? mighty + " " : "") + backFronter.setBack(stringFluffArr[1][subTypeNum(first,enchantTypes)][isPositive(first,enchantTypes)? 0 : 1])
			.getWithNum(in & 0b0000000011111111);
		}
		
	}


	/**
	 * @return the beforeName (String)
	 */
	@Override
	public String getBeforeName() {
		return inameResolver(true,beforeName);
	}


	/**
	 * @return the afterName (String)
	 */
	@Override
	public String getAfterName() {
		return inameResolver(false,afterName);
	}

	/**
	 * @return the speedMod (double)
	 */
	@Override
	public float getSpeedMod() {
		return getMultFor(EnchantType.SPEED.getNum(),.1f,-.1f);
	}

	/**
	 * @return the healthMod (double)
	 */
	@Override
	public float getHealthMod() {
		return getMultFor(EnchantType.HEALTH.getNum(),.2f,-.1f);
	}

	/**
	 * @return the damMod (double)
	 */
	@Override
	public float getDamMod() {
		return getMultFor(EnchantType.DAMAGE.getNum(),.1f,-.1f);
	}

	/**
	 * @return the aimMod (double)
	 */
	@Override
	public float getAimMod() {
		return getMultFor(EnchantType.AIM.getNum(),.15f,-.1f);
	}

	/**
	 * @return the dodgeMod (double)
	 */
	@Override
	public float getDodgeMod() {
		return getMultFor(EnchantType.DODGE.getNum(),.25f,-.15f);
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
				extra.println("  " +TrawelColor.colorBasedAtOne(d,TrawelColor.TIMID_GREEN,TrawelColor.TIMID_RED,TrawelColor.PRE_WHITE) + "x " + str);
			}
		
		}
		
	}

	@Override
	public Enchant.Type getEnchantType() {
		return Enchant.Type.CONSTANT;
	}
	public static void testAsserts() {
		EnchantConstant test;
		assert isPositive(true, 0b11111111) == false;
		assert isPositive(false, 0b11111111) == false;
		assert isPositive(true, 0) == true;
		assert isPositive(false, 0) == true;
		
		assert hasMighty( 0b10000000) == true;
		assert hasMighty( 0b01111111) == false;
		
		for (int i = 1; i < 6; i++) {
			test = new EnchantConstant(1f*i);
			System.out.println(test.getBeforeName() +"x" + test.getAfterName() + "]" + test.beforeName +"x"+test.afterName +"[" + test.enchantTypes);
			test.display(0);
		}
		byte testb = 1<<1;
		System.out.println("push test: " + Integer.toBinaryString(testb) + " and " + Integer.toBinaryString(testb | (1 << 7)));
		System.out.println("empties: " + Integer.toBinaryString(Chomp.emptyInt) + " " + Integer.toBinaryString(Chomp.emptyByte));
		
		for (int i = 1; i < 6; i++) {
			test = EnchantConstant.makeEnchant(i%1.2f, 50);
			if (test == null) {
				mainGame.errLog("null enchant, not made");
			}else {
				System.out.println(test.getBeforeName() +"x" + test.getAfterName() + "]" + test.beforeName +"x"+test.afterName +"[" + test.enchantTypes);
				test.display(0);
			}
		}
	}

	@Override
	public float fitness() {
		return goldMult;
	}

}
