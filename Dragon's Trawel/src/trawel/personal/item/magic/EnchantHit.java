package trawel.personal.item.magic;

import java.awt.Color;

import trawel.extra;

public class EnchantHit extends Enchant {

	private static final long serialVersionUID = 1L;
	private float fireMod,freezeMod,shockMod;
	private String name;
	private float goldMult;
	private boolean isKeen;
	private String colorSaved;

	public EnchantHit(float powMod) {
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
			case 1: fireMod = extra.randFloat()*powMod/2;
			name = extra.choose("fire","flame","burning","blazing","heat","charring","the inferno","combustion","conflagration","embers","pyres","scorching","searing","ignition","kindling","flames");
			enchantstyle = 3;
			colorSaved = extra.inlineColor(extra.colorMix(Color.RED,Color.LIGHT_GRAY,.5f));
			break;
			case 2: shockMod = extra.randFloat()*powMod/2;
			name = extra.choose("shock","lightning","shocking","sparks","thundering","zapping");
			enchantstyle = 2;
			colorSaved = extra.inlineColor(extra.colorMix(Color.YELLOW,Color.LIGHT_GRAY,.5f));
			break;
			case 3: freezeMod = extra.randFloat()*powMod/2;
			name = extra.choose("freeze","frost","chilling","rime","freezing","hoarfrost","ice");
			enchantstyle = 1;
			colorSaved = extra.inlineColor(extra.colorMix(Color.BLUE,Color.LIGHT_GRAY,.5f));
			break;
			}
		}
		//hard cap at 20% now
		fireMod = Math.min(.2f,fireMod);
		freezeMod = Math.min(.2f,freezeMod);
		shockMod = Math.min(.2f,shockMod);
		
		goldMult = 1+(freezeMod+shockMod+fireMod+(isKeen ? .3f : 0))/2;
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
	public Enchant.Type getEnchantType() {
		return Enchant.Type.HIT;
	}

	@Override
	public void display(int i) {
		if (isKeen) {
			extra.println(" Keen");
		}
		double d = 0;
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
	@Override
	public float getFireMod() {
		return fireMod;
	}
	@Override
	public float getShockMod() {
		return shockMod;
	}
	@Override
	public float getFreezeMod() {
		return freezeMod;
	}
	
	@Override
	public String getBeforeName() {
		if (isKeen) {
			return colorSaved+name + "[c_white] ";
		}
		return "";
	}
	
	@Override
	public String getAfterName() {
		if (isKeen) {
			return "";
		}
		return " of " +colorSaved+ name+"[c_white]";
	}
	
	@Override
	public float getGoldMult(){
		return goldMult;
	}
	
	@Override
	public boolean isKeen() {
		return isKeen;
	}

	@Override
	public int getGoldMod() {
		return 0;
	}
	

}
