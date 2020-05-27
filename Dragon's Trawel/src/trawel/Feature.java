package trawel;
import java.awt.Color;

public abstract class Feature implements java.io.Serializable{
	
	private static final long serialVersionUID = 7285785408935895233L;
	protected String name;
	protected SuperPerson owner = null;
	protected String tutorialText = null;
	protected Color color = Color.WHITE;
	protected Town town;
	protected int moneyEarned;
	public abstract void go();

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
}
