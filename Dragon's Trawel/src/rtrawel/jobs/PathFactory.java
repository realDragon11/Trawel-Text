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
					int cum = 0;
					for (int i = formerPoints+1; i <= points;i++) {
						if (i%2 == 0) {
							cum++;
						}
					}
				player.addWeaponPoints(points-formerPoints+cum);
				}
				
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
					if (jobActive && points > 10) {
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
				// 
				
			}});
		
			data.put("piety",new Path() {

				@Override
				public String name() {
					return "piety";//offense with weapons
				}

				@Override
				public String jobName() {
					return "priest";
				}

				@Override
				public void apply(RPlayer player, int points, boolean jobActive) {
					if (points > 0) {
						Buff b = new Buff();
						b.isDebuff = false;
						b.mag = points/6;
						b.passive = true;
						b.timeLeft = 1;
						b.type = Buff.BuffType.KNO_MOD;
						if (jobActive) {
						player.addAbility(ActionFactory.getActionByName("mend"));}
					}
					if (jobActive && points > 4) {
						player.addAbility(ActionFactory.getActionByName("battle heal"));
					}
					
				}

				@Override
				public void applyOnce(RPlayer player, int points, int formerPoints) { 
					if (points-formerPoints > 0) {
						int cum = 0;
						for (int i = formerPoints+1; i <= points;i++) {
							if (i%4 == 0) {
								cum++;
							}
						}
					if (cum > 0) {
					player.addWeaponPoints(cum);}
					}
					
				}

				});
			data.put("damnation",new Path() {

				@Override
				public String name() {
					return "damnation";//protection
				}

				@Override
				public String jobName() {
					return "priest";
				}

				@Override
				public void apply(RPlayer player, int points, boolean jobActive) {
					
				}

				@Override
				public void applyOnce(RPlayer player, int points, int formerPoints) {
					if (points-formerPoints > 0) {
						int cum = 0;
						for (int i = formerPoints+1; i <= points;i++) {
							if (i%5 == 0) {
								cum++;
							}
						}
					if (cum > 0) {
					player.addWeaponPoints(cum);}
					}
				}

				});
			data.put("faith",new Path() {

				@Override
				public String name() {
					return "faith";//miscs
				}

				@Override
				public String jobName() {
					return "priest";
				}

				@Override
				public void apply(RPlayer player, int points, boolean jobActive) {
					
				}

				@Override
				public void applyOnce(RPlayer player, int points, int formerPoints) {
					if (points-formerPoints > 0) {
						int cum = 0;
						for (int i = formerPoints+1; i <= points;i++) {
							if (i%4 == 0) {
								cum++;
							}
						}
					if (cum > 0) {
					player.addWeaponPoints(cum);}
					}
				}

				});
			
			data.put("judgement",new Path() {

				@Override
				public String name() {
					return "judgement";//offense
				}

				@Override
				public String jobName() {
					return "cleric";
				}

				@Override
				public void apply(RPlayer player, int points, boolean jobActive) {
					if (points > 0) {
						Buff b = new Buff();
						b.isDebuff = false;
						b.mag = points/10;
						b.passive = true;
						b.timeLeft = 1;
						b.type = Buff.BuffType.STR_MOD;
						if (jobActive) {
						player.addAbility(ActionFactory.getActionByName("smite"));}
					}
					
				}

				@Override
				public void applyOnce(RPlayer player, int points, int formerPoints) { 
					if (points-formerPoints > 0) {
					player.addWeaponPoints(points-formerPoints);}
					
				}

				});
			data.put("absolution",new Path() {

				@Override
				public String name() {
					return "absolution";//protection + healing
				}

				@Override
				public String jobName() {
					return "cleric";
				}

				@Override
				public void apply(RPlayer player, int points, boolean jobActive) {
					if (jobActive && points > 0) {
						player.addAbility(ActionFactory.getActionByName("battle heal"));
					}
				}

				@Override
				public void applyOnce(RPlayer player, int points, int formerPoints) {
					
				}

				});
			data.put("deliverance",new Path() {

				@Override
				public String name() {
					return "deliverance";//miscs
				}

				@Override
				public String jobName() {
					return "cleric";
				}

				@Override
				public void apply(RPlayer player, int points, boolean jobActive) {
					
				}

				@Override
				public void applyOnce(RPlayer player, int points, int formerPoints) {
					
				}

				});
		
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
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = points/5;
				b.passive = true;
				b.timeLeft = 1;
				b.type = Buff.BuffType.STR_MOD;
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
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = points/5;
				b.passive = true;
				b.timeLeft = 1;
				b.type = Buff.BuffType.STR_MOD;
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
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = points/5;
				b.passive = true;
				b.timeLeft = 1;
				b.type = Buff.BuffType.STR_MOD;
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
		
		data.put("knife",new Path() {

			@Override
			public String name() {
				return "knife";
			}

			@Override
			public String jobName() {
				return "";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (!player.getWeapon().getWeaponType().equals(WeaponType.KNIFE)) {
					return;
				}
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = points/4;
				b.passive = true;
				b.timeLeft = 1;
				b.type = Buff.BuffType.AGI_MOD;
				if (points > 1) {
					player.addAbility(ActionFactory.getActionByName("backlash"));
				}
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
		
		data.put("boomerang",new Path() {

			@Override
			public String name() {
				return "boomerang";
			}

			@Override
			public String jobName() {
				return "";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (!player.getWeapon().getWeaponType().equals(WeaponType.BOOMERANG)) {
					return;
				}
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = points/5;
				b.passive = true;
				b.timeLeft = 1;
				b.type = Buff.BuffType.AGI_MOD;
				if (points > 0) {
					player.addAbility(ActionFactory.getActionByName("ranga"));
				}
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
		
		data.put("crossbow",new Path() {

			@Override
			public String name() {
				return "crossbow";
			}

			@Override
			public String jobName() {
				return "";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (!player.getWeapon().getWeaponType().equals(WeaponType.CROSSBOW)) {
					return;
				}
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = points/5;
				b.passive = true;
				b.timeLeft = 1;
				b.type = Buff.BuffType.STR_MOD;
				if (points > 39) {
					player.addAbility(ActionFactory.getActionByName("dead bolt"));
				}
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
		
		data.put("sling",new Path() {

			@Override
			public String name() {
				return "sling";
			}

			@Override
			public String jobName() {
				return "";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				if (!player.getWeapon().getWeaponType().equals(WeaponType.SLING)) {
					return;
				}
				Buff b = new Buff();
				b.isDebuff = false;
				b.mag = points/4;
				b.passive = true;
				b.timeLeft = 1;
				b.type = Buff.BuffType.AGI_MOD;
				if (points > 9) {
					player.addAbility(ActionFactory.getActionByName("stunning sling"));
				}
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
	}
}
