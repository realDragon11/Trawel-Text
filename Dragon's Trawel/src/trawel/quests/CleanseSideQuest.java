package trawel.quests;

import trawel.extra;
import trawel.randomLists;
import trawel.factions.Faction;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.Gem;
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
		BEAR("bears","bear",3,new QKey[] {QKey.LAWFUL}),
		VAMPIRE("vampires","vampire",2,new QKey[] {QKey.GOOD}),
		WOLF("wolves","wolf",6,new QKey[] {QKey.LAWFUL}),
		HARPY("harpies","harpy",4,new QKey[] {}),
		BANDIT("bandits","bandit",3,new QKey[] {QKey.LAWFUL,QKey.GOOD}),
		UNICORN("unicorns","unicorn",1,new QKey[] {QKey.EVIL});
		public final String fluff, trigger;
		public final int count;
		public final QKey[] qAdds;
		CleanseType(String _fluff, String _trigger, int _count, QKey[] _qAdds){
			fluff = _fluff;
			trigger = _trigger;
			count = _count;
			qAdds = _qAdds;
		}
	}
	
	public static CleanseSideQuest generate(Feature generator, CleanseType subtype) {
		CleanseSideQuest q = new CleanseSideQuest();
		q.subtype = subtype;
		q.targetName = subtype.fluff;
		
		q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
		
		q.resolveGive(generator);
		q.qKeywords.add(QKey.CLEANSE);
		for (QKey add: subtype.qAdds) {
			q.qKeywords.add(add);
		}
		q.count = subtype.count;
		
		q.name = "Kill " + q.targetName + " for " + q.giverName ;
		q.desc = "Kill " + q.count + " more " + q.targetName + " on the roads for " + q.giverName;
		
		q.qRList.add(new QuestR(0,q.giverName,q,generator));
		
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
			int atLevel = Math.min(Player.player.getPerson().getLevel(),endFeature.getLevel());
			float mult = IEffectiveLevel.unclean(atLevel);
			int reward = 0;
			if (qKeywords.contains(QKey.LAWFUL)) {
				//if lawful, the law likes it
				if (qKeywords.contains(QKey.EVIL)) {
					Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,mult*0.05f,0);
				}else {
					Player.player.getPerson().facRep.addFactionRep(Faction.LAW_GOOD,mult*0.05f,0);
					Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,mult*0.05f,0);
				}
			}
			
			//who offered it
			if (qKeywords.contains(QKey.GIVE_HUNT_GUILD)) {
				if (!qKeywords.contains(QKey.EVIL)) {
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*0.05f,0);
				}
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*1f,0);
				reward = IEffectiveLevel.cleanRangeReward(atLevel,2f,.3f);
				endFeature.getTown().helpCommunity(1);
				//also grants amber
				int a_reward = IEffectiveLevel.cleanRangeReward(atLevel,2.5f,.6f);
				Gem.AMBER.changeGem(a_reward);
				extra.println("You gained " +a_reward+" amber.");
			}
			if (qKeywords.contains(QKey.GIVE_HGUILD)) {
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*1f,0);
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*0.05f,0);
				reward = IEffectiveLevel.cleanRangeReward(atLevel,3f,.6f);
				endFeature.getTown().helpCommunity(2);
			}
			if (qKeywords.contains(QKey.GIVE_MGUILD)) {
				if (!qKeywords.contains(QKey.EVIL)) {
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*0.05f,0);
				}
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*0.05f,0);
				reward = IEffectiveLevel.cleanRangeReward(atLevel,4f,.7f);
				endFeature.getTown().helpCommunity(1);
				Player.player.addMPoints(.2);
			}
			Player.player.addGold(reward);
			extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
			
			complete();
			Player.player.getPerson().addXp(atLevel);//xp scales weirdly
			return;
		}
		throw new RuntimeException("Invalid QRID for cleanse quest");
	}
	
	@Override
	public void take() {
		announceUpdate();
	}
}
