package derg.strings.fluffer;

import java.util.List;

import derg.strings.misc.StringContext;

public abstract class StringResult implements IStringResult{

	/**
	 * @return some descriptor used to determine what the concrete class's method is
	 */
	@Override
	public abstract String stringMethod();
	
	/**
	 * @return a (probably semi-random) string, usually from a list
	 */
	@Override
	public abstract String next();
	
	/**
	 * 
	 * @return if possible, a truly random with Math.random (or some other method that they deem is 'truly random'- speed is now a secondary concern)
	 *  instance from the backing dataset
	 * some cases this might just call next again
	 */
	@Override
	public abstract String any();
	
	/**
	 * @return the backing list, if possible
	 * End user should assume that changing the backing list may or may not change the results of the random-
	 * varying based on the underlying classes' method
	 * 
	 * if unsupported, will return null
	 */
	@Override
	public List<String> backing(){
		return null;
	}
	/**
	 * uses context, if not needed will just call next()
	 * @return a (probably semi-random) string, usually from a list
	 */
	@Override
	public String with(StringContext context) {
		return next();
	}
}
