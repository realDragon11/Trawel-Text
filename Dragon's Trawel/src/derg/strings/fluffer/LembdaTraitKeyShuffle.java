package derg.strings.fluffer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trawel.core.Rand;
import trawel.threads.ThreadData;


public class LembdaTraitKeyShuffle implements CanLembdaUpdate{

	private List<StringLembda> tree;
	/**
	 * amount of lembdas
	 */
	private int treeSize, 
	/*
	 * used as how many 'leaf' nodes exist. However, they do not have to be actual leaves,
	 * we just care about the bottom ones on the tree
	 */
	depthChunk,
	/**
	 * minimum allowed randomness when using safeNext
	 */
	minRand;
	
	public LembdaTraitKeyShuffle() {
		tree = new ArrayList<StringLembda>();
		minRand = 3;
	}
	
	/**
	 * you need to call shuffle at least once afterwards to get this to work properly.
	 * @param sl
	 */
	public LembdaTraitKeyShuffle assign(StringLembda sl) {
		tree.add(sl);
		//sl.users.put(this,-1);
		return this;
	}
	
	public void shuffle() {
		treeSize = tree.size();
		depthChunk = (treeSize+1)/2;
		Collections.shuffle(tree);
		for (int i = tree.size()-1; i >=0 ;i--) {
			tree.get(i).users.put(this, i);
		}
	}
	
	/**
	 * @return
	 */
	private List<StringLembda> leafLayer(int forword){
		List<StringLembda> results = new ArrayList<StringLembda>();
		int num = 0;
		for (int i = tree.size()-1; (num < minRand || i < depthChunk) && i >=0;i--) {//DOLATER: decide if num needs to be 2 or 3
			if (tree.get(i).variants.get(forword) != null) {
				results.add(tree.get(i));
				num++;
			}
		}
		return results;
	}
	
	private int parent(int n) {
		return (n-1)/2;
	}
	
	private void trickle(int n) {
		int cur = parent(n);
		int was = n;
		StringLembda sl;
		while (was != 0) {
			sl = tree.get(cur);
			tree.set(cur,tree.get(was));
			tree.set(was,sl);
			sl.users.put(this,was);//now the lemba knows where it is to further update
			was = cur;
			cur = parent(cur);
		}
		tree.get(0).users.put(this,0);//root needs an update- ie the thing we just trickled
		System.err.println(this.tree.toString());
	}

	/**
	 * use if not all lembdas have the forword you want in them
	 */
	public String safeNext(char forword) {
		if (!ThreadData.isMainThread()) {
			throw new RuntimeException("Tried to trait shuffle outside of main thread.");
		}
		List<StringLembda> list = leafLayer(forword);
		if (list.isEmpty()) {
			throw new RuntimeException("could not find Lembda in list " + this.toString() + " for forward " + forword);
		}
		StringLembda sl = Rand.randList(list);
		sl.updateAll();
		return sl.variants.get((int)forword);
		//search the tree from bottom to top, trying to get 3 choices to choose from in each layer
		//if have to go to next layer, just choose from the ones you have
		//note that this will probably perform poorly if the tree is too small
		//maybe just get first 3
		//after you find something, call the update function on it
		//this will force yourself and other trees using it to put it to the top of the tree, and trickle down
	}
	
	/**
	 * better to use if entire shuffler has all lembdas as valid with the keyword you want
	 */
	public String next(char forword) {
		assert ThreadData.isMainThread();
		StringLembda sl = tree.get(treeSize-Rand.getRand().nextInt(depthChunk));
		sl.updateAll();
		return sl.variants.get((int)forword);
	}

	@Override
	public void update(StringLembda sl) {
		int spot = sl.users.get(this);
		if (!sl.equals(tree.get(spot))){
			System.out.println(spot + " " + tree.toString());
			assert sl.equals(tree.get(spot));
		}
		trickle(spot);
	}
	
}
