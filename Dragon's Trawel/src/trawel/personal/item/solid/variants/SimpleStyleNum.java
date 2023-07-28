package trawel.personal.item.solid.variants;

import trawel.extra;

public abstract class SimpleStyleNum implements StyleNum {

	private byte[] maxes = new byte[4];
	
	/**
	 * takes 4 numbers which indicate the max bound of each
	 * each will get 8 bytes of space in the int
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public SimpleStyleNum(byte a, byte b, byte c, byte d) {
		maxes[0] = a;
		maxes[1] = b;
		maxes[2] = c;
		maxes[3] = d;
	}
	
	@Override
	public int generate() {
		int store = extra.emptyInt;
		for (int i = 0;i < 4;i++) {
			store |= extra.getRand().nextInt(maxes[i]) << 8;//DOLATER: make sure this actually works
		}
		return store;
	}
	
	public byte[] decodeHelper(int gen) {
		return null;//TODO
	}

}
