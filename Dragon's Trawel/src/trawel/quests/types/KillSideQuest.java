package trawel.quests.types;

import trawel.Networking;
import trawel.battle.Combat;
import trawel.factions.FBox;
import trawel.factions.Faction;
import trawel.helper.methods.extra;
import trawel.helper.methods.randomLists;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.item.solid.Gem;
import trawel.personal.people.Player;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.locations.QuestBoardLocation;
import trawel.quests.locations.QuestR;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;

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
			q.target = RaceFactory.makePeace(generator.getLevel());
		}else {
			q.qKeywords.add(QKey.LAWFUL);
			q.target = RaceFactory.makeMugger(generator.getLevel());
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
			float rewardMult = 2f;
			int reward;
			if (qKeywords.contains(QKey.GIVE_HUNT_GUILD)) {//hunter guild
				int amber = IEffectiveLevel.cleanRangeReward(target.getLevel(),Gem.AMBER.reward(.5f,true),.7f);
				Gem.AMBER.changeGem(amber);
				extra.println("Gained "+amber+" amber.");
			}
			if (qKeywords.contains(QKey.GIVE_ROGUE_GUILD)) {//rogue guild
				//more money only
				rewardMult *=2f;
			}
			if (qKeywords.contains(QKey.GIVE_MERCHANT_GUILD)) {//merchant
				Player.player.addMPoints(.2);
				rewardMult *=1.5f;
			}
			//community source
			if (qKeywords.contains(QKey.GIVE_INN) || qKeywords.contains(QKey.GIVE_HERO_GUILD) || qKeywords.contains(QKey.GIVE_SLUM)) {
				//still counts as some help, even if murder, or slightly more if not
				endFeature.getTown().helpCommunity(1);
			}
			if (isMurder) {
				reward = IEffectiveLevel.cleanRangeReward(target.getLevel(),rewardMult*2f,.5f);
			}else {
				reward = IEffectiveLevel.cleanRangeReward(target.getLevel(),rewardMult,.8f);
				endFeature.getTown().helpCommunity(2);
			}
			Player.player.addGold(reward);
			extra.println("Gained "+World.currentMoneyDisplay(reward)+".");
			
			Networking.unlockAchievement("huntquest1");
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
