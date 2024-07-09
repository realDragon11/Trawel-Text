package trawel.personal.item.body;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import trawel.battle.targets.TargetFactory;
import trawel.core.Print;
import trawel.core.Rand;
import trawel.helper.constants.TrawelColor;
import trawel.personal.RaceFactory;
import trawel.personal.RaceFactory.RaceClass;
import trawel.personal.RaceFactory.RaceID;
import trawel.personal.classless.Archetype;
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
	public List<String> insultList = new ArrayList<String>();
	public List<String> swears = new ArrayList<String>();
	public List<String> raceMaps = new ArrayList<String>();
	public String baseMap;
	public int magicPower, defPower;
	public RaceType racialType;
	public TargetFactory.TargetType targetType;
	public boolean emitsBlood;
	public RaceClass raceClass;
	
	/**
	 * note that you don't have to add this, and sometimes it will be better to set the racial archetype manually
	 * in the RaceFactory generation code, for example not all drudger stock are mages
	 * <br>
	 * this will likely get removed if the race changes, but only if the archetype is flaged as racial
	 */
	public Archetype archetype;
	
	public SoundBox.Voice voice = SoundBox.Voice.NONE;
	public float minPitch = 1, maxPitch = 1;
	
	public enum RaceType{
		/**
		 * applies to 'personable' things that have full people power, not things that are monsters like drudgers
		 * <br>
		 * the same target map can be used for a personable and non personable creature
		 * <br>
		 * some creatures also selectively have select mechanics restored, notably the ability to loot world currency
		 */
		PERSONABLE,
		BEAST;
	}
	
	public Race(RaceFactory.RaceID internalName) {
		this.internalName = internalName;
	}
	
	public RaceFactory.RaceID raceID() {
		return internalName;
	}
	
	public String renderName(boolean makePlural) {
		return Print.capFirst(makePlural ? internalName.namePlural : internalName.name);
	}
	
	@Override
	public String getName() {
		return renderName(false);
	}
	
	@Override
	public String getNameNoTier() {
		return renderName(false);
	}
	
	@Override
	public EnchantConstant getEnchant() {
		return null;
	}
	@Override
	public int getAetherValue() {
		return 0;
	}
	@Override
	public void display(int style, float markup) {
		Print.println(TrawelColor.STAT_HEADER+renderName(false));
		Print.println(internalName.name +", "+internalName.namePlural + ", " +internalName.adjective );
		Print.println("Type: " + racialType);
		Print.println("Legacy Sprite: " + raceClass.getLegacy().friendlyName());
		Print.println(TrawelColor.ITEM_DESC_PROP+"Blood: " + (emitsBlood ? TrawelColor.PRE_RED+"yes" : TrawelColor.PRE_WHITE +"no"));
		Print.println("Rarity: " + Print.F_TWO_TRAILING.format(rarity));
		Print.println(TrawelColor.ITEM_DESC_PROP+"Aiming: " +TrawelColor.ITEM_WANT_HIGHER+ Print.F_TWO_TRAILING.format(aimMod));
		Print.println(TrawelColor.ITEM_DESC_PROP+"Health: " +TrawelColor.ITEM_WANT_HIGHER+ Print.F_TWO_TRAILING.format(hpMod));
		Print.println(TrawelColor.ITEM_DESC_PROP+"Dodge: " +TrawelColor.ITEM_WANT_HIGHER+ Print.F_TWO_TRAILING.format(dodgeMod));
		Print.println(TrawelColor.ITEM_DESC_PROP+"Damage: " +TrawelColor.ITEM_WANT_HIGHER+ Print.F_TWO_TRAILING.format(damMod));
		Print.println(TrawelColor.ITEM_DESC_PROP+"Speed: " +TrawelColor.ITEM_WANT_HIGHER+ Print.F_TWO_TRAILING.format(speedMod));
		if (archetype != null) {
			Print.println(TrawelColor.ITEM_DESC_PROP+"Sterotype: " + archetype.friendlyName());
		}
		//the funnier sounding the 'better'
		Print.println(TrawelColor.ITEM_DESC_PROP+"Pitches: " +TrawelColor.ITEM_WANT_LOWER+ Print.F_TWO_TRAILING.format(minPitch)+ " " +TrawelColor.ITEM_WANT_HIGHER+Print.F_TWO_TRAILING.format(maxPitch));
		String slurs = null;
		for (String str: swears) {
			if (slurs == null) {
				slurs = str;
			}else {
				slurs +=", "+str;
			}
		}
		if (slurs != null) {
			Print.println("Slurs: " +slurs+".");
		}
		String phrases = null;
		for (String str: insultList) {
			if (phrases == null) {
				phrases = str;
			}else {
				phrases +=" "+str;
			}
		}
		if (phrases != null) {
			Print.println("Racist Phrases: " +phrases);
		}
	}
	@Override
	public void display(int style) {
		this.display(style, 1);
	}
	
	@Override
	public ItemType getType() {
		return Item.ItemType.RACE;
	}
	
	public String randomInsult() {
		return Rand.randList(insultList);
	}
	
	public String randomSwear() {
		return Rand.randList(swears);
	}
	
	public List<String> badNameList(){
		List<String> list = new ArrayList<String>();
		list.addAll(swears);
		list.add(raceID().name);
		list.add(raceID().name);
		return list;
	}
	
	public int randomRaceMap() {
		//return Integer.parseInt(extra.randList(raceMaps));//DOLATER check to see if the conversion worked
		return Rand.getRand().nextInt();//is moduluo'd for now
	}
	
	@Override
	public boolean canAetherLoot() {
		return false;//handled elsewhere
	}
	
	@Override
	public void levelUp() {}

	public String getLegacySprite() {
		return raceClass.getLegacy().getSpriteName(raceID());
	}
	
	public String getLegacyMap() {
		return raceClass.getLegacy().getMapName(raceID());
	}
	
	public int getLegacyNumber(int offset) {
		return raceClass.getLegacy().getMap(offset);
	}
	
	public String getWasddSprite() {
		return raceClass.getWasdd().getSpriteName(raceID());
	}
	
	public String getWasddMap() {
		return raceClass.getWasdd().getMapName(raceID());
	}
	
	public int getWasddNumber(int offset) {
		return raceClass.getWasdd().getMap(offset);
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
	
	@Override
	public String storeString(double markup, int canShow) {
		return this.getName() 
				+ " cost: " +  Print.F_WHOLE.format(Math.ceil(getAetherValue()*markup))
				;
	}
	
	public boolean canBeRacistTo(Race r) {
		return raceClass != r.raceClass;
	}
	
}
