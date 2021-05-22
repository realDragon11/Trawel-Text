package trawel;

import java.awt.Color;

public class EnchantHit extends Enchant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double fireMod,freezeMod,shockMod;
	private String name;
	private double goldMult;
	private boolean isKeen;
	private String colorSaved;
	
	public EnchantHit(double powMod) {
		//must constrain all enchantments from 0 to 1
		//well not must... but should.
		//max powmod is 2
		fireMod = 0;
		freezeMod = 0;
		shockMod = 0;
		if (extra.chanceIn(1, 3)) {
			isKeen = true;
			enchantstyle = 0;
			name = extra.choose("keen","honed","whetted");
			colorSaved = extra.inlineColor(extra.colorMix(Color.PINK,Color.LIGHT_GRAY,.5f));
		}else {
		switch (extra.randRange(1,3)) {
		case 1: fireMod = Math.random()*powMod/2;
		name = extra.choose("fire","flame","burning","blazing","heat","charring","the inferno","combustion","conflagration","embers","pyres","scorching","searing","ignition","kindling","flames");
		enchantstyle = 3;
		colorSaved = extra.inlineColor(extra.colorMix(Color.RED,Color.LIGHT_GRAY,.5f));
		break;
		case 2: shockMod = Math.random()*powMod/2;
		name = extra.choose("shock","lightning","shocking","sparks","thundering","zapping");
		enchantstyle = 2;
		colorSaved = extra.inlineColor(extra.colorMix(Color.YELLOW,Color.LIGHT_GRAY,.5f));
		break;
		case 3: freezeMod = Math.random()*powMod/2;
		name = extra.choose("freeze","frost","chilling","rime","freezing","hoarfrost","ice");
		enchantstyle = 1;
		colorSaved = extra.inlineColor(extra.colorMix(Color.BLUE,Color.LIGHT_GRAY,.5f));
		break;
		}}
		goldMult = 1+(freezeMod+shockMod+fireMod+(isKeen ? .3 : 0))/2;
	}
	
	public EnchantHit(boolean b,double powMod) {//testing only
		fireMod = 0;
		freezeMod = 0;
		shockMod = 0;
		
		goldMult = 1;
		name = " testing";
		enchantstyle = 1;
		colorSaved = extra.inlineColor(extra.colorMix(Color.MAGENTA,Color.LIGHT_GRAY,.5f));
	}

	@Override
	public String getEnchantType() {
		return "hit";
	}

	@Override
	public void display(int i) {
		double d = 2;
		String str = null;
		for (int j = 0; j < 3;j++) {
		switch (j) {
		case 0:	d = getFireMod(); str = "fire";break;
		case 1:	d = getShockMod(); str = "shock";break;
		case 2:	d = getFreezeMod(); str = "frost";break;
		}
		if (d != 0) {
			extra.println("  " +extra.format(d) + "x " + str);
		}
		
		}

	}

	
	// should be between 0 and 1 for all three
	//defense should be between 0 and 2
	
	//1 fire means 50% of armor will be down by end
	public double getFireMod() {
		return fireMod;
	}
	public double getShockMod() {
		return shockMod;
	}
	public double getFreezeMod() {
		return freezeMod;
	}
	
	public String getName() {
		if (isKeen) {
			return colorSaved+name + "[c_white] ";
		}
		return " of " +colorSaved+ name+"[c_white]";
	}
	
	public double getGoldMult(){
		return goldMult;
	}
	
	public boolean isKeen() {
		return isKeen;
	}
	

}
