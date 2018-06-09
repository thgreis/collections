//TODO implementar serializable e writeObject()/readObject()

package collection.set;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.WeakHashMap;

public class WeakHashSet<E> extends AbstractSet<E> implements Serializable, Cloneable {
	//static state
	private static final long serialVersionUID = -1L;
	private static final Object PRESENT = new Object();

	//mutable state
	private transient WeakHashMap<E, Object> mapper;

	//constructors
	public WeakHashSet() {
		mapper = new WeakHashMap<>();
	}

	public WeakHashSet(int initialCapacity) {
		mapper = new WeakHashMap<>(initialCapacity);
	}

	public WeakHashSet(int initialCapacity, float loadFactor) {
		mapper = new WeakHashMap<>(initialCapacity, loadFactor);
	}

	public WeakHashSet(Collection<? extends E> collection) {
		mapper = new WeakHashMap<>(Math.max((int)(Objects.requireNonNull(collection, "Invalid null collection.").size() / .75f) + 1, 16));
		addAll(collection);
	}

	//set behaviour
	@Override
	public int size() {
		return mapper.size();
	}

	@Override
	public boolean isEmpty() {
		return mapper.isEmpty();
	}

	@Override
	public boolean contains(Object object) {
		return mapper.containsKey(object);
	}

	@Override
	public Iterator<E> iterator() {
		return mapper.keySet().iterator();
	}

	@Override
	public boolean add(E element) {
		return mapper.put(element, PRESENT) == null;
	}

	@Override
	public boolean remove(Object object) {
		return mapper.remove(object) == PRESENT;
	}

	@Override
	public void clear() {
		mapper.clear();
	}

	@Override
	public Spliterator<E> spliterator() {
		return mapper.keySet().spliterator();
	}

	//cloneable behaviour
	@Override
	public Object clone() {
		try {
			WeakHashSet<E> clone = (WeakHashSet<E>)super.clone();
			clone.mapper = new WeakHashMap<>(mapper);
			return clone;
		} catch (CloneNotSupportedException exception) {
			throw new InternalError(exception);
		}
	}

	//object behaviour
	//inherited
}
