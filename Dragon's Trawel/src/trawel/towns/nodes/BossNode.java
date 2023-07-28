package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;

public class BossNode implements NodeType {
	
	private static final BossNode handler = new BossNode();
	
	private NodeConnector node;
	
	public static BossNode getSingleton() {
		return handler;
	}
	
	@Override
	public NodeConnector getNode(NodeFeature owner, int tier) {
		NodeConnector make = new NodeConnector();
		make.eventNum = owner.bossType();
		make.typeNum = -1;
		make.level = tier;
		make.storage1 = new ArrayList<Person>();
		return make;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		return getNode(owner,tier);
	}
	
	@Override
	public NodeConnector generate(NodeFeature owner, int size, int tier) {
		return getNode(owner,tier);
	}
	
	@Override
	public void apply(NodeConnector made) {
		Person p;
		List<Person> peeps = (List<Person>) made.storage1;
		switch (made.eventNum) {
		case 1:
			made.name = "The Fatespinner (Boss)";
			made.interactString = "challenge The Fatespinner";
			peeps.add(RaceFactory.makeMimic(extra.zeroOut(made.level-3)+1));
			peeps.add(RaceFactory.makeMimic(extra.zeroOut(made.level-3)+1));
			p = RaceFactory.getBoss(made.level);
			p.setTitle("The Fatespinner");
			p.getBag().getDrawBanes().add(DrawBane.TELESCOPE);
			peeps.add(p);
		break;
		case 2:
			made.name = "The Hell Baron (Boss)";
			made.interactString = "challenge The Hell Baron";
			p = RaceFactory.getBoss(made.level);
			p.setTitle("The Baron of Hell");
			p.getBag().getDrawBanes().add(DrawBane.LIVING_FLAME);
			peeps.add(p);
		break;
		}
		made.storage1 = peeps;
	}
	
	private boolean fatespinner() {
		if (node.state == 0) {
			extra.println(extra.PRE_RED+"You challenge the fatespinner!");
			List<Person> list = (List<Person>) node.storage1;
			List<Person> survivors = mainGame.HugeBattle(list,Player.list());
			if (survivors.contains(Player.player.getPerson())) {
				node.forceGo = false;
				node.interactString = "approach the fatespinner's corpse";
				node.storage1 = null;
				node.state = 1;
				node.name = "The Fatespinner's corpse";
				Networking.sendStrong("Achievement|boss1|");
				return false;
			}else {
				node.storage1 = survivors;
				return true;
			}
		}else {
			extra.println("Here lies the body of the fatespinner...");
			return false;
		}
		
		
	}
	
	private boolean hellbaron() {
		if (node.state == 0) {
			extra.println(extra.PRE_RED+"You challenge the hell baron!");
			List<Person> list = (List<Person>) node.storage1;
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),list.get(0));
			if (winner == Player.player.getPerson()) {
				node.forceGo = false;
				node.interactString = "approach the hell baron's corpse";
				node.storage1 = null;
				node.state = 1;
				node.name = "The Hell Baron's corpse";
				Networking.sendStrong("Achievement|boss2|");
				return false;
			}else {
				return true;
			}
		}else {
			extra.println("Here lies the body of the hell baron...");
			return false;
		}
		
		
	}

	@Override
	public boolean interact(NodeConnector node) {
		this.node = node;
		switch(node.eventNum) {
		case 1: return fatespinner();
		case 2: return hellbaron();
		}
		throw new RuntimeException("Invalid boss");
	}
	
	@Override
	public DrawBane[] dbFinds() {
		return null;
	}

	@Override
	public void passTime(NodeConnector node, double time, TimeContext calling) {
		// empty
		
	}

}
