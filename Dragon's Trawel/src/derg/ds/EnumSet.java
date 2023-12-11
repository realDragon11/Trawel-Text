package derg.ds;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class EnumSet<E extends Enum<E>> implements Set<E> {
	
	private Class<E> type;
	private int size = 0;
	private long[] vals;
	
	public EnumSet(Class<E> _type) {
		type = _type;
		vals = new long[(type.getEnumConstants().length/64)+1];
	}
	
	public static <T extends Enum<T>> EnumSet<T> of(T... val) {
		EnumSet<T> set = new EnumSet<T>(val[0].getDeclaringClass());
		for (T value: val) {
			set.add(value);
		}
		return set;
	}
	
	public static <T extends Enum<T>> EnumSet<T> noneOf(Class<T> val) {
		return new EnumSet<T>(val);
	}
	public static <T extends Enum<T>> EnumSet<T> copyOf(Set<T> base) {
		Iterator<T> a = base.iterator();
		T one = a.next();
		EnumSet<T> set = new EnumSet<T>(one.getDeclaringClass());
		set.add(one);
		while (a.hasNext()) {
			set.add(a.next());
		}
		return set;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		int ord = ((E)o).ordinal();
		return (vals[ord/64] & 0b01 << ord%64) != 0;
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			
			int count = 0;
			int scan = 0;

			@Override
			public boolean hasNext() {
				return EnumSet.this.size < count;
			}

			@Override
			public E next() {
				count++;
				do{
					scan++;
					if ((vals[scan/64] & 0b01 << scan%64) != 0) {
						return type.getEnumConstants()[scan];
					}
				}while(true);
			}};
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("to array large enum set");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("to array<T> large enum set");
	}

	@Override
	public boolean add(E e) {
		if (contains(e)) {
			return false;
		}
		int ord = e.ordinal();
		vals[ord/64] |= 0b01 << ord%64;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		if (!contains(o)) {
			return false;
		}
		int ord = ((E)o).ordinal();
		vals[ord/64] ^= 0b01 << ord%64;//xor
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException("containsAll large enum set");
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E val: c) {
			if (add(val)) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("retainAll large enum set");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("removeAll large enum set");
	}

	@Override
	public void clear() {
		for (int i = vals.length-1;i >=0;i--) {
			vals[i] = 0b0;
		}
	}

}
