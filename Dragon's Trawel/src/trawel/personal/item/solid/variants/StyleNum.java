package trawel.personal.item.solid.variants;


/**
 * note this is per slot
 */
public interface StyleNum{
	/**
	 * generates an int storing the permutations of a style 
	 */
	public int generate();
	
	/**
	 * returns an array of strings containing the information needed to display the style
	 * typically in the format of a number and a sprite name
	 * the number would be depth, and the alphabetical sprite name would be the style's sprite component
	 * 
	 * the first string is also the name of the item proper to be displayed normally
	 */
	public String[] decode(int gen);
}