package trawel.towns.features.services;
import java.util.ArrayList;

import trawel.helper.methods.extra;

public class Book implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	public String name, author;
	public ArrayList<String> text = new ArrayList<String>();
	public void display() {
		int i = 0;
		for (String str: text) {
			extra.println(str);
			if (i%10 == 0 && i > 1) {
				extra.println("1 continue");
				extra.inInt(1);
			}
		}
	}
}
