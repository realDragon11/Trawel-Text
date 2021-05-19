package rtrawel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rtrawel.items.Item;
import rtrawel.jobs.Progression;
import rtrawel.unit.RPlayer;

public class PersonSave implements Serializable {
	public String name;
	
	public String weap, shield;
	
	public String head, torso, arms, pants, feet, assec1, assec2;
	public Progression progression;
	public String currentJob;
	public List<String> inventory = new ArrayList<String>();
	public int hp, mana;
	
	public PersonSave(RPlayer r) {
		this.weap = r.weap.getName();
		this.name = r.getName();
		this.hp = r.getHp();
		this.mana = r.getMana();
		if (r.shield != null) {
		this.shield = r.shield.getName();}
		if (r.head != null) {
		this.head = r.head.getName();}
		if (r.torso != null) {
		this.torso = r.torso.getName();}
		if (r.arms != null) {
		this.arms = r.arms.getName();}
		if (r.feet != null) {
		this.feet = r.feet.getName();}
		if (r.assec1 != null) {
		this.assec1 = r.assec1.getName();}
		if (r.assec2 != null) {
		this.assec2 = r.assec2.getName();}
		this.currentJob = r.currentJob;
		this.progression = r.progression;
		for (Item i: r.inventory) {
			this.inventory.add(i.getName());
		}
	}
}
