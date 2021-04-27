package scimech.combat;

public interface TakeDamage {

	public void take(DamageTypes type, DamageMods mods, int value,Target damaged);
	
	public void suffer(DamageEffect de,Target damaged);
}
