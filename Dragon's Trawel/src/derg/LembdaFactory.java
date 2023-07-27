package derg;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LembdaFactory {

	public List<String> phrases = new ArrayList<String>();
	
	public LembdaFactory() {
		//
	}
	
	public LembdaFactory addVariant(char code,String phrase) {
		phrases.add(code + phrase);
		return this;
	}
	
	public StringLembda pop() {
		StringLembda sl = new StringLembda((String[]) phrases.toArray());
		phrases.clear();
		return sl;
	}
	
	public static void init() {
		LembdaFactory f = new LembdaFactory();
		f.addVariant('a', "strong").pop();
	}
}
