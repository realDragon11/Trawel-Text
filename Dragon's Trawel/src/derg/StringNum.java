package derg;

import trawel.extra;

/**
 * used for StringResults that let you faux-'seed' them, (get a number of the results and then use that to get a result later)
 */
public abstract class StringNum extends StringResult{

	/**
	 * will return null only if the StringResult is empty, or includes nulls
	 * @param i
	 * @return the string, wrapping around if too big
	 */
	public abstract String getWithNum(int i);
	
	/**
	 * this method will get the exact number, and instead of wrapping around,
	 * will return a null pointer
	 * @param i
	 * @return the string, or null
	 */
	public abstract String getWithNumExact(int i);
	
	/**
	 * will return null only if the StringResult is empty, or includes nulls
	 * @param i, which will be converted to an unsigned int from byte form
	 * @return the string, wrapping around if too big
	 */
	public String getWithNum(byte i) {
		return getWithNum(Byte.toUnsignedInt(i));
	}
	
	/**
	 * this method will get the exact number, and instead of wrapping around,
	 * will return a null pointer
	 * @param i, which will be converted to an unsigned int from byte form
	 * @return the string, or null
	 */
	public String getWithNumExact(byte i){
		return getWithNumExact(Byte.toUnsignedInt(i));
	}
	
	/**
	 * gets a number that can be stored to get a stringresult later from this StringResult
	 * @return the number
	 */
	public abstract int getNum();
	
	/**
	 * 
	 * @return the max number that getNum could give, usually the size of the backing list
	 */
	public abstract int getMaxNum();
	
	/**
	 * gets an unsigned byte that can be stored to get a stringresult later from this StringResult
	 * @return the unsigned byte
	 */
	public byte getNumByte() {
		assert getMaxNum() < 256;//we can probably do 256 since we use 0
		return (byte) extra.getRand().nextInt(getMaxNum());
	}
}
