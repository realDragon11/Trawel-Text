package trawel.personal.people.behaviors;

import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Behavior;

public class AbandonPostBehavior extends Behavior{

	@Override
	public void action(Agent user) {
		boolean removed = user.getLocation().removeAgentFromFeatures(user);
		assert removed;
		user.onlyGoal(AgentGoal.NONE);
		user.getLocation().addOccupant(user);
	}

}
