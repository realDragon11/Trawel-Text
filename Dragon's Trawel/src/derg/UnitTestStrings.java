package derg;

public class UnitTestStrings {

	public static void main(String[] args) {
		StringFluffer fluffy = new StringFluffer().addMapping("bacon:eggs",new SRInOrder("1baconeggsham","2eggshambacon","3hambaconeggs"));
		String testString = "Let's go! |sub(bacon:eggs). <|sub(bacon:eggs)> |sub(bacon:eggs) |sub(bacon:eggs) |sub(bacon:eggs) !!!!!!!!";
		System.out.println(testString);
		System.out.println(fluffy.process(testString));
		
		StringFluffer elementFluffy = new StringFluffer()
				.addMapping("word:fire",new SRPlainRandom("flame","not ice","fire","fire2"))
				.addMapping("word:ice",new SRPlainRandom("cold","freeze","ice","frost","not fire"))
				.addMapping("word:shock",new SRPlainRandom("lightning","sky fire","thunder","air","shocky stuff"))
				.addMapping("word:earth",new SRPlainRandom("ground","land","not sea","earth","dirt"))
				;
		testString = "|sub(bacon:eggs) |sub(word:fire) |sub(word:fire) |sub(word:earth,word:shock,word:shock) |sub(word:ice) |sub(word:fire) |sub(bacon:eggs)";
		System.out.println(testString);
		System.out.println(elementFluffy.process(fluffy.process(testString)));
	}

}
