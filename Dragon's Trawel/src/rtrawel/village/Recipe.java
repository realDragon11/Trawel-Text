package rtrawel.village;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

	public List<String> inputs = new ArrayList<String>();
	public List<Integer> inputCount = new ArrayList<Integer>();
	public List<String> outputs = new ArrayList<String>();
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < inputs.size();i++) {
			str+=inputs.get(i) + " (" + inputCount.get(i) + "), ";
		}
		str+= "->";
		for (String o: outputs) {
			str+=o + ", ";
		}
		return str;
	}
	public void addInput(String name, int amount) {
		inputs.add(name);
		inputCount.add(amount);
	}
}
