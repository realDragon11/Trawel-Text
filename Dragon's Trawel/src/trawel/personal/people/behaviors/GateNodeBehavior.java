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
		}
		return "ERROR";
	}
	public String getOpeningText() {
		switch (type) {
		case 0:
			return "The Bone Barrier makes way in response to your victory against "+getName()+" .";
		}
		return "ERROR";
	}
	public String getLockedText() {
		switch (type) {
		case 0:
			return "A Bone Barrier blocks the path forward. You hear a subtle chittering deep within: Slay "+getName()+" to prove your worth...";
		}
		return "ERROR";
	}
	
	public String getOpenName() {
		switch (type) {
		case 0:
			return "Bone Path";
		}
		return "ERROR";
	}
	public String getLockedName() {
		switch (type) {
		case 0:
			return "Bone Barrier";
		}
		return "ERROR";
	}
	
	public String getOpenInteract() {
		switch (type) {
		case 0:
			return "Examine Bone Path.";
		}
		return "ERROR";
	}
	
	public String getLockedInteract() {
		switch (type) {
		case 0:
			return "Test Bone Barrier.";
		}
		return "ERROR";
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
		return null;
	}

}
