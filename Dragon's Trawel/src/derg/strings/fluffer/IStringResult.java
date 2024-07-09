package derg.strings.fluffer;

import java.util.List;

import derg.strings.misc.StringContext;

public interface IStringResult {

	/**
	 * @return some descriptor used to determine what the concrete class's method is
	 */
	public String stringMethod();
	
	/**
	 * @return a (probably semi-random) string, usually from a list
	 */
	public String next();
	
	/**
	 * 
	 * @return if possible, a truly random with Math.random (or some other method that they deem is 'truly random'- speed is now a secondary concern)
	 *  instance from the backing dataset
	 * some cases this might just call next again
	 */
	public String any();
	
	/**
	 * @return the backing list, if possible
	 * End user should assume that changing the backing list may or may not change the results of the random-
	 * varying based on the underlying classes' method
	 * 
	 * if unsupported, will return null
	 */
	public List<String> backing();
	/**
	 * uses context, if not needed will just call next()
	 * @return a (probably semi-random) string, usually from a list
	 */
	public String with(StringContext context);
}
