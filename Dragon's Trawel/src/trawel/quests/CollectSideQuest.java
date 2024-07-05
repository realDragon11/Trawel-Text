package trawel.quests;

import java.util.Collections;
import java.util.List;

import trawel.Networking;
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
			q.qKeywords.add(QKey.TRADE_ALIGN);
			break;
		case TELESCOPE:
			q.count = 8;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			q.qKeywords.add(QKey.TRADE_ALIGN);
			break;
		case REPEL:
			q.count = 4;
			q.qKeywords.add(QKey.TRADE_ALIGN);
			break;
		case CEON_STONE:
			q.count = 6;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			q.qKeywords.add(QKey.TRADE_ALIGN);
			q.qKeywords.add(QKey.TRANSMUTE_ALIGN);
			break;
		case PROTECTIVE_WARD:
			q.count = 8;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			q.qKeywords.add(QKey.TRANSMUTE_ALIGN);
			break;
		case SILVER:
			q.count = 4;
			q.qKeywords.add(QKey.TRADE_ALIGN);
			q.qKeywords.add(QKey.TRANSMUTE_ALIGN);
			break;
		case GOLD:
			q.count = 6;
			q.qKeywords.add(QKey.TRADE_ALIGN);
			q.qKeywords.add(QKey.TRANSMUTE_ALIGN);
			break;
		case UNICORN_HORN:
			q.count = 8;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			q.qKeywords.add(QKey.TRADE_ALIGN);
			q.qKeywords.add(QKey.TRANSMUTE_ALIGN);
			break;
		case KNOW_FRAG:
			q.count = 20;
			q.qKeywords.add(QKey.KNOW_ALIGN);//can get this from turning in fragments, but not that many
			break;
		case PUMPKIN:
			q.count = 3;
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case MEAT:
			q.count = 2;
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case BAT_WING:
			q.count = 2;
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case APPLE:
			q.count = 2;
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case MIMIC_GUTS:
			q.count = 3;
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case BLOOD:
			q.count = 1;
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case WAX:
			q.count = 2;
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case WOOD:
			q.count = 2;
			q.qKeywords.add(QKey.TRADE_ALIGN);
			break;
		case VIRGIN://assembled
			q.count = 12;
			q.qKeywords.add(QKey.TRANSMUTE_ALIGN);
			break;
		case GRAVE_DUST:
			q.count = 7;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case BEATING_HEART:
			q.count = 14;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			q.qKeywords.add(QKey.BREW_ALIGN);
			break;
		case SINEW:
			q.count = 3;
			q.qKeywords.add(QKey.KNOW_ALIGN);
			q.qKeywords.add(QKey.BREW_ALIGN);
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
		
		q.qRList.add(new QuestR(0,q.giverName,q,generator));
		
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
			Networking.unlockAchievement("collectquest1");
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
