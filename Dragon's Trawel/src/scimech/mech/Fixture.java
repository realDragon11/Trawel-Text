package scimech.mech;

import trawel.MenuSelect;
import trawel.extra;

public abstract class Fixture implements TurnSubscriber{
	
	//TODO: don't save this, save a text representation of the name, power state, and damage state

	public boolean powered = true;
	public boolean overclocked = false;
	protected int damage = 0;//max 100
	public void heatCheck(int heat) {
		if (heat > heatCap()) {
			int over = (heat-heatCap())-1;
			takeDamage((int)Math.pow(2,over));
		}
	}
	
	public abstract int heatCap();
	
	public void takeDamage(int toTake) {
		damage = extra.clamp(damage+toTake, 0, 100);
	}
	
	
	public class MenuFixture extends MenuSelect {//can be extended further

		public Fixture fix;
		@Override
		public String title() {
			String damString = "";
			if (damage > 30) {
				if (damage > 60) {
					if (damage > 90) {
						damString = "destroyed";
					}else {
						damString = "damaged";
					}
				}else {
					damString = "scratched";
				}
			}
			return fix.getName() + ": " + (fix.powered ? "on" : "off") + (fix.overclocked ? "OVERCLOCKED" : "") + " " + damString;
		}

		@Override
		public boolean go() {
			// TODO Auto-generated method stub
			return false;
		}}


	public abstract String getName();
	
	public abstract int getEnergyDraw();
}
