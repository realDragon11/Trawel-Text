package trawel;
import java.awt.Color;
import java.util.ArrayList;

public abstract class Feature implements java.io.Serializable{
	
	private static final long serialVersionUID = 7285785408935895233L;
	protected String name;
	protected SuperPerson owner = null;
	protected String tutorialText = null;
	protected Color color = Color.WHITE;
	protected Town town;
	protected int moneyEarned;
	protected ArrayList<QuestR> qrList = new ArrayList<QuestR>();
	protected ArrayList<QuestR> qrRemoveList = new ArrayList<QuestR>();
	protected int background_variant = extra.randRange(1, 3);
	protected String background_area = "main";
	public static Feature atFeatureForHeader = null;
	public abstract void go();
	public void goHeader() {
		Networking.setBackground(background_area);
		double[] p = Calender.lerpLocation(town);
		Networking.sendStrong("Backvariant|"+background_area+background_variant+"|"+Player.player.world.getCalender().getBackTime(p[0],p[1])+"|");
		atFeatureForHeader = this;
	}
	public void init() {
	}
	public abstract void passTime(double time);
	
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

	public Color getColor() {
		return color;
	}
	
	public QRType getQRType() {
		return QRType.NONE;
	}
	
	public enum QRType{
		NONE, MOUNTAIN, FOREST, INN;
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
	
}
