package derg;


import java.util.List;

import trawel.extra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class LembdaTraitKeyShuffle implements CanLembdaUpdate{

	private List<StringLembda> tree;
	//private int treeSize, 
	//depthChunk;//used as how many 'leaf' nodes exist. However, they do not have to be actual leaves
	//we just care about the bottom ones on the tree
	
	public LembdaTraitKeyShuffle() {
		tree = new ArrayList<StringLembda>();
		//treeSize = tree.size();
		//depthChunk = -1;
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
		Collections.shuffle(tree);
		for (int i = tree.size()-1; i >=0 ;i--) {
			tree.get(i).users.put(this, i);
		}
	}
	
	/**
	 * @return
	 */
	public List<StringLembda> leafLayer(int forword){
		List<StringLembda> results = new ArrayList<StringLembda>();
		int num = 0;
		for (int i = tree.size()-1; num < 3 && i >=0;i--) {//DOLATER: decide if num needs to be 2 or 3
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

	public String next(char forword) {
		List<StringLembda> list = leafLayer(forword);
		if (list.isEmpty()) {
			throw new RuntimeException("could not find Lembda in list " + this.toString() + " for forward " + forword);
		}
		StringLembda sl = extra.randList(list);
		sl.updateAll();
		return sl.variants.get((int)forword);
		//FIXME: search the tree from bottom to top, trying to get 3 choices to choose from in each layer
		//if have to go to next layer, just choose from the ones you have
		//note that this will probably perform poorly if the tree is too small
		//maybe just get first 3
		//after you find something, call the update function on it
		//this will force yourself and other trees using it to put it to the top of the tree, and trickle down
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
