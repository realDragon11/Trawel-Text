package scimech.mech;

public abstract class Fixture {
	
	//TODO: don't save this, save a text representation of the name, power state, and damage state

	public boolean powered = true;
	public boolean overclocked = false;
	protected int damage = 0;//max 100
}
