package derg.ds;

import tests.UnitAssertions;

public class Chomp {

	/**
	 * dragon
	 * Various static methods to be used in other classes
	 * 2/5/2018
	 */
	
	public static final byte emptyByte = 0b00000000;
	public static final byte emptyInt = 0b0000000000000000;

	public static boolean getEnumByteFlag(int flag, byte flags) {
		return (byte) (flags & (1 << flag)) != 0b0;
	}

	public static byte setEnumByteFlag(int flag, byte flags, boolean bool) {
		if (bool) {
			flags |= (1 << flag);
			return flags;
		}
		flags &= ~(1 << flag);
		return flags;
	}

	public static boolean getEnumShortFlag(int flag, short flags) {
		return (short) (flags & (1 << flag)) != 0b0;
	}

	public static short setEnumShortFlag(int flag, short flags, boolean bool) {
		if (bool) {
			flags |= (1 << flag);
			return flags;
		}
		flags &= ~(1 << flag);
		return flags;
	}

	/**
	 * adapted from
	 * https://stackoverflow.com/a/18083093
	 */
	public static byte extractByteFromInt(final int in, final int offset)
	{
		//final int rightShifted = in >>> offset;
		//final int mask = (1 << 8) - 1;
		return (byte) ((in >>> offset) & ((1 << 8) - 1));
	}

	/**
	 * adapted from
	 * https://stackoverflow.com/a/18083093
	 */
	public static int extractIntFromLong(final long l, final int offset)
	{
		//final long rightShifted = l >>> offset;
		//final long mask = (1L << 32) - 1L;
		return (int) ((l >>> offset) & ((1L << 32) - 1L));
	}

	/**
	 * adapted from
	 * https://stackoverflow.com/a/18083093
	 */
	public static byte extractByteFromLong(final long l, final int offset){
		//final long rightShifted = l >>> offset;
		//final long mask = (1L << 8) - 1L;
		return (byte) ((l >>> offset) & ((1L << 8) - 1L));
	}

	/**
	 * must be passed an UNSIGNED number,
	 * so a byte can't actually store the values passing in due to lack of size and
	 * java deleting information when auto(un)boxing
	 * <BR>
	 * WARNING: if you use a binary literal to create the int,
	 * it MUST have the first bit be a 0. If it is a 1,
	 * just pad it with a leading 0,
	 * which is needed to make it 'know' it's negative,
	 * otherwise it will treat it as a two's complement negative number.
	 * Said complement number is strange because it autoconverts it to the bit length instead of taking it literally as a literal.
	 * <br>
	 * always remember to set what this returns! (l parameter) it can't know where to put it!
	 */
	public static long setXInLong(final long l,final int length,final int start_offset, final long toSet) {
		final long allon = ~(0b0);
		return  ((~((allon << start_offset) & (allon >>> (64-(start_offset+length)))) & l) | (toSet) << (start_offset));
	}

	/**
	 * see setXInLong for more details
	 * <BR>
	 * WARNING: if you use a binary literal to create the int,
	 * it MUST have the first bit be a 0. If it is a 1,
	 * just pad it with a leading 0,
	 * <br>
	 * always remember to set what this returns! (l parameter) it can't know where to put it!
	 */
	public static int setXInInt(final int l,final int length,final int start_offset, final int toSet) {
		final int allon = ~(0b0);
		return  ((~((allon << start_offset) & (allon >>> (32-(start_offset+length)))) & l) | (toSet) << (start_offset));
	}

	/**
	 * with my insane commentary
	 */
	public static long setXInLongVerbose(final long l,final int length,final int start_offset, final long toSet) {
		//starting point at https://stackoverflow.com/a/22664554
		//"""return (UINT_MAX >> (CHAR_BIT*sizeof(int)-to)) & (UINT_MAX << (from-1));"""
		//it's really unfortunate that I couldn't find any remotely decent java resources on this, and only bad ones for other languages
		//are these people really counting from 1 or am I going insane
		//this took more trial and error and also just using different language resources than it should have
		//end result was more my work than anything else... what a sad world for search engines to live in
		//looking back I should have just looked for 'entire bitwise tutorial' instead of wanting a basic, universal function like this to be written down
		final long allon = ~(0b0);
		final long offBottom = allon << start_offset;
		final long offTop = allon >>> ((64-(start_offset+length)));
		final long flipoff = ~(offBottom & offTop);
		final long set = (toSet) << (start_offset);//need to cast to long so if it runs up against the end of the bits it doesn't become negative
		final long prep = (flipoff & l);
	
		System.out.println("in"+UnitAssertions.pad(l)+" all" + UnitAssertions.pad(allon));
		System.out.println("top"+UnitAssertions.pad(offTop)+" bot" + UnitAssertions.pad(offBottom));
		System.out.println("off"+UnitAssertions.pad(flipoff));
		System.out.println("prep"+ UnitAssertions.pad(prep)+" set"+UnitAssertions.pad(set));
		return  (prep | set);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that
	 * <br>
	 * b must be a short, int, or long, due to unsigned issues
	 */
	public static long setByteInLong(final long l,final long toset, final int offset) {
		return setXInLong(l,8,offset,toset);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that
	 * <br>
	 * b must be an int or long, due to unsigned issues
	 */
	public static long setShortInLong(final long l,final long toset, final int offset) {
		return setXInLong(l,16,offset,toset);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that
	 * <br>
	 * b must be a long, due to unsigned issues
	 */
	public static long setIntInLong(final long l,final long toset, final int offset) {
		return setXInLong(l,32,offset,toset);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps that through setByteInLong
	 * <br>
	 * b must be a short, int, or long, due to unsigned issues
	 * <br>
	 * 0 <= num <= 7 (0 indexed)
	 */
	public static long setNthByteInLong(final long l,long toset, final int number_of_byte) {
		assert toset <= 255;
		assert toset >= 0;
		/*if (toset > Byte.MAX_VALUE) {
				toset-=Byte.MAX_VALUE;
			}*/
		return setXInLong(l,8,number_of_byte*8,toset);
	}

	/**
	 * see disclaimers in 'setXInLong', this just wraps setXInInt through setByteInInt
	 * <br>
	 * b must be a short, int, or long, due to unsigned issues
	 * <br>
	 * 0 <= num <= 7 (0 indexed)
	 */
	public static int setNthByteInInt(final int l,final int toset, final int number_of_byte) {
		assert toset <= 255;
		assert toset >= 0;
		/*if (toset > Byte.MAX_VALUE) {
				toset-=Byte.MAX_VALUE;
			}*/
		return setXInInt(l,8,number_of_byte*8,toset);
	}

	/**
	 * returns it as a NUMBER in an int. (0 indexed)
	 * <br>
	 * effectively reads the byte as unsigned
	 */
	public static int intGetNthByteFromLong(final long l, final int number_of_byte)
	{
		//final long rightShifted = l >>> number_of_byte*8;
		//final long mask = (1L << 8) - 1L;
		return (int) ((l >>> number_of_byte*8) & ((1L << 8) - 1L));
	}

	/**
	 * returns it as a NUMBER in an int. (0 indexed)
	 * <br>
	 * effectively reads the byte as unsigned
	 */
	public static int intGetNthByteFromInt(final int l, final int number_of_byte)
	{//doesn't need to be different
		return intGetNthByteFromLong(l,number_of_byte);
	}

	/**
	 * returns it as a NUMBER in an int. (0 indexed)
	 * <br>
	 * effectively reads the short as unsigned
	 */
	public static int intGetNthShortFromLong(final long l, final int number_of_short)
	{
		//final long rightShifted = l >>> number_of_short*16;
		//final long mask = (1L << 16) - 1L;
		return (int) ((l >>> number_of_short*16) & ((1L << 16) - 1L));
	}

	/**
	 * returns it as a NUMBER in an int. (0 indexed)
	 * <br>
	 * effectively reads the short as unsigned
	 * <br>
	 * @param offset - in bits
	 */
	public static int extractShortFromLong(final long l, final int offset)
	{
		return (int) ((l >>> offset) & ((1L << 16) - 1L));
	}

}
