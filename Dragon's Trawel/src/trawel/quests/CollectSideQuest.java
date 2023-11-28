package trawel.quests;

import java.util.Collections;
import java.util.List;

import trawel.extra;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory.QKey;
import trawel.towns.Feature;
import trawel.towns.fort.FortHall;
import trawel.towns.services.WitchHut;

public class CollectSideQuest extends BasicSideQuest {
	
	DrawBane collect;
	private int count;
	private boolean completed;
	
	public static CollectSideQuest generate(Feature generator, DrawBane collect) {
		CollectSideQuest q = new CollectSideQuest();
		q.collect = collect;
		q.targetName = collect.getName();
		q.resolveGive(generator);
		q.qKeywords.add(QKey.COLLECT);
		
		switch (collect) {
		case LIVING_FLAME:
			q.count = 12;
			q.qKeywords.add(QKey.FIRE_ALIGN);
			break;
		case TELESCOPE:
			q.count = 8;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			break;
		case REPEL:
			q.count = 4;
			break;
		case CEON_STONE:
			q.count = 6;
			break;
		case PROTECTIVE_WARD:
			q.count = 8;
			break;
		case SILVER:
			q.count = 4;
			break;
		case GOLD:
			q.count = 6;
			break;
		case UNICORN_HORN:
			q.count = 8;
			break;
		case KNOW_FRAG:
			q.count = 20;
			q.qKeywords.add(QKey.KNOW_ALIGN);//can get this from turning in fragments, but not that many
			break;
		case PUMPKIN:
			q.count = 3;
			break;
		case MEAT:
			q.count = 2;
			break;
		case BAT_WING:
			q.count = 2;
			break;
		case APPLE:
			q.count = 2;
			break;
		case MIMIC_GUTS:
			q.count = 3;
			break;
		case BLOOD:
			q.count = 1;
			break;
		case WAX:
			q.count = 2;
			break;
		case WOOD:
			q.count = 2;
			break;
		case VIRGIN://lmao
			q.count = 12;
			break;
		}
		q.desc = "Collect " + q.count +" "+ q.targetName + " pieces";
		
		if (generator instanceof WitchHut) {
			q.giverName = generator.getName() + " (Personal Collection)";
			q.name = "Gather " + q.targetName + " (Personal)";
		}
		if (generator instanceof FortHall) {
			q.giverName = generator.getName() + " Command";
			q.name = "Gather " + q.targetName + " (Personal)";
		}
		
		return q;
	}
	
	@Override
	public List<String> triggers() {
		return Collections.singletonList("db:"+collect.getName());
	}
	
	@Override
	public void questTrigger(TriggerType type, String trigger, int num) {
		if (type != TriggerType.COLLECT) {
			return;
		}
		if (!trigger.equals(collect.name())) {
			return;
		}
		count -=num;
		if (count <=0) {
			if (completed == false) {
				Feature endFeature = qRList.get(0).locationF;
				desc = "Return to " + endFeature.getName() +" in "+endFeature.getTown().getName() + " to assemble the " + targetName;
				setStage(0);
				announceUpdate();
				completed = true;
			}
		}else {
			desc = "Collect " + count + " more " + targetName + " pieces";
		}
	}
	
	@Override
	public void questReaction(int QRID) {
		Feature endFeature = qRList.get(0).locationF;
		switch (QRID) {
		case 0:
			extra.println("You assemble a whole " + targetName);
			Player.bag.addNewDrawBanePlayer(collect);
			complete();
			return;
		}
		throw new RuntimeException("Invalid QRID for collect quest");
	}
	
	@Override
	public void take() {
		announceUpdate();
	}

}
