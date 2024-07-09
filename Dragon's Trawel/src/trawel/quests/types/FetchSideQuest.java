package trawel.quests.types;

import derg.SRPlainRandom;
import derg.StringResult;
import trawel.Networking;
import trawel.factions.FBox;
import trawel.factions.Faction;
import trawel.helper.methods.extra;
import trawel.helper.methods.randomLists;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.events.QuestReactionFactory;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class FetchSideQuest extends BasicSideQuest {
	
	public FetchType subtype;

	public static enum FetchType{
		MERCHANT(new SRPlainRandom("supplies","goods","trade goods","luxury goods","documents","shipment","spice"),new QKey[] {}),
		CRIME(new SRPlainRandom("'taxes'","'spice'","letter","sealed letter","key"),new QKey[] {QKey.EVIL}),
		HERO(new SRPlainRandom("bandit reports","old war intel","silvered supplies"),new QKey[] {QKey.GOOD}),
		COMMUNITY(new SRPlainRandom("totem","heirloom","keepsake","letter","key"),new QKey[] {}),
		HUNTER(new SRPlainRandom("monster reports","silvered weapons","blessed wood"),new QKey[] {QKey.GOOD});
		
		public final StringResult itemList;
		public final QKey[] qAdds;
		FetchType(StringResult _itemList, QKey[] _qAdds){
			itemList = _itemList;
			qAdds = _qAdds;
		}
	}
	
	public static FetchSideQuest generate(Feature generator, FetchType subtype) {
		Town t = generator.getTown();
		
		FetchSideQuest q = new FetchSideQuest();
		q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
		q.targetName = subtype.itemList.next();
		q.qKeywords.add(QKey.FETCH);
		for (QKey add: subtype.qAdds) {
			q.qKeywords.add(add);
		}
		q.subtype = subtype;
		
		Feature targetFeature = ((QuestBoardLocation)generator).getQuestGoal();
		if (targetFeature == null) {
			targetFeature = generator;//fallback set to same place
		}
		
		q.resolveGive(generator);
		q.qRList.add(new QuestR(0,q.targetName,q,targetFeature));
		q.qRList.add(new QuestR(1,q.giverName,q,generator));
		q.resolveDest(targetFeature);
		
		q.name = q.giverName + "'s " + q.targetName;
		q.desc = "Fetch " + q.targetName + " from " + targetFeature.getName() + " in " + targetFeature.getTown().getName() + " for " + q.giverName;
		return q;
	}

	@Override
	public void questReaction(int QRID) {
		Feature endFeature = qRList.get(1).locationF;
		switch (QRID) {
		case 0:
			extra.println("You claim the " + this.targetName);
			this.desc = "Return the " + this.targetName + " to " + this.giverName + " at " + endFeature.getName() + " in " + endFeature.getTown().getName();
			advanceStage();
			announceUpdate();
			return;
		case 1:
			int reward;
			int atLevel = Math.min(Player.player.getPerson().getLevel(),endFeature.getLevel());
			float mult = IEffectiveLevel.unclean(atLevel);
			switch (subtype) {
			case COMMUNITY:
				reward = IEffectiveLevel.cleanRangeReward(atLevel,.5f, .5f);
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				endFeature.getTown().helpCommunity(2);//helps community twice as much
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*FBox.bonusLiked, 0);
				
				Networking.unlockAchievement("collectquest1");
				this.complete();
				Player.player.getPerson().addXp(atLevel);
				return;
			case CRIME:
				reward = IEffectiveLevel.cleanRangeReward(atLevel,1.8f,.3f);
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,mult*FBox.bonusDistant,0);
				Player.player.getPerson().facRep.addFactionRep(Faction.LAW_GOOD,0,mult*FBox.againstNear);
				Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,0,mult*FBox.againstDistant);
				//doesn't help community
				
				Networking.unlockAchievement("collectquest1");
				this.complete();
				Player.player.getPerson().addXp(atLevel);
				return;
			case HERO:
				reward = IEffectiveLevel.cleanRangeReward(atLevel,.8f,.7f);
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				endFeature.getTown().helpCommunity(1);
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*FBox.bonusFavored, 0);
				
				Networking.unlockAchievement("collectquest1");
				this.complete();
				Player.player.getPerson().addXp(atLevel);
				return;
			case MERCHANT:
				reward = IEffectiveLevel.cleanRangeReward(atLevel,1.3f,.7f);
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				Player.player.getPerson().facRep.addFactionRep(Faction.MERCHANT,mult*FBox.bonusLiked, 0);
				Player.player.addMPoints(.2f);
				endFeature.getTown().helpCommunity(1);
				
				Networking.unlockAchievement("collectquest1");
				this.complete();
				Player.player.getPerson().addXp(atLevel);
				return;
			case HUNTER:
				//gives amber instead
				reward = IEffectiveLevel.cleanRangeReward(atLevel,Gem.AMBER.reward(1f,true),.7f);
				Gem.AMBER.changeGem(reward);
				extra.println("Gained "+reward+" amber.");
				endFeature.getTown().helpCommunity(1);
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,mult*FBox.bonusLiked, 0);
				
				Networking.unlockAchievement("collectquest1");
				this.complete();
				Player.player.getPerson().addXp(atLevel);
				return;
			}
		}
		throw new RuntimeException("Invalid QRID for fetch quest");
	}
	
	@Override
	public void take() {
		setStage(0);
		announceUpdate();
	}
}
