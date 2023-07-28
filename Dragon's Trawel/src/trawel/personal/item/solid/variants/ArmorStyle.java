package trawel.personal.item.solid.variants;

/**
 * Stores a 'style' of armor, which, when combined with it's slot and material, give complete the physical properties of the item
 * not storable directly, but supports retrieval with shorts
 */
public enum ArmorStyle {
	PLATE,
	END;//example
	
	public StyleNum[] genner = new StyleNum[5];
	public float sharpMult, bluntMult, pierceMult, totalMult;//might turn into arrays later
	public float enchantMult, weightMult, costMult;
	public float dexMultBase;//combined with material and slot weirdly
	public RestrictType restrictType;
	public boolean[] bannedSlots = new boolean[] {false,false,false,false,false};
	
	//FIXME: if not using global pallete swapping, also needs ids
	ArmorStyle(){
		
	}
	
	public enum RestrictType{
		AGILE, LIGHT, MEDIUM, HEAVY//FIXME: change names
	}
	
	
	public static void init() {
		PLATE.sharpMult = 1.2f;
		PLATE.bluntMult = 1f;
		PLATE.pierceMult = 1f;
		PLATE.totalMult = 1.2f;
		PLATE.enchantMult = 1f;
		PLATE.weightMult = 2f;
		PLATE.costMult = 2f;
		PLATE.genner[0] = new NameStyleNum("plate helm");
		PLATE.genner[1] = new NameStyleNum("plate gauntlets");
		PLATE.genner[2] = new NameStyleNum("plate chestplate");
		PLATE.genner[3] = new NameStyleNum("plate greaves");
		PLATE.genner[4] = new NameStyleNum("plate boots");
			
		};
	
	public static ArmorStyle fetch(short i) {
		return ArmorStyle.values()[i];
	}
}
