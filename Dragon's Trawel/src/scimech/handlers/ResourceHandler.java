package scimech.handlers;

public class ResourceHandler {
	private int biomass = 0, metals = 0, fuel = 0, energy = 0, energyLast = 0;
	private int crystals = 0, darkmatter = 0;
	
	public int getBiomass() {
		return biomass;
	}
	public void addBiomass(int biomass) {
		this.biomass += biomass;
	}
	public int getMetals() {
		return metals;
	}
	public void addMetals(int metals) {
		this.metals += metals;
	}
	public int getFuel() {
		return fuel;
	}
	public void addFuel(int fuel) {
		this.fuel += fuel;
	}
	public int getEnergy() {
		return energy+energyLast;
	}
	public void plusEnergy(int energy) {
		this.energy += energy;
	}
	
	public void minusEnergy(int en) {
		if (en > energyLast) {
			en -= energyLast;
			energyLast = 0;
			energy-=en;
			return;
		}
		energyLast-=en;
	}
	
	public int flipEnergy() {
		int ret = energyLast;
		energyLast = energy;
		energy = 0;
		return ret;
	}
	
	public int getCrystals() {
		return crystals;
	}
	public void addCrystals(int crystals) {
		this.crystals += crystals;
	}
	public int getDarkmatter() {
		return darkmatter;
	}
	public void addDarkmatter(int darkmatter) {
		this.darkmatter += darkmatter;
	}
	
	
}
