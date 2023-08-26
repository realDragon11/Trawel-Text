package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import trawel.extra;
import trawel.personal.classless.Skill.Type;

public enum Perk implements IHasSkills{
	EMPTY("","",IHasSkills.emptySkillSet),
	RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns.",EnumSet.of(Skill.RACIAL_SHIFTS)),
	SKY_BLESS_1("Leaf on the Wind","Has a minor blessing from the sky, granting them paranatural speed.",EnumSet.of(Skill.SPEEDDODGE,Skill.BLITZ)),
	SKY_BLESS_2("Growing Storm","Has a blessing from the sky, granting acute vision and senses that heal them when used.",EnumSet.of(Skill.DODGEREF,Skill.NIGHTVISION),0,4,1)
	,CULT_LEADER_BLOOD("Cult Leader (Blood)","Chosen by the cult of blood.",EnumSet.of(Skill.BLOODTHIRSTY))//also used by npcs
	,MINE_ALL_VEINS("Meticulous Miner","Know for their painstaking digging process.",EnumSet.of(Skill.NIGHTVISION))
	,GRAVEYARD_SIGHT("Gravesight","Can see in the dark due to a lot of experience.",EnumSet.of(Skill.NIGHTVISION))
	,HELL_BARONESS("Baroness of Hell","Has fiendish powers.",EnumSet.of(Skill.CURSE_MAGE,Skill.TA_NAILS),5,0,5)
	,HELL_BARON_NPC("Baron of Hell","Has fiendish powers.",EnumSet.of(Skill.NPC_BURN_ARMOR,Skill.RAW_GUTS,Skill.TA_NAILS,Skill.SPUNCH,Skill.KILLHEAL,Skill.CONDEMN_SOUL,Skill.NO_HOSTILE_CURSE),50,0,20)
	,FATESPINNER_NPC("Fated","Has preternatural intuition.",EnumSet.of(Skill.OPENING_MOVE,Skill.CURSE_MAGE,Skill.PRESS_ADV,Skill.SPEEDDODGE,Skill.MESMER_ARMOR,Skill.PLOT_ARMOR,Skill.NO_HOSTILE_CURSE),10,10,40)
	,FATED("Fated","Has preternatural intuition.",EnumSet.of(Skill.OPENING_MOVE,Skill.SPEEDDODGE),0,0,10)
	,STAND_TALL("Towering","They stand tall.",EnumSet.of(Skill.TA_NAILS),20,0,0)
	,NPC_PRIMAL_MOUNTAIN("Oread","The primal forces within them embody the mountains, sullied not by mortal picks."
			,EnumSet.of(Skill.LIFE_MAGE,Skill.TA_NAILS,Skill.RAW_GUTS,Skill.STERN_STUFF),20,0,10
			)
	, YORE_NPC("Mythic","Is a living story.",EnumSet.of(Skill.PLOT_ARMOR,Skill.RAW_GUTS,Skill.NO_HOSTILE_CURSE),50,50,50)
	,STORYTELLER("Storyteller","Has lived through an epic narrative.",EnumSet.of(Skill.PLOT_ARMOR,Skill.STERN_STUFF),0,0,5);
	private final String name, desc;
	private final Set<Skill> skills;
	private final int strength, dexterity, clarity;
	
	Perk(String _name, String description, Set<Skill> skillset, int str, int dex, int cla){
		name = _name;
		desc = description;
		skills = skillset;
		strength = str;
		dexterity = dex;
		clarity = cla;
	}
	
	Perk(String _name, String description, Set<Skill> skillset){
		this(_name,description,skillset,0,0,0);
	}
	
	@Override
	public Stream<Skill> collectSkills() {
		return skills.stream();
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
	public String getBriefText() {
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
	public int getClarity() {
		return clarity;
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
	public Set<Skill> giveSet() {
		return skills;
	}

}
