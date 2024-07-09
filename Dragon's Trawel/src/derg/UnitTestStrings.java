package derg;

import trawel.mainGame;
import trawel.helper.methods.extra;
import trawel.towns.Calender;

public class UnitTestStrings {

	public static void main(String[] args) {
		extra.setMainThread();
		System.out.println("starting");
		mainGame.unitTestSetup();
		System.out.println("setup");
		
		Calender cal = new Calender();
		float latlong = 40f;
		for (double i = 21.1; i < 365; i++) {
			double[] fl = cal.getSunTime(i, latlong, latlong);
			System.out.println(Calender.getLocalTime(fl[0],latlong) +"|"+Calender.getLocalTime(fl[1],latlong) +"|"+Calender.getLocalTime(fl[2],latlong));
			cal.timeCounter = i;
			float[] fa = cal.getBackTime(latlong, latlong);
			System.out.println("  Hour: "+(cal.timeCounter%24)+": "+fa[0] + "_"+fa[1]);
		}
		
		
		
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
		
		System.out.println("-------");
		for (int i = 0; i < 100;i++) {
			System.out.print(extra.hrandomFloat()+", ");
		}
		
		System.out.println("-------");
		
		LembdaFactory.test();
	}

}
