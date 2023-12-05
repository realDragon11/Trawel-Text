package trawel.quests;

import derg.SRPlainRandom;
import derg.StringResult;
import trawel.extra;
import trawel.randomLists;
import trawel.factions.Faction;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory.QKey;
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
			switch (subtype) {
			case COMMUNITY:
				Player.player.getPerson().addXp(1);
				reward =  Math.max(1,(int)endFeature.getUnEffectiveLevel()/5);
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				endFeature.getTown().helpCommunity(2);
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,.1f, 0);
				this.complete();
				return;
			case CRIME:
				reward = Math.max(1,(int)endFeature.getUnEffectiveLevel());
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,.4f,0);
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,0, .05f);
				//doesn't help community
				this.complete();
				return;
			case HERO:
				Player.player.getPerson().addXp(1);
				reward =  Math.max(1,(int)endFeature.getUnEffectiveLevel()/3);
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				endFeature.getTown().helpCommunity(1);
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,.2f, 0);
				this.complete();
				return;
			case MERCHANT:
				Player.player.getPerson().facRep.addFactionRep(Faction.MERCHANT,.1f, 0);
				Player.player.addMPoints(.2f);
				Player.player.getPerson().addXp(1);
				reward =  Math.max(1,(int)endFeature.getUnEffectiveLevel());
				Player.player.addGold(reward);
				extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
				endFeature.getTown().helpCommunity(1);
				this.complete();
				return;
			case HUNTER:
				Player.player.getPerson().addXp(1);
				reward =  Math.max(1,(int)endFeature.getUnEffectiveLevel()/3);
				Gem.AMBER.changeGem(reward);
				extra.println("Gained "+reward+" amber.");
				endFeature.getTown().helpCommunity(1);
				Player.player.getPerson().facRep.addFactionRep(Faction.HUNTER,.15f, 0);
				this.complete();
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
