package trawel;


import java.util.ArrayList;

public class EventQuene {
	
	public static ArrayList<Event> list = new ArrayList<Event>();
	
	public void grab() {
		//first, we condense them
		condense();
		printStack();
	}
	
	public void place(Event e) {
		list.add(e);
	}

	private void printStack() {
		for (Event e: list) {
			((EventParagraph)e).display();
		}
		emptyStack();
		
	}

	private void emptyStack() {
		list = new ArrayList<Event>();
	}

	private void condense() {
		// if still has non paragraphs, condese further
		//start with larger condense blocks
		
		//working list
		ArrayList<Event> list2 = new ArrayList<Event>();
		//condense main
		//detect condense blocks in order, skip paragraphs
		
		if (list.size() >= 2) {
		attackHit();
		}
		
		
		
		list = list2;
		boolean hasNon= false;
		for (Event e: list) {
			if (!EventParagraph.class.isInstance(e)) {
				hasNon = true;
				break;
			}
		}
		if (hasNon) {
		condense();}
	}

	private void attackHit() {
		if (!EventAttack.class.isInstance(list.get(0)) && !EventHit.class.isInstance(list.get(1))){
			return;
		}	
		switch(extra.randRange(0,0)) {
		case 0:
			
		}
	}

}
