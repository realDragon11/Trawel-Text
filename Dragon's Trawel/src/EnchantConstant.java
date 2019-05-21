/**
 * A constant enchantment is one that is always active- it applies to the character's base stats.
 * @author Brian Malone
 * 2/8/2018
 */
public class EnchantConstant extends Enchant {
	//instance variables
	private String beforeName = "";
	private String afterName = "";// beforeName armor afterName
	private double goldMult = 1;
	private double goldMod = 0;

	//powers
	private double speedMod = 1;
	private double healthMod = 1;
	private double damMod = 1;
	private double aimMod = 1;
	private double dodgeMod = 1;//would add armor, but keywords would be too close to dodging
	private double magnitudeOne,magnitudeTwo;
	
	//constructors
	/**
	 * An constant enchantment which effects the user's stats constantly.
	 * @param powMod (double)
	 */
	public EnchantConstant(double powMod){
		if (powMod <= 0){
			extra.println("Not enough base enchant? Did the ai try to enchant a steel weapon?");
			//should be handled top level to prevent the ai from endlessly trying to enchant a steel weapon and failing
			//is of runtime exception because it isn't worth a throw chain, when it shouldn't happen anyway
			//ie should be avoided, never caught in a try/catch, to avoid any chance of an endless loop
			throw new InvalidEnchantException();
		}
		//first component of enchantment
		magnitudeOne = Math.random()*powMod;
		magnitudeTwo = Math.random()*powMod;
		
		magnitudeOne = extra.clamp(magnitudeOne,1,4);
		magnitudeTwo = extra.clamp(magnitudeTwo,1,4);
		if (extra.chanceIn(3,5)) {//3/5 chance of only getting one enchantment
			if (extra.chanceIn(1,2)) {
			magnitudeTwo = 0;}else {
			magnitudeOne = 0;
			}
		}
		if (magnitudeOne > 0){
		switch ((int)(Math.random()*10)) {
			case 0: beforeName = (String)extra.choose("speedy","quick","fast","hasty","brisk");
			speedMod+=.2*magnitudeOne;
			goldMult +=.2*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 1: beforeName = (String)extra.choose("slow","sluggish","lackadaisical","lethargic","reluctant");
			speedMod-=.2*magnitudeOne;
			goldMult -=.2*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 2: beforeName = (String)extra.choose("healthy","bolstering","hearty","tough","robust","stalwart");
			healthMod+=.2*magnitudeOne;
			goldMult +=.2*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 3: beforeName = (String)extra.choose("sickly","ailing","delicate");
			healthMod-=.2*magnitudeOne;
			goldMult -=.2*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 4: beforeName = (String)extra.choose("powerful","offensive","angry","mighty","furious");
			damMod+=.2*magnitudeOne;
			goldMult +=.2*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 5: beforeName = (String)extra.choose("weak","pitiful","merciful","timid");
			damMod-=.2*magnitudeOne;
			goldMult -=.2*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 6: beforeName = (String)extra.choose("accurate","exact","eagle-eyed");
			aimMod+=.2*magnitudeOne;
			goldMult +=.2*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 7: beforeName = (String)extra.choose("clumsy","unwieldy","bungling","graceless","clunky");
			aimMod-=.2*magnitudeOne;
			goldMult -=.2*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
			case 8: beforeName = (String)extra.choose("dodgy","displacing","evasive");
			dodgeMod+=.2*magnitudeOne;
			goldMult +=.2*magnitudeOne;
			goldMod +=magnitudeOne*20;
			break;
			case 9: beforeName = (String)extra.choose("restraining","exposing","constraining","restrictive");
			dodgeMod-=.2*magnitudeOne;
			goldMult -=.2*magnitudeOne;
			goldMod -=magnitudeOne*20;
			break;
		
		}
		beforeName += " ";
		}
		//second component of enchantment
		if (magnitudeTwo > 0){
			afterName = " of "+randomLists.powerAdjective();
		switch ((int)(Math.random()*10)) {
			case 0: afterName += (String)extra.choose("speed","quickness","haste","alacrity","briskness","fleetness");
			speedMod+=.2*magnitudeTwo;
			goldMult +=.2*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 1: afterName += (String)extra.choose("slowness","sluggishness","reluctance","lethargy");
			speedMod-=.2*magnitudeTwo;
			goldMult -=.2*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 2: afterName += (String)extra.choose("health","heart","resistance","toughness","robustness");
			healthMod+=.2*magnitudeTwo;
			goldMult +=.2*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 3: afterName += (String)extra.choose("sickness","illness","infirmity");
			healthMod-=.2*magnitudeTwo;
			goldMult -=.2*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 4: afterName += (String)extra.choose("power","offense","anger","might","fury");
			damMod+=.2*magnitudeTwo;
			goldMult +=.2*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 5: afterName += (String)extra.choose("weakness","pitifulness","mercy");
			damMod-=.2*magnitudeTwo;
			goldMult -=.2*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 6: afterName += (String)extra.choose("accuracy","aiming","exactness");
			aimMod+=.2*magnitudeTwo;
			goldMult +=.2*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 7: afterName += (String)extra.choose("missing","bungling","floundering");
			aimMod-=.2*magnitudeTwo;
			goldMult -=.2*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
			case 8: afterName += (String)extra.choose("dodging","displacement","evasion","avoidance");
			dodgeMod+=.2*magnitudeTwo;
			goldMult +=.2*magnitudeTwo;
			goldMod +=magnitudeTwo*20;
			break;
			case 9: afterName += (String)extra.choose("restrainment","openness","restriction","stumbling");
			dodgeMod-=.2*magnitudeTwo;
			goldMult -=.2*magnitudeTwo;
			goldMod -=magnitudeTwo*20;
			break;
		
		}
		}
	}
	
	//instance methods
	
	/**
	 * @return the goldMod (double)
	 */
	public double getGoldMod() {
		return goldMod;
	}


	/**
	 * @return the goldMult (double)
	 */
	public double getGoldMult() {
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
	public double getSpeedMod() {
		return speedMod;
	}

	/**
	 * @return the healthMod (double)
	 */
	public double getHealthMod() {
		return healthMod;
	}


	/**
	 * @return the damMod (double)
	 */
	public double getDamMod() {
		return damMod;
	}



	/**
	 * @return the aimMod (double)
	 */
	public double getAimMod() {
		return aimMod;
	}



	/**
	 * @return the dodgeMod (double)
	 */
	public double getDodgeMod() {
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
			extra.println("  " +extra.format(d) + "x " + str);
		}
		
		}
		
	}

	@Override
	public String getEnchantType() {
		return "constant";
	}



}
