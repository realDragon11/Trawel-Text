package trawel.quests;

import trawel.extra;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.factions.Faction;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
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
		
		Feature targetFeature = generator;//fallback set to same place
		
		for (int i = extra.randRange(3, 5); i >= 0;i--) {
			targetFeature = extra.randList(t.getQuestLocationsInRange(i));
			if (targetFeature != generator) {
				break;
			}
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
					if (isMurder) {
						Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,0,1f);
						Player.player.getPerson().facRep.addFactionRep(Faction.LAW_EVIL,.5f, 0);
					}else {
						Player.player.getPerson().facRep.addFactionRep(Faction.HEROIC,.5f, 0);
						Player.player.getPerson().facRep.addFactionRep(Faction.LAW_GOOD,.5f, 0);
					}
				}
			}
			return;
		case 1:
			int reward;
			if (isMurder) {
				reward = Math.round(target.getLevel()/2);
			}else {
				reward = Math.round(target.getLevel()/3);
				endFeature.getTown().helpCommunity(2);
			}
			if (endFeature instanceof MerchantGuild) {
				Player.player.addMPoints(.2);
			}
			Player.player.getPerson().addXp(reward);
			Player.player.addGold(reward);
			extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
			complete();
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
