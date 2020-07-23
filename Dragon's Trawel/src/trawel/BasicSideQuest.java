package trawel;

import java.util.ArrayList;

public abstract class BasicSideQuest implements Quest{

	public QuestR giver, target;
	
	public String name, desc;
	
	public void cleanup() {
		giver.cleanup();
		target.cleanup();
	}
	
	public void fail() {
		cleanup();
	}
	
	public void complete() {
		cleanup();
	}
	
	public static BasicSideQuest getRandomSideQuest() {
		
		switch (extra.randRange(1,3)) {
		case 1: //fetch quest
			
			
			break;
		
		}
		return null;
	}
}

