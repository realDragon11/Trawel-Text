package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.battle.Combat.SkillCon;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Perk;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;

public class BossNode implements NodeType {
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier){
		int node = holder.newNode();
		holder.setEventNum(node,holder.parent.bossType());
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
		return -1;//MAYBELATER
	}
	
	@Override
	public void apply(NodeConnector holder, int madeNode) {
		Person p;
		List<Person> peeps = holder.getStorageFirstClass(madeNode, List.class);//any list, since type erasure
		//we don't need to put the list back since we have a ref to it
		int level = holder.getLevel(madeNode);
		switch (holder.getEventNum(madeNode)) {
		case 1:
			//made.name = "The Fatespinner (Boss)";
			//made.interactString = "challenge The Fatespinner";
			
			p = RaceFactory.getBoss(level);
			p.cleanSetSkillHas(Perk.FATESPINNER_NPC);
			p.setTitle(", the Fatespinner");
			p.getBag().addDrawBaneSilently(DrawBane.TELESCOPE);
			p.liteRefreshClassless();
			p.finishGeneration();
			peeps.add(p);
			p = (RaceFactory.makeMimic(extra.zeroOut(level-3)+1));
			p.setFlag(PersonFlag.IS_MOOK, true);
			peeps.add(p);
			p = (RaceFactory.makeMimic(extra.zeroOut(level-3)+1));
			p.setFlag(PersonFlag.IS_MOOK, true);
			peeps.add(p);
			
		break;
		case 2:
			//made.name = "The Hell Baron (Boss)";
			//made.interactString = "challenge The Hell Baron";
			p = RaceFactory.makeDemonOverlord(level);
			p.cleanSetSkillHas(Perk.HELL_BARON_NPC);
			p.setTitle(", Baron of Hell");
			p.getBag().addDrawBaneSilently(DrawBane.LIVING_FLAME);
			p.liteRefreshClassless();
			p.finishGeneration();
			peeps.add(p);
		break;
		case 3:
			level = Math.max(6,level);
			p = RaceFactory.getBoss(level);
			p.cleanSetSkillHas(Perk.YORE_NPC);
			p.setFirstName("Yore");
			p.setTitle("");
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
			p.getBag().addDrawBaneSilently(DrawBane.KNOW_FRAG);
			p.liteRefreshClassless();
			p.finishGeneration();
			peeps.add(p);
			p = RaceFactory.makeFellReaver(level-3);
			p.setFlag(PersonFlag.IS_MOOK, true);
			peeps.add(p);
			p = (RaceFactory.makeFellReaver(level-3));
			p.setFlag(PersonFlag.IS_MOOK, true);
			peeps.add(p);
		break;
		default:
			throw new RuntimeException("invalid boss " +holder.getEventNum(madeNode) + " in " + holder.parent.getName() + " in " + holder.parent.getTown().getName());
		}
		//we don't need to put the list back since we have a ref to it
	}
	
	private void setGenericCorpse(NodeConnector holder,int node, Person body) {
		GenericNode.setSimpleDeadPerson(holder, node, body);
	}
	
	private boolean fatespinner(NodeConnector holder,int node) {
		//if (holder.getEventNum(node) == 0) {
			extra.println(extra.PRE_RED+"You challenge the fatespinner!");
			List<Person> list = (List<Person>) holder.getStorageFirstClass(node,List.class);
			Combat c = Player.player.massFightWith(list);
			if (c.playerWon() > 0) {
				holder.setForceGo(node,false);
				//node.interactString = "approach the fatespinner's corpse";
				//node.storage1 = null;
				
				Person spinner = list.stream().filter(p->!p.getFlag(PersonFlag.IS_MOOK)).findAny().get();//throw if can't find
				//node.state = 1;
				//node.name = "The Fatespinner's corpse";
				setGenericCorpse(holder,node, spinner);
				Player.unlockPerk(Perk.FATED);
				Networking.unlockAchievement("boss1");
				return false;
			}else {
				//now they need to be killed all at once, could also sort out the adds if dead
				//node.storage1 = survivors;
				return true;//lost, kick out
			}
			/*
		}else {
			extra.println("Here lies the body of the fatespinner...");
			return false;
		}*/
		
		
	}
	
	private boolean hellbaron(NodeConnector holder,int node) {
		//if (node.state == 0) {
			extra.println(extra.PRE_RED+"You challenge the Hell Baron!");
			List<Person> list = (List<Person>) holder.getStorageFirstClass(node,List.class);
			Combat c = Player.player.massFightWith(list);
			if (c.playerWon() > 0) {
				holder.setForceGo(node,false);
				//node.interactString = "approach the hell baron's corpse";
				//node.storage1 = null;
				//node.state = 1;
				//node.name = "The Hell Baron's corpse";
				//just in case I want to add adds later
				Person baron = list.stream().filter(p->!p.getFlag(PersonFlag.IS_MOOK)).findAny().get();
				setGenericCorpse(holder,node, baron);
				Networking.unlockAchievement("boss2");
				Player.unlockPerk(Perk.HELL_BARONESS);
				return false;
			}else {
				return true;//lost, kick out
			}
		//}else {
		//	extra.println("Here lies the body of the hell baron...");
		//	return false;
		//}
		
		
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
				return false;
			}else {
				return true;//lost, kick out
			}
	}

	@Override
	public boolean interact(NodeConnector holder,int node) {
		switch(holder.getEventNum(node)) {
		case 1: return fatespinner(holder,node);
		case 2: return hellbaron(holder,node);
		case 3: return yore(holder,node);
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
		// MAYBELATER Auto-generated method stub
	}

	@Override
	public String interactString(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1: return extra.PRE_RED+"reject Fate";
		case 2: return extra.PRE_RED+"become the Baroness";
		case 3: return extra.PRE_RED+"stall a Story";
		}
		throw new RuntimeException("Invalid boss");
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1: return "Fatespinner's Observatory";
		case 2: return "A Minor Throne of Hell";
		case 3: return "A Rigged Arena";
		}
		throw new RuntimeException("Invalid boss");
	}

}
