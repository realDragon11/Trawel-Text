package trawel.arc.story.arches;

import trawel.arc.story.Story;
import trawel.core.Print;
import trawel.towns.features.Feature;
import trawel.towns.features.multi.Inn;
import trawel.towns.features.multi.Slum;
import trawel.towns.features.services.guilds.MerchantGuild;
import trawel.towns.features.services.guilds.RogueGuild;

public class StoryHireMurder extends Story {
	
	/**
	 * 0 = no advising
	 * 1 = advised to make potion
	 * 2 = advised inside questboard
	 */
	private int adviseStage = 0, lastAdviseStage = 0;
	
	private int deathTicker = 0;

	@Override
	public void storyStart() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void enterFeature(Feature f) {
		if (adviseStage == 1 && (f instanceof Slum || f instanceof MerchantGuild || f instanceof RogueGuild || f instanceof Inn)) {
			updateAdviseStage(2);
			Print.println("A quest board! Maybe they have work for someone of your... particular talents.");
			return;//skip rest of checks
		}
		checkToAdviseMurder();
	}
	
	private void checkToAdviseMurder() {
		//TODO
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
