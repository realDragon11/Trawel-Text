package derg.strings.fluffer;

public class LembdaFactory {

	//public List<String> phrases = new ArrayList<String>();
	
	private StringLembda working;
	
	public LembdaFactory() {
		working = new StringLembda();
	}
	
	public LembdaFactory addVariant(char code,String phrase) {
		working.variants.put((int) code, phrase);
		return this;
	}
	
	public StringLembda pop() {
		StringLembda sl = working;
		working = new StringLembda();
		return sl;
	}
	
	public static void init() {
		LembdaFactory f = new LembdaFactory();
		f.addVariant('a', "strong").pop();
	}
	
	
	public static void test() {
		LembdaFactory f = new LembdaFactory();
		LembdaTraitKeyShuffle[] shuf = new LembdaTraitKeyShuffle[] {
				new LembdaTraitKeyShuffle(),new LembdaTraitKeyShuffle(),new LembdaTraitKeyShuffle(),new LembdaTraitKeyShuffle()
		};
		StringLembda l;
		l = f.addVariant('a', "1 a-dancing").addVariant('b', "1 b-dancing").addVariant('c', "1 c-dancing").pop();
		shuf[0].assign(l);
		shuf[1].assign(l);
		shuf[3].assign(l);
		
		l = f.addVariant('a', "2 a-grooving").addVariant('b', "2 b-grooving").addVariant('c', "2 c-grooving").pop();
		shuf[0].assign(l);
		shuf[1].assign(l);
		shuf[2].assign(l);
		
		l = f.addVariant('a', "3 a-moving").addVariant('b', "3 b-moving").addVariant('c', "3 c-moving").pop();
		shuf[0].assign(l);
		shuf[1].assign(l);
		shuf[2].assign(l);
		
		l = f.addVariant('a', "3 a-breakdown").addVariant('b', "3 b-breakdown").pop();
		shuf[0].assign(l);
		shuf[1].assign(l);
		shuf[2].assign(l);
		shuf[3].assign(l);
		
		l = f.addVariant('a', "4 a-buildup").addVariant('b', "3 b-buildup").pop();
		shuf[0].assign(l);
		shuf[1].assign(l);
		shuf[2].assign(l);
		shuf[3].assign(l);
		
		l = f.addVariant('a', "5 a-empty").addVariant('b', "3 b-empty").pop();
		shuf[0].assign(l);
		shuf[1].assign(l);
		shuf[2].assign(l);
		shuf[3].assign(l);
		
		
		for (int i = 0; i < 4;i++) {
			shuf[i].shuffle();
		}
		
		for (int i = 0; i < 30;i++) {
			System.out.println(" test " +i);
			System.out.println(shuf[i%4].next('a'));
			System.out.println(shuf[(i+1)%4].next('b'));
		}
		System.out.println(shuf[0].next('c') + shuf[0].next('c') + shuf[0].next('c'));
		//we only use c for three things, so should print all three
	}
}
