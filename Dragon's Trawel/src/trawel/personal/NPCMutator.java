package trawel.personal;

import java.util.List;

import trawel.core.Rand;
import trawel.factions.HostileTask;
import trawel.helper.methods.randomLists;
import trawel.personal.Person.AIJob;
import trawel.personal.Person.PersonFlag;
import trawel.personal.Person.PersonType;
import trawel.personal.RaceFactory.CultType;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.DrawBane;

public class NPCMutator {
	
	private static String name_Primal(String type) {
		return Rand.choose(", ","the ") +Rand.choose("","Primal ")+type+Rand.choose("Keeper","Defender","Servant","Judge","Warden");
	}
	
	public static Person primal_Random(Person p) {
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
	

}
