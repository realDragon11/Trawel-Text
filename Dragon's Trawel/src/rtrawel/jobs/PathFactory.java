package rtrawel.jobs;

import java.util.HashMap;

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
				
			}});
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
				
			}});
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
				
			}});
	}
}
