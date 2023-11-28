package trawel.quests;

import derg.SRPlainRandom;
import derg.StringResult;
import trawel.extra;
import trawel.randomLists;
import trawel.factions.Faction;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory.QKey;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

public class FetchSideQuest extends BasicSideQuest {
	
	public FetchType subtype;

	public static enum FetchType{
		MERCHANT(new SRPlainRandom("supplies","goods","trade goods","documents")),
		CRIME(new SRPlainRandom("'taxes'","spice","letter","sealed letter","key")),
		HERO(new SRPlainRandom("sword of ultimate fate")),
		COMMUNITY(new SRPlainRandom("totem","heirloom","keepsake","letter","key"));
		
		public final StringResult itemList;
		FetchType(StringResult _itemList){
			itemList = _itemList;
		}
	}
	
	public static FetchSideQuest generate(Feature generator, FetchType subtype) {
		Town t = generator.getTown();
		
		FetchSideQuest q = new FetchSideQuest();
		q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
		q.targetName = subtype.itemList.next();
		q.qKeywords.add(QKey.FETCH);
		
		Feature targetFeature = generator;//fallback set to same place
		
		for (int i = 1; i < 5;i++) {
			targetFeature = extra.randList(t.getQuestLocationsInRange(i));
			if (targetFeature != generator) {
				break;
			}
		}
		
		q.qRList.add(new QuestR(0,q.targetName,q,targetFeature));
		q.qRList.add(new QuestR(1,q.giverName,q,generator));
		
		
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
			qRList.get(1).locationF.addQR(qRList.get(1));
			this.desc = "Return the " + this.targetName + " to " + this.giverName + " at " + endFeature.getName() + " in " + endFeature.getTown().getName();
			this.cleanup();
			this.announceUpdate();
			return;
		case 1:
			switch (subtype) {
			case COMMUNITY:
				Player.player.getPerson().addXp(1);
				Player.player.addGold(1);
				extra.println("Gained "+World.currentMoneyDisplay(1)+".");
				endFeature.getTown().helpCommunity(1);
				Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,.1f, 0);
				this.complete();
				break;
			case CRIME:
				break;
			case HERO:
				break;
			case MERCHANT:
				break;
			}
			return;
		}
		throw new RuntimeException("Invalid QRID for fetch quest");
	}
}
