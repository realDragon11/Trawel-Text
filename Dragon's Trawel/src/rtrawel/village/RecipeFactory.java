package rtrawel.village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rtrawel.unit.Action;

public class RecipeFactory {


	public static RecipeHolder getHolder(String string) {
		return data2.get(string);
	}
	//public static Recipe getRecipe(String string) {
	//	return data.get(string);
	//}
	
	//private static HashMap<String,Recipe> data = new HashMap<String, Recipe>();
	private static HashMap<String,RecipeHolder> data2 = new HashMap<String, RecipeHolder>();
	public static void init() {
		Recipe r = new Recipe();
		r.addInput("much o' mushroom", 2);
		r.addInput("pole",1);
		r.outputs.add("mushroom masher");
		RecipeHolder h = new RecipeHolder();
		h.list.add(r);
		r = new Recipe();
		r.addInput("pot lid", 1);
		r.addInput("leather pelt",1);
		r.outputs.add("leather shield");
		h.list.add(r);
		r = new Recipe();
		r.addInput("iron chunk", 2);
		r.addInput("copper sword",1);
		r.outputs.add("iron sword");
		h.list.add(r);
		data2.put("hemo",h);
	}
}
