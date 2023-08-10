package trawel.towns;
import java.util.ArrayList;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.QuestR;
import trawel.time.ContextLevel;
import trawel.time.ContextType;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public abstract class Feature extends TContextOwner{
	
	private static final long serialVersionUID = 7285785408935895233L;
	protected String name;
	protected SuperPerson owner = null;
	protected String tutorialText = null;
	protected Town town;
	protected int moneyEarned;
	protected ArrayList<QuestR> qrList = new ArrayList<QuestR>();
	protected ArrayList<QuestR> qrRemoveList = new ArrayList<QuestR>();
	protected int background_variant = extra.randRange(1, 3);
	protected String background_area = "main";
	public static Feature atFeatureForHeader = null;
	protected int tier;
	
	public abstract void go();
	public void goHeader() {
		Networking.setBackground(background_area);
		this.sendBackVariant();
		atFeatureForHeader = this;
	}
	public void sendBackVariant() {
		double[] p = Calender.lerpLocation(town);
		float[] b = Player.player.getWorld().getCalender().getBackTime(p[0],p[1]);
		Networking.sendStrong("Backvariant|"+background_area+background_variant+"|"+b[0]+"|"+b[1]+"|");
	}
	
	public void init() {
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SuperPerson getOwner() {
		return owner;
	}

	public void setOwner(SuperPerson owner) {
		this.owner = owner;
	}
	
	public void printTutorial() {
		//if (tutorialText != null) {
			extra.println(tutorialText);
		//}
	}

	public abstract String getColor();
	
	public QRType getQRType() {
		return QRType.NONE;
	}
	
	public enum QRType{
		NONE, MOUNTAIN, FOREST, INN, SLUM, WHUT;
	}
	
	public void addQR(QuestR qr) {
		qrList.add(qr);
	}
	
	public void removeQR(QuestR qr) {
		qrList.remove(qr);
	}
	
	public void cueRemoveQR(QuestR qr) {
		qrRemoveList.add(qr);
	}
	
	public void flushQR() {
		qrList.removeAll(qrRemoveList);
		qrRemoveList.clear();
	}
	
	public int qrSize() {
		return qrList.size();
	}

	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.UNBOUNDED,this);
		timeSetup();
	}
	public Town getTown() {
		return town;
	}
	
	@Override
	public List<TimeEvent> consumeEvents(List<TimeEvent> list) {
		if (list.size() > 0) {
			System.err.println(list.toString());
		}
		return list;
	}
	@Override
	public ContextLevel contextLevel() {
		return ContextLevel.FEATURE;
	}
	
	public boolean canShow() {
		return true;
	}
	protected void setTownInternal(Town town2) {
		town = town2;
	}
	public String getTutorialText() {
		return tutorialText;
	}
	
}
