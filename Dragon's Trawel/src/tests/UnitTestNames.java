package tests;

import trawel.core.mainGame;
import trawel.helper.methods.randomLists;
import trawel.threads.ThreadData;

public class UnitTestNames {
	public static void main(String[] args) {
		ThreadData.setMainThread();
		System.out.println("starting");
		mainGame.unitTestSetup();
		System.out.println("setup");
		for (int i = 0; i < 20; i++) {
			System.out.println(randomLists.randomWolfName());
		}
		for (int i = 0; i < 20; i++) {
			System.out.println(randomLists.randomDrudgerStockName());
		}
		for (int i = 0; i < 20; i++) {
			System.out.println(randomLists.randomDrudgerHonorName());
		}
	}
}
