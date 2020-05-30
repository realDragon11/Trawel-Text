package rtrawel.unit;

public class Buff {

	public double timeLeft;
	public BuffType type;
	public double mag;
	public boolean isDebuff;
	public boolean passive;
	
	
	public enum BuffType{
		STR_MULT, STR_MOD,
		KNO_MULT, KNO_MOD,
		SPD_MULT,SPD_MOD,
		AGI_MULT,AGI_MOD,
		DEX_MULT,DEX_MOD,
		RES_MULT,RES_MOD,
		LOOT_CHANCE;
	}
}
