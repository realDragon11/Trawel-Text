package trawel.quests;

import trawel.extra;
import trawel.randomLists;
import trawel.factions.Faction;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory.QKey;
import trawel.towns.Feature;
import trawel.towns.World;
import trawel.towns.services.MerchantGuild;

public class CleanseSideQuest extends BasicSideQuest {
	public CleanseType subtype;
	public int count = 0;
	public boolean completed = false;

	public static enum CleanseType{
		BEAR("bears","bear",3),
		VAMPIRE("vampires","vampire",2),
		WOLF("wolves","wolf",6),
		HARPY("harpies","harpy",4),
		BANDIT("bandits","bandit",3);
		public final String fluff, trigger;
		public final int count;
		CleanseType(String _fluff, String _trigger, int _count){
			fluff = _fluff;
			trigger = _trigger;
			count = _count;
		}
	}
	
	public static CleanseSideQuest generate(Feature generator, CleanseType subtype) {
		CleanseSideQuest q = new CleanseSideQuest();
		q.subtype = subtype;
		q.targetName = subtype.fluff;
		
		q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
		
		q.qKeywords.add(QKey.CLEANSE);
		q.qKeywords.add(QKey.LAWFUL);
		q.count = subtype.count;
		
		q.name = "Kill " + q.targetName + " for " + q.giverName ;
		q.desc = "Kill " + q.count + " more " + q.targetName + " on the roads for " + q.giverName;
		
		return q;
	}
	
	@Override
	public void questTrigger(TriggerType type, String trigger, int num) {
		if (type != TriggerType.CLEANSE) {
			return;
		}
		if (!trigger.equals(subtype.trigger)) {
			return;
		}
		count -=num;
		if (count <= 0) {
			if (completed == false) {
				Feature endFeature = qRList.get(0).locationF;
				desc = "Return to " + giverName + " at " + endFeature.getName() + " in " + endFeature.getTown().getName();
				setStage(0);
				announceUpdate();
				completed = true;
			}
		}else {
			desc = "Kill " + count + " more " + targetName + " on the roads for " + giverName;
		}
	}
	
	@Override
	public void questReaction(int QRID) {
		Feature endFeature = qRList.get(0).locationF;
		switch (QRID) {
		case 0:
			int reward = Math.min(Player.player.getPerson().getLevel(),endFeature.getLevel());//TODO: fix money value uneffective
			Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,2,0);
			Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,1,0);
			Player.player.getPerson().addXp(reward);
			Player.player.addGold(reward);
			extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
			if (endFeature instanceof MerchantGuild) {
				Player.player.addMPoints(.2);
				endFeature.getTown().helpCommunity(1);
			}else {
				endFeature.getTown().helpCommunity(2);
			}
			complete();
			return;
		}
		throw new RuntimeException("Invalid QRID for cleanse quest");
	}
	
	@Override
	public void take() {
		announceUpdate();
	}
}
