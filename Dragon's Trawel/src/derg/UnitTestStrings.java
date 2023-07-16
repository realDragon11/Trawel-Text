package derg;

public class UnitTestStrings {

	public static void main(String[] args) {
		StringFluffer fluffy = new StringFluffer().addMapping("bacon:eggs",new SRInOrder("1baconeggsham","2eggshambacon","3hambaconeggs"));
		String testString = "Let's go! |sub(bacon:eggs). <|sub(bacon:eggs)> |sub(bacon:eggs) |sub(bacon:eggs) |sub(bacon:eggs) !!!!!!!!";
		System.out.println(testString);
		System.out.println(fluffy.process(testString));
		
	}

}
