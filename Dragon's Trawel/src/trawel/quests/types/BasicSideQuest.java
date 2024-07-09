package trawel.quests.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.core.Print;
import trawel.core.Rand;
import trawel.personal.item.solid.DrawBane;
import trawel.personal.people.Player;
import trawel.quests.events.QuestReactionFactory;
import trawel.quests.events.QuestReactionFactory.QKey;
import trawel.quests.locations.QuestR;
import trawel.towns.contexts.Town;
import trawel.towns.features.Feature;

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
		Print.println("You abandoned "+name()+".");
	}
	
	public void announceUpdate() {
		Print.println(desc);
	}
	
	@Override
	public void complete() {
		cleanup();
		Player.player.sideQuests.remove(this);
		Print.println("You completed "+name()+"!");
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
			if (Rand.randFloat() < odds) {
				List<Quest> list = new ArrayList<Quest>();
				Player.player.sideQuests.stream().filter(q -> q.getKeys().contains(align)).forEach(list::add);
				if (list.size() > 0) {
					Quest q = Rand.randList(list);
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

