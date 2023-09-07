package trawel.towns.misc;
import java.util.ArrayList;
import java.util.List;

import com.github.yellowstonegames.core.WeightedTable;

import derg.menus.MenuBack;
import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import trawel.Networking;
import trawel.Networking.Area;
import trawel.extra;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;
import trawel.towns.Feature;
import trawel.towns.Town;

public class Garden extends Feature {
	
	private static String[] fills = new String[] {
			"",
			"apple seed",
			"apple tree",
			"garlic seed",
			"garlic",
			"pumpkin seed",
			"pumpkin patch",
			"eggcorn seed",
			"eggcorn",
			"bee larva",
			"bee hive",
			"truffle spores",
			"truffle",
			"exhausted apple tree",
			"empty pumpkin patch",
			"angry bee hive",
			"ent sapling",
			"ent",
			"fairy dust",
			"unicorn horn"
			};
	
	public enum PlantFill{
		NONE(new float[] {1f}),//100% chance of empty
		FOOD(new float[] {
				//""
				.5f,
				//"apple seed"
				2f,
				//"apple tree"
				1f,
				//"garlic seed"
				2f,
				//"garlic"
				1f,
				//"pumpkin seed"
				2f,
				//"pumpkin patch"
				1f,
				//"eggcorn seed"
				2f,
				//"eggcorn"
				1f,
				//"bee larva"
				.5f,
				//"bee hive"
				1f,
				//"truffle spores"
				.5f,
				//"truffle"
				.25f,
				//"exhausted apple tree"
				1f,
				//"empty pumpkin patch"
				1f,
				//"angry bee hive"
				1f,
				//"ent sapling"
				0f,
				//"ent"
				0f,
				//"fairy dust"
				0f,
				//"unicorn horn"
				0f
		}),
		WITCH(new float[] {
				//""
				.5f,
				//"apple seed"
				1f,
				//"apple tree"
				1f,
				//"garlic seed"
				3f,
				//"garlic"
				2f,
				//"pumpkin seed"
				2f,
				//"pumpkin patch"
				2f,
				//"eggcorn seed"
				0f,
				//"eggcorn"
				0f,
				//"bee larva"
				1f,
				//"bee hive"
				3f,
				//"truffle spores"
				1f,
				//"truffle"
				1f,
				//"exhausted apple tree"
				1f,
				//"empty pumpkin patch"
				1f,
				//"angry bee hive"
				2f,
				//"ent sapling"
				.1f,
				//"ent"
				.2f,
				//"fairy dust"
				.5f,
				//"unicorn horn"
				.1f
		}),
		BAD_HARVEST(new float[] {
				//""
				4f,
				//"apple seed"
				1f,
				//"apple tree"
				.1f,
				//"garlic seed"
				1f,
				//"garlic"
				.1f,
				//"pumpkin seed"
				1f,
				//"pumpkin patch"
				.1f,
				//"eggcorn seed"
				1f,
				//"eggcorn"
				.5f,
				//"bee larva"
				0f,
				//"bee hive"
				0f,
				//"truffle spores"
				0f,
				//"truffle"
				0f,
				//"exhausted apple tree"
				1f,
				//"empty pumpkin patch"
				1f,
				//"angry bee hive"
				0f,
				//"ent sapling"
				0f,
				//"ent"
				0f,
				//"fairy dust"
				0f,
				//"unicorn horn"
				0f
		});
		
		private final WeightedTable roller;
		PlantFill(float[] weights){
			roller = new WeightedTable(weights);
		}
		
		public String roll() {
			return fills[roller.random(extra.getRand())];
		}
	}

	private static final long serialVersionUID = 1L;
	private ArrayList<PlantSpot> plants = new ArrayList<PlantSpot>();
	private float growMult;
	private double reTimer = 0;
	private PlantFill filler;
	public Garden(Town town,String _name, float _growMult, PlantFill defaultFill) {
		filler = defaultFill;
		this.town = town;
		this.tier = town.getTier();
		if (_growMult == 0) {
			growMult = (1f+(tier/100f));
		}else {
			growMult = _growMult;
		}
		tier = town.getTier();
		name = _name;
		tutorialText = "Garden.";
		for (int i = 0; i < 6;i++) {
			plants.add(new PlantSpot(tier));
			plants.get(i).contains = defaultFill.roll();
			//delay starting growth randomly
			plants.get(i).timer = -(20+(50*extra.randFloat()));
		}
		background_area = "forest";
		background_variant = 1;
		area_type = Area.GARDEN;
	}
	
	@Override
	public String getColor() {
		return extra.F_SERVICE;
	}

	@Override
	public void go() {
		Networking.sendStrong("Discord|imagesmall|garden|Garden|");
		extra.menuGo(new MenuGenerator() {
			@Override
			public List<MenuItem> gen() {
				List<MenuItem> list = new ArrayList<MenuItem>();
				for (PlantSpot ps: plants) {
					list.add(ps.getMenuForGarden());
				}
				list.add(new MenuBack("leave"));
				return list;
			}
		});
	}

	@Override
	public List<TimeEvent> passTime(double time, TimeContext calling) {
		if (filler != PlantFill.NONE) {
			reTimer += time;
			if (reTimer > 120) {
				for (PlantSpot p: plants) {
					if (p.contains == "" && extra.chanceIn(1,3)) {
						p.contains = filler.roll();
						p.timer = -(50+(100*extra.randFloat()));
					}
				}
				reTimer = 0;
			}
		}
		
		time *= growMult;
		for (PlantSpot p: plants) {
			p.passTime(time,calling);
		}
		return null;
	}

}
