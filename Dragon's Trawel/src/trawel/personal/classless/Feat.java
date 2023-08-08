package trawel.personal.classless;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;
import trawel.personal.classless.Skill.Type;

public enum Feat implements IHasSkills{
	NOT_PICKY("Not Picky","Grants 2 additional feat picks. (Picks don't give you more feats, just more times to choose.)",""
			,1f,EnumSet.of(FeatType.COMMON),null,EnumSet.noneOf(Skill.class)
			,5,5,null,null),//should grant a low level in every stat
	COMMON_TOUGH("The Tough","They're tougher than they look. And they look tough.","",
			1f,FeatType.COMMON,EnumSet.of(Skill.TA_NAILS,Skill.RAW_GUTS),2,0),
	MAGIC_WITCH("The Witch","Curses and potions are their forte.","",
			1f,EnumSet.of(FeatType.POTIONS,FeatType.CURSES),null
			,EnumSet.of(Skill.CURSE_MAGE,Skill.P_BREWER),0,2,null,null)
	,HEMOVORE("Hemovore","Extracts life energy from fleeting mortality.","",
			1f,EnumSet.of(FeatType.BATTLE),EnumSet.of(FeatType.CURSES,FeatType.MAGIC)
			,EnumSet.of(Skill.BLOODTHIRSTY,Skill.KILLHEAL),1,2,null,null)
	,UNBREAKABLE("Unbreakable","Nothing stops them.","",
			1f,null,EnumSet.of(FeatType.BATTLE,FeatType.SPIRIT)
			,EnumSet.of(Skill.TA_NAILS,Skill.ARMORHEART),4,0,null,null)
	,UNDERHANDED("Underhanded","They'll do anything and everything to win.",""
			,1f,EnumSet.of(FeatType.TRICKS),null
			,EnumSet.of(Skill.SPUNCH)
			,0,10,null,null)
	;

	private final String name, desc, getDesc;
	private final Set<Skill> skills;
	/**
	 * can set to 0 to make feat not spawn, or set FeatType to null
	 */
	public final float rarity;
	public final Set<FeatType> typesAll, typesAny;
	public final Set<IHasSkills> needsAll, needsOne;
	private final int strength, dexterity;
	Feat(String _name, String _desc, String _getDesc,float _rarity,Set<FeatType> _typesAll,Set<FeatType> _typesAny,Set<Skill> skillset,int stre, int dext
			,Set<IHasSkills> needsAllOf, Set<IHasSkills> needsOneOf){
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
	}
	
	Feat(String _name, String _desc, String _getDesc,float _rarity,FeatType _type ,Set<Skill> skillset,int stre, int dext){
		this(_name,_desc,_getDesc,_rarity,null,EnumSet.of(_type),skillset,stre,dext,null,null);
	}
	
	Feat(String _name, String _desc, String _getDesc,float _rarity,FeatType _type ,Set<Skill> skillset){
		this(_name,_desc,_getDesc,_rarity,_type,skillset,0,0);
	}
	
	public enum FeatType{
		COMMON, MAGIC, POTIONS, CURSES, TRICKS, SOCIAL, BATTLE, SPIRIT;
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
	public static Feat randFeat(Set<FeatType> set,Set<Feat> has) {
		Set<Feat> copyList = EnumSet.noneOf(Feat.class);
		double totalRarity = 0;
		for (Feat f: Feat.values()){
			if (f.rarity > 0 &&
					!has.contains(f)&&
					(f.typesAll == null || set.containsAll(f.typesAll))&&
					(f.typesAny == null || !Collections.disjoint(f.typesAny, set))&&
					(f.needsAll == null || has.containsAll(has)) &&
					(f.needsOne == null || !Collections.disjoint(f.needsOne, has))//either disjoint or streams
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

}
