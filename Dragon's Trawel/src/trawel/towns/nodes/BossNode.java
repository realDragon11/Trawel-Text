package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.battle.Combat.SkillCon;
import trawel.factions.Faction;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.fort.SubSkill;

public class BossNode implements NodeType {
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier){
		int node = holder.newNode();
		holder.setEventNum(node,holder.parent.bossType().ordinal());
		holder.setTypeNum(node,NodeType.NodeTypeNum.BOSS.ordinal());
		holder.setLevel(node, tier);
		holder.setStorage(node,new Object[] {"",new ArrayList<Person>()});
		return node;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		return null;//should not be used
	}
	
	@Override
	public int generate(NodeConnector holder, int from, int sizeLeft, int tier) {
		return -1;
	}
	
	public enum BossType{
		NONE,FATESPINNER,GENERIC_DEMON_OVERLORD,YORE,OLD_QUEEN
	}
	
	@Override
	public void apply(NodeConnector holder, int madeNode) {
		Person p;
		List<Person> peeps = holder.getStorageFirstClass(madeNode, List.class);//any list, since type erasure
		//we don't need to put the list back since we have a ref to it
		int level = holder.getLevel(madeNode);
		switch (BossType.values()[holder.getEventNum(madeNode)]) {
		case NONE:
			throw new RuntimeException("trying to make none boss in "+holder.parent.getName());
		case FATESPINNER:
			p = RaceFactory.getBoss(level);
			p.setFlag(PersonFlag.PLAYER_LOOT_ONLY,true);
			p.cleanSetSkillHas(Perk.FATESPINNER_NPC);
			p.setTitle(", the Fatespinner");
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
			p.getBag().addDrawBaneSilently(DrawBane.TELESCOPE);
			//p.liteRefreshClassless();//now unneeded
			p.finishGeneration();
			peeps.add(p);
			refillFatespinnerList(peeps,null,level);	
		break;
		case GENERIC_DEMON_OVERLORD:
			p = RaceFactory.makeDemonOverlord(level);
			p.cleanSetSkillHas(Perk.HELL_BARON_NPC);
			p.setTitle(", Baron of Hell");
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
			p.getBag().addDrawBaneSilently(DrawBane.LIVING_FLAME);
			//p.liteRefreshClassless();//now unneeded
			p.finishGeneration();
			peeps.add(p);
		break;
		case YORE:
			level = Math.max(6,level);
			p = RaceFactory.getBoss(level);
			p.cleanSetSkillHas(Perk.YORE_NPC);
			p.setFirstName("Yore");
			p.setTitle("");
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
			//p.liteRefreshClassless();//now unneeded
			p.finishGeneration();
			peeps.add(p);
			p = RaceFactory.makeFellReaver(level-6);
			p.setFlag(PersonFlag.IS_MOOK, true);
			peeps.add(p);
			p = (RaceFactory.makeFellReaver(level-6));
			p.setFlag(PersonFlag.IS_MOOK, true);
			peeps.add(p);
		break;
		case OLD_QUEEN:
			p = RaceFactory.getBoss(level);
			p.setFlag(PersonFlag.PLAYER_LOOT_ONLY,true);
			p.cleanSetSkillHas(Perk.ANCIENT);
			p.setTitle(", the Empress");
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
			p.getBag().addDrawBaneSilently(DrawBane.VIRGIN);//idk
			p.finishGeneration();
			peeps.add(p);
			refillOldQueenList(peeps,null,level);
			break;
		default:
			throw new RuntimeException("invalid boss " +holder.getEventNum(madeNode) + " in " + holder.parent.getName() + " in " + holder.parent.getTown().getName());
		}
		//we don't need to put the list back since we have a ref to it
	}
	
	private void setGenericCorpse(NodeConnector holder,int node, Person body) {
		GenericNode.setSimpleDeadPerson(holder, node, body);
	}
	
	private static interface MookRefiller {
		public Person refill(int nodeLevel);
	}
	
	/**
	 * if peeps is the list that is already stored, you will not need to re-store it
	 */
	private void refillList(List<Person> peeps, List<Person> keep, int size, int nodeLevel, MookRefiller filler) {
		if (keep != null) {
			peeps.removeIf(p -> p.getFlag(PersonFlag.IS_MOOK) && !keep.contains(p));
		}
		while (peeps.size() < 4) {
			peeps.add(filler.refill(nodeLevel));
		}
	}
	
	private void refillFatespinnerList(List<Person> peeps, List<Person> keep, int nodeLevel) {
		refillList(peeps,keep,4,nodeLevel,new MookRefiller() {
			@Override
			public Person refill(int nodeLevel) {
				Person p = (RaceFactory.makeMimic(extra.zeroOut(nodeLevel-3)+1));
				p.setFlag(PersonFlag.IS_MOOK, true);
				return p;
			}});
	}
	
	private void refillOldQueenList(List<Person> peeps, List<Person> keep, int nodeLevel) {
		refillList(peeps,keep,5,nodeLevel,new MookRefiller() {
			@Override
			public Person refill(int nodeLevel) {
				Person p = RaceFactory.makeDGuard(extra.zeroOut(nodeLevel-5)+1);
				p.setFlag(PersonFlag.IS_MOOK, true);
				return p;
			}});
	}
	
	
	private boolean fatespinner(NodeConnector holder,int node) {
		//if (holder.getEventNum(node) == 0) {
			extra.println(extra.PRE_BATTLE+"You challenge the Fatespinner!");
			List<Person> fated = (List<Person>) holder.getStorageFirstClass(node,List.class);
			List<Person> playerSide = holder.parent.getHelpFighters();
			playerSide.addAll(Player.player.getAllies());
			List<List<Person>> people = new ArrayList<List<Person>>();
			people.add(playerSide);
			people.add(fated);
			//fatespinner can scry
			List<SkillCon> cons = Collections.singletonList(new SkillCon(SubSkill.SCRYING,50,1));
			Combat c = mainGame.HugeBattle(holder.getWorld(), cons, people, true);
			
			Person spinner = fated.stream().filter(p->!p.getFlag(PersonFlag.IS_MOOK)).findAny().get();//throw if can't find
			
			List<Person> survs = c.getNonSummonSurvivors();
			holder.parent.retainAliveFighters(survs);
			if (c.playerWon() > 0) {
				if (c.playerWon() < 2) {
					//player must survive battle for it to stick
					extra.println("Fate will not allow this... It seems like this Fatespinner's thread winds on.");
					extra.println("You wake up at the base of the tower, a storm passing by overhead.");
					//this is the list that is already stored
					refillFatespinnerList(fated,survs,holder.getLevel(node));
					return true;
				}else {
					holder.setForceGo(node,false);
					setGenericCorpse(holder,node, spinner);
					Player.unlockPerk(Perk.FATED);
					Networking.unlockAchievement("boss1");
					setBossKilled(spinner.getName());
					Player.player.addAchieve("fatespinner","Owner of Their Own Fate");
					heroRep(holder,node,1f);
					return false;
				}
			}else {
				//now they need to be killed all at once, could also sort out the adds if dead
				if (survs.size() != fated.size()) {
					if (!survs.contains(spinner)) {
						extra.println("The Spinner's Fate is not so easily changed. You have the sinking feeling you haven't seen the last of them.");
					}else {
						extra.println("The Spinner admires good help. The mimics slain are likely already replaced.");
					}
				}else {
					extra.println("The Spinner cannot weave you fate, but it seems they are still in control of their own.");
				}
				//this is the list that is already stored
				refillFatespinnerList(fated,survs,holder.getLevel(node));
				return true;//lost, kick out
			}
	}
	
	private boolean hellbaron(NodeConnector holder,int node) {
		//if (node.state == 0) {
			extra.println(extra.PRE_BATTLE+"You challenge the Hell Baron!");
			List<Person> list = (List<Person>) holder.getStorageFirstClass(node,List.class);
			Combat c = Player.player.massFightWith(list);
			if (c.playerWon() > 0) {
				holder.setForceGo(node,false);
				Person baron = list.stream().filter(p->!p.getFlag(PersonFlag.IS_MOOK)).findAny().get();
				setGenericCorpse(holder,node, baron);
				Networking.unlockAchievement("boss2");
				Player.unlockPerk(Perk.HELL_BARONESS_1);
				setBossKilled(baron.getName());
				Player.player.addAchieve("hell_baron","Hell Baroness");
				heroRep(holder,node,2f);
				return false;
			}else {
				return true;//lost, kick out
			}
	}
	
	private boolean yore(NodeConnector holder,int node) {
			extra.println(extra.PRE_BATTLE+"You challenge a legend!");
			List<Person> list = (List<Person>) holder.getStorageFirstClass(node,List.class);
			List<List<Person>> battleList = new ArrayList<List<Person>>();
			battleList.add(Player.player.getAllies());
			battleList.add(list);
			List<SkillCon> cons = ((Dungeon)(holder.parent)).getBattleCons();
			Combat c = mainGame.HugeBattle(holder.getWorld(), cons,battleList, true);
			if (c.playerWon() > 0) {
				holder.setForceGo(node,false);
				Person yore = list.stream().filter(p->!p.getFlag(PersonFlag.IS_MOOK)).findAny().get();//throw if can't find
				setGenericCorpse(holder,node, yore);
				Player.unlockPerk(Perk.STORYTELLER);
				Networking.unlockAchievement("boss3");
				setBossKilled(yore.getName());
				Player.player.addAchieve("yore","Story Slayer");
				heroRep(holder,node,3f);
				return false;
			}else {
				return true;//lost, kick out
			}
	}
	
	private boolean oldQueen(NodeConnector holder,int node) {
		extra.println(extra.PRE_BATTLE+"You challenge the Ancient Queen!");
		List<Person> queens = (List<Person>) holder.getStorageFirstClass(node,List.class);
		List<Person> playerSide = holder.parent.getHelpFighters();
		playerSide.addAll(Player.player.getAllies());
		List<List<Person>> people = new ArrayList<List<Person>>();
		people.add(playerSide);
		people.add(queens);
		Combat c = mainGame.HugeBattle(holder.getWorld(), null, people, true);
		
		Person queen = queens.stream().filter(p->!p.getFlag(PersonFlag.IS_MOOK)).findAny().get();//throw if can't find
		
		List<Person> survs = c.getNonSummonSurvivors();
		holder.parent.retainAliveFighters(survs);
		if (c.playerWon() > 0) {
			if (c.playerWon() < 2) {
				//player must survive battle for it to stick
				extra.println("It would seem your associates were able to finish the battle but not kill of the Queen for good.");
				//this is the list that is already stored
				refillFatespinnerList(queens,survs,holder.getLevel(node));
				return true;
			}else {
				holder.setForceGo(node,false);
				setGenericCorpse(holder,node, queen);
				Player.unlockPerk(Perk.QUEENSLAYER);
				Networking.unlockAchievement("boss4");
				setBossKilled(queen.getName());
				Player.player.addAchieve("old_queen","Ancient Queen Slayer");
				heroRep(holder,node,2f);
				return false;
			}
		}else {
			if (survs.size() != queens.size()) {
				if (!survs.contains(queen)) {
					extra.println("The Queen has died many times. But without the right ritual, it's never stuck.");
				}else {
					extra.println("The Queen has likely mustered new bodyguards already.");
				}
			}else {
				extra.println("The Queen's defenses have proven steadfast. Perhaps this is how she has survived so long.");
			}
			//this is the list that is already stored
			refillFatespinnerList(queens,survs,holder.getLevel(node));
			return true;//lost, kick out
		}
	}

	@Override
	public boolean interact(NodeConnector holder,int node) {
		switch (BossType.values()[holder.getEventNum(node)]) {
		case FATESPINNER: return fatespinner(holder,node);
		case GENERIC_DEMON_OVERLORD: return hellbaron(holder,node);
		case YORE: return yore(holder,node);
		case OLD_QUEEN: return oldQueen(holder,node);
		}
		throw new RuntimeException("Invalid boss");
	}
	
	public static DrawBane[] dblist = new DrawBane[] {DrawBane.VIRGIN};//welp
	
	@Override
	public DrawBane[] dbFinds() {
		//bosses can be present through generics, should honestly stop using boss nodes at this point
		return dblist;
	}

	@Override
	public void passTime(NodeConnector holder, int node, double time, TimeContext calling) {
	}

	@Override
	public String interactString(NodeConnector holder, int node) {
		switch (BossType.values()[holder.getEventNum(node)]) {
		case FATESPINNER: return extra.PRE_BATTLE+"reject Fate";
		case GENERIC_DEMON_OVERLORD: return extra.PRE_BATTLE+"become the Baroness";
		case YORE: return extra.PRE_BATTLE+"stall a Story";
		case OLD_QUEEN: return extra.PRE_BATTLE+"oust an Empress";
		}
		throw new RuntimeException("Invalid boss");
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch (BossType.values()[holder.getEventNum(node)]) {
		case FATESPINNER: return "Fatespinner's Observatory";
		case GENERIC_DEMON_OVERLORD: return "A Minor Throne of Hell";
		case YORE: return "A Rigged Arena";
		case OLD_QUEEN: return "Prehistoric Throne";
		}
		throw new RuntimeException("Invalid boss");
	}
	
	public static void setBossKilled(String bossname) {
		Player.player.addGroupedAchieve("boss","Bosses",bossname);
	}
	
	public static void heroRep(NodeConnector holder,int node,float mult) {
		Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult* IEffectiveLevel.unEffective(holder.getLevel(node)), 0);
	}

}
