package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.Person.PersonFlag;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;

public class BossNode implements NodeType {
	
	private static final BossNode handler = new BossNode();
	
	public static BossNode getSingleton() {
		return handler;
	}
	
	@Override
	public int getNode(NodeConnector owner, int guessDepth, int tier){
		int node = owner.newNode();
		owner.setEventNum(node,owner.parent.bossType());
		owner.setTypeNum(node,NodeType.ReservedType.BOSS_TYPE.num);
		owner.setLevel(node, tier);
		owner.setStorage(node,new Object[] {"",new ArrayList<Person>()});
		return node;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		return null;//should not be used
	}
	
	@Override
	public NodeConnector generate(NodeFeature owner, int size, int tier) {
		return null;//MAYBELATER
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
			p.setTitle("The Fatespinner");
			p.getBag().getDrawBanes().add(DrawBane.TELESCOPE);
			peeps.add(p);
			p = (RaceFactory.makeMimic(extra.zeroOut(level-3)+1));
			p.setFlag(PersonFlag.IS_ADD, true);
			peeps.add(p);
			p = (RaceFactory.makeMimic(extra.zeroOut(level-3)+1));
			p.setFlag(PersonFlag.IS_ADD, true);
			peeps.add(p);
			
		break;
		case 2:
			//made.name = "The Hell Baron (Boss)";
			//made.interactString = "challenge The Hell Baron";
			p = RaceFactory.getBoss(level);
			p.setTitle("The Baron of Hell");
			p.getBag().getDrawBanes().add(DrawBane.LIVING_FLAME);
			peeps.add(p);
		break;
		}
		//we don't need to put the list back since we have a ref to it
	}
	
	private void setGenericCorpse(NodeConnector holder,int node, Person body) {
		holder.setStateNum(node,0);
		holder.setTypeNum(node,NodeType.ReservedType.GENERIC_TYPE.num);
		holder.setEventNum(node,1);//FIXME GENERIC CORPSE LOOKING with person as only storage.
		//also make variant with just a string instead of a person
		//and another variant that doesn't have either, just using the state number for 'wolves' or 'person', etc
		holder.setStorage(node, body);
	}
	
	private boolean fatespinner(NodeConnector holder,int node) {
		//if (holder.getEventNum(node) == 0) {
			extra.println(extra.PRE_RED+"You challenge the fatespinner!");
			List<Person> list = (List<Person>) holder.getStorageFirstClass(node,List.class);
			List<List<Person>> listlist = new ArrayList<List<Person>>();
			listlist.add(Player.list());
			listlist.add(list);
			Combat c = mainGame.HugeBattle(holder.getWorld(),listlist);
			if (c.playerWon() > 0) {
				holder.setForceGo(node,false);
				//node.interactString = "approach the fatespinner's corpse";
				//node.storage1 = null;
				
				Person spinner = list.stream().filter(p->!p.getFlag(PersonFlag.IS_ADD)).findAny().get();//throw if can't find
				//node.state = 1;
				//node.name = "The Fatespinner's corpse";
				setGenericCorpse(holder,node, spinner);
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
			List<List<Person>> listlist = new ArrayList<List<Person>>();
			listlist.add(Player.list());
			listlist.add(list);
			Combat c = mainGame.HugeBattle(holder.getWorld(),listlist);
			if (c.playerWon() > 0) {
				holder.setForceGo(node,false);
				//node.interactString = "approach the hell baron's corpse";
				//node.storage1 = null;
				//node.state = 1;
				//node.name = "The Hell Baron's corpse";
				//just in case I want to add adds later
				Person baron = list.stream().filter(p->!p.getFlag(PersonFlag.IS_ADD)).findAny().get();
				setGenericCorpse(holder,node, baron);
				Networking.unlockAchievement("boss2");
				return false;
			}else {
				return true;//lost, kick out
			}
		//}else {
		//	extra.println("Here lies the body of the hell baron...");
		//	return false;
		//}
		
		
	}

	@Override
	public boolean interact(NodeConnector holder,int node) {
		switch(holder.getEventNum(node)) {
		case 1: return fatespinner(holder,node);
		case 2: return hellbaron(holder,node);
		}
		throw new RuntimeException("Invalid boss");
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return null;
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
		}
		throw new RuntimeException("Invalid boss");
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		case 1: return "Fatespinner's Observatory";
		case 2: return "A Minor Throne of Hell";
		}
		throw new RuntimeException("Invalid boss");
	}

}
