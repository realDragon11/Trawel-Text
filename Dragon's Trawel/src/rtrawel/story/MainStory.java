package rtrawel.story;

import trawel.core.Print;

public class MainStory implements Story {

	@Override
	public void flagAt(String flag, int at) {
		switch (flag) {
		case "new_game":
			if (at == 1) {
				Print.println("You've been hired to clear out the homan well. A large squid has been plaguing the town.");
			}
			break;
		case "homa_unun_boss":
			if (at == 1) {
				Print.println("You should travel back to unun to try to find more party members.");
			}
			break;
		}

	}

}
