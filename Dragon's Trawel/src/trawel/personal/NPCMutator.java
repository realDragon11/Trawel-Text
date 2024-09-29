package trawel.personal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trawel.core.Rand;
import trawel.factions.HostileTask;
import trawel.helper.methods.randomLists;
import trawel.personal.Person.AIJob;
import trawel.personal.Person.PersonFlag;
import trawel.personal.Person.PersonType;
import trawel.personal.RaceFactory.CultType;
import trawel.personal.classless.Feat;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.DrawBane;

public class NPCMutator {
	
	private static String name_Primal(String type) {
		return Rand.choose(", ","the ") +Rand.choose("","Primal ")+type+Rand.choose("Keeper","Defender","Servant","Judge","Warden");
	}
	
	public static Person primal_Random(Person p) {
		//TODO: make detect if already has the type they are being changed to, and add another, as well as a fallback
		switch (Rand.randRange(0,5)) {
		case 0: default:
			return primal_Mountain(p);
		case 1:
			return primal_Forest(p);
		case 2:
			return primal_Breeze(p);
		case 3:
			return primal_Grove(p);
		case 4:
			return primal_Water(p);
		case 5:
			return primal_Sea(p);
		}
	}

	public static Person primal_Mountain(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_MOUNTAIN);
		p.setTitle(name_Primal(Rand.choose("Mountain","Peak","Hill")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Forest(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_FOREST);
		p.setTitle(name_Primal(Rand.choose("Forest","Jungle","Woods","Bush")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Breeze(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_BREEZE);
		p.setTitle(name_Primal(Rand.choose("Breeze","Wind","Sky","Gale")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Grove(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_GROVE);
		p.setTitle(name_Primal(Rand.choose("Grove","Copse","Thicket","Orchard")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Water(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_WATER);
		p.setTitle(name_Primal(Rand.choose("Spring","Geyser","Pond","River")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Sea(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_SEA);
		p.setTitle(name_Primal(Rand.choose("Sea","Ocean","Brine","Tide")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	
	/**
	 * can be used to promote people to blood cult leaders or in direct creation
	 */
	public static Person cultLeader_Blood(Person p, boolean addDraws) {
		if (addDraws) {
			List<DrawBane> list = p.getBag().getDrawBanes();
			if (Rand.chanceIn(1,3)) {
				list.add(DrawBane.BEATING_HEART);
			}else {
				list.add(DrawBane.SINEW);
			}
			list.add(DrawBane.BLOOD);
		}
		p.setTitle(Rand.choose("the Blood Queen",", Chosen by The Blood","the Blood Champion"));
		p.setPerk(Perk.CULT_CHOSEN_BLOOD);
		p.setPerk(Perk.CULT_LEADER);
		p.hTask = HostileTask.CULTIST;
		return p;
	}
	
	/**
	 * can be used to make people culty or in direct creation
	 */
	public static Person cultist_Blood(Person p, boolean addDraws) {
		if (addDraws) {
			List<DrawBane> list = p.getBag().getDrawBanes();
			if (Rand.chanceIn(1,3)) {
				list.add(DrawBane.SINEW);
			}else {
				list.add(DrawBane.BLOOD);
			}
		}
		p.setTitle(Rand.choose(", Servant of Blood","the Bloodtender",", Sanguine Servant","the Crimson Cultist","the Bloodguard","the Bloody Believer","the Crimson Convert"));
		p.hTask = HostileTask.CULTIST;
		return p;
	}
	
	/**
	 * can be used to promote people to sky cult leaders or in direct creation
	 */
	public static Person cultLeader_Sky(Person p, boolean addDraws) {
		if (addDraws) {
			List<DrawBane> list = p.getBag().getDrawBanes();
			if (Rand.chanceIn(1,3)) {
				list.add(DrawBane.LIVING_FLAME);
			}else {
				list.add(DrawBane.UNICORN_HORN);
			}
			list.add(DrawBane.TELESCOPE);
		}
		p.setTitle(Rand.choose("the Sky Queen",", Chosen by The Sky","the Sky Champion"));
		p.setPerk(Perk.CULT_CHOSEN_SKY);
		p.setPerk(Perk.CULT_LEADER);
		p.hTask = HostileTask.CULTIST;
		return p;
	}
	
	/**
	 * can be used to make people culty or in direct creation
	 */
	public static Person cultist_Sky(Person p, boolean addDraws) {
		if (addDraws) {
			List<DrawBane> list = p.getBag().getDrawBanes();
			if (Rand.chanceIn(1,3)) {
				list.add(DrawBane.BAT_WING);
			}
		}
		p.setTitle(Rand.choose(", Servant of Sky","the Skywatcher",", Sky Servant","the Cloud Cultist","the Cloudguard","the Beyond Believer","the Cosmos Convert"));
		p.hTask = HostileTask.CULTIST;
		return p;
	}
	
	
	public static Person cultist_Switch(Person p, CultType type, boolean addDraws) {
		switch (type) {
		case BLOOD:
			return cultist_Blood(p,addDraws);
		case SKY:
			return cultist_Sky(p, addDraws);
		}
		throw new RuntimeException("Invalid cult type for NPCMutator: " + type);
	}
	public static Person cultistLeader_Switch(Person p, CultType type, boolean addDraws) {
		switch (type) {
		case BLOOD:
			return cultLeader_Blood(p,addDraws);
		case SKY:
			return cultLeader_Sky(p, addDraws);
		}
		throw new RuntimeException("Invalid cult leader type for NPCMutator: " + type);
	}
	
	public static Person mutateImproveGear(Person p,int amount) {
		p.bag.getSolids().forEach(item -> {
			item.temperNegQuality(amount);
			item.improvePosQuality(amount);
		});
		return p;
	}
	
	/**
	 * actual bosses should use their own RaceFactory methods, using other mutators as needed
	 */
	public static Person mutateMiniboss(Person p,boolean addDraws,boolean improveEquips) {
		p.setFlag(PersonFlag.IS_MOOK,false);
		if (addDraws) {
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
		}
		if (improveEquips) {
			mutateImproveGear(p,1);
		}
		p.setPerk(Perk.NPC_PROMOTED);
		p.clearEffects();//cure effects like curse
		p.hTask = HostileTask.BOSS;
		return p;
	}
	
	/**
	 * should only be used on a stock drudger which already has no honorific
	 * @param p
	 * @return
	 */
	public static Person mutateHonorStockDrudger(Person p) {
		p.setPerk(Perk.NPC_PROMOTED);
		p.clearEffects();//cure effects like curse
		p.setFlag(PersonFlag.IS_MOOK, false);
		p.setFirstName(randomLists.honorDrudgerName(p.getNameNoTitle()));
		return p;
	}
	
	/**
	 * defaults to no feat, however drawbanes that aren't listed in this can't be added
	 */
	private static final Map<DrawBane,Feat> harpyAddFeats = new HashMap<DrawBane,Feat>();
	/**
	 * defaults to a weight of 1
	 */
	private static final Map<DrawBane,Double> harpyAddWeights = new HashMap<DrawBane,Double>();
	
	static {
		harpyAddFeats.put(DrawBane.WOOD,Feat.NOT_PICKY);
		harpyAddWeights.put(DrawBane.WOOD,2d);
		harpyAddFeats.put(DrawBane.CLOTH,Feat.NOT_PICKY);
		harpyAddWeights.put(DrawBane.CLOTH,2d);
		
		harpyAddFeats.put(DrawBane.APPLE,Feat.GLUTTON);
		harpyAddFeats.put(DrawBane.PUMPKIN,Feat.GLUTTON);
		harpyAddFeats.put(DrawBane.EGGCORN,Feat.GLUTTON);
		harpyAddWeights.put(DrawBane.EGGCORN,0.5d);
		harpyAddFeats.put(DrawBane.MEAT,Feat.GLUTTON);
		harpyAddFeats.put(DrawBane.TRUFFLE,Feat.GLUTTON);
		harpyAddWeights.put(DrawBane.TRUFFLE,0.2d);
		harpyAddFeats.put(DrawBane.HONEY,Feat.GLUTTON);
		harpyAddWeights.put(DrawBane.HONEY,0.5d);
		harpyAddFeats.put(DrawBane.GARLIC,Feat.GLUTTON);
		harpyAddWeights.put(DrawBane.GARLIC,0.5d);
		
		harpyAddFeats.put(DrawBane.BLOOD,Feat.HEMOVORE);
		
		harpyAddFeats.put(DrawBane.PROTECTIVE_WARD,Feat.SHAMAN);
		harpyAddWeights.put(DrawBane.PROTECTIVE_WARD,0.1d);
		harpyAddFeats.put(DrawBane.UNICORN_HORN,Feat.SHAMAN);
		harpyAddWeights.put(DrawBane.UNICORN_HORN,0.1d);
		
		harpyAddFeats.put(DrawBane.BAT_WING,Feat.SWIFT);
		harpyAddFeats.put(DrawBane.MIMIC_GUTS,Feat.ARMORPAINTER);
		harpyAddFeats.put(DrawBane.TELESCOPE,Feat.UNDERHANDED);
		
		harpyAddFeats.put(DrawBane.GOLD,null);
		harpyAddWeights.put(DrawBane.GOLD,0.2d);
		harpyAddFeats.put(DrawBane.SILVER,null);
		harpyAddWeights.put(DrawBane.SILVER,0.5d);
		
		harpyAddFeats.put(DrawBane.CEON_STONE,Feat.UNBREAKABLE);
		harpyAddWeights.put(DrawBane.CEON_STONE,0.1d);
		
		
		
		harpyAddFeats.put(DrawBane.WAX,Feat.COCOONED);
		
		harpyAddFeats.put(DrawBane.GRAVE_DIRT,Feat.WITCHY);
		harpyAddWeights.put(DrawBane.GRAVE_DIRT,0.5d);
		harpyAddFeats.put(DrawBane.GRAVE_DUST,Feat.WITCHY);
		harpyAddWeights.put(DrawBane.GRAVE_DUST,0.3d);
		
		harpyAddFeats.put(DrawBane.REPEL,Feat.AMBUSHER);
		harpyAddWeights.put(DrawBane.REPEL,0.5d);
	}
	
	public static boolean mutateAddFindHarpy(Person p) {
		List<DrawBane> finds = new ArrayList<DrawBane>();
		List<Double> weights = new ArrayList<Double>();
		List<DrawBane> has = p.getBag().getDrawBanes();
		double totalWeight = 0;
		for (DrawBane d: harpyAddFeats.keySet()) {
			//can only add drawbanes they don't have already
			if (!has.contains(d)) {
				finds.add(d);
				double weight = harpyAddWeights.getOrDefault(d,1d);
				weights.add(weight);
				totalWeight+=weight;
			}
		}
		if (finds.isEmpty()) {
			//return false because there's nothing else to add to their inventory
			return false;
		}
		//roll a weight
		totalWeight *= Rand.getRand().nextDouble();
		DrawBane add;
		for (int i = weights.size()-1; true; i--) {
			totalWeight -= weights.get(i);
			//if this is the last case (rounding error) or we rolled this add, set it
			if (totalWeight <= 0 || i == 0) {
				add = finds.get(i);
				break;
			}
		}
		assert add != null;
		p.getBag().addDrawBaneSilently(add);
		Feat feat = harpyAddFeats.getOrDefault(add,null);//get the feat allocated with the drawbane
		if (feat == null) {
			//if there is no assigned feat, add a feat point instead
			p.addFeatPoint();
			p.finishGeneration();
		}else {
			if (p.hasFeat(feat)) {//if they already have the feat, also add a feat point
				p.addFeatPoint();
				p.finishGeneration();
			}else {
				//otherwise set the feat
				p.setFeat(feat);
			}
		}
		//skills already updated so don't need to call the function for that
		return true;
	}
	

}
