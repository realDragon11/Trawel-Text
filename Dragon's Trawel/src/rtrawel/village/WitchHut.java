package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

import rtrawel.EventFlag;
import rtrawel.battle.Party;
import trawel.extra;

public class WitchHut implements Content {
	
	public String evF;
	
	public WitchHut(String place) {
		evF = place;
	}

	@Override
	public boolean go() {
		EventFlag.eventFlag.setEF(this.evF,1);
		List<String> alist = new ArrayList<String>();
		if (EventFlag.eventFlag.getEF("hemo_recipes") == 1) {
			alist.add("hemo");
		}
		while (true) {
			extra.println("1 back");
			for (int i = 0; i < alist.size();i++) {
				extra.println((i+2) + " " + alist.get(i));
			}
			int in = extra.inInt(alist.size()+1);
			if (in == 1) {
				return false;
			}
			in-=2;
			List<Recipe> rs =RecipeFactory.getHolder(alist.get(in)).list;
			while (true) {
				extra.println("1 back");
				for (int i = 0; i < rs.size();i++) {
					extra.println((i+2) + " " + rs.get(i).toString());
				}
				int in2 = extra.inInt(alist.size()+1);
				if (in2 == 1) {
					break;
				}
				in2-=2;
				make(rs.get(in2));
				
			}
		}
	}

	private void make(Recipe recipe) {
		for (int i = 0; i < recipe.inputs.size();i++) {
			if (Party.party.items.get(recipe.inputs.get(i)) < recipe.inputCount.get(i)){
				extra.println("Not enough " + recipe.inputs.get(i));
				return;
			}
		}
		for (int i = 0; i < recipe.inputs.size();i++) {
			Party.party.addItem(recipe.inputs.get(i),-recipe.inputCount.get(i));
		}
		for (String o: recipe.outputs)
		Party.party.addItem(o,1);
	}

	@Override
	public String name() {
		return "witch hut";
	}

}
