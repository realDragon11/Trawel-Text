package trawel.personal.people.behaviors;

import java.util.List;

import trawel.extra;
import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Behavior;
import trawel.time.TimeEvent;
import trawel.towns.nodes.NodeFeature;

public class GateNodeBehavior extends Behavior {

	public boolean opened;
	public final int type;
	public final NodeFeature location;
	public final Agent owner; 
	
	public GateNodeBehavior(Agent user, NodeFeature location,int type) {
		this.type = type;
		owner = user;
		this.location = location;
		opened = false;
		setTimeTo(Double.NaN);
	}
	
	public GateNodeBehavior(Agent user, NodeFeature location) {
		this.type = extra.randRange(0,1);
		owner = user;
		this.location = location;
		opened = false;
		setTimeTo(Double.NaN);
	}
	
	public String getName() {
		return owner.getPerson().getName();
	}
	
	public String getOpenedText() {
		switch (type) {
		case 0:
			return "This Bone Barrier is open for you, forming a path.";
		case 1:
			return "This Hidden Stairwell has been dug up, paving the way forward.";
		case 2:
			return "This Abandoned Harpy Nest has had a door revealed and opened, allowing you to advance.";
		}
		return "ERROR";
	}
	public String getOpeningText() {
		switch (type) {
		case 0:
			return "The Bone Barrier makes way in response to your victory against "+getName()+" .";
		case 1:
			return "You find a Hidden Stairwell digging up a spot marked a map "+getName() +" had.";
		case 2:
			return "You find and open a Secret Door using instructions on "+getName() +"'s key.";
		}
		return "ERROR";
	}
	public String getLockedText() {
		switch (type) {
		case 0:
			return "A Bone Barrier blocks the path forward. You hear a subtle chittering deep within: Slay "+getName()+" to prove your worth...";
		case 1:
			return "There is an area covered with potholes. You hear a faint whisper from below the earth: Slay "+getName()+" for the map...";
		case 2:
			return "There is an abandoned Harpy Nest here. You hear a quiet chirping from the nest: Slay "+getName()+" for a key...";
		}
		return "ERROR";
	}
	
	public String getOpenName() {
		switch (type) {
		case 0:
			return "Bone Path";
		case 1:
			return "Hidden Stairwell";
		case 2:
			return "Nested Secret Door";
		}
		return "ERROR";
	}
	public String getLockedName() {
		switch (type) {
		case 0:
			return "Bone Barrier";
		case 1:
			return "Pothole Clearing";
		case 2:
			return "Abandoned Harpy Nest";
		}
		return "ERROR";
	}
	
	public String getOpenInteract() {
		switch (type) {
		case 0:
			return "Examine Bone Path.";
		case 1:
			return "Examine Hidden Stairwell.";
		case 2:
			return "Examine Abandoned Harpy Nest.";
		}
		return "ERROR";
	}
	
	public String getLockedInteract() {
		switch (type) {
		case 0:
			return "Test Bone Barrier.";
		case 1:
			return "Test Pothole Clearing.";
		case 2:
			return "Test Abandoned Harpy Nest.";
		}
		return "ERROR";
	}
	
	public void printChallengeAgent() {
		extra.print(extra.PRE_BATTLE);
		switch (type) {
			default:
				extra.println(owner.getPerson().getName() + " appears to challenge you!");break;
			case 0:
				extra.println(owner.getPerson().getName() + " tests if you are worthy of the booty!");break;
			case 1:
				extra.println(owner.getPerson().getName() + " has come to collect treasure!");break;
			case 2:
				extra.println(owner.getPerson().getName() + " wants your shinies for their nest!");break;
		}
	}
	
	public void printSlayAgent() {
		extra.print(location.getName() + " in " + location.getTown().getName() + " has a new secret for you: ");
		switch (type) {
			default:
				extra.println("ERROR");break;
			case 0:
				extra.println("A Bone Barrier will now open for you.");break;
			case 1:
				extra.println("This map will reveal which hole to dig up.");break;
			case 2:
				extra.println("This key will show a secret door in a harpy nest.");break;
		}
	}
	
	public boolean checkOpened() {
		//location.getTown().getIsland().getWorld().hasReoccuring(owner) //would also detect deathcheating or the like
		if (owner.hasGoal(AgentGoal.WORLD_ENCOUNTER)) {
			//still closed
			return false;
		}else {
			//open up
			opened = true;
			return true;
		}
	}
	
	public String getNodeName() {
		return opened ? getOpenName() : getLockedName();
	}
	
	public String getInteractText() {
		return opened ? getOpenInteract() : getLockedInteract();
	}

	@Override
	public List<TimeEvent> action(Agent user) {
		throw new RuntimeException(user.getPerson().getName() + " activated their gate note behavior!" + this.toString());
	}

}
