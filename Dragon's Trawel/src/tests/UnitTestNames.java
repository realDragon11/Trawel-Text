package tests;

import trawel.core.mainGame;
import trawel.helper.methods.randomLists;
import trawel.personal.NPCMutator;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
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
			System.out.println(randomLists.randomBearName());
		}
		for (int i = 0; i < 20; i++) {
			System.out.println(randomLists.randomBatName());
		}
		for (int i = 0; i < 5; i++) {
			System.out.println(randomLists.randomDrudgerStockName());
		}
		for (int i = 0; i < 5; i++) {
			System.out.println(randomLists.randomDrudgerHonorName());
		}
		for (int i = 0; i < 20; i++) {
			System.out.println(randomLists.randomEntName());
		}
		
		/*
		for (int i = 0; i < 3;i++) {
			NPCMutator.mutateHonorStockDrudger(RaceFactory.makeDrudgerStock(1)).debugCombatStats();
			for (int j = 0; j < 3;j++) {
				RaceFactory.makeDrudgerMage(3).debugCombatStats();
			}
			RaceFactory.makeDrudgerTitan(5).debugCombatStats();
		}*/
	}
}
