package trawel.personal;

import java.util.List;

import trawel.extra;
import trawel.factions.HostileTask;
import trawel.personal.Person.AIJob;
import trawel.personal.Person.PersonType;
import trawel.personal.RaceFactory.CultType;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.DrawBane;

public class NPCMutator {
	
	private static String name_Primal(String type) {
		return extra.choose(", ","the ") +extra.choose("","Primal ")+type+extra.choose("Keeper","Defender","Servant","Judge","Warden");
	}
	
	public static Person primal_Random(Person p) {
		switch (extra.randRange(0,5)) {
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
		p.setTitle(name_Primal(extra.choose("Mountain","Peak","Hill")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Forest(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_FOREST);
		p.setTitle(name_Primal(extra.choose("Forest","Jungle","Woods","Bush")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Breeze(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_BREEZE);
		p.setTitle(name_Primal(extra.choose("Breeze","Wind","Sky","Gale")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Grove(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_GROVE);
		p.setTitle(name_Primal(extra.choose("Grove","Copse","Thicket","Orchard")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Water(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_WATER);
		p.setTitle(name_Primal(extra.choose("Spring","Geyser","Pond","River")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	public static Person primal_Sea(Person p) {
		p.setPerk(Perk.NPC_PRIMAL_SEA);
		p.setTitle(name_Primal(extra.choose("Sea","Ocean","Brine","Tide")));
		p.setPersonType(PersonType.LIFEKEEPER);
		return p;
	}
	
	/**
	 * can be used to promote people to blood cult leaders or in direct creation
	 */
	public static Person cultLeader_Blood(Person p, boolean addDraws) {
		if (addDraws) {
			List<DrawBane> list = p.getBag().getDrawBanes();
			if (extra.chanceIn(1,3)) {
				list.add(DrawBane.BEATING_HEART);
			}else {
				list.add(DrawBane.SINEW);
			}
			list.add(DrawBane.BLOOD);
		}
		p.setTitle(extra.choose("the Blood Queen",", Chosen by The Blood","the Blood Champion"));
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
			if (extra.chanceIn(1,3)) {
				list.add(DrawBane.SINEW);
			}else {
				list.add(DrawBane.BLOOD);
			}
		}
		p.setTitle(extra.choose(", Servant of Blood","the Bloodtender",", Sanguine Servant","the Crimson Cultist","the Bloodguard","the Bloody Believer","the Crimson Convert"));
		p.hTask = HostileTask.CULTIST;
		return p;
	}
	
	
	public static Person cultist_Switch(Person p, CultType type, boolean addDraws) {
		switch (type) {
		case BLOOD:
			return cultist_Blood(p,addDraws);
		}
		throw new RuntimeException("Invalid cult type for NPCMutator: " + type);
	}
	public static Person cultistLeader_Switch(Person p, CultType type, boolean addDraws) {
		switch (type) {
		case BLOOD:
			return cultLeader_Blood(p,addDraws);
		}
		throw new RuntimeException("Invalid cult leader type for NPCMutator: " + type);
	}
}