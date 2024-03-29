package trawel.towns;
import java.util.List;

import trawel.Networking;
import trawel.extra;
import trawel.mainGame;
import trawel.personal.classless.IEffectiveLevel;
import trawel.personal.people.Agent;
import trawel.personal.people.Player;
import trawel.personal.people.SuperPerson;
import trawel.quests.QuestBoardLocation;
import trawel.quests.QuestBoardLocation.FeatureRoller;
import trawel.quests.QuestReactionFactory.QKey;
import trawel.time.ContextLevel;
import trawel.time.ContextType;
import trawel.time.TContextOwner;
import trawel.time.TimeContext;
import trawel.time.TimeEvent;

public abstract class Feature extends TContextOwner implements IEffectiveLevel{
	
	private static final long serialVersionUID = 7285785408935895233L;
	protected String name;
	protected SuperPerson owner = null;
	protected String tutorialText = null;
	protected Town town;
	protected int moneyEarned;
	protected int background_variant = extra.randRange(1, 3);
	protected String background_area = "main";
	protected int tier;
	protected Networking.Area area_type;
	protected String intro, outro;
	
	/**
	 * only used if implements QuestBoardLocation interface
	 */
	protected transient FeatureRoller qbRoller;
	/**
	 * allows quests to go one layer deep in replaced features without stuff exploding
	 * <br>
	 * note that features that can't support QRs will still lead to dead end quests
	 * <br>
	 * if a feature is replaced with itself, that means it's no longer useable
	 */
	protected Feature replaced = null;
	
	protected abstract void go();
	protected void goHeader() {
		Player.player.atFeature = this;
		Networking.setArea(area_type);
		//Networking.setBackground(background_area);
		//sendBackVariant();
		
	}
	public void sendBackVariant() {
		double[] p = Calender.lerpLocation(town);
		float[] b = Player.player.getWorld().getCalender().getBackTime(p[0],p[1]);
		Networking.sendStrong("Backvariant|"+background_area+background_variant+"|"+b[0]+"|"+b[1]+"|");
	}
	public void sendBackVariantOf(String background) {
		
	}
	
	public void init() {
	}
	
	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return getName();
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
		extra.println(getTutorialText());
	}

	public abstract String getColor();
	
	public QRType getQRType() {
		return QRType.NONE;
	}
	
	public enum QRType{
		NONE(new QKey[0],null,0f),
		MOUNTAIN(new QKey[] {QKey.DEST_MOUNTAIN},null,1f),
		FOREST(new QKey[] {QKey.DEST_WOODS},null,1f),
		INN(new QKey[] {QKey.DEST_INN},QKey.GIVE_INN,1.5f),
		SLUM(new QKey[] {QKey.DEST_SLUM},QKey.GIVE_SLUM,1.5f),
		WHUT(new QKey[] {QKey.DEST_WHUT},QKey.GIVE_WHUT,1.1f),
		MGUILD(new QKey[] {QKey.DEST_GUILD},QKey.GIVE_MGUILD,1.1f),
		RGUILD(new QKey[] {QKey.DEST_GUILD},QKey.GIVE_RGUILD,1.1f),
		HGUILD(new QKey[] {QKey.DEST_GUILD},QKey.GIVE_HGUILD,1.1f),
		HUNT_GUILD(new QKey[] {QKey.DEST_GUILD},QKey.GIVE_HUNT_GUILD,.7f);
		public final QKey[] dests;
		public final QKey give;
		public final float targetMult;
		QRType(QKey[] _dests,QKey _give, float _targetMult){
			dests = _dests;
			give = _give;
			targetMult = _targetMult;
		}
	}

	@Override
	public void reload() {
		timeScope = new TimeContext(ContextType.UNBOUNDED,this);
		timeSetup();
		if (this instanceof QuestBoardLocation) {
			qbRoller = new FeatureRoller((QuestBoardLocation)this);
		}
	}
	public Town getTown() {
		return town;
	}
	
	@Override
	public List<TimeEvent> consumeEvents(List<TimeEvent> list) {
		if (list.size() > 0) {
			mainGame.errLog(list.toString());
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
	
	@Override
	public int getLevel() {
		return tier;	
	}
	
	/**
	 * provides the feature header and the go
	 * <br>
	 * does not include local town code and story updating
	 */
	public void enter() {
		goHeader();
		String text = getIntro();
		if (text != null && mainGame.displayFeatureFluff) {
			extra.println(text);
		}
		if (owner == Player.player && moneyEarned > 0) {
			extra.println("You take the " + moneyEarned + " in profits.");
			Player.player.addGold(moneyEarned);
			moneyEarned = 0;
		}
		go();
		text = getOutro();
		if (text != null && mainGame.displayFeatureFluff) {
			extra.println(text);
		}
	}
	
	public Feature setIntro(String _saying) {
		intro = _saying;
		return this;
	}
	public Feature setOutro(String _saying) {
		outro = _saying;
		return this;
	}
	
	/**
	 * can be overwritten to implement variable intros
	 */
	public String getIntro() {
		return intro;
	}
	/**
	 * can be overwritten to implement variable outros
	 */
	public String getOutro() {
		return outro;
	}
	
	public RemoveAgentFromFeatureEvent laterRemoveAgent(Agent a) {
		return null;
	}
	
	public static class RemoveAgentFromFeatureEvent extends TimeEvent{
		public final Agent agent;
		public final Feature feature;
		public final boolean putInTown;
		
		public RemoveAgentFromFeatureEvent(Agent _agent, Feature _feature, boolean _putInTown) {
			agent = _agent;
			feature = _feature;
			putInTown = _putInTown;
			context = ContextLevel.FEATURE;
		}
	}
	
	public float occupantDesire() {
		return .5f;
	}
	public String nameOfType() {
		return this.getClass().getTypeName();
	}
	
	public Feature getReplaced() {
		return replaced;
	}
	
	public void setReplaced(Feature feature) {
		replaced = feature;
	}
	
	/**
	 * only used if QuestBoardLocation
	 * @param range
	 * @return
	 */
	public Feature getQuestGoal(int range){
		assert this instanceof QuestBoardLocation;
		return qbRoller.roll();
	}
}
