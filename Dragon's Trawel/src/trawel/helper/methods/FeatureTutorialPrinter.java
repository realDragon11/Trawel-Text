package trawel.helper.methods;

@FunctionalInterface
public interface FeatureTutorialPrinter {
	public void print();
	//this could also not be a functional interface and provide all the other static data, but constructors are handier for the other types of data
}
