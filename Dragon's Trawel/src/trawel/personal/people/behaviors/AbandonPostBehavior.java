package trawel.personal.people.behaviors;

import java.util.Collections;
import java.util.List;

import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.time.TimeEvent;
import trawel.personal.people.Behavior;
import trawel.towns.Feature.RemoveAgentFromFeatureEvent;

public class AbandonPostBehavior extends Behavior{

	@Override
	public List<TimeEvent> action(Agent user) {
		RemoveAgentFromFeatureEvent event = user.getLocation().laterRemoveAgentAnyFeature(user);
		assert event != null;
		user.onlyGoal(AgentGoal.NONE);
		return Collections.singletonList(event);
	}

}
