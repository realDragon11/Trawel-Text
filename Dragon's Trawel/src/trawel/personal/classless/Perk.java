package trawel.personal.classless;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import trawel.helper.constants.TrawelColor;
import trawel.helper.methods.extra;
import trawel.personal.classless.Skill.Type;

public enum Perk implements IHasSkills{
	EMPTY("","",IHasSkills.emptySkillSet)
	//misc npc perks
	,RACIAL_SHIFTS("Flexible","Prone to changing its defense patterns.",EnumSet.of(Skill.RACIAL_SHIFTS))
	,STAND_TALL("Towering","They stand tall.",EnumSet.of(Skill.TA_NAILS),20,0,0)
	,CULT_LEADER("Cult Leader","Leads a cult sect.",EnumSet.of(Skill.NO_HOSTILE_CURSE,Skill.PLOT_ARMOR),0,0,10)
	,NPC_PROMOTED("Promoted","They made it big.",EnumSet.of(Skill.TA_NAILS,Skill.STERN_STUFF, Skill.PLOT_ARMOR,Skill.NO_HOSTILE_CURSE),5,5,5)
	//blessing perks
	,SKY_BLESS_1("Leaf on the Wind","Has a minor blessing from the sky, granting them paranatural speed.",EnumSet.of(Skill.SPEEDDODGE,Skill.BLITZ),0,4,1)
	,SKY_BLESS_2("Growing Storm","Has a blessing from the sky, granting them unexpected bursts of power.",EnumSet.of(Skill.QUICK_START,Skill.DEADLY_AIM),0,4,1)
	,FOREST_BLESS_1("Sappy Seeder","Has a minor blessing of the forest, hardening their skin and granting alchemy affinity.",EnumSet.of(Skill.TA_NAILS,Skill.P_BREWER),4,0,1)
	,FOREST_BLESS_2("Heart of Regrowth","Has a blessing of the forest, integrating bark skin with their armor.",EnumSet.of(Skill.ARMORHEART,Skill.LIVING_ARMOR),4,0,1)
	//chosen perks, tend to grant 5 attributes; used on both players and npc cultists
	,CULT_CHOSEN_BLOOD("Chosen (Blood)","Chosen by the cult of Blood.",EnumSet.of(Skill.BLOODTHIRSTY),1,1,3)
	,CULT_CHOSEN_SKY("Chosen (Sky)","Chosen by the cult of Sky.",EnumSet.of(Skill.REACTIVE_DODGE),0,3,2)
	//misc world perks
	,MINE_ALL_VEINS("Meticulous Miner","Known for their painstaking digging process.",EnumSet.of(Skill.NIGHTVISION))
	,GRAVEYARD_SIGHT("Gravesight","Can see in the dark due to a lot of experience.",EnumSet.of(Skill.NIGHTVISION))
	//boss npc perks
	,FATESPINNER_NPC("Fated","Has preternatural intuition.",EnumSet.of(Skill.OPENING_MOVE,Skill.NO_QUARTER,Skill.SPEEDDODGE,Skill.MESMER_ARMOR,Skill.PLOT_ARMOR,Skill.NO_HOSTILE_CURSE),10,10,40)
	,HELL_BARON_NPC("Baron of Hell","Has fiendish powers.",EnumSet.of(Skill.NPC_BURN_ARMOR,Skill.RAW_GUTS,Skill.TA_NAILS,Skill.SPUNCH,Skill.KILLHEAL,Skill.CONDEMN_SOUL,Skill.NO_HOSTILE_CURSE),50,0,20)
	,YORE_NPC("Mythic","Is a living story.",EnumSet.of(Skill.PLOT_ARMOR,Skill.RAW_GUTS,Skill.NO_HOSTILE_CURSE),50,50,50)
	,ANCIENT("Ancient","Older than history.",EnumSet.of(Skill.NO_HOSTILE_CURSE),10,10,10)
	//boss reward perks (mostly same order), tend to grant 10 attributes and one skill or 5 attributes and two skills
	,FATED("Fated","Has preternatural intuition.",EnumSet.of(Skill.OPENING_MOVE),0,0,10)
	,HELL_BARONESS_1("Baroness of Hell (Peana's Throne)","Has fiendish powers.",EnumSet.of(Skill.TA_NAILS),5,0,5)
	,STORYTELLER("Storyteller","Has lived through an epic narrative.",EnumSet.of(Skill.PLOT_ARMOR,Skill.STERN_STUFF),0,0,5)
	,QUEENSLAYER("Queenslayer","Committed an important regicide.",EnumSet.of(Skill.DSTRIKE),4,4,2)
	//nymph perks
	,NPC_PRIMAL_MOUNTAIN("Oread","The primal forces within them embody the mountains, sullied not by mortal picks."
			,EnumSet.of(Skill.LIFE_MAGE,Skill.TA_NAILS,Skill.RAW_GUTS,Skill.STERN_STUFF),20,0,10)
	,NPC_PRIMAL_FOREST("Dryad","The primal forces within them embody the wild forests, fierce and untamed."
			,EnumSet.of(Skill.LIFE_MAGE,Skill.BLOODTHIRSTY,Skill.ARMORHEART,Skill.LIVING_ARMOR),10,10,10)
	,NPC_PRIMAL_BREEZE("Aurae","The primal forces within them embody the unexpected force of the wind."
			,EnumSet.of(Skill.LIFE_MAGE,Skill.QUICK_START,Skill.OPENING_MOVE,Skill.DEADLY_AIM),0,10,20)
	,NPC_PRIMAL_GROVE("Alseide","The primal forces within them embody the deceptively dangerous groves."
			,EnumSet.of(Skill.LIFE_MAGE,Skill.OPENING_MOVE,Skill.SPUNCH,Skill.MESMER_ARMOR),5,10,15)
	,NPC_PRIMAL_WATER("Naiad","The primal forces within them embody the lifegiving fresh water."
			,EnumSet.of(Skill.LIFE_MAGE,Skill.DODGEREF,Skill.BLOODTHIRSTY,Skill.SALVAGE),0,20,10)
	,NPC_PRIMAL_SEA("Haliae","The primal forces within them embody the wrathful seas."
			,EnumSet.of(Skill.LIFE_MAGE,Skill.SPUNCH,Skill.COUNTER,Skill.PRESS_ADV),15,5,10)
	;
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
		return TrawelColor.ITEM_VALUE+ name +TrawelColor.PRE_WHITE + ": "+desc;
	}
	
	@Override
	public String getBriefText() {
		return TrawelColor.ITEM_VALUE+ name +TrawelColor.PRE_WHITE + ": "+desc;
	}
	
	@Override
	public String getStanceText() {
		return null;
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
