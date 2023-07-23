package trawel.towns.services;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import trawel.extra;

public class BookFactory {

	private static ArrayList<Book> books = new ArrayList<Book>();
	public BookFactory() {
		Book bo;
		bo = new Book();
		bo.name = "The Book Book";
		bo.author = "Mr. Book";
		bo.text.add("This is a book.");
		books.add(bo);
	
		
		final File folder = new File("./Books/");
		if (folder.exists()) {
		for (File f: listFilesForFolder(folder)) {
			loadBook(f);
		}}else {
			System.err.println("books not detected");
		}
	}
	
	public static Book randBook() {
		return extra.randList(books);
	}
	
	/**
	 * https://stackoverflow.com/a/1846349/9320090
	 */
	private ArrayList<File> listFilesForFolder(final File folder) {
		ArrayList<File> list = new ArrayList<File>(); 
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            list.addAll(listFilesForFolder(fileEntry));
	        } else {
	            list.add(fileEntry);
	        }
	    }
	    return list;
	}
	
	private void loadBook(File f) {
		Book b = new Book();
		try {
			Scanner s = new Scanner(f);
			s.nextLine();//header
			b.name = s.nextLine();
			b.author = s.nextLine();
			while (s.hasNextLine()) {
				b.text.add(s.nextLine());
			}
			s.close();
		} catch (FileNotFoundException e) {
			return;
		}
		books.add(b);
	}

	
	
}
