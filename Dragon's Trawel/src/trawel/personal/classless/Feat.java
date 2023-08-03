package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;

public enum Feat implements HasSkills{
	TOUGH_COMMON("The Tough","They're tougher than they look. And they look tough.","",
			1f,FeatType.COMMON,EnumSet.of(Skill.TA_NAILS,Skill.RAW_GUTS))
	;

	public final String name, desc, getDesc;
	public final Set<Skill> skills;
	/**
	 * can set to 0 to make feat not spawn, or set FeatType to null
	 */
	public final float rarity;
	public final FeatType type;
	Feat(String _name, String _desc, String _getDesc,float _rarity,FeatType _type ,Set<Skill> skillset){
		name = _name;
		desc = _desc;
		getDesc = _getDesc;
		skills = skillset;
		rarity = _rarity;
		type = _type;
	}
	
	public enum FeatType{
		COMMON;
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
			if (f.rarity > 0 && set.contains(f.type)) {
				copyList.add(f);
				totalRarity +=f.rarity;
			}
		}
		totalRarity *= extra.getRand().nextDouble();
		Feat r = null;
		for (Feat f: Feat.values()){
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
			str += "\n "+HasSkills.padNewlines(s.disp());
		}
		return str;
	}

}
