/**
 * 
 * @author Brian Malone
 * A class that holds taunts and boasts to be used while in combat and on level up.
 *
 */
public class Taunts implements java.io.Serializable{
	//instance variables
	private int tauntCount;
	private String[] taunts = new String[21];
	private int tauntPos;
	private int boastCount;
	private String[] boasts = new String[21];
	private int boastPos;
	private Race race;
	
	//constructors
	/**
	 * Create a instance with some starter taunts and boasts.
	 */
	public Taunts(Race r) {
	race = r;
	int x = 5+(int)(Math.random()*12);
	int y = 3+((int)Math.random()*12);
	int i = 0;
	
	while (i < x) {
		this.addTaunt(TauntsFactory.randTaunt(race));i++;
	}
	i = 0;
	while (i < y) {
		this.addBoast(TauntsFactory.randBoast(race));
		i++;
	}
	
	}
	
	//instance methods
	
	/**
	 * Add a new taunt. If the maximum has been reached, it simply discards it.
	 * @param newTaunt (String)
	 */
	public void addTaunt(String newTaunt) {
	if (tauntCount < 20){//fail silently- it doesn't matter in this case
	taunts[tauntCount] = newTaunt;
	tauntCount++;
	}
	}
	/**
	 * Gets the number of current taunts.
	 * @return number of taunts (int)
	 */
	public int getTauntCount() {
		return tauntCount;
	}
	
	/**
	 * Removes a random taunt.
	 */
	public void removeTaunt() {
	if (tauntCount > 3) {
	int i = (int) (Math.random()*tauntCount);
	while (i < tauntCount) {
		taunts[i] = taunts[i+1];
		i++;
	}
	tauntPos = 0;
	tauntCount--;
	}
	}
	
	/**
	 * Returns the next taunt in the taunt stack.
	 * @return String
	 */
	public String getTaunt() {
		if (tauntCount <=0) {
			this.addTaunt(TauntsFactory.randTaunt(race));
			this.addTaunt(TauntsFactory.randTaunt(race));
			this.addTaunt(TauntsFactory.randTaunt(race));
			return "Your best is my worst!";
		}
		else {
		tauntPos++;
		if (tauntPos >= tauntCount || tauntPos >=taunts.length) {
			tauntPos = 0;
		}
		return taunts[tauntPos];
		}
	}
	
	/**
	 * Add a new boast. If the cap is reached, it simply discards it.
	 * @param newBoast (String)
	 */
	public void addBoast(String newBoast) {
	if (boastCount < 20){//fail silently- it doesn't matter in this case
	boasts[boastCount] = newBoast;
	boastCount++;
	}
	}
	/**
	 * Gets the current number of boasts.
	 * @return boastCount (int)
	 */
	public int getBoastCount() {
		return boastCount;
	}
	/**
	 * Removes a random boast.
	 */
	public void removeBoast() {
	if (boastCount > 3) {
	int i = (int) (Math.random()*boastCount);
	while (i < boastCount) {
		boasts[i] = boasts[i+1];
		i++;
	}
	boastPos = 0;
	
	boastCount--;
	}
	}
	
	/**
	 * Get's the next boast in the stack
	 * @return String
	 */
	public String getBoast() {
		if (boastCount <=0) {
			this.addBoast(TauntsFactory.randBoast(race));
			this.addBoast(TauntsFactory.randBoast(race));
			this.addBoast(TauntsFactory.randBoast(race));
			return "I'm the best there is!";
		}
		else {
			boastPos++;
		if (boastPos >= boastCount || boastPos >=boasts.length) {
			boastPos = 0;
		}
		return boasts[boastPos];
		}
	}
	
}
