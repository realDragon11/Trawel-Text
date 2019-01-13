import java.util.ArrayList;

public class Race extends Item implements java.io.Serializable{
	//only extends item so I can have my secret hidden race store
	public String name, namePlural;
	public double tradeMod, aimMod, hpMod,dodgeMod, damMod, speedMod, rarity;
	public ArrayList<String> insultList = new ArrayList<String>();
	public ArrayList<String> swears = new ArrayList<String>();
	public ArrayList<String> raceMaps = new ArrayList<String>();
	public String baseMap;
	public int magicPower, defPower;
	
	
	
	@Override
	public EnchantConstant getEnchant() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getCost() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void display(int style) {
		extra.println(name);
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
