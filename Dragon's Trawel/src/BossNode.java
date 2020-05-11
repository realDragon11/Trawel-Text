import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class BossNode extends NodeConnector implements Serializable {
	private ArrayList<Person> people;
	private int state = 0;
	private int type;
	public BossNode(int tier,int t){
		type = t;
		setConnects(new ArrayList<NodeConnector>());
		level = tier;
		switch (type) {
		case 0:
			name = "The Fatespinner (Boss)";
			interactString = "challenge The Fatespinner";
			people = new ArrayList<Person>();
			people.add(RaceFactory.makeMimic(extra.zeroOut(tier-3)+1));
			people.add(RaceFactory.makeMimic(extra.zeroOut(tier-3)+1));
			Person p = new Person(tier);
			p.setTitle("The Fatespinner");
			people.add(p);
		break;
		}
	}
	
	private boolean fatespinner() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("You challenge the fatespinner!");
			ArrayList<Person> list = people;
			ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
			if (survivors.contains(Player.player.getPerson())) {
			forceGo = false;
			interactString = "approach the fatespinner's corpse";
			people = null;
			state = 1;
			name = name + "'s corpse";
			Networking.sendStrong("Achievement|boss1|");
			return false;}else {
				people = survivors;
				return true;
			}
		}else {
			extra.println("Here lies the body of the fatespinner...");
			return false;
		}
		
		
	}

	@Override
	protected boolean interact() {
		switch (type) {
		case 0: return fatespinner();
		}
		throw new RuntimeException("Invalid boss");
	}

}
