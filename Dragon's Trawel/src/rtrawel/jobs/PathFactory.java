package rtrawel.jobs;

import java.util.HashMap;

import rtrawel.items.Weapon.WeaponType;
import rtrawel.unit.ActionFactory;
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
				return "honor";
			}

			@Override
			public String jobName() {
				return "warrior";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) { 
				if (points > 4 && formerPoints <= 4 ) {
				player.addWeaponPoints(4);}
				
			}

			});
		data.put("courage",new Path() {

			@Override
			public String name() {
				return "courage";
			}

			@Override
			public String jobName() {
				return "warrior";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {
				// TODO Auto-generated method stub
				
			}

			});
		data.put("valor",new Path() {

			@Override
			public String name() {
				return "valor";
			}

			@Override
			public String jobName() {
				return "warrior";
			}

			@Override
			public void apply(RPlayer player, int points, boolean jobActive) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {
				// TODO Auto-generated method stub
				
			}

			});
		
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
			}

			@Override
			public void applyOnce(RPlayer player, int points, int formerPoints) {}

			});
	}
}
