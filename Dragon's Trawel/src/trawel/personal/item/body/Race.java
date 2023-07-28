package trawel.personal.item.body;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

import trawel.extra;
import trawel.battle.attacks.TargetFactory;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.item.Item;
import trawel.personal.item.magic.EnchantConstant;


public class Race extends Item{
	/**
	 * should not be saved
	 */
	private static final long serialVersionUID = 1L;
	//only extends item so I can have my secret hidden race store
	private final RaceFactory.RaceID internalName;
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
	
	public Race(RaceFactory.RaceID internalName) {
		this.internalName = internalName;
	}
	
	public RaceFactory.RaceID raceID() {
		return internalName;
	}
	
	public String renderName(boolean makePlural) {
		return makePlural ? internalName.namePlural : internalName.name;
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
		extra.println(internalName.name);
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
	
	@Override
	public boolean coinLoot() {
		return false;//handled elsewhere
	}
	
	@Override
	public void levelUp() {}

	public String getMap() {
		return internalName.map;
	}
	
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException{
		throw new RuntimeException("can't save races");
	}
	
	
	public Object writeReplace() throws ObjectStreamException{
		//System.out.println("Storing: " + internalName.name());
		//doesn't get stored right now, but can get stored
		return new STORE(internalName);
	}
	
	
	private class STORE implements Serializable{
		public final RaceID r;
		public STORE(RaceID r) {
			this.r = r;
		}
		//enums have special saves, so this will refresh their data
		public Object readResolve() throws ObjectStreamException{
			//System.out.println("Resolving " + r.name + "/" + r.name());
			return RaceFactory.getRace(r);
		}
	}
	
}
