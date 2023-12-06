package trawel.factions;

import java.util.ArrayList;
import java.util.List;

import trawel.extra;
import trawel.personal.Person;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Player;

public class FBox implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	//TODO: turn into an enum map of float[2] to avoid a lot of object overhead
	private List<FSub> factions = new ArrayList<FSub>();
	
	//if don't put in a tri/duo enum map, put FSub in FST written classes
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
	
	public static final float againstClose = 1.0f/5f;
	public static final float againstNear = 1.0f/10f;
	public static final float againstDistant = 1.0f/20f;
	public static final float bonusFavored = 1.0f/3f;
	public static final float bonusLiked = 1.0f/6f;
	public static final float bonusDistant = 1.0f/12f;
	public static final float bonusTiny = 1.0f/24f;
	
	
	public static void repCalc(Person manOne, Person manTwo) {
		float iLevel = manTwo.getUnEffectiveLevel();
		switch (manTwo.hTask) {
		case ANIMAL:
			manOne.facRep.addFactionRep(Faction.HUNTER,iLevel*bonusTiny,0);
			manOne.facRep.addFactionRep(Faction.MERCHANT,iLevel*bonusTiny,0);
			manOne.facRep.addFactionRep(Faction.FOREST,0,iLevel*againstNear);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,iLevel*bonusTiny,0);
			Player.player.addMPoints(iLevel/80.0);
			break;
		case DUEL:
			manOne.facRep.addFactionRep(Faction.DUEL,iLevel*bonusLiked,0);
			break;
		case MONSTER:
			manOne.facRep.addFactionRep(Faction.HUNTER,iLevel*bonusFavored,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,iLevel*bonusFavored,0);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,iLevel*bonusLiked,0);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,iLevel*bonusLiked,0);
			manOne.facRep.addFactionRep(Faction.MERCHANT,iLevel*bonusDistant,0);
			manOne.facRep.addFactionRep(Faction.FOREST,iLevel*bonusLiked,0);
			Player.player.addMPoints(iLevel/80.0);
			break;
		case MUG:
			manOne.facRep.addFactionRep(Faction.MERCHANT,iLevel*bonusLiked,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,iLevel*bonusLiked,0);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,iLevel*bonusFavored,0);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,iLevel*bonusFavored,0);
			Player.player.addMPoints(iLevel/20.0);
			break;
		case PEACE:
			manOne.facRep.addFactionRep(Faction.HEROIC,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,0,iLevel*againstClose);
			break;
		case RACIST:
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,0,iLevel*againstClose);
			break;
		case RICH:
			manOne.facRep.addFactionRep(Faction.MERCHANT,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,0,iLevel*againstClose);
			break;
		case BOSS:
			manOne.facRep.addFactionRep(Faction.HUNTER,iLevel*bonusLiked,0);
			manOne.facRep.addFactionRep(Faction.HEROIC,iLevel*bonusFavored,0);
			break;
		case GUARD_DUNGEON://Neutral dungeon
			manOne.facRep.addFactionRep(Faction.HEROIC,iLevel*bonusFavored,0);
			break;
		case LAWLESS_NODE_GUARDS:
			manOne.facRep.addFactionRep(Faction.HEROIC,iLevel*bonusFavored,0);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,iLevel*bonusFavored,0);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,iLevel*bonusFavored,0);
			break;
		case LUMBER:
			manOne.facRep.addFactionRep(Faction.FOREST,iLevel*bonusFavored,0);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,0,iLevel*againstClose);
			break;
		case REVENGE:
			manOne.facRep.addFactionRep(Faction.DUEL,iLevel*bonusFavored,0);
			break;
		case LAW_GOOD:
			manOne.facRep.addFactionRep(Faction.ROGUE, iLevel*bonusLiked, 0);
			manOne.facRep.addFactionRep(Faction.HEROIC,0,iLevel*againstNear);
			manOne.facRep.addFactionRep(Faction.MERCHANT,0,iLevel*againstNear);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,0,iLevel*againstClose);
			break;
		case LAW_EVIL:
			manOne.facRep.addFactionRep(Faction.ROGUE, iLevel*bonusFavored, 0);
			manOne.facRep.addFactionRep(Faction.HEROIC,0,iLevel*againstDistant);
			manOne.facRep.addFactionRep(Faction.MERCHANT,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,0,iLevel*againstClose);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,0,iLevel*againstNear);
			break;
		case HUNT:
			manOne.facRep.addFactionRep(Faction.HEROIC,0,iLevel*againstNear);
			manOne.facRep.addFactionRep(Faction.HUNTER,0,iLevel*againstNear);
			manOne.facRep.addFactionRep(Faction.MERCHANT,0,iLevel*againstNear);
			manOne.facRep.addFactionRep(Faction.LAW_EVIL,0,iLevel*againstNear);
			manOne.facRep.addFactionRep(Faction.LAW_GOOD,0,iLevel*againstNear);
			break;
		}
		
	}
	
	/**
	 * will return 0 for neutral, negative numbers for disagreeing (attack on <-1) and positive numbers for liking
	 * <br>
	 * positive reactions tend to beat out negative ones, so for example high rogue rep + high law rep -> rogues still like you
	 * <br>
	 * also uses hostile tasks
	 */
	public int getReactionAgainst(Person us, Person them) {
		FBox other = them.facRep;
		HostileTask ourTask = us.hTask;
		HostileTask theirTask = them.hTask;
		if (theirTask == HostileTask.MONSTER && ourTask != HostileTask.MONSTER) {
			return -2;
		}
		if (ourTask == HostileTask.MONSTER && theirTask != HostileTask.MONSTER) {
			return -2;
		}
		float[][] ourAligns = getAlignment();
		float[][] theirAligns = other.getAlignment();
		switch (ourTask) {
		case DUEL:
			if (theirTask == HostileTask.DUEL) {
				return -2;
			}
			break;
		case HUNT:
			switch (theirTask) {
			case ANIMAL:
				return -2;
			case MONSTER:
				return -4;
			}
			break;
		case LAW_EVIL:
			switch (theirTask) {
			case ANIMAL:
				return -2;
			case LAWLESS_NODE_GUARDS:
				return -2;
			case LAW_EVIL:
				return 2;
			case MONSTER:
				return -2;
			case MUG:
				return -2;
			}
			if (
					(
						(theirAligns[AlignmentClass.LAW.ordinal()][0]-theirAligns[AlignmentClass.LAW.ordinal()][1])
					+
						(theirAligns[AlignmentClass.MERCHANT.ordinal()][0]-theirAligns[AlignmentClass.MERCHANT.ordinal()][1])
					)
					-
					(
						(theirAligns[AlignmentClass.LAWLESS.ordinal()][0]-theirAligns[AlignmentClass.LAWLESS.ordinal()][1])	
					)
				<0
				) {
					return -2;
				}
			break;
		case LAW_GOOD:
			switch (theirTask) {
			case MONSTER:
				return -2;
			case MUG:
				return -2;
			}
			if (
					(
						(theirAligns[AlignmentClass.LAW.ordinal()][0]-theirAligns[AlignmentClass.LAW.ordinal()][1])
					+
						(theirAligns[AlignmentClass.MERCHANT.ordinal()][0]-(theirAligns[AlignmentClass.MERCHANT.ordinal()][1]/2f))
					+
						(theirAligns[AlignmentClass.HEROIC.ordinal()][0]-theirAligns[AlignmentClass.HEROIC.ordinal()][1])
					)
					-
					(
						(theirAligns[AlignmentClass.LAWLESS.ordinal()][0]-theirAligns[AlignmentClass.LAWLESS.ordinal()][1])	
					)
				<0
				) {
					return -2;
				}
			break;
		case MUG:
			switch (theirTask) {
			case RICH:
				return -2;
			case LAW_EVIL:
				return -2;
			case LAW_GOOD:
				return -2;
			}
			if (
					(
						(theirAligns[AlignmentClass.LAW.ordinal()][0]-theirAligns[AlignmentClass.LAW.ordinal()][1])
					+
						(theirAligns[AlignmentClass.MERCHANT.ordinal()][0]-theirAligns[AlignmentClass.MERCHANT.ordinal()][1])
					)
					-
					(
						(theirAligns[AlignmentClass.LAWLESS.ordinal()][0]-theirAligns[AlignmentClass.LAWLESS.ordinal()][1])	
					)
				>0
				) {
					return -2;
				}
			break;
		case RACIST:
			if (us.getBag().getRace() != them.getBag().getRace()) {
				return -2;
			}
			break;
		case REVENGE:
			if (them.isPlayer()) {
				return -2;
			}
			break;
		}
		
		return 0;
	}
	
	public enum AlignmentClass{
		LAW, HEROIC, LAWLESS, FAME, MERCHANT, PRIMAL
	}
	
	public float[][] getAlignment() {
		float[][] rets = new float[AlignmentClass.values().length][2];
		//I think they start at 0?
		//in most cases should already be handled by it giving it to multiple facs in the first place
		for (FSub s: factions) {
			switch (s.name) {
			case DUEL:
				rets[AlignmentClass.FAME.ordinal()][0] += s.forFac;
				break;
			case FOREST:
				rets[AlignmentClass.PRIMAL.ordinal()][0] += s.forFac;
				rets[AlignmentClass.PRIMAL.ordinal()][1] += s.againstFac;
				break;
			case HEROIC:
				rets[AlignmentClass.HEROIC.ordinal()][0] += s.forFac;
				rets[AlignmentClass.FAME.ordinal()][0] += s.forFac/2f;
				rets[AlignmentClass.HEROIC.ordinal()][1] += s.againstFac;
				break;
			case HUNTER:
				rets[AlignmentClass.HEROIC.ordinal()][0] += s.forFac/3f;
				rets[AlignmentClass.MERCHANT.ordinal()][0] += s.forFac/3f;
				rets[AlignmentClass.FAME.ordinal()][0] += s.forFac/10f;
				break;
			case LAW_EVIL:
				rets[AlignmentClass.MERCHANT.ordinal()][0] += s.forFac/2f;
				rets[AlignmentClass.LAW.ordinal()][0] += s.forFac;
				rets[AlignmentClass.LAW.ordinal()][1] += s.againstFac;
				break;
			case LAW_GOOD:
				rets[AlignmentClass.MERCHANT.ordinal()][0] += s.forFac/4f;
				rets[AlignmentClass.LAW.ordinal()][0] += s.forFac;
				rets[AlignmentClass.LAW.ordinal()][1] += s.againstFac;
				break;
			case MERCHANT:
				rets[AlignmentClass.MERCHANT.ordinal()][0] += s.forFac*2f;
				rets[AlignmentClass.MERCHANT.ordinal()][1] += s.againstFac;
				rets[AlignmentClass.LAW.ordinal()][1] += s.againstFac/4f;
				break;
			case ROGUE:
				rets[AlignmentClass.LAWLESS.ordinal()][0] += s.forFac;
				rets[AlignmentClass.LAWLESS.ordinal()][1] += s.againstFac;
				rets[AlignmentClass.FAME.ordinal()][0] += s.forFac/10f;
				break;	
			}
		}
		return rets;
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
