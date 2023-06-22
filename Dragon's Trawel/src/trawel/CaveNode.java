package trawel;
import java.util.ArrayList;

public class CaveNode extends NodeConnector implements java.io.Serializable{
	//potentail problem: all this code is in a highly duplicated node


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		case 2: storage1 = extra.choose("silver","gold","platinum","iron","copper"); name = storage1+" vein"; interactString = "mine "+storage1;break;
		case 3: name = extra.choose("bat"); interactString = "ERROR"; forceGo = true;
		storage1 = RaceFactory.makeBat(level);break;
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
		case -1: Networking.sendStrong("Achievement|cave1|"); break;
		case 1: bear1(); if (state == 0) {return true;};break;
		case 2: goldVein1();break;
		case 3: bat1(); if (state == 0) {return true;};break;
		}
		return false;
	}
	
	
	private void bear1() {
		if (state == 0) {
			extra.println(extra.PRE_RED+"The bear attacks you!");
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

	
	private void goldVein1() {
		if (state == 0) {
			Networking.sendStrong("Achievement|ore1|");
			int mult1 = 0, mult2 = 0;
			switch (storage1.toString()) {
			case "gold": mult1 = 100; mult2 = 200;break;
			case "silver": mult1 = 75; mult2 = 150;break;
			case "platinum": mult1 = 150; mult2 = 300;break;
			case "iron": mult1 = 50; mult2 = 100;break;
			case "copper": mult1 = 25; mult2 = 50;break;
			}
			int gold = extra.randRange(mult1,mult2)*level;
			Player.bag.addGold(gold);
			extra.println("You mine the vein for "+storage1+" worth "+ gold + " gold.");
					state = 1;
					name = "empty vein";
					interactString = "examine empty vein";
			}else {extra.println("The "+storage1+" has already been mined.");}
		
	}
	
	private void bat1() {
		if (state == 0) {
			extra.println(extra.PRE_RED+"The bat attacks you!");
			Person p = (Person)storage1;
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					state = 1;
					storage1 = null;
					name = "dead "+name;
					interactString = "examine body";
					forceGo = false;
				}
		}else {extra.println("The bat corpse lies here.");}
		
	}

	

	@Override
	protected String shapeName() {
		return parent.getShape().name();
	}

	@Override
	public void passTime(double d) {
		// Auto-generated method stub
		
	}

	@Override
	public void timeFinish() {
		// Auto-generated method stub
		
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
