package trawel.quests.locations;

import java.util.ArrayList;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.helper.methods.extra;
import trawel.quests.types.Quest;
import trawel.towns.contexts.Town;
import trawel.towns.data.Connection;
import trawel.towns.data.Connection.ConnectClass;
import trawel.towns.features.Feature;

/**
 * must be a feature as well
 */
public interface QuestBoardLocation{
	
	public void generateSideQuest();
	public void removeSideQuest(Quest q);
	
	/**
	 * @param maxRange
	 * @return
	 */
	public default List<Feature> getQuestLocationsInRange(int maxRange){
		List<Town> tList = new ArrayList<Town>();
		List<Town> addList = new ArrayList<Town>();
		tList.add(getTown());
		for (int v = 0; v < maxRange;v++) {
			for (Town t: tList) {
				for (Connection c: t.getConnects()) {
					if (c.getType().type == ConnectClass.MAGIC) {
						continue;
					}
					Town f = c.otherTown(t);
					if (!tList.contains(f) && !addList.contains(f)) {
						addList.add(f);
					}
				}
			}
			tList.addAll(addList);
			addList.clear();
		}
		List<Feature> retList = new ArrayList<Feature>();
		for (Town t: tList) {
			for (Feature f: t.getFeatures()){
				if (f.getQRType() != Feature.QRType.NONE) {
					retList.add(f);
				}
			}
		}
		if (retList.size() > 1) {
			retList.remove(this);//if we can afford to, remove this (since we MUST be a Feature, this will work)
		}
		return retList;
	}
	/**
	 * by default this is uniform across all other locations
	 * features can choose to use their own method to distribute the quest targets
	 * <br>
	 * note that the range might not be respected in such a case, it is merely a suggestion on a max
	 * <br>
	 * Feature implements it's own roller, so this would have to be called with super
	 * @return
	 */
	public default Feature getQuestGoal(){
		return extra.randList(getQuestLocationsInRange(5));
	}
	
	public Town getTown();
	
	/**
	 * note that they must be regenerated to properly account for world changes
	 * <br>
	 * but in most cases only regen-ing on game load is probably fine
	 */
	public class FeatureRoller {
		private final List<Feature> goals;
		private final WeightedTable table;
		public FeatureRoller(QuestBoardLocation location) {
			List<Float> tempFloats = new ArrayList<Float>();
			goals = new ArrayList<Feature>();
			
			List<Town> openSet = new ArrayList<Town>();
			List<Town> exploreList = new ArrayList<Town>();
			List<Town> closedList = new ArrayList<Town>();
			exploreList.add(location.getTown());
			float fallOff;
			for (int v = 0; v < 5;v++) {
				switch (v) {
					default:
						fallOff = 1f;//ideal range
						break;
					case 1: case 4:
						fallOff = 0.5f;//less common
						break;
					case 0: case 5:
						fallOff = .1f;//rarer at edges and in same town
						break;
				}
				openSet.addAll(exploreList);
				exploreList.clear();
				while (!openSet.isEmpty()) {
					Town t = openSet.remove(openSet.size()-1);
					closedList.add(t);
					for (Connection c: t.getConnects()) {//could not explore on last step to save some time
						if (c.getType().type == ConnectClass.MAGIC) {
							continue;
						}
						Town f = c.otherTown(t);
						if (!exploreList.contains(f) && !closedList.contains(f)) {
							exploreList.add(f);//save to explore later
						}
					}
					for (Feature f: t.getFeatures()) {
						//should never examine the same town more than once so don't need to check per feature
						if (f.getQRType() != Feature.QRType.NONE) {
							goals.add(f);
							tempFloats.add(fallOff*f.getQRType().targetMult);
						}
					}
				}
			}
			float[] floats = new float[tempFloats.size()];
			for (int i = 0; i < floats.length;i++) {
				floats[i] = tempFloats.get(i);
			}
			table = new WeightedTable(floats);
		}
		
		/**
		 * can return null if rolls an unusable feature twice
		 */
		public Feature roll() {
			Feature f = goals.get(table.random(extra.getRand()));
			if (f.getReplaced() == f) {
				//try a reroll, but only one
				f = goals.get(table.random(extra.getRand()));
				if (f.getReplaced() == f) {
					return null;
				}
			}
			return f;
		}
	}
}
