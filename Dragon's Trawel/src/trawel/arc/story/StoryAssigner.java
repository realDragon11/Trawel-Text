package trawel.arc.story;

import java.util.HashMap;
import java.util.Map;

import trawel.arc.story.arches.StoryHireMurder;
import trawel.arc.story.arches.StoryPotionMaker;
import trawel.personal.classless.Archetype;

public class StoryAssigner {

	private static final Map<Archetype,StoryCreator> storyMap = new HashMap<Archetype, StoryCreator>();
	
	@FunctionalInterface
	private static interface StoryCreator{
		Story makeStory();
	}
	
	static {
		StoryCreator creator;
		
		//example none story
		creator = new StoryCreator() {
			
			@Override
			public Story makeStory() {
				return new StoryNone();
			}
		};
		//put no story into null for use in things that aren't found
		storyMap.put(null,creator);
		
		//real stories
		
		//potion maker story
		creator = new StoryCreator() {
			
			@Override
			public Story makeStory() {
				return new StoryPotionMaker();
			}
		};
		storyMap.put(Archetype.VIRAGO,creator);
		storyMap.put(Archetype.CHEF_ARCH,creator);
		
		//hired for murder story
		creator = new StoryCreator() {
			
			@Override
			public Story makeStory() {
				return new StoryHireMurder();
			}
		};
		storyMap.put(Archetype.HIRED_HATCHET,creator);
		storyMap.put(Archetype.CUT_THROAT,creator);
	}
	
	public static Story getStoryFor(Archetype a) {
		if (!storyMap.containsKey(a)) {
			a = null;//use null instead
		}
		return storyMap.get(a).makeStory();
	}
}
