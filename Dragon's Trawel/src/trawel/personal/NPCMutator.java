package trawel.personal;

import trawel.extra;
import trawel.personal.Person.PersonType;
import trawel.personal.classless.Perk;

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
}
