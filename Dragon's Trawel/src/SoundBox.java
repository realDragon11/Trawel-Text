
public class SoundBox {
	
	public enum Voice{
		FEMALE_BASIC, NONE;
	}
	public enum Type{
		SWING, GRUNT;
	}
	public static String getSound(Voice v, Type t) {
		switch (v) {
		case FEMALE_BASIC:
			switch (t) {
			case GRUNT:
				return "sound_fgrunt"+ extra.randRange(1, 15);
			case SWING:
				return "sound_hiya"+ extra.randRange(1, 5);
			default:
				return null;
			
			}

		default:
			return null;
		
		}
	}
	
}
