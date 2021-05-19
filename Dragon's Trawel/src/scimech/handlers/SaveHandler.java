package scimech.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import scimech.mech.Mech;
import trawel.extra;

public class SaveHandler implements java.io.Serializable{

	public static SaveHandler save;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<String> mechs = new ArrayList<String>();

	/*
	switch (getName.toLowerCase()) {
	//fixtures just create new fixtures
	//systems just create new systems
	//mounts create mounts from traits and then call deserialize on all their fixtures
	//mechs do the same but with mounts and systems
	//pilots TODO
	}*/
	
	public static void clean() {
		save = new SaveHandler();
	}
	
	public static void imprintMechs(List<Mech> lMechs) {
		for (Mech m: lMechs) {
			save.mechs.add(m.saveString());
		}
	}
	
	public static List<Mech> exportMechs() {
		List<Mech> exporting = new ArrayList<Mech>();
		try {
		for (String m: save.mechs) {
			exporting.add((Mech) SaveHandler.deserialize(m));
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return exporting;
	}
	
	public static void save() {
		extra.println("saving...");
		   FileOutputStream fos;
			try {
				fos = new FileOutputStream("scimech.save");
				 ObjectOutputStream oos = new ObjectOutputStream(fos);
				 oos.writeObject(save);
			     oos.close();
			     fos.close();
			     extra.println("saved!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public static void load() {
		extra.println("loading...");
		FileInputStream fos;
		try {
			fos = new FileInputStream("scimechs.save");
			 ObjectInputStream oos = new ObjectInputStream(fos);
			 save = (SaveHandler) oos.readObject();
			 oos.close();
			 fos.close();
			 extra.println("loaded!");
			 return;
		} catch (Exception e) {
			e.printStackTrace();
			extra.println("Invalid load. Either no save file was found or it was outdated.");
			return;
		}
	}
	
	public static Savable deserialize(String s) throws Exception {
		String getName = s.substring(0,s.indexOf('&'));
		//System.err.println(s);
		return (Savable)Class.forName(getName).getMethod("deserialize", String.class).invoke(null,s);
	}
}
