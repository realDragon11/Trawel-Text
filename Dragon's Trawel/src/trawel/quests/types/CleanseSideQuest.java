package trawel.quests.types;

import trawel.core.Networking;
import trawel.core.Print;
import trawel.factions.FBox;
import trawel.factions.Faction;
import trawel.helper.methods.randomLists;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.locations.QuestR;
import trawel.towns.contexts.World;
import trawel.towns.features.Feature;

public class CleanseSideQuest extends BasicSideQuest {
	public CleanseType subtype;
	public int count = 0;
	public boolean completed = false;

	public static enum CleanseType{
		BEAR("bears","bear",3,new QKey[] {QKey.LAWFUL}),
		VAMPIRE("vampires","vampire",2,new QKey[] {QKey.GOOD}),
		WOLF("wolves","wolf",6,new QKey[] {QKey.LAWFUL}),
		HARPY("harpies","harpy",4,new QKey[] {}),
		BANDIT("bandits","bandit",4,new QKey[] {QKey.LAWFUL,QKey.GOOD}),
		UNICORN("unicorns","unicorn",1,new QKey[] {QKey.EVIL}),
		/**
		 * note that drudgers will now now count dock drudgers so number is high
		 */
		DRUDGER("drudgers","drudger",10,new QKey[] {QKey.LAWFUL,QKey.GOOD}),
		FELL("fell creatures","fell",3,new QKey[] {QKey.GOOD}),
		ANIMALS("animals","animal",12,new QKey[] {QKey.LAWFUL}),
		MONSTERS("monsters","monster",10,new QKey[] {QKey.GOOD}),
		;
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
		q.desc = "Kill " + q.count + " more " + q.targetName + " for " + q.giverName;
		
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
			desc = "Kill " + count + " more " + targetName + " for " + giverName;
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
					Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,mult*FBox.bonusLiked,0);
				}else {
					Player.player.getPerson().facRep.addFactionRep(Faction.LAW_GOOD,mult*FBox.bonusLiked,0);
					Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,mult*FBox.bonusLiked,0);
				}
			}
			
			//who offered it
			if (qKeywords.contains(QKey.GIVE_HUNT_GUILD)) {//hunter source
				if (!qKeywords.contains(QKey.EVIL)) {
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*FBox.bonusLiked,0);
				}
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*FBox.bonusFavored,0);
				reward = IEffectiveLevel.cleanRangeReward(atLevel,2f,.3f);
				endFeature.getTown().helpCommunity(1);
				//also grants amber
				int a_reward = IEffectiveLevel.cleanRangeReward(atLevel,Gem.AMBER.reward(2f,true),.6f);
				Gem.AMBER.changeGem(a_reward);
				Print.println("You gained " +a_reward+" amber.");
			}
			if (qKeywords.contains(QKey.GIVE_HERO_GUILD)) {//hero source
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*FBox.bonusFavored,0);
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*FBox.bonusLiked,0);
				reward = IEffectiveLevel.cleanRangeReward(atLevel,3f,.6f);
				endFeature.getTown().helpCommunity(2);
			}
			if (qKeywords.contains(QKey.GIVE_MERCHANT_GUILD)) {//merchant source
				Player.player.getPerson().facRep.addFactionRep(Faction.MERCHANT,mult*FBox.bonusFavored,0);
				if (!qKeywords.contains(QKey.EVIL)) {
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*FBox.bonusLiked,0);
				}
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*FBox.bonusLiked,0);
				reward = IEffectiveLevel.cleanRangeReward(atLevel,4f,.7f);
				endFeature.getTown().helpCommunity(1);
				Player.player.addMPoints(.2);
			}
			if (qKeywords.contains(QKey.GIVE_INN) || qKeywords.contains(QKey.GIVE_SLUM)) {//community given
				//more reputation
				if (!qKeywords.contains(QKey.EVIL)) {
					Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*FBox.bonusLiked,0);
				}
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*FBox.bonusLiked,0);
				//much more community help but less normal rewards
				endFeature.getTown().helpCommunity(4);
				reward = IEffectiveLevel.cleanRangeReward(atLevel,1f,.6f);
			}
			Player.player.addGold(reward);
			Print.println("Gained "+World.currentMoneyDisplay(reward)+".");
			
			Networking.unlockAchievement("huntquest1");
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
