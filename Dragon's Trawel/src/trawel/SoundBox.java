package trawel;

public class SoundBox {
	
	public enum Voice{
		FEMALE_BASIC, BEAR, WOLF, F_GOLEM, ENT, NONE, BAT;
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
		case BEAR:
			switch (t) {
			case GRUNT:
			case SWING:
				return "sound_bear"+ extra.randRange(1, 5);
			default:
				return null;
			
			}
		case WOLF:
			switch (t) {
			case GRUNT:
				return "sound_wolfhit";
			case SWING:
				return "sound_wolfbite";
			default:
				return null;
			
			}
		case F_GOLEM:
			switch (t) {
			case GRUNT:
			case SWING:
				return "sound_splat"+ extra.randRange(1, 4);
			default:
				return null;
			
			}
		case ENT:
			switch (t) {
			case GRUNT:
			case SWING:
				return "sound_enthit";
			default:
				return null;
			
			}
		case BAT:
			switch (t) {
			case GRUNT:
			case SWING:
				return "sound_bat"+ extra.randRange(1, 2);
			default:
				return null;
			
			}

		default:
			return null;
		
		}
	}
	
}
