package trawel.quests;

import trawel.extra;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.factions.FBox;
import trawel.factions.Faction;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory.QKey;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;
import trawel.towns.services.MerchantGuild;

public class KillSideQuest extends BasicSideQuest {
	
	public boolean isMurder;
	public Person target;
	
	public static KillSideQuest generate(Feature generator, boolean isMurder) {
		Town t = generator.getTown();
		
		KillSideQuest q = new KillSideQuest();
		q.isMurder = isMurder;
		q.resolveGive(generator);
		q.qKeywords.add(QKey.KILL);
		if (isMurder) {
			q.qKeywords.add(QKey.EVIL);
			q.target = RaceFactory.getPeace(generator.getLevel());
		}else {
			q.qKeywords.add(QKey.LAWFUL);
			q.target = RaceFactory.getMugger(generator.getLevel());
		}
		
		q.giverName = randomLists.randomFirstName() + " " +  randomLists.randomLastName();
		q.targetName = q.target.getName();
		
		Feature targetFeature = ((QuestBoardLocation)generator).getQuestGoal();
		if (targetFeature == null) {
			targetFeature = generator;//fallback set to same place
		}
		
		q.qRList.add(new QuestR(0,q.targetName,q,targetFeature));
		q.qRList.add(new QuestR(1,q.giverName,q,generator));
		
		if (isMurder) {
			q.name = "Murder " + q.targetName + " for " + q.giverName;
			q.desc = "Murder " + q.targetName + " at " + targetFeature.getName() + " in " + targetFeature.getTown().getName() + " for " + q.giverName;
		}else {
			q.name = "Execute " + q.targetName + " for " + q.giverName;
			q.desc = "Execute " + q.targetName + " at " + targetFeature.getName() + " in " + targetFeature.getTown().getName() + " for " + q.giverName;
		}
		
		q.resolveDest(targetFeature);
		
		return q;
	}
	
	@Override
	public void questReaction(int QRID) {
		Feature endFeature = qRList.get(1).locationF;
		switch (QRID) {
		case 0:
			if (target.reallyAttack()) {
				Combat c = Player.player.fightWith(target);
				if (c.playerWon() > 0) {
					desc = "Return to " + giverName + " at " + endFeature.getName() + " in " + endFeature.getTown().getName();
					this.advanceStage();
					announceUpdate();
					float mult = target.getUnEffectiveLevel();
					if (isMurder) {
						Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,0,mult*FBox.againstNear);
						Player.player.getPerson().facRep.addFactionRep(Faction.LAW_GOOD,0,mult*FBox.againstClose);
						Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,mult*FBox.bonusTiny, 0);
						Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,mult*FBox.bonusTiny,0);
					}else {
						Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,mult*FBox.bonusDistant, 0);
						Player.player.getPerson().facRep.addFactionRep(Faction.LAW_GOOD,mult*FBox.bonusLiked, 0);
						Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,mult*FBox.bonusFavored, 0);
						Player.player.getPerson().facRep.addFactionRep(Faction.ROGUE,0,mult*FBox.againstNear);
					}
				}
			}
			return;
		case 1:
			int reward;
			if (isMurder) {
				reward = IEffectiveLevel.cleanRangeReward(target.getLevel(),4f,.5f);
			}else {
				reward = IEffectiveLevel.cleanRangeReward(target.getLevel(),2f,.8f);
				endFeature.getTown().helpCommunity(2);
			}
			if (qKeywords.contains(QKey.GIVE_MGUILD)) {
				Player.player.addMPoints(.2);
			}
			Player.player.addGold(reward);
			extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
			
			complete();
			Player.player.getPerson().addXp(target.getLevel());
			return;
		}
		throw new RuntimeException("Invalid QRID for kill quest");
	}
	
	@Override
	public void take() {
		setStage(0);
		announceUpdate();
	}

}
