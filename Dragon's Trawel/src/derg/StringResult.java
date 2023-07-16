package derg;

public abstract class StringResult {

	/**
	 * @return some descriptor used to determine what the concrete class's method is
	 */
	public abstract String stringMethod();
	
	/**
	 * @return a (probably semi-random) string, usually from a list
	 */
	public abstract String next();
}
