
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
				return extra.choose("");
			case SWING:
				return extra.choose("");
			default:
				return null;
			
			}

		default:
			return null;
		
		}
	}
	
}
