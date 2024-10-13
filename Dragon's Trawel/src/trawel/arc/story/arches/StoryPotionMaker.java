package trawel.arc.story.arches;

import trawel.arc.story.Story;
import trawel.core.Print;
import trawel.personal.people.Player;
import trawel.towns.contexts.Town;
import trawel.towns.features.Feature;
import trawel.towns.features.services.WitchHut;

public class StoryPotionMaker extends Story {

	/**
	 * 0 = no advising
	 * 1 = advised to make potion
	 * 2 = advised inside witch hut
	 */
	private int adviseStage = 0, lastAdviseStage = 0;
	
	private int deathTicker = 0;
	
	private WitchHut lastWitchHut;
	
	@Override
	public void storyStart() {
		checkToAdvisePotion();
	}
	
	private Town nearestWitchHutTown() {
		return null;//TODO
	}
	
	@Override
	public void enterFeature(Feature f) {
		if (adviseStage == 1 && f instanceof WitchHut) {
			updateAdviseStage(2);
			lastWitchHut = (WitchHut) f;
			Print.println("A Cauldron! Perfect for brewing a potion in.");
			return;//skip rest of checks
		}
		checkToAdvisePotion();
	}
	
	@Override
	public void onDeathPart2(){
		deathTicker++;
		if (deathTicker%3==0) {//every 3 times (1st death is 1 so doesn't proc immediately)
			switch (lastAdviseStage) {
			case 1:
				Print.println("You think a potion could help you out in fights.");
				break;
			case 2:
				Print.println("You think back to the cauldron in "+lastWitchHut.getTown()+" that could help you make a potion- could that help in a fight?");
				break;
			}
		}
	}
	
	private void checkToAdvisePotion() {
		if (adviseStage >0 && Player.player.hasFlask()) {//if they were advised and now have a potion, they did it so we can reset this
			resetAdvising();
			return;
		}
		if (adviseStage == 0) {
			return;//don't advise if we were already advising
		}
		if (!Player.player.hasFlask()) {
			Print.println("You think a potion could help you out in fights.");
			updateAdviseStage(1);
		}
	}
	
	private void updateAdviseStage(int stage) {
		lastAdviseStage = adviseStage;
		adviseStage = stage;
		deathTicker = 0;
	}
	
	private void resetAdvising() {
		adviseStage = 0;
		lastAdviseStage = 0;
		deathTicker = 0;
	}

}
