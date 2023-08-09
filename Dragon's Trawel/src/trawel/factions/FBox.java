package trawel.factions;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.Person;
import trawel.personal.people.Player;

public class FBox implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	//DOLATER: maybe turn into an enum map?
	private List<FSub> factions = new ArrayList<FSub>();
	
	public class FSub implements java.io.Serializable{
		private static final long serialVersionUID = 1L;
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
			Player.player.addMPoints(manTwo.getLevel()/80.0);
			break;
		case DUEL:
			manOne.facRep.addFactionRep(Faction.DUEL,manTwo.getLevel()/10.0f,0);
			break;
		case MONSTER:
			manOne.facRep.addFactionRep(Faction.HUNTER,manTwo.getLevel()/10.0f,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,manTwo.getLevel()/10.0f,0);
			manOne.facRep.addFactionRep(Faction.MERCHANT,manTwo.getLevel()/40.0f,0);
			manOne.facRep.addFactionRep(Faction.FOREST,manTwo.getLevel()/25.0f,0);
			Player.player.addMPoints(manTwo.getLevel()/80.0);
			break;
		case MUG:
			manOne.facRep.addFactionRep(Faction.MERCHANT,manTwo.getLevel()/10.0f,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,manTwo.getLevel()/10.0f,0);
			Player.player.addMPoints(manTwo.getLevel()/20.0);
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
		case LAW:
			manOne.facRep.addFactionRep(Faction.ROGUE, manTwo.getLevel()/25.0f, 0);
			manOne.facRep.addFactionRep(Faction.HEROIC,0,manTwo.getLevel());
			manOne.facRep.addFactionRep(Faction.MERCHANT,0,manTwo.getLevel()/10.0f);
			break;
		case HUNT:
			manOne.facRep.addFactionRep(Faction.HEROIC,0,manTwo.getLevel());
			manOne.facRep.addFactionRep(Faction.HUNTER,0,manTwo.getLevel()*2);
			manOne.facRep.addFactionRep(Faction.MERCHANT,0,manTwo.getLevel()/2);
			break;
		}
		
	}

	public void display() {
		for (FSub s: factions) {
			extra.print(s.name.name + ": " + (int)(s.forFac - s.againstFac) + ",");
		}
		extra.println();
	}
	
	public static float getSpendableFor(FSub sub) {
		if (sub == null) {return 0f;}
		float total = sub.forFac-sub.againstFac;
		FSub sub2 = Player.player.factionSpent.getFacRep(sub.name);
		if (sub2 == null) {
			return total;
		}
		return total-sub2.forFac;
	}
}
