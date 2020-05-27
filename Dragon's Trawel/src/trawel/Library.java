package trawel;
import java.util.ArrayList;

public class Library extends Feature {

	private ArrayList<Book> books = new ArrayList<Book>();
	private Town town;
	
	public Library(String _name, Town _town) {
		start();
		name = _name;
		town = _town;
		tutorialText = "Libraries hold knowledge from all worlds.";
	}

	@Override
	public void go() {
		Networking.setArea("shop");
		Networking.sendStrong("Discord|imagesmall|library|Library|");
		int i = 1;
		for (Book b: books) {
			extra.println(i +" " + b.name + " by " + b.author);
			i++;
		}
		extra.println(i + " leave");
		int in = extra.inInt(i);
		i = 1;
		for (Book b: books) {
			if (in == i) {
				b.display();
			}
			i++;
		}
		if (in == i) {
			return;
		}
		go();
	}

	private void start() {
		while (books.size() < 4) {
			books.add(BookFactory.randBook());
		}
		
	}

	@Override
	public void passTime(double time) {
		if (books.size() > 4 && extra.chanceIn(1,5)) {
			books.remove(0);
		}
		if (books.size() > 7) {
			books.remove(0);
		}
		if (books.size() < 8 && extra.chanceIn(1,3)) {
			books.add(BookFactory.randBook());
		}

	}

}
