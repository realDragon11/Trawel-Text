package scimech.people;

public enum Trait {

	HARDENED, GUN_NUT,LASER_SPEC,DUELIST,LOBBER;
	@Override
	public String toString() {
		return this.name();
	}
}
