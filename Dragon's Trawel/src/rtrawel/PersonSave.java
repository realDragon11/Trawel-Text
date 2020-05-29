package rtrawel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rtrawel.items.Armor;
import rtrawel.items.Item;
import rtrawel.items.Weapon;
import rtrawel.jobs.Progression;
import rtrawel.unit.Action;
import rtrawel.unit.RPlayer;

public class PersonSave implements Serializable {
	public String name;
	
	public Weapon weap, shield;
	
	public Armor head, torso, arms, pants, feet, assec1, assec2;
	public Progression progression;
	public String currentJob;
	public List<Item> inventory;
	
	public PersonSave(RPlayer r) {
		this.weap = r.weap;
		this.name = r.getName();
		this.shield = r.shield;
		this.head = r.head;
		this.torso = r.torso;
		this.arms = r.arms;
		this.feet = r.feet;
		this.assec1 = r.assec1;
		this.assec2 = r.assec2;
		this.currentJob = r.currentJob;
		this.progression = r.progression;
		this.inventory = r.inventory;
	}
}
