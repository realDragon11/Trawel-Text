package trawel.helper.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.yellowstonegames.core.WeightedTable;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.personal.AIClass;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.Armor;
import trawel.personal.item.solid.Gem;
import trawel.personal.item.solid.MaterialFactory;
import trawel.personal.item.solid.Weapon;
import trawel.personal.item.solid.Weapon.WeaponType;
import trawel.personal.people.Player;
import trawel.towns.contexts.World;

public class LootTables {
	private static final Map<LootType,WeightedTable> tables = new HashMap<LootType,WeightedTable>();
	private static final List<LootEvent> events = new ArrayList<LootTables.LootEvent>();
	/**
	 * used for assembly only
	 */
	private static Map<LootType,List<Float>> weights;
	
	public static void initLootTables(){
		//create lists to store the floats until we can finalize the chances with a table
		weights = new HashMap<LootType,List<Float>>();
		for (LootType lt: LootType.values()) {
			weights.put(lt,new ArrayList<Float>());
		}
		//clear events table in case this is run more than once
		events.clear();
		
		//add events
		//add the chances before registering the event
		
		//basic pile of money and aether
		addChance(LootType.BEACH_CHEST,1f);
		addChance(LootType.GRAVEYARD_COFFIN,1f);
		addChance(LootType.UNLOCKED_DUNGEON_CHEST,3f);
		registerEvent(new LootEvent() {

			@Override
			public void loot(int level, LootTheme theme) {
				int moneyReward = IEffectiveLevel.cleanRangeReward(level,RaceFactory.WEALTH_HIGH,.7f);
				int aetherReward = IEffectiveLevel.cleanRangeReward(level,1000,.5f);
				Player.bag.addAether(aetherReward);
				Player.player.addGold(moneyReward);
				Print.println("[r_good]You find "+World.currentMoneyDisplay(moneyReward) + " and "+aetherReward + " Aether!");
			}});
		
		//hunter stash with silver weapon
		addChance(LootType.BEACH_CHEST,0.5f);
		addChance(LootType.GRAVEYARD_COFFIN,2f);
		registerEvent(new LootEvent() {
			
			@Override
			public void loot(int level, LootTheme theme) {
				Weapon silvered = new Weapon(level,MaterialFactory.getMat("silver"),Rand.choose(WeaponType.MACE,WeaponType.LONGSWORD,WeaponType.BROADSWORD,WeaponType.SPEAR));
				int amberAmount = IEffectiveLevel.cleanRangeReward(level,Gem.AMBER.reward(2f,theme.gem == Gem.AMBER), .5f);
				Gem.AMBER.changeGem(amberAmount);
				Print.println("[r_good]You find a Hunter's cache with " + amberAmount + " Amber and a "+silvered.getName()+"!");
				AIClass.playerFindItem(silvered);
			}
		});
		
		//misc gem stash
		addChance(LootType.BEACH_CHEST,2f);
		addChance(LootType.GRAVEYARD_COFFIN,1f);
		addChance(LootType.UNLOCKED_DUNGEON_CHEST,3f);
		registerEvent(new LootEvent() {
			
			@Override
			public void loot(int level, LootTheme theme) {
				int emeraldAmount = IEffectiveLevel.cleanRangeReward(level,Gem.EMERALD.reward(1f,theme.gem == Gem.EMERALD), .5f);
				int rubyAmount = IEffectiveLevel.cleanRangeReward(level,Gem.RUBY.reward(1f,theme.gem == Gem.RUBY), .5f);
				//higher amount because skill based action
				int sapphireAmount = IEffectiveLevel.cleanRangeReward(level,Gem.SAPPHIRE.reward(1f,theme.gem == Gem.SAPPHIRE), .5f);
				Gem.EMERALD.changeGem(emeraldAmount);
				Gem.RUBY.changeGem(rubyAmount);
				Gem.SAPPHIRE.changeGem(sapphireAmount);
				Print.println("[r_good]You find a Gem cache with " + emeraldAmount + " Emeralds, "+rubyAmount + " Rubies, and " + sapphireAmount + " Sapphires!");
			}
		});
		
		//enchanted equipment soaked in aether
		addChance(LootType.BEACH_CHEST,.5f);
		addChance(LootType.GRAVEYARD_COFFIN,1f);
		addChance(LootType.UNLOCKED_DUNGEON_CHEST,.5f);
		registerEvent(new LootEvent() {
			
			@Override
			public void loot(int level, LootTheme theme) {
				int aetherReward = IEffectiveLevel.cleanRangeReward(level,1000,.5f);
				Player.bag.addAether(aetherReward);
				Print.println("You find some armor soaking in "+aetherReward+" Aether!");
				for (int i = 0; i < 3;i++) {
					Armor a = new Armor(level);
					//try to enchant it or improve the enchantment if it already has one- won't try to enchant unenchantable stuff but it's fine to have that as loot
					a.improveEnchantChance(a.getLevel());
					AIClass.playerFindItem(a);
				}
			}
		});
		
		//finished adding, can finalize tables
		tables.clear();
		for (LootType lt: LootType.values()) {
			//can't figure out a way to cast List<Float> to float[] (multiple combining issues) so just doing it manually (I have to do this a lot tbh)
			float[] w = new float[events.size()];
			List<Float> l = weights.get(lt);
			for (int i = 0; i < w.length;i++) {
				w[i] = l.get(i);//autocast from Float to float
			}
			tables.put(lt,new WeightedTable(w));
		}
	}
	
	private static void registerEvent(LootEvent event) {
		events.add(event);
		int len = events.size();
		for (List<Float> wl: weights.values()) {
			if (wl.size() < len) {//if didn't have a chance assigned
				wl.add(0f);//add no chance to spawn it
			}
		}
	}
	
	private static void addChance(LootType lt, Float chance) {
		weights.get(lt).add(chance);
	}
	
	@FunctionalInterface
	private interface LootEvent{
		public void loot(int level, LootTheme theme);
	}
	
	public enum LootType{
		BEACH_CHEST, GRAVEYARD_COFFIN, UNLOCKED_DUNGEON_CHEST;
	}
	public enum LootTheme{
		/**
		 * use attribute contest skill actions
		 */
		SKILLED(Gem.SAPPHIRE),
		/**
		 * explore/harvest
		 */
		EXPLORE(Gem.EMERALD),
		/**
		 * boss/fight
		 */
		BOSS(Gem.RUBY),
		/**
		 * hunt/fight
		 */
		HUNT(Gem.AMBER);
		public final Gem gem;
		private LootTheme(Gem _gem) {
			gem = _gem;
		}
	}
	
	public static void doLoot(int level, LootType type, LootTheme theme) {
		//from the event list, get a random index weighted by the type, and then loot at the certain level and theme
		events.get(tables.get(type).random(Rand.getRand())).loot(level, theme);;
	}
}
