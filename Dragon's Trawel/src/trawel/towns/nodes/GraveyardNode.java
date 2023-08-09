package trawel.towns.nodes;
import java.util.ArrayList;
import java.util.List;

import trawel.AIClass;
import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.Skill;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.time.TimeContext;
import trawel.towns.nodes.NodeConnector.NodeFlag;
import trawel.towns.services.Oracle;

public class GraveyardNode implements NodeType{
	private static final int EVENT_NUMBER =7;
	
	@Override
	public int getNode(NodeConnector holder, int owner, int guessDepth, int tier) {
		int idNum =extra.randRange(1,EVENT_NUMBER);
		int ret = holder.newNode(NodeType.NodeTypeNum.CAVE.ordinal(),idNum,tier);
		return ret;
	}
	
	@Override
	public NodeConnector getStart(NodeFeature owner, int size, int tier) {
		NodeConnector start = new NodeConnector();
		generate(start,0,size,tier);
		return start.complete(owner);
	}
	
	@Override
	public int generate(NodeConnector holder,int from, int size, int tier) {
		int made = getNode(holder,from,0,tier);
		//TODO make the graveyard generator set the idnum of many of it's nodes to make a semi-sane progression into the graveyard
		if (size <= 0) {
			return made;
		}
		int split = extra.randRange(1,Math.min(size,3));
		int i = 0;
		int sizeLeft = size-(split+1);
		int[] dist = new int[split]; 
		while (sizeLeft > 0)//DOLATER: maybe give a better gen type
		{
			dist[extra.randRange(0,split-1)]++;
			sizeLeft--;
		}
		while (i < split) {
			int tempLevel = tier;
			if (extra.chanceIn(1,10)) {
				tempLevel++;
			}
			int n = generate(holder,from,dist[i],tempLevel);
			holder.setMutualConnect(made, n);
			i++;
		}
		return made;
	}
	private static final String 
			STR_SHADOW_FIGURE_ACT = "Approach Shadowy Figure",
			STR_SHADOW_FIGURE_NAME = "Shadowy Figure",
			STR_SHADOW_OBJECT_ACT = "Approach Shadowy Object",
			STR_SHADOW_OBJECT_NAME = "Shadowy Object"
			;
	/**
	 * if should display 'shadowy figure' instead of it's normal text
	 * <br>
	 * NORMAL STATES SHOULD BE >=10 IN GRAVEYARD
	 */
	private static final int STATE_SHADOW_FIGURE = 0;
	/**
	 * if should display 'shadowy object' instead of it's normal text
	 * <br>
	 * NORMAL STATES SHOULD BE >=10 IN GRAVEYARD
	 */
	private static final int STATE_SHADOW_OBJECT = 1;
	
	//note that you can't use generic nodes if you want shadowy behavior
	
	@Override
	public void apply(NodeConnector holder,int madeNode) {
		switch (holder.getEventNum(madeNode)) {
		//case -1:made.name = extra.choose("stairs","ladder"); made.interactString = "traverse "+made.name;made.setForceGo(true);break;
		case 1: 
			holder.setStorage(madeNode, RaceFactory.makeGravedigger(holder.getLevel(madeNode)));
			break;
		case 2:
			holder.setStorage(madeNode, RaceFactory.makeGraverobber(holder.getLevel(madeNode)));
			break;
		case 3: 
			int batLevel = holder.getLevel(madeNode);
			int batAmount = 1;
			while (batAmount < 6) {
				if (batLevel > 3 && extra.chanceIn(3,4)) {
					batAmount+=2;
					batLevel--;
				}else {
					break;
				}
			}
			
			List<Person> list = new ArrayList<Person>();
			for (int i = 0;i < batAmount;i++) {
				list.add(RaceFactory.makeBat(batLevel));
			}
			holder.setStorage(madeNode,list);
			holder.setForceGo(madeNode, true);
		;break;
		case 4:
			holder.setStorage(madeNode, RaceFactory.makeVampire(holder.getLevel(madeNode)));
			;break;
		case 5:
			holder.setStorage(madeNode, RaceFactory.makeCollector(holder.getLevel(madeNode)));
			break;
		case 6://will set the state to the times it is seen until it will attack +10 after seen once
			//will attack instantly if attempting to loot
			holder.setFlag(madeNode,NodeFlag.SILENT_FORCEGO_POSSIBLE,true);
		case 7: //non hostile statue which will show but never attack
			holder.setStorage(madeNode, RaceFactory.makeStatue(holder.getLevel(madeNode)));
			break;
		}
	}
	
	@Override
	public boolean interact(NodeConnector holder, int node) {
		switch(holder.getEventNum(node)) {
		//case -1: Networking.sendStrong("PlayDelay|sound_footsteps|1|"); break;
		case 1:graveDigger(); break;
		case 2: mugger1(); if (node.state == 0) {return true;};break;
		case 3: return packOfBats();
		case 4: vampire1(); if (node.state == 0) {return true;};break;
		case 5:	collector();break;
		case 6: statue(); if (node.state == 0) {return true;};break;
		case 7: statueLoot();break;
		}
		return false;
	}
	
	@Override
	public String interactString(NodeConnector holder, int node) {
		int state = holder.getStateNum(node);
		interactStringState(holder,node,state);
		switch(holder.getEventNum(node)) {
		case 1:
			return "examine entryway";
		}
		return null;
	}
	
	public String interactStringState(NodeConnector holder, int node, int state) {
		if (state < 10 && !Player.player.getPerson().hasSkill(Skill.NIGHTVISION)) {
			if (state == STATE_SHADOW_FIGURE) {
				return STR_SHADOW_FIGURE_ACT;
			}
			if (state == STATE_SHADOW_OBJECT) {
				return STR_SHADOW_OBJECT_ACT;
			}
		}
		return null;
	}

	@Override
	public String nodeName(NodeConnector holder, int node) {
		int state = holder.getStateNum(node);
		if (state < 10 && !Player.player.getPerson().hasSkill(Skill.NIGHTVISION)) {
			if (state == STATE_SHADOW_FIGURE) {
				return STR_SHADOW_FIGURE_NAME;
			}
			if (state == STATE_SHADOW_OBJECT) {
				return STR_SHADOW_OBJECT_NAME;
			}
		}
		switch(holder.getEventNum(node)) {
		case 1:
			return "cave entrance";
		}
		return null;
	}
	
	private boolean packOfBats() {
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The bats descend upon you!");
			List<Person> list = (List<Person>)node.storage1;
			List<Person> survivors = mainGame.HugeBattle(list,Player.list());
			
			if (survivors.contains(Player.player.getPerson())) {
				node.setForceGo(false);
				node.interactString = "approach bat corpses";
				node.storage1 = null;
				node.state = 1;
				node.name = "dead swarm of bats";
			return false;}else {
				node.storage1 = survivors;
				return true;
			}
		}else {
			extra.println("There are a few bat corpses here.");
			node.findBehind("corpses");
			return false;
		}
	}


	private void graveDigger() {
		extra.println("Approach the "+ node.name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (node.state == 0) {
			while (true) {
				Person p = (Person)node.storage1;
				p.getBag().graphicalDisplay(1, p);
				extra.println("You come across a weary gravedigger, warding against undead during a break.");
				node.name = "Gravedigger";node.interactString = "Approach the "+node.name;
				extra.println("1 Leave");//DOLATER: fix menu
				extra.print(extra.PRE_RED);
				extra.println("2 Mug them");
				extra.println("3 Chat with them");
				switch (extra.inInt(3)) {
				default: case 1: extra.println("You leave the gravedigger alone.");return;
				case 2: extra.println("You attack the "+node.name+"!");
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					node.state = 1;
					node.storage1 = null;
					node.name = "dead "+node.name;
					node.interactString = "examine body";
				}
				;return;
				case 3: extra.println("The " + node.name + " turns and answers your greeting.");
				while (true) {
					extra.println("What would you like to ask about?");
					extra.println("1 tell them goodbye");
					extra.println("2 ask for a tip");
					extra.println("3 this graveyard");
					int in = extra.inInt(3);
					switch (in) {
					case 1: extra.println("They wish you well.") ;break;
					case 2: Oracle.tip("gravedigger");break;
					case 3: extra.println("\"We are in " + node.parent.getName() + ". Beware, danger lurks everywhere.\"");break;
					}
					if (in == 1) {
						break;
					}
				}
				}
			}
		}else {
			randomLists.deadPerson();
			node.findBehind("body");
		}
	}
	
	private void mugger1() {
		extra.println("Approach the "+ node.name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The graverobber attacks you!");
			node.name = "Graverobber";
			node.interactString = "Approach the "+node.name;
			Person p = (Person)node.storage1;
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
			if (winner != p) {
				node.state = 1;
				node.storage1 = null;
				node.name = "dead "+node.name;
				node.interactString = "examine body";
				node.setForceGo(false);
			}
		}else {
			randomLists.deadPerson();node.findBehind("body");
		}

	}

	private void vampire1() {
		extra.println("Approach the "+ node.name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("The vampire attacks you!");
			node.name = "Vampire";
			node.interactString = "Approach the "+node.name;
			Person p = (Person)node.storage1;
			Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
			if (winner != p) {
				node.state = 1;
				node.storage1 = null;
				node.name = "dead "+node.name;
				node.interactString = "examine body";
				node.setForceGo(false);
			}
		}else {
			randomLists.deadPerson();
			node.findBehind("dust");
		}

	}
	
	//will add even if we swapped tracks, make sure didn't set to a hit and then pass it
	
	/**
	 * states <10 is normal hasn't visited
	 * <br>
	 * states 11 to 20 are silently stalking
	 * <br>
	 * states 21 to 30 are stalking after having seen
	 * <br>
	 * state 31 is first fighting, state 32 is after visibly fighting, and state 33 is destroyed
	 */
	private boolean statue(NodeConnector holder,int node) {
		int state = holder.getStateNum(node);
		
		//can have nightvision and not have interacted yet, in which case we need special dialogue
		//will silently forcego to count the timer
		boolean forced = holder.isForced();
		boolean fresh = state < 21;//if fresh and not forced they're examining it
		if (state == 20 || state == 30) {//will attack
			state = 31;
		}
		if (state < 10) {
			if (forced) {
				state = extra.randRange(11,18);
			}else {
				state = extra.randRange(21,28);
			}
			
		}else {
			if (state < 30) {
				if (forced) {
					if (extra.chanceIn(1,8)) {
						extra.println("...Did that statue just move?");
					}
					state++;//will only add this way if forced, also can add if examined for real
				}
				
				
			}else {
				//destroyed or attacking
			}
			
		}
		//leave if silently forced, so it's actually silent (aside from 'just move?')
		if (forced && state != 31) {
			return false;
		}
		
		Person p = holder.getStorageFirstPerson(node);
		//now we can display them
		p.getBag().graphicalDisplay(1, p);
		if (state == 33) {
			//destroyed
			extra.println("The destroyed statue slumps in the mud.");
			holder.findBehind(node,"broken statue");
			return false;
		}
		
		if (state != 31) {
			String str = interactStringState(holder,node,STATE_SHADOW_FIGURE);
			if (state > 20 || str == null) {//if has seen before or has nightvision
				extra.println(statueLootAsk(p));
				if (!extra.yesNo() || extra.chanceIn(1,4)) {//25% chance to attack instantly if ignored
					return false;
				}
			}else {
				extra.println(str);
				if (!extra.yesNo()) {
					return false;
				}
				//seen, convert to the seen track, and mess up the timing to mess with
				//them if they checked it due to the 'just move?'
				if (state < 18) {//if wasn't close
					state = extra.randRange(21,26);
				}else {//if was close
					state = extra.randRange(26, 28);
				}
				//they approached it, now we can ask to loot it for real
				extra.println(statueLootAsk(p));
				if (!extra.yesNo() || extra.chanceIn(1,4)) {//25% chance to attack instantly if ignored
					return false;
				}
			}
			
		}
		
		//if we get here, they want to loot it or it's attacking them
		
		if (state == 31) {//first fight
			extra.println(extra.PRE_RED+"The statue springs to life and attacks you!");
		}
		Combat c = Player.player.fightWith(p);
		if (c.playerWon() > 0) {
			holder.setStateNum(node,33);
			holder.setForceGo(node,false);
			return false;
		}else {
			holder.setStateNum(node,32);
			return true;
		}
		
		
	}
	
	private String statueLootAsk(Person p) {
		return ("The motionless statue of a " +p.getBag().getRace().renderName(false) + " overlooks a coffin. Loot it?");
	}
	
	private void statueLoot() {
		extra.println("Approach the "+ node.name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (node.state == 0) {
			extra.print(extra.PRE_RED);
			extra.println("You loot the statue...");
			Person p = RaceFactory.makeStatue(Math.min(Player.player.getPerson().getLevel(), node.level)); ;
			p.getBag().graphicalDisplay(1,p);
			AIClass.loot(p.getBag(),Player.bag,true,Player.player.getPerson());
			node.state = 1;
			node.name = "looted statue";
			node.interactString = "examine statue";
			node.setForceGo(false);
				
		}else {
			extra.println("You already looted this statue!");
			node.findBehind("statue");
		}
		Networking.clearSide(1);
	}

	private void collector() {
		extra.println("Approach the "+ node.name+"?");
		if (!extra.yesNo()) {
			return;
		}
		if (node.state == 0) {
			Person p = (Person)node.storage1;
			node.name = p.getName();
			node.interactString = "Approach "+ node.name;
			p.getBag().graphicalDisplay(1, p);
			extra.print(extra.PRE_RED);
			extra.println("Challenge "+ p.getName() + "?");
			if (extra.yesNo()){
				Person winner = mainGame.CombatTwo(Player.player.getPerson(),p);
				if (winner != p) {
					node.state = 1;
					node.storage1 = null;
					node.name = "dead "+node.name;
					node.interactString = "examine body";
				}
			}
		}else {
			randomLists.deadPerson();
			node.findBehind("body");
		}

	}
	
	@Override
	public DrawBane[] dbFinds() {
		return new DrawBane[] {DrawBane.GRAVE_DIRT,DrawBane.GRAVE_DUST,DrawBane.GARLIC,DrawBane.TELESCOPE};
	}

	@Override
	public void passTime(NodeConnector holder,int node, double time, TimeContext calling){
		//none yet
	}
	

}
