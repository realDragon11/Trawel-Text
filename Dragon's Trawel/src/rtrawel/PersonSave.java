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
	
	public String weap, shield;
	
	public String head, torso, arms, pants, feet, assec1, assec2;
	public Progression progression;
	public String currentJob;
	public List<String> inventory;
	
	public PersonSave(RPlayer r) {
		this.weap = r.weap.getName();
		this.name = r.getName();
		this.shield = r.shield.getName();
		this.head = r.head.getName();
		this.torso = r.torso.getName();
		this.arms = r.arms.getName();
		this.feet = r.feet.getName();
		this.assec1 = r.assec1.getName();
		this.assec2 = r.assec2.getName();
		this.currentJob = r.currentJob;
		this.progression = r.progression;
		for (Item i: r.inventory) {
			this.inventory.add(i.getName());
		}
	}
}
