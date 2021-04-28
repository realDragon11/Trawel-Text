package scimech.people;

public enum Trait {

	HARDENED, GUN_NUT,LASER_SPEC,DUELIST,LOBBER,THICK_SKULL,GREASE_MONKEY;
	@Override
	public String toString() {
		return this.name();
	}
}
