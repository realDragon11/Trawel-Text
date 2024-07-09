package trawel.personal.item.body;

import trawel.core.Rand;

public class SoundBox {
	
	public enum Voice{
		FEMALE_BASIC, BEAR, WOLF, F_GOLEM, ENT, NONE, BAT, HARPY, MONSTER1, HORSE;
	}
	public enum Type{
		SWING, GRUNT;
	}
	public static String getSound(Voice v, Type t) {
		switch (v) {
		case FEMALE_BASIC:
			switch (t) {
			case GRUNT:
				return "sound_fgrunt"+ Rand.randRange(1, 15);
			case SWING:
				return "sound_hiya"+ Rand.randRange(1, 5);
			default:
				return null;
			
			}
		case BEAR:
			switch (t) {
			case GRUNT:
			case SWING:
				return "sound_bear"+ Rand.randRange(1, 5);
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
				return "sound_splat"+ Rand.randRange(1, 4);
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
				return "sound_bat"+ Rand.randRange(1, 2);
			default:
				return null;
			}
		case HARPY:
			switch (t) {
			case GRUNT:
			case SWING:
				return "sound_uncanny_scream"+ Rand.randRange(1, 5);
			default:
				return null;
			}
		case MONSTER1:
			switch (t) {
			case GRUNT:
				return "sound_monster_hiss"+ Rand.randRange(1, 2);
			case SWING:
				return "sound_monster_hiss_attack";
			default:
				return null;
			}
		case HORSE:
			switch (t) {
			case GRUNT:
			case SWING:
				return "sound_horse"+ Rand.randRange(1, 3);
			default:
				return null;
			}
		default:
			return null;
		
		}
	}
	
}
