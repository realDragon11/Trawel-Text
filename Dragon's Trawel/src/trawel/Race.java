package trawel;
import java.util.ArrayList;


public class Race extends Item implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//only extends item so I can have my secret hidden race store
	public String name, namePlural;
	public double tradeMod, aimMod, hpMod,dodgeMod, damMod, speedMod, rarity;
	public ArrayList<String> insultList = new ArrayList<String>();
	public ArrayList<String> swears = new ArrayList<String>();
	public ArrayList<String> raceMaps = new ArrayList<String>();
	public String baseMap;
	public int magicPower, defPower;
	public RaceType racialType;
	public TargetFactory.TargetType targetType;
	public boolean emitsBlood;
	
	public SoundBox.Voice voice = SoundBox.Voice.NONE;
	public float minPitch = 1, maxPitch = 1;
	
	public enum RaceType{
		HUMANOID, BEAST;
	}
	
	@Override
	public EnchantConstant getEnchant() {
		return null;
	}
	@Override
	public int getCost() {
		return 0;
	}
	@Override
	public void display(int style, float markup) {
		extra.println(name);
	}
	@Override
	public void display(int style) {
		this.display(style, 1);
	}
	@Override
	public String getType() {
		return "race";
	}
	
	public String randomInsult() {
		return extra.randList(insultList);
	}
	
	public String randomSwear() {
		return extra.randList(swears);
	}
	public String randomRaceMap() {
		return extra.randList(raceMaps);
	}
	
	public void levelUp() {}
	
}
