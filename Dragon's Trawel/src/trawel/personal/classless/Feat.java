package trawel.personal.classless;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;
import trawel.personal.classless.Skill.Type;

public enum Feat implements IHasSkills{
	NOT_PICKY("Not-Picky","Grants 2 additional feat picks. (Picks don't give you more feats, just more times to choose.)",""
			,1f,EnumSet.of(FeatType.COMMON),null
			,EnumSet.noneOf(Skill.class),5,5,5//should grant a low level in every stat
			,null,null),
	COMMON_TOUGH("Tough","They're tougher than they look. And they look tough.","",
			1f,FeatType.COMMON,EnumSet.of(Skill.TA_NAILS,Skill.RAW_GUTS),2,0,0),
	WITCHY("Witchy","Curses and potions are their forte.","",// Washy
			1f,EnumSet.of(FeatType.POTIONS,FeatType.CURSES),null
			,EnumSet.of(Skill.CURSE_MAGE,Skill.P_BREWER),0,1,4
			,null,null)
	,HEMOVORE("Hemovore","Extracts life energy from fleeting mortality.","",
			1f,EnumSet.of(FeatType.MYSTIC),EnumSet.of(FeatType.CURSES,FeatType.BATTLE,FeatType.POTIONS)
			,EnumSet.of(Skill.BLOODTHIRSTY,Skill.KILLHEAL,Skill.BLOODDRINKER),0,0,5
			//needs at least one of the things it grants from some other source
			,null,EnumSet.of(Skill.BLOODTHIRSTY,Skill.KILLHEAL,Skill.BLOODDRINKER))
	,UNBREAKABLE("Unbreakable","Nothing stops them.","",
			1f,null,EnumSet.of(FeatType.BATTLE,FeatType.SPIRIT)
			,EnumSet.of(Skill.TA_NAILS,Skill.ARMORHEART),4,0,0
			,null,null)
	,UNDERHANDED("Underhanded","They'll do anything and everything to win.",""
			,1f,EnumSet.of(FeatType.TRICKS),null
			,EnumSet.of(Skill.SPUNCH),0,10,0
			,null,null)
	,ARMORPAINTER("Painter","They paint their armor with magical dyes.",""
			,1f,EnumSet.of(FeatType.MYSTIC),EnumSet.of(FeatType.SMITHS,FeatType.TRICKS)
			,EnumSet.of(Skill.MESMER_ARMOR,Skill.ARMOR_MAGE),0,0,5
			,null,null)
	,AMBUSHER("Ambusher","They know how to start a fight.",""
			,1f,null,EnumSet.of(FeatType.BATTLE,FeatType.TRICKS)
			,EnumSet.of(Skill.OPENING_MOVE,Skill.QUICK_START),0,5,0
			,null,null
			)
	,ACROBAT("Acrobat","To them, a battle is a playful dance, just with higher stakes.",""
			,1f,EnumSet.of(FeatType.AGILITY),EnumSet.of(FeatType.TRICKS,FeatType.SPIRIT)
			,EnumSet.of(Skill.REACTIVE_DODGE,Skill.SPEEDDODGE),0,5,0
			,null,null
			)
	,FLAME_WARDEN("Flamewarden","Wields fire fiercly, fueling their defense.",""
			,.7f,null,null
			,EnumSet.of(Skill.ELEMENTALIST,Skill.M_PYRO,Skill.ARMORHEART),0,0,4
			,EnumSet.of(Skill.ARCANIST),null
			)
	,FROST_WARDEN("Frostwarden","Uses ice to bolster their armor.",""
			,.7f,null,null
			,EnumSet.of(Skill.ELEMENTALIST,Skill.M_CRYO,Skill.ARMOR_TUNING),0,0,4
			,EnumSet.of(Skill.ARCANIST),null
			)
	,SHOCK_SAVANT("Shock-Savant","Shocks their foes with static constantly, increasing the damage wrought by their charges.",""
			,.7f,null,null
			,EnumSet.of(Skill.ELEMENTALIST,Skill.M_AERO,Skill.SPUNCH),0,0,4
			,EnumSet.of(Skill.ARCANIST),null
			)
	;

	private final String name, desc, getDesc;
	private final Set<Skill> skills;
	/**
	 * can set to 0 to make feat not spawn, or set FeatType to null
	 */
	public final float rarity;
	public final Set<FeatType> typesAll, typesAny;
	public final Set<Skill> needsAll, needsOne;
	private final int strength, dexterity, clarity;
	Feat(String _name, String _desc, String _getDesc,float _rarity,Set<FeatType> _typesAll,Set<FeatType> _typesAny,Set<Skill> skillset
			,int stre, int dext, int cla
			,Set<Skill> needsAllOf, Set<Skill> needsOneOf){
		name = _name;
		desc = _desc;
		getDesc = _getDesc;
		skills = skillset;
		rarity = _rarity;
		typesAny = _typesAny;
		typesAll = _typesAll;
		strength = stre;
		dexterity = dext;
		needsAll = needsAllOf;
		needsOne = needsOneOf;
		clarity = cla;
	}
	
	Feat(String _name, String _desc, String _getDesc,float _rarity,FeatType _type ,Set<Skill> skillset,int stre, int dext, int cla){
		this(_name,_desc,_getDesc,_rarity,null,EnumSet.of(_type),skillset,stre,dext,cla,null,null);
	}
	
	Feat(String _name, String _desc, String _getDesc,float _rarity,FeatType _type ,Set<Skill> skillset){
		this(_name,_desc,_getDesc,_rarity,_type,skillset,0,0,0);
	}
	
	public enum FeatType{
		COMMON,
		MYSTIC,//should be granted if any of the following are granted:
		ARCANE,CURSES
		//end mystic sub classes
		, POTIONS,TRICKS, SOCIAL, BATTLE, SPIRIT, SMITHS, AGILITY;
	}
	
	@Override
	public Stream<Skill> collectSkills() {
		return skills.stream();
	}
	
	/**
	 * returns a feat from the allowed type sets, that doesn't include the has set
	 * <br>
	 * can't use vose's alias method because we can't precompute
	 * @param set
	 * @param has
	 * @return
	 */
	public static Feat randFeat(Set<FeatType> set,Set<Feat> has,Set<Skill> hasSkills) {
		Set<Feat> copyList = EnumSet.noneOf(Feat.class);
		double totalRarity = 0;
		for (Feat f: Feat.values()){
			if (f.rarity > 0 &&
					!has.contains(f)&&
					(f.typesAll == null || set.containsAll(f.typesAll))&&
					(f.typesAny == null || !Collections.disjoint(f.typesAny, set))&&
					(f.needsAll == null || hasSkills.containsAll(f.needsAll)) &&
					(f.needsOne == null || !Collections.disjoint(f.needsOne, hasSkills))//either disjoint or streams
				) {
				//allll the predicate code time
				//we don't use real predicates...yet
				copyList.add(f);
				totalRarity +=f.rarity;
			}
		}
		totalRarity *= extra.getRand().nextDouble();
		Feat r = null;
		for (Feat f: copyList){
			totalRarity-=f.rarity;
			if (totalRarity <=0) {
				return f;
			}
			r = f;//in case of rounding errors
		}
		return r;
	}

	@Override
	public String getText() {
		String str = name + ": "+desc;
		for (Skill s: skills) {
			if (s.getType() == Type.INTERNAL_USE_ONLY) {
				continue;
			}
			str += "\n "+IHasSkills.padNewlines(s.disp());
		}
		return str;
	}
	
	@Override
	public String getOwnText() {
		return name + ": "+desc;
	}
	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}

	@Override
	public String friendlyName() {
		return name;
	}

	@Override
	public boolean goMenuItem() {
		extra.println("n/a");
		return false;
	}

	@Override
	public int getClarity() {
		return clarity;
	}
	
	@Override
	public Set<Skill> giveSet() {
		return skills;
	}

}
