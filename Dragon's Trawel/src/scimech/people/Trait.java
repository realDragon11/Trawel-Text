package scimech.people;

public enum Trait {

	//normal traits
	HARDENED, GUN_NUT,LASER_SPEC,DUELIST,LOBBER,THICK_SKULL,GREASE_MONKEY,PINPOINT,
	//capstone traits
	ACCURATE,MOBILE,TOUGH, EVASIVE;
	@Override
	public String toString() {
		return this.name();
	}
}
