package trawel.personal.people.behaviors;

import java.util.Collections;
import java.util.List;

import trawel.personal.people.Agent;
import trawel.personal.people.Agent.AgentGoal;
import trawel.personal.people.Behavior;
import trawel.time.TimeEvent;
import trawel.towns.contexts.Town;
import trawel.towns.features.Feature.RemoveAgentFromFeatureEvent;

public class AbandonPostBehavior extends Behavior{
	
	public Town town;

	@Override
	public List<TimeEvent> action(Agent user) {
		if (town == null) {
			throw new RuntimeException("abandon post event has null town for user " + user.getPerson().getName());
		}
		RemoveAgentFromFeatureEvent event = town.laterRemoveAgentAnyFeature(user);
		if (event == null) {
			throw new RuntimeException("abandon post event is null for user " + user.getPerson().getName()+ " in " + town.getName());
		}
		user.onlyGoal(AgentGoal.NONE);
		return Collections.singletonList(event);
	}
}