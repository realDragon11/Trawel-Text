package trawel.factions;

import java.util.ArrayList;
import java.util.List;

import trawel.Person;

public class FBox {

	
	private List<FSub> factions = new ArrayList<FSub>();
	
	public class FSub{
		public Faction name;
		public float forFac;
		public float againstFac;
		
		public FSub(Faction name, float fora, float aga) {
			this.name = name;
			this.forFac = fora;
			this.againstFac = aga;
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param fora
	 * @param aga
	 * @return if added new faction to list
	 */
	public boolean addFactionRep(Faction name, float fora, float aga) {
		for (FSub f: factions) {
			if (f.name == name) {
				f.forFac += fora;
				f.againstFac +=aga;
				return false;
			}
		}
		factions.add(new FSub(name,fora,aga));
		return true;
	}
	
	public FSub getFacRep(Faction name){
		for (FSub f: factions) {
			if (f.name == name) {
				return f;
			}
		}
		return null;
	}
	
	public static void repCalc(Person manOne, Person manTwo) {
		switch (manTwo.hTask) {
		case ANIMAL:
			manOne.facRep.addFactionRep(Faction.HUNTER,manTwo.getLevel()/25.0f,0);
			manOne.facRep.addFactionRep(Faction.MERCHANT,manTwo.getLevel()/40.0f,0);
			manOne.facRep.addFactionRep(Faction.FOREST,0,manTwo.getLevel()/25.0f);
			break;
		case DUEL:
			manOne.facRep.addFactionRep(Faction.DUEL,manTwo.getLevel()/10.0f,0);
			break;
		case MONSTER:
			manOne.facRep.addFactionRep(Faction.HUNTER,manTwo.getLevel()/10.0f,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,manTwo.getLevel()/10.0f,0);
			manOne.facRep.addFactionRep(Faction.MERCHANT,manTwo.getLevel()/40.0f,0);
			manOne.facRep.addFactionRep(Faction.FOREST,manTwo.getLevel()/25.0f,0);
			break;
		case MUG:
			manOne.facRep.addFactionRep(Faction.MERCHANT,manTwo.getLevel()/10.0f,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,manTwo.getLevel()/10.0f,0);
			break;
		case PEACE:
			manOne.facRep.addFactionRep(Faction.HEROIC,0,manTwo.getLevel());
			break;
		case RACIST:
			break;
		case RICH:
			manOne.facRep.addFactionRep(Faction.MERCHANT,0,manTwo.getLevel()/5.0f);
			break;
		case BOSS:
			manOne.facRep.addFactionRep(Faction.HUNTER,manTwo.getLevel()/10.0f,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,manTwo.getLevel(),0);
			break;
		case GUARD_DUNGEON:
			manOne.facRep.addFactionRep(Faction.HEROIC,manTwo.getLevel()/15.0f,0);
			break;
		case LUMBER:
			manOne.facRep.addFactionRep(Faction.FOREST,manTwo.getLevel()/5.0f,0);
			break;
		case REVENGE:
			break;
		}
		
	}
}
