package trawel.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.github.tommyettinger.random.EnhancedRandom;
import com.github.tommyettinger.random.WhiskerRandom;

public class Rand {

	private static final ThreadLocal<EnhancedRandom> localRands = new ThreadLocal<EnhancedRandom>() {
		@Override protected EnhancedRandom initialValue() {
			return new WhiskerRandom();
		}
	};

	/**
	 * gets the rand instance for the current thread, should be used
	 * instead of making your own.
	 * @return
	 */
	public static final EnhancedRandom getRand() {
		//https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html
		return localRands.get();
	}

	public static final float randFloat() {
		return getRand().nextFloat();
	}

	/**
	 * randomly returns one of the parameters
	 * @param a variable amount of strings (String)
	 * @return (String)
	 */
	public static String choose(String... options) {
		return options[getRand().nextInt(options.length)];
	}

	public static <E> E choose(E... options) {
		return options[getRand().nextInt(options.length)];
	}

	/**
	 * Has a (a) in (b) chance of returning true
	 * @param a (int)
	 * @param b (int)
	 * @return (boolean)
	 */
	public static final boolean chanceIn(int a,int b) {
		return (getRand().nextInt(b+1)+1 <= a);
	}

	public static final int randRange(int i, int j) {
		//return (int)(Math.random()*(j+1-i))+i;
		return getRand().nextInt((j+1)-i)+i;
	}

	public static final float randRange(float i, float j) {
		return getRand().nextInclusiveFloat((j)-i)+i;
	}

	/**
	 * given by TEtt from squidsquad
	 */
	public static final double hrandom() {
		return ((Long.bitCount(getRand().nextLong()) - 32. + getRand().nextDouble() - getRand().nextDouble()) / 66.0 + 0.5);
	}

	/**
	 * given by TEtt from squidsquad
	 */
	public static final float hrandomFloat() {
		return ((Long.bitCount(getRand().nextLong()) - 32f + getRand().nextFloat() - getRand().nextFloat()) / 66f + 0.5f);
	}

	public static <E> E randList(ArrayList<E> list) {
		return list.get(getRand().nextInt(list.size()));
	}

	public static <E> E randList(List<E> list) {
		return list.get(getRand().nextInt(list.size()));
	}

	public static <E> E randList(E[] list) {
		return list[getRand().nextInt(list.length)];
	}

	/**
	 * adapted from https://stackoverflow.com/a/68640122
	 * <br>
	 * will return null if couldn't find anything
	 */
	public static <E> E randCollection(Collection<E> collect) {
		return collect.stream().skip(Rand.getRand().nextInt(collect.size())).findAny().orElse(null);
	}

	/**
	 * in most cases you might want to implement a better way with size yourself
	 */
	public static <E> E randStream(Stream<E> stream, int size) {
		return stream.skip(Rand.getRand().nextInt(size)).findAny().orElse(null);
	}

}
