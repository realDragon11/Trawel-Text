import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

public class CaveNode extends NodeConnector implements java.io.Serializable{
	//potentail problem: all this code is in a highly duplicated node


	private static final int EVENT_NUMBER =3;
	private int state;
	private int idNum;
	
	private Object storage1, storage2;
	
	//private Town town;
	private Grove parent;
	
	
	
	public CaveNode(int size, int tier,Grove p,boolean stair) {
		state = 0;
		parent = p;
		parentName = "cave";
		
		idNum = extra.randRange(1,EVENT_NUMBER);

		if (extra.chanceIn(1,10)) {
			idNum = 1;//bear
			
		}
		level = tier;
		if (extra.chanceIn(1,10)) {
			level++;
		}
		if (stair) {
			idNum = -1;
			isStair = true;
		}
		
		setConnects(new ArrayList<NodeConnector>());
		forceGo = false;
		//town = t;
		generate(size);
		
	}
	
	public int getLevel() {
		return level;
	}
	
	
	
	private void generate(int size) {
		switch (idNum) {
		case -1:name = extra.choose("cave entrance"); interactString = "traverse "+name;forceGo = true;break;
		case 1: name = extra.choose("bear"); interactString = "ERROR"; forceGo = true;
		storage1 = RaceFactory.makeBear(level);break;
		}
		if (size < 2 || parent.getShape() != Grove.Shape.STANDARD) {
			return;
		}
		int split = extra.randRange(1,Math.min(size,3));
		int i = 1;
		int sizeLeft = size;
		while (i < split) {
			int sizeRemove = extra.randRange(0,sizeLeft-1);
			sizeLeft-=sizeRemove;
			CaveNode n = new CaveNode(sizeRemove,level,parent,false);
			connects.add(n);
			n.getConnects().add(this);
			i++;
		}
		CaveNode n = new CaveNode(sizeLeft,level,parent,false);
		connects.add(n);
		n.getConnects().add(this);
	}
	
	@Override
	protected boolean interact() {
		switch(idNum) {
		case 1: bear1(); if (state == 0) {return true;};break;
		}
		return false;
	}
	
	private void mugger1() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("They attack you!");
			Person p = (Person)storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
					forceGo = false;
				}
		}else {randomLists.deadPerson();}
		
	}
	
	private void bear1() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("The bear attacks you!");
			Person p = (Person)storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
					forceGo = false;
				}
		}else {extra.println("The bear's corpse lays here.");}
		
	}
	
	private boolean gateGuards() {
		if (state == 0) {
			Networking.sendColor(Color.RED);
			extra.println("They attack you!");
			ArrayList<Person> list = (ArrayList<Person>)storage1;
			ArrayList<Person> survivors = mainGame.HugeBattle(list,Player.list());
			
			
			if (survivors.contains(Player.player.getPerson())) {
			forceGo = false;
			interactString = "approach bodies";
			storage1 = null;
			state = 1;
			name = "dead "+name;
			return false;}else {
				storage1 = survivors;
				return true;
			}
		}else {
			randomLists.deadPerson();
			randomLists.deadPerson();
			return false;
		}
		
		
	}

	

	@Override
	protected String shapeName() {
		return parent.getShape().name();
	}



	/*
	public void addBacks() {
		backed++;
		if (connects.size() > 0) {
			for (DungeonNode d: connects) {
				if ((this.isStair)
				if (backed == 0) {
				d.connects.add(this);
				d.addBacks();}
			}
		}
		
	}*/
	

}
