import java.util.ArrayList;

public class StyleFactory {

	public static ArrayList<Style> StyleList = new ArrayList<Style>();
	public StyleFactory() {
		Style s = new Style();
		s.name = "";//normal
		s.damage = 1;
		s.hit = 1;
		s.speed = 1;
		s.rarity = 3;
		StyleList.add(s);
		
		s = new Style();
		s.name = "precise ";
		s.damage = 1;
		s.hit = 1.3;
		s.speed = 1.3;
		s.rarity = 1;
		StyleList.add(s);
		
		s = new Style();
		s.name = "wild ";
		s.damage = 1;
		s.hit = .7;
		s.speed = .7;
		s.rarity = 1;
		StyleList.add(s);
		
		s = new Style();
		s.name = "heavy ";
		s.damage = 1.2;
		s.hit = 1;
		s.speed = 1.2;
		s.rarity = 1;
		StyleList.add(s);
		
		s = new Style();
		s.name = "quick ";
		s.damage = .8;
		s.hit = 1;
		s.speed = .8;
		s.rarity = 1;
		StyleList.add(s);
		
		
		
	}
	
	
	public static Style randStyle() {
		ArrayList<Style> copyList = new ArrayList<Style>();
		ArrayList<Style> copyList2 = new ArrayList<Style>();
		for (Style mat: StyleList){
			copyList.add(mat);
		}
		double totalRarity = 0;
		Style mat;
		do {
			int i = (int) Math.floor((Math.random()*copyList.size()));
			mat = copyList.get(i);
			copyList2.add(mat);
			totalRarity += mat.rarity;
			copyList.remove(i);
		}while(!copyList.isEmpty());
		totalRarity*=Math.random();
		do {
			mat = copyList2.get(0);
			if (totalRarity > mat.rarity) {
				totalRarity-=mat.rarity;
				copyList2.remove(0);
			}else {
				totalRarity = 0;
			} 
				
				
		}while(totalRarity > 0);
		return mat;
	}
}
