package trawel.quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import derg.menus.MenuGenerator;
import derg.menus.MenuItem;
import derg.menus.MenuSelect;
import trawel.extra;
import trawel.randomLists;
import trawel.battle.Combat;
import trawel.factions.Faction;
import trawel.personal.Person;
import trawel.personal.RaceFactory;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.QuestReactionFactory.QKey;
import trawel.towns.Feature;
import trawel.towns.Town;
import trawel.towns.World;
import trawel.towns.fight.Slum;
import trawel.towns.fort.FortHall;
import trawel.towns.services.Inn;
import trawel.towns.services.MerchantGuild;
import trawel.towns.services.WitchHut;

public abstract class BasicSideQuest implements Quest{

	private static final long serialVersionUID = 1L;

	public List<QuestR> qRList = new ArrayList<QuestR>();
	/**
	 * stage is used to determine which qR is active
	 * <br>
	 * otherwise it will be at -1
	 */
	public int stage = -1;
	
	public String giverName;
	public String targetName;
	
	public String name, desc;
	
	public List<QKey> qKeywords = new ArrayList<QKey>();
	
	public int reactionsLeft = 2;
	
	public void advanceStage() {
		qRList.get(stage).cleanup();
		stage++;
		qRList.get(stage).enable();
	}
	
	public void setStage(int newstage) {
		stage = newstage;
		for (int i = 0; i < qRList.size();i++) {
			QuestR current = qRList.get(i);
			current.cleanup();//remove it either way to prevent dupes
			if (i == stage) {
				current.enable();
			}
		}
	}
	
	public void cleanup() {
		for (int i = 0; i < qRList.size();i++) {
			qRList.get(i).cleanup();
		}
	}
	
	@Override
	public void fail() {
		cleanup();
		Player.player.sideQuests.remove(this);
	}
	
	public void announceUpdate() {
		extra.println(desc);
	}
	
	@Override
	public void complete() {
		cleanup();
		Player.player.sideQuests.remove(this);
	}
	
	protected void resolveDest(Feature locationF) {
		qKeywords.add(QKey.NORMAL_DEST);
		for (QKey qk: locationF.getQRType().dests) {
			qKeywords.add(qk);
		}
	}
	
	protected void resolveGive(Feature locationG) {
		qKeywords.add(locationG.getQRType().give);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String desc() {
		return desc;
	}

	@Override
	public void questTrigger(TriggerType type, String trigger, int num) {
		//EMPTY		
	}

	@Override
	public BasicSideQuest reactionQuest() {
		if (reactionsLeft <= 0) {
			return null;
		}
		return this;
	}


	@Override
	public List<String> triggers() {
		return Collections.singletonList(null);
	}
	
	public static DrawBane attemptCollectAlign(QKey align,float odds,int amount) {
		if (Player.player.getFindTime() > 1 && Player.player.sideQuests.size() > 0) {
			if (extra.randFloat() < odds) {
				List<Quest> list = new ArrayList<Quest>();
				Player.player.sideQuests.stream().filter(q -> q.getKeys().contains(align)).forEach(list::add);
				if (list.size() > 0) {
					Quest q = extra.randList(list);
					List<String> triggers = q.triggers();
					for (String str: triggers) {
						if (str.startsWith("db:")) {
							Player.player.delayFind();//unlike nodes, will only prevent if actually finds
							Player.player.questTrigger(TriggerType.COLLECT, str, amount);//ehhh I guess just add to all instead of this one?
							return DrawBane.getByName(str.substring(2));
						}
					}
				}
			}
		}
		return null;
	}


	@Override
	public List<QKey> getKeys() {
		return qKeywords;
	}
	
	@Override
	public Town nextLocation() {
		if (stage == -1) {
			return null;
		}
		return qRList.get(stage).locationF.getTown();
	}
	
	/**
	 * note that this only works for naive basics that use stages
	 */
	@Override
	public List<QuestR> getActiveQRs(){
		if (stage == -1) {
			return null;
		}
		return Collections.singletonList(qRList.get(stage));
	}
}

