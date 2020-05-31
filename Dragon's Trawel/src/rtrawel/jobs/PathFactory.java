package rtrawel.jobs;

import java.util.HashMap;

import rtrawel.items.Weapon.WeaponType;
import rtrawel.unit.ActionFactory;
import rtrawel.unit.Buff;
import rtrawel.unit.RPlayer;

public class PathFactory {
	private static HashMap<String,Path> data = new HashMap<String, Path>();
	
	public static Path getPathByName(String str) {
		return data.get(str);
	}
	
	public static void init() {
		data.put("honor",new Path() {

			@Override
			public String name() {
				return "honor";//offense with weapons
			}

			@Override
			public String jobName() {
				return "warrior";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (points > 0) {
					Buff b = new Buff();
					b.isDebuff = false;
					b.mag = points/6;
					b.passive = true;
					b.timeLeft = 1;
					b.type = Buff.BuffType.STR_MOD;
				}
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) { 
				if (points-formerPoints > 0) {
				player.addWeaponPoints(points-formerPoints);}
				
			}

			});
		data.put("courage",new Path() {

			@Override
			public String name() {
				return "courage";//protection
			}

			@Override
			public String jobName() {
				return "warrior";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (points > 0) {
					Buff b = new Buff();
					b.isDebuff = false;
					b.mag = points/3;
					b.passive = true;
					b.timeLeft = 1;
					b.type = Buff.BuffType.RES_MOD;
				}
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {
				// TODO Auto-generated method stub
				
			}

			});
		data.put("valor",new Path() {

			@Override
			public String name() {
				return "valor";//miscs
			}

			@Override
			public String jobName() {
				return "warrior";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				// TODO Auto-generated method stub
				if (jobActive) {
					if (points > 10) {
						player.addAbility(ActionFactory.getActionByName("body slam"));
					}
				}
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {
				// TODO Auto-generated method stub
				
			}

			});
		
		data.put("hunting",new Path() {

			@Override
			public String name() {
				return "hunting";
			}

			@Override
			public String jobName() {
				return "ranger";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {
				if (points-formerPoints > 0) {
					player.addWeaponPoints(points-formerPoints);}
				
			}});
		data.put("gathering",new Path() {

			@Override
			public String name() {
				return "gathering";
			}

			@Override
			public String jobName() {
				return "ranger";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (points > 0) {
					Buff b = new Buff();
					b.isDebuff = false;
					b.mag = points/4;
					b.passive = true;
					b.timeLeft = 1;
					b.type = Buff.BuffType.DEX_MOD;
				}
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {
				// TODO Auto-generated method stub
				
			}});
		data.put("surveying",new Path() {

			@Override
			public String name() {
				return "surveying";
			}

			@Override
			public String jobName() {
				return "ranger";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (points > 0) {
					Buff b = new Buff();
					b.isDebuff = false;
					b.mag = 1+ (jobActive ? (points/300.0) : (points/600.0));
					b.passive = true;
					b.timeLeft = 1;
					b.type = Buff.BuffType.LOOT_CHANCE;
				}
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {
				// TODO Auto-generated method stub
				
			}});
		
		
		//TODO: WEAPON PATHS:
		data.put("sword",new Path() {

			@Override
			public String name() {
				return "sword";
			}

			@Override
			public String jobName() {
				return "";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				// TODO Auto-generated method stub
				if (!player.getWeapon().getWeaponType().equals(WeaponType.SWORD)) {
					return;
				}
				if (points > 0) {
					player.addAbility(ActionFactory.getActionByName("sword thrust"));
				}
				if (points > 2) {
					player.addAbility(ActionFactory.getActionByName("cleave"));
				}
				
				if (points > 4) {
					player.addAbility(ActionFactory.getActionByName("sword dance"));
				}
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
		data.put("hammer",new Path() {

			@Override
			public String name() {
				return "hammer";
			}

			@Override
			public String jobName() {
				return "";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (!player.getWeapon().getWeaponType().equals(WeaponType.HAMMER)) {
					return;
				}
				if (points > 20) {
					player.addAbility(ActionFactory.getActionByName("hammer stun"));
				}
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
		data.put("spear",new Path() {

			@Override
			public String name() {
				return "spear";
			}

			@Override
			public String jobName() {
				return "";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (!player.getWeapon().getWeaponType().equals(WeaponType.SPEAR)) {
					return;
				}
				if (points > 1) {
					player.addAbility(ActionFactory.getActionByName("sudden spear"));
				}
				
				if (points > 9) {
					player.addAbility(ActionFactory.getActionByName("triple thrust"));
				}
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
	}
}
