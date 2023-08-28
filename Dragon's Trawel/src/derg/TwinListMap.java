package derg;

import java.io.Serializable;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * both values and keys must be Serializable
 * @param <K> key
 * @param <V> value
 */
public class TwinListMap<K extends Serializable,V extends Serializable> implements Map<K, V>, Serializable {

	protected List<K> keyList = new ArrayList<K>();
	protected List<V> valueList = new ArrayList<V>();
	
	@Override
	public void clear() {
		keyList.clear();
		valueList.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return keyList.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return valueList.contains(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K,V>> set = new HashSet<Map.Entry<K,V>>();
		for (int i = 0; i < keyList.size();i++) {
			set.add(new SimpleImmutableEntry<K, V>(keyList.get(i),valueList.get(i)));
		}
		return set;
	}

	@Override
	public V get(Object key) {
		try {
			return valueList.get(keyList.indexOf(key));
		}catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		return keyList.isEmpty();
	}

	/**
	 * map is not backed by this set.
	 */
	@Override
	public Set<K> keySet() {
		Set<K> set = new HashSet<K>();
		set.addAll(keyList);
		return set;
	}

	@Override
	public V put(K key, V value) {
		/* if need to do Objects, could do this, but Serializable can just be the key
		if (!(key instanceof Serializable)) {
			throw new TypeConstraintException("key " + key +" is not Serializable.");
		}*/
		
		int index = keyList.indexOf(key);
		if (index == -1) {
			keyList.add(key);
			valueList.add(value);
			return null;
		}else{
			V ret = valueList.get(index);
			valueList.set(index, value);
			return ret;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (K key: map.keySet()) {
			put(key,map.get(key));
		}
	}

	@Override
	public V remove(Object key) {
		int index = keyList.indexOf(key);
		if (index == -1) {
			return null;
		}else {
			keyList.remove(index);
			return valueList.remove(index);
		}
	}

	@Override
	public int size() {
		return keyList.size();
	}

	/**
	 * does actually back this map, so don't edit, it will make errors
	 */
	@Override
	public Collection<V> values() {
		return valueList;
	}

}
