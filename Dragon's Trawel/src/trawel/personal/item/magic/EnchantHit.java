package trawel.personal.item.magic;

import java.awt.Color;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.randomLists;

public class EnchantHit extends Enchant {

	private static final long serialVersionUID = 1L;
	private float fireMod,freezeMod,shockMod;
	private String name;
	private float goldMult;
	private boolean isKeen;
	private String colorSaved;

	public EnchantHit(float powMod, boolean forceElemental) {
		fireMod = 0;
		freezeMod = 0;
		shockMod = 0;
		if (!forceElemental && Rand.chanceIn(1, 3)) {
			isKeen = true;
			enchantstyle = 0;
			name = randomLists.enchantHitKeen();
			colorSaved = TrawelColor.inlineColor(TrawelColor.colorMix(Color.PINK,Color.LIGHT_GRAY,.5f));
		}else {
			switch (Rand.randRange(1,3)) {
			case 1: fireMod = Rand.randFloat()*powMod/2;
			name = randomLists.enchantHitIgnite();
			enchantstyle = 3;
			colorSaved = TrawelColor.inlineColor(TrawelColor.colorMix(Color.RED,Color.LIGHT_GRAY,.5f));
			break;
			case 2: shockMod = Rand.randFloat()*powMod/2;
			name = randomLists.enchantHitElec();
			enchantstyle = 2;
			colorSaved = TrawelColor.inlineColor(TrawelColor.colorMix(Color.YELLOW,Color.LIGHT_GRAY,.5f));
			break;
			case 3: freezeMod = Rand.randFloat()*powMod/2;
			name = randomLists.enchantHitFrost();
			enchantstyle = 1;
			colorSaved = TrawelColor.inlineColor(TrawelColor.colorMix(Color.BLUE,Color.LIGHT_GRAY,.5f));
			break;
			}
		}
		//hard cap at 20% now
		fireMod = Math.min(.2f,fireMod);
		freezeMod = Math.min(.2f,freezeMod);
		shockMod = Math.min(.2f,shockMod);
		
		goldMult = 1+(freezeMod+shockMod+fireMod+(isKeen ? .3f : 0))/2;
	}
	
	public EnchantHit(boolean b) {//testing only
		fireMod = 0;
		freezeMod = 0;
		shockMod = 0;
		
		goldMult = 1;
		name = " testing";
		enchantstyle = 1;
		colorSaved = TrawelColor.inlineColor(TrawelColor.colorMix(Color.MAGENTA,Color.LIGHT_GRAY,.5f));
	}

	@Override
	public Enchant.Type getEnchantType() {
		return Enchant.Type.HIT;
	}

	@Override
	public void display(int i) {
		if (isKeen) {
			Print.println(TrawelColor.ITEM_WANT_HIGHER+" Keen");
		}
		double d = 0;
		String str = null;
		for (int j = 0; j < 3;j++) {
		switch (j) {
		case 0:	d = getFireMod(); str = colorSaved+"ignite";break;
		case 1:	d = getShockMod(); str = colorSaved+"elec";break;
		case 2:	d = getFreezeMod(); str = colorSaved+"frost";break;
		}
		if (d != 0) {
			Print.println("  "+TrawelColor.ITEM_WANT_HIGHER +Print.format(d) + "x " + str);
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
	
	@Override
	public float fitness() {
		return goldMult;
	}
	

}
